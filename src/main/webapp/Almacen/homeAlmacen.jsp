<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.example.telitobodeguero.beans.Movimiento" %>
<%@ page import="java.util.ArrayList" %>

<%
    ArrayList<Movimiento> lista = (ArrayList<Movimiento>) request.getAttribute("listaMovs");
    String stockTotal   = String.valueOf(request.getAttribute("stockTotal"));
    String movEntradaHoy= String.valueOf(request.getAttribute("inToday"));
    String movSalidaHoy = String.valueOf(request.getAttribute("outToday"));
    String bajoMin      = String.valueOf(request.getAttribute("min"));
    String ctx = request.getContextPath();
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Inicio · Almacén</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.0.2/dist/css/bootstrap.min.css" rel="stylesheet">

    <style>
        /* ===== Desktop (default) ===== */
        body{ min-height:100vh; background:#f8f9fa; overflow-x:hidden; }

        /* Sidebar fijo (se incluye con jsp:include) */
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

        /* Contenido principal alineado al sidebar */
        .content{
            margin-left:280px; transition:margin-left .25s ease;
            padding:2rem 1rem; min-height:100vh;
        }
        .content.collapsed{ margin-left:80px; }

        /* Contenedor centrado */
        .page-wrap{ max-width:1100px; margin:0 auto; }

        .titulo-principal{ font-size:3rem; }
        .titulo-secundario{ font-size:1.25rem; }

        /* KPIs */
        .kpi-card{ background:#fff; border:1px solid #e9ecef; border-radius:.5rem; padding:1rem; }
        .kpi-title{ font-size:.8rem; color:#6c757d; text-transform:uppercase; letter-spacing:.03em; }
        .kpi-value{ font-size:1.6rem; font-weight:700; }

        /* Topbar (solo móvil) */
        .topbar{
            position:fixed; top:0; left:0; right:0; height:56px;
            background:#fff; border-bottom:1px solid #e5e7eb;
            display:flex; align-items:center; padding:.5rem .75rem; z-index:1100;
        }
        .topbar-logo{ height:28px; width:auto; }

        /* Botón personalizado */
        .btn-personalizado{ background:#1872a2; color:#fff; border-color:transparent; }
        .btn-personalizado:hover{ background:#104b6b; color:#fff; }

        /* ===== Mobile (≤768px): sidebar off-canvas y tabla compacta ===== */
        @media (max-width: 767.98px){
            .sidebar{
                left:-280px; width:280px;
                display:flex !important; /* por si sidebar.jsp trae util classes */
            }
            .sidebar.show{ left:0; }   /* al togglear: muestra */
            .content{ margin-left:0; padding:1rem; padding-top:72px; } /* deja espacio topbar */
            .page-wrap{ max-width:100%; }
            .titulo-principal{ font-size:2rem; text-align:center; }

            .table{ font-size:.9rem; }
            /* Oculta columnas menos críticas para móvil: CANTIDAD y PRODUCTO pueden quedar; puedes ajustar */
            .table thead th:nth-child(3),
            .table tbody td:nth-child(3){ display:none; }      /* TIPO */
            .table thead th:nth-child(5),
            .table tbody td:nth-child(5){ display:none; }      /* CANTIDAD */
        }
    </style>
</head>

<body>

<!-- Sidebar (desktop por defecto; off-canvas en móvil) -->
<jsp:include page="/sidebar.jsp" />

<!-- Topbar solo en móvil -->
<header class="topbar d-md-none">
    <button id="btnMobileMenu" class="btn btn-primary me-2" aria-label="Menú">☰</button>
    <img src="<%=ctx%>/Almacen/img/telitoLogo.png" alt="Telito" class="topbar-logo">
    <span class="ms-2 fw-bold">Almacén</span>
</header>

<!-- Contenido -->
<main id="main" class="content">
    <div class="page-wrap">

        <h1 class="text-primary fw-bold titulo-principal text-center mb-3">BIENVENIDO, ALMACÉN</h1>

        <!-- KPIs -->
        <div class="row g-3 mb-4">
            <div class="col-12 col-md-4">
                <div class="kpi-card">
                    <div class="kpi-title">Stock total</div>
                    <div class="kpi-value"><%= stockTotal %></div>
                </div>
            </div>
            <div class="col-12 col-md-4">
                <div class="kpi-card">
                    <div class="kpi-title">Movimientos hoy</div>
                    <div class="kpi-value">IN: <%= movEntradaHoy %> · OUT: <%= movSalidaHoy %></div>
                </div>
            </div>
            <div class="col-12 col-md-4">
                <div class="kpi-card">
                    <div class="kpi-title">Bajo mínimo</div>
                    <div class="kpi-value text-danger"><%= bajoMin %></div>
                </div>
            </div>
        </div>

        <!-- Tabla de movimientos -->
        <h3 class="text-secondary titulo-secundario mb-2">Movimientos recientes</h3>
        <div class="table-responsive">
            <table class="table table-striped align-middle">
                <thead class="table-light">
                <tr>
                    <th style="width:18%">Fecha</th>
                    <th style="width:15%">Tipo</th>
                    <th style="width:12%">SKU</th>
                    <th style="width:15%">Cantidad</th>
                    <th>Producto</th>
                </tr>
                </thead>
                <tbody>
                <%
                    if (lista != null) {
                        for (Movimiento mov : lista) {
                %>
                <tr>
                    <td><%= mov.getFecha() %></td>
                    <td><%= mov.getTipoMovimiento() %></td>
                    <td><%= mov.getLote()!=null && mov.getLote().getProducto()!=null ? mov.getLote().getProducto().getSku() : "" %></td>
                    <td><%= mov.getCantidad() %></td>
                    <td><%= mov.getLote()!=null && mov.getLote().getProducto()!=null ? mov.getLote().getProducto().getNombre() : "" %></td>
                </tr>
                <%
                        }
                    }
                %>
                </tbody>
            </table>
        </div>

    </div>
</main>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.0.2/dist/js/bootstrap.bundle.min.js"></script>
<script>
    // Desktop: colapsar sidebar si existe botón #btnToggle dentro del sidebar.jsp
    const btn = document.getElementById('btnToggle');
    const sidebar = document.getElementById('sidebar');
    const main = document.getElementById('main');
    if (btn && sidebar && main) {
        btn.addEventListener('click', () => {
            sidebar.classList.toggle('collapsed');
            main.classList.toggle('collapsed');
        });
    }

    // Móvil: abrir/cerrar panel deslizable
    const btnMobile = document.getElementById('btnMobileMenu');
    if (btnMobile && sidebar) {
        btnMobile.addEventListener('click', () => {
            sidebar.classList.toggle('show'); // mueve left:-280px -> 0
        });
    }
</script>
</body>
</html>
