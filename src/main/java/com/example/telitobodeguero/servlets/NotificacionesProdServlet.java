package com.example.telitobodeguero.servlets;

import com.example.telitobodeguero.daos.OrdenCompraDao;
import com.example.telitobodeguero.beans.OrdenCompra;
import com.example.telitobodeguero.beans.Usuarios;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.*;

@WebServlet(name = "NotificacionesServlet", value = "/Notificaciones")
public class NotificacionesProdServlet extends HttpServlet {

    private final OrdenCompraDao dao = new OrdenCompraDao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession ses = request.getSession(false);
        Usuarios user = (Usuarios) (ses != null ? ses.getAttribute("usuarioLog") : null);

        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        int idProductor = user.getIdUsuarios();
        String action = request.getParameter("a");
        String idStr = request.getParameter("id");

        // 1️⃣ Inicializar lista de vistos en sesión si no existe
        @SuppressWarnings("unchecked")
        Set<Integer> ordenesVistas = (Set<Integer>) ses.getAttribute("ordenesVistas");
        if (ordenesVistas == null) {
            ordenesVistas = new HashSet<>();
            ses.setAttribute("ordenesVistas", ordenesVistas);
        }

        // 2️⃣ Marcar una notificación como vista
        if ("visto".equals(action) && idStr != null) {
            try {
                int idOrden = Integer.parseInt(idStr);
                ordenesVistas.add(idOrden);
                ses.setAttribute("ordenesVistas", ordenesVistas);
            } catch (NumberFormatException ignored) {}
            response.sendRedirect(request.getContextPath() + "/Notificaciones");
            return;
        }

        // 3️⃣ Obtener órdenes del productor
        List<OrdenCompra> lista = new ArrayList<>();
        try {
            lista = dao.listarOCConItemsParaProductor(idProductor);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 4️⃣ Convertir órdenes en mensajes legibles
        List<Map<String, String>> notificaciones = new ArrayList<>();
        for (OrdenCompra oc : lista) {
            String estado = oc.getEstado();
            String mensaje = null;

            switch (estado) {
                case "Enviada":
                    mensaje = "📦 Nueva orden #" + oc.getCodigoOrdenCompra() +
                            " ha sido enviada. Verifica los productos solicitados.";
                    break;
                case "Recibido":
                    mensaje = "🧾 Orden #" + oc.getCodigoOrdenCompra() +
                            " fue confirmada. Prepara el despacho del lote.";
                    break;
                case "En tránsito":
                    mensaje = "🚚 La orden #" + oc.getCodigoOrdenCompra() +
                            " está en tránsito hacia el almacén.";
                    break;
            }

            if (mensaje != null) {
                Map<String, String> noti = new HashMap<>();
                noti.put("id", String.valueOf(oc.getCodigoOrdenCompra()));
                noti.put("mensaje", mensaje);
                noti.put("estado", estado);
                notificaciones.add(noti);
            }
        }

        // 5️⃣ Enviar al JSP
        request.setAttribute("notificaciones", notificaciones);
        request.setAttribute("ordenesVistas", ordenesVistas);
        request.getRequestDispatcher("/Productor/Notificaciones.jsp").forward(request, response);
    }
}
