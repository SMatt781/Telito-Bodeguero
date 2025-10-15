package com.example.telitobodeguero.servlets;

import com.example.telitobodeguero.beans.Alertas;
import com.example.telitobodeguero.daos.InicioAdminDao;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.ArrayList;

@WebServlet(name = "InicioAdminServlet", value = "/InicioAdminServlet")
public class InicioAdminServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        InicioAdminDao alertasDao = new InicioAdminDao();
        ArrayList<Alertas> listaAlertas = alertasDao.obtenerListaAlertas();
        request.setAttribute("lista", listaAlertas);
        request.setAttribute("cantidadAlertas", listaAlertas.size());
        //operación para el cuadrito de alertas obtenidas

        InicioAdminDao incidenciaDao = new InicioAdminDao();
        int cantidadIncidencias = incidenciaDao.obtenerCantidadIncidencias();
        request.setAttribute("cantidadReportes", cantidadIncidencias);

        InicioAdminDao gastoZonaDao = new InicioAdminDao();
        ArrayList<Object[]> listaGastos = gastoZonaDao.obtenerGastosPorZona();
        request.setAttribute("listaGastos", listaGastos);

        int usuariosActivos = gastoZonaDao.contarUsuariosActivos();
        request.setAttribute("usuariosActivos", usuariosActivos);

        RequestDispatcher view = request.getRequestDispatcher("/Admin_Inicio.jsp");
        view.forward(request, response);
    }
}
