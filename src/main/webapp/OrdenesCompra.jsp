<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ page import="java.util.List,beans.OrdenCompra" %>
<%
    String ctx = request.getContextPath();
    List<OrdenCompra> filas = (List<OrdenCompra>) request.getAttribute("filas");
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="utf-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1" />
    <title>Órdenes de Compra - Telito Bodeguero</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.0.2/dist/css/bootstrap.min.css"
          rel="stylesheet" crossorigin="anonymous" />
    <style>
        :root { --brand:#2e63f5; }
        body{display:flex;min-height:100vh;}
        .main-content{flex:1;padding:2rem;background:#f8f9fa;min-height:100vh;}
        .sidebar{width:280px;transition:width .3s ease;}
        .sidebar.collapsed{width:80px;}
        .sidebar.collapsed .sidebar-text{display:none;}
        .nav-link.text-white:hover{background:#0d6efd;color:#fff!important;}
    </style>
</head>
<body>
<div class="d-flex flex-column flex-shrink-0 p-3 text-white bg-dark sidebar" id="sidebar">
    <div class="d-flex justify-content-between align-items-center mb-3">
        <button class="btn btn-primary" id="toggleButton" type="button">&#9776;</button>
        <a href="#" class="d-flex align-items-center text-white text-decoration-none">
            <span class="fs-4 sidebar-text">Telito bodeguero</span>
        </a>
    </div>
    <hr />
    <ul class="nav nav-pills flex-column mb-auto">
        <li><a href="<%=ctx%>/index.jsp"     class="nav-link text-white"><span class="sidebar-text">Inicio</span></a></li>
        <li><a href="<%=ctx%>/MisProductos"   class="nav-link text-white"><span class="sidebar-text">Mis Productos</span></a></li>
        <li><a href="<%=ctx%>/Lotes"          class="nav-link text-white"><span class="sidebar-text">Gestión de Lotes</span></a></li>
        <li><a href="<%=ctx%>/OrdenesCompra"  class="nav-link text-white active"><span class="sidebar-text">Órdenes de Compra</span></a></li>
    </ul>
</div>

<main class="main-content">
    <div class="d-flex align-items-center justify-content-between">
        <h1 class="text-uppercase m-0 text-primary fw-bold">Órdenes de compra</h1>
        <a href="<%=ctx%>/MisProductos" class="btn btn-outline-secondary btn-sm">Volver</a>
    </div>
    <hr>

    <div class="table-responsive">
        <table id="tablaOC" class="table table-secondary table-striped align-middle">
            <thead>
            <tr>
                <th>OC</th>
                <th>Estado</th>
                <th>Fecha Llegada</th>
                <th>Item</th>
                <th>Cantidad</th>
                <th>SKU</th>
                <th>Producto</th>
            </tr>
            </thead>
            <tbody>
            <%
                if (filas != null && !filas.isEmpty()) {
                    for (OrdenCompra r : filas) {
            %>
            <tr>
                <td><%= r.getIdOrdenCompra() %></td>
                <td><%= r.getEstado() %></td>
                <td><%= r.getFechaLlegada()==null?"—":r.getFechaLlegada() %></td>
                <td><%= r.getIdItem() %></td>
                <td><%= r.getCantidad() %></td>
                <td><%= r.getSku() %></td>
                <td><%= r.getProducto() %></td>
            </tr>
            <%
                }
            } else {
            %>
            <tr><td colspan="7" class="text-muted">No hay órdenes para mostrar</td></tr>
            <% } %>
            </tbody>
        </table>
    </div>

    <div class="d-flex justify-content-between align-items-center mt-2">
        <small class="text-muted"><% if (filas != null) { %>Mostrando <%= filas.size() %> registro(s)<% } %></small>
        <button class="btn btn-outline-primary btn-sm" onclick="exportarCSV()">Exportar CSV</button>
    </div>
</main>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.0.2/dist/js/bootstrap.bundle.min.js" crossorigin="anonymous"></script>
<script>
    document.getElementById('toggleButton')
        .addEventListener('click', ()=>document.getElementById('sidebar').classList.toggle('collapsed'));

    function exportarCSV(){
        const filas = Array.from(document.querySelectorAll('#tablaOC tr'))
            .map(tr => Array.from(tr.children)
                .map(td => '"' + td.innerText.replace(/"/g,'""') + '"')
                .join(','))
            .join('\n');
        const blob = new Blob([filas], {type: 'text/csv;charset=utf-8;'});
        const a = document.createElement('a');
        a.href = URL.createObjectURL(blob);
        a.download = 'ordenes_compra.csv';
        a.click();
    }
</script>
</body>
</html>

