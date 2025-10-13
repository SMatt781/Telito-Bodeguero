package com.example.telitobodeguero.servlets;

import com.example.telitobodeguero.beans.Alertas;
import com.example.telitobodeguero.daos.AlertasDao;
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

        AlertasDao alertasDao = new AlertasDao();
        ArrayList<Alertas> listaAlertas = alertasDao.obtenerListaAlertas();
        request.setAttribute("lista", listaAlertas);
        RequestDispatcher view = request.getRequestDispatcher("/Admin_Inicio.jsp");
        view.forward(request, response);
    }
}
