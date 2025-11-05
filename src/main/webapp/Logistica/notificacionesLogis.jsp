<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="com.example.telitobodeguero.dtos.NotificacionTipo" %>
<%@ page import="com.example.telitobodeguero.dtos.NotificacionLogisDTO" %>

<%
    List<NotificacionLogisDTO> notificaciones =
            (List<NotificacionLogisDTO>) request.getAttribute("listaNotificaciones");
%>
<!doctype html>
<html lang="es">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Telito - Logística · Notificaciones</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.0.2/dist/css/bootstrap.min.css" rel="stylesheet">

    <style>
        /* ====== Layout base de Logística (igual a bienvenidos.jsp) ====== */
        body{ margin:0; background:#f3f5f7; }
        .sidebar{
            position:fixed; inset:0 auto 0 0; width:280px; background:#212529; color:#fff;
            z-index:1000; transition:width .25s ease; overflow-y:auto;
        }
        .sidebar.collapsed{ width:80px; }
        .sidebar .brand{ padding:1rem 1.25rem; display:flex; align-items:center; gap:.75rem; }
        .sidebar .brand .toggle{ border:0; background:#0d6efd; color:#fff; padding:.5rem .6rem; border-radius:.5rem; }
        .sidebar .nav-link{ color:#d6d6d6; }
        .sidebar .nav-link:hover,.sidebar .nav-link:focus{ background:#0d6efd; color:#fff; }
        .sidebar .dropdown-menu{ background:#2b3035; }
        .sidebar .dropdown-item{ color:#fff; }
        .sidebar .dropdown-item:hover{ background:#0d6efd; }
        .sidebar .spacer{ height:1px; background:#343a40; margin:.5rem 0; }
        .sidebar.collapsed .text-label{ display:none; }

        .main{ margin-left:280px; transition:margin-left .25s ease; min-height:100vh; padding:2rem; }
        .main.collapsed{ margin-left:80px; }

        /* ====== Estilos de tarjetas de alerta ====== */
        .alert-box{
            background:#fff; border-radius:16px; padding:18px;
            box-shadow:0 6px 16px rgba(0,0,0,.06);
        }
        .notif{
            display:flex; gap:14px; padding:14px 16px; border-radius:10px;
            border-left:6px solid transparent; background:#f9fafb; margin-bottom:12px;
        }
        .notif .icon{
            font-size:1.2rem; width:28px; height:28px;
            display:flex; align-items:center; justify-content:center;
        }
        .notif .content{ flex:1; }
        .notif .content .title{ font-weight:700; margin-bottom:4px; color:#1f2937; }
        .notif .content .msg{ margin:0; color:#4b5563; }
        .notif .meta{ font-size:.9rem; color:#6b7280; margin-top:6px; }

        /* Colores por tipo */
        .notif.warning{ border-left-color:#f59e0b; background:#fffbeb; }
        .notif.warning .icon{ color:#b45309; }
        .notif.info{ border-left-color:#3b82f6; background:#eff6ff; }
        .notif.info .icon{ color:#1d4ed8; }

        .empty{
            text-align:center; color:#6b7280; padding:36px;
            background:#fff; border:1px dashed #cbd5e1; border-radius:12px;
        }

        h1{ font-weight:800; color:#2e63f5; letter-spacing:.3px; }
    </style>
    <link rel="stylesheet"
          href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.4/css/all.min.css">
</head>
<body>

<!-- Sidebar (misma inclusión que en Logística) -->
<jsp:include page="/sidebar.jsp" />

<!-- Main -->
<main class="main" id="main">
    <!-- Botón atrás como en bienvenidos.jsp -->
    <div class="mb-2">
        <button type="button" class="btn btn-outline-secondary btn-sm" onclick="history.back()">
            &larr; Volver
        </button>
    </div>

    <h1 class="mb-3">Notificaciones · Logística</h1>
    <hr>

    <div class="alert-box">
        <h5 class="mb-3">Alertas activas</h5>

        <%
            if (notificaciones == null || notificaciones.isEmpty()) {
        %>
        <div class="empty">
            ¡Todo en orden! No hay notificaciones de logística por ahora.
        </div>
        <%
        } else {
            for (NotificacionLogisDTO n : notificaciones) {
                String klass = "info";
                String icon = "&#128276;"; // campana por defecto

                if (n.getTipo() == NotificacionTipo.STOCK_BAJO) {
                    klass = "warning";
                    icon = "&#9888;"; // ⚠
                } else if (n.getTipo() == NotificacionTipo.CAMBIO_ESTADO_OC) {
                    klass = "info";
                    icon = "&#10227;"; // ↻
                }
        %>
        <div class="notif <%= klass %>">
            <div class="icon"><%= icon %></div>
            <div class="content">
                <div class="title"><%= n.getTitulo() %></div>
                <p class="msg"><%= n.getMensaje() %></p>
                <div class="meta">
                    <span>Fecha: <%= n.getFechaRelevante() %></span>
                    <% if (n.getZonaNombre() != null) { %>
                    &nbsp;·&nbsp;<span>Zona: <%= n.getZonaNombre() %></span>
                    <% } %>
                </div>
            </div>
        </div>
        <%
                } // end for
            } // end else
        %>
    </div>
</main>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.0.2/dist/js/bootstrap.bundle.min.js"></script>
<script>
    // Toggle sidebar (mismo patrón que en Logística)
    const btn = document.getElementById('btnToggle');
    const sidebar = document.getElementById('sidebar');
    const main = document.getElementById('main');
    if (btn && sidebar && main) {
        btn.addEventListener('click', () => {
            sidebar.classList.toggle('collapsed');
            main.classList.toggle('collapsed');
        });
    }
</script>
</body>
</html>
