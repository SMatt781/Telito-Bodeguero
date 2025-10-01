package Servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;

import daos.ProductoDao;
import beans.Producto;

@WebServlet(name = "ProductoPrecioServlet", urlPatterns = {"/ProductoPrecio"})
public class ProductoPrecioServlet extends HttpServlet {

    private final ProductoDao dao = new ProductoDao();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        req.setCharacterEncoding("UTF-8");

        String idStr     = req.getParameter("idProducto");
        String sku       = req.getParameter("sku");
        String nombre    = req.getParameter("nombre");
        String precioStr = req.getParameter("precio");
        String stockStr  = req.getParameter("stock"); // opcional

        try {
            int id = Integer.parseInt(idStr);
            BigDecimal precio = new BigDecimal(precioStr);
            Integer stock = (stockStr == null || stockStr.isBlank()) ? null : Integer.valueOf(stockStr);

            // === Usa UNA de las dos opciones ===

            // Opción A: si implementaste editarProducto(id, sku, nombre, precio) en el DAO
            // dao.editarProducto(id, sku, nombre, precio);

            // Opción B: usando actualizar(Producto p) del DAO
            Producto p = new Producto();
            p.setIdProducto(id);
            p.setSku(sku);
            p.setNombre(nombre);
            p.setPrecio(precio);
            if (stock != null) p.setStock(stock); // si tu form lo manda
            dao.actualizar(p);

            // OK -> vuelve a la lista
            resp.sendRedirect(req.getContextPath() + "/MisProductos");

        } catch (NumberFormatException e) {
            // Datos inválidos del form
            String q = "?idProducto=" + safe(idStr) + "&sku=" + safe(sku)
                    + "&nombre=" + safe(nombre) + "&precio=" + safe(precioStr)
                    + (stockStr != null ? "&stock=" + safe(stockStr) : "");
            req.getSession().setAttribute("error", "Datos inválidos: " + e.getMessage());
            resp.sendRedirect(req.getContextPath() + "/ProductoForm.jsp" + q);

        } catch (SQLException e) {
            // Captura la SQLException del DAO y re-lanza como ServletException
            throw new ServletException("No se pudo actualizar el producto", e);
        }
    }

    private String safe(String s) {
        return URLEncoder.encode(s == null ? "" : s, StandardCharsets.UTF_8);
    }
}

