<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ page import="java.util.Set" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="com.example.telitobodeguero.beans.Usuarios" %>

<%
    String ctx  = request.getContextPath();
    String uri  = request.getRequestURI();
    Set<Integer> permisos = (Set<Integer>) session.getAttribute("permisosRol");
    if (permisos == null) permisos = java.util.Collections.emptySet();

    String homeUrl = (String) session.getAttribute("homeUrl");
    if (homeUrl == null || homeUrl.isEmpty()) {
        homeUrl = ctx + "/index.jsp";
    }

    Integer roleId = null;
    Usuarios u = (Usuarios) session.getAttribute("usuarioLog");
    if (u != null && u.getRol() != null) {
        roleId = u.getRol().getIdRoles();
    }
%>
<style>
    .sidebar {
        display: flex;
        flex-direction: column;
    }
    .sidebar .nav {
        flex-grow: 1;
    }
</style>

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
        <% if ((permisos.contains(1) && roleId==1) || (permisos.contains(1) && roleId==2) || (permisos.contains(1) && roleId==3) || (permisos.contains(1) && roleId==4)) { %>
        <li class="nav-item dropdown mb-1">
            <a class="nav-link dropdown-toggle" href="#" id="usuariosDropdown" data-bs-toggle="dropdown" aria-expanded="false">
                <span class="text-label">Gestión de usuarios y roles</span>
            </a>
            <ul class="dropdown-menu dropdown-menu-dark" aria-labelledby="usuariosDropdown">
                <% if ((permisos.contains(2) && roleId==1) || (permisos.contains(2) && roleId==2) || (permisos.contains(2) && roleId==3) || (permisos.contains(2) && roleId==4)) { %>
                <li>
                    <a class="dropdown-item <%= uri.contains("/ListaUsuariosServlet") ? "active" : "" %>" href="<%= ctx %>/ListaUsuariosServlet">
                        <img src="images/image.png" alt="" width="20" height="20" class="me-2">Listado de usuarios
                    </a>
                </li>
                <% } %>
                <% if ((permisos.contains(6) && roleId==1)) { %>
                <li>
                    <a class="dropdown-item <%= uri.contains("/ListaUsuariosServlet") ? "active" : "" %>" href="<%= ctx %>/GestionPermisosServlet">
                        <img src="images/gestionPermisos.png" alt="" width="20" height="20" class="me-2">Gestión de permisos
                    </a>
                </li>
                <% } %>
            </ul>
        </li>
        <% } %>


        <%-- Inventario (permiso 20, ejemplo) --%>
        <% if ((permisos.contains(7) && roleId==1) || (permisos.contains(7) && roleId==2) || (permisos.contains(7) && roleId==3) || (permisos.contains(7) && roleId==4)) { %>
        <li class="nav-item dropdown mb-1">
            <a class="nav-link dropdown-toggle" href="#" id="reportesDropdown" data-bs-toggle="dropdown" aria-expanded="false">
                <span class="text-label">Reportes Globales</span>
            </a>
            <ul class="dropdown-menu dropdown-menu-dark" aria-labelledby="reportesDropdown">
                <% if ((permisos.contains(8) && roleId==1) || (permisos.contains(8) && roleId==2) || (permisos.contains(8) && roleId==3) || (permisos.contains(8) && roleId==4)) { %>
                <li>
                    <a class="dropdown-item <%= uri.contains("/ListaProductos") ? "active" : "" %>" href="<%= ctx %>/ListaProductos">
                        <img src="images/reporteInventarios.jpg" alt="" width="20" height="20" class="me-2">Reporte de inventarios
                    </a>
                </li>
                <% } %>
                <% if ((permisos.contains(9) && roleId==1) || (permisos.contains(9) && roleId==2) || (permisos.contains(9) && roleId==3) || (permisos.contains(9) && roleId==4)) { %>
                <li>
                    <a class="dropdown-item <%= uri.contains("/ReporteMovimientos") ? "active" : "" %>" href="<%= ctx %>/ReporteMovimientos">
                        <img src="images/reporteMovimientos.png" alt="" width="20" height="20" class="me-2">Reporte de movimientos
                    </a>
                </li>
                <% } %>
                <% if ((permisos.contains(10) && roleId==1) || (permisos.contains(10) && roleId==2) || (permisos.contains(10) && roleId==3) || (permisos.contains(10) && roleId==4)) { %>
                <li>
                    <a class="dropdown-item <%= uri.contains("/StockBajo_OrdenCompra") ? "active" : "" %>" href="<%= ctx %>/StockBajo_OrdenCompra?action=stock">
                        <img src="images/reporteVentas.png" alt="" width="20" height="20" class="me-2">Reporte de compras
                    </a>
                </li>
                <% } %>
            </ul>
        </li>
        <% } %>

        <% if ((permisos.contains(11) && roleId==1) || (permisos.contains(11) && roleId==2) || (permisos.contains(11) && roleId==3) || (permisos.contains(11) && roleId==4)) { %>
        <li class="nav-item dropdown mb-1">
            <a class="nav-link dropdown-toggle" href="#" id="panelDropdown" data-bs-toggle="dropdown" aria-expanded="false">
                <span class="text-label">Panel de supervisión</span>
            </a>
            <ul class="dropdown-menu dropdown-menu-dark" aria-labelledby="panelDropdown">
                <% if ((permisos.contains(12) && roleId==1) || (permisos.contains(12) && roleId==2) || (permisos.contains(12) && roleId==3) || (permisos.contains(12) && roleId==4)) { %>
                <li>
                    <a class="dropdown-item <%= uri.contains("/StockBajo_OrdenCompra") ? "active" : "" %>" href="/StockBajo_OrdenCompra">
                        <img src="images/estadisticas.png" alt="" width="20" height="20" class="me-2">Estadísticas
                    </a>
                </li>
                <% } %>
                <% if ((permisos.contains(13) && roleId==1) || (permisos.contains(13) && roleId==2) || (permisos.contains(13) && roleId==3) || (permisos.contains(13) && roleId==4)) { %>
                <li>
                    <a class="dropdown-item <%= uri.contains("/StockBajo_OrdenCompra") ? "active" : "" %>" href="/StockBajo_OrdenCompra">
                        <img src="images/alertas.png" alt="" width="20" height="20" class="me-2">Alertas y notificaciones
                    </a>
                </li>
                <% } %>
            </ul>
        </li>
        <% } %>

        <!-- Productor -->

        <% if ((permisos.contains(14) && roleId==1) || (permisos.contains(14) && roleId==2) || (permisos.contains(14) && roleId==3) || (permisos.contains(14) && roleId==4)) { %>
        <li class="nav-item mt-2">
            <a class="nav-link <%= uri.contains("/MisProductos") ? "active" : "" %>" href="<%= ctx %>/MisProductos">
                <span class="text-label">Mis productos</span>
            </a>
        </li>
        <% } %>
        <% if ((permisos.contains(15) && roleId==1) || (permisos.contains(15) && roleId==2) || (permisos.contains(15) && roleId==3) || (permisos.contains(15) && roleId==4)) { %>
        <li class="nav-item mt-2">
            <a class="nav-link <%= uri.contains("/Lotes") ? "active" : "" %>" href="<%= ctx %>/Lotes">
                <span class="text-label">Gestión de Lotes</span>
            </a>
        </li>
        <% } %>
        <% if ((permisos.contains(16) && roleId==1) || (permisos.contains(16) && roleId==2) || (permisos.contains(16) && roleId==3) || (permisos.contains(16) && roleId==4)) { %>
        <li class="nav-item mt-2">
            <a class="nav-link <%= uri.contains("/OrdenesCompra") ? "active" : "" %>" href="<%= ctx %>/OrdenesCompra">
                <span class="text-label">Órdenes de compras</span>
            </a>
        </li>
        <% } %>

        <!-- Almacén -->

        <% if ((permisos.contains(17) && roleId==1) || (permisos.contains(17) && roleId==2) || (permisos.contains(17) && roleId==3) || (permisos.contains(17) && roleId==4)) { %>
        <li>
            <a href="<%=request.getContextPath()%>/AlmacenServlet" class="nav-link text-white">
                <img src="<%=request.getContextPath()%>/Almacen/img/indexGestion2.png" width="25" height="25" class="me-2">
                <span class="sidebar-text">Gestion de inventarios</span>
            </a>
        </li>
        <% } %>

        <% if ((permisos.contains(18) && roleId==1) || (permisos.contains(18) && roleId==2) || (permisos.contains(18) && roleId==3) || (permisos.contains(18) && roleId==4)) { %>
        <li>
            <a href="<%=request.getContextPath()%>/cargaExcel" class="nav-link text-white">
                <img src="<%=request.getContextPath()%>/Almacen/img/indexCarga.png" width="25" height="25" class="me-2">
                <span class="sidebar-text">Carga masiva de datos</span>
            </a>
        </li>
        <% } %>

        <% if ((permisos.contains(19) && roleId==1) || (permisos.contains(19) && roleId==2) || (permisos.contains(19) && roleId==3) || (permisos.contains(19) && roleId==4)) { %>
        <li>
            <a href="<%=request.getContextPath()%>/IncidenciaAlmServlet" class="nav-link text-white">
                <img src="<%=request.getContextPath()%>/Almacen/img/incidencia.png" width="25" height="25" class="me-2">
                <span class="sidebar-text">Incidencias </span>
            </a>
        </li>
        <% } %>
    </ul>

        <!-- Cerrar sesión -->

            <li class="nav-item mt-auto m-4">
                <div class="dropdown">
                    <a href="#" class="d-flex align-items-center text-white text-decoration-none dropdown-toggle" id="dropdownUser1" data-bs-toggle="dropdown" aria-expanded="false">
                        <% if (roleId==1) { %>
                        <img src="<%=request.getContextPath()%>/images/SesionAdmin.jpg" alt="" width="32" height="32" class="rounded-circle me-2">
                        <% } %>
                        <% if (roleId==2) { %>
                        <img src="<%=request.getContextPath()%>/Logistica/images/sesionLogistica.jpg" alt="" width="32" height="32" class="rounded-circle me-2">
                        <% } %>
                        <% if (roleId==3) { %>
                        <img src="<%=request.getContextPath()%>/Almacen/img/indexUsuario.webp" alt="" width="32" height="32" class="rounded-circle me-2">
                        <% } %>
                        <% if (roleId==4) { %>
                        <img src="<%=request.getContextPath()%>/Productor/images/sesionProductor.webp" alt="" width="32" height="32" class="rounded-circle me-2">
                        <% } %>

                        <strong class="text-label">Usuario</strong>
                    </a>

                    <ul class="dropdown-menu dropdown-menu-dark text-small shadow" aria-labelledby="dropdownUser1">
                        <li><a class="dropdown-item" href="<%= ctx %>/index.jsp">Cerrar sesión</a></li>
                    </ul>
                </div>
            </li>


</aside>
