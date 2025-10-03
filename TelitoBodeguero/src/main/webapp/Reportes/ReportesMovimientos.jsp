<%@ page import="java.util.Date" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!doctype html>
<html lang="es">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Telito - Admin | Reportes de movimientos</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.0.2/dist/css/bootstrap.min.css" rel="stylesheet">

    <style>
        /* ===== LAYOUT ===== */
        body{ margin:0; background:#f3f5f7; }
        .sidebar{
            position:fixed; inset:0 auto 0 0;
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

        /* ===== TARJETAS / FILTROS ===== */
        .panel-card{
            background:#84a8ce; border-radius:1rem; box-shadow:0 8px 20px rgba(0,0,0,.12);
        }
        .filtros{ background:#aec0d0a8; border-radius:.5rem; box-shadow:0 6px 14px rgba(0,0,0,.08); }
        .kpi-card{ background:#ffffff66; border-radius:.75rem; }
        .btn-export{ background:#e8f4ef; border-color:#e8f4ef; color:#0b3d2a; }

        /* ===== TABLA OSCURA ===== */
        .table-wrap{
            background:#1b3146; border-radius:.75rem; overflow:hidden;
            box-shadow:0 6px 14px rgba(0,0,0,.18);
        }
        .table-wrap thead th{ background:#263a4a; color:#fff; position:sticky; top:0; z-index:1; }
        .table-wrap tbody td, .table-wrap tbody th{ color:#e8f0f7; }
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

        <h1 class="h3 mb-3">Reportes de movimientos</h1>

        <div class="panel-card p-3 p-md-4">
            <!-- Filtros -->
            <form class="row g-3 mb-3">
                <div class="col-md-3">
                    <label class="form-label">Desde</label>
                    <input type="date" class="form-control filtros">
                </div>
                <div class="col-md-3">
                    <label class="form-label">Hasta</label>
                    <input type="date" class="form-control filtros">
                </div>
                <div class="col-md-2">
                    <label class="form-label">Movimiento</label>
                    <select class="form-select filtros">
                        <option value="">Todos</option>
                        <option value="entrada">Entrada</option>
                        <option value="salida">Salida</option>
                    </select>
                </div>
                <div class="col-md-2">
                    <label class="form-label">Producto</label>
                    <select class="form-select filtros">
                        <option value="">Todos</option>
                        <option value="aceite">Aceite Vegetal</option>
                        <option value="arroz">Arroz</option>
                        <option value="azucar">Azúcar</option>
                    </select>
                </div>
                <div class="col-md-2">
                    <label class="form-label">Zona</label>
                    <select class="form-select filtros">
                        <option value="">Todas</option>
                        <option value="norte">Norte</option>
                        <option value="este">Este</option>
                        <option value="sur">Sur</option>
                        <option value="oeste">Oeste</option>
                    </select>
                </div>
            </form>

            <!-- Resumen -->
            <div class="row g-3 mb-3">
                <div class="col-md-3">
                    <div class="kpi-card text-center p-3">
                        <h6 class="fw-bold mb-1">Entradas</h6>
                        <p class="fs-4 fw-bold mb-0">7</p>
                    </div>
                </div>
                <div class="col-md-3">
                    <div class="kpi-card text-center p-3">
                        <h6 class="fw-bold mb-1">Salidas</h6>
                        <p class="fs-4 fw-bold mb-0">8</p>
                    </div>
                </div>
            </div>

            <div class="mb-3">
                <button class="btn btn-export btn-sm">Exportar CSV</button>
            </div>

            <!-- Tabla -->
            <div class="table-responsive table-wrap">
                <table class="table table-striped align-middle">
                    <thead>
                    <tr>
                        <th>Fecha</th>
                        <th>Movimiento</th>
                        <th>Producto</th>
                        <th>Cantidad</th>
                        <th>Almacén</th>
                        <th>Proveedor</th>
                    </tr>
                    </thead>
                    <tbody>
                    <tr><td>06/09/2025</td><td>Entrada</td><td>Arroz 1kg</td><td>30</td><td>Almacén A</td><td>amoreno</td></tr>
                    <tr><td>05/09/2025</td><td>Salida</td><td>Atún</td><td>10</td><td>Almacén B</td><td>jperez</td></tr>
                    <tr><td>03/09/2025</td><td>Salida</td><td>Fideos 500g</td><td>20</td><td>Almacén C</td><td>mlopez</td></tr>
                    <tr><td>03/09/2025</td><td>Entrada</td><td>Leche 1L</td><td>50</td><td>Almacén D</td><td>rquiroz</td></tr>
                    <tr><td>30/08/2025</td><td>Salida</td><td>Azúcar 1kg</td><td>15</td><td>Almacén A</td><td>cmendez</td></tr>
                    <tr><td>02/09/2025</td><td>Entrada</td><td>Café 250g</td><td>45</td><td>Almacén B</td><td>lrojas</td></tr>
                    <tr><td>02/09/2025</td><td>Salida</td><td>Harina 1kg</td><td>20</td><td>Almacén C</td><td>agonzales</td></tr>
                    <tr><td>01/09/2025</td><td>Entrada</td><td>Leche en polvo 500g</td><td>30</td><td>Almacén D</td><td>mcarpio</td></tr>
                    <tr><td>01/09/2025</td><td>Salida</td><td>Aceite Vegetal 1L</td><td>15</td><td>Almacén A</td><td>jcastro</td></tr>
                    <tr><td>31/08/2025</td><td>Entrada</td><td>Azúcar 1kg</td><td>50</td><td>Almacén B</td><td>fvaldez</td></tr>
                    <tr><td>31/08/2025</td><td>Salida</td><td>Arroz 5kg</td><td>12</td><td>Almacén C</td><td>druiz</td></tr>
                    <tr><td>31/08/2025</td><td>Entrada</td><td>Conserva de atún</td><td>28</td><td>Almacén D</td><td>mquintana</td></tr>
                    <tr><td>30/08/2025</td><td>Salida</td><td>Leche 1L</td><td>18</td><td>Almacén A</td><td>rvera</td></tr>
                    <tr><td>30/08/2025</td><td>Entrada</td><td>Fideos 500g</td><td>40</td><td>Almacén B</td><td>cchavez</td></tr>
                    <tr><td>30/08/2025</td><td>Salida</td><td>Sal 1kg</td><td>25</td><td>Almacén C</td><td>sparedes</td></tr>
                    </tbody>
                </table>
            </div>
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
