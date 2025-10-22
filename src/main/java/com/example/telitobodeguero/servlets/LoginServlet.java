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
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.Set;

@WebServlet(name="LoginServlet", value="/LoginServlet")
public class LoginServlet extends HttpServlet {
    private final UsuariosDao usuariosDao = new UsuariosDao();
    private final ListaPermisosDao permisosDao = new ListaPermisosDao();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        String accion = request.getParameter("accion");
        if (accion == null) accion = "login";

        switch (accion) {
            case "logout":
                HttpSession ses = request.getSession(false);
                if (ses != null) ses.invalidate();               // cerrar sesión
                response.sendRedirect(request.getContextPath()+"/index.jsp");
                return;

            case "login":
            default:
                String correo = request.getParameter("correo");
                String contrasenha = request.getParameter("contrasenha");

                Usuarios usuario = usuariosDao.validarUsuario(correo, contrasenha); // SIN hash por ahora
                if (usuario != null) {
                    HttpSession s = request.getSession(true);
                    s.setAttribute("usuarioLog", usuario);
                    s.setAttribute("permisosRol", permisosDao.permisosActivosDeRol(usuario.getRol().getIdRoles()));
                    s.setMaxInactiveInterval(30 * 60); // 30 minutos

                    // Redirección por rol
                    int rol = usuario.getRol().getIdRoles();
                    String homeUrl = switch (rol) {
                        case 1 -> request.getContextPath()+"/Admin_Inicio.jsp";
                        case 2 -> request.getContextPath()+"/Bienvenidos";
                        case 3 -> request.getContextPath()+"/InicioAlmacenServlet";
                        case 4 -> request.getContextPath()+"/Productor/indexProductor.jsp";
                        default -> request.getContextPath()+"/index.jsp";
                    };
                    s.setAttribute("homeUrl", homeUrl);
                    response.sendRedirect(homeUrl);
                } else {
                    request.getSession(true).setAttribute("flash_error", "Credenciales inválidas o usuario inactivo.");
                    response.sendRedirect(request.getContextPath()+"/index.jsp"); // redirect + flash (mensaje único)
                }
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        // Si ya hay sesión, no muestres login: manda a su home
        Usuarios u = (Usuarios) request.getSession().getAttribute("usuarioLog");
        if (u != null) {
            int rol = u.getRol().getIdRoles();
            switch (rol) {
                case 1 -> response.sendRedirect(request.getContextPath()+"/Admin_Inicio.jsp");
                case 2 -> response.sendRedirect(request.getContextPath()+"/Bienvenidos");
                case 3 -> response.sendRedirect(request.getContextPath()+"/InicioAlmacenServlet");
                case 4 -> response.sendRedirect(request.getContextPath()+"/Productor/indexProductor.jsp");
                default -> response.sendRedirect(request.getContextPath()+"/index.jsp");
            }
            return;
        }
        request.getRequestDispatcher("/index.jsp").forward(request, response);
    }
}
