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
        body { margin:0; background:#f3f5f7; }

        /* ==== Sidebar desplegable ==== */
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
        .sidebar .nav-link:hover,.sidebar .nav-link:focus{
            background:#0d6efd; color:#fff;
        }
        .sidebar.collapsed .text-label{ display:none; }

        /* ==== Main content ==== */
        .main{
            margin-left:280px; transition:margin-left .25s ease;
            min-height:100vh; padding:2rem;
        }
        .main.collapsed{ margin-left:80px; }

        /* ==== Tablas (mismo estilo que el JSP anterior) ==== */
        .table-products thead th,
        #tablaProductos thead th,
        #tablaPrecios thead th{
            position: sticky;
            top: 0;
            z-index: 1;
            background:#e9ecef;
            color:#212529;
            border-bottom:2px solid #ced4da;
        }

        .table-products tbody tr:nth-child(odd),
        #tablaProductos tbody tr:nth-child(odd),
        #tablaPrecios tbody tr:nth-child(odd){
            background:#fff;
        }
        .table-products tbody tr:nth-child(even),
        #tablaProductos tbody tr:nth-child(even),
        #tablaPrecios tbody tr:nth-child(even){
            background:#f7f8fa;
        }
        .table-products td, .table-products th,
        #tablaProductos td, #tablaProductos th,
        #tablaPrecios td, #tablaPrecios th{
            padding:.65rem .75rem;
            vertical-align: middle;
            border-color:#dee2e6;
        }

        /* ==== Título igual que el otro JSP ==== */
        h1, h2 {
            font-weight:800;
            color:#2e63f5;
            letter-spacing:.3px;
        }

        /* ==== Búsqueda ==== */
        .search-wrap input{
            max-width:320px;
            border-radius: .4rem;
        }

        .muted{opacity:.65;}

        /* ancho cómodo para SKU */
        #tablaProductos th:first-child, #tablaProductos td:first-child,
        #tablaPrecios   th:first-child, #tablaPrecios   td:first-child {
            white-space:nowrap; min-width:110px;
        }
    </style>

</head>
<body>

<jsp:include page="/sidebar.jsp" />

<main class="main" id="main">
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
                    </tr>
                    <%  }
                    } else { %>
                    <tr class="muted"><td colspan="4" class="text-muted">No hay productos para mostrar.</td></tr>
                    <% } %>
                    </tbody>
                </table>
            </div>
            <div id="noMatch1" class="text-center text-muted d-none">Sin coincidencias en esta tabla.</div>
        </div>
    </div>

    <div class="d-flex justify-content-between align-items-end mb-2">
        <div>
            <h5 class="m-0 text-primary fw-bold">Precios sugeridos</h5>
            <small class="text-muted">Cálculo simple: precio unitario × 1.30 (margen 30%).</small>
        </div>
    </div>

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
                        <td><%= p.getPrecio() %></td>     <td><%= recomendado %></td>
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
            // 🛑 Los índices de columna de búsqueda ahora son: SKU(0), Nombre(1) 🛑
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

    // Busca en ambas tablas al mismo tiempo (ajuste de índices SIN COLUMNA ACCIONES)
    const input = document.getElementById('buscarInput');
    input.addEventListener('input', ()=>{
        // Tabla 1: SKU(0), Nombre(1), Precio(2), Stock(3)
        filtrarTabla('tablaProductos', 0, 1, 'noMatch1');
        // Tabla 2: SKU(0), Producto(1), Precio Unitario(2), Precio Recomendado(3)
        filtrarTabla('tablaPrecios', 0, 1, 'noMatch2');
    });
</script>
</body>
</html>