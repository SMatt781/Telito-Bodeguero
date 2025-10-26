package com.example.telitobodeguero.servlets;

import com.example.telitobodeguero.beans.OrdenCompra;
import com.example.telitobodeguero.beans.Lote;
import com.example.telitobodeguero.beans.Usuarios;
import com.example.telitobodeguero.daos.LoteDao;
import com.example.telitobodeguero.daos.OrdenCompraDao;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@WebServlet(name = "OrdenCompraProdServlet", value = "/OrdenesCompraProd")
public class OrdenCompraProdServlet extends HttpServlet {

    private static final int ROL_PRODUCTOR = 4; // Rol de Productor

    // ==========================================================
    // MÉTODO DOGET: Listar y Cargar Modal
    // ==========================================================
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        Usuarios usuarioLog = (Usuarios) (session != null ? session.getAttribute("usuarioLog") : null);

        // 1. Validación de Sesión y Rol
        if (usuarioLog == null || usuarioLog.getRol() == null || usuarioLog.getRol().getIdRoles() != ROL_PRODUCTOR) {
            response.sendRedirect(request.getContextPath() + "/index.jsp");
            return;
        }

        int idProductor = usuarioLog.getIdUsuarios();
        String action = Optional.ofNullable(request.getParameter("a")).orElse("listar");
        OrdenCompraDao ocDao = new OrdenCompraDao();

        // --- Manejo de acciones del Productor ---
        switch (action) {
            case "recibir":
                this.recibirOrden(request, response, ocDao);
                break;

            case "mostrar_despacho":
                // Carga la data y la guarda en la SESIÓN antes de redirigir
                this.mostrarModalDespacho(request, response, ocDao, idProductor);
                break;

            case "listar":
            default:
                this.listarOrdenes(request, response, idProductor, ocDao);
                break;
        }
    }

    // ==========================================================
    // MÉTODO DOPOST: Despacho Transaccional
    // ==========================================================
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession(false);
        Usuarios usuarioLog = (Usuarios) (session != null ? session.getAttribute("usuarioLog") : null);

        // 1. Validar Sesión y Rol
        if (usuarioLog == null || usuarioLog.getRol() == null || usuarioLog.getRol().getIdRoles() != ROL_PRODUCTOR) {
            response.sendRedirect(request.getContextPath() + "/index.jsp");
            return;
        }

        String action = Optional.ofNullable(request.getParameter("a")).orElse("");

        if ("despachar".equals(action)) {
            this.despacharItem(request, response);
        } else {
            response.sendRedirect(request.getContextPath() + "/OrdenesCompraProd");
        }
    }

    // ==========================================================
    // MÉTODOS PRIVADOS DE LÓGICA
    // ==========================================================

    /**
     * Carga el detalle de un ítem de orden y los lotes disponibles para ese producto,
     * guardando los datos en la SESIÓN para el modal.
     */
    private void mostrarModalDespacho(HttpServletRequest request, HttpServletResponse response, OrdenCompraDao ocDao, int idProductor) throws ServletException, IOException {
        String idItemStr = request.getParameter("idItem");

        try {
            int idItem = Integer.parseInt(idItemStr);
            LoteDao loteDao = new LoteDao();

            // 1. Obtener Detalle del Ítem (usando idItem)
            OrdenCompra ocDetalle = ocDao.obtenerDetalleParaDespacho(idItem);

            if (ocDetalle != null && ocDetalle.getProducto() != null) {
                // 2. Obtener Lotes disponibles para ese Producto y Productor
                int idProducto = ocDetalle.getProducto().getIdProducto();

                List<Lote> lotesDisponibles = loteDao.listarLotesPorProducto(idProducto, idProductor);

                // 🛑 CORRECCIÓN CLAVE: Guardar la data en la SESIÓN antes de redirigir
                request.getSession().setAttribute("ocDetalle", ocDetalle);
                request.getSession().setAttribute("lotesDisponibles", lotesDisponibles);

            } else {
                request.getSession().setAttribute("errorMsg", "Ítem de orden no encontrado o ya despachado.");
            }

        } catch (NumberFormatException e) {
            request.getSession().setAttribute("errorMsg", "Error: ID de Ítem de Orden inválido.");
        } catch (SQLException e) {
            e.printStackTrace();
            request.getSession().setAttribute("errorMsg", "Error de BD al cargar lotes disponibles: " + e.getMessage());
        }

        // Redirige al listado, el JSP detectará los atributos de sesión para mostrar el modal.
        response.sendRedirect(request.getContextPath() + "/OrdenesCompraProd");
    }

    /**
     * Maneja la transacción de despacho de UN ÍTEM (reducción de stock y asignación de lote).
     */
    private void despacharItem(HttpServletRequest request, HttpServletResponse response) throws IOException {
        OrdenCompraDao ocDao = new OrdenCompraDao();
        LoteDao loteDao = new LoteDao();
        HttpSession session = request.getSession();

        // Parámetros CLAVE (Obtenidos del formulario POST)
        String idItemStr = request.getParameter("idItemDespachar");
        String idLoteStr = request.getParameter("idLoteSeleccionado");
        String cantidadReqStr = request.getParameter("cantidadRequerida");

        int idItem = 0, idLote = 0, cantidadReq = 0;
        Connection conn = null;

        try {
            idItem = Integer.parseInt(idItemStr);
            idLote = Integer.parseInt(idLoteStr);
            cantidadReq = Integer.parseInt(cantidadReqStr);

            // 1. Abrir conexión e INICIAR TRANSACCIÓN
            conn = ocDao.getOpenConnection();
            conn.setAutoCommit(false);

            try {
                // 2. Reducir el stock del Lote
                loteDao.reducirStockLote(conn, idLote, cantidadReq);

                // 3. Asignar el Lote al Item de la Orden
                ocDao.asignarLoteADetalle(conn, idItem, idLote);

                // 4. Cambiar estado de la orden a "En tránsito"
                int idOrden = ocDao.obtenerIdOrdenDesdeItem(conn, idItem);
                ocDao.actualizarEstadoSimple(idOrden, "En tránsito");

                conn.commit(); // Éxito: CONFIRMAR ambas operaciones

                // Éxito
                session.setAttribute("successMsg", "Ítem (ID:" + idItem + ") despachado. Lote " + idLote + " asignado y stock actualizado. Estado cambiado a 'En tránsito'.");

            } catch (SQLException e) {
                // Falla: Deshacer toda la transacción
                if (conn != null) conn.rollback();
                e.printStackTrace();
                String msg = "Error en el despacho. Revise el stock del lote.";

                // Intento de detectar error de stock
                if (e.getMessage() != null && e.getMessage().contains("stock")) {
                    msg = "Error de Stock: La cantidad requerida excede el stock disponible del lote. Revise la consola del servidor para detalles.";
                }
                session.setAttribute("errorMsg", msg);
            } finally {
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            }

        } catch (NumberFormatException e) {
            session.setAttribute("errorMsg", "Error en el formato de los datos (ID/Cantidad). Asegúrese de seleccionar un lote.");
        } catch (SQLException e) {
            session.setAttribute("errorMsg", "Error de conexión/BD al iniciar la transacción.");
        }

        response.sendRedirect(request.getContextPath() + "/OrdenesCompraProd");
    }

    private void listarOrdenes(HttpServletRequest request, HttpServletResponse response, int idProductor, OrdenCompraDao ocDao) throws ServletException, IOException {
        try {
            // Usa el método que trae la lista de ítems pendientes
            List<OrdenCompra> itemsPendientes = ocDao.listarOCConItemsParaProductor(idProductor);
            request.setAttribute("filas", itemsPendientes);

            // Los atributos de sesión del modal son limpiados en el JSP antes de ir al forward.

            request.getRequestDispatcher("/Productor/OrdenesCompra.jsp").forward(request, response);
        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("errorMsg", "Error al listar las órdenes: " + e.getMessage());
            request.getRequestDispatcher("/Productor/OrdenesCompra.jsp").forward(request, response);
        }
    }

    private void recibirOrden(HttpServletRequest request, HttpServletResponse response, OrdenCompraDao ocDao) throws IOException {
        // ... (Tu código para confirmar recepción de la OC completa)
        String idOrdenStr = request.getParameter("id");
        int idOrden;

        try {
            idOrden = Integer.parseInt(idOrdenStr);
            ocDao.actualizarEstadoSimple(idOrden, "Recibido");

            HttpSession session = request.getSession();
            session.setAttribute("successMsg", "Orden de Compra " + idOrden + " recibida. Lista para Despachar sus ítems.");

            response.sendRedirect(request.getContextPath() + "/OrdenesCompraProd");

        } catch (NumberFormatException e) {
            HttpSession session = request.getSession();
            session.setAttribute("errorMsg", "ID de orden inválido.");
            response.sendRedirect(request.getContextPath() + "/OrdenesCompraProd");
        } catch (SQLException e) {
            e.printStackTrace();
            HttpSession session = request.getSession();
            session.setAttribute("errorMsg", "Error de BD al confirmar recepción.");
            response.sendRedirect(request.getContextPath() + "/OrdenesCompraProd");
        }
    }
}