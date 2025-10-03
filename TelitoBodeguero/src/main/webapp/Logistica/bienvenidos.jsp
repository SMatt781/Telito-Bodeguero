<%@ page import="java.util.ArrayList" %>
<%@ page import="com.example.telitobodeguero.beans.Movimiento" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    ArrayList<Movimiento> listaMovimientos =
            (ArrayList<Movimiento>) request.getAttribute("reporteMovimientos");
    Integer totalMovimientosObj = (Integer) request.getAttribute("totalMovimientos");
    int totalMovimientos = (totalMovimientosObj != null) ? totalMovimientosObj : 0;
    int movimientosMostrados = (listaMovimientos != null) ? listaMovimientos.size() : 0;
%>
<!doctype html>
<html lang="es">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Telito - Logística</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.0.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <style>
        /* ===== LAYOUT base (sidebar colapsable) ===== */
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

        /* ===== Estilos que pediste “como antes” ===== */
        /* Cards verdes claras */
        .stat-card{
            background:#a8e6a1; color:#0b2e18;
            border:0; border-radius:24px; box-shadow:0 6px 16px rgba(0,0,0,.08);
        }
        .stat-card .card-body{ padding:18px 22px; }
        .stat-card .card-title{ font-weight:700; margin-bottom:.25rem; }
        .stat-card .card-text{ font-size:1.75rem; font-weight:800; margin:0; }

        /* Panel gráfico blanco con borde gris */
        .graph-box{
            background:#fff; border:1.5px solid rgba(0,0,0,.22);
            border-radius:18px; padding:18px; box-shadow:0 8px 24px rgba(0,0,0,.06);
        }
        .chart-frame{
            height:220px; border:2px solid rgba(0,0,0,.45);
            border-radius:22px; display:flex; align-items:center; justify-content:center;
            color:#6b707c; font-weight:700;
        }
        .legend-line{ display:flex; align-items:center; gap:12px; font-weight:700; color:#2a2e36; margin-top:12px; }
        .pill{ display:inline-block; width:48px; height:16px; border-radius:999px; }
        .pill.in{ background:#47c776; }  /* Entrada */
        .pill.out{ background:#d23c3c; } /* Salida */

        /* Tabla en tonos grises (como la original) */
        .tabla-wrap{ background:#fff; border-radius:16px; padding:12px; box-shadow:0 6px 16px rgba(0,0,0,.06); }
        .table thead th{ position:sticky; top:0; background:#e9ecef; }

        /* Cabecera */
        h1{ font-weight:800; color:#2e63f5; letter-spacing:.3px; }
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
            <a class="nav-link" href="<%= request.getContextPath() %>/Bienvenidos">
                <span class="text-label">Inicio Logística</span>
            </a>
        </li>
        <li class="nav-item mb-1">
            <a class="nav-link" href="<%= request.getContextPath() %>/StockBajo_OrdenCompra?action=stock">
                <span class="text-label">Productos con stock bajo</span>
            </a>
        </li>
        <li class="nav-item mb-1">
            <a class="nav-link" href="<%= request.getContextPath() %>/StockBajo_OrdenCompra?action=ordenes">
                <span class="text-label">Órdenes de compra</span>
            </a>
        </li>
        <li class="nav-item mb-1">
            <a class="nav-link" href="<%= request.getContextPath() %>/ReporteMovimientos">
                <span class="text-label">Reportes de movimientos</span>
            </a>
        </li>
        <li class="nav-item mb-1">
            <a class="nav-link" href="<%= request.getContextPath() %>/StockBajo_OrdenCompra?action=crear">
                <span class="text-label">Generar nueva orden</span>
            </a>
        </li>
        <li class="nav-item mb-1">
            <a class="nav-link" href="<%= request.getContextPath() %>/ListaProductos">
                <span class="text-label">Productos</span>
            </a>
        </li>

        <div class="spacer"></div>

        <!-- Cerrar sesión al final -->
        <li class="nav-item mt-auto mb-3">
            <a class="nav-link" href="<%= request.getContextPath() %>/index.jsp">
                <span class="text-label">Cerrar sesión</span>
            </a>
        </li>
    </ul>
</aside>

<!-- ===== Main ===== -->
<main class="main" id="main">
    <!-- Botón atrás como estaba -->
    <div class="mb-2">
        <button type="button" class="btn btn-outline-secondary btn-sm" onclick="history.back()">
            &larr; Volver
        </button>
    </div>

    <h1 class="mb-3">BIENVENIDO A LOGÍSTICA!</h1>
    <hr>

    <!-- KPIs (mismos colores de antes) -->
    <div class="container-fluid mb-3">
        <div class="row g-3 justify-content-center">
            <div class="col-auto">
                <div class="card stat-card h-80">
                    <div class="card-body text-center">
                        <h5 class="card-title">Órdenes en tránsito</h5>
                        <p class="card-text">
                            <span class="fs-2 fw-bold text-primary">${ordenesEnTransito}</span>
                        </p>
                    </div>
                </div>
            </div>
            <div class="col-auto">
                <div class="card stat-card h-80">
                    <div class="card-body text-center">
                        <h5 class="card-title">Alertas de stock bajo</h5>
                        <p class="card-text">
                            <span class="fs-2 fw-bold text-danger">${alertasStockBajo}</span>
                        </p>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <!-- Panel gráfico (colores previos) -->
    <div class="graph-box mb-4">
        <div class="d-flex align-items-center justify-content-between">
            <h6 class="m-0 fw-bold text-secondary text-uppercase">Gráfico entrada/salida</h6>
            <a class="btn btn-sm btn-outline-primary"
               href="<%= request.getContextPath() %>/StockBajo_OrdenCompra?action=stock"
               style="white-space: nowrap;">
                Ver productos con stock bajo
            </a>
        </div>

        <div class="chart-frame mt-3">Gráfico de barras</div>

        <div class="legend-line">
            <span>Leyenda:</span>
            <span class="pill in"></span> <span>Entrada</span>
            <span class="pill out ms-4"></span> <span>Salida</span>
        </div>
    </div>

    <!-- Tabla en gris (como la original) -->
    <div class="tabla-wrap">
        <div class="table-responsive">
            <table class="table table-secondary table-striped align-middle mb-0">
                <thead>
                <tr>
                    <th>Fecha</th>
                    <th>Movimiento</th>
                    <th>Producto</th>
                    <th>Cantidad</th>
                    <th>Zona</th>
                </tr>
                </thead>
                <tbody>
                <%
                    if (listaMovimientos != null && !listaMovimientos.isEmpty()) {
                        for (Movimiento mov : listaMovimientos) {
                            String nombreProducto = (mov.getProducto()!=null && mov.getProducto().getNombre()!=null)
                                    ? mov.getProducto().getNombre() : "N/A";
                            String nombreZona = (mov.getZona()!=null && mov.getZona().getNombre()!=null)
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
                    <td colspan="5" class="text-center">No se encontraron movimientos recientes.</td>
                </tr>
                <% } %>
                </tbody>
            </table>
        </div>

        <div class="d-flex justify-content-between align-items-center mt-2">
            <small class="text-muted">
                Mostrando <%= movimientosMostrados %> de <%= totalMovimientos %> movimientos
            </small>

            <div class="d-flex gap-2">
                <a href="ReporteMovimientos?action=list" class="btn btn-outline-secondary btn-sm">
                    Ver reporte completo
                </a>
                <button class="btn btn-outline-primary btn-sm">Exportar CSV</button>
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
    btn.addEventListener('click', () => {
        sidebar.classList.toggle('collapsed');
        main.classList.toggle('collapsed');
    });
</script>
</body>
</html>
