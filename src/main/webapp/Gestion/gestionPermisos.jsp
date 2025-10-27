<%@ page import="java.util.Date" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="com.example.telitobodeguero.beans.Permisos" %>
<%@ page import="com.example.telitobodeguero.beans.Roles" %>
<%@ page import="com.example.telitobodeguero.beans.Roles_has_Permisos" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%
    ArrayList<Roles_has_Permisos> listaPermisos= (ArrayList<Roles_has_Permisos>) request.getAttribute("lista");
    ArrayList<Roles> listaRoles = (ArrayList<Roles>) request.getAttribute("listaRoles");
    if (listaRoles == null) listaRoles = new ArrayList<>();

    // Leer rolId del query param
    String rolSeleccionadoParam = request.getParameter("rolId");
    Integer rolSeleccionado = null;
    if (rolSeleccionadoParam != null && rolSeleccionadoParam.matches("\\d+")) {
        rolSeleccionado = Integer.parseInt(rolSeleccionadoParam);
    }

    // Si no vino rolId (por ejemplo entrada directa a la página),
    // elegimos el primero de listaRoles como default para no dejar nada "sin selección".
    if (rolSeleccionado == null && !listaRoles.isEmpty()) {
        rolSeleccionado = listaRoles.get(0).getIdRoles();
    }
%>

<!doctype html>
<html lang="es">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Telito - Admin | Gestión de permisos</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.0.2/dist/css/bootstrap.min.css" rel="stylesheet">

    <style>
        body { margin:0; background:#f3f5f7; }
        .sidebar{
            position:fixed; inset:0 auto 0 0; width:280px; background:#212529; color:#fff;
            z-index:1000; transition:width .25s ease; overflow-y:auto;
        }
        .sidebar.collapsed{ width:80px; }
        .sidebar .brand{ padding:1rem 1.25rem; display:flex; align-items:center; gap:.75rem; }
        .sidebar .toggle{
            border:0; background:#0d6efd; color:#fff;
            padding:.5rem .6rem; border-radius:.5rem;
        }
        .sidebar .nav-link{ color:#d6d6d6; }
        .sidebar .nav-link:hover{ background:#0d6efd; color:#fff; }
        .text-label{ display:inline; }
        .sidebar.collapsed .text-label{ display:none; }

        .main{
            margin-left:280px; transition:margin-left .25s ease;
            min-height:100vh; padding:2rem;
        }
        .main.collapsed{ margin-left:80px; }

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
            color: #2e63f5;
            letter-spacing: .3px;
            margin-bottom: 1.25rem;
            text-transform: uppercase;
        }

        .filter-group {
            display: flex;
            flex-wrap: wrap;
            gap: 1.5rem;
        }
        .filter-item {
            display: flex;
            align-items: center;
            cursor: pointer;
            user-select: none;
        }
        .filter-item input[type="radio"] {
            display: none;
        }
        .filter-item .radio-circle {
            width: 20px;
            height: 20px;
            border: 2px solid #adb5bd;
            border-radius: 50%;
            margin-right: 0.5rem;
            display: flex;
            align-items: center;
            justify-content: center;
            transition: all 0.2s ease;
        }
        .filter-item .radio-circle::after {
            content: '';
            width: 10px;
            height: 10px;
            background-color: #0d6efd;
            border-radius: 50%;
            display: block;
            transform: scale(0);
            transition: transform 0.2s ease;
        }
        .filter-item input[type="radio"]:checked + .radio-circle {
            border-color: #0d6efd;
        }
        .filter-item input[type="radio"]:checked + .radio-circle::after {
            transform: scale(1);
        }
        .filter-item .role-name {
            font-weight: 500;
            color: #495057;
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
            <h1 class="mb-0">Gestión de permisos</h1>
        </div>

        <!-- FILTRO DE ROL (SIN "VER TODOS") -->
        <div class="mb-4">
            <label class="form-label fw-bold d-block mb-2">Filtrar por rol:</label>

            <form method="get"
                  action="<%=request.getContextPath()%>/GestionPermisosServlet"
                  id="roleFilterForm">

                <div class="filter-group">

                    <% for (Roles r : listaRoles) { %>
                    <label class="filter-item">
                        <input type="radio"
                               name="rolId"
                               value="<%=r.getIdRoles()%>"
                               onchange="this.form.submit()"
                            <%= (rolSeleccionado != null && rolSeleccionado.equals(r.getIdRoles()))
                                           ? "checked"
                                           : "" %>>
                        <span class="radio-circle"></span>
                        <span class="role-name"><%=r.getNombre()%></span>
                    </label>
                    <% } %>

                </div>
            </form>
        </div>
        <!-- FIN FILTRO DE ROL -->

        <div class="table-responsive table-card">
            <table class="table table-striped align-middle mb-0">
                <thead>
                <tr>
                    <th scope="col">#</th>
                    <th scope="col">Rol</th>
                    <th scope="col">Permiso</th>
                    <th scope="col">Activo/Desactivo</th>
                </tr>
                </thead>
                <tbody>
                <% int contadorFilas = 0; %>
                <% for (Roles_has_Permisos rhp: listaPermisos) {

                    // Ahora ya no mostramos "todos": solo mostramos
                    // las filas cuyo rol coincide con rolSeleccionado
                    if (rolSeleccionado != null
                            && rhp.getRol().getIdRoles() == rolSeleccionado) {

                        contadorFilas++;
                %>
                <tr>
                    <th scope="row"><%= contadorFilas %></th>
                    <td><%=rhp.getRol().getNombre()%></td>
                    <td><%=rhp.getPermiso().getNombre()%></td>
                    <td>
                        <form method="post"
                              action="<%=request.getContextPath()%>/GestionPermisosServlet"
                              style="display:inline;"
                              id="form-<%=rhp.getRol().getIdRoles()%>-<%=rhp.getPermiso().getIdPermisos()%>">

                            <input type="hidden" name="action" value="toggle">
                            <input type="hidden" name="rolId" value="<%=rhp.getRol().getIdRoles()%>">
                            <input type="hidden" name="permisoId" value="<%=rhp.getPermiso().getIdPermisos()%>">

                            <!-- estado nuevo que vamos a mandar -->
                            <input type="hidden"
                                   name="estado"
                                   value="<%= rhp.isActivacion() ? 0 : 1 %>"
                                   id="estado-<%=rhp.getRol().getIdRoles()%>-<%=rhp.getPermiso().getIdPermisos()%>">

                            <!-- para mantener el filtro seleccionado al recargar -->
                            <input type="hidden"
                                   name="rolFiltro"
                                   value="<%= rolSeleccionado != null ? rolSeleccionado.toString() : "" %>">

                            <button type="button"
                                    class="badge <%= rhp.isActivacion() ? "bg-success" : "bg-secondary" %>"
                                    onclick="toggleStatus('<%=rhp.getRol().getIdRoles()%>', '<%=rhp.getPermiso().getIdPermisos()%>', this)">
                                <%= rhp.isActivacion() ? "Activo" : "Inactivo" %>
                            </button>
                        </form>
                    </td>
                </tr>
                <% } } %>
                </tbody>
            </table>
        </div>
    </div>
</main>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.0.2/dist/js/bootstrap.bundle.min.js"></script>
<script>
    // Sidebar collapse
    const btn = document.getElementById('btnToggle');
    const sidebar = document.getElementById('sidebar');
    const main = document.getElementById('main');
    if (btn && sidebar && main) {
        btn.addEventListener('click', () => {
            sidebar.classList.toggle('collapsed');
            main.classList.toggle('collapsed');
        });
    }

    // Activar / desactivar permiso (toggle badge)
    function toggleStatus(rolId, permisoId, element) {
        const hiddenInput = document.getElementById("estado-" + rolId + "-" + permisoId);

        if (element.classList.contains("bg-success")) {
            element.classList.remove("bg-success");
            element.classList.add("bg-secondary");
            element.textContent = "Inactivo";
            hiddenInput.value = "0";
        } else {
            element.classList.remove("bg-secondary");
            element.classList.add("bg-success");
            element.textContent = "Activo";
            hiddenInput.value = "1";
        }

        document.getElementById("form-" + rolId + "-" + permisoId).submit();
    }

    // Guardar scroll antes de recargar
    window.addEventListener("beforeunload", () => {
        sessionStorage.setItem("scrollPos", window.scrollY);
    });

    // Restaurar scroll después de recargar
    window.addEventListener("load", () => {
        const scrollPos = sessionStorage.getItem("scrollPos");
        if (scrollPos !== null) {
            window.scrollTo({ top: parseInt(scrollPos), behavior: "instant" });
            sessionStorage.removeItem("scrollPos");
        }
    });
</script>
</body>
</html>
