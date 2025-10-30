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

    //------HELPERS--------
    //NUEVO 14/10:
    private int getZonaId(HttpServletRequest req) {
        Object z = req.getSession().getAttribute("zonaIdActual");
        if (z instanceof Integer) return (Integer) z;
        try { return Integer.parseInt(String.valueOf(z)); } catch (Exception e) { return 1; }
    }


    private void injectProductoRowAttrs(HttpServletRequest req) {
        // Campos que la tabla manda como hidden SIEMPRE para abrir formularios
        req.setAttribute("sku",        req.getParameter("sku"));
        req.setAttribute("prodNombre", req.getParameter("prodNombre"));
        req.setAttribute("loteId",     req.getParameter("loteId"));
        req.setAttribute("bloqueId",   req.getParameter("bloqueId"));
        req.setAttribute("ubicacion",  req.getParameter("ubicacion")); // código del bloque
        req.setAttribute("zonaNombre", req.getParameter("zonaNombre"));
        req.setAttribute("stockFila",  req.getParameter("stockFila")); // stock mostrado en esa fila
    }



    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        int zonaId = getZonaId(request);
        String accion = request.getParameter("accion");

        if (accion == null || accion.isEmpty() || "verInventario".equals(accion)) {
            ProductoDaoAlm productoDaoAlm = new ProductoDaoAlm();
            ArrayList<Producto> listaProd = productoDaoAlm.obtenerProductos(zonaId);
            request.setAttribute("listaProductos", listaProd);
            request.getRequestDispatcher("/Almacen/gestionAlmacen.jsp").forward(request, response);
            return;
        }

        // Cualquier otro GET, por ahora vuelve a inventario
        response.sendRedirect(request.getContextPath() + "/AlmacenServlet?accion=verInventario");
    }




    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String ctx   = request.getContextPath();
        String accion = request.getParameter("accion");
        int zonaId   = getZonaId(request); // tu helper existente

        // Helpers locales (simple parsing y reinyectar atributos de la fila)
        java.util.function.Function<String,Integer> parseIntOrNull = s -> {
            try { return (s==null || s.isBlank()) ? null : Integer.valueOf(s.trim()); } catch (Exception e){ return null; }
        };
        java.util.function.Function<String,Integer> parseIntOrZero = s -> {
            try { return (s==null || s.isBlank()) ? 0 : Integer.valueOf(s.trim()); } catch (Exception e){ return 0; }
        };
//        Runnable injectFromRow = () -> {
//            request.setAttribute("sku",        request.getParameter("sku"));
//            request.setAttribute("prodNombre", request.getParameter("prodNombre"));
//            request.setAttribute("loteId",     request.getParameter("loteId"));
//            request.setAttribute("bloqueId",   request.getParameter("bloqueId"));
//            request.setAttribute("ubicacion",  request.getParameter("ubicacion"));  // código del bloque
//            request.setAttribute("zonaNombre", request.getParameter("zonaNombre"));
//            request.setAttribute("stockFila",  request.getParameter("stockFila"));  // solo aplica a salida
//        };

        MovimientoDao movDao = new MovimientoDao();

        // ======================== REGISTRAR MOVIMIENTO (IN/OUT) ========================
        if ("registrarMovimiento".equals(accion)) {
            String fase = request.getParameter("fase"); // "form" | "grabar"
            String tipo = request.getParameter("tipo"); // "IN"  | "OUT"

            // ---- Abrir formulario desde la tabla (POST sin URL larga) ----
            if ("form".equalsIgnoreCase(fase)) {
                injectProductoRowAttrs(request);
                request.setAttribute("fechaHoy", java.time.LocalDate.now().toString());
                if ("IN".equalsIgnoreCase(tipo)) {
                    request.getRequestDispatcher("/Almacen/registrarEntrada.jsp").forward(request, response);
                } else {
                    request.getRequestDispatcher("/Almacen/registrarSalida.jsp").forward(request, response);
                }
                return;
            }

            // ---- Grabar movimiento ----
            if ("grabar".equalsIgnoreCase(fase)) {
                String fechaStr     = request.getParameter("fechaRegistro");
                String cantidadStr  = request.getParameter("cantidad");   // si vacío => 0 (tu convención)
                String loteStr      = request.getParameter("loteId");
                String bloqueIdStr  = request.getParameter("bloqueId");
                String stockFilaStr = request.getParameter("stockFila");  // solo importa en OUT
                String ubi = request.getParameter("ubicacion");
                // Beans mínimos
                Movimiento mov = new Movimiento();
                mov.setTipoMovimiento(tipo != null ? tipo : "");
                mov.setCantidad(parseIntOrZero.apply(cantidadStr));  // vacío => 0

                try {
                    mov.setFecha((fechaStr==null || fechaStr.isBlank()) ? null : java.time.LocalDate.parse(fechaStr));
                } catch (Exception e) {
                    mov.setFecha(null);
                }

                Lote l = new Lote();
                l.setIdLote(parseIntOrZero.apply(loteStr)); // si no llega, 0 (DAO validará)
                mov.setLote(l);

                Zonas z = new Zonas();
                z.setIdZonas(zonaId);
                mov.setZona(z);

                Integer bloqueId  = parseIntOrNull.apply(bloqueIdStr);
                Integer stockFila = parseIntOrNull.apply(stockFilaStr); // puede ser null para IN

                Bloque bloque = new Bloque();
                bloque.setIdBloque(bloqueId);
                bloque.setCodigo(ubi);
                mov.setBloque(bloque);

                String resultado;
                if ("IN".equalsIgnoreCase(tipo)) {
                    // Valida cantidad>0, fecha, lote>0, bloque válido y capacidad dentro del DAO
                    resultado = movDao.registrarEntradaBackup(mov, (bloqueId!=null?bloqueId:0));
                    if (!"ok".equals(resultado)) {
                        request.setAttribute("error", resultado);
                        injectProductoRowAttrs(request); // reinyecta datos de la fila
                        request.getRequestDispatcher("/Almacen/registrarEntrada.jsp").forward(request, response);
                        return;
                    }
                } else {
                    // Valida cantidad>0, fecha, no dejar la FILA negativa (stockFila) y no extraer más del BLOQUE
                    resultado = movDao.registrarSalidaConBloque(mov, (bloqueId!=null?bloqueId:0), stockFila);
                    if (!"ok".equals(resultado)) {
                        request.setAttribute("error", resultado);
                        injectProductoRowAttrs(request); // reinyecta datos de la fila
                        request.getRequestDispatcher("/Almacen/registrarSalida.jsp").forward(request, response);
                        return;
                    }
                }

                // Éxito → PRG (Post/Redirect/Get) con mensaje
                String msgTag = "IN".equalsIgnoreCase(tipo) ? "in_ok" : "out_ok";
                String qSku = java.net.URLEncoder.encode(s(request.getParameter("sku")), java.nio.charset.StandardCharsets.UTF_8);
                String qPro = java.net.URLEncoder.encode(s(request.getParameter("prodNombre")), java.nio.charset.StandardCharsets.UTF_8);
                String qCan = java.net.URLEncoder.encode(String.valueOf(mov.getCantidad()), java.nio.charset.StandardCharsets.UTF_8);
                response.sendRedirect(ctx + "/AlmacenServlet?accion=verInventario&msg=" + msgTag +
                        "&sku=" + qSku + "&prod=" + qPro + "&cant=" + qCan);
                return;
            }

            // Fallback si fase desconocida
            response.sendRedirect(ctx + "/AlmacenServlet?accion=verInventario");
            return;
        }

        // ======================== REGISTRAR INCIDENCIA ========================
        if ("registrarIncidencia".equals(accion)) {
            String fase = request.getParameter("fase"); // "form" | "grabar"

            // Abrir formulario desde la tabla
            if ("form".equalsIgnoreCase(fase)) {
                injectProductoRowAttrs(request);
                request.getRequestDispatcher("/Almacen/registrarIncidencia.jsp").forward(request, response);
                return;
            }

            // Grabar incidencia
            if ("grabar".equalsIgnoreCase(fase)) {
                String tipoInc  = request.getParameter("tipoInc");         // FALTANTE|VENCIDO|DAÑO
                String cantInc  = request.getParameter("cantidadInc");     // vacío => 0 (convención)
                String descInc  = request.getParameter("descripcionInc");
                String loteStr  = request.getParameter("loteId");            // id lote
                String estado   = request.getParameter("estado");          // QUITADA|MANTENIDA|REGISTRADA
                String ubicacion  = request.getParameter("ubicacion");
                String bloqueIdStr = request.getParameter("bloqueId");

                Incidencia inc = new Incidencia();
                inc.setTipoIncidencia(tipoInc);
                inc.setDescripcion(descInc);
                inc.setEstado(estado);
                inc.setLote_idLote(parseIntOrZero.apply(loteStr)); // 0 si no llegó (DAO valida)
                inc.setCantidad(parseIntOrZero.apply(cantInc));    // 0 si vacío (DAO valida)

                // (Opcional) para mostrar en JSP si retorna error
                Zonas zona = new Zonas();
                zona.setIdZonas(zonaId);
                zona.setNombre(request.getParameter("zonaNombre"));
                inc.setZona(zona);

                Producto prod = new Producto();
                prod.setSku(request.getParameter("sku"));
                prod.setNombre(request.getParameter("prodNombre"));
                inc.setProducto(prod);

                inc.setUbicacionTemporal(ubicacion);
                inc.setIdUbicacion(parseIntOrZero.apply(bloqueIdStr));

                String resultado = movDao.registrarIncidencia(inc);
                if (!"ok".equals(resultado)) {
                    request.setAttribute("error", resultado);
                    injectProductoRowAttrs(request); // conserva datos de la fila
                    request.getRequestDispatcher("/Almacen/registrarIncidencia.jsp").forward(request, response);
                    return;
                }

                response.sendRedirect(ctx + "/IncidenciaAlmServlet?accion=verIncidencias&msg=inc_ok");
                return;
            }

            // Fallback si fase desconocida
            response.sendRedirect(ctx + "/AlmacenServlet?accion=verInventario");
            return;
        }

        // ======================== Fallback otras acciones ========================
        response.sendRedirect(ctx + "/AlmacenServlet?accion=verInventario");
    }

    // Helper para evitar nulls al armar los mensajes de éxito
    private String s(String v) { return v != null ? v : ""; }



}
