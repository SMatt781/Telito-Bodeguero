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
import java.util.List;

@WebServlet(name = "MisProductosServlet", urlPatterns = {"/MisProductos"})
public class ProductoServlet extends HttpServlet {

    private final ProductoDao productoDao = new ProductoDao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        int idProductor = 4; // TODO: reemplazar por el id de la sesión del usuario logueado

        try {
            // Tabla 1: visibles (con o sin lotes) pero stock total > 0
            List<Producto> productos = productoDao.listarVisiblesPorProductor(idProductor);

            // Tabla 2: precios sugeridos (todo el catálogo)
            List<Producto> productosPrecios = productoDao.listarTodos();

            request.setAttribute("productos", productos);
            request.setAttribute("productosPrecios", productosPrecios);

            RequestDispatcher rd = request.getRequestDispatcher("/Productor/MisProductos.jsp");
            rd.forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            throw new ServletException(e);
        }
    }
}

