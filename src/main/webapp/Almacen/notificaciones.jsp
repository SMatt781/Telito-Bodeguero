<%-- PASO 1: Importar las clases correctas para Notificaciones --%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>

<%@ page import="com.example.telitobodeguero.dtos.NotificacionTipo" %>
<%@ page import="com.example.telitobodeguero.dtos.NotificacionAlmDTO" %>

<%

    String ctx = request.getContextPath();
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Notificaciones · Almacén</title>

    <%-- CSS de Bootstrap (de tu plantilla de incidencias) --%>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.0.2/dist/css/bootstrap.min.css" rel="stylesheet">

    <%-- PASO 2: CSS de Font Awesome (para los iconos de las alertas) --%>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.4/css/all.min.css">


    <style>
        /* ===== CSS DE TU PLANTILLA (Sidebar, Content, Responsive) ===== */
        body{ min-height:100vh; background:#f7fafc; overflow-x:hidden; }

        /* Sidebar (incluido vía jsp:include) */
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

        /* Contenido */
        .content{
            margin-left:280px; transition:margin-left .25s ease;
            padding:2rem 1rem; min-height:100vh;
        }
        .content.collapsed{ margin-left:80px; }

        /* Contenedor centrado */
        .page-wrap{ max-width:1100px; margin:0 auto; }

        .titulo-principal{ font-size:2.25rem; color: #2d3748; }

        /* Topbar (móvil) */
        .topbar{
            position:fixed; top:0; left:0; right:0; height:56px;
            background:#fff; border-bottom:1px solid #e5e7eb;
            display:flex; align-items:center; padding:.5rem .75rem; z-index:1100;
        }
        .topbar-logo{ height:28px; width:auto; }

        /* ===== Mobile (≤768px) ===== */
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

        /* ===== NUEVO CSS (Estilos de las Alertas) ===== */

        /* Texto 'Notifications are grouped...' */
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

        /* Estilo base para todas las alertas */
        .alert {
            display: flex;
            align-items: flex-start;
            padding: 16px; /* 1rem */
            margin-bottom: 16px;
            border-radius: 6px; /* Bordes redondeados */
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

        .alert .close-btn {
            font-size: 1.5em;
            color: #a0aec0;
            cursor: pointer;
            line-height: 1;
            margin-left: 15px;
            font-weight: 400;
        }
        .alert .close-btn:hover { color: #2d3748; }

        /* Variantes de Color */
        .alert-success { background-color: #f0fdf4; border-top-color: #22c55e; }
        .alert-success .icon, .alert-success .alert-content strong { color: #166534; }
        .alert-error { background-color: #fef2f2; border-top-color: #ef4444; }
        .alert-error .icon, .alert-error .alert-content strong { color: #991b1b; }
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
        /* ===== CSS PARA EL NAVBAR (Móvil y Escritorio) ===== */

        /* Topbar (móvil) */
        .topbar{
            position:fixed; top:0; left:0; right:0; height:56px;
            background:#fff; border-bottom:1px solid #e5e7eb;
            display:flex; align-items:center;
            padding:.5rem .75rem; z-index:1100;
            justify-content: space-between;
        }

        /* Header de Escritorio */
        .desktop-header {
            display: flex;
            justify-content: flex-end; /* Alinea todo a la derecha */
            align-items: center;
            padding: 1rem 2rem;
            background: #fff;
            border-bottom: 1px solid #e5e7eb;
            height: 70px;
        }

        /* Botón de icono (para campana, menú móvil) */
        .btn-icon {
            border: none; background: transparent;
            font-size: 1.3rem; color: #718096;
            cursor: pointer;
        }
        .btn-icon:hover { color: #2d3748; }

        /* Avatar circular con iniciales */
        .user-avatar-initials {
            display: flex; align-items: center; justify-content: center;
            width: 40px; height: 40px;
            border-radius: 50%;
            background-color: #e0e7ff; color: #3730a3;
            font-weight: 600; font-size: 1rem;
        }

        /* Avatar móvil más pequeño */
        .topbar .user-avatar-initials {
            width: 32px; height: 32px; font-size: 0.9rem;
        }

        /* Link de perfil de usuario */
        .user-profile-link {
            display: flex; align-items: center;
            text-decoration: none; /* <-- ESTO QUITA EL SUBRAYADO AZUL */
            color: inherit;
        }
        /* Quita el color azul de los links dentro */
        .user-profile-link:hover,
        .user-profile-link strong,
        .user-profile-link small {
            text-decoration: none; /* <-- QUITA EL SUBRAYADO AZUL */
        }

        .user-info { line-height: 1.3; }
        .user-info strong {
            display: block; font-size: 0.9rem;
            color: #374151; /* Color de texto normal */
        }
        .user-info small {
            font-size: 0.8rem; color: #718096;
        }

    </style>
</head>
<body>

<jsp:include page="/sidebar.jsp" />

<header class="topbar d-md-none">
    <button id="btnMobileMenu" class="btn btn-primary me-2" aria-label="Menú">☰</button>
    <%-- Asumo que la ruta de tu logo es esta --%>
    <img src="<%=ctx%>/Almacen/img/telitoLogo.png" alt="Telito" class="topbar-logo">
    <span class="ms-2 fw-bold">Notificaciones</span> <%-- Título cambiado --%>
</header>

<main id="main" class="content">

    <div class="page-wrap">

        <h1 class="fw-bold titulo-principal mb-2">Notificaciones</h1>
        <p>Aquí se agrupan las alertas pasivas, de acción y notificaciones.</p>

        <h2 class="passive-alerts-title">Alertas Pasivas</h2>

        <%--
          PASO 7: Insertar la lógica de Notificaciones.
          Este es el código (scriptlet + HTML) que hicimos antes.
          Reemplaza la <table> de 'incidencias.jsp'.
        --%>
        <%
            // 1. Obtenemos la lista que el Servlet nos mandó
            List<NotificacionAlmDTO> notificaciones =
                    (List<NotificacionAlmDTO>) request.getAttribute("listaNotificaciones");

            // 2. Verificamos si la lista está vacía o es nula
            if (notificaciones == null || notificaciones.isEmpty()) {
        %>

        <div class="no-notificaciones">
            <p>¡Todo en orden! No tienes notificaciones nuevas.</p>
        </div>

        <%
        } else {
            // 3. Si la lista SÍ tiene cosas, la recorremos con un for de Java
            for (NotificacionAlmDTO noti : notificaciones) {

                String cssClass = "";
                String iconClass = "";

                switch (noti.getTipo()) {
                    case ORDEN_LLEGADA:
                        cssClass = "alert-success";
                        iconClass = "fas fa-check-circle"; // Icono de check
                        break;
                    case ORDEN_RETRASO:
                        cssClass = "alert-error";
                        iconClass = "fas fa-times-circle"; // Icono de error
                        break;
                    case BLOQUE_CRITICO:
                        cssClass = "alert-warning";
                        iconClass = "fas fa-exclamation-triangle"; // Icono de advertencia
                        break;
                    case INCIDENCIA_NUEVA:
                        cssClass = "alert-info";
                        iconClass = "fas fa-info-circle"; // Icono de info
                        break;
                    default:
                        cssClass = "alert-info";
                        iconClass = "fas fa-bell";
                        break;
                }
        %>

        <div class="alert <%= cssClass %>">
            <i class="icon <%= iconClass %>"></i>

            <div class="alert-content">
                <strong><%= noti.getTitulo() %></strong>
                <p><%= noti.getMensaje() %></p>

                <div class="meta">
                    <span class="fecha">Fecha: <%= noti.getFechaRelevante() %></span>
                    <span class="zona">Zona: <%= noti.getZonaNombre() %></span>
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


    const btnMobile = document.getElementById('btnMobileMenu');
    if (btnMobile && sidebar) {
        btnMobile.addEventListener('click', () => {
            sidebar.classList.toggle('show');
        });
    }
</script>

</body>
</html>