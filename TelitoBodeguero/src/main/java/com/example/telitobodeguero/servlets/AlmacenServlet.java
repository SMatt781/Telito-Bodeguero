package com.example.telitobodeguero.servlets;


import com.example.telitobodeguero.beans.Incidencia;
import com.example.telitobodeguero.daos.ProductoDao;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.example.telitobodeguero.beans.Movimiento;
import com.example.telitobodeguero.beans.Producto;
import com.example.telitobodeguero.daos.MovimientoDao;
import com.example.telitobodeguero.daos.ProductoDaoLogis;

import java.io.IOException;
import java.util.ArrayList;

@WebServlet(name = "AlmacenServlet",value = "/AlmacenServlet")
public class AlmacenServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {


        String accion = request.getParameter("accion");
        if ("mostrarRegistro".equals(accion)){
            //obtengo los parametros de la URL
            String tipo = request.getParameter("tipo");
            String sku = request.getParameter("sku"); //capturo el SKU de la tabla
            String lote = request.getParameter("lote");

            //paso los datos al request
            request.setAttribute("sku",sku);
            request.setAttribute("lote",lote);

            if ("entrada".equals(tipo)){
                //para el registro entrada
                request.getRequestDispatcher("registrarEntrada.jsp").forward(request,response);
            } else if ("salida".equals(tipo)) {
                //para el registro salida
                request.getRequestDispatcher("registrarSalida.jsp").forward(request,response);
            }
        }else if("mostrarIncidencia".equals(accion)){
            String sku = request.getParameter("sku");
            String lote = request.getParameter("lote");
            //paso los datos al request
            request.setAttribute("sku",sku);
            request.setAttribute("lote",lote);
            request.getRequestDispatcher("registrarIncidencia.jsp").forward(request,response);
        }else{
            response.setContentType("text/html");

            //creo la instancia y llamo al metodo
            ProductoDao productoDao = new ProductoDao();
            ArrayList<Producto> listaProductos = productoDao.obtenerProductos();

            //configuro datos a enviar
            request.setAttribute("listaProductos",listaProductos);
            //elijo el metodo de redireccion
            RequestDispatcher view = request.getRequestDispatcher("gestionAlmacen.jsp");
            view.forward(request,response);
        }

    }


    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String accion = request.getParameter("accion");


        boolean  exitoIn=false;
        boolean  exitoOut=false;
        //boolean exitoInc=false;
        //llamo al metodo Dao
        MovimientoDao movimientoDao = new MovimientoDao();
        if("registrarMovimiento".equals(accion)){
            //recibo datos
            String tipo  =request.getParameter("tipo");
            String fechaRegistro =  request.getParameter("fechaRegistro");
            String cantidadStr = (request.getParameter("cantidad"));
            int cantidad = 0;
            if (cantidadStr != null && !cantidadStr.trim().isEmpty()){
                cantidad = Integer.parseInt(cantidadStr);
            }
            String  loteStr = (request.getParameter("lote"));
            int lote = 0;
            if (loteStr != null && !loteStr.trim().isEmpty()){
                lote = Integer.parseInt(loteStr.trim());
            }
            String fechaVencimiento =  request.getParameter("fechaVencimiento");
            int idZona = Integer.parseInt(request.getParameter("idZona"));
            String sku = request.getParameter("sku");


            //lleno en bean de movimiento
            Movimiento movimiento  = new Movimiento();
            movimiento.setTipoMovimiento(tipo);
            movimiento.setCantidad(cantidad);
            //movimiento.setFecha(fechaRegistro);
            movimiento.setLote_idLote(lote);
            movimiento.setZonas_idZonas(idZona);
            if("IN".equals(tipo)){
                exitoIn = movimientoDao.registrarEntrada(movimiento, sku, fechaVencimiento,1);
                if(exitoIn){
                    response.sendRedirect(request.getContextPath()+"/AlmacenServlet?accion=verInvertario");
                }else{
                    request.setAttribute("error","Error al registrar la entrada. Verifique que el SKU y el Lote sean válidos.");
                    request.getRequestDispatcher("registrarEntrada.jsp").forward(request,response);

                }
            } else if ("OUT".equals(tipo)) {
                exitoOut = movimientoDao.registrarSalida(movimiento);
                if(exitoOut){
                    response.sendRedirect(request.getContextPath()+"/AlmacenServlet?accion=verInvertario");
                }else{
                    request.setAttribute("error","Error al registrar la salida. Verifique que el SKU y el Lote sean válidos.");
                    request.getRequestDispatcher("registrarSalida.jsp").forward(request,response);

                }
            }



        } else if ("registrarIncidencia".equals(accion)) {
            Incidencia inc =  new Incidencia();
            //recibo datos
            String tipoInc = request.getParameter("tipoInc");
            String cantidadIncStr = request.getParameter("cantidadInc");
            int cantidadInc = 0;
            if (cantidadIncStr != null && !cantidadIncStr.trim().isEmpty()){
                cantidadInc = Integer.parseInt(cantidadIncStr);
            }
            String descripcionInc = request.getParameter("descripcionInc");
            String loteIncStr = request.getParameter("lote");
            int loteInc=0;
            if(loteIncStr != null && !loteIncStr.trim().isEmpty()){
                loteInc = Integer.parseInt(loteIncStr);
            }

            //lleno en el bean de incidencia
            inc.setTipoIncidencia(tipoInc);
            inc.setCantidad(cantidadInc);
            inc.setDescripcion(descripcionInc);
            inc.setLote_idLote(loteInc);

            //llamo al metodo
            boolean exitoInc=movimientoDao.registrarIncidencia(inc);
            if(exitoInc){
                response.sendRedirect(request.getContextPath()+"/AlmacenServlet?accion=verInvertario");
            }else{
                request.setAttribute("error","Error al registrar la incidencia. Verifique que el SKU y el Lote sean válidos.");
                request.getRequestDispatcher("registrarIncidencia.jsp").forward(request,response);
            }






        }


    }

}
