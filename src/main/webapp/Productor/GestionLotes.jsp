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
        .main-content{flex:1;padding:2rem;background:#f8f9fa}
        #tablaLotes tbody tr{cursor:pointer}
        #tablaLotes tbody tr.table-active td{background:#cfe2ff!important}
    </style>
</head>
<body>

<jsp:include page="/sidebar.jsp" />

<main class="main" id="main">
    <div class="d-flex justify-content-between align-items-center mb-3">
        <h2 class="display-6 fw-bold text-primary m-0">Gestión de lotes</h2>

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
                <th>Stock</th>
            </tr>
            </thead>
            <tbody id="tbodyLotes">
            <%
                if (lista != null && !lista.isEmpty()) {
                    for (Lote l : lista) {
            %>
            <tr data-id="<%= l.getIdLote() %>" data-prod="<%= l.getProducto_idProducto() %>">
                <td><%= l.getIdLote() %></td>
                <td><%= (l.getProductoNombre()==null?"—":l.getProductoNombre()) %></td>
                <td><%= (l.getFechaVencimiento()==null?"—":l.getFechaVencimiento()) %></td>
                <td><%= l.getCantidad() %></td>
            </tr>
            <%
                }
            } else {
            %>
            <tr><td colspan="4" class="text-muted">No hay lotes para mostrar</td></tr>
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
</main>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.0.2/dist/js/bootstrap.bundle.min.js" crossorigin="anonymous"></script>
<script>
    (() => {
        const ctx         = '<%= ctx %>';
        const idProducto  = '<%= idProducto == null ? "" : idProducto.toString() %>';

        // 🎯 Nuevo: Referencia al select de filtro
        const selectProducto = document.querySelector('select[name="idProducto"]');

        // Toggle del sidebar
        const btn = document.getElementById('btnToggle');
        const sidebar = document.getElementById('sidebar');
        const main = document.getElementById('main');
        btn.addEventListener('click', () => {
            sidebar.classList.toggle('collapsed');
            main.classList.toggle('collapsed');
        });

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

        // Añadir
        btnAdd.addEventListener('click', () => {
            const idSeleccionado = selectProducto.value; // Obtiene el valor del producto seleccionado

            if (!idSeleccionado || idSeleccionado === "") {
                alert('Por favor, selecciona primero un producto del filtro para añadir un lote.');
                selectProducto.focus();
                return;
            }

            // Redirige al Servlet con la acción formCrear y el ID del producto seleccionado
            window.location.href = ctx + '/Lotes?action=formCrear&idProducto=' + encodeURIComponent(idSeleccionado);
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
            // Usa el valor del select como filtro de retorno
            const prod   = selected.dataset.prod || selectProducto.value;
            if (!confirm('¿Eliminar el lote ' + idLote + '?')) return;
            window.location.href = ctx + '/Lotes?action=borrar&id=' + encodeURIComponent(idLote)
                + (prod ? '&idProducto=' + encodeURIComponent(prod) : '');
        });
    })();
</script>
</body>
</html>