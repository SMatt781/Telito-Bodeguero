<%@ page import="java.util.ArrayList" %>
<%@ page import="com.example.telitobodeguero.beans.Producto" %>
<%@ page import="com.example.telitobodeguero.beans.Zonas" %>
<%@ page import="com.example.telitobodeguero.beans.Usuarios" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    ArrayList<Producto> listaProductos =
            (ArrayList<Producto>) request.getAttribute("listaProductos");
    ArrayList<Zonas> listaZonas =
            (ArrayList<Zonas>) request.getAttribute("listaZonas");
    ArrayList<Usuarios> listaProductores =
            (ArrayList<Usuarios>) request.getAttribute("listaProductores");

    Integer zonaSel = (Integer) request.getAttribute("zonaSeleccionada");
    Integer prodSel = (Integer) request.getAttribute("productoSeleccionado");
    Integer provSel = (Integer) request.getAttribute("proveedorSeleccionado");
%>
<!doctype html>
<html lang="es">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Telito - Logística</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.0.2/dist/css/bootstrap.min.css" rel="stylesheet">

    <style>
        body { margin:0; background:#f3f5f7; }

        /* ==== Sidebar desplegable (mismo que Productos) ==== */
        .sidebar{
            position:fixed; inset:0 auto 0 0;
            width:280px; background:#212529; color:#fff;
            z-index:1000; transition:width .25s ease;
            overflow-y:auto;
        }
        .sidebar.collapsed{ width:80px; }
        .sidebar .brand{
            padding:1rem 1.25rem; display:flex; align-items:center; gap:.75rem;
        }
        .sidebar .brand .toggle{
            border:0; background:#0d6efd; color:#fff;
            padding:.5rem .6rem; border-radius:.5rem;
        }
        .sidebar .nav-link{ color:#d6d6d6; }
        .sidebar .nav-link:hover,.sidebar .nav-link:focus{ background:#0d6efd; color:#fff; }
        .sidebar.collapsed .text-label{ display:none; }

        .main{
            margin-left:280px; transition:margin-left .25s ease;
            min-height:100vh; padding:2rem;
        }
        .main.collapsed{ margin-left:80px; }

        /* Título */
        h1{ font-weight:800; color:#2e63f5; letter-spacing:.3px; }

        /* Wrapper para que el formulario no sea tan largo */
        .form-wrapper{
            max-width: 720px;           /* ancho similar a "Nuevo lote" */
            margin: 0 auto;             /* centrado horizontal */
        }

        /* Card + controles del formulario */
        .form-card .form-select,
        .form-card .form-control{
            height:46px;
            border-radius:10px;
        }
    </style>
    <link rel="stylesheet"
          href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.4/css/all.min.css">
</head>
<body>
<!-- ===== Sidebar ===== -->
<jsp:include page="/sidebar.jsp" />

<!-- ===== Main ===== -->
<main class="main" id="main">
    <!-- Botón volver -->
    <div class="mb-2">
        <button type="button" class="btn btn-outline-secondary btn-sm" onclick="history.back()">
            &larr; Volver
        </button>
    </div>

    <h1 class="mb-3 text-uppercase">Nueva orden de compra</h1>
    <hr>

    <!-- WRAPPER CENTRADO -->
    <div class="form-wrapper">
        <div class="card mt-3 form-card">
            <div class="card-body p-3 p-md-4">

                <form method="POST" action="StockBajo_OrdenCompra?action=crear">

                    <!-- ZONA -->
                    <h6 class="fw-bold mb-3">Zona</h6>
                    <div class="mb-3">
                        <select class="form-select" name="zonaId" id="zonaSelect" onchange="onZonaChange()">
                            <option value="">Seleccione una zona</option>
                            <%
                                if (listaZonas != null) {
                                    for (Zonas zona : listaZonas) {
                                        boolean selected = (zonaSel != null && zonaSel == zona.getIdZonas());
                            %>
                            <option value="<%= zona.getIdZonas() %>" <%= selected ? "selected" : "" %>>
                                <%= zona.getNombre() %>
                            </option>
                            <%
                                    }
                                }
                            %>
                        </select>
                    </div>

                    <!-- PRODUCTO -->
                    <h6 class="fw-bold mb-3">Producto</h6>
                    <div class="mb-3">
                        <select class="form-select" name="productoId" id="productoSelect"
                                onchange="onProductoChange()"
                                <%= (listaProductos == null || listaProductos.isEmpty()) ? "disabled" : "" %>>
                            <option value="">Seleccione un producto</option>
                            <%
                                if (listaProductos != null) {
                                    for (Producto prod : listaProductos) {
                                        boolean selected = (prodSel != null && prodSel == prod.getIdProducto());
                            %>
                            <option value="<%= prod.getIdProducto() %>" <%= selected ? "selected" : "" %>>
                                <%= prod.getNombre() %>
                            </option>
                            <%
                                    }
                                }
                            %>
                        </select>
                    </div>

                    <!-- PROVEEDOR -->
                    <h6 class="fw-bold mb-3">Proveedor</h6>
                    <div class="mb-3">
                        <select class="form-select" name="proveedorId"
                                id="proveedorSelect" onchange="onProveedorChange()"
                                <%= (listaProductores == null || listaProductores.isEmpty()) ? "disabled" : "" %>>
                            <option value="">Seleccione un proveedor</option>
                            <%
                                if (listaProductores != null) {
                                    for (Usuarios prod : listaProductores) {
                                        boolean selected = (provSel != null && provSel == prod.getIdUsuarios());
                            %>
                            <option value="<%= prod.getIdUsuarios() %>" <%= selected ? "selected" : "" %>>
                                <%= prod.getNombre() + " " + prod.getApellido() %>
                            </option>
                            <%
                                    }
                                }
                            %>
                        </select>
                    </div>

                    <!-- FECHA -->
                    <h6 class="fw-bold mb-3">Fecha de llegada</h6>
                    <div class="mb-3">
                        <input type="date" class="form-control" name="fechaLlegada" required>
                    </div>

                    <!-- CANTIDAD -->
                    <h6 class="fw-bold mb-3">Cantidad</h6>
                    <div class="mb-3">
                        <input type="number" class="form-control" name="cantidad"
                               placeholder="Ingrese cantidad" min="1" required>
                    </div>

                    <div class="d-flex justify-content-end">
                        <button type="submit" class="btn btn-primary"
                                <%= (prodSel == null || provSel == null) ? "disabled" : "" %>>
                            Generar orden
                        </button>
                    </div>
                </form>

            </div>
        </div>
    </div>

</main>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.0.2/dist/js/bootstrap.bundle.min.js"></script>
<script>
    // Función para manejar el cambio de ZONA
    function onZonaChange() {
        const zonaId = document.getElementById('zonaSelect').value;
        window.location.href =
            '<%= request.getContextPath() %>/StockBajo_OrdenCompra?action=form_crear&zonaId=' + zonaId;
    }

    // Función para manejar el cambio de PRODUCTO
    function onProductoChange() {
        const zonaId = document.getElementById('zonaSelect').value;
        const productoId = document.getElementById('productoSelect').value;
        window.location.href =
            '<%= request.getContextPath() %>/StockBajo_OrdenCompra?action=form_crear&zonaId='
            + zonaId + '&productoId=' + productoId;
    }

    // Función para manejar el cambio de PROVEEDOR
    function onProveedorChange() {
        const zonaId = document.getElementById('zonaSelect').value;
        const productoId = document.getElementById('productoSelect').value;
        const proveedorId = document.getElementById('proveedorSelect').value;
        window.location.href =
            '<%= request.getContextPath() %>/StockBajo_OrdenCompra?action=form_crear&zonaId='
            + zonaId + '&productoId=' + productoId + '&proveedorId=' + proveedorId;
    }

    // Toggle sidebar (igual que en Productos)
    const btn = document.getElementById('btnToggle');
    const sidebar = document.getElementById('sidebar');
    const main = document.getElementById('main');
    if (btn && sidebar && main) {
        btn.addEventListener('click', () => {
            sidebar.classList.toggle('collapsed');
            main.classList.toggle('collapsed');
        });
    }
</script>
</body>
</html>
