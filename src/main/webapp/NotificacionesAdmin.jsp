<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.*, java.util.Map" %>
<%
    String ctx = request.getContextPath();
    List<Map<String,String>> notificaciones =
            (List<Map<String,String>>) request.getAttribute("notificaciones");
    if (notificaciones == null) notificaciones = new ArrayList<>();

    List<Map<String,String>> otras =
            (List<Map<String,String>>) request.getAttribute("otrasNotificaciones");
    if (otras == null) otras = new ArrayList<>();
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8"/>
    <title>Notificaciones – Admin</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.2.3/dist/css/bootstrap.min.css" rel="stylesheet"/>
    <style>
        .unread{background:#e8f1ff;}
    body{margin:0; background:#f3f5f7;}
    .sidebar{
        position:fixed; inset:0 auto 0 0;       /* top:0; left:0; bottom:0 */
        width:280px; background:#212529; color:#fff;
        z-index:1000; transition:width .25s ease;
        overflow-y:auto;
    }
    .sidebar.collapsed{ width:80px; }
    .sidebar .brand{ padding:1rem 1.25rem; display:flex; align-items:center; gap:.75rem; }
    .sidebar .brand .toggle{ border:0; background:#0d6efd; color:#fff; padding:.5rem .6rem; border-radius:.5rem; }
    .sidebar .nav-link{ color:#d6d6d6; }
    .sidebar .nav-link:hover, .sidebar .nav-link:focus{ background:#0d6efd; color:#fff; }
    .sidebar .dropdown-menu{ background:#2b3035; }
    .sidebar .dropdown-item{ color:#fff; }
    .sidebar .dropdown-item:hover{ background:#0d6efd; }
    /* Ocultar textos cuando está colapsado */
    .sidebar.collapsed .text-label{ display:none; }
    .main{
        margin-left:280px; transition:margin-left .25s ease;
        min-height:100vh; padding:2rem;
    }
        .main.collapsed{ margin-left:80px; }
    </style>
</head>
<body>
<jsp:include page="/sidebar.jsp"/>

<main class="main" id="main">
<div class="container mt-4">
    <div class="d-flex justify-content-between align-items-center mb-3">
        <h4 class="m-0">Notificaciones</h4>
        <form method="post" action="<%=ctx%>/Admin/Notificaciones" class="m-0">
            <input type="hidden" name="action" value="markAll"/>
            <button class="btn btn-sm btn-outline-secondary">Marcar todas como leídas</button>
        </form>
    </div>

    <div class="card mb-4">
        <div class="card-body">
            <h5 class="mb-3">Cambios de estado de Orden de Compra</h5>
            <ul class="list-group">
                <%
                    if (notificaciones.isEmpty()) {
                %>
                <li class="list-group-item">No hay notificaciones.</li>
                <%
                } else {
                    for (Map<String,String> n : notificaciones) {
                        boolean leido = "1".equals(n.get("leido"));
                %>
                <li class="list-group-item d-flex justify-content-between align-items-start <%= leido ? "" : "unread" %>">
                    <div class="ms-2 me-auto">
                        <div class="fw-bold">OC #<%= n.get("id") %></div>
                        <div><%= n.get("mensaje") %></div>
                        <small class="text-muted"><%= n.get("ts") %></small>
                    </div>
                    <% if (!leido) { %>
                    <form method="post" action="<%=ctx%>/Admin/Notificaciones" class="ms-3">
                        <input type="hidden" name="action" value="markOne"/>
                        <input type="hidden" name="key" value="<%= n.get("key") %>"/>
                        <button class="btn btn-sm btn-outline-primary">Leída</button>
                    </form>
                    <% } %>
                </li>
                <%
                        }
                    }
                %>
            </ul>
        </div>
    </div>


    <div class="card">
        <div class="card-body">
            <h5 class="mb-3">Otras notificaciones</h5>
            <%
                if (otras.isEmpty()) {
            %>
            <div class="text-muted">Aún no hay otras notificaciones…</div>
            <%
            } else {
            %>
            <ul class="list-group">
                <%
                    for (Map<String,String> n : otras) {
                %>
                <li class="list-group-item d-flex justify-content-between align-items-start">
                    <div class="ms-2 me-auto">
                        <div class="fw-bold"><%= n.getOrDefault("titulo","Notificación") %></div>
                        <div><%= n.getOrDefault("mensaje","(sin mensaje)") %></div>
                    </div>
                </li>
                <% } %>
            </ul>
            <%
                }
            %>
        </div>
    </div>

</div>
</main>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.0.2/dist/js/bootstrap.bundle.min.js"></script>
<script>
    // Toggle del sidebar
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
