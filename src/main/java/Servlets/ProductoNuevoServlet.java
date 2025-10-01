package Servlets;

import beans.Producto;
import daos.ProductoDao;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.math.BigDecimal;

@WebServlet(name = "ProductoNuevoServlet", urlPatterns = {"/ProductoNuevo"})
public class ProductoNuevoServlet extends HttpServlet {

    private final ProductoDao productoDao = new ProductoDao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Solo muestra el formulario vacío
        RequestDispatcher rd = request.getRequestDispatcher("/ProductoForm.jsp");
        rd.forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        try {
            String sku = request.getParameter("sku");
            String nombre = request.getParameter("nombre");
            BigDecimal precio = new BigDecimal(request.getParameter("precio"));
            int stock = Integer.parseInt(request.getParameter("stock"));

            Producto p = new Producto();
            p.setSku(sku);
            p.setNombre(nombre);
            p.setPrecio(precio);
            p.setStock(stock);

            productoDao.crear(p);

            response.sendRedirect(request.getContextPath() + "/MisProductos");
        } catch (Exception ex) {
            throw new ServletException(ex);
        }
    }
}

