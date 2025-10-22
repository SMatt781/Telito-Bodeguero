package com.example.telitobodeguero.servlets;

import com.example.telitobodeguero.beans.Movimiento;
import com.example.telitobodeguero.daos.MovimientoDao;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.ArrayList;

@WebServlet(name="InicioAlmacen", value = "/InicioAlmacenServlet")
public class InicioAlmacenServlet extends HttpServlet {
    private int getZonaId(HttpServletRequest req) {
        Object z = req.getSession().getAttribute("zonaIdActual");
        if (z instanceof Integer) return (Integer) z;
        try { return Integer.parseInt(String.valueOf(z)); } catch (Exception e) { return 1; }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
    }
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        //primero lo haremos por el id de la zona
        //YA NO VA HARDCODEO
        // int zonaId = 1; //por el momento
        //EN REEMPLAZO 14/10

        int zonaId = getZonaId(request);


        MovimientoDao movDao = new MovimientoDao();
        ArrayList<Movimiento> listaMovs = movDao.obtenerListaMovimientos(zonaId);
        int stockTotal = movDao.getStockTotal(zonaId);
        int inToday = movDao.getInToday(zonaId);
        int outToday = movDao.getOutToday(zonaId);
        int min = movDao.getMin(zonaId);
        request.setAttribute("listaMovs", listaMovs);
        request.setAttribute("stockTotal", stockTotal);
        request.setAttribute("inToday", inToday);
        request.setAttribute("outToday", outToday);
        request.setAttribute("min", min);
        RequestDispatcher view =  request.getRequestDispatcher("/Almacen/homeAlmacen.jsp");
        view.forward(request, response);

    }
}

