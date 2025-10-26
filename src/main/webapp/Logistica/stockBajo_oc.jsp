<%@ page import="java.util.ArrayList" %>
<%@ page import="com.example.telitobodeguero.beans.OrdenCompra" %>
<%@ page import="com.example.telitobodeguero.beans.Producto" %>
<%@ page import="com.example.telitobodeguero.beans.Lote" %>
<%@ page import="com.example.telitobodeguero.beans.Zonas" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%
    // Parámetro de estado (Para la sección de Órdenes de Compra si aplica)
    String estadoActual = request.getParameter("estado");
    if (estadoActual == null) estadoActual = "";

    String ctx = request.getContextPath();

    // Lista de Órdenes de Compra (Se mantiene para contexto, si es necesario)
    ArrayList<OrdenCompra> listaOrdenCompra = (ArrayList<OrdenCompra>) request.getAttribute("listaOrdenCompra");

    // Datos de Stock Bajo (Asegúrate de que el DAO los esté devolviendo)
    // 1. Lista de Productos con stock bajo (Viene del ProductoDaoLogis.obtenerTop5ProductosStockBajo())
    ArrayList<Producto> listaTop5StockBajo = (ArrayList<Producto>) request.getAttribute("listaTop5StockBajo");

    // 2. Total de productos bajo stock mínimo (Viene del ProductoDaoLogis.contarTotalProductosStockBajo())
    Integer totalStockBajoObj = (Integer) request.getAttribute("totalStockBajo");
    int totalStockBajo = (totalStockBajoObj != null) ? totalStockBajoObj : 0;
%>

<!doctype html>
<html lang="es">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">

    <title>Telito - Logística</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.0.2/dist/css/bootstrap.min.css" rel="stylesheet">

    <style>
        /* ===== Sidebar deslizante (colapsable) ===== */
        body{ margin:0; background:#f8f9fa; }
        .sidebar{
            position:fixed; inset:0 auto 0 0;             /* top:0;left:0;bottom:0 */
            width:280px; background:#212529; color:#fff;
            z-index:1000; transition:width .25s ease; overflow-y:auto;
        }
        .sidebar.collapsed{ width:80px; }
        .sidebar .brand{
            padding:1rem 1.25rem; display:flex; align-items:center; gap:.75rem;
        }
        .sidebar .brand .toggle{
            border:0; background:#0d6efd; color:#fff; padding:.5rem .6rem; border-radius:.5rem;
        }
        .sidebar .nav-link{ color:#d6d6d6; }
        .sidebar .nav-link:hover,.sidebar .nav-link:focus{ background:#0d6efd; color:#fff; }
        .sidebar .spacer{ height:1px; background:#343a40; margin:.5rem 0; }
        .sidebar.collapsed .text-label{ display:none; }

        /* Main que se desplaza según el ancho de la barra */
        .main{
            margin-left:280px; transition:margin-left .25s ease;
            min-height:100vh; padding:2rem;
        }
        .main.collapsed{ margin-left:80px; }

        /* Detalles visuales existentes */
        .nav-link.text-white:hover { background-color:#0d6efd; color:#fff !important; }
        :root{ --brand:#2e63f5; }
        h1{
            font-family:'Poppins', Inter, system-ui, -apple-system, Segoe UI, Roboto, Helvetica, Arial, sans-serif;
            font-weight:800; color:var(--brand); letter-spacing:.3px; margin-bottom:1.25rem;
        }
        .stat-card{
            background:#a8e6a1; color:#0b2e18;
            border:0; border-radius:24px; box-shadow:0 6px 16px rgba(0,0,0,.08);
        }
        .stat-card .card-body{ padding:18px 22px; }
        .stat-card .card-title{ font-weight:700; margin-bottom:.25rem; }
        .stat-card .card-text{ font-size:1.75rem; font-weight:800; margin:0; }

        .chart-panel{
            background:#fff; border:1.5px solid rgba(0,0,0,.22);
            border-radius:18px; padding:18px; box-shadow:0 8px 24px rgba(0,0,0,.06);
        }
        .chart-frame{
            height:220px; border:2px solid rgba(0,0,0,.45);
            border-radius:22px; display:flex; align-items:center; justify-content:center;
            color:#6b707c; font-weight:700;
        }
        .legend-line{ display:flex; align-items:center; gap:12px; font-weight:700; color:#2a2e36; margin-top:12px; }
        .pill{display:inline-block;width:48px;height:16px;border-radius:999px}
        .pill.in{background:#47c776}   /* Entrada */
        .pill.out{background:#d23c3c}  /* Salida */
    </style>
</head>
<body>

<!-- ===== Sidebar ===== -->
<jsp:include page="/sidebar.jsp" />

<!-- ===== Contenido principal ===== -->
<main class="main" id="main">
    <!-- Botón volver arriba, como antes -->
    <div class="mb-2">
        <button type="button" class="btn btn-outline-secondary btn-sm" onclick="history.back()">
            &larr; Volver
        </button>
    </div>

    <div class="container my-4" id="stock-bajo">
        <!-- INICIO: Nueva Sección de Alerta Compacta y Botón -->
        <div class="d-flex align-items-center justify-content-between mb-3">
            <div class="alert alert-danger m-0 py-2 px-3 rounded-pill d-inline-flex align-items-center gap-2 fw-bold">
                <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="text-white-50">
                    <path d="M10.29 3.86L1.82 18a2 2 0 001.71 3h16.94a2 2 0 001.71-3L13.71 3.86a2 2 0 00-3.42 0z"/>
                    <line x1="12" y1="9" x2="12" y2="13"/>
                    <line x1="12" y1="17" x2="12.01" y2="17"/>
                </svg>
                Alerta de stock bajo (<%= totalStockBajo %> productos)
            </div>
            <a class="btn btn-sm btn-outline-danger"
               href="<%= ctx %>/ListaProductos?action=stock"
               style="white-space: nowrap;">
                Ver todos los productos
            </a>
        </div>
        <h5 class="text-secondary mb-3">Productos con menor stock restante:</h5>

        <!-- Tabla de Top 5 productos con stock bajo (Estructura: Código, Nombre, Stock, Mínimo, Zona) -->
        <div class="table-responsive mb-5 shadow rounded">
            <table class="table table-bordered table-sm align-middle text-center bg-white">
                <thead class="bg-danger text-white">
                <tr>
                    <th scope="col">Código</th>
                    <th scope="col">Nombre</th>
                    <th scope="col">Stock (Actual)</th>
                    <th scope="col">Mínimo Requerido</th>
                    <th scope="col">Zona</th>
                </tr>
                </thead>
                <tbody>
                <%
                    // Usamos las variables recuperadas en la cabecera
                    if (listaTop5StockBajo != null && !listaTop5StockBajo.isEmpty()) {
                        for (Producto p : listaTop5StockBajo) {
                            // p.getStock() trae el stock calculado por zona
                            // p.getStockMinimo() trae el umbral de alerta
                            String zonaNombre = (p.getZona() != null) ? p.getZona().getNombre() : "N/A";
                %>
                <tr class="table-danger fw-bold">
                    <td><%= p.getSku() != null ? p.getSku() : p.getIdProducto() %></td>
                    <td class="text-start"><%= p.getNombre() %></td>
                    <td>
                        <!-- Resaltar el stock bajo actual -->
                        <span class="badge bg-dark p-2"><%= p.getStock() %></span>
                    </td>
                    <td>
                        <!-- Mostrar el stock mínimo para comparación -->
                        <%= p.getStockMinimo() %>
                    </td>
                    <td><%= zonaNombre %></td>
                </tr>
                <%
                    }
                } else {
                %>
                <tr>
                    <td colspan="5" class="text-center text-success fw-bold">
                        <i class="fas fa-check-circle"></i> ¡Todo el inventario está por encima del stock mínimo!
                    </td>
                </tr>
                <% } %>
                </tbody>
            </table>
        </div>

        <div>

            <small class="text-muted">Mostrando <%= totalStockBajo %> productos</small>

            <hr>

        </div>

        <!-- Órdenes de compra -->
        <div class="d-flex align-items-center justify-content-between mb-2">
            <h5 class="m-0 fw-bold">Órdenes de compra</h5>
        </div>
        <hr>

        <div class="d-flex flex-wrap justify-content-between align-items-center mb-3 gap-2">
            <div class="d-flex flex-wrap align-items-center gap-2 mb-2" aria-label="Filtrar por estado">
                <a href="<%=ctx%>/StockBajo_OrdenCompra"
                   class="btn btn-sm btn-outline-dark rounded-pill px-3 <%= estadoActual.isEmpty() ? "active" : "" %>">
                    TODOS
                </a>
                <a href="<%=ctx%>/StockBajo_OrdenCompra?estado=enviada"
                   class="btn btn-sm btn-outline-info rounded-pill px-3 <%= "enviada".equalsIgnoreCase(estadoActual) ? "active" : "" %>">
                    Enviada
                </a>
                <a href="<%=ctx%>/StockBajo_OrdenCompra?estado=en%20transito"
                   class="btn btn-sm btn-outline-success rounded-pill px-3 <%= "en transito".equalsIgnoreCase(estadoActual) ? "active" : "" %>">
                    En Tránsito
                </a>
                <a href="<%=ctx%>/StockBajo_OrdenCompra?estado=recibida"
                   class="btn btn-sm btn-outline-secondary rounded-pill px-3 <%= "recibida".equalsIgnoreCase(estadoActual) ? "active" : "" %>">
                    Recibida
                </a>
                <a href="<%=ctx%>/StockBajo_OrdenCompra?estado=completada"
                   class="btn btn-sm btn-outline-secondary rounded-pill px-3 <%= "completada".equalsIgnoreCase(estadoActual) ? "active" : "" %>">
                    Completada
                </a>
            </div>

            <form method="GET" action="StockBajo_OrdenCompra" class="ms-auto" role="search" style="max-width:320px;">
                <input type="hidden" name="action" value="list">
                <%
                    String estadoActivo = (String) request.getAttribute("estadoFiltro");
                    if (estadoActivo != null && !estadoActivo.isBlank()) {
                %>
                <input type="hidden" name="estado" value="<%= estadoActivo %>">
                <% } %>

                <div class="input-group input-group-sm">
                    <span class="input-group-text">Proveedor</span>
                    <input type="search" class="form-control" name="busquedaProveedor"
                           placeholder="Buscar proveedor..."
                           value="<%= request.getAttribute("terminoBusquedaProveedor") != null ? request.getAttribute("terminoBusquedaProveedor") : "" %>"
                           aria-label="Buscar proveedor">
                    <button class="btn btn-outline-secondary" type="submit">Buscar</button>
                </div>
            </form>
        </div>

        <div class="table-responsive">
            <table class="table table-secondary table-striped align-middle">
                <thead>
                <tr>
                    <th>Código OC</th>
                    <th>Proveedor</th>
                    <th>Producto</th>
                    <th>Zona</th>         <!-- <--- NUEVA COLUMNA DE ENCABEZADO -->
                    <th>Fecha llegada</th>
                    <th>Cantidad</th>
                    <th>Estado</th>
                    <th>Generar</th>
                </tr>
                </thead>
                <tbody>
                <%
                    if (listaOrdenCompra != null && !listaOrdenCompra.isEmpty()) {
                        for (OrdenCompra oc : listaOrdenCompra) {
                            String proveedor = oc.getNombreProveedor() != null ? oc.getNombreProveedor() : "N/A";
                            String producto  = (oc.getProducto() != null && oc.getProducto().getNombre() != null)
                                    ? oc.getProducto().getNombre() : "N/A";
                            // Aseguramos que la Zona se muestre correctamente
                            String zona      = (oc.getZona() != null && oc.getZona().getNombre() != null)
                                    ? oc.getZona().getNombre() : "N/A"; // <-- LEYENDO EL BEAN ZONAS

                            String fecha     = (oc.getFechaLlegada() != null) ? oc.getFechaLlegada().toString() : "—";
                            String estado    = oc.getEstado() != null ? oc.getEstado() : "—";

                            String badgeClass = "bg-secondary";
                            if (estado != null) {
                                if (estado.equalsIgnoreCase("borrador"))   badgeClass = "bg-secondary";
                                else if (estado.equalsIgnoreCase("enviada"))    badgeClass = "bg-info";
                                else if (estado.equalsIgnoreCase("en tránsito") || estado.equalsIgnoreCase("en transito")) badgeClass = "bg-warning";
                                else if (estado.equalsIgnoreCase("recibida"))   badgeClass = "bg-primary";
                                else if (estado.equalsIgnoreCase("completada")) badgeClass = "bg-success";
                                else if (estado.equalsIgnoreCase("cancelada"))  badgeClass = "bg-danger";
                            }
                %>
                <tr>
                    <td><%= oc.getCodigoOrdenCompra() %></td>
                    <td><%= proveedor %></td>
                    <td><%= producto %></td>
                    <td><%= zona %></td> <!-- <--- NUEVA CELDA DE VALOR -->
                    <td><%= fecha %></td>
                    <td><%= oc.getCantidad() %></td>
                    <td><span class="badge <%= badgeClass %>"><%= estado %></span></td>
                    <td>
                        <% if (estado != null && (estado.equalsIgnoreCase("borrador") || estado.equalsIgnoreCase("enviada"))) { %>
                        <form action="<%= ctx %>/StockBajo_OrdenCompra"
                              method="post" class="d-inline"
                              onsubmit="return confirm('¿Está seguro de CANCELAR la Orden <%= oc.getCodigoOrdenCompra() %>?');">
                            <input type="hidden" name="action" value="delete">
                            <input type="hidden" name="id" value="<%= oc.getCodigoOrdenCompra() %>">
                            <button type="submit" class="btn btn-sm btn-danger">Cancelar</button>
                        </form>
                        <% } else { %>
                        <button type="button" class="btn btn-sm btn-outline-secondary" disabled>—</button>
                        <% } %>
                    </td>
                </tr>
                <%
                    } // for
                } else {
                %>
                <tr>
                    <td colspan="8" class="text-center">No se encontraron órdenes de compra.</td> <!-- <--- COLSPAN AJUSTADO A 8 -->
                </tr>
                <% } %>
                </tbody>
            </table>
        </div>

        <div class="mt-3">
            <a href="StockBajo_OrdenCompra?action=form_crear" class="text-decoration-underline">Generar nueva orden</a>
        </div>
    </div>
</main>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.0.2/dist/js/bootstrap.bundle.min.js"></script>
<script>
    // Toggle del sidebar
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
