<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ page import="com.example.telitobodeguero.beans.Lote, com.example.telitobodeguero.beans.Producto, java.util.List" %>
<%
    String ctx = request.getContextPath();
    Lote lote = (Lote) request.getAttribute("lote"); // null si es crear
    List<Producto> productos = (List<Producto>) request.getAttribute("productos");

    // idProducto es String para coincidir con Lote.getProducto_idProducto()
    String idProducto = (String) request.getAttribute("idProducto"); // preselección opcional
    boolean esEdicion = (lote != null);
    if (esEdicion) { idProducto = lote.getProducto_idProducto(); }

    Integer disponibleAttr = (Integer) request.getAttribute("disponible");
    int disponible = (disponibleAttr == null ? 0 : disponibleAttr);
    String error = (String) request.getAttribute("error");

    boolean sinStockParaCrear = (!esEdicion && disponible <= 0);
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title><%= esEdicion ? "Editar" : "Nuevo" %> Lote</title>

    <meta name="viewport" content="width=device-width, initial-scale=1" />
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.0.2/dist/css/bootstrap.min.css"
          rel="stylesheet" crossorigin="anonymous" />

    <style>
        body{display:flex;min-height:100vh;background:#f7f9fc}
        .sidebar{width:280px;transition:width .3s ease}
        .sidebar.collapsed{width:84px}.sidebar.collapsed .sidebar-text{display:none}
        .nav-link.text-white:hover{background:#0d6efd;color:#fff!important}
        .main-content{flex:1;padding:2rem}
        .card{border:0;box-shadow:0 10px 25px rgba(16,24,40,.05)}
        .form-help{font-size:.875rem;color:#6c757d}
        .badge-soft{background:#e7f1ff;color:#0d6efd}
        .btn-pill{border-radius:50rem;padding:.5rem 1.25rem;font-weight:700}
        .muted{opacity:.7}
        .hint{font-variant-numeric:tabular-nums}
        .input-suffix{position:absolute;right:.75rem;top:50%;transform:translateY(-50%);pointer-events:none}
        .fi{font-style:normal;opacity:.65}
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
        <li><a class="nav-link text-white" href="<%=ctx%>/MisProductos"><span class="sidebar-text">Mis productos</span></a></li>
        <li><a class="nav-link text-white active" href="<%=ctx%>/Lotes"><span class="sidebar-text">Gestión de Lotes</span></a></li>
        <li><a class="nav-link text-white" href="<%=ctx%>/OrdenesCompra"><span class="sidebar-text">Órdenes de Compra</span></a></li>
    </ul>
</div>

<!-- Main -->
<div class="main-content">
    <!-- Header -->
    <div class="d-flex justify-content-between align-items-center mb-3">
        <div>
            <h2 class="display-6 fw-bold text-primary m-0"><%= esEdicion ? "Editar" : "Nuevo" %> lote</h2>
            <small class="text-muted">Completa los datos del lote. No se permite exceder el stock disponible.</small>
        </div>
        <a class="btn btn-outline-secondary btn-sm btn-pill"
           href="<%=ctx%>/Lotes<%= (idProducto!=null?("?idProducto="+idProducto):"") %>">↩ Volver</a>
    </div>

    <!-- Mensajes -->
    <% if (error != null) { %>
    <div class="alert alert-danger d-flex align-items-center" role="alert">
        <span class="me-2">⚠️</span> <%= error %>
    </div>
    <% } %>

    <div class="card">
        <div class="card-body">
            <form method="post" action="<%=ctx%>/Lotes" class="row g-3 position-relative">
                <input type="hidden" name="action" value="guardar">
                <% if (esEdicion) { %>
                <input type="hidden" name="idLote" value="<%= lote.getIdLote() %>">
                <% } %>

                <!-- Producto -->
                <div class="col-12">
                    <label class="form-label">Producto</label>
                    <select name="idProducto" class="form-select" <%= esEdicion ? "disabled" : "" %> required>
                        <option value="" disabled <%= (idProducto==null?"selected":"") %>>Seleccione…</option>
                        <%
                            if (productos != null) {
                                for (Producto p : productos) {
                                    String optVal = String.valueOf(p.getIdProducto());
                        %>
                        <option value="<%= optVal %>"
                                <%= (idProducto!=null && idProducto.equals(optVal) ? "selected" : "") %>>
                            <%= p.getNombre() %> (<%= p.getSku() %>)
                        </option>
                        <%      }
                        }
                        %>
                    </select>
                    <%-- En edición, el select está deshabilitado; mandamos valor real oculto --%>
                    <% if (esEdicion) { %>
                    <input type="hidden" name="idProducto" value="<%= idProducto %>">
                    <% } %>
                    <div class="form-help">El cambio de producto no está permitido en edición.</div>
                </div>

                <!-- Fila info stock -->
                <div class="col-12 d-flex justify-content-between align-items-center">
                    <div class="form-help">
                        <span class="badge badge-soft rounded-pill px-3 py-2">
                            Stock disponible: <span id="lblDisponible" class="hint"><%= disponible %></span> u.
                        </span>
                        <span class="ms-2 muted">(<%= esEdicion ? "máximo permitido para editar" : "máximo para crear" %>)</span>
                    </div>
                    <% if (sinStockParaCrear) { %>
                    <div class="text-danger small">No hay stock disponible para crear un nuevo lote.</div>
                    <% } %>
                </div>

                <!-- Vencimiento (String en tu bean) -->
                <div class="col-md-6">
                    <label class="form-label">Fecha de vencimiento</label>
                    <input type="date" name="fechaVencimiento" class="form-control"
                           value="<%= esEdicion && lote.getFechaVencimiento()!=null ? lote.getFechaVencimiento() : "" %>">
                </div>

                <!-- Ubicación -->
                <div class="col-md-6">
                    <label class="form-label">Ubicación</label>
                    <input type="text" name="ubicacion" class="form-control"
                           value="<%= esEdicion && lote.getUbicacion()!=null ? lote.getUbicacion() : "" %>"
                           maxlength="45" placeholder="Almacén A, Estante 3, Fila 2">
                </div>

                <!-- Cantidad -->
                <div class="col-md-6 position-relative">
                    <label class="form-label">Cantidad</label>
                    <input type="number"
                           name="cantidad"
                           class="form-control"
                           min="1"
                           max="<%= Math.max(disponible, 0) %>"
                           step="1"
                           inputmode="numeric"
                           value="<%= esEdicion ? lote.getCantidad() : "" %>"
                        <%= (sinStockParaCrear ? "disabled" : "") %>
                           required>
                    <span class="input-suffix fi">unid.</span>
                    <div class="form-help">
                        Máximo permitido: <span id="lblMax" class="hint"><%= Math.max(disponible, 0) %></span>.
                        <span id="lblRestante" class="ms-2 hint"></span>
                    </div>
                </div>

                <!-- Acciones -->
                <input type="hidden" name="keepFilter" value="<%= (request.getParameter("idProducto")!=null || esEdicion) ? "1":"0" %>">
                <div class="col-12 d-flex gap-2">
                    <button class="btn btn-primary btn-pill" type="submit"
                            <%= (sinStockParaCrear ? "disabled" : "") %>>
                        Guardar
                    </button>
                    <a class="btn btn-outline-secondary btn-pill"
                       href="<%=ctx%>/Lotes<%= (idProducto!=null?("?idProducto="+idProducto):"") %>">Cancelar</a>
                </div>
            </form>
        </div>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.0.2/dist/js/bootstrap.bundle.min.js" crossorigin="anonymous"></script>
<script>
    // Sidebar toggle
    document.getElementById('toggleButton')
        .addEventListener('click',()=>document.getElementById('sidebar').classList.toggle('collapsed'));

    // UX: clamp y feedback de cantidad vs máximo
    (function () {
        const inp = document.querySelector('input[name="cantidad"]');
        if (!inp) return;
        const max = parseInt(inp.max || '0', 10);
        const restante = document.getElementById('lblRestante');

        function update() {
            let v = parseInt(inp.value || '0', 10);
            if (isNaN(v)) v = 0;
            if (max && v > max) {
                inp.value = max;
                v = max;
            }
            if (v <= 0) {
                restante.textContent = '';
            } else {
                const r = max - v;
                restante.textContent = 'Te quedarán: ' + (r < 0 ? 0 : r) + ' u.';
            }
        }
        inp.addEventListener('input', update);
        update();
    })();
</script>
</body>
</html>
