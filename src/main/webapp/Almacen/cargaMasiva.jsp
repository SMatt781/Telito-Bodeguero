<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" import="java.util.*,java.time.*" %>
<%
    // Objetos que el servlet coloca
    @SuppressWarnings("unchecked")
    List<?> preview = (List<?>) request.getAttribute("preview");
    // lista de filas con errores/ok
    Boolean tieneErrores = (Boolean) request.getAttribute("tieneErrores");
    Integer insertados = (Integer) request.getAttribute("insertados");
    String mensaje = (String) request.getAttribute("mensaje");
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Carga masiva desde Excel (validar → confirmar)</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">

    <style>
        body {
            display: flex;
            margin: 0;
            overflow-x: hidden;
           background: #f7f7f9;
        }
        .sidebar{
            position:fixed; inset:0 auto 0 0;       /* top:0; left:0; bottom:0 */
            width:280px; background:#212529; color:#fff;
            z-index:1000; transition:width .25s ease;
            overflow-y:auto;
            display: flex;
            flex-direction: column;
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

        .nav-link.text-white:hover {
            background-color: #0d6efd;
            color: #fff !important;
        }
        .main-content {
            flex-grow: 1;
            padding: 3rem;
            background-color: #f8f9fa;
            margin-left: 280px; /* Margen para dejar espacio a la barra lateral */
            transition: margin-left 0.3s ease-in-out; /* Animación para el cambio de margen */
            min-height: 100vh;
        }

        .main.collapsed{ margin-left:80px;
        }
        .titulo-principal {
            font-size: 3rem;
        }
        .container{
            max-width:1100px;
        }
    </style>
</head>
<body>

<jsp:include page="/sidebar.jsp" />

<main class="main" id="main">
    <div class="container py-4">
        <h3 class="mb-3">Carga masiva desde Excel</h3>

        <div class="card mb-4">
            <div class="card-body">
                <form action="<%=request.getContextPath()%>/cargaExcel" method="post" enctype="multipart/form-data">
                    <input type="hidden" name="accion" value="validar">
                    <div class="row g-3 align-items-end">
                        <div class="col-md-4">
                            <label class="form-label">Zona (id)</label>
                            <input type="number" name="zonaId" class="form-control" value="1" required>
                        </div>
                        <div class="col-md-8">
                            <label class="form-label">Archivo Excel (.xlsx)</label>
                            <input type="file" name="archivo" accept=".xlsx" class="form-control" required>
                        </div>
                    </div>
                    <div class="form-text mt-2">El Excel debe tener encabezados en la fila 1: <code>sku, zona, fecha, cantidad, lote, fecha_vencimiento</code></div>
                    <button class="btn btn-primary mt-3" type="submit">Validar</button>
                </form>
                </div>
        </div>

        <% if (preview != null) { %>
        <div class="card mb-4">
            <div class="card-header">Resultado de validación</div>
            <div class="card-body">
                <div class="alert <%= (tieneErrores != null && tieneErrores) ? "alert-danger" : "alert-success" %>">
                <%= (tieneErrores != null && tieneErrores) ? "Hay errores. Corrige el archivo y vuelve a validar." : "Todo OK. Puedes confirmar." %>
            </div>
                <div class="table-responsive">
                    <table class="table table-sm table-striped align-middle">
                        <thead class="table-light">
                        <tr>
                            <th>#</th> <th>SKU</th>
                            <th>Zona</th>
                            <th>Fecha</th>
                            <th>Cantidad</th> <th>Lote</th>
                            <th>F. Venc.</th>
                            <th>Estado</th>
                        </tr> </thead>
                        <tbody>
                        <% for (Object o : preview) {
                            Class<?> c = o.getClass();
                            int idx = (Integer) c.getField("index").get(o);
                            String sku = (String) c.getField("sku").get(o);
                            Integer zona = (Integer) c.getField("zonaId").get(o);
                            String fecha = String.valueOf(c.getField("fecha").get(o));
                            Integer cantidad = (Integer) c.getField("cantidad").get(o);
                            Integer lote = (Integer) c.getField("loteId").get(o);
                            String fv = String.valueOf(c.getField("fechaVenc").get(o));
                            String error = (String) c.getField("error").get(o);
                        %>
                        <tr>
                            <td><%=idx%></td>
                            <td><%=sku%></td>
                            <td><%=zona%></td>
                            <td><%=fecha%></td>
                            <td><%=cantidad%></td>
                            <td><%=lote%></td>
                            <td><%=fv%></td>
                            <td class="<%= (error == null) ? "text-success" : "text-danger" %>">
                            <%= (error == null) ? "OK" : error %>
                        </td>
                        </tr>
                        <% } %>
                        </tbody>
                    </table>
                </div>

                <% if (tieneErrores != null && !tieneErrores) { %>
                <form action="<%=request.getContextPath()%>/cargaExcel" method="post" class="mt-3">
                    <input type="hidden" name="accion" value="confirmar">
                    <button class="btn btn-success" type="submit">Confirmar e insertar</button>
                </form>
                <% } %>
            </div>
        </div>
        <% } %>

        <% if (insertados != null) { %>
        <div class="alert alert-info">
            Insertados: <strong><%=insertados%></strong>
            <%= (mensaje != null ? (" - " + mensaje) : "") %>
        </div>
        <% } %>
    </div>
</main>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>

<script>
    // Toggle del sidebar
    const btn = document.getElementById('btnToggle');
    const sidebar = document.getElementById('sidebar');
    const main = document.getElementById('main');
    btn.addEventListener('click', () => {
        sidebar.classList.toggle('collapsed');
        main.classList.toggle('collapsed');
    });

</script>
</body>
</html>
