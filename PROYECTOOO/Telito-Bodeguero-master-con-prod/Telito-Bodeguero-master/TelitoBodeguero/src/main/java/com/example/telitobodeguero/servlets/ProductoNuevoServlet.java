package com.example.telitobodeguero.servlets;

import com.example.telitobodeguero.beans.Producto;
import com.example.telitobodeguero.daos.ProductoDao;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet(name = "ProductoNuevoServlet", urlPatterns = {"/ProductoNuevo"})
public class ProductoNuevoServlet extends HttpServlet {

    private final ProductoDao productoDao = new ProductoDao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        RequestDispatcher rd = request.getRequestDispatcher("/ProductoForm.jsp");
        rd.forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        try {
            String sku    = request.getParameter("sku");
            String nombre = request.getParameter("nombre");
            String precio = request.getParameter("precio"); // ahora String
            int stock     = Integer.parseInt(request.getParameter("stock"));

            Producto p = new Producto();
            p.setSku(sku);
            p.setNombre(nombre);
            p.setPrecio(precio);   // <-- String
            p.setStock(stock);

            productoDao.crear(p);

            response.sendRedirect(request.getContextPath() + "/MisProductos");
        } catch (Exception ex) {
            throw new ServletException(ex);
        }
    }
}
