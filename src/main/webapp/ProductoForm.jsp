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
</head>
<body class="bg-light">
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
</body>
</html>

