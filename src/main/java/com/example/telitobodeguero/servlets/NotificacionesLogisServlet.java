package com.example.telitobodeguero.servlets;

import com.example.telitobodeguero.daos.NotiLogisDao;
import com.example.telitobodeguero.dtos.NotificacionLogisDTO;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@WebServlet(name = "NotificacionesLogisServlet", urlPatterns = {"/NotificacionesLogisServlet"})
public class NotificacionesLogisServlet extends HttpServlet {

    private Integer getZonaIdNullable(HttpServletRequest req) {
        Object z = req.getSession().getAttribute("zonaIdActual");
        if (z == null) return null;
        try { return Integer.valueOf(String.valueOf(z)); }
        catch (Exception e) { return null; }
    }

    private int getUmbral(HttpServletRequest req) {
        String u = req.getParameter("umbral");
        int def = 10;
        if (u == null) return def;
        try {
            int val = Integer.parseInt(u);
            return Math.max(0, val);
        } catch (Exception e) {
            return def;
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        try {
            NotiLogisDao notiDao = new NotiLogisDao();

            List<NotificacionLogisDTO> notificacionesTotales = new ArrayList<>();

            // ===== STOCK BAJO =====
            Integer zonaId = getZonaIdNullable(req);
            int umbral = getUmbral(req);

            ArrayList<NotificacionLogisDTO> stockBajo =
                    notiDao.getNotificacionesStockBajoPorMovimientos(zonaId, umbral);
            if (stockBajo != null) notificacionesTotales.addAll(stockBajo);

            // ===== CAMBIO DE ESTADO DE OC =====
            List<NotificacionLogisDTO> cambiosEstado =
                    notiDao.getNotificacionesCambioEstadoOC();
            if (cambiosEstado != null) notificacionesTotales.addAll(cambiosEstado);

            // ===== ORDENAR POR FECHA DESC =====
            notificacionesTotales.sort(
                    Comparator.comparing(NotificacionLogisDTO::getFechaRelevante).reversed()
            );

            req.setAttribute("listaNotificaciones", notificacionesTotales);
            RequestDispatcher view = req.getRequestDispatcher("/Logistica/notificacionesLogis.jsp");
            view.forward(req, resp);

        } catch (Exception e) {
            System.out.println("ERROR en NotificacionesLogisServlet: " + e.getMessage());
            e.printStackTrace();
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Error al consultar notificaciones de logística.");
        }
    }
}
