package Servlets;

import beans.Producto;
import daos.ProductoDao;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.util.List;

@WebServlet(name = "ProductoServlet", urlPatterns = {"/MisProductos"})
public class ProductoServlet extends HttpServlet {

    private final ProductoDao productoDao = new ProductoDao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        int idProductor = 4; // TODO: tomar de sesión

        try {
            List<Producto> productosResumen = productoDao.listarPorProductor(idProductor);
            List<Producto> productosPrecios = productoDao.listarTodos();

            request.setAttribute("productos", productosResumen);      // Tabla 1
            request.setAttribute("productosPrecios", productosPrecios); // Tabla 2

            RequestDispatcher rd = request.getRequestDispatcher("/MisProductos.jsp");
            rd.forward(request, response);
        } catch (Exception ex) {
            throw new ServletException(ex);
        }
    }
}
