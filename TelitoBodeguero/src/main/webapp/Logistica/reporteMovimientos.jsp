<%@ page import="java.util.ArrayList" %>
<%@ page import="com.example.telitobodeguero.beans.Movimiento" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%
    // Datos recibidos del servlet
    ArrayList<Movimiento> listaMovimientos =
            (ArrayList<Movimiento>) request.getAttribute("listaMovimientos");

    Integer totalEntradasObj = (Integer) request.getAttribute("totalEntradas");
    Integer totalSalidasObj  = (Integer) request.getAttribute("totalSalidas");

    int totalEntradas = (totalEntradasObj != null) ? totalEntradasObj : 0;
    int totalSalidas  = (totalSalidasObj  != null) ? totalSalidasObj  : 0;

    // (Opcionales) filtros que pudieras mostrar
    String fechaDesde = (String) request.getAttribute("fechaDesde");
    String fechaHasta = (String) request.getAttribute("fechaHasta");
    String movimiento = (String) request.getAttribute("movimiento");
    String producto   = (String) request.getAttribute("producto");
    String zona       = (String) request.getAttribute("zona");

    String ctx = request.getContextPath();
%>
<!doctype html>
<html lang="es">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Telito - Reportes de movimientos</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.0.2/dist/css/bootstrap.min.css" rel="stylesheet">

    <style>
        /* ===== Sidebar deslizante ===== */
        body{ margin:0; background:#f8f9fa; }
        .sidebar{
            position:fixed; inset:0 auto 0 0;           /* top:0; left:0; bottom:0 */
            width:280px; background:#212529; color:#fff;
            z-index:1000; transition:width .25s ease; overflow-y:auto;
        }
        .sidebar.collapsed{ width:80px; }
        .sidebar .brand{ padding:1rem 1.25rem; display:flex; align-items:center; gap:.75rem; }
        .sidebar .brand .toggle{ border:0; background:#0d6efd; color:#fff; padding:.5rem .6rem; border-radius:.5rem; }
        .sidebar .nav-link{ color:#d6d6d6; }
        .sidebar .nav-link:hover,.sidebar .nav-link:focus{ background:#0d6efd; color:#fff; }
        .sidebar .spacer{ height:1px; background:#343a40; margin:.5rem 0; }
        .sidebar.collapsed .text-label{ display:none; }

        /* ===== Main que se desplaza según la barra ===== */
        .main{
            margin-left:280px; transition:margin-left .25s ease;
            min-height:100vh; padding:2rem;
        }
        h1 {
            font-family: 'Poppins', Inter, sans-serif;
            font-weight: 800;
            color: #2e63f5;     /* Azul corporativo */
            letter-spacing: .3px;
            margin-bottom: 1.25rem;
            text-transform: uppercase;
        }

        .main.collapsed{ margin-left:80px; }

        /* ===== Cards y tabla (mantener estilos grises) ===== */
        .card-kpi{ border-radius:1rem; box-shadow:0 6px 14px rgba(0,0,0,.08); }
        .table-wrap{ background:#fff; border-radius:16px; padding:12px; box-shadow:0 6px 16px rgba(0,0,0,.06); }
        .table thead th{ position:sticky; top:0; background:#e9ecef; }
    </style>
</head>
<body>

<!-- ===== Sidebar ===== -->
<aside class="sidebar" id="sidebar">
    <div class="brand">
        <button class="toggle" id="btnToggle" aria-label="Alternar menú">&#9776;</button>
        <span class="h5 mb-0 text-label">Telito - Bodeguero</span>
    </div>
    <hr class="text-secondary my-2">

    <ul class="nav nav-pills flex-column px-2">
        <li class="nav-item mb-1">
            <a class="nav-link" href="${sessionScope.homeUrl}"><span class="text-label">Inicio</span></a>
        </li>


        <div class="spacer"></div>

        <!-- Cerrar sesión al final -->
        <li class="nav-item mt-auto mb-3">
            <a class="nav-link" href="<%= ctx %>/index.jsp"><span class="text-label">Cerrar sesión</span></a>
        </li>
    </ul>
</aside>

<!-- ===== Contenido ===== -->
<main class="main" id="main">
    <!-- Botón Volver arriba -->
    <div class="mb-2">
        <button type="button" class="btn btn-outline-secondary btn-sm" onclick="history.back()">
            &larr; Volver
        </button>
    </div>

    <div class="container my-2">
        <h1 class="mb-3 text-uppercase">Reportes de movimientos</h1>

        <!-- KPIs -->
        <div class="row g-3 mb-3">
            <div class="col-12 col-sm-6 col-lg-3">
                <div class="card card-kpi text-center">
                    <div class="card-body">
                        <h6 class="card-title fw-bold">Entradas</h6>
                        <div class="fs-4 fw-bold"><%= totalEntradas %></div>
                    </div>
                </div>
            </div>
            <div class="col-12 col-sm-6 col-lg-3">
                <div class="card card-kpi text-center">
                    <div class="card-body">
                        <h6 class="card-title fw-bold">Salidas</h6>
                        <div class="fs-4 fw-bold"><%= totalSalidas %></div>
                    </div>
                </div>
            </div>
        </div>

        <!-- Tabla gris -->
        <div class="table-wrap">
            <div class="table-responsive">
                <table class="table table-secondary table-striped align-middle mb-0">
                    <thead>
                    <tr>
                        <th>Fecha</th>
                        <th>Movimiento</th>
                        <th>Producto</th>
                        <th>Cant.</th>
                        <th>Zona</th>
                    </tr>
                    </thead>
                    <tbody>
                    <%
                        if (listaMovimientos != null && !listaMovimientos.isEmpty()) {
                            for (Movimiento mov : listaMovimientos) {
                                String nombreProducto = (mov.getProducto() != null && mov.getProducto().getNombre() != null)
                                        ? mov.getProducto().getNombre() : "N/A";
                                String nombreZona = (mov.getZona() != null && mov.getZona().getNombre() != null)
                                        ? mov.getZona().getNombre() : "N/A";
                    %>
                    <tr>
                        <td><%= mov.getFecha() %></td>
                        <td><%= mov.getTipoMovimiento() %></td>
                        <td><%= nombreProducto %></td>
                        <td><%= mov.getCantidad() %></td>
                        <td><%= nombreZona %></td>
                    </tr>
                    <%
                        }
                    } else {
                    %>
                    <tr>
                        <td colspan="5" class="text-center">No se encontraron movimientos.</td>
                    </tr>
                    <% } %>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
</main>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.0.2/dist/js/bootstrap.bundle.min.js"></script>
<script>
    // Toggle sidebar
    const btn = document.getElementById('btnToggle');
    const sidebar = document.getElementById('sidebar');
    const main = document.getElementById('main');
    btn && btn.addEventListener('click', () => {
        sidebar.classList.toggle('collapsed');
        main.classList.toggle('collapsed');
    });
</script>
</body>
</html>
