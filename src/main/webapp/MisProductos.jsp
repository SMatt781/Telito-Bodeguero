<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List, java.math.BigDecimal, java.math.RoundingMode, beans.Producto" %>
<%
    String ctx = request.getContextPath();
    List<Producto> productos = (List<Producto>) request.getAttribute("productos");                 // tabla 1
    List<Producto> productosPrecios = (List<Producto>) request.getAttribute("productosPrecios");   // tabla 2
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1" />
    <title>Mis Productos</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.0.2/dist/css/bootstrap.min.css" rel="stylesheet" crossorigin="anonymous" />
    <style>
        body{display:flex;min-height:100vh;background:#f7f9fc}
        .sidebar{width:280px;transition:width .3s ease}
        .sidebar.collapsed{width:84px}.sidebar.collapsed .sidebar-text{display:none}
        .nav-link.text-white:hover{background:#0d6efd;color:#fff!important}
        .main-content{flex:1;padding:2rem}
    </style>
</head>
<body>

<!-- Sidebar -->
<div class="d-flex flex-column flex-shrink-0 p-3 text-white bg-dark sidebar" id="sidebar">
    <div class="d-flex justify-content-between align-items-center mb-3">
        <button class="btn btn-primary" id="toggleButton" type="button" aria-label="Toggle Sidebar">&#9776;</button>
        <a href="<%=ctx%>/index.jsp" class="d-flex align-items-center text-white text-decoration-none">
            <span class="fs-5 sidebar-text">Telito bodeguero</span>
        </a>
    </div>
    <hr />
    <ul class="nav nav-pills flex-column mb-auto">
        <li><a class="nav-link text-white" href="<%=ctx%>/index.jsp"><span class="sidebar-text">Inicio</span></a></li>
        <li><a class="nav-link text-white active" href="<%=ctx%>/MisProductos"><span class="sidebar-text">Mis productos</span></a></li>
        <li><a class="nav-link text-white" href="<%=ctx%>/Lotes"><span class="sidebar-text">Gestión de Lotes</span></a></li>
        <li><a class="nav-link text-white" href="<%=ctx%>/OrdenesCompra"><span class="sidebar-text">Órdenes de Compra</span></a></li>
    </ul>
</div>

<!-- Main -->
<div class="main-content">
    <!-- Encabezado + botón Añadir -->
    <div class="d-flex justify-content-between align-items-center mb-2">
        <div>
            <h2 class="display-6 fw-bold text-primary m-0">Mis productos</h2>
            <small class="text-muted">Resumen de tus productos y stock total (según tus lotes).</small>
        </div>
        <a href="<%=ctx%>/ProductoNuevo" class="btn btn-primary btn-sm">Añadir producto</a>
    </div>

    <!-- Tabla 1: Resumen del productor -->
    <div class="card border-0 shadow-sm mb-4">
        <div class="card-body">
            <div class="table-responsive">
                <table class="table align-middle">
                    <thead class="table-light">
                    <tr>
                        <th>ID</th>
                        <th>SKU</th>
                        <th>Nombre</th>
                        <th>Precio</th>
                        <th>Stock Total</th>
                    </tr>
                    </thead>
                    <tbody>
                    <%
                        if (productos != null && !productos.isEmpty()) {
                            for (Producto p : productos) {
                    %>
                    <tr>
                        <td><%= p.getIdProducto() %></td>
                        <td><%= p.getSku() %></td>
                        <td><%= p.getNombre() %></td>
                        <td><%= p.getPrecio() %></td>
                        <td><%= p.getStock() %></td>
                    </tr>
                    <%  }
                    } else { %>
                    <tr><td colspan="5" class="text-muted">No hay productos para mostrar.</td></tr>
                    <% } %>
                    </tbody>
                </table>
            </div>
        </div>
    </div>

    <!-- Título de la segunda tabla -->
    <div class="d-flex justify-content-between align-items-end mb-2">
        <div>
            <h5 class="m-0 text-primary fw-bold">Precios sugeridos</h5>
            <small class="text-muted">Cálculo simple: precio unitario × 1.30 (margen 30%).</small>
        </div>
    </div>


    <!-- Tabla 2: Precios sugeridos (todos los productos) -->
    <div class="card border-0 shadow-sm">
        <div class="card-body">
            <div class="table-responsive">
                <table class="table align-middle">
                    <thead class="table-light">
                    <tr>
                        <th>ID</th>
                        <th>SKU</th>
                        <th>Producto</th>
                        <th>Precio Unitario</th>
                        <th>Precio Venta Recomendado</th>
                    </tr>
                    </thead>
                    <tbody>
                    <%
                        if (productosPrecios != null && !productosPrecios.isEmpty()) {
                            for (Producto p : productosPrecios) {
                                java.math.BigDecimal recomendado =
                                        p.getPrecio().multiply(new BigDecimal("1.30")).setScale(2, RoundingMode.HALF_UP);
                    %>
                    <tr>
                        <td><%= p.getIdProducto() %></td>
                        <td><%= p.getSku() %></td>
                        <td><%= p.getNombre() %></td>
                        <td><%= p.getPrecio() %></td>
                        <td><%= recomendado %></td>
                    </tr>
                    <%  }
                    } else { %>
                    <tr><td colspan="5" class="text-muted">Sin datos.</td></tr>
                    <% } %>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.0.2/dist/js/bootstrap.bundle.min.js" crossorigin="anonymous"></script>
<script>
    document.getElementById('toggleButton')
        .addEventListener('click',()=>document.getElementById('sidebar').classList.toggle('collapsed'));
</script>
</body>
</html>

