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

    // --- Métodos Helper (copiados de tu DAO para usarlos aquí) ---
    private String normalizar(String s) {
        if (s == null) return "";
        s = s.trim().toLowerCase();
        s = s.replace("tránsito", "transito");
        return s;
    }

    private Map<String,String> buscarNotiEnQueue(List<Map<String,String>> q, String key){
        for (Map<String,String> n : q) {
            if (key.equals(n.get("key"))) return n;
        }
        return null; // No encontrado
    }
    // --- Fin de Métodos Helper ---

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // 1. Obtenemos las órdenes filtradas (solo estados del "flujo") desde el DAO
        List<OrdenCompra> listaOrdenesDB = notifDao.listarOrdenesParaAdmin();

        // 2. Obtenemos la cola de notificaciones existente de la sesión
        HttpSession session = request.getSession(); // Obtenemos o creamos sesión
        @SuppressWarnings("unchecked")
        List<Map<String,String>> queueEnSesion = (List<Map<String,String>>) session.getAttribute("adminNotiQueue");
        if (queueEnSesion == null) queueEnSesion = new ArrayList<>();

        // 3. Preparamos la nueva lista que verá el JSP
        List<Map<String, String>> notificacionesParaJsp = new ArrayList<>();

        // 4. Sincronizamos la lista de la BD con la lista de la Sesión
        for (OrdenCompra oc : listaOrdenesDB) {

            // Generamos una clave ÚNICA para esta orden Y su estado actual
            // Ej: "oc_123_en-transito"
            // Asumo que tu bean tiene getIdOrdenCompra()
            String key = "oc_" + oc.getCodigoOrdenCompra() + "_" + normalizar(oc.getEstado());

            // 5. Buscamos si esta notificación (orden+estado) ya existe en la sesión
            Map<String,String> notiExistente = buscarNotiEnQueue(queueEnSesion, key);

            if (notiExistente != null) {
                // SÍ EXISTE: La re-agregamos a la lista del JSP con su estado de "leido"
                notificacionesParaJsp.add(notiExistente);
            } else {
                // NO EXISTE: Es una orden en un estado nuevo.
                // La creamos y la marcamos como "no leída"
                Map<String, String> n = new HashMap<>();
                n.put("id", String.valueOf(oc.getCodigoOrdenCompra()));
                n.put("mensaje", notifDao.getMensajeEstadoActual(oc)); // Usamos el nuevo método del DAO

                // Asumo que tu bean tiene getFechaRegistro() o getFechaActualizacion()
                // Cambia esto por el campo de fecha que tengas
                n.put("ts", oc.getFechaLlegada() != null ? oc.getFechaLlegada().toString() : "Fecha no disp.");
                n.put("leido", "0"); // Nueva, por lo tanto no leída
                n.put("key", key); // Guardamos la clave única

                notificacionesParaJsp.add(n);
            }
        }

        // 6. Guardamos la lista "sincronizada" de nuevo en la sesión
        //    Esto preserva el estado "leido" de las notis existentes
        //    y añade las nuevas como "no leídas".
        session.setAttribute("adminNotiQueue", notificacionesParaJsp);

        // 7. Enviamos la lista al JSP
        request.setAttribute("notificaciones", notificacionesParaJsp);
        request.setAttribute("otrasNotificaciones", new ArrayList<Map<String,String>>()); // reservado

        request.getRequestDispatcher("/NotificacionesAdmin.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        // TU MÉTODO doPost ORIGINAL SE MANTIENE EXACTAMENTE IGUAL
        // Ahora funcionará correctamente porque el 'key' es único por estado.

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
