package com.example.telitobodeguero.servlets;

import com.example.telitobodeguero.beans.Lote;
import com.example.telitobodeguero.beans.Producto;
import com.example.telitobodeguero.beans.Usuarios;
import com.example.telitobodeguero.daos.LoteDao;
import com.example.telitobodeguero.daos.ProductoDao;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import jakarta.servlet.http.*;
import java.sql.SQLException;
import java.util.List;
import java.io.PrintWriter;

@WebServlet(name = "LoteServlet", value = "/Lotes")
public class LoteServlet extends HttpServlet {

    private final LoteDao loteDao = new LoteDao();
    private final ProductoDao productoDao = new ProductoDao();

    // ==========================================================
    // MÉTODO DOGET
    // ==========================================================

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        Usuarios usuario = (Usuarios) (session != null ? session.getAttribute("usuarioLog") : null);

        // 1. Validar sesión
        if (usuario == null || usuario.getIdUsuarios() == 0) {
            response.sendRedirect(request.getContextPath() + "/index.jsp");
            return;
        }

        int idProductor = usuario.getIdUsuarios();
        String idProductoStr = request.getParameter("idProducto");
        String action = request.getParameter("action");
        String a = request.getParameter("a"); // Usamos 'a' para la acción AJAX

        // 🛑 LÓGICA DE LISTADO DE LOTES COMO JSON PARA AJAX 🛑
        if ("listarJson".equals(a)) {
            // Se asume que este método se usaría para cargar lotes dinámicamente en algún otro formulario/modal.
            listarLotesJson(request, response, idProductor, idProductoStr);
            return; // Termina el doGet aquí para la solicitud AJAX
        }

        // --- Lógica de la interfaz de gestión de lotes (Resto de las acciones) ---

        if ("formCrear".equals(action)) {
            String idProdFiltro = request.getParameter("idProducto");
            if (idProdFiltro == null || idProdFiltro.isEmpty()) {
                response.sendRedirect(request.getContextPath() + "/Lotes");
                return;
            }

            int idProductoNuevo;
            try {
                idProductoNuevo = Integer.parseInt(idProdFiltro.trim());
            } catch (NumberFormatException e) {
                response.sendRedirect(request.getContextPath() + "/Lotes");
                return;
            }

            request.setAttribute("idProductoNuevo", idProductoNuevo);

            try {
                List<Producto> productos = productoDao.listarVisiblesPorProductor(idProductor);
                request.setAttribute("productos", productos);

                Producto producto = productoDao.obtenerPorId(idProductoNuevo);
                request.setAttribute("productoSeleccionado", producto);

                if (producto != null) {
                    int stockDisponible = productoDao.stockRestanteParaUsuario(idProductor, idProductoNuevo);
                    request.setAttribute("disponible", stockDisponible);
                }

            } catch (SQLException e) {
                System.err.println("Error al obtener datos para el formulario: " + e.getMessage());
                request.setAttribute("error", "Error interno al cargar datos del producto.");
            }

            request.getRequestDispatcher("/Productor/LoteForm.jsp").forward(request, response);
            return;
        }

        if ("editar".equals(action)) {
            String idLoteStr = request.getParameter("id");
            int idLote;

            try {
                idLote = Integer.parseInt(idLoteStr.trim());
            } catch (NumberFormatException e) {
                response.sendRedirect(request.getContextPath() + "/Lotes");
                return;
            }

            try {
                Lote lote = loteDao.obtenerPorId(idLote);

                if (lote != null && lote.getUsuarios_idUsuarios() == idProductor) {

                    request.setAttribute("lote", lote);
                    List<Producto> productos = productoDao.listarVisiblesPorProductor(idProductor);
                    request.setAttribute("productos", productos);

                    // Se asume que getProducto_idProducto() devuelve un String que se puede parsear
                    int idProd = Integer.parseInt(lote.getProducto_idProducto());
                    Producto producto = productoDao.obtenerPorId(idProd);

                    int stockDisponible = 0;
                    if (producto != null) {
                        int stockRestante = productoDao.stockRestanteParaUsuario(idProductor, idProd);
                        stockDisponible = stockRestante + lote.getCantidad();
                    }
                    request.setAttribute("disponible", stockDisponible);

                    request.getRequestDispatcher("/Productor/LoteForm.jsp").forward(request, response);
                    return;

                } else {
                    response.sendRedirect(request.getContextPath() + "/Lotes");
                    return;
                }
            } catch (SQLException | NumberFormatException e) {
                e.printStackTrace();
                request.setAttribute("error", "Error al cargar el lote para edición: " + e.getMessage());
                request.getRequestDispatcher("/error.jsp").forward(request, response);
                return;
            }
        }

        if ("borrar".equals(action)) {
            String idLoteStr = request.getParameter("id");

            try {
                int idLote = Integer.parseInt(idLoteStr.trim());

                loteDao.eliminarLote(idLote, idProductor);

                response.sendRedirect(request.getContextPath() + "/Lotes");
                return;

            } catch (NumberFormatException e) {
                System.err.println("Error: ID de lote inválido para borrar.");
            } catch (SQLException e) {
                e.printStackTrace();
                session.setAttribute("errorMsg", "Error de BD al intentar eliminar el lote.");
            }

            response.sendRedirect(request.getContextPath() + "/Lotes");
            return;
        }


        // Lógica de Listado (Por defecto)
        try {
            String errorMsg = (String) session.getAttribute("errorMsg");
            if (errorMsg != null) {
                request.setAttribute("error", errorMsg);
                session.removeAttribute("errorMsg");
            }

            List<Lote> lista = loteDao.listarPorProductor(idProductor, idProductoStr);
            List<Producto> productos = productoDao.listarVisiblesPorProductor(idProductor);

            request.setAttribute("productos", productos);
            request.setAttribute("lista", lista);

            if (idProductoStr != null && !idProductoStr.trim().isEmpty()) {
                request.setAttribute("idProducto", Integer.parseInt(idProductoStr.trim()));
            }

            request.getRequestDispatcher("/Productor/GestionLotes.jsp").forward(request, response);

        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("error", "Ocurrió un error al listar los lotes: " + e.getMessage());
            request.getRequestDispatcher("/error.jsp").forward(request, response);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            request.setAttribute("error", "El filtro de producto debe ser un número válido.");
            request.getRequestDispatcher("/error.jsp").forward(request, response);
        }
    }

    // ==========================================================
    // MÉTODO AUXILIAR: Listar Lotes para AJAX (JSON) - CORREGIDO
    // ==========================================================
    private void listarLotesJson(HttpServletRequest request, HttpServletResponse response, int idProductor, String idProductoStr)
            throws IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        // Verificación 1: ID de Producto inválido
        if (idProductoStr == null || idProductoStr.trim().isEmpty() || idProductoStr.trim().equalsIgnoreCase("null")) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"error\": \"ID de Producto no proporcionado o inválido.\"}");
            return;
        }

        try {
            int idProducto = Integer.parseInt(idProductoStr.trim());

            // Verificación 2: ID de Producto es cero
            if (idProducto == 0) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("{\"error\": \"ID de Producto no puede ser cero.\"}");
                return;
            }

            // 🛑 AJUSTE CLAVE: Usamos listarLotesPorProducto
            List<Lote> lotes = loteDao.listarLotesPorProducto(idProducto, idProductor);

            try (PrintWriter out = response.getWriter()) {
                out.write("[");

                boolean first = true;
                for (Lote lote : lotes) {
                    if (!first) {
                        out.write(","); // Separador entre objetos
                    }

                    out.write("{");

                    // idLote
                    out.write("\"idLote\":" + lote.getIdLote() + ",");

                    // cantidad (Stock disponible)
                    out.write("\"cantidad\":" + lote.getCantidad() + ",");

                    // fechaVencimiento
                    String fechaVencimiento = lote.getFechaVencimiento() != null ?
                            "\"" + lote.getFechaVencimiento() + "\"" : "null";
                    out.write("\"fechaVencimiento\":" + fechaVencimiento);

                    out.write("}");
                    first = false;
                }

                out.write("]");
            }

        } catch (NumberFormatException e) {
            // Error: El idProducto enviado por el JSP no era un número
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"error\": \"ID de Producto inválido (no numérico).\"}");
        } catch (SQLException e) {
            // Error: Fallo en la consulta a la base de datos (Error 500)
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

            // Mensaje de error seguro para JSON
            String mensajeError = e.getMessage() != null ?
                    e.getMessage().replace("\"", "'").replace("\n", " ") :
                    "Error de BD desconocido.";

            response.getWriter().write("{\"error\": \"Error de base de datos al listar lotes: " + mensajeError + "\"}");
        } catch (Exception e) {
            // Captura cualquier otro error no esperado
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\": \"Error interno inesperado en el servidor.\"}");
        }
    }


    // ==========================================================
    // MÉTODO DOPOST
    // ==========================================================
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession(false);
        Usuarios usuario = (Usuarios) (session != null ? session.getAttribute("usuarioLog") : null);

        if (usuario == null || usuario.getIdUsuarios() == 0) {
            response.sendRedirect(request.getContextPath() + "/index.jsp");
            return;
        }

        int idProductor = usuario.getIdUsuarios();

        String action = request.getParameter("action");
        String idLoteStr = request.getParameter("idLote");
        String idProductoStr = request.getParameter("idProducto");

        if ("guardar".equals(action)) {

            String cantidadStr = request.getParameter("cantidad");
            String fechaVencimiento = request.getParameter("fechaVencimiento");

            Lote lote = new Lote();

            try {
                int cantidad = Integer.parseInt(cantidadStr);

                lote.setProducto_idProducto(idProductoStr);
                lote.setCantidad(cantidad);
                lote.setFechaVencimiento(fechaVencimiento);
                lote.setUsuarios_idUsuarios(idProductor);

                if (idLoteStr == null || idLoteStr.isEmpty()) {
                    loteDao.crearLote(lote);
                    session.setAttribute("successMsg", "Lote creado exitosamente.");
                } else {
                    int idLote = Integer.parseInt(idLoteStr);
                    lote.setIdLote(idLote);
                    loteDao.actualizarLote(lote);
                    session.setAttribute("successMsg", "Lote actualizado exitosamente.");
                }

                String redirectTo = request.getContextPath() + "/Lotes";
                if (idProductoStr != null && !idProductoStr.isEmpty()) {
                    redirectTo += "?idProducto=" + idProductoStr;
                }
                response.sendRedirect(redirectTo);

            } catch (NumberFormatException e) {
                session.setAttribute("errorMsg", "Error en el formato de los datos (Cantidad o ID de Producto).");
                e.printStackTrace();
                response.sendRedirect(request.getContextPath() + "/Lotes");

            } catch (SQLException e) {
                session.setAttribute("errorMsg", "Error de base de datos al guardar/actualizar el lote. Verifique el stock.");
                e.printStackTrace();
                response.sendRedirect(request.getContextPath() + "/Lotes");
            }
        } else {
            response.sendRedirect(request.getContextPath() + "/Lotes");
        }
    }
}