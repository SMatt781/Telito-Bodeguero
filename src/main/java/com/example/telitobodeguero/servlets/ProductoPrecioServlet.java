package com.example.telitobodeguero.servlets;

import com.example.telitobodeguero.beans.Producto;
import com.example.telitobodeguero.daos.ProductoDao;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;

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
        String precioStr = req.getParameter("precio");   // precio como String
        String stockStr  = req.getParameter("stock");    // opcional

        try {
            // Validaciones básicas
            if (idStr == null || idStr.isBlank()) {
                throw new IllegalArgumentException("Falta el ID del producto.");
            }
            int id = Integer.parseInt(idStr);

            // Validación simple del precio: "123" o "123.45"
            if (precioStr == null || !precioStr.matches("\\d+(\\.\\d{1,2})?")) {
                throw new IllegalArgumentException("Precio inválido. Usa formato 0.00");
            }

            Integer stock = (stockStr == null || stockStr.isBlank()) ? null : Integer.valueOf(stockStr);

            // Construir el bean
            Producto p = new Producto();
            p.setIdProducto(id);
            p.setSku(sku);
            p.setNombre(nombre);
            p.setPrecio(precioStr);     // String
            if (stock != null) p.setStock(stock);

            // Actualizar en BD
            dao.actualizar(p);

            // OK -> vuelve a la lista
            resp.sendRedirect(req.getContextPath() + "/MisProductos");

        } catch (IllegalArgumentException e) {
            // Cubre NumberFormatException y otras validaciones
            String q = "?idProducto=" + safe(idStr) + "&sku=" + safe(sku)
                    + "&nombre=" + safe(nombre) + "&precio=" + safe(precioStr)
                    + (stockStr != null ? "&stock=" + safe(stockStr) : "");
            req.getSession().setAttribute("error", "Datos inválidos: " + e.getMessage());
            resp.sendRedirect(req.getContextPath() + "/Productor/ProductoForm.jsp" + q);

        } catch (SQLException e) {
            throw new ServletException("No se pudo actualizar el producto", e);
        }
    }

    private String safe(String s) {
        return URLEncoder.encode(s == null ? "" : s, StandardCharsets.UTF_8);
    }
}



