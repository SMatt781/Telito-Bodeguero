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

    <%-- PASO 1: CSS de Font Awesome (para los iconos de las alertas) --%>
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
        /* ... (Se omiten los estilos de navbar del primer JSP
               ya que no estaban siendo usados en el segundo,
               pero se podrían copiar aquí si fuera necesario) ... */

        /* CSS ORIGINAL DEL JSP 2 (Mantenido) */
        .unread{background:#e8f1ff;}

    </style>
</head>
<body>
<jsp:include page="/sidebar.jsp"/>

<%--
  PASO 2: Cambiar "main" por "content" y "container" por "page-wrap"
  para que coincida con el CSS del JSP de Almacén.
--%>
<main class="content" id="main">
    <div class="page-wrap">

        <%-- PASO 3: Usar la cabecera del JSP de Almacén --%>
        <h1 class="fw-bold titulo-principal mb-2">Notificaciones</h1>
        <p>Aquí se agrupan las alertas de cambios de estado y otras notificaciones.</p>

        <%-- Mantenemos el formulario de "Marcar todas" --%>
        <form method="post" action="<%=ctx%>/Admin/Notificaciones" class="m-0 mb-4">
            <input type="hidden" name="action" value="markAll"/>
            <button class="btn btn-sm btn-outline-secondary">Marcar todas como leídas</button>
        </form>


        <%-- PASO 4: Usar el título h2 del JSP de Almacén --%>
        <h2 class="passive-alerts-title">Cambios de estado de Orden de Compra</h2>

        <%-- PASO 5: Reemplazar el list-group por la estructura de div.alert --%>
        <%
            if (notificaciones.isEmpty()) {
        %>
        <%-- Usar el estilo de "no-notificaciones" --%>
        <div class="no-notificaciones">
            <p>¡Todo en orden! No hay notificaciones de este tipo.</p>
        </div>
        <%
        } else {
            for (Map<String,String> n : notificaciones) {
                boolean leido = "1".equals(n.get("leido"));

                // Asignamos un estilo (p.ej. 'info') y un icono
                String cssClass = "alert-info";
                String iconClass = "fas fa-info-circle";
        %>
        <%--
          Se aplica la clase .alert y .unread (si aplica)
          Se mantiene el formulario de "Leída" al final
        --%>
        <div class="alert <%= cssClass %> <%= leido ? "" : "unread" %>">
            <i class="icon <%= iconClass %>"></i>

            <div class="alert-content">
                <strong>OC #<%= n.get("id") %></strong>
                <p><%= n.get("mensaje") %></p>

                <div class="meta">
                    <span class="fecha">Fecha: <%= n.get("ts") %></span>
                </div>
            </div>

            <% if (!leido) { %>
            <form method="post" action="<%=ctx%>/Admin/Notificaciones" class="ms-3">
                <input type="hidden" name="action" value="markOne"/>
                <input type="hidden" name="key" value="<%= n.get("key") %>"/>
                <button class="btn btn-sm btn-outline-primary">Leída</button>
            </form>
            <% } %>
        </div>

        <%
                }
            }
        %>

        <%-- PASO 6: Repetir el proceso para "Otras notificaciones" --%>

        <h2 class="passive-alerts-title">Otras notificaciones</h2>

        <%
            if (otras.isEmpty()) {
        %>
        <div class="no-notificaciones">
            <p>Aún no hay otras notificaciones.</p>
        </div>
        <%
        } else {
            for (Map<String,String> n : otras) {
                // Usamos otro estilo (p.ej. 'success') para diferenciarlas
                String cssClass = "alert-success";
                String iconClass = "fas fa-check-circle";
        %>
        <div class="alert <%= cssClass %>">
            <i class="icon <%= iconClass %>"></i>
            <div class="alert-content">
                <strong><%= n.getOrDefault("titulo","Notificación") %></strong>
                <p><%= n.getOrDefault("mensaje","(sin mensaje)") %></p>
                <%-- Este grupo no tiene meta-data ni botón de "leído" --%>
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
    // Toggle del sidebar
    // Este script funciona igual porque el ID "main" se mantuvo
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
