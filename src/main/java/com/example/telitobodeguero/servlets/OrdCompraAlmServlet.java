package com.example.telitobodeguero.servlets;

import com.example.telitobodeguero.beans.*;
import com.example.telitobodeguero.daos.MovimientoDao;
import com.example.telitobodeguero.daos.OrdenCompraDaoAlm;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.util.ArrayList;

@WebServlet(name = "OrdCompraAlmServlet",value = "/OrdCompraAlmServlet")
public class OrdCompraAlmServlet extends HttpServlet {
    private int getZonaId(HttpServletRequest req) {
        Object z = req.getSession().getAttribute("zonaIdActual");
        if (z instanceof Integer) return (Integer) z;
        try { return Integer.parseInt(String.valueOf(z)); } catch (Exception e) { return 1; }
    }
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
        int zonaid = getZonaId(request);

        String accion = request.getParameter("accion");
        if(accion==null || accion.isEmpty() || accion.equals("verIncidencias")) {
            OrdenCompraDaoAlm incidenciaDao = new OrdenCompraDaoAlm();
            ArrayList<OrdenCompra> oc_transito = incidenciaDao.getOCEnTransito(zonaid);
            ArrayList<OrdenCompra> oc_completadas = incidenciaDao.getOCCompletadas(zonaid);
            ArrayList<OrdenCompra> oc_registradas = incidenciaDao.getOCRegistradas(zonaid);
            request.setAttribute("oc_transito", oc_transito);
            request.setAttribute("oc_completadas", oc_completadas);
            request.setAttribute("oc_registradas", oc_registradas);
            RequestDispatcher view = request.getRequestDispatcher("/Almacen/ordenCompra.jsp");
            view.forward(request, response);

            return;
        }
        response.sendRedirect(request.getContextPath() + "/OrdCompraAlmServlet?accion=verIncidencias");
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
        int zonaid = getZonaId(request);

        String accion = request.getParameter("accion");

        OrdenCompraDaoAlm ocDao = new OrdenCompraDaoAlm();
        String mensaje;

        if("confirmarLlegada".equals(accion)) {
            String idOCStr = request.getParameter("idOC");
            int idOC = 0;
            if (idOCStr != null && !idOCStr.trim().isEmpty()){
                idOC = Integer.parseInt(idOCStr);
            }
            boolean exitoConfLlegada = ocDao.marcarCompletada(idOC);

            if(exitoConfLlegada){
                mensaje = "success|OC " + idOC + " marcada como COMPLETADA.";
            }else {
                mensaje = "error|Error al actualizar la OC " + idOC + " marcada como COMPLETADA.";
            }
            response.sendRedirect(request.getContextPath()
                    + "/OrdCompraAlmServlet?accion=verIncidencias&statusMessage="
                    + URLEncoder.encode(mensaje, "UTF-8"));
            return;

        } else if ("registrarEntrada".equals(accion)) {
            // Para el id de OC
            String idOCStr = request.getParameter("idOC");
            int idOC = 0;
            if (idOCStr != null && !idOCStr.trim().isEmpty()){
                idOC = Integer.parseInt(idOCStr);
            }
            //para la cantidad
            String cantStr = request.getParameter("cantidad");
            int cant = 0;
            if (cantStr != null && !cantStr.trim().isEmpty()){
                cant = Integer.parseInt(cantStr);
            }
            //para el loteID
            String loteIdStr = request.getParameter("loteId");
            int loteId = 0;
            if (loteIdStr != null && !loteIdStr.trim().isEmpty()){
                loteId = Integer.parseInt(loteIdStr);
            }

            final int CAPACIDAD_NUEVO_BLOQUE = 300;
            OrdenCompraDaoAlm ocAyudaDao = new OrdenCompraDaoAlm();
            MovimientoDao movDao = new MovimientoDao();
            Bloque bloqueAsignado = null;

            //primero se busca el bloque
            bloqueAsignado = ocAyudaDao.buscarBloqueDisponible(zonaid, cant);

            // si no se encontro un bloque
            if(bloqueAsignado == null){

                //se crea otro
                Bloque nuevoBloque = ocAyudaDao.crearNuevoBloqueSecuencial(zonaid);

                if (nuevoBloque == null) {
                    //error critico
                    response.sendRedirect(request.getContextPath() + "/OrdenCompraServlet?statusMessage=error|Error crítico: No se pudo crear un nuevo bloque.");
                    return;
                }
                //verificamos si el nuevo bloque tiene suficiente espacio
                if (CAPACIDAD_NUEVO_BLOQUE < cant) {
                    response.sendRedirect(request.getContextPath() + "/OrdenCompraServlet?statusMessage=error|La OC es demasiado grande para un bloque nuevo.");
                    return;
                }
                bloqueAsignado = nuevoBloque;

            }

            //con el bloque lleno el ben de mov
            Movimiento m = new Movimiento();
            m.setFecha(LocalDate.now());
            m.setTipoMovimiento("IN");
            m.setCantidad(cant);

            Lote lote = new Lote();
            lote.setIdLote(loteId);
            m.setLote(lote);

            Zonas z = new Zonas();
            z.setIdZonas(zonaid);
            m.setZona(z);

            m.setBloque(bloqueAsignado);

            //insertar la entrada
            String resultado = movDao.registrarEntradaDinamica(m, idOC);

            //redirigir con el resultado
            if ("ok".equals(resultado)) {
                mensaje = "success|Entrada de OC " + idOC + " registrada en " + bloqueAsignado.getCodigo();
            } else {
                mensaje = "error|Error al registrar la entrada: " + resultado;
            }
             response.sendRedirect(request.getContextPath() + "/OrdCompraAlmServlet?statusMessage=" + URLEncoder.encode(mensaje, "UTF-8"));
             return;

        }

        response.sendRedirect(request.getContextPath() + "/OrdCompraAlmServlet?accion=verIncidencias");
    }

}
