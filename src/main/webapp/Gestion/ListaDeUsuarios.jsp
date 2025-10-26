<%@ page import="java.util.Date" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="com.example.telitobodeguero.beans.Usuarios" %>
<%@ page import="java.util.Set" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<% Set<Integer> permisos = (Set<Integer>) session.getAttribute("permisosRol");
    if (permisos == null) permisos = java.util.Collections.emptySet(); %>
<% ArrayList<Usuarios> listaUsuarios = (ArrayList<Usuarios>) request.getAttribute("usuarios");
    if (listaUsuarios == null) listaUsuarios = new ArrayList<>();

    Integer roleId = null;
    Usuarios u = (Usuarios) session.getAttribute("usuarioLog");
    if (u != null && u.getRol() != null) {
        roleId = u.getRol().getIdRoles();
    }

    // ===== Paginación: valores enviados desde el servlet =====
    Integer currentPageAttr = (Integer) request.getAttribute("currentPage");
    Integer totalPagesAttr  = (Integer) request.getAttribute("totalPages");

    int currentPage = (currentPageAttr == null) ? 1 : currentPageAttr;
    int totalPages  = (totalPagesAttr  == null) ? 1 : totalPagesAttr;

    // Base URL para los links de paginación.
    // Nota: mantenemos action=usuarios para que vuelva al case "usuarios" del servlet.
    String baseUrl = request.getContextPath() + "/ListaUsuariosServlet?action=usuarios&page=";
%>
<!doctype html>
<html lang="es">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Telito - Admin | Lista de usuarios</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.0.2/dist/css/bootstrap.min.css" rel="stylesheet">

    <style>
        /* ===== Layout ===== */
        body { margin:0; background:#f3f5f7; }
        .sidebar{
            position:fixed; inset:0 auto 0 0;      /* top:0; left:0; bottom:0 */
            width:280px; background:#212529; color:#fff;
            z-index:1000; transition:width .25s ease; overflow-y:auto;
        }
        .sidebar.collapsed{ width:80px; }
        .sidebar .brand{ padding:1rem 1.25rem; display:flex; align-items:center; gap:.75rem; }
        .sidebar .toggle{ border:0; background:#0d6efd; color:#fff; padding:.5rem .6rem; border-radius:.5rem; }
        .sidebar .nav-link{ color:#d6d6d6; }
        .sidebar .nav-link:hover{ background:#0d6efd; color:#fff; }
        .text-label{ display:inline; }
        .sidebar.collapsed .text-label{ display:none; }

        .main{
            margin-left:280px; transition:margin-left .25s ease;
            min-height:100vh; padding:2rem;
        }
        .main.collapsed{ margin-left:80px; }

        /* ===== Encabezado de página ===== */
        .page-title{ color:#1f2d3d; }

        /* ===== Tabla (tarjeta contenedora) ===== */
        .table-card{
            background:#fff; border-radius:.75rem; overflow:hidden;
            box-shadow:0 6px 14px rgba(0,0,0,.12);
        }
        .table-card thead th{
            background:#f0f2f5;
        }
        h1 {
            font-family: 'Poppins', Inter, sans-serif;
            font-weight: 800;
            color: #2e63f5;     /* Azul corporativo */
            letter-spacing: .3px;
            margin-bottom: 1.25rem;
            text-transform: uppercase;
        }

        /* ===== Paginación ===== */
        .pagination-wrapper{
            background:#fff;
            border-radius:.75rem;
            box-shadow:0 6px 14px rgba(0,0,0,.12);
            margin-top:1rem;
            padding:.75rem 1rem;
            display:flex;
            justify-content:center;
        }
    </style>
</head>
<body>

<!-- ===== Sidebar izquierda ===== -->
<jsp:include page="/sidebar.jsp" />

<!-- ===== Contenido principal ===== -->
<main class="main" id="main">
    <div class="container-fluid">
        <div class="d-flex align-items-center justify-content-between flex-wrap mb-3">
            <h1 class="mb-0">Lista de usuarios</h1>

            <% if ((permisos.contains(3) && roleId==1) || (permisos.contains(3) && roleId==2) || (permisos.contains(3) && roleId==3) || (permisos.contains(3) && roleId==4)) { %>
            <a href="<%=request.getContextPath()%>/ListaUsuariosServlet?action=formCrear"
               class="btn btn-primary">
                + Nuevo usuario
            </a>
            <% } %>
        </div>
        <div class="table-responsive table-card">
            <table class="table table-striped align-middle mb-0">
                <thead>
                <tr>
                    <th scope="col">Id</th>
                    <th scope="col">Usuario</th>
                    <th scope="col">Rol</th>
                    <th scope="col">Correo</th>
                    <th scope="col">Acciones</th>
                    <th></th>
                </tr>
                </thead>
                <tbody>
                <% for (Usuarios usuario: listaUsuarios){ %>
                <tr>
                    <th scope="row"><%=usuario.getIdUsuarios()%></th>
                    <td><%=usuario.getNombre() +" "+usuario.getApellido()%></td>
                    <td><%=usuario.getRol().getNombre()%></td>
                    <td><%=usuario.getCorreo()%></td>
                    <td class="d-flex gap-2">
                        <%-- Botón Editar (con estilo de botón) --%>
                        <a href="<%=request.getContextPath()%>/ListaUsuariosServlet?action=editar&id=<%=usuario.getIdUsuarios()%>"
                           class="btn btn-warning btn-sm">Editar</a>
                        <%-- Botón Activo/Inactivo (con estilo de botón) --%>
                        <% if (usuario.getActivo()) { %>
                        <a class="btn btn-success btn-sm"
                           href="<%=request.getContextPath()%>/ListaUsuariosServlet?action=borrar&id=<%=usuario.getIdUsuarios()%>"
                           onclick="return confirm('¿Desactivar a <%=usuario.getNombre()%>?');">
                            Activo
                        </a>
                        <% } else { %>
                        <a class="btn btn-secondary btn-sm"
                           href="<%=request.getContextPath()%>/ListaUsuariosServlet?action=activar&id=<%=usuario.getIdUsuarios()%>">
                            Inactivo
                        </a>
                        <% } %>
                    </td>

                </tr>
                <% } %>

                </tbody>
            </table>
        </div>

        <!-- ===== PAGINACIÓN ===== -->
        <div class="pagination-wrapper">
            <nav aria-label="Paginación de usuarios">
                <ul class="pagination mb-0">

                    <!-- Botón «Anterior» -->
                    <li class="page-item <%= (currentPage <= 1 ? "disabled" : "") %>">
                        <a class="page-link"
                           href="<%= baseUrl + (currentPage - 1) %>">
                            &laquo;
                        </a>
                    </li>

                    <!-- Números de página -->
                    <%
                        for (int i = 1; i <= totalPages; i++) {
                    %>
                    <li class="page-item <%= (i == currentPage ? "active" : "") %>">
                        <a class="page-link"
                           href="<%= baseUrl + i %>"><%= i %></a>
                    </li>
                    <%
                        }
                    %>

                    <!-- Botón «Siguiente» -->
                    <li class="page-item <%= (currentPage >= totalPages ? "disabled" : "") %>">
                        <a class="page-link"
                           href="<%= baseUrl + (currentPage + 1) %>">
                            &raquo;
                        </a>
                    </li>

                </ul>
            </nav>
        </div>
    </div>
</main>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.0.2/dist/js/bootstrap.bundle.min.js"></script>
<script>
    // Colapsar/expandir sidebar
    const btn = document.getElementById('btnToggle');
    const sidebar = document.getElementById('sidebar');
    const main = document.getElementById('main');
    btn.addEventListener('click', () => {
        sidebar.classList.toggle('collapsed');
        main.classList.toggle('collapsed');
    });
</script>
</body>
</html>

