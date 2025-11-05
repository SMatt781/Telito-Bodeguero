<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.*, java.util.Map" %>

<%
    String ctx = request.getContextPath();
    List<Map<String, String>> notificaciones =
            (List<Map<String, String>>) request.getAttribute("notificaciones");
    Set<Integer> ordenesVistas =
            (Set<Integer>) request.getAttribute("ordenesVistas");
%>

<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1" />
    <title>Notificaciones</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.0.2/dist/css/bootstrap.min.css"
          rel="stylesheet" crossorigin="anonymous" />

    <style>
        body {
            display: flex;
            min-height: 100vh;
            background: #f7f9fc;
        }
        /* Sidebar */
        .sidebar {
            position: fixed;
            inset: 0 auto 0 0;
            width: 280px;
            background: #212529;
            color: #fff;
            z-index: 1000;
            transition: width 0.25s ease;
            overflow-y: auto;
        }
        .sidebar.collapsed { width: 80px; }
        .sidebar .brand {
            padding: 1rem 1.25rem;
            display: flex;
            align-items: center;
            gap: .75rem;
        }
        .sidebar .brand .toggle {
            border: 0;
            background: #0d6efd;
            color: #fff;
            padding: .5rem .6rem;
            border-radius: .5rem;
        }
        .sidebar .nav-link { color: #d6d6d6; }
        .sidebar .nav-link:hover,
        .sidebar .nav-link:focus { background: #0d6efd; color: #fff; }
        .sidebar.collapsed .text-label { display: none; }

        /* Contenido principal */
        .main {
            margin-left: 280px;
            transition: margin-left 0.25s ease;
            min-height: 100vh;
            padding: 2rem;
        }
        .main.collapsed { margin-left: 80px; }

        /* Estilo de notificaciones */
        .alert-custom {
            background: #fff;
            border-left: 5px solid #0d6efd;
            padding: 1rem;
            margin-bottom: 1rem;
            border-radius: .5rem;
            display: flex;
            justify-content: space-between;
            align-items: center;
            box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
            transition: transform 0.2s ease;
        }
        .alert-custom:hover { transform: translateY(-3px); }
        .alert-custom.read {
            opacity: 0.65;
            border-left-color: #6c757d;
        }
        .alert-message { flex: 1; word-break: break-word; }
        .dot {
            width: 10px;
            height: 10px;
            background: #0d6efd;
            border-radius: 50%;
            display: inline-block;
            margin-right: .5rem;
        }
        .dot.read { background: #6c757d; }

        /* Texto vacío */
        .no-notificaciones {
            text-align: center;
            color: #718096;
            font-size: 1.1em;
            padding: 40px;
            background-color: #fff;
            border: 1px dashed #e2e8f0;
            border-radius: 8px;
        }

        /* Responsive */
        @media (max-width: 991.98px) {
            .sidebar {
                width: 0;
                overflow: hidden;
            }
            .main {
                margin-left: 0 !important;
                padding: 1rem;
            }
            .alert-custom {
                flex-direction: column;
                align-items: flex-start;
                gap: .75rem;
            }
            .alert-message {
                font-size: .95rem;
            }
            .btn {
                align-self: flex-end;
                width: 100%;
            }
        }

        @media (max-width: 575.98px) {
            .alert-message { font-size: 0.9rem; }
            .display-6 { font-size: 1.5rem; }
        }
    </style>
</head>
<body>

<jsp:include page="/sidebar.jsp" />

<main class="main" id="main">
    <div class="d-flex flex-wrap justify-content-between align-items-center mb-3 gap-2">
        <div>
            <h2 class="display-6 fw-bold text-primary m-0">Notificaciones</h2>
            <small class="text-muted">Aquí puedes ver tus notificaciones recientes y marcarlas como leídas.</small>
        </div>
    </div>

    <% if (notificaciones == null || notificaciones.isEmpty()) { %>
    <div class="no-notificaciones">
        🎉 ¡Todo en orden! No tienes notificaciones nuevas.
    </div>
    <% } else { %>
    <% for (Map<String, String> noti : notificaciones) {
        int id = Integer.parseInt(noti.get("id"));
        boolean visto = ordenesVistas != null && ordenesVistas.contains(id);
    %>
    <div class="alert-custom <%= visto ? "read" : "" %>">
        <div class="alert-message">
            <span class="dot <%= visto ? "read" : "" %>"></span>
            <%= noti.get("mensaje") %>
        </div>

        <% if (!visto) { %>
        <a href="<%= ctx %>/Notificaciones?a=visto&id=<%= id %>"
           class="btn btn-sm btn-outline-primary">Marcar como leído</a>
        <% } else { %>
        <span class="text-muted small">✔ Leído</span>
        <% } %>
    </div>
    <% } %>
    <% } %>
</main>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.0.2/dist/js/bootstrap.bundle.min.js"
        crossorigin="anonymous"></script>
<script>
    const btn = document.getElementById('btnToggle');
    const sidebar = document.getElementById('sidebar');
    const main = document.getElementById('main');
    if(btn && sidebar && main){
        btn.addEventListener('click',()=>{
            sidebar.classList.toggle('collapsed');
            main.classList.toggle('collapsed');
        });
    }
</script>
</body>
</html>


