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
import java.sql.SQLException;
import java.util.List;

@WebServlet(name = "LoteServlet", value = "/Lotes")
public class LoteServlet extends HttpServlet {

    private final LoteDao loteDao = new LoteDao();
    private final ProductoDao productoDao = new ProductoDao();

    // ==========================================================
    // MÉTODO DOGET (Para listar, crear, editar y ELIMINAR)
    // ==========================================================

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        Usuarios usuario = (Usuarios) (session != null ? session.getAttribute("usuarioLog") : null);

        if (usuario == null || usuario.getIdUsuarios() == 0) {
            response.sendRedirect(request.getContextPath() + "/index.jsp");
            return;
        }

        int idProductor = usuario.getIdUsuarios();
        String idProductoStr = request.getParameter("idProducto");
        String action = request.getParameter("action");

        // ==========================================================
        // Manejar la acción 'formCrear'
        // ==========================================================
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
                // Lista de productos para el dropdown (solo visibles/con lotes del productor)
                List<Producto> productos = productoDao.listarVisiblesPorProductor(idProductor);
                request.setAttribute("productos", productos);

                Producto producto = productoDao.obtenerPorId(idProductoNuevo);
                request.setAttribute("productoSeleccionado", producto);

                if (producto != null) {
                    // Calculamos el stock disponible para ser asignado a un nuevo lote
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

        // ==========================================================
        // Manejar la acción 'editar'
        // ==========================================================
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

                    int idProd = Integer.parseInt(lote.getProducto_idProducto());
                    Producto producto = productoDao.obtenerPorId(idProd);

                    int stockDisponible = 0;
                    if (producto != null) {
                        // Stock total disponible = stock restante en maestro + cantidad actual del lote que se está editando
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

        // ==========================================================
        // Manejar la acción 'borrar' 🎯 CORREGIDA PARA REDIRECCIÓN
        // ==========================================================
        if ("borrar".equals(action)) {
            String idLoteStr = request.getParameter("id");
            // String idProductoRedir = request.getParameter("idProducto"); // <-- Se ignora este parámetro

            try {
                int idLote = Integer.parseInt(idLoteStr.trim());

                // Elimina el lote (la lógica de la BD debe manejar la reversión del stock)
                loteDao.eliminarLote(idLote, idProductor);

                // 🛑 CORRECCIÓN: Siempre redirige a la lista base sin filtros,
                // para que el usuario no vea una tabla vacía si borró el último lote del producto filtrado.
                response.sendRedirect(request.getContextPath() + "/Lotes");
                return;

            } catch (NumberFormatException e) {
                System.err.println("Error: ID de lote inválido para borrar.");
            } catch (SQLException e) {
                e.printStackTrace();
                // Si hay un error de BD, guardamos el mensaje y redirigimos
                session.setAttribute("errorMsg", "Error de BD al intentar eliminar el lote.");
            }

            // Redirige al listado principal (con o sin errorMsg en sesión)
            response.sendRedirect(request.getContextPath() + "/Lotes");
            return;
        }


        // ==========================================================
        // Lógica de Listado (Por defecto)
        // ==========================================================

        try {
            // Muestra mensaje de error si existe uno de las acciones POST o BORRAR
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
    // MÉTODO DOPOST (Para guardar/actualizar datos)
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
            // El campo ubicación fue eliminado del JSP, asignamos un valor por defecto.
            String ubicacion = "";
            String fechaVencimiento = request.getParameter("fechaVencimiento");

            Lote lote = new Lote();

            try {
                int idProducto = Integer.parseInt(idProductoStr);
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

                // Redirige al listado con el filtro de producto (ya que el producto aún existe)
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
                // Si falla la transacción (por ejemplo, stock negativo o restricción de BD)
                session.setAttribute("errorMsg", "Error de base de datos al guardar/actualizar el lote. Verifique el stock.");
                e.printStackTrace();
                response.sendRedirect(request.getContextPath() + "/Lotes");
            }
        } else {
            response.sendRedirect(request.getContextPath() + "/Lotes");
        }
    }
}
