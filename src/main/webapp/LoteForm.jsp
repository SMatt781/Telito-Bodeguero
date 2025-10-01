<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ page import="beans.Lote, beans.Producto, java.util.List" %>
<%
    String ctx = request.getContextPath();
    Lote lote = (Lote) request.getAttribute("lote"); // null si es crear
    List<Producto> productos = (List<Producto>) request.getAttribute("productos");
    Integer idProducto = (Integer) request.getAttribute("idProducto"); // preselección opcional
    boolean esEdicion = (lote != null);
    if (esEdicion) { idProducto = lote.getProductoId(); }
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title><%= esEdicion ? "Editar" : "Nuevo" %> Lote</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.0.2/dist/css/bootstrap.min.css"
          rel="stylesheet" crossorigin="anonymous" />
</head>
<body class="bg-light">
<div class="container py-4">
    <h3 class="mb-3"><%= esEdicion ? "Editar" : "Nuevo" %> Lote</h3>

    <form method="post" action="<%=ctx%>/Lotes">
        <input type="hidden" name="action" value="guardar">
        <% if (esEdicion) { %>
        <input type="hidden" name="idLote" value="<%= lote.getIdLote() %>">
        <% } %>

        <div class="mb-3">
            <label class="form-label">Producto</label>
            <select name="idProducto" class="form-select" <%= esEdicion ? "disabled" : "" %> required>
                <option value="" disabled <%= (idProducto==null?"selected":"") %>>Seleccione...</option>
                <%
                    if (productos != null) {
                        for (Producto p : productos) {
                %>
                <option value="<%=p.getIdProducto()%>"
                        <%= (idProducto!=null && idProducto.equals(p.getIdProducto()) ? "selected" : "") %>>
                    <%= p.getNombre() %> (<%= p.getSku() %>)
                </option>
                <%
                        }
                    }
                %>
            </select>
            <%-- Si edición, enviamos el idProducto real en oculto (porque el select está disabled) --%>
            <% if (esEdicion) { %>
            <input type="hidden" name="idProducto" value="<%= idProducto %>">
            <% } %>
        </div>

        <div class="mb-3">
            <label class="form-label">Fecha de vencimiento</label>
            <input type="date" name="fechaVencimiento" class="form-control"
                   value="<%= esEdicion && lote.getFechaVencimiento()!=null ? lote.getFechaVencimiento().toString() : "" %>">
        </div>

        <div class="mb-3">
            <label class="form-label">Ubicación</label>
            <input type="text" name="ubicacion" class="form-control"
                   value="<%= esEdicion && lote.getUbicacion()!=null ? lote.getUbicacion() : "" %>" maxlength="45">
        </div>

        <div class="mb-3">
            <label class="form-label">Cantidad</label>
            <input type="number" name="cantidad" class="form-control" min="0" required
                   value="<%= esEdicion ? lote.getCantidad() : "" %>">
        </div>

        <%-- Si llegaste filtrado y quieres mantener el filtro al volver, manda keepFilter=1 --%>
        <input type="hidden" name="keepFilter" value="<%= (request.getParameter("idProducto")!=null || esEdicion) ? "1":"0" %>">

        <div class="d-flex gap-2">
            <button class="btn btn-primary" type="submit">Guardar</button>
            <a class="btn btn-secondary" href="<%=ctx%>/Lotes<%= (idProducto!=null?("?idProducto="+idProducto):"") %>">Cancelar</a>
        </div>
    </form>
</div>
</body>
</html>

