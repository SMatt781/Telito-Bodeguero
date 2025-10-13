package com.example.telitobodeguero.servlets;

import com.example.telitobodeguero.beans.Usuarios;
import com.example.telitobodeguero.daos.ListaPermisosDao;
import com.example.telitobodeguero.daos.UsuariosDao;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Set;

@WebServlet(name="LoginServlet", value = "/LoginServlet")
public class LoginServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        String correo = request.getParameter("correo");
        String contrasenha = request.getParameter("contrasenha");

        UsuariosDao usuariosDao = new UsuariosDao();
        Usuarios usuario = usuariosDao.validarUsuario(correo, contrasenha);

        if (usuario != null) {
            // Guardar al usuario logueado en sesión
            request.getSession().setAttribute("usuarioLog", usuario);

            // Cargar permisos activos del rol
            ListaPermisosDao permisosDao = new ListaPermisosDao();
            Set<Integer> permisosActivos = permisosDao.permisosActivosDeRol(usuario.getRol().getIdRoles());

            request.getSession().setAttribute("permisosRol", permisosActivos);

            // Redirigir a la página principal
            int rol = usuario.getRol().getIdRoles();
            String homeUrl;
            switch (rol){
                case 1:
                    homeUrl = request.getContextPath()+"/Admin_Inicio.jsp";
                    break;      // Admin
                case 2:
                    homeUrl = request.getContextPath() + "/Bienvenidos";
                    break;  // Logística
                case 3:
                    homeUrl = request.getContextPath()+"/InicioAlmacenServlet";
                    break;    // Almacén
                case 4:
                    homeUrl = request.getContextPath()+"/Productor/indexProductor.jsp";
                    break;  // Productor
                default:
                    homeUrl = request.getContextPath()+"/index.jsp";
            }
            request.getSession().setAttribute("homeUrl", homeUrl);
            response.sendRedirect(homeUrl);
            return;
        } else {
            // Login fallido → volver al login
            request.setAttribute("loginError", "Usuario o contraseña incorrectos");
            RequestDispatcher rd = request.getRequestDispatcher("/index.jsp");
            rd.forward(request, response);
            return;
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Si ya está logueado, lo mandamos directamente
        Usuarios usuario = (Usuarios) request.getSession().getAttribute("usuarioLog");

        if (usuario != null) {
            int rol = usuario.getRol().getIdRoles();
            switch (rol) {
                case 1:
                    response.sendRedirect(request.getContextPath()+"/InicioAdminServlet");
                    return;      // Admin
                case 2:
                    response.sendRedirect(request.getContextPath()+"/Bienvenidos");
                    return;  // Logística
                case 3:
                    response.sendRedirect(request.getContextPath()+"/Almacen/homeAlmacen.jsp");
                    return;    // Almacén
                case 4:
                    response.sendRedirect(request.getContextPath()+"/Productor/indexProductor.jsp");
                    return;  // Productor
                default:
                    response.sendRedirect(request.getContextPath()+"/index.jsp");
                    return;
            }
        }
        // Sino, mostrar el formulario
        RequestDispatcher view = request.getRequestDispatcher("/index.jsp");
        view.forward(request, response);
    }
}
