package com.example.telitobodeguero.servlets;

import com.example.telitobodeguero.beans.OrdenCompra;
import com.example.telitobodeguero.daos.NotificacionesAdminDao;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.util.*;

@WebServlet(name = "NotificacionesAdminServlet", value = "/Admin/Notificaciones")
public class NotificacionesAdminServlet extends HttpServlet {

    private final NotificacionesAdminDao notifDao = new NotificacionesAdminDao();

    @SuppressWarnings("unchecked")
    private List<Map<String,String>> getQueue(HttpSession session){
        Object obj = session.getAttribute("adminNotiQueue");
        return (obj instanceof List) ? (List<Map<String,String>>) obj : new ArrayList<>();
    }

    private boolean existeEnQueue(List<Map<String,String>> q, String key){
        for (Map<String,String> n : q) {
            if (key.equals(n.get("key"))) return true;
        }
        return false;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false); // no crear sesión nueva aquí
        @SuppressWarnings("unchecked")
        List<Map<String,String>> queue = (session == null)
                ? new ArrayList<>()
                : (List<Map<String,String>>) session.getAttribute("adminNotiQueue");
        if (queue == null) queue = new ArrayList<>();

        request.setAttribute("notificaciones", queue);
        request.setAttribute("otrasNotificaciones", new ArrayList<Map<String,String>>()); // reservado

        // IMPORTANTE: usa tu JSP real dentro de /Admin
        request.getRequestDispatcher("/NotificacionesAdmin.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession(false);
        if (session == null) { resp.sendRedirect(req.getContextPath()+"/Admin/Notificaciones"); return; }

        String action = req.getParameter("action");
        @SuppressWarnings("unchecked")
        List<Map<String,String>> queue =
                (List<Map<String,String>>) session.getAttribute("adminNotiQueue");
        if (queue == null) queue = new ArrayList<>();

        if ("markAll".equals(action)) {
            for (Map<String,String> n : queue) n.put("leido","1");
        } else if ("markOne".equals(action)) {
            String key = req.getParameter("key");
            for (Map<String,String> n : queue) {
                if (Objects.equals(key, n.get("key"))) { n.put("leido","1"); break; }
            }
        }
        session.setAttribute("adminNotiQueue", queue);
        resp.sendRedirect(req.getContextPath()+"/Admin/Notificaciones");
    }
}
