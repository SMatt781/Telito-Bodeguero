<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ page import="java.util.Set" %>

<%
    String ctx  = request.getContextPath();
    String uri  = request.getRequestURI();
    Set<Integer> permisos = (Set<Integer>) session.getAttribute("permisosRol");
    if (permisos == null) permisos = java.util.Collections.emptySet();

    String homeUrl = (String) session.getAttribute("homeUrl");
    if (homeUrl == null || homeUrl.isEmpty()) {
        homeUrl = ctx + "/index.jsp";
    }
%>

<aside class="sidebar" id="sidebar">
    <div class="brand">
        <button class="toggle" id="btnToggle" aria-label="Alternar menú">&#9776;</button>
        <span class="h5 mb-0 text-label">Telito - Bodeguero</span>
    </div>
    <hr class="text-secondary my-2">

    <ul class="nav nav-pills flex-column px-2">

        <li class="nav-item mb-1">
            <a class="nav-link <%= uri.endsWith("Inicio.jsp") ? "active" : "" %>"
               href="<%= homeUrl %>">
                <span class="text-label">Inicio</span>
            </a>
        </li>

        <%-- Lista de usuarios (permiso 9) --%>
        <% if (permisos.contains(9)) { %>
        <li class="nav-item dropdown mb-1">
            <a class="nav-link dropdown-toggle" href="#" id="usuariosDropdown" data-bs-toggle="dropdown" aria-expanded="false">
                <span class="text-label">Gestión de usuarios y roles</span>
            </a>
            <ul class="dropdown-menu dropdown-menu-dark" aria-labelledby="usuariosDropdown">
                <li>
                    <a class="dropdown-item <%= uri.contains("/ListaUsuariosServlet") ? "active" : "" %>" href="<%= ctx %>/ListaUsuariosServlet">
                        <img src="images/image.png" alt="" width="20" height="20" class="me-2">Listado de usuarios
                    </a>
                </li>
                <li>
                    <a class="dropdown-item <%= uri.contains("/ListaUsuariosServlet") ? "active" : "" %>" href="<%= ctx %>/GestionPermisosServlet">
                        <img src="images/gestionPermisos.png" alt="" width="20" height="20" class="me-2">Gestión de permisos
                    </a>
                </li>
            </ul>
        </li>
        <% } %>


        <%-- Inventario (permiso 20, ejemplo) --%>
        <% if (permisos.contains(9)) { %>
        <li class="nav-item dropdown mb-1">
            <a class="nav-link dropdown-toggle" href="#" id="reportesDropdown" data-bs-toggle="dropdown" aria-expanded="false">
                <span class="text-label">Reportes Globales</span>
            </a>
            <ul class="dropdown-menu dropdown-menu-dark" aria-labelledby="reportesDropdown">
                <li>
                    <a class="dropdown-item <%= uri.contains("/ListaProductos") ? "active" : "" %>" href="<%= ctx %>/ListaProductos">
                        <img src="images/reporteInventarios.jpg" alt="" width="20" height="20" class="me-2">Reporte de inventarios
                    </a>
                </li>
                <li>
                    <a class="dropdown-item <%= uri.contains("/ReporteMovimientos") ? "active" : "" %>" href="<%= ctx %>/ReporteMovimientos">
                        <img src="images/reporteMovimientos.png" alt="" width="20" height="20" class="me-2">Reporte de movimientos
                    </a>
                </li>
                <li>
                    <a class="dropdown-item <%= uri.contains("/StockBajo_OrdenCompra") ? "active" : "" %>" href="<%= ctx %>/StockBajo_OrdenCompra?action=stock">
                        <img src="images/reporteVentas.png" alt="" width="20" height="20" class="me-2">Reporte de compras
                    </a>
                </li>
            </ul>
        </li>
        <% } %>

        <% if (permisos.contains(9)) { %>
        <li class="nav-item dropdown mb-1">
            <a class="nav-link dropdown-toggle" href="#" id="panelDropdown" data-bs-toggle="dropdown" aria-expanded="false">
                <span class="text-label">Panel de supervisión</span>
            </a>
            <ul class="dropdown-menu dropdown-menu-dark" aria-labelledby="panelDropdown">
                <li>
                    <a class="dropdown-item <%= uri.contains("/StockBajo_OrdenCompra") ? "active" : "" %>" href="/StockBajo_OrdenCompra">
                        <img src="images/estadisticas.png" alt="" width="20" height="20" class="me-2">Estadísticas
                    </a>
                </li>
                <li>
                    <a class="dropdown-item <%= uri.contains("/StockBajo_OrdenCompra") ? "active" : "" %>" href="/StockBajo_OrdenCompra">
                        <img src="images/alertas.png" alt="" width="20" height="20" class="me-2">Alertas y notificaciones
                    </a>
                </li>
            </ul>
        </li>
        <% } %>

        <!-- Cerrar sesión -->
        <li class="nav-item mt-2">
            <a class="nav-link" href="<%= ctx %>/index.jsp">
                <span class="text-label">Cerrar sesión</span>
            </a>
        </li>
    </ul>
</aside>
