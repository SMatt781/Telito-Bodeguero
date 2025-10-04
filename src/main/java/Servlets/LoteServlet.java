package Servlets;

import beans.Lote;
import beans.Producto;
import daos.LoteDao;
import daos.ProductoDao;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
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

                    // Si viene idProducto en querystring, calcular disponible para mostrar/limitar
                    String sidProd = request.getParameter("idProducto");
                    if (sidProd != null && !sidProd.isBlank()) {
                        Integer idProducto = Integer.valueOf(sidProd);
                        request.setAttribute("idProducto", idProducto);

                        try {
                            int disponible = loteDao.stockDisponible(idProducto, idProductor);
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

                    // productos para mostrar el nombre / combo (puedes deshabilitar en el JSP si no quieres cambiar producto)
                    List<Producto> productos = productoDao.listarPorProductor(idProductor);

                    // disponible para edición = stockDisponible + cantidad actual del lote (máximo al que puedes subir)
                    int disponibleEdicion = 0;
                    try {
                        int disp = loteDao.stockDisponible(l.getProductoId(), idProductor);
                        int actual = loteDao.cantidadActualDelLote(l.getIdLote(), idProductor);
                        disponibleEdicion = disp + actual;
                    } catch (SQLException e) {
                        // si algo falla, al menos permite mantener el valor actual
                        disponibleEdicion = l.getCantidad();
                    }

                    request.setAttribute("lote", l);
                    request.setAttribute("productos", productos);
                    request.setAttribute("idProducto", l.getProductoId());
                    request.setAttribute("disponible", disponibleEdicion); // el JSP debe usar este 'max'
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
                    // volver a la lista (con o sin filtro, según parámetro)
                    String backProd = request.getParameter("idProducto");
                    response.sendRedirect(request.getContextPath() + "/Lotes" + (backProd != null ? ("?idProducto=" + backProd) : ""));
                    break;
                }
                case "lista":
                default: {
                    Integer idProducto = null;
                    String sidProd = request.getParameter("idProducto");
                    if (sidProd != null && !sidProd.isBlank()) idProducto = Integer.valueOf(sidProd);

                    List<Lote> lista = loteDao.listarPorProductor(idProductor, idProducto);
                    List<Producto> productos = productoDao.listarPorProductor(idProductor);

                    request.setAttribute("lista", lista);
                    request.setAttribute("productos", productos);
                    request.setAttribute("idProducto", idProducto);

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
                String sidLote   = request.getParameter("idLote"); // null para crear
                Integer idProducto = Integer.valueOf(request.getParameter("idProducto"));
                String ubicacion = request.getParameter("ubicacion");
                Integer cantidad = Integer.valueOf(request.getParameter("cantidad"));
                String sFecha    = request.getParameter("fechaVencimiento");
                LocalDate fv     = (sFecha == null || sFecha.isBlank()) ? null : LocalDate.parse(sFecha);

                Lote l = new Lote();
                l.setUsuarioId(idProductor);
                l.setProductoId(idProducto);
                l.setUbicacion(ubicacion);
                l.setCantidad(cantidad);
                l.setFechaVencimiento(fv);

                try {
                    if (sidLote == null || sidLote.isBlank()) {
                        // CREAR con validación en DAO
                        loteDao.crear(l);
                    } else {
                        // ACTUALIZAR con validación en DAO
                        l.setIdLote(Integer.valueOf(sidLote));
                        loteDao.actualizar(l);
                    }

                    // Si estabas filtrado, puedes volver filtrado:
                    String keepFilter = request.getParameter("keepFilter"); // "1" o null
                    if ("1".equals(keepFilter)) {
                        response.sendRedirect(request.getContextPath() + "/Lotes?idProducto=" + idProducto);
                    } else {
                        response.sendRedirect(request.getContextPath() + "/Lotes"); // lista completa
                    }
                    return;

                } catch (SQLException ex) {
                    // Si el DAO lanzó "stock insuficiente"
                    if ("45000".equals(ex.getSQLState())) {
                        // preparar form con error y disponible correcto para el max
                        int disponible;
                        try {
                            if (sidLote == null || sidLote.isBlank()) {
                                // crear → max = disponible
                                disponible = loteDao.stockDisponible(idProducto, idProductor);
                            } else {
                                // editar → max = disponible + cantidad actual
                                int disp = loteDao.stockDisponible(idProducto, idProductor);
                                int actual = loteDao.cantidadActualDelLote(Integer.parseInt(sidLote), idProductor);
                                disponible = disp + actual;
                            }
                        } catch (SQLException e2) {
                            disponible = 0;
                        }

                        // recargar combo de productos y atributos del form
                        List<Producto> productos = productoDao.listarPorProductor(idProductor);
                        request.setAttribute("productos", productos);
                        request.setAttribute("lote", l);
                        request.setAttribute("idProducto", idProducto);
                        request.setAttribute("disponible", disponible);
                        request.setAttribute("error", "Stock insuficiente. Disponible: " + disponible);

                        // si venías en edición, pásalo
                        if (sidLote != null && !sidLote.isBlank()) {
                            request.setAttribute("editMode", true);
                        }

                        RequestDispatcher rd = request.getRequestDispatcher("/LoteForm.jsp");
                        rd.forward(request, response);
                        return;
                    }
                    // otros errores SQL: propaga
                    throw ex;
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
