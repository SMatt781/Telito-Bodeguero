package com.example.telitobodeguero.servlets;

import com.example.telitobodeguero.daos.NotiAlmDao;
import com.example.telitobodeguero.dtos.NotificacionAlmDTO;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;

@WebServlet(name = "NotificacionesServlet", urlPatterns = {"/NotificacionesServlet"})
public class NotificacionesAlmServlet extends HttpServlet {
    private int getZonaId(HttpServletRequest req) {
        Object z = req.getSession().getAttribute("zonaIdActual");
        if (z instanceof Integer) return (Integer) z;
        try { return Integer.parseInt(String.valueOf(z)); } catch (Exception e) { return 1; }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int zonaId = getZonaId(req);

        ArrayList<NotificacionAlmDTO> notificacionesTotales = new ArrayList<>();
        NotiAlmDao notiDao = new NotiAlmDao();
        try{
            notificacionesTotales.addAll(notiDao.getNotificacionesOrdenesLlegada(zonaId));
            notificacionesTotales.addAll(notiDao.getNotificacionesOrdenesRetrasadas(zonaId));
            notificacionesTotales.addAll(notiDao.getNotificacionesBloquesCriticos(zonaId));
            notificacionesTotales.addAll(notiDao.getNotificacionesIncidencias(zonaId));

            //para ordenar la lista total por fecha
            notificacionesTotales.sort(Comparator.comparing(NotificacionAlmDTO::getFechaRelevante).reversed());
            req.setAttribute("listaNotificaciones", notificacionesTotales);
            RequestDispatcher view = req.getRequestDispatcher("/Almacen/notificaciones.jsp");
            view.forward(req, resp);
        }catch (Exception e){
            System.out.println("Error de SQL en NotificacionesServlet: "+e.getMessage());
            e.printStackTrace();
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error al consultar la base de datos.");
        }
    }
}
