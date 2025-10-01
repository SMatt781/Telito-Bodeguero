package Servlets;

import beans.OrdenCompra;
import daos.OrdenCompraDao;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.util.List;

@WebServlet(name = "OrdenesCompraServlet", urlPatterns = {"/OrdenesCompra"})
public class OrdenesCompraServlet extends HttpServlet {

    private final OrdenCompraDao dao = new OrdenCompraDao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        int idProductor = 4; // TODO: tomar de sesión

        try {
            List<OrdenCompra> filas = dao.listarOCConItemsParaProductor(idProductor);
            request.setAttribute("filas", filas);
            RequestDispatcher rd = request.getRequestDispatcher("/OrdenesCompra.jsp");
            rd.forward(request, response);
        } catch (Exception ex) {
            throw new ServletException(ex);
        }
    }
}
