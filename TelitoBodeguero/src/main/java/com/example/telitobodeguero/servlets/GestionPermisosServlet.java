package com.example.telitobodeguero.servlets;

import com.example.telitobodeguero.beans.Permisos;
import com.example.telitobodeguero.beans.Roles_has_Permisos;
import com.example.telitobodeguero.daos.ListaPermisosDao;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.ArrayList;

@WebServlet(name="GestionPermisosServlet", value = "/GestionPermisosServlet")
public class GestionPermisosServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        ListaPermisosDao listaPermisosDao = new ListaPermisosDao();
        ArrayList<Roles_has_Permisos> listaPermisos = listaPermisosDao.obtenerListaPermisos();
        request.setAttribute("lista", listaPermisos);

        RequestDispatcher view = request.getRequestDispatcher("/Gestion/gestionPermisos.jsp");
        view.forward(request, response);
        }
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
            request.setCharacterEncoding("UTF-8");
        String action = request.getParameter("action");

        if ("toggle".equals(action)) {
            int rolId = Integer.parseInt(request.getParameter("rolId"));
            int permisoId = Integer.parseInt(request.getParameter("permisoId"));
            int estado = Integer.parseInt(request.getParameter("estado"));

            ListaPermisosDao dao = new ListaPermisosDao();
            dao.actualizarActivacion(rolId, permisoId, estado == 1);

            // Volvemos a la tabla
            response.sendRedirect(request.getContextPath() + "/GestionPermisosServlet");
            return;
        }

        // por si hay algun error
        response.sendRedirect(request.getContextPath() + "/GestionPermisosServlet");

        }
}


