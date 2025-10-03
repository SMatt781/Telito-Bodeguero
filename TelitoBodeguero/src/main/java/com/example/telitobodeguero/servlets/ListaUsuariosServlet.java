package com.example.telitobodeguero.servlets;

import com.example.telitobodeguero.beans.Usuarios;
import com.example.telitobodeguero.daos.UsuariosDao;
import com.example.telitobodeguero.utils.Auth;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.net.Authenticator;
import java.util.ArrayList;

@WebServlet(name = "ListaUsuariosServlet", value="/ListaUsuariosServlet")
public class ListaUsuariosServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        String action = request.getParameter("action") == null ? "usuarios" : request.getParameter("action");
        UsuariosDao usuariosDao = new UsuariosDao();
        RequestDispatcher view;

        switch (action) {
            case "crear":
                boolean nuevo = false;
                String idParam = request.getParameter("id");
                if (idParam == null){
                    nuevo= true;
                }
                String nombre = request.getParameter("nombre");
                String apellido = request.getParameter("apellido");
                String correo = request.getParameter("correo");
                String contrasenha = request.getParameter("contrasenha");
                //Para validar rol y distrito
                String rolParam      = request.getParameter("rol_id");
                String distritoParam = request.getParameter("distrito_id");

                if (rolParam == null || rolParam.trim().isEmpty()
                        || distritoParam == null || distritoParam.trim().isEmpty()) {
                    // Manejo simple de error: vuelve al form con mensaje
                    request.setAttribute("error", "Debe seleccionar Rol y Distrito.");
                    request.getRequestDispatcher("/Gestion/ModificacionUsuarios/nuevoUser.jsp").forward(request, response);
                    return;
                }
                int rol_id = Integer.parseInt(rolParam);
                int distrito_id = Integer.parseInt(distritoParam);

                if(nuevo){
                    usuariosDao.crearUsuario(nombre,apellido,correo,distrito_id,contrasenha,rol_id);
                }else {
                    int idUsuario = Integer.parseInt(idParam);
                    usuariosDao.actualizarUsuario(idUsuario,nombre,apellido,correo,contrasenha,distrito_id,rol_id);
                }
                response.sendRedirect(request.getContextPath()+"/ListaUsuariosServlet");
                break;
        }
    }
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action=request.getParameter("action") == null ? "usuarios" : request.getParameter("action");
        UsuariosDao usuariosDao = new UsuariosDao();
        RequestDispatcher view;
        switch (action) {
            case "usuarios":
                if (!Auth.can(request, 9)) { // 9 = Ver usuarios
                    response.sendError(HttpServletResponse.SC_FORBIDDEN);
                    return;
                }
                ArrayList<Usuarios> listaUsuarios = usuariosDao.obtenerUsuarios();
                request.setAttribute("usuarios", listaUsuarios);
                view = request.getRequestDispatcher("/Gestion/ListaDeUsuarios.jsp");
                view.forward(request, response);
                break;
            case "formCrear":
                if (!Auth.can(request, 10)) { // Ejemplo: permiso "Crear Usuario"
                    response.sendError(HttpServletResponse.SC_FORBIDDEN);
                    return;
                }
                view= request.getRequestDispatcher("/Gestion/ModificacionUsuarios/nuevoUser.jsp");
                view.forward(request, response);
                break;
            case "editar":
                if (!Auth.can(request, 11)) { // Ejemplo: permiso para "Editar Usuario"
                    response.sendError(HttpServletResponse.SC_FORBIDDEN);
                    return;
                }
                int idUsuario = Integer.parseInt(request.getParameter("id"));
                Usuarios usuario= usuariosDao.buscarUsuario(idUsuario);
                if(usuario==null){
                    response.sendRedirect(request.getContextPath()+"/ListaUsuariosServlet");
                }else{
                    request.setAttribute("usuario", usuario);
                    view = request.getRequestDispatcher("/Gestion/ModificacionUsuarios/editarUser.jsp");
                    view.forward(request, response);
                }
                break;
            case "borrar":
                if (!Auth.can(request, 12)) { // Ejemplo: permiso "Editar Usuario"
                    response.sendError(HttpServletResponse.SC_FORBIDDEN);
                    return;
                }
                idUsuario= Integer.parseInt(request.getParameter("id"));;
                if(usuariosDao.buscarUsuario(idUsuario)!=null){
                    usuariosDao.borrarUsuario(idUsuario);
                }
                response.sendRedirect(request.getContextPath()+"/ListaUsuariosServlet");
                break;
        }


    }
}
