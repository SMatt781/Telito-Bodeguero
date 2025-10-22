package com.example.telitobodeguero.servlets;

import com.example.telitobodeguero.beans.Movimiento;
import com.example.telitobodeguero.daos.MovimientoDao;
import com.example.telitobodeguero.daos.MovimientoDaoLogis;
import com.example.telitobodeguero.utils.Auth;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.rmi.ServerException;
import java.util.ArrayList;

@WebServlet(name = "ReporteMovimientos", value = "/ReporteMovimientos")
public class ReporteMovimientos extends HttpServlet {
    private final MovimientoDaoLogis movDao = new MovimientoDaoLogis();
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        String action = request.getParameter("action");
        if (action == null || action.equals("list")) {
            // filtros
            String fechaDesde = request.getParameter("fechaDesde");
            String fechaHasta = request.getParameter("fechaHasta");
            String movimiento = request.getParameter("movimiento");
            String producto = request.getParameter("producto");
            String zona      = request.getParameter("zona");

            // 🚨 NUEVA LÓGICA: OBTENER CONTEOS DE ENTRADAS Y SALIDAS 🚨
            // Estos conteos NO se ven afectados por los filtros, son el total general.
            int totalEntradas = movDao.contarMovimientosPorTipo("IN");
            int totalSalidas  = movDao.contarMovimientosPorTipo("OUT");

            ArrayList<Movimiento> listaReporte = movDao.obtenerReporteFiltrado(
                    fechaDesde, fechaHasta, movimiento, producto, zona
            );

            request.setAttribute("listaMovimientos", listaReporte);

            // 🚨 PASAR CONTEOS AL JSP 🚨
            request.setAttribute("totalEntradas", totalEntradas);
            request.setAttribute("totalSalidas", totalSalidas);

            request.setAttribute("fechaDesde", fechaDesde);
            request.setAttribute("fechaHasta", fechaHasta);
            request.setAttribute("movimiento", movimiento);
            request.setAttribute("producto", producto);
            request.setAttribute("zona", zona);

            RequestDispatcher view = request.getRequestDispatcher("/Logistica/reporteMovimientos.jsp");
            view.forward(request, response);
            return;
        }

        // Si quisieras más acciones (exportar, etc.) las manejas aquí...
    }


    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Se deja vacío ya que la búsqueda (el formulario de filtros) usa GET
    }

}