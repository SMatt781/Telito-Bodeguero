<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.*" %>
<%@ page import="com.example.telitobodeguero.dtos.NotificacionLogisDTO" %>
<%@ page import="com.example.telitobodeguero.dtos.NotificacionTipo" %>

<%
    // Lista total que viene del servlet
    List<NotificacionLogisDTO> todas =
            (List<NotificacionLogisDTO>) request.getAttribute("listaNotificaciones");
    if (todas == null) todas = new ArrayList<>();

    // Separamos en dos listas: cambios de estado OC y stock bajo
    List<NotificacionLogisDTO> cambiosOC = new ArrayList<>();
    List<NotificacionLogisDTO> stockBajo = new ArrayList<>();

    for (NotificacionLogisDTO n : todas) {
        if (n.getTipo() == NotificacionTipo.CAMBIO_ESTADO_OC) {
            cambiosOC.add(n);
        } else if (n.getTipo() == NotificacionTipo.STOCK_BAJO) {
            stockBajo.add(n);
        }
    }
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8"/>
    <title>Notificaciones – Logística</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.2.3/dist/css/bootstrap.min.css" rel="stylesheet"/>

    <!-- Iconos -->
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.4/css/all.min.css">

    <style>
        /* ===== Layout base (copiado del JSP de Admin) ===== */
        body{ min-height:100vh; background:#f7fafc; overflow-x:hidden; }

        .sidebar{
            position:fixed; top:0; left:0; bottom:0;
            width:280px; background:#212529; color:#fff;
            z-index:1000; transition:width .25s ease;
            overflow-y:auto; display:flex; flex-direction:column;
        }
        .sidebar.collapsed{ width:80px; }
        .sidebar .brand{ padding:1rem 1.25rem; display:flex; align-items:center; gap:.75rem; }
        .sidebar .brand .toggle{ border:0; background:#0d6efd; color:#fff; padding:.5rem .6rem; border-radius:.5rem; }
        .sidebar .nav-link{ color:#d6d6d6; }
        .sidebar .nav-link:hover,.sidebar .nav-link:focus{ background:#0d6efd; color:#fff; }
        .sidebar .dropdown-menu{ background:#2b3035; }
        .sidebar .dropdown-item{ color:#fff; }
        .sidebar .dropdown-item:hover{ background:#0d6efd; }
        .sidebar.collapsed .text-label{ display:none; }

        .content{
            margin-left:280px; transition:margin-left .25s ease;
            padding:2rem 1rem; min-height:100vh;
        }
        .content.collapsed{ margin-left:80px; }

        .page-wrap{ max-width:1100px; margin:0 auto; }

        .titulo-principal{ font-size:2.25rem; color: #2d3748; }

        @media (max-width: 767.98px){
            .sidebar{
                left:-280px; width:280px;
                display:flex !important;
            }
            .sidebar.show{ left:0; }
            .content{ margin-left:0; padding:1rem; padding-top:72px; }
            .page-wrap{ max-width:100%; }
            .titulo-principal{ font-size:1.8rem; text-align:center; }
        }

        /* ===== Estilos de las alertas ===== */
        .page-wrap > p {
            font-size: 1.1em;
            color: #718096;
            margin-top: 0;
            margin-bottom: 30px;
        }

        .passive-alerts-title {
            font-size: 1.5em;
            color: #2d3748;
            margin-top: 40px;
            margin-bottom: 20px;
            font-weight: 600;
        }

        .alert {
            display: flex;
            align-items: flex-start;
            padding: 16px;
            margin-bottom: 16px;
            border-radius: 6px;
            box-shadow: 0 1px 3px 0 rgba(0, 0, 0, 0.1), 0 1px 2px 0 rgba(0, 0, 0, 0.06);
            border-top-width: 4px;
            border-top-style: solid;
            border-left: none;
            border-right: none;
            border-bottom: none;
        }

        .alert .icon {
            font-size: 1.4em;
            margin-right: 12px;
            min-width: 24px;
            text-align: center;
            padding-top: 2px;
        }

        .alert-content { flex-grow: 1; }
        .alert-content strong {
            font-size: 1.1em;
            display: block;
            margin-bottom: 4px;
            font-weight: 600;
        }
        .alert-content p {
            margin: 0 0 10px 0;
            color: #4a5568;
            line-height: 1.5;
            font-size: 1em;
        }
        .alert-content .meta { font-size: 0.9em; color: #718096; }
        .alert-content .meta span { margin-right: 15px; }

        /* Variantes de color */
        .alert-warning { background-color: #fffbeb; border-top-color: #f59e0b; }
        .alert-warning .icon, .alert-warning .alert-content strong { color: #854d0e; }

        .alert-info { background-color: #eff6ff; border-top-color: #3b82f6; }
        .alert-info .icon, .alert-info .alert-content strong { color: #1e40af; }

        .no-notificaciones {
            text-align: center;
            color: #718096;
            font-size: 1.1em;
            padding: 40px;
            background-color: #fff;
            border: 1px dashed #e2e8f0;
            border-radius: 8px;
        }
    </style>
</head>
<body>

<jsp:include page="/sidebar.jsp"/>

<main class="content" id="main">
    <div class="page-wrap">

        <h1 class="fw-bold titulo-principal mb-2">Notificaciones · Logística</h1>
        <p>Aquí se agrupan los cambios de estado de órdenes de compra y las alertas de stock bajo.</p>

        <!-- ===== Cambios de estado de Orden de Compra ===== -->
        <h2 class="passive-alerts-title">Cambios de estado de Orden de Compra</h2>

        <%
            if (cambiosOC.isEmpty()) {
        %>
        <div class="no-notificaciones">
            <p>¡Todo en orden! No hay cambios de estado recientes.</p>
        </div>
        <%
        } else {
            for (NotificacionLogisDTO n : cambiosOC) {
        %>
        <div class="alert alert-info">
            <i class="icon fas fa-info-circle"></i>
            <div class="alert-content">
                <strong><%= n.getTitulo() %></strong>
                <p><%= n.getMensaje() %></p>
                <div class="meta">
                    <span class="fecha">Fecha: <%= n.getFechaRelevante() %></span>
                </div>
            </div>
        </div>
        <%
                }
            }
        %>

        <!-- ===== Alertas de stock bajo ===== -->
        <h2 class="passive-alerts-title">Alertas de stock bajo</h2>

        <%
            if (stockBajo.isEmpty()) {
        %>
        <div class="no-notificaciones">
            <p>¡Todo en orden! No hay productos con stock bajo.</p>
        </div>
        <%
        } else {
            for (NotificacionLogisDTO n : stockBajo) {
        %>
        <div class="alert alert-warning">
            <i class="icon fas fa-exclamation-triangle"></i>
            <div class="alert-content">
                <strong><%= n.getTitulo() %></strong>
                <p><%= n.getMensaje() %></p>
                <div class="meta">
                    <span class="fecha">Fecha: <%= n.getFechaRelevante() %></span>
                    <%
                        if (n.getZonaNombre() != null && !n.getZonaNombre().trim().isEmpty()) {
                    %>
                    <span>Zona: <%= n.getZonaNombre() %></span>
                    <%
                        }
                    %>
                </div>
            </div>
        </div>
        <%
                }
            }
        %>

    </div>
</main>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.0.2/dist/js/bootstrap.bundle.min.js"></script>
<script>
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
