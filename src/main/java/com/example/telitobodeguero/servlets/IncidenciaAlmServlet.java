package com.example.telitobodeguero.servlets;


import com.example.telitobodeguero.beans.*;
import com.example.telitobodeguero.daos.IncidenciaDao;
import com.example.telitobodeguero.daos.MovimientoDao;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Date;


@WebServlet(name = "IncidenciaAlmServlet",value = "/IncidenciaAlmServlet")
public class IncidenciaAlmServlet extends HttpServlet{

    private int getZonaId(HttpServletRequest req) {
        Object z = req.getSession().getAttribute("zonaIdActual");
        if (z instanceof Integer) return (Integer) z;
        try { return Integer.parseInt(String.valueOf(z)); } catch (Exception e) { return 1; }
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //No hardcodeo
        // int zonaId = 1; //caso zona Oeste
        //reemplazo
        int zonaId = getZonaId(request);

        String accion = request.getParameter("accion");
        if(accion==null || accion.isEmpty() || accion.equals("verIncidencias")){
            IncidenciaDao  incidenciaDao = new IncidenciaDao();
            ArrayList<Incidencia> listaIncidencias = incidenciaDao.obtenerIncidencias(zonaId);
            request.setAttribute("listaIncidencias", listaIncidencias);
            RequestDispatcher view = request.getRequestDispatcher("/Almacen/incidencias.jsp");
            view.forward(request,response);

            return;
        }

        response.sendRedirect(request.getContextPath() + "/AlmacenServlet?accion=verInventario");
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //No hardcodeo
        // int zonaId = 1; //caso zona Oeste
        //reemplazo
        int zonaId = getZonaId(request);
        String accion = request.getParameter("accion");
        String idIncStr = request.getParameter("idInc");
        int idInc = 0;
        if (idIncStr != null && !idIncStr.trim().isEmpty()){
            idInc = Integer.parseInt(idIncStr);
        }
        IncidenciaDao incidenciaDao = new IncidenciaDao();
        boolean exitoMantener = false;
        boolean exitoQuitar = false;
        String estado;
        String mensaje;
        if("mantener".equals(accion)){
            estado = "MANTENIDA";
            exitoMantener = incidenciaDao.mantener(estado,idInc);
            if(exitoMantener){
                //response.sendRedirect(request.getContextPath()+"/Almacen/incidencias.jsp");
                //System.out.println("La acción se realizó correctamente");
                mensaje = "success|Incidencia " + idInc + " marcada como MANTENIDA.";
            }else {
                //System.out.println("Ocurrio un error");
                mensaje = "error|Error al actualizar la Incidencia " + idInc + " a MANTENIDA.";
            }
        } else if ("quitar".equals(accion)) {
            //recupero los datos

            String tipoInc = request.getParameter("tipo");
            String cantidadStr = request.getParameter("cantidad");

            String loteIdStr = request.getParameter("loteId");
            int loteId = 0;
            if (loteIdStr != null && !loteIdStr.trim().isEmpty()){
                loteId = Integer.parseInt(loteIdStr);
            }
            String bloqueIdStr = request.getParameter("bloqueId");
            String ubicacion = request.getParameter("ubicacion");
            int cantidad = 0;
            if (idIncStr != null && !idIncStr.trim().isEmpty()){
                cantidad = Integer.parseInt(cantidadStr);
            }
            int bloqueId = 0;
            if (bloqueIdStr != null && !idIncStr.trim().isEmpty()){
                bloqueId = Integer.parseInt(bloqueIdStr);
            }
            //para obtener fecha
            LocalDate fechaRegistro = LocalDate.now();

            //creo mi dao de mov
            Movimiento m =  new Movimiento();
            m.setTipoMovimiento("OUT");
            m.setCantidad(cantidad);
            Lote lote = new Lote();
            lote.setIdLote(loteId);
            m.setLote(lote);
            Zonas z = new Zonas();
            z.setIdZonas(zonaId);
            m.setZona(z);
            Bloque b = new Bloque();
            b.setIdBloque(bloqueId);
            b.setCodigo(ubicacion);
            m.setBloque(b);
            m.setFecha(fechaRegistro);

            //para buscar el stock por fila
            int stockFila = incidenciaDao.obtenerStockFila(loteId,bloqueId,zonaId);

            //ahora que ya tengo, llamo al metodo de salida
            MovimientoDao movimientoDao = new MovimientoDao();
            String res = movimientoDao.registrarSalidaConBloque(m,bloqueId,stockFila);

            if("ok".equals(res)){
                incidenciaDao.marcarQuitada(idInc);
                mensaje = "success|Se retiró la incidencia correctamente.";
            }else{
                mensaje = "error|Error al retirar la incidencia: " + res;
            }




        }else{
            response.sendRedirect(request.getContextPath() + "/IncidenciaAlmServlet?accion=verIncidencias");
            return;
        }

        response.sendRedirect(request.getContextPath()
                + "/IncidenciaAlmServlet?accion=verIncidencias&statusMessage="
                + URLEncoder.encode(mensaje, StandardCharsets.UTF_8.toString()));
        return;


    }



}
