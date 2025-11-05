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

@WebServlet(name = "NotificacionesLogisServlet", urlPatterns = {"/NotificacionesLogisServlet"})
public class NotificacionesLogisServlet extends HttpServlet {

    private Integer getZonaIdNullable(HttpServletRequest req) {
        Object z = req.getSession().getAttribute("zonaIdActual");
        if (z == null) return null;                  // sin filtro por zona
        try { return Integer.valueOf(String.valueOf(z)); }
        catch (Exception e) { return null; }         // si viene mal formado, ignora filtro
    }

    private int getUmbral(HttpServletRequest req) {
        String u = req.getParameter("umbral");       // permite /NotificacionesLogisServlet?umbral=12
        int def = 10;
        if (u == null) return def;
        try {
            int val = Integer.parseInt(u);
            return Math.max(0, val);                 // evita negativos
        } catch (Exception e) {
            return def;
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        NotiLogisDao notiDao = new NotiLogisDao();
        ArrayList<NotificacionLogisDTO> notificacionesTotales = new ArrayList<>();

        try {
            Integer zonaId = getZonaIdNullable(req); // null => todas las zonas
            int umbral = getUmbral(req);             // default 10 si no envías param

            // Stock bajo calculado desde movimientos (tu SQL real)
            ArrayList<NotificacionLogisDTO> stockBajo =
                    notiDao.getNotificacionesStockBajoPorMovimientos(zonaId, umbral);
            if (stockBajo != null) notificacionesTotales.addAll(stockBajo);

            // Ordena por fecha (recientes primero)
            notificacionesTotales.sort(Comparator.comparing(NotificacionLogisDTO::getFechaRelevante).reversed());

            req.setAttribute("listaNotificaciones", notificacionesTotales);
            RequestDispatcher view = req.getRequestDispatcher("/Logistica/notificacionesLogis.jsp");
            view.forward(req, resp);

        } catch (Exception e) {
            System.out.println("Error en NotificacionesLogisServlet: " + e.getMessage());
            e.printStackTrace();
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Error al consultar notificaciones de logística.");
        }
    }
}
