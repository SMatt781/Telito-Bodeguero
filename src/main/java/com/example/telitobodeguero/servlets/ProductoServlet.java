package com.example.telitobodeguero.servlets;

import com.example.telitobodeguero.beans.Producto;
import com.example.telitobodeguero.daos.ProductoDao;
// Importa el bean de Usuarios (Asegúrate de que esta ruta es correcta)
import com.example.telitobodeguero.beans.Usuarios;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession; // Importación necesaria

import java.io.IOException;
import java.util.List;

@WebServlet(name = "MisProductosServlet", urlPatterns = {"/MisProductos"})
public class ProductoServlet extends HttpServlet {

    private final ProductoDao productoDao = new ProductoDao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // 🛑 CORRECCIÓN CLAVE: Obtener el ID del productor de la SESIÓN
        HttpSession session = request.getSession();
        Usuarios usuario = (Usuarios) session.getAttribute("usuarioLog");

        if (usuario == null) {
            // Manejar caso donde el usuario no ha iniciado sesión (ej: redirigir a login)
            response.sendRedirect(request.getContextPath() + "/LoginServlet");
            return;
        }

        int idProductor = usuario.getIdUsuarios(); // ⬅️ ¡Aquí se obtiene el ID real!

        try {
            // Tabla 1: visibles (con lotes) -> Lógica basada en Lote, por tu diseño SQL.
            List<Producto> productos = productoDao.listarVisiblesPorProductor(idProductor);

            // Tabla 2: precios sugeridos (todo el catálogo)
            List<Producto> productosPrecios = productoDao.listarTodos();

            request.setAttribute("productos", productos);
            request.setAttribute("productosPrecios", productosPrecios);

            RequestDispatcher rd = request.getRequestDispatcher("/Productor/MisProductos.jsp");
            rd.forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            // Lanza el error para que Tomcat lo muestre
            throw new ServletException("Error interno al cargar la lista de productos: " + e.getMessage(), e);
        }
    }
    // ... otros métodos doGet/doPost
}
