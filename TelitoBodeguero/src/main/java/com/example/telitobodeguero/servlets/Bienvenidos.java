package com.example.telitobodeguero.servlets;

import com.example.telitobodeguero.beans.Movimiento;
import com.example.telitobodeguero.daos.MovimientoDao;
import com.example.telitobodeguero.daos.MovimientoDaoLogis;
import com.example.telitobodeguero.daos.ProductoDaoLogis;
import com.example.telitobodeguero.daos.OrdenCompraDao; // 🚨 NUEVA IMPORTACIÓN PARA ÓRDENES DE COMPRA

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.ArrayList;

@WebServlet(name = "Bienvenidos", value = "/Bienvenidos")
public class Bienvenidos extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        MovimientoDaoLogis movDao = new MovimientoDaoLogis();
        ProductoDaoLogis prodDao = new ProductoDaoLogis();
        OrdenCompraDao ocDao = new OrdenCompraDao(); // 🚨 INSTANCIA DE ORDENCOMPRADAO

        // Lógica de Movimientos
        // ------------------------------------
        ArrayList<Movimiento> listaObtenidaDelDao = movDao.obtenerListaMovimientos();
        int totalMovimientos = movDao.contarTotalMovimientos();

        // Lógica de Alertas de Stock (ya implementada)
        // ----------------------------------------------------
        int alertasStockBajo = prodDao.contarTotalProductosStockBajo();

        // 🚨 NUEVA LÓGICA: Productos en Tránsito 🚨
        // ----------------------------------------

        int ordenesEnTransito = ocDao.contarOrdenesEnTransito();


        // 3. Pasar los datos a la vista (bienvenidos.jsp)
        // -----------------------------------------------
        // Movimientos
        request.setAttribute("reporteMovimientos", listaObtenidaDelDao);
        request.setAttribute("totalMovimientos", totalMovimientos);

        // Alertas de Stock
        request.setAttribute("alertasStockBajo", alertasStockBajo);

        // Productos en Tránsito
        request.setAttribute("ordenesEnTransito", ordenesEnTransito); // 🚨 PASAMOS EL CONTEO PARA EL JSP


        RequestDispatcher view = request.getRequestDispatcher("/Logistica/bienvenidos.jsp");
        view.forward(request, response);

    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    }
}