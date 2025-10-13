<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ page import="java.util.List,com.example.telitobodeguero.beans.OrdenCompra" %>
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
        .sidebar{
            position:fixed; inset:0 auto 0 0;       /* top:0; left:0; bottom:0 */
            width:280px; background:#212529; color:#fff;
            z-index:1000; transition:width .25s ease;
            overflow-y:auto;
        }
        .sidebar.collapsed{ width:80px; }
        .sidebar .brand{ padding:1rem 1.25rem; display:flex; align-items:center; gap:.75rem; }
        .sidebar .brand .toggle{ border:0; background:#0d6efd; color:#fff; padding:.5rem .6rem; border-radius:.5rem; }
        .sidebar .nav-link{ color:#d6d6d6; }
        .sidebar .nav-link:hover, .sidebar .nav-link:focus{ background:#0d6efd; color:#fff; }
        .sidebar .dropdown-menu{ background:#2b3035; }
        .sidebar .dropdown-item{ color:#fff; }
        .sidebar .dropdown-item:hover{ background:#0d6efd; }
        /* Ocultar textos cuando está colapsado */
        .sidebar.collapsed .text-label{ display:none; }

        .main{
            margin-left:280px; transition:margin-left .25s ease;
            min-height:100vh; padding:2rem;
        }
        .main.collapsed{ margin-left:80px; }
        .nav-link.text-white:hover{background:#0d6efd;color:#fff!important;}
    </style>
</head>
<body>

<jsp:include page="/sidebar.jsp" />

<main class="main" id="main">
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
                <th>Cantidad</th>
                <th>SKU</th>
                <th>Producto</th>
                <th>Proveedor</th>
            </tr>
            </thead>
            <tbody>
            <%
                if (filas != null && !filas.isEmpty()) {
                    for (OrdenCompra r : filas) {
                        String sku = "";
                        String nombreProd = "";
                        if (r.getProducto() != null) {
                            if (r.getProducto().getSku() != null)    sku = r.getProducto().getSku();
                            if (r.getProducto().getNombre() != null) nombreProd = r.getProducto().getNombre();
                        }
            %>
            <tr>
                <td><%= r.getCodigoOrdenCompra() %></td>
                <td><%= r.getEstado() %></td>
                <td><%= r.getFechaLlegada() == null ? "—" : r.getFechaLlegada() %></td>
                <td><%= r.getCantidad() %></td>
                <td><%= sku %></td>
                <td><%= nombreProd %></td>
                <td><%= r.getNombreProveedor() == null ? "" : r.getNombreProveedor() %></td>
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
    // Toggle del sidebar
    const btn = document.getElementById('btnToggle');
    const sidebar = document.getElementById('sidebar');
    const main = document.getElementById('main');
    btn.addEventListener('click', () => {
        sidebar.classList.toggle('collapsed');
        main.classList.toggle('collapsed');
    });

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


