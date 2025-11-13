<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ page import="java.util.List,com.example.telitobodeguero.beans.Lote,com.example.telitobodeguero.beans.Producto" %>
<%
    String ctx = request.getContextPath();
    List<Lote> lista      = (List<Lote>) request.getAttribute("lista");
    List<Producto> prods  = (List<Producto>) request.getAttribute("productos");
    Integer idProducto    = (Integer) request.getAttribute("idProducto");
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
        body {
            margin:0;
            background:#f3f5f7;
            font-family: 'Inter', system-ui, -apple-system, Segoe UI, Roboto, Helvetica, Arial, sans-serif;
        }

        /* ==== SIDEBAR ==== */
        .sidebar{
            position:fixed;
            inset:0 auto 0 0;
            width:280px;
            background:#212529;
            color:#fff;
            z-index:1000;
            overflow-y:auto;
            transition:width .25s ease;
        }
        .sidebar.collapsed { width:80px; }
        .sidebar.collapsed .text-label{ display:none; }

        .sidebar .brand{
            padding:1rem 1.25rem;
            display:flex;
            align-items:center;
            gap:.75rem;
        }

        .sidebar .brand .toggle{
            border:0;
            background:#0d6efd;
            color:#fff;
            padding:.5rem .6rem;
            border-radius:.5rem;
        }

        .sidebar .nav-link{ color:#d6d6d6; }
        .sidebar .nav-link:hover,
        .sidebar .nav-link:focus{
            background:#0d6efd;
            color:#fff;
        }

        /* ==== MAIN ==== */
        .main{
            margin-left:280px;
            padding:2rem;
            min-height:100vh;
            transition:margin-left .25s ease;
        }
        .main.collapsed{ margin-left:80px; }

        h2{
            font-weight:800;
            color:#2e63f5;
            text-transform:uppercase;
            letter-spacing:.3px;
        }

        /* ==== TABLA ==== */
        .table-responsive{
            border-radius:12px;
            overflow-x:auto !important;
        }

        table thead th{
            position:sticky;
            top:0;
            background:#e9ecef !important;
            z-index:3;
            border-bottom:2px solid #ced4da;
            font-weight:600;
        }

        table tbody tr:nth-child(odd){ background:#ffffff; }
        table tbody tr:nth-child(even){ background:#f7f8fa; }

        td, th{
            white-space:nowrap;
            padding:.65rem .75rem;
            vertical-align:middle;
        }

        #tablaLotes tbody tr.table-active td{
            background:#cfe2ff !important;
        }

        /* =====================================================
           BOTONES CUADRADOS — ESTILO APLICADO EN TODA LA VISTA
           ===================================================== */
        .btn,
        .btn-primary,
        .btn-danger,
        .btn-outline-primary,
        .btn-outline-secondary,
        .rounded-pill {
            border-radius: 6px !important;     /* ← BOTONES CUADRADOS */
        }

        .btn-primary {
            background:#2e63f5 !important;
            border:none !important;
            padding:.45rem 1.3rem !important;
            font-weight:600 !important;
        }

        .btn-danger {
            padding:.45rem 1.3rem !important;
            font-weight:700 !important;
        }

        .btn-outline-primary,
        .btn-outline-secondary {
            border-radius:6px !important;
            border-width:1px !important;
            font-weight:600 !important;
        }

        #btnAdd,
        #btnEdit,
        #btnDel {
            border-radius:6px !important;
            padding:.45rem 1.4rem !important;
        }

        /* ==== RESPONSIVE ==== */
        @media (max-width: 1024px){
            .main{
                padding:1.5rem;
            }
        }

        @media (max-width: 768px){
            .main{
                padding:1.2rem;
            }

            h2{
                text-align:center;
                font-size:1.4rem;
            }

            .d-flex.gap-2{
                flex-wrap:wrap;
                gap:1rem !important;
            }

            .d-flex.gap-2 select,
            .d-flex.gap-2 button,
            .d-flex.gap-2 a{
                width:100%;
            }

            .d-flex.justify-content-center.gap-3{
                flex-wrap:wrap;
                gap:1rem!important;
            }

            .btn-primary, .btn-danger{
                width:100%;
            }
        }

        @media (max-width: 480px){
            .main{
                padding:1rem;
            }

            td, th{
                font-size:.8rem;
            }

            .d-flex.justify-content-between{
                flex-direction:column;
                gap:0.8rem;
                text-align:center;
            }
        }
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
        <button id="btnAdd"  type="button" class="btn btn-primary fw-bold">Añadir</button>
        <button id="btnEdit" type="button" class="btn btn-primary fw-bold" disabled>Modificar</button>
        <button id="btnDel"  type="button" class="btn btn-danger  fw-bold" disabled>Eliminar</button>
    </div>

</main>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.0.2/dist/js/bootstrap.bundle.min.js" crossorigin="anonymous"></script>

<script>
    (() => {

        const ctx         = '<%= ctx %>';
        const idProducto  = '<%= idProducto == null ? "" : idProducto.toString() %>';

        const selectProducto = document.querySelector('select[name="idProducto"]');

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
            const idSel = selectProducto.value;

            if (!idSel || idSel === "") {
                alert('Selecciona primero un producto del filtro para añadir un lote.');
                selectProducto.focus();
                return;
            }

            window.location.href =
                ctx + '/Lotes?action=formCrear&idProducto=' + encodeURIComponent(idSel);
        });

        // Editar
        btnEdit.addEventListener('click', () => {
            if (!selected) { alert('Selecciona un lote'); return; }
            const idLote = selected.dataset.id;

            window.location.href =
                ctx + '/Lotes?action=editar&id=' + encodeURIComponent(idLote);
        });

        // Eliminar
        btnDel.addEventListener('click', () => {
            if (!selected) { alert('Selecciona un lote'); return; }

            const idLote = selected.dataset.id;
            const prod   = selected.dataset.prod || selectProducto.value;

            if (!confirm('¿Eliminar el lote ' + idLote + '?')) return;

            window.location.href =
                ctx + '/Lotes?action=borrar&id=' + encodeURIComponent(idLote)
                + (prod ? '&idProducto=' + encodeURIComponent(prod) : '');
        });

    })();
</script>

</body>
</html>