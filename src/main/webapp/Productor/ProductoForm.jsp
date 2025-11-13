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
        body { margin:0; background:#f3f5f7; }

        /* ===== Sidebar ===== */
        .sidebar{
            position:fixed; inset:0 auto 0 0;
            width:280px; background:#212529; color:#fff;
            z-index:1000; transition:width .25s ease;
            overflow-y:auto;
        }
        .sidebar.collapsed{ width:80px; }
        .sidebar .brand{ padding:1rem 1.25rem; display:flex; align-items:center; gap:.75rem; }
        .sidebar .brand .toggle{
            border:0; background:#0d6efd; color:#fff;
            padding:.5rem .6rem; border-radius:.5rem;
        }
        .sidebar .nav-link{ color:#d6d6d6; }
        .sidebar .nav-link:hover,.sidebar .nav-link:focus{
            background:#0d6efd; color:#fff;
        }
        .sidebar .dropdown-menu{ background:#2b3035; }
        .sidebar .dropdown-item{ color:#fff; }
        .sidebar .dropdown-item:hover{ background:#0d6efd; }
        .sidebar.collapsed .text-label{ display:none; }

        /* ===== Main ===== */
        .main{
            margin-left:280px;
            width: calc(100% - 280px);   /* ← NECESARIO PARA CENTRAR */
            min-height:100vh;
            padding:2rem;

            display:flex;
            justify-content:center;      /* ← CENTRA TODO */
        }
        .main.collapsed{
            margin-left:80px;
            width: calc(100% - 80px);    /* ← SE AJUSTA AL COLAPSAR */
        }

        /* ===== WRAPPER CENTRADO ===== */
        .form-wrapper{
            width:100%;
            max-width:750px;
        }

        /* ===== Títulos ===== */
        h3{
            font-weight:800;
            color:#2e63f5;
            letter-spacing:.3px;
            text-transform:uppercase;
        }

        /* ===== Card del formulario ===== */
        .card{
            border:0;
            border-radius:12px;
            box-shadow:0 4px 12px rgba(0,0,0,.08);
            background:#ffffff;
        }

        label.form-label{
            font-weight:600;
            color:#444;
        }

        input.form-control{
            border-radius:8px;
        }

        .btn-primary{
            background:#2e63f5;
            border:none;
            font-weight:600;
            padding:.47rem 1.2rem;
            border-radius:8px;
        }

        .btn-outline-secondary{
            border-radius:8px;
            font-weight:600;
        }


    </style>

</head>
<body class="bg-light">
<jsp:include page="/sidebar.jsp" />

<!-- ★★★ WRAPPER CENTRADO AÑADIDO ★★★ -->
<main class="main" id="main">
    <div class="form-wrapper">

        <div class="d-flex justify-content-between align-items-center mb-3">
            <h3 class="m-0">Añadir producto</h3>
            <a href="<%=ctx%>/MisProductos" class="btn btn-outline-secondary btn-sm">Volver</a>
        </div>

        <form class="card p-3" method="post" action="<%=ctx%>/ProductoNuevo">
            <div class="row g-3">

                <div class="col-md-5">
                    <label class="form-label">Nombre</label>
                    <input type="text" name="nombre" class="form-control" required>
                </div>

                <div class="col-md-3">
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