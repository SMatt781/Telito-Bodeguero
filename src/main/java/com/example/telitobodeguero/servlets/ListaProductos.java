package com.example.telitobodeguero.servlets;

import com.example.telitobodeguero.beans.Movimiento;
import com.example.telitobodeguero.beans.Producto;
import com.example.telitobodeguero.beans.Usuarios;
import com.example.telitobodeguero.daos.ProductoDao;
import com.example.telitobodeguero.daos.ProductoDaoLogis;
import com.example.telitobodeguero.utils.Auth;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.ArrayList;

@WebServlet(name = "ListaProductos", value = "/ListaProductos")
public class ListaProductos extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Configuración de codificación
        request.setCharacterEncoding("UTF-8");

        ProductoDaoLogis productoDao = new ProductoDaoLogis();

        Usuarios usuario =(Usuarios) request.getSession().getAttribute("usuarioLog");
        if (usuario == null) {
            response.sendRedirect(request.getContextPath() + "/index.jsp");
            return;
        }

        int rol = usuario.getRol().getIdRoles();
        int permisoAValidar;

        switch (rol) {
            case 1:
                permisoAValidar = 13;
                break;
            case 2:
                permisoAValidar = 8;
                break;
            default:
                response.sendError(HttpServletResponse.SC_FORBIDDEN);
                return;
        }
        if (!Auth.can(request, permisoAValidar)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }


        // Cambiamos proveedorFiltro por busquedaTermino
        //String busquedaTermino = request.getParameter("busqueda");
        //String ordenFiltro = request.getParameter("orden");

        // La firma del DAO debe haber sido actualizada para aceptar busquedaTermino
        ArrayList<Producto> listaProductos = productoDao.obtenerListaProductos();
        request.setAttribute("listaProductos", listaProductos);

        // Pasamos el término de búsqueda para que se mantenga en el campo input del JSP
        //request.setAttribute("busquedaTermino", busquedaTermino);
        //request.setAttribute("ordenFiltro", ordenFiltro);

        // 4. Redirigir al JSP de productos
        RequestDispatcher view = request.getRequestDispatcher("/Logistica/Productos.jsp");
        view.forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Por ahora lo dejamos vacío.
    }
}