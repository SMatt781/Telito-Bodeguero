package com.example.telitobodeguero.servlets;

import com.example.telitobodeguero.beans.Producto;
import com.example.telitobodeguero.daos.ProductoDao;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;


import com.example.telitobodeguero.beans.Producto;
import com.example.telitobodeguero.daos.ProductoDao;
import com.example.telitobodeguero.beans.Usuarios;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet(name = "ProductoNuevoServlet", urlPatterns = {"/ProductoNuevo"})
public class ProductoNuevoServlet extends HttpServlet {

    private final ProductoDao productoDao = new ProductoDao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        Usuarios usuario = (Usuarios) session.getAttribute("usuarioLog");

        if (usuario == null) {
            response.sendRedirect(request.getContextPath() + "/LoginServlet");
            return;
        }

        request.getRequestDispatcher("/Productor/ProductoForm.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession();
        Usuarios usuario = (Usuarios) session.getAttribute("usuarioLog");

        if (usuario == null) {
            response.sendRedirect(request.getContextPath() + "/LoginServlet");
            return;
        }

        int idProductor = usuario.getIdUsuarios();

        String sku = request.getParameter("sku");
        String nombre = request.getParameter("nombre");
        String precioStr = request.getParameter("precio");
        String stockStr = request.getParameter("stock");

        int stock;
        String redireccionFallo = request.getContextPath() + "/ProductoNuevo?error=datos_invalidos"; // Redirige al doGet de este mismo servlet


        try {
            if (sku == null || sku.trim().isEmpty() || nombre == null || nombre.trim().isEmpty() ||
                    precioStr == null || precioStr.trim().isEmpty() || stockStr == null || stockStr.trim().isEmpty()) {
                throw new IllegalArgumentException("Campos requeridos faltantes.");
            }
            stock = Integer.parseInt(stockStr); // Lanza NumberFormatException (subclase de IllegalArgumentException)

        } catch (IllegalArgumentException e) {
            System.err.println("Error de validación de datos: " + e.getMessage());
            response.sendRedirect(redireccionFallo);
            return;
        }

        Producto nuevoProducto = new Producto();
        nuevoProducto.setSku(sku.trim());
        nuevoProducto.setNombre(nombre.trim());
        nuevoProducto.setPrecio(Double.parseDouble(precioStr));
        nuevoProducto.setStock(stock);

        try {
            // Este método ya no fallará si usas la corrección de ProductoDao (Opción 2)
            productoDao.crear(nuevoProducto, idProductor);

            // 🚀 REDIRECCIÓN DE ÉXITO 🚀
            response.sendRedirect(request.getContextPath() + "/MisProductos");

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error al guardar producto en la BD: " + e.getMessage());

            // Redirige al doGet de este servlet para mostrar el formulario con el error
            response.sendRedirect(redireccionFallo + "_bd");
        }
    }
}