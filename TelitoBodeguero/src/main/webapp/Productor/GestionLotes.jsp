<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ page import="java.util.List,com.example.telitobodeguero.beans.Lote,com.example.telitobodeguero.beans.Producto" %>
<%
    String ctx = request.getContextPath();
    List<Lote> lista      = (List<Lote>) request.getAttribute("lista");
    List<Producto> prods  = (List<Producto>) request.getAttribute("productos");
    Integer idProducto    = (Integer) request.getAttribute("idProducto"); // puede ser null
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1" />
    <title>Gestión de Lotes</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.0.2/dist/css/bootstrap.min.css"
          rel="stylesheet" crossorigin="anonymous" />
    <style>
        body{display:flex;min-height:100vh}
        .sidebar{width:280px;transition:width .3s ease}
        .sidebar.collapsed{width:80px}.sidebar.collapsed .sidebar-text{display:none}
        .nav-link.text-white:hover{background:#0d6efd;color:#fff!important}
        .main-content{flex:1;padding:2rem;background:#f8f9fa}
        #tablaLotes tbody tr{cursor:pointer}
        #tablaLotes tbody tr.table-active td{background:#cfe2ff!important}
    </style>
</head>
<body>

<div class="d-flex flex-column flex-shrink-0 p-3 text-white bg-dark sidebar" id="sidebar">
    <div class="d-flex justify-content-between align-items-center mb-3">
        <button class="btn btn-primary" id="toggleButton" type="button">&#9776;</button>
        <a href="<%=ctx%>/index.jsp" class="d-flex align-items-center text-white text-decoration-none">
            <span class="fs-5 sidebar-text">Telito bodeguero</span>
        </a>
    </div>
    <hr/>
    <ul class="nav nav-pills flex-column mb-auto">
        <li><a href="<%=ctx%>/index.jsp"    class="nav-link text-white"><span class="sidebar-text">Inicio</span></a></li>
        <li><a href="<%=ctx%>/MisProductos" class="nav-link text-white"><span class="sidebar-text">Mis Productos</span></a></li>
        <li><a href="<%=ctx%>/Lotes"        class="nav-link text-white active"><span class="sidebar-text">Gestión de Lotes</span></a></li>
        <li><a href="<%=ctx%>/OrdenesCompra"class="nav-link text-white"><span class="sidebar-text">Órdenes de Compra</span></a></li>
    </ul>
</div>

<div class="main-content">
    <div class="d-flex justify-content-between align-items-center mb-3">
        <h2 class="display-6 fw-bold text-primary m-0">Gestión de lotes</h2>

        <!-- Filtro por producto -->
        <form class="d-flex gap-2" method="get" action="<%=ctx%>/Lotes">
            <select name="idProducto" class="form-select">
                <option value="">Todos los productos</option>
                <%
                    if (prods != null) {
                        for (Producto p : prods) {
                %>
                <option value="<%=p.getIdProducto()%>" <%= (idProducto!=null && idProducto==p.getIdProducto() ? "selected" : "") %>>
                    <%= p.getNombre() %> (<%= p.getSku() %>)
                </option>
                <%
                        }
                    }
                %>
            </select>
            <button class="btn btn-outline-primary" type="submit">Filtrar</button>
            <a class="btn btn-outline-secondary" href="<%=ctx%>/Lotes">Quitar filtro</a>
        </form>
    </div>

    <div class="table-responsive">
        <table id="tablaLotes" class="table align-middle">
            <thead class="table-light">
            <tr>
                <th>ID</th>
                <th>Producto</th>
                <th>Vencimiento</th>
                <th>Ubicación</th>
                <th>Stock</th>
            </tr>
            </thead>
            <!-- IMPORTANTE: usamos data-id y data-prod para que JS sepa qué editar/borrar -->
            <tbody id="tbodyLotes">
            <%
                if (lista != null && !lista.isEmpty()) {
                    for (Lote l : lista) {
            %>
            <tr data-id="<%= l.getIdLote() %>" data-prod="<%= l.getProducto_idProducto() %>">
                <td><%= l.getIdLote() %></td>
                <td><%= (l.getProductoNombre()==null?"—":l.getProductoNombre()) %></td>
                <td><%= (l.getFechaVencimiento()==null?"—":l.getFechaVencimiento()) %></td>
                <td><%= (l.getUbicacion()==null?"—":l.getUbicacion()) %></td>
                <td><%= l.getCantidad() %></td>
            </tr>
            <%
                }
            } else {
            %>
            <tr><td colspan="5" class="text-muted">No hay lotes para mostrar</td></tr>
            <% } %>
            </tbody>
        </table>
    </div>

    <div class="d-flex justify-content-center gap-3 mt-3">
        <button id="btnAdd"  type="button" class="btn btn-primary rounded-pill px-4 fw-bold">Añadir</button>
        <button id="btnEdit" type="button" class="btn btn-primary rounded-pill px-4 fw-bold" disabled>Modificar</button>
        <button id="btnDel"  type="button" class="btn btn-danger  rounded-pill px-4 fw-bold" disabled>Eliminar</button>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.0.2/dist/js/bootstrap.bundle.min.js" crossorigin="anonymous"></script>
<script>
    (() => {
        const ctx         = '<%= ctx %>';
        const idProducto  = '<%= idProducto == null ? "" : idProducto.toString() %>';

        document.getElementById('toggleButton')
            .addEventListener('click', ()=>document.getElementById('sidebar').classList.toggle('collapsed'));

        const tbody   = document.getElementById('tbodyLotes');
        const btnAdd  = document.getElementById('btnAdd');
        const btnEdit = document.getElementById('btnEdit');
        const btnDel  = document.getElementById('btnDel');

        let selected = null;

        // Selección de fila
        tbody.addEventListener('click', (e) => {
            const tr = e.target.closest('tr');
            if (!tr) return;
            if (selected) selected.classList.remove('table-active');
            selected = tr;
            tr.classList.add('table-active');
            btnEdit.disabled = false;
            btnDel.disabled  = false;
        });

        // Añadir: el servlet formCrear requiere idProducto; si no hay, envío a MisProductos
        btnAdd.addEventListener('click', () => {
            if (!idProducto) {
                alert('Primero elige un producto en "Mis Productos" y entra por "Ver detalle".');
                window.location.href = ctx + '/MisProductos';
                return;
            }
            window.location.href = ctx + '/Lotes?action=formCrear&idProducto=' + encodeURIComponent(idProducto);
        });

        // Modificar
        btnEdit.addEventListener('click', () => {
            if (!selected) { alert('Selecciona un lote'); return; }
            const idLote = selected.dataset.id;
            window.location.href = ctx + '/Lotes?action=editar&id=' + encodeURIComponent(idLote);
        });

        // Eliminar (mantiene el filtro si lo hay)
        btnDel.addEventListener('click', () => {
            if (!selected) { alert('Selecciona un lote'); return; }
            const idLote = selected.dataset.id;
            const prod   = selected.dataset.prod || idProducto;
            if (!confirm('¿Eliminar el lote ' + idLote + '?')) return;
            window.location.href = ctx + '/Lotes?action=borrar&id=' + encodeURIComponent(idLote)
                + (prod ? '&idProducto=' + encodeURIComponent(prod) : '');
        });
    })();
</script>
</body>
</html>
