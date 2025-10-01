<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    String ctx = request.getContextPath();
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Telito - Productor</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.0.2/dist/css/bootstrap.min.css" rel="stylesheet" crossorigin="anonymous">
    <style>
        body{display:flex;min-height:100vh}
        .sidebar{width:280px;transition:width .3s}
        .sidebar.collapsed{width:84px}.sidebar.collapsed .sidebar-text{display:none}
        .nav-link.text-white:hover{background:#0d6efd;color:#fff!important}
        .main-content{flex:1;padding:3rem;background:#f8f9fa}
        .hero-title{color:#2f55a4;font-weight:800;line-height:1.05;font-size:clamp(2rem,3.2vw+1rem,3.5rem)}
        .hero-accent{width:64px;height:6px;border-radius:999px;background:#2f55a4;box-shadow:0 2px 8px rgba(47,85,164,.25);margin:.75rem 0 1.25rem}
    </style>
</head>
<body>
<div class="d-flex flex-column flex-shrink-0 p-3 text-white bg-dark sidebar" id="sidebar">
    <div class="d-flex justify-content-between align-items-center mb-3">
        <button class="btn btn-primary" id="toggleButton" type="button">&#9776;</button>
        <a href="<%=ctx%>/Productor.jsp" class="d-flex align-items-center text-white text-decoration-none">
            <span class="fs-5 sidebar-text">Telito bodeguero</span>
        </a>
    </div>
    <hr/>
    <ul class="nav nav-pills flex-column mb-auto">
        <li><a href="<%=ctx%>/Productor.jsp" class="nav-link text-white active"><span class="sidebar-text">Inicio</span></a></li>
        <li><a href="<%=ctx%>/MisProductos" class="nav-link text-white"><span class="sidebar-text">Mis Productos</span></a></li>
        <li><a href="<%=ctx%>/GestionLotes.jsp" class="nav-link text-white"><span class="sidebar-text">Gestión de Lotes</span></a></li>
        <li><a href="<%=ctx%>/OrdenesCompra" class="nav-link text-white"><span class="sidebar-text">Órdenes de Compra</span></a></li>
    </ul>
</div>

<div class="main-content">
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
                    <a href="<%=ctx%>/MisProductos" class="btn btn-outline-primary">Entrar</a>
                </div>
            </div>
        </div>

        <div class="col-md-4">
            <div class="card shadow-sm border-0">
                <div class="card-body text-center">
                    <h5 class="card-title text-primary">Gestión de Lotes</h5>
                    <p class="card-text">Controle inventarios y movimientos.</p>
                    <a href="<%=ctx%>/GestionLotes.jsp" class="btn btn-outline-primary">Entrar</a>
                </div>
            </div>
        </div>

        <div class="col-md-4">
            <div class="card shadow-sm border-0">
                <div class="card-body text-center">
                    <h5 class="card-title text-primary">Órdenes de compra</h5>
                    <p class="card-text">Revise órdenes y estados.</p>
                    <a href="<%=ctx%>/OrdenesCompra" class="btn btn-outline-primary">Entrar</a>
                </div>
            </div>
        </div>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.0.2/dist/js/bootstrap.bundle.min.js" crossorigin="anonymous"></script>
<script>
    document.getElementById('toggleButton').addEventListener('click',()=>document.getElementById('sidebar').classList.toggle('collapsed'));
</script>
</body>
</html>

