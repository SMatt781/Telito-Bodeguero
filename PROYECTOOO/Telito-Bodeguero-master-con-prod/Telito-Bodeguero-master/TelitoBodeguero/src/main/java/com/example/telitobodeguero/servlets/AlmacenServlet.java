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
import com.example.telitobodeguero.daos.ProductoDaoLogis;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;

@WebServlet(name = "AlmacenServlet",value = "/AlmacenServlet")
public class AlmacenServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        int zonaId = 2; //caso zona Oeste
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



            //paso los datos al request
            request.setAttribute("sku",sku);
            request.setAttribute("lote",lote);
            request.setAttribute("zonaNombre",zonaNombre);
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
            request.getRequestDispatcher("/Almacen/registrarIncidencia.jsp").forward(request,response);
        }


    }


    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String accion = request.getParameter("accion");
        int zonaId = 2; //caso zona Oeste


        boolean  exitoIn=false;
        boolean  exitoOut=false;
        //boolean exitoInc=false;
        //llamo al metodo Dao
        MovimientoDao movimientoDao = new MovimientoDao();

        if("registrarMovimiento".equals(accion)){
            //recibo datos
            String tipo  =request.getParameter("tipo");
            String fechaRegistro =  request.getParameter("fechaRegistro");
            String cantidadStr = (request.getParameter("cantidad"));
            int cantidad = 0;
            if (cantidadStr != null && !cantidadStr.trim().isEmpty()){
                cantidad = Integer.parseInt(cantidadStr);
            }
            String  loteStr = (request.getParameter("lote"));
            int lote = 0;
            if (loteStr != null && !loteStr.trim().isEmpty()){
                lote = Integer.parseInt(loteStr.trim());
            }
            String fechaVencimiento =  request.getParameter("fechaVencimiento");
//            int idZona = Integer.parseInt(request.getParameter("idZona"));
            String sku = request.getParameter("sku");

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

            if("IN".equals(tipo)){
                exitoIn = movimientoDao.registrarEntrada(movimiento);
                if(exitoIn){
                    response.sendRedirect(request.getContextPath()+"/AlmacenServlet?accion=verInventario");

                }else{
                    String mensaje = "Error al registrar la entrada. Verifique que el SKU y el Lote sean válidos.";
                    request.setAttribute("error",mensaje);
                    request.getRequestDispatcher("/Almacen/registrarEntrada.jsp").forward(request,response);
                }
            }else  if("OUT".equals(tipo)){
                exitoOut = movimientoDao.registrarSalida(movimiento);
                if(exitoOut){
                    response.sendRedirect(request.getContextPath()+"/AlmacenServlet?accion=verInventario");
                }else{
                    String mensaje = "Error al registrar la entrada. Verifique que el SKU y el Lote sean válidos.";
                    request.setAttribute("error",mensaje);
                    request.getRequestDispatcher("/Almacen/registrarSalida.jsp").forward(request,response);
                }
            }

        }else if("registrarIncidencia".equals(accion)){
            Incidencia inc =  new Incidencia();
            String tipoInc = request.getParameter("tipoInc");
            String cantidadIncStr = request.getParameter("cantidadInc");
            int cantidadInc = 0;
            if (cantidadIncStr != null && !cantidadIncStr.trim().isEmpty()){
                cantidadInc = Integer.parseInt(cantidadIncStr);
            }
            String descripcionInc = request.getParameter("descripcionInc");
            String loteIncStr = request.getParameter("lote");
            int loteInc=0;
            if(loteIncStr != null && !loteIncStr.trim().isEmpty()){
                loteInc = Integer.parseInt(loteIncStr);
            }
            String zonaNombre = request.getParameter("zonaNombre");
            String sku = request.getParameter("sku");
            String prodNombre = request.getParameter("prodNombre");
            String estado =  request.getParameter("estado");

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

            //llamo al metodo
            boolean exitoInc=movimientoDao.registrarIncidencia(inc);
            if(exitoInc){
                response.sendRedirect(request.getContextPath()+"/AlmacenServlet?accion=verInventario");
            }else{
                request.setAttribute("error","Error al registrar la incidencia. Verifique que el SKU y el Lote sean válidos.");
                request.getRequestDispatcher("/Almacen/registrarIncidencia.jsp").forward(request,response);
            }


        }



    }

}
