package com.example.telitobodeguero.servlets;

import com.example.telitobodeguero.beans.Producto;
import com.example.telitobodeguero.beans.Usuarios;
import com.example.telitobodeguero.daos.ProductoDao;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet(name = "ProductoServlet", urlPatterns = {"/MisProductos"})
public class ProductoServlet extends HttpServlet {

    private final ProductoDao productoDao = new ProductoDao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // 1. Obtención y Validación del Usuario Logueado
        HttpSession session = request.getSession();
        Usuarios usuario = (Usuarios) session.getAttribute("usuarioLog");

        if (usuario == null) {
            response.sendRedirect(request.getContextPath() + "/LoginServlet");
            return;
        }

        int idProductor = usuario.getIdUsuarios();

        try {
            // 2. Carga de Datos: UNA SOLA VEZ, FILTRADA POR EL PRODUCTOR
            // Esta lista solo contiene los productos que tiene este productor.
            List<Producto> misProductosFiltrados = productoDao.listarVisiblesPorProductor(idProductor);

            // 3. Asignación de Atributos al Request

            // TABLA 1: Mis productos y Stock
            request.setAttribute("productos", misProductosFiltrados);

            // TABLA 2: Precios Sugeridos (Reutiliza la lista filtrada, SOLUCIONANDO EL PROBLEMA)
            request.setAttribute("productosPrecios", misProductosFiltrados);

            // 4. Despachar al JSP
            RequestDispatcher rd = request.getRequestDispatcher("/Productor/MisProductos.jsp");
            rd.forward(request, response);

        } catch (SQLException e) {
            e.printStackTrace();
            // Manejo de errores de base de datos
            request.setAttribute("error", "Error al cargar productos: " + e.getMessage());
            RequestDispatcher rd = request.getRequestDispatcher("/error.jsp");
            rd.forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            // Manejo de otros errores
            throw new ServletException("Error interno al cargar la lista de productos: " + e.getMessage(), e);
        }
    }

    // Puedes añadir el método doPost aquí si ProductoPrecioServlet no existe.
    // Como ProductoPrecioServlet existe, este ProductoServlet solo maneja el GET.
}