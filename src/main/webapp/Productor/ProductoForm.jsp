<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%
    String ctx = request.getContextPath();
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Nuevo producto</title>
    <meta name="viewport" content="width=device-width, initial-scale=1" />
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.0.2/dist/css/bootstrap.min.css" rel="stylesheet" crossorigin="anonymous" />

    <style>
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
    </style>
</head>
<body class="bg-light">
<jsp:include page="/sidebar.jsp" />

<main class="main" id="main">
    <div class="container py-4">
        <div class="d-flex justify-content-between align-items-center mb-3">
            <h3 class="m-0">Añadir producto</h3>
            <a href="<%=ctx%>/MisProductos" class="btn btn-outline-secondary btn-sm">Volver</a>
        </div>

        <form class="card p-3" method="post" action="<%=ctx%>/ProductoNuevo">
            <div class="row g-3">
                <div class="col-md-3">
                    <label class="form-label">SKU</label>
                    <input type="text" name="sku" class="form-control" required>
                </div>
                <div class="col-md-5">
                    <label class="form-label">Nombre</label>
                    <input type="text" name="nombre" class="form-control" required>
                </div>
                <div class="col-md-2">
                    <label class="form-label">Precio (S/)</label>
                    <input type="number" name="precio" class="form-control" step="0.01" min="0" required>
                </div>
                <div class="col-md-2">
                    <label class="form-label">Stock</label>
                    <input type="number" name="stock" class="form-control" min="0" value="0" required>
                </div>
            </div>

            <div class="mt-3 d-flex gap-2">
                <button class="btn btn-primary">Guardar</button>
                <a href="<%=ctx%>/MisProductos" class="btn btn-outline-secondary">Cancelar</a>
            </div>
        </form>
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
