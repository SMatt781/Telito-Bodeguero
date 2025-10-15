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
    .nav-category {
        padding: 0.5rem 0.75rem;
        font-size: 0.8rem;
        font-weight: 700;
        color: #adb5bd; /* Un gris claro para el título */
        text-transform: uppercase;
        display: block;
        margin-top: 0.5rem;
    }
    /* Ajuste para los enlaces anidados */
    .nav .nav-link {
        color: #d6d6d6;
        transition: background-color 0.2s, color 0.2s;
    }
    .nav .nav-link:hover, .nav .nav-link.active {
        background-color: #0d6efd;
        color: #fff;
    }
    .nav .nav .nav-link { /* Estilo específico para sub-items */
        font-size: 0.9em;
        padding-left: 2rem; /* Mayor indentación para sub-items */
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
        <li class="nav-item">
            <span class="nav-category text-label">Gestión y Roles</span>
            <ul class="nav flex-column">
                <% if ((permisos.contains(2) && roleId==1) || (permisos.contains(2) && roleId==2) || (permisos.contains(2) && roleId==3) || (permisos.contains(2) && roleId==4)) { %>
                <li class="nav-item">
                    <a class="nav-link <%= uri.contains("/ListaUsuariosServlet") ? "active" : "" %>" href="<%= ctx %>/ListaUsuariosServlet">
                        <img src="images/image.png" alt="" width="20" height="20" class="me-2">
                        <span class="text-label">Listado de usuarios</span>
                    </a>
                </li>
                <% } %>
                <% if ((permisos.contains(6) && roleId==1)) { %>
                <li class="nav-item">
                    <a class="nav-link <%= uri.contains("/GestionPermisosServlet") ? "active" : "" %>" href="<%= ctx %>/GestionPermisosServlet">
                        <img src="images/gestionPermisos.png" alt="" width="20" height="20" class="me-2">
                        <span class="text-label">Gestión de permisos</span>
                    </a>
                </li>
                <% } %>
            </ul>
        </li>
        <% } %>


        <%-- Inventario (permiso 20, ejemplo) --%>
        <% if ((permisos.contains(7) && roleId==1) || (permisos.contains(7) && roleId==2) || (permisos.contains(7) && roleId==3) || (permisos.contains(7) && roleId==4)) { %>
        <li class="nav-item">
            <span class="nav-category text-label">Reportes Globales</span>
            <ul class="nav flex-column">
                <% if ((permisos.contains(8) && roleId==1) || (permisos.contains(8) && roleId==2) || (permisos.contains(8) && roleId==3) || (permisos.contains(8) && roleId==4)) { %>
                <li class="nav-item">
                    <a class="nav-link <%= uri.contains("/ListaProductos") ? "active" : "" %>" href="<%= ctx %>/ListaProductos">
                        <img src="images/reporteInventarios.jpg" alt="" width="20" height="20" class="me-2">
                        <span class="text-label">Reporte de inventarios</span>
                    </a>
                </li>
                <% } %>
                <% if ((permisos.contains(9) && roleId==1) || (permisos.contains(9) && roleId==2) || (permisos.contains(9) && roleId==3) || (permisos.contains(9) && roleId==4)) { %>
                <li class="nav-item">
                    <a class="nav-link <%= uri.contains("/ReporteMovimientos") ? "active" : "" %>" href="<%= ctx %>/ReporteMovimientos">
                        <img src="images/reporteMovimientos.png" alt="" width="20" height="20" class="me-2">
                        <span class="text-label">Reporte de movimientos</span>
                    </a>
                </li>
                <% } %>
                <% if ((permisos.contains(10) && roleId==1) || (permisos.contains(10) && roleId==2) || (permisos.contains(10) && roleId==3) || (permisos.contains(10) && roleId==4)) { %>
                <li class="nav-item">
                    <a class="nav-link <%= uri.contains("/StockBajo_OrdenCompra") ? "active" : "" %>" href="<%= ctx %>/StockBajo_OrdenCompra?action=stock">
                        <img src="images/reporteVentas.png" alt="" width="20" height="20" class="me-2">
                        <span class="text-label">Reporte de compras</span>
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
                <span class="text-label">Gestion de inventarios</span>
            </a>
        </li>
        <% } %>

        <% if ((permisos.contains(18) && roleId==1) || (permisos.contains(18) && roleId==2) || (permisos.contains(18) && roleId==3) || (permisos.contains(18) && roleId==4)) { %>
        <li>
            <a href="<%=request.getContextPath()%>/cargaExcel" class="nav-link text-white">
                <img src="<%=request.getContextPath()%>/Almacen/img/indexCarga.png" width="25" height="25" class="me-2">
                <span class="text-label">Carga masiva de datos</span>
            </a>
        </li>
        <% } %>

        <% if ((permisos.contains(19) && roleId==1) || (permisos.contains(19) && roleId==2) || (permisos.contains(19) && roleId==3) || (permisos.contains(19) && roleId==4)) { %>
        <li>
            <a href="<%=request.getContextPath()%>/IncidenciaAlmServlet" class="nav-link text-white">
                <img src="<%=request.getContextPath()%>/Almacen/img/incidencia.png" width="25" height="25" class="me-2">
                <span class="text-label">Incidencias </span>
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
