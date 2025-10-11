<%@ page import="java.util.Date" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!doctype html>
<html lang="es">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Telito - Admin | Reportes de inventarios</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.0.2/dist/css/bootstrap.min.css" rel="stylesheet">

    <style>
        /* ===== LAYOUT ===== */
        body{ margin:0; background:#f3f5f7; }
        .sidebar{
            position:fixed; inset:0 auto 0 0;      /* top:0; left:0; bottom:0 */
            width:280px; background:#212529; color:#fff;
            z-index:1000; transition:width .25s ease; overflow-y:auto;
        }
        .sidebar.collapsed{ width:80px; }
        .sidebar .brand{ padding:1rem 1.25rem; display:flex; align-items:center; gap:.75rem; }
        .sidebar .toggle{ border:0; background:#0d6efd; color:#fff; padding:.5rem .6rem; border-radius:.5rem; }
        .sidebar .nav-link{ color:#d6d6d6; }
        .sidebar .nav-link:hover{ background:#0d6efd; color:#fff; }
        .text-label{ display:inline; }
        .sidebar.collapsed .text-label{ display:none; }

        .main{
            margin-left:280px; transition:margin-left .25s ease;
            min-height:100vh; padding:2rem;
        }
        .main.collapsed{ margin-left:80px; }

        /* ===== CARDS KPI (tus colores) ===== */
        .kpi-card{ border-radius:1rem; color:#fff; height:100%; box-shadow:0 8px 20px rgba(0,0,0,.12); }
        .card-total{       background:#3f72b5; }
        .card-stock-bajo{  background:#b69532; }
        .card-sin-stock{   background:#bd3644; }
        .card-stock{       background:#2d8e60; }

        /* ===== BOTÓN ===== */
        .btn-light-alt{ background:#e4eced; border-color:#e4eced; color:#0b2239; }

        /* ===== TABLA ===== */
        .table-card{
            background:#1b3146; border-radius:.75rem; overflow:hidden;
            box-shadow:0 6px 14px rgba(0,0,0,.18);
        }
        .table-card thead th{
            background:#263a4a; color:#fff; position:sticky; top:0; z-index:1;
        }
        .table-card tbody td, .table-card tbody th{ color:#e8f0f7; }
    </style>
</head>
<body>

<!-- ===== Sidebar izquierda ===== -->
<aside class="sidebar" id="sidebar">
    <div class="brand">
        <button class="toggle" id="btnToggle" aria-label="Alternar menú">&#9776;</button>
        <span class="h5 mb-0 text-label">Bienvenido - Admin</span>
    </div>
    <hr class="text-secondary my-2">
    <ul class="nav nav-pills flex-column px-2">
        <li class="nav-item mb-1">
            <a class="nav-link" href="${sessionScope.homeUrl}"><span class="text-label">Inicio</span></a>
        </li>
        <li class="nav-item mb-1">
            <a class="nav-link" href="javascript:history.back()"><span class="text-label">Atrás</span></a>
        </li>
        <li class="nav-item mt-2">
            <a class="nav-link" href="#"><span class="text-label">Cerrar sesión</span></a>
        </li>
    </ul>
</aside>

<!-- ===== Contenido principal ===== -->
<main class="main" id="main">
    <div class="container-fluid">

        <h1 class="h3 mb-3">Reportes de inventarios</h1>

        <!-- KPIs -->
        <div class="row g-3 mb-2">
            <div class="col-12 col-md-6 col-lg-3">
                <div class="kpi-card card-total p-3">
                    <h6 class="mb-2">Total productos</h6>
                    <p class="display-6 mb-0 text-center">100</p>
                </div>
            </div>
            <div class="col-12 col-md-6 col-lg-3">
                <div class="kpi-card card-stock-bajo p-3">
                    <h6 class="mb-2">Productos con stock bajo</h6>
                    <p class="display-6 mb-0 text-center">100</p>
                </div>
            </div>
            <div class="col-12 col-md-6 col-lg-3">
                <div class="kpi-card card-sin-stock p-3">
                    <h6 class="mb-2">Productos sin stock</h6>
                    <p class="display-6 mb-0 text-center">100</p>
                </div>
            </div>
            <div class="col-12 col-md-6 col-lg-3">
                <div class="kpi-card card-stock p-3">
                    <h6 class="mb-2">Reportes totales</h6>
                    <p class="display-6 mb-0 text-center">100</p>
                </div>
            </div>
        </div>

        <!-- Botón -->
        <div class="m-3">
            <a href="nueva_orden.html" class="btn btn-light-alt btn-sm text-dark">Generar nuevo compra</a>
        </div>

        <!-- Tabla -->
        <div class="table-responsive table-card">
            <table class="table table-striped table-hover align-middle mb-0">
                <thead>
                <tr>
                    <th scope="col">SKU</th>
                    <th scope="col">Producto</th>
                    <th scope="col">Stock</th>
                    <th scope="col">Almacén</th>
                    <th scope="col">Lote</th>
                    <th scope="col">Precio</th>
                </tr>
                </thead>
                <tbody>
                <tr>
                    <th scope="row">PROD-2413</th>
                    <td>Leche</td>
                    <td>8</td>
                    <td>Almacén A</td>
                    <td>Lote 1234</td>
                    <td>S/ 4.30</td>
                </tr>
                <tr>
                    <th scope="row">PROD-2414</th>
                    <td>Arroz 5kg</td>
                    <td>0</td>
                    <td>Almacén B</td>
                    <td>Lote 5678</td>
                    <td>S/ 19.80</td>
                </tr>
                <tr>
                    <th scope="row">PROD-2415</th>
                    <td>Azúcar 5kg</td>
                    <td>10</td>
                    <td>Almacén C</td>
                    <td>Lote 9101</td>
                    <td>S/ 17.50</td>
                </tr>
                <tr>
                    <th scope="row">PROD-2416</th>
                    <td>Frijol 650g</td>
                    <td>7</td>
                    <td>Almacén D</td>
                    <td>Lote 1123</td>
                    <td>S/ 8.90</td>
                </tr>
                <tr>
                    <th scope="row">PROD-2417</th>
                    <td>Lentejas 650g</td>
                    <td>13</td>
                    <td>Almacén B</td>
                    <td>Lote 1456</td>
                    <td>S/ 5.50</td>
                </tr>
                </tbody>
            </table>
        </div>

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


