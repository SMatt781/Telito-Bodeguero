<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List, java.math.BigDecimal, java.math.RoundingMode, com.example.telitobodeguero.beans.Producto" %>
<%
    String ctx = request.getContextPath();
    List<Producto> productos = (List<Producto>) request.getAttribute("productos");                 // tabla 1
    List<Producto> productosPrecios = (List<Producto>) request.getAttribute("productosPrecios");   // tabla 2
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1" />
    <title>Mis Productos</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.0.2/dist/css/bootstrap.min.css" rel="stylesheet" crossorigin="anonymous" />
    <style>
        body{display:flex;min-height:100vh;background:#f7f9fc}
        .sidebar{width:280px;transition:width .3s ease}
        .sidebar.collapsed{width:84px}.sidebar.collapsed .sidebar-text{display:none}
        .nav-link.text-white:hover{background:#0d6efd;color:#fff!important}
        .main-content{flex:1;padding:2rem}
        .search-wrap{min-width:260px}
        .search-wrap input{max-width:320px}
        .muted{opacity:.65}
        /* ancho cómodo para SKU */
        #tablaProductos th:first-child, #tablaProductos td:first-child,
        #tablaPrecios   th:first-child, #tablaPrecios   td:first-child { white-space:nowrap; min-width:110px; }
    </style>
</head>
<body>

<!-- Sidebar -->
<div class="d-flex flex-column flex-shrink-0 p-3 text-white bg-dark sidebar" id="sidebar">
    <div class="d-flex justify-content-between align-items-center mb-3">
        <button class="btn btn-primary" id="toggleButton" type="button" aria-label="Toggle Sidebar">&#9776;</button>
        <a href="<%=ctx%>/indexProductor.jsp" class="d-flex align-items-center text-white text-decoration-none">
            <span class="fs-5 sidebar-text">Telito bodeguero</span>
        </a>
    </div>
    <hr />
    <ul class="nav nav-pills flex-column mb-auto">
        <li><a class="nav-link text-white" href="<%=ctx%>/indexProductor.jsp"><span class="sidebar-text">Inicio</span></a></li>
        <li><a class="nav-link text-white active" href="<%=ctx%>/MisProductos"><span class="sidebar-text">Mis productos</span></a></li>
        <li><a class="nav-link text-white" href="<%=ctx%>/Lotes"><span class="sidebar-text">Gestión de Lotes</span></a></li>
        <li><a class="nav-link text-white" href="<%=ctx%>/OrdenesCompra"><span class="sidebar-text">Órdenes de Compra</span></a></li>
    </ul>
</div>

<!-- Main -->
<div class="main-content">
    <!-- Encabezado + botón Añadir + Buscador -->
    <div class="d-flex flex-wrap justify-content-between align-items-center mb-3 gap-2">
        <div>
            <h2 class="display-6 fw-bold text-primary m-0">Mis productos</h2>
            <small class="text-muted">Resumen de tus productos y stock total (según tus lotes).</small>
        </div>
        <div class="d-flex align-items-center gap-2">
            <div class="input-group search-wrap">
                <span class="input-group-text">🔎</span>
                <input id="buscarInput" type="search" class="form-control" placeholder="Buscar por SKU o nombre...">
            </div>
            <a href="<%=ctx%>/ProductoNuevo" class="btn btn-primary btn-sm">Añadir producto</a>
        </div>
    </div>

    <!-- Tabla 1: Resumen del productor (sin ID) -->
    <div class="card border-0 shadow-sm mb-4">
        <div class="card-body">
            <div class="table-responsive">
                <table id="tablaProductos" class="table align-middle">
                    <thead class="table-light">
                    <tr>
                        <th>SKU</th>
                        <th>Nombre</th>
                        <th>Precio</th>
                        <th>Stock Total</th>
                        <th>Acciones</th>
                    </tr>
                    </thead>
                    <tbody>
                    <%
                        if (productos != null && !productos.isEmpty()) {
                            for (Producto p : productos) {
                    %>
                    <tr>
                        <td><%= p.getSku() %></td>
                        <td><%= p.getNombre() %></td>
                        <td><%= p.getPrecio() %></td>
                        <td><%= p.getStock() %></td>
                        <td>
                            <!-- usamos idProducto solo en el link, no se muestra -->
                            <a class="btn btn-sm btn-outline-primary"
                               href="<%=ctx%>/Lotes?idProducto=<%= p.getIdProducto() %>">
                                Detalles
                            </a>
                        </td>
                    </tr>
                    <%  }
                    } else { %>
                    <tr class="muted"><td colspan="5" class="text-muted">No hay productos para mostrar.</td></tr>
                    <% } %>
                    </tbody>
                </table>
            </div>
            <div id="noMatch1" class="text-center text-muted d-none">Sin coincidencias en esta tabla.</div>
        </div>
    </div>

    <!-- Título de la segunda tabla -->
    <div class="d-flex justify-content-between align-items-end mb-2">
        <div>
            <h5 class="m-0 text-primary fw-bold">Precios sugeridos</h5>
            <small class="text-muted">Cálculo simple: precio unitario × 1.30 (margen 30%).</small>
        </div>
    </div>

    <!-- Tabla 2: Precios sugeridos (sin ID) -->
    <div class="card border-0 shadow-sm">
        <div class="card-body">
            <div class="table-responsive">
                <table id="tablaPrecios" class="table align-middle">
                    <thead class="table-light">
                    <tr>
                        <th>SKU</th>
                        <th>Producto</th>
                        <th>Precio Unitario</th>
                        <th>Precio Venta Recomendado</th>
                    </tr>
                    </thead>
                    <tbody>
                    <%
                        if (productosPrecios != null && !productosPrecios.isEmpty()) {
                            for (Producto p : productosPrecios) {

                                // Convertir el String del bean a BigDecimal para calcular el recomendado
                                BigDecimal precioUnit;
                                try {
                                    precioUnit = new BigDecimal(p.getPrecio());
                                } catch (Exception ex) {
                                    precioUnit = BigDecimal.ZERO; // fallback si no es numérico
                                }
                                BigDecimal recomendado = precioUnit
                                        .multiply(new BigDecimal("1.30"))
                                        .setScale(2, RoundingMode.HALF_UP);
                    %>
                    <tr>
                        <td><%= p.getSku() %></td>
                        <td><%= p.getNombre() %></td>
                        <td><%= p.getPrecio() %></td>     <!-- se muestra el String original -->
                        <td><%= recomendado %></td>
                    </tr>
                    <%      }
                    } else { %>
                    <tr class="muted"><td colspan="4" class="text-muted">Sin datos.</td></tr>
                    <% } %>
                    </tbody>
                </table>
            </div>
            <div id="noMatch2" class="text-center text-muted d-none">Sin coincidencias en esta tabla.</div>
        </div>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.0.2/dist/js/bootstrap.bundle.min.js" crossorigin="anonymous"></script>
<script>
    // Sidebar toggle
    document.getElementById('toggleButton')
        .addEventListener('click',()=>document.getElementById('sidebar').classList.toggle('collapsed'));

    // Normaliza texto para búsqueda: sin tildes, sin mayúsculas
    function norm(s){
        if(!s) return '';
        try {
            return s.toString()
                .normalize('NFD').replace(/\p{Diacritic}/gu,'')
                .toLowerCase().trim();
        } catch(e){
            return s.toString()
                .replace(/[áàäâ]/gi,'a')
                .replace(/[éèëê]/gi,'e')
                .replace(/[íìïî]/gi,'i')
                .replace(/[óòöô]/gi,'o')
                .replace(/[úùüû]/gi,'u')
                .replace(/ñ/gi,'n')
                .toLowerCase().trim();
        }
    }

    // Filtra una tabla por columnas de SKU y Nombre
    function filtrarTabla(idTabla, idxSKU, idxNombre, idNoMatch){
        const q = norm(document.getElementById('buscarInput').value);
        const tbody = document.querySelector('#'+idTabla+' tbody');
        if(!tbody) return;
        const rows = Array.from(tbody.querySelectorAll('tr'));
        let visibles = 0;

        rows.forEach(tr=>{
            if(tr.classList.contains('muted')) { tr.style.display=''; return; }
            const celdas = tr.querySelectorAll('td');
            const sku = norm(celdas[idxSKU]?.textContent || '');
            const nom = norm(celdas[idxNombre]?.textContent || '');
            const match = !q || sku.includes(q) || nom.includes(q);
            tr.style.display = match ? '' : 'none';
            if(match) visibles++;
        });

        const msg = document.getElementById(idNoMatch);
        if(msg){
            msg.classList.toggle('d-none', !(q && visibles===0));
        }
    }

    // Busca en ambas tablas al mismo tiempo (ajuste de índices SIN ID)
    const input = document.getElementById('buscarInput');
    input.addEventListener('input', ()=>{
        // Tabla 1: SKU(0), Nombre(1), Precio(2), Stock(3), Acciones(4)
        filtrarTabla('tablaProductos', 0, 1, 'noMatch1');
        // Tabla 2: SKU(0), Producto(1), Precio Unitario(2), Precio Recomendado(3)
        filtrarTabla('tablaPrecios', 0, 1, 'noMatch2');
    });
</script>
</body>
</html>
