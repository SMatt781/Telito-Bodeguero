package com.example.telitobodeguero.servlets;

import com.example.telitobodeguero.beans.Alertas;
import com.example.telitobodeguero.beans.OrdenCompra;
import com.example.telitobodeguero.daos.InicioAdminDao;
import com.example.telitobodeguero.daos.ProductoDaoLogis;

// ⬇️ IMPORTS NUEVOS
import com.example.telitobodeguero.daos.NotificacionesAdminDao;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet(name = "InicioAdminServlet", value = "/InicioAdminServlet")
public class InicioAdminServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        // =========================
        // 1) Tu lógica existente
        // =========================
        InicioAdminDao alertasDao = new InicioAdminDao();
        alertasDao.generarAlertasDesdeStock();
        ArrayList<Alertas> listaAlertas = alertasDao.obtenerListaAlertas();
        request.setAttribute("lista", listaAlertas);
        request.setAttribute("cantidadAlertas", listaAlertas.size());

        InicioAdminDao incidenciaDao = new InicioAdminDao();
        int cantidadIncidencias = incidenciaDao.obtenerCantidadIncidencias();
        request.setAttribute("cantidadReportes", cantidadIncidencias);

        InicioAdminDao gastoZonaDao = new InicioAdminDao();
        ArrayList<Object[]> listaGastos = gastoZonaDao.obtenerGastosPorZona();
        request.setAttribute("listaGastos", listaGastos);

        int usuariosActivos = gastoZonaDao.contarUsuariosActivos();
        request.setAttribute("usuariosActivos", usuariosActivos);

        ProductoDaoLogis prodDao = new ProductoDaoLogis();
        int alertasStockBajo = prodDao.contarTotalProductosStockBajo();
        request.setAttribute("alertasStockBajo", alertasStockBajo);

        // =========================
        // 2) SCANNER de notificaciones (SOLO AQUÍ)
        // =========================
        HttpSession session = request.getSession(true);

        // Estados previos por OC
        @SuppressWarnings("unchecked")
        Map<Integer, String> estadosPrevios =
                (Map<Integer, String>) session.getAttribute("adminEstadosPrevios");
        if (estadosPrevios == null) estadosPrevios = new HashMap<>();

        // Cola persistente de notificaciones
        @SuppressWarnings("unchecked")
        List<Map<String, String>> queue =
                (List<Map<String, String>>) session.getAttribute("adminNotiQueue");
        if (queue == null) queue = new ArrayList<>();

        // Traer órdenes actuales (usa tu DAO PLURAL: NotificacionesAdminDao)
        NotificacionesAdminDao notifDao = new NotificacionesAdminDao();
        List<OrdenCompra> ordenes = notifDao.listarOrdenesParaAdmin();

        // Detectar cambios y apilar (sin duplicar por key = "id|estadoNuevo")
        for (OrdenCompra oc : ordenes) {
            int id = oc.getCodigoOrdenCompra();    // getters tuyos
            String estadoActual = oc.getEstado();
            String estadoPrevio = estadosPrevios.get(id);

            if (estadoPrevio != null && estadoActual != null &&
                    !estadoPrevio.equalsIgnoreCase(estadoActual)) {

                String key = id + "|" + estadoActual;
                boolean existe = false;
                for (Map<String, String> n : queue) {
                    if (key.equals(n.get("key"))) { existe = true; break; }
                }
                if (!existe) {
                    String msg = notifDao.generarMensajeCambioEstado(estadoPrevio, estadoActual, id);
                    Map<String, String> n = new HashMap<>();
                    n.put("key", key);
                    n.put("id", String.valueOf(id));
                    n.put("tipo", "OC_ESTADO");
                    n.put("estadoAnterior", estadoPrevio);
                    n.put("estadoNuevo", estadoActual);
                    n.put("mensaje", msg);
                    n.put("ts", new Date().toString());
                    n.put("leido", "0");
                    queue.add(n);
                }
            }

            // Actualiza “último visto” (inicializa si es la 1ra vez)
            if (estadoActual != null) {
                estadosPrevios.put(id, estadoActual);
            }
        }

        // Persistir en sesión (para la campana y el JSP de notificaciones)
        session.setAttribute("adminEstadosPrevios", estadosPrevios);
        session.setAttribute("adminNotiQueue", queue);

        // =========================
        // 3) Forward a tu dashboard
        // =========================
        RequestDispatcher view = request.getRequestDispatcher("/Admin_Inicio.jsp");
        view.forward(request, response);
    }
}
