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
            position:fixed; inset:0 auto 0 0;
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

        /* 💡 AGREGADO PARA RESPONSIVIDAD: Oculta el sidebar y ajusta el main en móviles */
        @media (max-width: 991.98px) {
            .sidebar:not(.collapsed) {
                /* Para que se oculte completamente en dispositivos pequeños */
                width: 0;
                overflow: hidden;
                transition: width 0s; /* No transiciona al ocultar */
            }
            .main {
                /* Asegura que el contenido principal ocupe todo el ancho */
                margin-left: 0 !important;
            }
        }
        /* Fin de las reglas responsivas añadidas */

        /* Ajuste para cuando el sidebar está colapsado y es pequeño */
        @media (min-width: 992px) {
            .sidebar.collapsed { width: 80px; }
            .main.collapsed { margin-left: 80px; }
        }
    </style>
</head>
<body>

<jsp:include page="/sidebar.jsp" />

<main class="main" id="main">
    <div class="d-flex align-items-center justify-content-between">
        <h1 class="text-uppercase m-0 text-primary fw-bold">Órdenes de compra</h1>
        <a href="<%=ctx%>/Lotes" class="btn btn-outline-secondary btn-sm">Volver a Lotes</a>
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
                <th>Producto</th>
            </tr>
            </thead>
            <tbody>
            <%
                if (filas != null && !filas.isEmpty()) {
                    for (OrdenCompra r : filas) {
                        String nombreProd = (r.getProducto() != null && r.getProducto().getNombre() != null) ? r.getProducto().getNombre() : "—";
            %>
            <tr>
                <td><%= r.getCodigoOrdenCompra() %></td>
                <td><%= r.getEstado() %></td>
                <td><%= r.getFechaLlegada() == null ? "—" : r.getFechaLlegada() %></td>
                <td><%= r.getCantidad() %></td>
                <td><%= nombreProd %></td>
            </tr>
            <%
                }
            } else {
            %>
            <tr><td colspan="5" class="text-muted text-center">No hay órdenes para mostrar.</td></tr>
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
    // Toggle del sidebar (se mantiene tu lógica JavaScript original)
    const btn = document.getElementById('btnToggle');
    const sidebar = document.getElementById('sidebar');
    const main = document.getElementById('main');

    if (btn && sidebar && main) {
        btn.addEventListener('click', () => {
            sidebar.classList.toggle('collapsed');
            main.classList.toggle('collapsed');
        });
    }

    function exportarCSV(){
        // Se exportarán las 5 columnas (Proveedor fue eliminada)
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