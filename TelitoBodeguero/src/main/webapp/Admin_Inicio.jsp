<%@ page import="java.util.Date" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="com.example.telitobodeguero.beans.Alertas" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<% ArrayList<Alertas> listaAlertas = (ArrayList<Alertas>) request.getAttribute("lista");
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
        .card-inventario{ background-color:#b69532; color:#fff; }
        .card-alertas{    background-color:#bd3644; color:#fff; }
        /* ====== TABLA ====== */
        .tabla1{ background:#0d1822; border-radius:.75rem; overflow:hidden; box-shadow:0 6px 14px rgba(0,0,0,.18);}
        .tabla1 thead th{ background:#263a4a; position:sticky; top:0; z-index:1; }
        .tabla1 a{ color:#9fd2ff; }
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

        <!-- Inicio -->
        <li class="nav-item mb-1">
            <a class="nav-link" href="<%=request.getContextPath()%>/InicioAdminServlet">
                <span class="text-label">Inicio</span>
            </a>
        </li>

        <!-- Gestión de usuarios y roles (DROPDOWN) -->
        <li class="nav-item dropdown mb-1">
            <a class="nav-link dropdown-toggle" href="#" id="usuariosDropdown" data-bs-toggle="dropdown" aria-expanded="false">
                <span class="text-label">Gestión de usuarios y roles</span>
            </a>
            <ul class="dropdown-menu dropdown-menu-dark" aria-labelledby="usuariosDropdown">
                <li>
                    <a class="dropdown-item" href="<%=request.getContextPath()%>/ListaUsuariosServlet">
                        <img src="images/image.png" alt="" width="20" height="20" class="me-2">Listado de usuarios
                    </a>
                </li>
                <li>
                    <a class="dropdown-item" href="<%=request.getContextPath()%>/GestionPermisosServlet">
                        <img src="images/gestionPermisos.png" alt="" width="20" height="20" class="me-2">Gestión de permisos
                    </a>
                </li>
            </ul>
        </li>

        <!-- Reportes Globales (DROPDOWN) -->
        <li class="nav-item dropdown mb-1">
            <a class="nav-link dropdown-toggle" href="#" id="reportesDropdown" data-bs-toggle="dropdown" aria-expanded="false">
                <span class="text-label">Reportes Globales</span>
            </a>
            <ul class="dropdown-menu dropdown-menu-dark" aria-labelledby="reportesDropdown">
                <li>
                    <a class="dropdown-item" href="<%= request.getContextPath() %>/ListaProductos">
                        <img src="images/reporteInventarios.jpg" alt="" width="20" height="20" class="me-2">Reporte de inventarios
                    </a>
                </li>
                <li>
                    <a class="dropdown-item" href="<%= request.getContextPath() %>/ReporteMovimientos">
                        <img src="images/reporteMovimientos.png" alt="" width="20" height="20" class="me-2">Reporte de movimientos
                    </a>
                </li>
                <li>
                    <a class="dropdown-item" href="<%= request.getContextPath() %>/StockBajo_OrdenCompra?action=stock">
                        <img src="images/reporteVentas.png" alt="" width="20" height="20" class="me-2">Reporte de compras
                    </a>
                </li>
            </ul>
        </li>

        <!-- Configuración del sistema (DROPDOWN) -->
        <li class="nav-item dropdown mb-1">
            <a class="nav-link dropdown-toggle" href="#" id="configDropdown" data-bs-toggle="dropdown" aria-expanded="false">
                <span class="text-label">Configuración del sistema</span>
            </a>
            <ul class="dropdown-menu dropdown-menu-dark" aria-labelledby="configDropdown">
                <li>
                    <a class="dropdown-item" href="Configuracion/parametrosInternos.html">
                        <img src="images/parametrosInternos.png" alt="" width="20" height="20" class="me-2">Parámetros internos
                    </a>
                </li>
            </ul>
        </li>

        <!-- Panel de supervisión (DROPDOWN) -->
        <li class="nav-item dropdown mb-1">
            <a class="nav-link dropdown-toggle" href="#" id="panelDropdown" data-bs-toggle="dropdown" aria-expanded="false">
                <span class="text-label">Panel de supervisión</span>
            </a>
            <ul class="dropdown-menu dropdown-menu-dark" aria-labelledby="panelDropdown">
                <li>
                    <a class="dropdown-item" href="PanelSupervicion/estadisticas.html">
                        <img src="images/estadisticas.png" alt="" width="20" height="20" class="me-2">Estadísticas
                    </a>
                </li>
                <li>
                    <a class="dropdown-item" href="PanelSupervicion/alertasNotificaciones.html">
                        <img src="images/alertas.png" alt="" width="20" height="20" class="me-2">Alertas y notificaciones
                    </a>
                </li>
            </ul>
        </li>

        <!-- Cerrar sesión -->
        <li class="nav-item mt-2 mb-3">
            <a class="nav-link" href="index.jsp"><span class="text-label">Cerrar sesión</span></a>
        </li>
    </ul>
</aside>

<!-- ===== Contenido principal ===== -->

<main class="main" id="main">
    <h1 class="my-3">Bienvenido - Admin</h1>
    <!-- KPIs -->
    <div class="container-fluid">
        <div class="row g-3 mb-3">
            <div class="col-12 col-sm-6 col-lg-3">
                <div class="card card-usuarios card-estilos h-100">
                    <div class="card-body">
                        <h5 class="card-title">Usuarios activos</h5>
                        <p class="card-text display-6">XX</p>
                    </div>
                </div>
            </div>
            <div class="col-12 col-sm-6 col-lg-3">
                <div class="card card-reportes card-estilos h-100">
                    <div class="card-body">
                        <h5 class="card-title">Reportes totales</h5>
                        <p class="card-text display-6">XX</p>
                    </div>
                </div>
            </div>
            <div class="col-12 col-sm-6 col-lg-3">
                <div class="card card-inventario card-estilos h-100">
                    <div class="card-body">
                        <h5 class="card-title">Inventario</h5>
                        <p class="card-text display-6">XX</p>
                    </div>
                </div>
            </div>
            <div class="col-12 col-sm-6 col-lg-3">
                <div class="card card-alertas card-estilos h-100">
                    <div class="card-body">
                        <h5 class="card-title">Alertas</h5>
                        <p class="card-text display-6">XX</p>
                    </div>
                </div>
            </div>
        </div>

        <!-- Tabla -->
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
                    <th></th>
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
                    <td><a class="item" href="Reportes/reportesInventarios.html">ir</a></td>
                </tr>
                <% }
                    } %>
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
