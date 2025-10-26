package com.example.telitobodeguero.servlets;

import com.example.telitobodeguero.beans.OrdenCompra;
import com.example.telitobodeguero.beans.Usuarios; // Necesitas importar el bean Usuarios
import com.example.telitobodeguero.daos.OrdenCompraDao;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession; // Necesitas importar HttpSession

import java.io.IOException;
import java.util.List;

@WebServlet(name = "OrdenesCompraServlet", urlPatterns = {"/OrdenesCompra"})
public class OrdenesCompraServlet extends HttpServlet {

    private final OrdenCompraDao dao = new OrdenCompraDao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        Usuarios usuarioLog = (Usuarios) session.getAttribute("usuarioLog");

        // 1. VALIDACIÓN DE SESIÓN (SEGURIDAD) 🛑
        if (usuarioLog == null || usuarioLog.getRol().getIdRoles() != 4) { // Asume que ID 4 es el rol de Productor
            // Si no hay sesión o el rol no es Productor, redirige al login
            response.sendRedirect(request.getContextPath() + "/LoginServlet");
            return;
        }

        // 2. OBTENER ID DEL PRODUCTOR DE LA SESIÓN ✅
        int idProductor = usuarioLog.getIdUsuarios();

        try {
            // 3. LLAMADA AL DAO CON EL ID CORREGIDO
            List<OrdenCompra> filas = dao.listarOCConItemsParaProductor(idProductor);

            // 4. PREPARAR VISTA
            request.setAttribute("filas", filas);
            RequestDispatcher rd = request.getRequestDispatcher("/Productor/OrdenesCompra.jsp");
            rd.forward(request, response);

        } catch (Exception ex) {
            // Manejo de errores de base de datos o DAO
            System.err.println("Error al listar órdenes de compra para el productor: " + ex.getMessage());

            // Opcional: Redirigir a una página de error o mostrar un mensaje
            session.setAttribute("error", "Error al cargar las órdenes de compra.");
            response.sendRedirect(request.getContextPath() + "/InicioProductor");
            // Nota: Si quieres mantener el RequestDispatcher original, podrías hacerlo después de guardar el error
            // throw new ServletException(ex);
        }
    }
}