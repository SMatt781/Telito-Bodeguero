<%@ page import="java.util.Date" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="com.example.telitobodeguero.beans.Alertas" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<% ArrayList<Alertas> listaAlertas = (ArrayList<Alertas>) request.getAttribute("lista");
    ArrayList<Object[]> listaGastos = (ArrayList<Object[]>) request.getAttribute("listaGastos");
    if (listaAlertas == null) {
        response.sendRedirect(request.getContextPath()+"/InicioAdminServlet");
        return;
    }%>
<!doctype html>
<html lang="es">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Telito - Admin</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.0.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
    <style>
        /* ====== LAYOUT ====== */
        body{margin:0; background:#f3f5f7;}
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

        h1 {
            font-family: 'Poppins', Inter, sans-serif;
            font-weight: 800;
            color: #2e63f5;     /* Azul corporativo */
            letter-spacing: .3px;
            margin-bottom: 1.25rem;
            text-transform: uppercase;
        }

        .main.collapsed{ margin-left:80px; }
        /* ====== CARDS (colores) ====== */
        .card-estilos{ border-radius:1rem; box-shadow:0 6px 14px rgba(0,0,0,.18); }
        .card-usuarios{   background-color:#3f72b5; color:#fff; }
        .card-reportes{   background-color:#2d8e60; color:#fff; }
        .card-alertas{    background-color:#bd3644; color:#fff; }
        /* ====== TABLA ====== */
        .tabla1{ background:#0d1822; border-radius:.75rem; overflow:hidden; box-shadow:0 6px 14px rgba(0,0,0,.18);}
        .tabla1 thead th{ background:#263a4a; position:sticky; top:0; z-index:1; }
        .tabla1 a{ color:#9fd2ff; }
    </style>
</head>
<body>

<jsp:include page="/sidebar.jsp" />


<main class="main" id="main">
    <h1 class="my-3">Bienvenido - Admin</h1>
    <div class="container-fluid">
        <div class="row g-3 mb-3 justify-content-center">
            <div class="col-12 col-sm-6 col-lg-4">
                <div class="card card-usuarios card-estilos h-100">
                    <div class="card-body">
                        <h5 class="card-title">Usuarios activos</h5>
                        <p class="card-text display-6"><%= request.getAttribute("usuariosActivos") %></p>
                    </div>
                </div>
            </div>
            <div class="col-12 col-sm-6 col-lg-4">
                <div class="card card-reportes card-estilos h-100">
                    <div class="card-body">
                        <h5 class="card-title">Reportes totales</h5>
                        <p class="card-text display-6">${cantidadReportes}</p>
                    </div>
                </div>
            </div>
            <div class="col-12 col-sm-6 col-lg-4">
                <div class="card card-alertas card-estilos h-100">
                    <div class="card-body">
                        <h5 class="card-title">Alertas</h5>
                        <p class="card-text display-6"><%= request.getAttribute("cantidadAlertas") %></p>
                    </div>
                </div>
            </div>
        </div>

        <h3 class="mt-5">Tabla de alertas</h3>

        <div class="table-responsive mt-3">
            <table class="table table-secondary table-striped table-hover align-middle mb-0">
                <thead>
                <tr>
                    <th>#Alerta</th>
                    <th>Sku</th>
                    <th>Producto</th>
                    <th>Motivo</th>
                    <th>Stock</th>
                    <th>Zona</th>
                    <th>Generar Orden</th>
                </tr>
                </thead>
                <tbody>
                <% if (listaAlertas != null) {
                    for (Alertas alertas: listaAlertas) { %>
                <tr>
                    <th scope="row"><%=alertas.getIdAlertas()%></th>
                    <td><%=alertas.getProducto().getSku()%></td>
                    <td><%=alertas.getProducto().getNombre()%></td>
                    <td><%=alertas.getTipoAlerta()%></td>
                    <td><%=alertas.getProducto().getStock()%></td>
                    <td><%=alertas.getZonas().getNombre()%></td>
                    <td>
                        <a href="StockBajo_OrdenCompra?action=form_crear&productoId=<%= alertas.getProducto().getIdProducto() %>"
                           class="btn btn-sm btn-primary">Generar orden</a>
                    </td>
                </tr>
                <% }
                } %>
                </tbody>
            </table>
        </div>

        <div class="container-fluid px-0 mt-5">
            <div class="row justify-content-center">
                <div class="col-12 col-lg-12">
                    <canvas id="graficoGastos" style="max-width:100%; height:450px;"></canvas>
                </div>
            </div>
        </div>

        <script>
            const ctx = document.getElementById('graficoGastos').getContext('2d');
            const labels = [ <% for (Object[] fila : listaGastos) { %>"<%= fila[0] %>", <% } %> ];
            const data = [ <% for (Object[] fila : listaGastos) { %><%= fila[1] %>, <% } %> ];

            new Chart(ctx, {
                type: 'bar',
                data: {
                    labels: labels,
                    datasets: [{
                        label: 'Gasto total por zona (S/.)',
                        data: data,
                        backgroundColor: '#2e63f5',
                        borderRadius: 5
                    }]
                },
                options: {
                    indexAxis: 'y',
                    responsive: true,
                    maintainAspectRatio: false,
                    plugins: {
                        legend: { display: false },
                        title: {
                            display: true,
                            text: 'Distribución de gastos por zona',
                            // ✅ CAMBIO: Se aumentó el tamaño de la fuente a 24
                            font: { size: 24 }
                        }
                    },
                    scales: {
                        x: {
                            beginAtZero: true,
                            ticks: { callback: value => 'S/. ' + value }
                        }
                    }
                }
            });
        </script>
    </div>
</main>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.0.2/dist/js/bootstrap.bundle.min.js"></script>
<script>
    // Toggle del sidebar
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
