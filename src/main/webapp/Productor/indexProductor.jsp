<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    String ctx = request.getContextPath();
    String uri  = request.getRequestURI();
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Telito - Productor</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.0.2/dist/css/bootstrap.min.css" rel="stylesheet" crossorigin="anonymous">
    <style>
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
        .main.collapsed{ margin-left:80px; }

        .nav-link.text-white:hover{background:#0d6efd;color:#fff!important}
        .main-content{flex:1;padding:3rem;background:#f8f9fa}
        .hero-title{color:#2f55a4;font-weight:800;line-height:1.05;font-size:clamp(2rem, calc(3.2vw + 1rem), 3.5rem)}
        .hero-accent{width:64px;height:6px;border-radius:999px;background:#2f55a4;box-shadow:0 2px 8px rgba(47,85,164,.25);margin:.75rem 0 1.25rem}
    </style>
</head>
<body>
<jsp:include page="/sidebar.jsp" />

<main class="main" id="main">
    <section>
        <h1 class="hero-title">¡Bienvenido, Productor!</h1>
        <div class="hero-accent"></div>
        <p class="text-muted">Seleccione una de las opciones:</p>
    </section>

    <div class="row mt-4">
        <div class="col-md-4">
            <div class="card shadow-sm border-0">
                <div class="card-body text-center">
                    <h5 class="card-title text-primary">Mis Productos</h5>
                    <p class="card-text">Gestione sus productos.</p>
                    <a href="<%=ctx%>/MisProductos" class="btn btn-outline-primary <%= uri.contains("/MisProductos") ? "active" : "" %>">Entrar</a>
                </div>
            </div>
        </div>

        <div class="col-md-4">
            <div class="card shadow-sm border-0">
                <div class="card-body text-center">
                    <h5 class="card-title text-primary">Gestión de Lotes</h5>
                    <p class="card-text">Controle inventarios y movimientos.</p>
                    <a href="<%=ctx%>/Lotes" class="btn btn-outline-primary <%= uri.contains("/Lotes") ? "active" : "" %>">Entrar</a>
                </div>
            </div>
        </div>

        <div class="col-md-4">
            <div class="card shadow-sm border-0">
                <div class="card-body text-center">
                    <h5 class="card-title text-primary">Órdenes de compra</h5>
                    <p class="card-text">Revise órdenes y estados.</p>
                    <a href="<%=ctx%>/OrdenesCompra" class="btn btn-outline-primary <%= uri.contains("/OrdenesCompra") ? "active" : "" %>">Entrar</a>
                </div>
            </div>
        </div>
    </div>
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

</script>
</body>
</html>


