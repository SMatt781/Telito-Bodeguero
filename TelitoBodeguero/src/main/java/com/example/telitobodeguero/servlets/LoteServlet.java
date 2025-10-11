package com.example.telitobodeguero.servlets;

import com.example.telitobodeguero.beans.Lote;
import com.example.telitobodeguero.beans.Producto;
import com.example.telitobodeguero.daos.LoteDao;
import com.example.telitobodeguero.daos.ProductoDao;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet(name = "LoteServlet", urlPatterns = {"/Lotes"})
public class LoteServlet extends HttpServlet {

    private final LoteDao loteDao = new LoteDao();
    private final ProductoDao productoDao = new ProductoDao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action") == null ? "lista" : request.getParameter("action");
        int idProductor = 4; // TODO: leer de sesión real
        System.out.println("[LoteServlet][GET] action=" + action);

        try {
            switch (action) {
                case "formCrear": {
                    // Cargar productos para el combo
                    List<Producto> productos = productoDao.listarPorProductor(idProductor);
                    request.setAttribute("productos", productos);

                    // Si viene idProducto en querystring, calcular disponible
                    String idProductoStr = request.getParameter("idProducto");
                    if (idProductoStr != null && !idProductoStr.isBlank()) {
                        request.setAttribute("idProducto", idProductoStr);
                        try {
                            int disponible = loteDao.stockDisponible(idProductoStr, idProductor);
                            request.setAttribute("disponible", disponible);
                        } catch (SQLException e) {
                            request.setAttribute("disponible", 0);
                        }
                    }

                    RequestDispatcher rd = request.getRequestDispatcher("/LoteForm.jsp");
                    rd.forward(request, response);
                    break;
                }
                case "editar": {
                    String sid = request.getParameter("id");
                    if (sid == null) {
                        response.sendRedirect(request.getContextPath() + "/Lotes");
                        return;
                    }
                    Lote l = loteDao.obtenerPorIdYProductor(Integer.parseInt(sid), idProductor);
                    if (l == null) {
                        response.sendRedirect(request.getContextPath() + "/Lotes");
                        return;
                    }

                    List<Producto> productos = productoDao.listarPorProductor(idProductor);

                    int disponibleEdicion;
                    try {
                        int disp = loteDao.stockDisponible(l.getProducto_idProducto(), idProductor);
                        int actual = loteDao.cantidadActualDelLote(l.getIdLote(), idProductor);
                        disponibleEdicion = disp + actual;
                    } catch (SQLException e) {
                        disponibleEdicion = l.getCantidad();
                    }

                    request.setAttribute("lote", l);
                    request.setAttribute("productos", productos);
                    request.setAttribute("idProducto", l.getProducto_idProducto()); // String
                    request.setAttribute("disponible", disponibleEdicion);
                    request.setAttribute("editMode", true);

                    RequestDispatcher rd = request.getRequestDispatcher("/LoteForm.jsp");
                    rd.forward(request, response);
                    break;
                }
                case "borrar": {
                    String sid = request.getParameter("id");
                    if (sid != null) {
                        loteDao.borrar(Integer.parseInt(sid), idProductor);
                    }
                    String backProd = request.getParameter("idProducto");
                    response.sendRedirect(request.getContextPath() + "/Lotes" + (backProd != null ? ("?idProducto=" + backProd) : ""));
                    break;
                }
                case "lista":
                default: {
                    String idProductoStr = request.getParameter("idProducto");
                    List<Lote> lista = loteDao.listarPorProductor(idProductor,
                            (idProductoStr == null || idProductoStr.isBlank()) ? null : idProductoStr);
                    List<Producto> productos = productoDao.listarPorProductor(idProductor);

                    request.setAttribute("lista", lista);
                    request.setAttribute("productos", productos);
                    request.setAttribute("idProducto", idProductoStr);

                    RequestDispatcher rd = request.getRequestDispatcher("/GestionLotes.jsp");
                    rd.forward(request, response);
                    break;
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new ServletException(ex);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        String action = request.getParameter("action") == null ? "guardar" : request.getParameter("action");
        int idProductor = 4; // TODO: de sesión real
        System.out.println("[LoteServlet][POST] action=" + action);

        try {
            if ("guardar".equals(action)) {
                String sidLote        = request.getParameter("idLote"); // null para crear
                String idProductoStr  = request.getParameter("idProducto"); // ahora String
                String ubicacion      = request.getParameter("ubicacion");
                Integer cantidad      = Integer.valueOf(request.getParameter("cantidad"));
                String sFecha         = request.getParameter("fechaVencimiento"); // String "yyyy-MM-dd" o vacío

                Lote l = new Lote();
                l.setUsuarios_idUsuarios(idProductor);
                l.setProducto_idProducto(idProductoStr);
                l.setUbicacion(ubicacion);
                l.setCantidad(cantidad);
                l.setFechaVencimiento((sFecha == null || sFecha.isBlank()) ? null : sFecha);

                try {
                    if (sidLote == null || sidLote.isBlank()) {
                        // CREAR con validación en DAO
                        loteDao.crear(l);
                    } else {
                        // ACTUALIZAR con validación en DAO
                        l.setIdLote(Integer.parseInt(sidLote));
                        loteDao.actualizar(l);
                    }

                    String keepFilter = request.getParameter("keepFilter"); // "1" o null
                    if ("1".equals(keepFilter) && idProductoStr != null && !idProductoStr.isBlank()) {
                        response.sendRedirect(request.getContextPath() + "/Lotes?idProducto=" + idProductoStr);
                    } else {
                        response.sendRedirect(request.getContextPath() + "/Lotes");
                    }
                    return;

                } catch (SQLException ex) {
                    if ("45000".equals(ex.getSQLState())) {
                        // stock insuficiente → recalcular disponible correcto
                        int disponible;
                        try {
                            if (sidLote == null || sidLote.isBlank()) {
                                disponible = loteDao.stockDisponible(idProductoStr, idProductor);
                            } else {
                                int disp = loteDao.stockDisponible(idProductoStr, idProductor);
                                int actual = loteDao.cantidadActualDelLote(Integer.parseInt(sidLote), idProductor);
                                disponible = disp + actual;
                            }
                        } catch (SQLException e2) {
                            disponible = 0;
                        }

                        List<Producto> productos = productoDao.listarPorProductor(idProductor);
                        request.setAttribute("productos", productos);
                        request.setAttribute("lote", l);
                        request.setAttribute("idProducto", idProductoStr);
                        request.setAttribute("disponible", disponible);
                        request.setAttribute("error", "Stock insuficiente. Disponible: " + disponible);

                        if (sidLote != null && !sidLote.isBlank()) {
                            request.setAttribute("editMode", true);
                        }

                        RequestDispatcher rd = request.getRequestDispatcher("/LoteForm.jsp");
                        rd.forward(request, response);
                        return;
                    }
                    throw ex; // otros SQL
                }

            } else {
                response.sendRedirect(request.getContextPath() + "/Lotes");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new ServletException(ex);
        }
    }
}
