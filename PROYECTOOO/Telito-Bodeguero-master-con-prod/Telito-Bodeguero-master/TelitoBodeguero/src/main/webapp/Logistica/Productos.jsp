<%@ page import="java.util.ArrayList" %>
<%@ page import="com.example.telitobodeguero.beans.Producto" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    ArrayList<Producto> listaProductos =
            (ArrayList<Producto>) request.getAttribute("listaProductos");
%>
<!doctype html>
<html lang="es">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Telito - Logística</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.0.2/dist/css/bootstrap.min.css" rel="stylesheet">

    <style>
        body { margin:0; background:#f3f5f7; }

        /* ==== Sidebar desplegable ==== */
        .sidebar{
            position:fixed; inset:0 auto 0 0;
            width:280px; background:#212529; color:#fff;
            z-index:1000; transition:width .25s ease;
            overflow-y:auto;
        }
        .sidebar.collapsed{ width:80px; }
        .sidebar .brand{
            padding:1rem 1.25rem; display:flex; align-items:center; gap:.75rem;
        }
        .sidebar .brand .toggle{
            border:0; background:#0d6efd; color:#fff;
            padding:.5rem .6rem; border-radius:.5rem;
        }
        .sidebar .nav-link{ color:#d6d6d6; }
        .sidebar .nav-link:hover,.sidebar .nav-link:focus{ background:#0d6efd; color:#fff; }
        .sidebar.collapsed .text-label{ display:none; }

        .main{ margin-left:280px; transition:margin-left .25s ease;
            min-height:100vh; padding:2rem; }
        .main.collapsed{ margin-left:80px; }

        /* ==== Tabla productos ==== */
        .table-products thead th{
            position: sticky; top: 0; z-index: 1;
            background:#e9ecef; color:#212529;
            border-bottom:2px solid #ced4da;
        }
        .table-products tbody tr:nth-child(odd){ background:#fff; }
        .table-products tbody tr:nth-child(even){ background:#f7f8fa; }
        .table-products td, .table-products th{
            padding:.65rem .75rem;
            vertical-align: middle;
            border-color:#dee2e6;
        }
        .table-products .col-acciones{ width: 160px; text-align:center; }

        /* Título */
        h1{ font-weight:800; color:#2e63f5; letter-spacing:.3px; }
    </style>
</head>
<body>
<!-- ===== Sidebar ===== -->
<aside class="sidebar" id="sidebar">
    <div class="brand">
        <button class="toggle" id="btnToggle" aria-label="Alternar menú">&#9776;</button>
        <span class="h5 mb-0 text-label">Telito bodeguero</span>
    </div>
    <hr class="text-secondary my-2">

    <ul class="nav nav-pills flex-column px-2">
        <li class="nav-item mb-1">
            <a class="nav-link" href="${sessionScope.homeUrl}">
                <span class="text-label">Inicio</span>
            </a>
        </li>
        <div class="spacer"></div>
        <li class="nav-item mb-1">
            <a class="nav-link" href="<%= request.getContextPath() %>/index.jsp">
                <span class="text-label">Cerrar sesión</span>
            </a>
        </li>
    </ul>
</aside>

<!-- ===== Main ===== -->
<main class="main" id="main">
    <!-- Botón volver -->
    <div class="mb-2">
        <button type="button" class="btn btn-outline-secondary btn-sm" onclick="history.back()">
            &larr; Volver
        </button>
    </div>

    <h1 class="mb-3 text-uppercase">Productos</h1>
    <hr>

    <div class="card mt-3">
        <div class="card-body p-3 p-md-4">
            <!-- Filtro búsqueda -->
            <form method="GET" action="<%= request.getContextPath() %>/ListaProductos" id="ordenForm">
                <div class="d-flex gap-3 align-items-center">
                    <input type="text"
                           class="form-control"
                           name="busqueda"
                           placeholder="Buscar por SKU o Nombre..."
                           value="<%= request.getAttribute("busquedaTermino") != null ? request.getAttribute("busquedaTermino") : "" %>"
                           style="width: 250px;">

                    <select class="form-select"
                            name="orden"
                            onchange="document.getElementById('ordenForm').submit()"
                            style="width: 250px;"> <option value="nombre_asc" <%= "nombre_asc".equals(request.getAttribute("ordenFiltro")) ? "selected" : "" %>>Producto (A - Z)</option>
                        <option value="nombre_desc" <%= "nombre_desc".equals(request.getAttribute("ordenFiltro")) ? "selected" : "" %>>Producto (Z - A)</option>
                        <option value="stock_asc" <%= "stock_asc".equals(request.getAttribute("ordenFiltro")) ? "selected" : "" %>>Stock (Menos a Más)</option>
                        <option value="stock_desc" <%= "stock_desc".equals(request.getAttribute("ordenFiltro")) ? "selected" : "" %>>Stock (Más a Menos)</option>
                    </select>

                    <button type="submit" class="btn btn-primary">Aplicar</button>
                </div>
            </form>

            <!-- Tabla de productos -->
            <div class="table-responsive mb-2 mt-3">
                <table class="table table-striped table-hover align-middle table-products">
                    <thead>
                    <tr>
                        <th>Código</th>
                        <th>Nombre</th>
                        <th>Stock</th>
                        <th>Lote</th>
                        <th>Zona</th>
                    </tr>
                    </thead>
                    <tbody>
                    <%
                        if (listaProductos != null && !listaProductos.isEmpty()) {
                            for (Producto p : listaProductos) {
                                int loteId = (p.getLote()!=null)? p.getLote().getIdLote() : 0;
                                int zonaId = (p.getZona()!=null)? p.getZona().getIdZonas() : 0;
                    %>
                    <tr>
                        <td><%= p.getSku() %></td>
                        <td><%= p.getNombre() %></td>
                        <td><%= p.getStock() %></td>
                        <td><%= loteId %></td>
                        <td><%= zonaId %></td>
                    </tr>
                    <%
                        }
                    } else {
                    %>
                    <tr>
                        <td colspan="6" class="text-center">No se encontraron productos.</td>
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
    btn.addEventListener('click', () => {
        sidebar.classList.toggle('collapsed');
        main.classList.toggle('collapsed');
    });
</script>
</body>
</html>
