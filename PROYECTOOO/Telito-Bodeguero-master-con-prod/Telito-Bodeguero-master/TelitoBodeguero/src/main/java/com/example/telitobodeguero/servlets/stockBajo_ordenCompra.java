package com.example.telitobodeguero.servlets;

import com.example.telitobodeguero.beans.OrdenCompra;
import com.example.telitobodeguero.beans.Producto;
import com.example.telitobodeguero.beans.Usuarios;
import com.example.telitobodeguero.beans.Zonas;
import com.example.telitobodeguero.daos.OrdenCompraDao;
import com.example.telitobodeguero.daos.ProductoDao;
import com.example.telitobodeguero.daos.ProductoDaoLogis;
import com.example.telitobodeguero.utils.Auth;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.ArrayList;

@WebServlet(name = "StockBajo_OrdenCompra", value = "/StockBajo_OrdenCompra")
public class stockBajo_ordenCompra extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        String action = request.getParameter("action") == null ? "list" : request.getParameter("action");
        OrdenCompraDao ocDao = new OrdenCompraDao();

        switch (action) {

            case "form_crear" -> {

                // Lógica para cargar productos (se mantiene igual)
                ArrayList<Producto> listaProductos = ocDao.obtenerProductos();
                request.setAttribute("listaProductos", listaProductos);

                // 🚨 NUEVA LÓGICA: Cargar Zonas para el dropdown
                ArrayList<Zonas> listaZonas = ocDao.obtenerListaZonas();
                request.setAttribute("listaZonas", listaZonas);

                RequestDispatcher view = request.getRequestDispatcher("/Logistica/generarOrden.jsp");
                view.forward(request, response);
            }

            case "list" -> {
                // ===================================================
                //  1. LÓGICA DE PRODUCTOS STOCK BAJO (FINAL)
                // ===================================================
                ProductoDaoLogis prodDao = new ProductoDaoLogis();

                // Cargar los 5 productos con menor stock (con Lote/Zona por subconsulta)
                ArrayList<Producto> listaTop5StockBajo = prodDao.obtenerTop5ProductosStockBajo();

                // Cargar el total de productos que cumplen la condición de stock bajo
                int totalStockBajo = prodDao.contarTotalProductosStockBajo();

                // Pasar AMBOS datos al JSP
                request.setAttribute("listaTop5StockBajo", listaTop5StockBajo);
                request.setAttribute("totalStockBajo", totalStockBajo);

                // ===================================================
                // 2. LÓGICA DE ÓRDENES DE COMPRA (EXISTENTE)
                // ===================================================

                // 1. Capturar el filtro por ESTADO (se mantiene)
                String estadoFiltro = request.getParameter("estado");
                if (estadoFiltro != null) estadoFiltro = estadoFiltro.trim();

                // 2. Capturar el filtro por PROVEEDOR (se mantiene)
                String terminoBusquedaProveedor = request.getParameter("busquedaProveedor");
                if (terminoBusquedaProveedor != null) terminoBusquedaProveedor = terminoBusquedaProveedor.trim();


                // 3. Llamar al DAO con AMBOS filtros (se mantiene)
                ArrayList<OrdenCompra> listaOrdenCompra = ocDao.obtenerOrdenCompra(
                        (estadoFiltro != null && !estadoFiltro.isBlank()) ? estadoFiltro : null,
                        (terminoBusquedaProveedor != null && !terminoBusquedaProveedor.isBlank()) ? terminoBusquedaProveedor : null
                );

                // 4. Pasar la lista y los términos de búsqueda al JSP (se mantiene)
                request.setAttribute("listaOrdenCompra", listaOrdenCompra);
                request.setAttribute("estadoFiltro", estadoFiltro);
                request.setAttribute("terminoBusquedaProveedor", terminoBusquedaProveedor);

                RequestDispatcher view = request.getRequestDispatcher("/Logistica/stockBajo_oc.jsp");
                view.forward(request, response);
            }

            default -> response.sendRedirect(request.getContextPath() + "/StockBajo_OrdenCompra?action=list");
        }

    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        String action = request.getParameter("action");
        OrdenCompraDao ocDao = new OrdenCompraDao(); // Instancia el DAO aquí

        switch (action) {

            // 🛠️ ACTUALIZACIÓN: Maneja la creación de la nueva orden
            case "crear" -> {

                String productoIdStr = request.getParameter("productoId");
                String cantidadStr = request.getParameter("cantidad");
                String fechaLlegadaStr = request.getParameter("fechaLlegada");
                String zonaIdStr = request.getParameter("zonaId"); // 🚨 NUEVO: Capturamos el ID de la zona

                try {
                    int productoId = Integer.parseInt(productoIdStr);
                    int cantidad = Integer.parseInt(cantidadStr);
                    int idZona = Integer.parseInt(zonaIdStr); // 🚨 NUEVO: Convertimos a int

                    // 🚨 CAMBIO CRÍTICO: La llamada al DAO ahora incluye 4 parámetros
                    ocDao.crearOrden(productoId, cantidad, fechaLlegadaStr, idZona);

                    // Si la creación es exitosa, redirige al listado
                    response.sendRedirect(request.getContextPath() + "/StockBajo_OrdenCompra?action=list");
                    return;

                } catch (NumberFormatException e) {
                    System.err.println("Error de formato al crear orden: ID, Cantidad o Zona no numéricos. " + e.getMessage());
                    response.sendRedirect(request.getContextPath() + "/StockBajo_OrdenCompra?action=form_crear&error=formato");
                    return;
                } catch (RuntimeException e) {
                    System.err.println("Error crítico al crear orden: " + e.getMessage());
                    e.printStackTrace();
                    // Redirige al formulario con un error
                    response.sendRedirect(request.getContextPath() + "/StockBajo_OrdenCompra?action=form_crear&error=db_fail");
                    return;
                }
            }

            // Lógica de DELETE que migraste al doPost
            case "delete" -> {

                String idOrdenStr = request.getParameter("id");

                if (idOrdenStr != null && idOrdenStr.matches("\\d+")) {
                    try {
                        int idOrden = Integer.parseInt(idOrdenStr);
                        ocDao.borrarOrden(idOrden);
                    } catch (NumberFormatException e) {
                        System.err.println("Error: ID de orden no numérico. Recibido: " + idOrdenStr);
                    }
                } else {
                    System.err.println("Intento de DELETE sin ID válido. Recibido: " + idOrdenStr);
                }

                // PRG: vuelve al listado
                response.sendRedirect(request.getContextPath() + "/StockBajo_OrdenCompra?action=list");
                return;
            }

            // Si la acción no se reconoce, regresa al listado
            default -> response.sendRedirect(request.getContextPath() + "/StockBajo_OrdenCompra?action=list");
        }
    }
}
