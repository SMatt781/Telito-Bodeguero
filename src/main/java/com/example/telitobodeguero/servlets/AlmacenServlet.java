package com.example.telitobodeguero.servlets;


import com.example.telitobodeguero.beans.*;
import com.example.telitobodeguero.daos.ProductoDao;
import com.example.telitobodeguero.daos.ProductoDaoAlm;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.example.telitobodeguero.daos.MovimientoDao;
//nuevos
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;

@WebServlet(name = "AlmacenServlet",value = "/AlmacenServlet")
public class AlmacenServlet extends HttpServlet {

    //NUEVO 14/10:
    private int getZonaId(HttpServletRequest req) {
        Object z = req.getSession().getAttribute("zonaIdActual");
        if (z instanceof Integer) return (Integer) z;
        try { return Integer.parseInt(String.valueOf(z)); } catch (Exception e) { return 1; }
    }


    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        //No hardcodeo
        // int zonaId = 1; //caso zona Oeste
        //reemplazo
        int zonaId = getZonaId(request);


        String accion = request.getParameter("accion");

        if(accion == null || accion.isEmpty() || accion.equals("verInventario")){




            ProductoDaoAlm productoDaoAlm = new ProductoDaoAlm();
            ArrayList<Producto> listaProd = productoDaoAlm.obtenerProductos(zonaId);
            request.setAttribute("listaProductos", listaProd);
            RequestDispatcher view = request.getRequestDispatcher("/Almacen/gestionAlmacen.jsp");
            view.forward(request,response);
        }else if(accion.equals("mostrarRegistro")){
            String tipo = request.getParameter("tipo");
            String sku = request.getParameter("sku"); //capturo el SKU de la tabla
            String lote = request.getParameter("lote");
            String zonaNombre = request.getParameter("zonaNombre");
            String prodNombre = request.getParameter("prodNombre");



            //paso los datos al request
            request.setAttribute("sku",sku);
            request.setAttribute("lote",lote);
            request.setAttribute("zonaNombre",zonaNombre);
            request.setAttribute("prodNombre", prodNombre);
            request.setAttribute("zonaId",zonaId);

            if ("entrada".equals(tipo)){
                //para el registro entrada
                request.getRequestDispatcher("/Almacen/registrarEntrada.jsp").forward(request,response);
            } else if ("salida".equals(tipo)) {
                //para el registro salida
                request.getRequestDispatcher("/Almacen/registrarSalida.jsp").forward(request,response);
            }

        } else if (accion.equals("mostrarIncidencia")) {
            String sku = request.getParameter("sku");
            String lote = request.getParameter("lote");
            String zona = request.getParameter("zonaNombre");
            String prodNombre = request.getParameter("prodNombre");
            //paso los datos al request
            request.setAttribute("sku",sku);
            request.setAttribute("lote",lote);
            request.setAttribute("zonaNombre",zona);
            request.setAttribute("prodNombre",prodNombre);
            request.setAttribute("zonaId",zonaId);
            request.getRequestDispatcher("/Almacen/registrarIncidencia.jsp").forward(request,response);
        }


    }


    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String ctx = request.getContextPath();
        String accion = request.getParameter("accion");
        //No hardcodeo
        // int zonaId = 1; //caso zona Oeste
        //reemplazo
        int zonaId = getZonaId(request);


        boolean  exitoIn=false;
        boolean  exitoOut=false;

        //llamo al metodo Dao
        MovimientoDao movimientoDao = new MovimientoDao();

        if("registrarMovimiento".equals(accion)){
            //recibo datos
            String tipo  =request.getParameter("tipo");
            String fechaRegistro =  request.getParameter("fechaRegistro");
            String cantidadStr = (request.getParameter("cantidad"));


            String  loteStr = (request.getParameter("lote"));

            String fechaVencimiento =  request.getParameter("fechaVencimiento");

            String sku = request.getParameter("sku");
            //nuevo
            String prodNombre = request.getParameter("prodNombre");
            String zonaNombre = request.getParameter("zonaNombre");

            //Validacion-NUEVO--------------------------------
            Integer cantidad = null, lote = null;
            try { cantidad = Integer.valueOf(cantidadStr); } catch (Exception ignored) {}
            try { lote = Integer.valueOf(loteStr); } catch (Exception ignored) {}

            if (cantidad == null || cantidad <= 0) {
                request.setAttribute("error", "La cantidad debe ser un número entero mayor que 0.");
                request.setAttribute("sku", sku);
                request.setAttribute("lote", loteStr);
                request.setAttribute("zonaNombre", zonaNombre);
                request.setAttribute("prodNombre", prodNombre);
                if ("IN".equals(tipo)) {
                    request.getRequestDispatcher("/Almacen/registrarEntrada.jsp").forward(request, response);
                } else {
                    request.getRequestDispatcher("/Almacen/registrarSalida.jsp").forward(request, response);
                }
                return;
            }
            if (lote == null || lote <= 0) {
                request.setAttribute("error", "El lote debe ser un número entero válido.");
                request.setAttribute("sku", sku);
                request.setAttribute("lote", loteStr);
                request.setAttribute("zonaNombre", zonaNombre);
                request.setAttribute("prodNombre", prodNombre);
                if ("IN".equals(tipo)) {
                    request.getRequestDispatcher("/Almacen/registrarEntrada.jsp").forward(request, response);
                } else {
                    request.getRequestDispatcher("/Almacen/registrarSalida.jsp").forward(request, response);
                }
                return;
            }
            if (sku == null || sku.isBlank()) {
                request.setAttribute("error", "SKU vacío o inválido.");
                request.setAttribute("sku", sku);
                request.setAttribute("lote", loteStr);
                request.setAttribute("zonaNombre", zonaNombre);
                request.setAttribute("prodNombre", prodNombre);
                if ("IN".equals(tipo)) {
                    request.getRequestDispatcher("/Almacen/registrarEntrada.jsp").forward(request, response);
                } else {
                    request.getRequestDispatcher("/Almacen/registrarSalida.jsp").forward(request, response);
                }
                return;
            }
            if (fechaRegistro == null || fechaRegistro.isBlank()) {
                request.setAttribute("sku", sku);
                request.setAttribute("lote", loteStr);
                request.setAttribute("zonaNombre", zonaNombre);
                request.setAttribute("prodNombre", prodNombre);
                request.setAttribute("error", "La fecha es obligatoria.");
                if ("IN".equals(tipo)) {
                    request.getRequestDispatcher("/Almacen/registrarEntrada.jsp").forward(request, response);
                } else {
                    request.getRequestDispatcher("/Almacen/registrarSalida.jsp").forward(request, response);
                }
                return;
            }
            //----------


            //lleno el bean de movimiento
            Movimiento movimiento = new Movimiento();
            movimiento.setTipoMovimiento(tipo);
            movimiento.setFecha(LocalDate.parse(fechaRegistro));
            movimiento.setCantidad(cantidad);

            Lote l = new Lote();
            l.setIdLote(lote);
            l.setFechaVencimiento(fechaVencimiento);
            Producto p = new Producto();
            p.setSku(sku);
            l.setProducto(p);
            Zonas z  = new Zonas();
            z.setIdZonas(zonaId);
            movimiento.setLote(l);
            movimiento.setZona(z);

            boolean ok = "IN".equals(tipo) ? movimientoDao.registrarEntrada(movimiento) : movimientoDao.registrarSalida(movimiento);

            if (ok) {
                // Redirigir a la tabla con mensaje y datos
                String tag  = "IN".equals(tipo) ? "in_ok" : "out_ok";
                String qSku = URLEncoder.encode(sku != null ? sku : "", StandardCharsets.UTF_8);
                String qPro = URLEncoder.encode(prodNombre != null ? prodNombre : "", StandardCharsets.UTF_8);
                String qCan = URLEncoder.encode(String.valueOf(cantidad), StandardCharsets.UTF_8);
                response.sendRedirect(ctx + "/AlmacenServlet?accion=verInventario&msg=" + tag +
                        "&sku=" + qSku + "&prod=" + qPro + "&cant=" + qCan);
            } else {
                request.setAttribute("error", "Error al registrar el movimiento. Verifique SKU/Lote o stock disponible.");
                request.setAttribute("sku", sku);
                request.setAttribute("lote", String.valueOf(lote));
                request.setAttribute("zonaNombre", zonaNombre);
                request.setAttribute("prodNombre", prodNombre);
                if ("IN".equals(tipo)) {
                    request.getRequestDispatcher("/Almacen/registrarEntrada.jsp").forward(request, response);
                } else {
                    request.getRequestDispatcher("/Almacen/registrarSalida.jsp").forward(request, response);
                }
            }
            return;
        }

        if("registrarIncidencia".equals(accion)){
            Incidencia inc =  new Incidencia();
            String tipoInc = request.getParameter("tipoInc");
            String cantidadIncStr = request.getParameter("cantidadInc");

            String descripcionInc = request.getParameter("descripcionInc");
            String loteStr = request.getParameter("lote");

            String zonaNombre = request.getParameter("zonaNombre");
            String sku = request.getParameter("sku");
            String prodNombre = request.getParameter("prodNombre");
            String estado =  request.getParameter("estado");

            //validacion
            Integer cantidadInc = null, loteInc = null;
            try { cantidadInc = Integer.valueOf(cantidadIncStr); } catch (Exception ignored) {}
            try { loteInc     = Integer.valueOf(loteStr); } catch (Exception ignored) {}

            if (cantidadInc == null || cantidadInc <= 0) {
                request.setAttribute("error", "La cantidad debe ser un número entero mayor que 0.");
                // reinyectar campos mínimos
                request.setAttribute("sku", sku);
                request.setAttribute("lote", loteStr);
                request.setAttribute("zonaNombre", zonaNombre);
                request.setAttribute("prodNombre", prodNombre);
                request.getRequestDispatcher("/Almacen/registrarIncidencia.jsp").forward(request, response);
                return;
            }
            if (loteInc == null || loteInc <= 0) {
                request.setAttribute("error", "El lote debe ser un número entero válido.");
                request.setAttribute("sku", sku);
                request.setAttribute("lote", loteStr);
                request.setAttribute("zonaNombre", zonaNombre);
                request.setAttribute("prodNombre", prodNombre);
                request.getRequestDispatcher("/Almacen/registrarIncidencia.jsp").forward(request, response);
                return;
            }


            //lleno en el bean de incidencia
            inc.setTipoIncidencia(tipoInc);
            inc.setCantidad(cantidadInc);
            inc.setDescripcion(descripcionInc);
            inc.setLote_idLote(loteInc);
            inc.setEstado(estado);

            Zonas zona = new Zonas();
            zona.setIdZonas(zonaId);
            zona.setNombre(zonaNombre);

            Producto p = new Producto();
            p.setNombre(prodNombre);
            p.setSku(sku);

            inc.setProducto(p);
            inc.setZona(zona);

            boolean exitoInc = new MovimientoDao().registrarIncidencia(inc); // según tu implementación existente
            if (exitoInc) {
                // Llévalo a la lista de incidencias (puedes capturar ?msg=inc_ok en incidencias.jsp si quieres)
                response.sendRedirect(ctx + "/IncidenciaAlmServlet?accion=verIncidencias&msg=inc_ok");
            } else {
                request.setAttribute("error", "Error al registrar la incidencia. Verifique SKU/Lote.");
                request.getRequestDispatcher("/Almacen/registrarIncidencia.jsp").forward(request, response);
            }


        }



    }

}
