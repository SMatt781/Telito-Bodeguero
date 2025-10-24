package com.example.telitobodeguero.servlets;

// ... (Importaciones necesarias) ...
import com.example.telitobodeguero.beans.Producto;
import com.example.telitobodeguero.beans.Usuarios;
import com.example.telitobodeguero.daos.ProductoDao;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;


// 🛑 Mapeo DOBLE: /PreciosRecomendados para listar (GET) y /ProductoPrecio para acción (POST)
@WebServlet(name = "ProductoPrecioServlet", urlPatterns = {"/PreciosRecomendados", "/ProductoPrecio"})
public class ProductoPrecioServlet extends HttpServlet {

    private final ProductoDao dao = new ProductoDao();

    // ==========================================================
    // MÉTODO DOGET: Para listar la tabla de Precios (Separada)
    // ==========================================================
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        Usuarios usuarioLog = (Usuarios) session.getAttribute("usuarioLog");

        if (usuarioLog == null || usuarioLog.getRol().getIdRoles() != 4) {
            response.sendRedirect(request.getContextPath() + "/index.jsp");
            return;
        }

        // ⚠️ Nota: No necesitamos el ID del productor aquí si 'listarTodos' no filtra.
        // Pero si quieres que esta lista de precios solo muestre productos de SU zona,
        // necesitarías el ID. Por ahora, asumiremos que es el catálogo completo.

        try {
            // 🎯 Carga la lista COMPLETA de productos para mostrar precios
            List<Producto> catalogoCompleto = dao.listarTodos();

            // Usamos un atributo diferente al del otro Servlet para evitar conflictos si usaran el mismo JSP.
            request.setAttribute("preciosRecomendados", catalogoCompleto);

            // Redirigir a un JSP DEDICADO para esta tabla. Si usas MisProductos.jsp,
            // este JSP DEBE USAR el atributo "preciosRecomendados".
            request.getRequestDispatcher("/Productor/PreciosRecomendados.jsp").forward(request, response);

        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("error", "Error de base de datos al listar precios: " + e.getMessage());
            request.getRequestDispatcher("/error.jsp").forward(request, response);
        }
    }


    // ==========================================================
    // MÉTODO DOPOST: Actualización de precio/stock
    // ==========================================================
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        // ... (CÓDIGO POST INTACTO) ...
        req.setCharacterEncoding("UTF-8");

        HttpSession session = req.getSession();
        Usuarios usuarioLog = (Usuarios) session.getAttribute("usuarioLog");

        if (usuarioLog == null || usuarioLog.getRol().getIdRoles() != 4) {
            resp.sendRedirect(req.getContextPath() + "/index.jsp");
            return;
        }
        int idProductor = usuarioLog.getIdUsuarios();

        String idStr     = req.getParameter("idProducto");
        String sku       = req.getParameter("sku");
        String nombre    = req.getParameter("nombre");
        String precioStr = req.getParameter("precio");
        String stockStr  = req.getParameter("stock");

        try {
            // Lógica de validación
            if (idStr == null || idStr.isBlank()) {
                throw new IllegalArgumentException("Falta el ID del producto.");
            }
            int id = Integer.parseInt(idStr);

            if (precioStr == null || !precioStr.matches("\\d+(\\.\\d{1,2})?")) {
                throw new IllegalArgumentException("Precio inválido. Usa formato 0.00");
            }

            Integer stock = (stockStr == null || stockStr.isBlank()) ? null : Integer.valueOf(stockStr);

            Producto p = new Producto();
            p.setIdProducto(id);
            p.setSku(sku);
            p.setNombre(nombre);
            p.setPrecio(precioStr);
            if (stock != null) p.setStock(stock);

            // SEGURIDAD: Llama al DAO
            if (!dao.esPropiedadDeProductor(id, idProductor)) {
                throw new SecurityException("Intento de modificar un producto ajeno.");
            }

            // ACTUALIZACIÓN: Llama al DAO
            dao.actualizar(p);

            // ✅ ÉXITO: Redirige al listado principal (MisProductosServlet)
            resp.sendRedirect(req.getContextPath() + "/MisProductos");

        } catch (SecurityException e) {
            // ❌ ERROR DE SEGURIDAD
            req.getSession().setAttribute("error", "Acceso denegado: " + e.getMessage());
            resp.sendRedirect(req.getContextPath() + "/MisProductos");

        } catch (IllegalArgumentException e) {
            // ❌ ERROR DE VALIDACIÓN
            String q = "?idProducto=" + safe(idStr) + "&sku=" + safe(sku)
                    + "&nombre=" + safe(nombre) + "&precio=" + safe(precioStr)
                    + (stockStr != null ? "&stock=" + safe(stockStr) : "");

            req.getSession().setAttribute("error", "Datos inválidos: " + e.getMessage());
            resp.sendRedirect(req.getContextPath() + "/Productor/ProductoForm.jsp" + q);

        } catch (SQLException e) {
            // ❌ ERROR DE BASE DE DATOS
            req.getSession().setAttribute("error", "Error de base de datos: " + e.getMessage());
            resp.sendRedirect(req.getContextPath() + "/MisProductos");
        }
    }

    private String safe(String s) {
        return URLEncoder.encode(s == null ? "" : s, StandardCharsets.UTF_8);
    }
}