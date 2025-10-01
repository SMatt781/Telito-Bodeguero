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
        int idProductor = 4; // TODO: leer de sesión
        System.out.println("[LoteServlet][GET] action=" + action);

        try {
            switch (action) {
                case "formCrear": {
                    // cargar lista de productos del productor para el combo
                    List<Producto> productos = productoDao.listarPorProductor(idProductor);
                    request.setAttribute("productos", productos);

                    // opcional: preselección si vino idProducto en querystring
                    String sidProd = request.getParameter("idProducto");
                    if (sidProd != null && !sidProd.isBlank()) {
                        request.setAttribute("idProducto", Integer.valueOf(sidProd));
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

                    // productos para mostrar el nombre / combo (lo dejamos deshabilitado en edición)
                    List<Producto> productos = productoDao.listarPorProductor(idProductor);

                    request.setAttribute("lote", l);
                    request.setAttribute("productos", productos);
                    request.setAttribute("idProducto", l.getProductoId());

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
        int idProductor = 4; // TODO: de sesión
        System.out.println("[LoteServlet][POST] action=" + action);

        try {
            if ("guardar".equals(action)) {
                String sidLote = request.getParameter("idLote"); // null para crear
                Integer idProducto = Integer.valueOf(request.getParameter("idProducto"));
                String ubicacion = request.getParameter("ubicacion");
                Integer cantidad  = Integer.valueOf(request.getParameter("cantidad"));
                String sFecha     = request.getParameter("fechaVencimiento");
                LocalDate fv = (sFecha == null || sFecha.isBlank()) ? null : LocalDate.parse(sFecha);

                Lote l = new Lote();
                l.setUsuarioId(idProductor);
                l.setProductoId(idProducto);
                l.setUbicacion(ubicacion);
                l.setCantidad(cantidad);
                l.setFechaVencimiento(fv);

                if (sidLote == null || sidLote.isBlank()) {
                    loteDao.crear(l);
                } else {
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
            } else {
                response.sendRedirect(request.getContextPath() + "/Lotes");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new ServletException(ex);
        }
    }
}
