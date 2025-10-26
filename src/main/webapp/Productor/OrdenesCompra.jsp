<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="com.example.telitobodeguero.beans.OrdenCompra" %>
<%@ page import="com.example.telitobodeguero.beans.Usuarios" %>
<%@ page import="com.example.telitobodeguero.beans.Lote" %>

<%
    String ctx = request.getContextPath();
    // 1. Obtener listas y data del modal
    List<OrdenCompra> filas = (List<OrdenCompra>) request.getAttribute("filas");
    Usuarios usuarioLog = (Usuarios) session.getAttribute("usuarioLog");

    OrdenCompra ocDetalle = (OrdenCompra) session.getAttribute("ocDetalle");
    List<Lote> lotesDisponibles = (List<Lote>) session.getAttribute("lotesDisponibles");

    // 2. Manejo de Mensajes (successMsg, errorMsg)
    String successMsg = (String) session.getAttribute("successMsg");
    String errorMsg = (String) session.getAttribute("errorMsg");

    if (successMsg != null) session.removeAttribute("successMsg");
    if (errorMsg != null) session.removeAttribute("errorMsg");

    // 3. Limpiar data del modal después de obtenerla para mostrarla
    if (ocDetalle != null) {
        session.removeAttribute("ocDetalle");
        session.removeAttribute("lotesDisponibles");
    }
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="utf-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1" />
    <title>Órdenes de Compra - Telito Bodeguero</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.0.2/dist/css/bootstrap.min.css"
          rel="stylesheet" crossorigin="anonymous" />

    <style>
        /* ... Estilos CSS ... (Mantener estilos) */
        :root { --brand:#2e63f5; }
        body{display:flex;min-height:100vh;}
        .sidebar{
            position:fixed; inset:0 auto 0 0;
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
        .sidebar.collapsed .text-label{ display:none; }

        .main{
            margin-left:280px; transition:margin-left .25s ease;
            min-height:100vh; padding:2rem;
        }
        .main.collapsed{ margin-left:80px; }
        .nav-link.text-white:hover{background:#0d6efd;color:#fff!important;}

        @media (max-width: 991.98px) {
            .sidebar:not(.collapsed) { width: 0; overflow: hidden; transition: width 0s; }
            .main { margin-left: 0 !important; }
        }
        @media (min-width: 992px) {
            .sidebar.collapsed { width: 80px; }
            .main.collapsed { margin-left: 80px; }
        }
    </style>
</head>
<body>

<jsp:include page="/sidebar.jsp" />

<main class="main" id="main">
    <div class="d-flex align-items-center justify-content-between">
        <h1 class="text-uppercase m-0 text-primary fw-bold">Órdenes de compra</h1>
        <a href="<%=ctx%>/Lotes" class="btn btn-outline-secondary btn-sm">Gestionar Lotes</a>
    </div>
    <hr>

    <%-- Mostrar Mensajes --%>
    <% if (successMsg != null && !successMsg.isEmpty()) { %>
    <div class="alert alert-success alert-dismissible fade show" role="alert">
        <%= successMsg %>
        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
    </div>
    <% } %>
    <% if (errorMsg != null && !errorMsg.isEmpty()) { %>
    <div class="alert alert-danger alert-dismissible fade show" role="alert">
        <strong>Error:</strong> <%= errorMsg %>
        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
    </div>
    <% } %>
    <%-- Fin Mostrar Mensajes --%>

    <p class="text-muted">A continuación se muestran los **ítems de órdenes de compra** que requieren ser despachados desde su stock.</p>

    <div class="table-responsive">
        <table id="tablaOC" class="table table-secondary table-striped align-middle">
            <thead>
            <tr>
                <th>OC #</th>
                <th>Ítem ID</th>
                <th>Estado OC</th>
                <th>Fecha Llegada</th>
                <th>Producto</th>
                <th>Cantidad Requerida</th>
                <th>Acción</th>
            </tr>
            </thead>
            <tbody>
            <%
                if (filas != null && !filas.isEmpty()) {
                    for (OrdenCompra r : filas) {

                        String estado = r.getEstado();
                        String badgeClass;

                        if ("Enviada".equals(estado)) badgeClass = "bg-warning text-dark";
                        else if ("Recibido".equals(estado)) badgeClass = "bg-info text-dark";
                        else if ("En tránsito".equals(estado)) badgeClass = "bg-primary text-white";
                        else badgeClass = "bg-secondary";

                        boolean puedeDespachar = (r.getLote() == null || r.getLote().getIdLote() == 0)
                                && !"En tránsito".equals(estado);
            %>

            <tr>
                <td><%= r.getCodigoOrdenCompra() %></td>
                <td><strong><%= r.getIdItem() %></strong></td>
                <td><span class="badge <%= badgeClass %>"><%= estado %></span></td>
                <td><%= r.getFechaLlegada() == null ? "—" : r.getFechaLlegada() %></td>
                <td><%= r.getProducto().getNombre() %></td>
                <td><%= r.getCantidad() %></td>
                <td>
                    <% if (estado.equals("Enviada")) { %>
                    <a href="<%=ctx%>/OrdenesCompraProd?a=recibir&id=<%= r.getCodigoOrdenCompra() %>"
                       class="btn btn-sm btn-outline-success">Confirmar Recepción OC</a>
                    <% } else if (estado.equals("Recibido") && puedeDespachar) { %>

                    <%-- Botón que llama al doGet con a=mostrar_despacho y el ID del ÍTEM --%>
                    <a href="<%=ctx%>/OrdenesCompraProd?a=mostrar_despacho&idItem=<%= r.getIdItem() %>"
                       class="btn btn-sm btn-primary">
                        Despachar Ítem
                    </a>

                    <% } else if (estado.equals("En tránsito")) { %>
                    <span class="badge bg-success">Despachado</span>
                    <% } else { %>
                    <span class="text-muted">No Aplica</span>
                    <% } %>
                </td>
            </tr>
            <% } } else { %>
            <tr><td colspan="7" class="text-muted text-center">No hay ítems pendientes de despacho.</td></tr>
            <% } %>
            </tbody>
        </table>
    </div>

    <div class="d-flex justify-content-between align-items-center mt-2">
        <small class="text-muted"><% if (filas != null) { %>Mostrando <%= filas.size() %> ítem(s) pendiente(s)<% } %></small>
        <button class="btn btn-outline-primary btn-sm" onclick="exportarCSV()">Exportar CSV</button>
    </div>

    <%-- ========================================================== --%>
    <%-- MODAL DE DESPACHO (Activado por atributo de SESIÓN) --%>
    <%-- ========================================================== --%>
    <% if (ocDetalle != null) {
        boolean hayLotes = lotesDisponibles != null && !lotesDisponibles.isEmpty();
    %>
    <div class="modal fade" id="despachoModal" tabindex="-1" aria-labelledby="despachoModalLabel" aria-hidden="true">
        <div class="modal-dialog">
            <div class="modal-content">
                <form method="POST" action="<%= ctx %>/OrdenesCompraProd">
                    <input type="hidden" name="a" value="despachar" />

                    <%-- CAMPOS OCULTOS CRUCIALES (con data de ocDetalle) --%>
                    <input type="hidden" name="idItemDespachar" value="<%= ocDetalle.getIdItem() %>" />
                    <input type="hidden" name="cantidadRequerida" value="<%= ocDetalle.getCantidad() %>" />

                    <div class="modal-header">
                        <h5 class="modal-title" id="despachoModalLabel">Despachar Ítem de OC #<%= ocDetalle.getCodigoOrdenCompra() %></h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                    </div>
                    <div class="modal-body">
                        <p>Ítem ID: <strong><%= ocDetalle.getIdItem() %></strong></p>
                        <p>Producto: <strong><%= ocDetalle.getProducto().getNombre() %></strong></p>
                        <p>Cantidad a Descontar: <strong class="text-danger"><%= ocDetalle.getCantidad() %></strong></p>

                        <hr>

                        <div class="mb-3">
                            <label for="idLoteSeleccionado" class="form-label">Seleccione Lote de Stock:</label>
                            <select name="idLoteSeleccionado" id="idLoteSeleccionado" class="form-select" required>
                                <% if (hayLotes) { %>
                                <option value="">-- Seleccione un Lote --</option>
                                <% for (Lote lote : lotesDisponibles) { %>
                                <option value="<%= lote.getIdLote() %>">
                                    Lote #<%= lote.getIdLote() %> | Stock: <%= lote.getCantidad() %> | Vence: <%= lote.getFechaVencimiento() %>
                                </option>
                                <% } %>
                                <% } else { %>
                                <option value="" disabled>-- ¡ADVERTENCIA! No hay lotes disponibles con stock. --</option>
                                <% } %>
                            </select>
                            <small class="text-muted">Se descontará la cantidad requerida del lote seleccionado.</small>
                        </div>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancelar</button>
                        <button type="submit" class="btn btn-primary" <% if (!hayLotes) { %>disabled<% } %>>Confirmar Despacho</button>
                    </div>
                </form>
            </div>
        </div>
    </div>
    <% } %>

</main>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.0.2/dist/js/bootstrap.bundle.min.js" crossorigin="anonymous"></script>

<script>
    // ... (El resto de sus funciones JavaScript: exportarCSV, toggle, etc.) ...
    const btn = document.getElementById('btnToggle');
    const sidebar = document.getElementById('sidebar');
    const main = document.getElementById('main');
    if (btn && sidebar && main) {
        btn.addEventListener('click', () => {
            sidebar.classList.toggle('collapsed');
            main.classList.toggle('collapsed');
        });
    }

    function exportarCSV(){
        const filas = Array.from(document.querySelectorAll('#tablaOC tr'))
            .map(tr => Array.from(tr.children)
                .map(td => '"' + td.innerText.replace(/"/g,'""') + '"')
                .join(','))
            .join('\n');
        const blob = new Blob([filas], {type: 'text/csv;charset=utf-8;'});
        const a = document.createElement('a');
        a.href = URL.createObjectURL(blob);
        a.download = 'ordenes_compra_pendientes.csv';
        a.click();
    }

    // Código para mostrar el modal automáticamente si hay data en la sesión
    window.addEventListener('load', () => {
        // Usamos una variable de JSP para chequear la condición sin JSTL
        const ocDetalleExists = <%= ocDetalle != null %>;

        if (ocDetalleExists) {
            const modalElement = document.getElementById('despachoModal');
            if (modalElement) {
                // Hay que usar jQuery o Bootstrap 5 nativo
                const despachoModal = new bootstrap.Modal(modalElement);
                despachoModal.show();
            }
        }
    });
</script>
</body>
</html>