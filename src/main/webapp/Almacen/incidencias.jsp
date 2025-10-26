<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="com.example.telitobodeguero.beans.Incidencia" %>
<jsp:useBean id="listaIncidencias" scope="request" type="java.util.ArrayList<com.example.telitobodeguero.beans.Incidencia>"/>

<%
    String ctx = request.getContextPath();
    String statusMessage = (String) request.getAttribute("statusMessage");
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Incidencias · Almacén</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.0.2/dist/css/bootstrap.min.css" rel="stylesheet">

    <style>
        /* ===== Desktop (default) ===== */
        body{ min-height:100vh; background:#f8f9fa; overflow-x:hidden; }

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

        .titulo-principal{ font-size:2.25rem; }
        .btn-personalizado{ background:#1872a2; color:#fff; border-color:transparent; }
        .btn-personalizado:hover{ background:#104b6b; color:#fff; }

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

            .table{ font-size:.9rem; }
            /* Ocultar columnas menos críticas en móvil (ajusta si quieres) */
            .table thead th:nth-child(3),
            .table tbody td:nth-child(3){ display:none; }   /* Nombre */
            .table thead th:nth-child(7),
            .table tbody td:nth-child(7){ display:none; }   /* Descripción */
        }
    </style>
</head>
<body>

<!-- Sidebar (desktop/off-canvas móvil) -->
<jsp:include page="/sidebar.jsp" />

<!-- Topbar móvil -->
<header class="topbar d-md-none">
    <button id="btnMobileMenu" class="btn btn-primary me-2" aria-label="Menú">☰</button>
    <img src="<%=ctx%>/Almacen/img/telitoLogo.png" alt="Telito" class="topbar-logo">
    <span class="ms-2 fw-bold">Incidencias</span>
</header>

<!-- Contenido -->
<main id="main" class="content">
    <div class="page-wrap">

        <h1 class="text-primary fw-bold titulo-principal mb-3">Lista de incidencias</h1>

        <% if (statusMessage != null && !statusMessage.isEmpty()) {
            String[] parts = statusMessage.split("\\|", 2);
            String status = parts.length>0 ? parts[0] : "success";
            String msg    = parts.length>1 ? parts[1] : "";
            String alertClass = "success".equalsIgnoreCase(status) ? "alert-success" : "alert-danger";
        %>
        <div class="alert <%=alertClass%> alert-dismissible fade show" role="alert">
            <%= msg %>
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        </div>
        <% } %>

        <div class="table-responsive">
            <table class="table table-striped align-middle">
                <thead class="table-light">
                <tr>
                    <th style="width:6%">ID</th>
                    <th style="width:12%">SKU</th>
                    <th style="width:20%">Nombre</th>
                    <th style="width:10%">Tipo</th>
                    <th style="width:8%">Cantidad</th>
                    <th style="width:8%">Lote</th>
                    <th>Descripción</th>
                    <th style="width:10%">Estado</th>
                    <th style="width:16%">Acciones</th>
                </tr>
                </thead>
                <tbody>
                <% for (Incidencia inc : listaIncidencias) { %>
                <tr>
                    <td><%= inc.getIdIncidencia() %></td>
                    <td><%= inc.getProducto().getSku() %></td>
                    <td><%= inc.getProducto().getNombre() %></td>
                    <td><%= inc.getTipoIncidencia() %></td>
                    <td><%= inc.getCantidad() %></td>
                    <td><%= inc.getLote_idLote() %></td>
                    <td><%= inc.getDescripcion() %></td>
                    <td><%= inc.getEstado() %></td>
                    <td>
                        <div class="d-flex gap-3">
                            <form method="post" action="<%=ctx%>/IncidenciaAlmServlet" class="m-0 p-0">
                                <input type="hidden" name="accion" value="mantener">
                                <input type="hidden" name="idInc" value="<%=inc.getIdIncidencia()%>">
                                <button type="submit" class="btn btn-link p-0 m-0" style="text-decoration:none;">Mantener</button>
                            </form>

                            <form method="post" action="<%=ctx%>/IncidenciaAlmServlet" class="m-0 p-0">
                                <input type="hidden" name="accion" value="quitar">
                                <input type="hidden" name="idInc" value="<%=inc.getIdIncidencia()%>">
                                <input type="hidden" name="tipo" value="OUT">
                                <input type="hidden" name="cantidad" value="<%=inc.getCantidad()%>">
                                <input type="hidden" name="idLote" value="<%=inc.getLote_idLote()%>">
                                <button type="submit" class="btn btn-link p-0 m-0 text-danger" style="text-decoration:none;">Quitar</button>
                            </form>
                        </div>
                    </td>
                </tr>
                <% } %>
                </tbody>
            </table>
        </div>

    </div>
</main>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.0.2/dist/js/bootstrap.bundle.min.js"></script>
<script>
    // Desktop: colapsar sidebar (si tu sidebar.jsp trae #btnToggle)
    const btn = document.getElementById('btnToggle');
    const sidebar = document.getElementById('sidebar');
    const main = document.getElementById('main');
    if (btn && sidebar && main) {
        btn.addEventListener('click', () => {
            sidebar.classList.toggle('collapsed');
            main.classList.toggle('collapsed');
        });
    }

    // Móvil: mostrar/ocultar sidebar como off-canvas
    const btnMobile = document.getElementById('btnMobileMenu');
    if (btnMobile && sidebar) {
        btnMobile.addEventListener('click', () => {
            sidebar.classList.toggle('show');
        });
    }
</script>
</body>
</html>
