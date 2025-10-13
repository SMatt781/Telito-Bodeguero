package com.example.telitobodeguero.servlets;


import com.example.telitobodeguero.beans.Incidencia;
import com.example.telitobodeguero.beans.Lote;
import com.example.telitobodeguero.beans.Movimiento;
import com.example.telitobodeguero.beans.Zonas;
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
import java.util.Date;


@WebServlet(name = "IncidenciaAlmServlet",value = "/IncidenciaAlmServlet")
public class IncidenciaAlmServlet extends HttpServlet{

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int zonaId = 2;

        String accion = request.getParameter("accion");
        if(accion==null || accion.isEmpty() || accion.equals("verIncidencias")){
            IncidenciaDao  incidenciaDao = new IncidenciaDao();
            ArrayList<Incidencia> listaIncidencias = incidenciaDao.obtenerIncidencias(zonaId);
            request.setAttribute("listaIncidencias", listaIncidencias);
            RequestDispatcher view = request.getRequestDispatcher("/Almacen/incidencias.jsp");
            view.forward(request,response);
        }
    }


    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int zonaId = 2;
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
        String mensaje="";
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
            estado = "QUITADA";
            String tipo = "OUT";
            String cantidad = request.getParameter("cantidad");
            LocalDate fechaAhora =  LocalDate.now();
            //Date fecha = java.sql.Date.valueOf(fechaAhora);
            String idLote = request.getParameter("idLote");

            //pongo en el bean mov
            Movimiento mov =  new Movimiento();
            mov.setTipoMovimiento(tipo);
            mov.setCantidad(Integer.valueOf(cantidad));
            mov.setFecha(fechaAhora);
            Lote lote = new Lote();
            lote.setIdLote(Integer.valueOf(idLote));
            mov.setLote(lote);
            Zonas zona = new Zonas();
            zona.setIdZonas(zonaId);
            mov.setZona(zona);

            exitoQuitar = incidenciaDao.quitar(mov, estado, idInc);

            if (exitoQuitar){
//                response.sendRedirect(request.getContextPath()+"/Almacen/incidencias.jsp");
//                System.out.println("La acción se realizó correctamente");
                mensaje = "success|Incidencia " + idInc + " marcada como QUITADA. Stock de Lote " + idLote + " disminuido en " + cantidad + ".";
            }else{
//                System.out.println("Ocurrio un error");
                mensaje = "error|Error al procesar la acción QUITAR para la Incidencia " + idInc + ".";
            }

        }

        //cargo datos
        ArrayList<Incidencia> listaIncidencias = incidenciaDao.obtenerIncidencias(zonaId);

        //envio el msj y lista a la jsp
        request.setAttribute("listaIncidencias", listaIncidencias);
        request.setAttribute("statusMessage", mensaje);

        //redireccionar quedandose en la msima URL
        RequestDispatcher view = request.getRequestDispatcher("/Almacen/incidencias.jsp");
        view.forward(request,response);




    }



}

