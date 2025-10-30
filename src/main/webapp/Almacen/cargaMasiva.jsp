<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="java.net.URLDecoder" %>
<%
    String ctx = request.getContextPath();
    String statusMessageEncoded = request.getParameter("statusMessage");
    String statusMessage = null;

    // Decodificar mensaje de la redirección
    if (statusMessageEncoded != null) {
        try {
            statusMessage = URLDecoder.decode(statusMessageEncoded, "UTF-8");
        } catch (Exception e) {
            statusMessage = "error|Error al leer el mensaje.";
        }
    }

    // Atributos del Servlet para errores de validación
    List<String> erroresCarga = (List<String>) request.getAttribute("erroresCarga");
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Carga Masiva de Entradas · Almacén</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.0.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.5.0/font/bootstrap-icons.css" rel="stylesheet">

    <style>
        /* ===== Desktop (default) ===== */
        body{ min-height:100vh; background:#f8f9fa; overflow-x:hidden; }

        .sidebar{
            position:fixed; top:0; left:0; bottom:0;
            width:280px; background:#212529; color:#fff;
            z-index:1000; transition:width .25s ease;
            overflow-y:auto; display:flex; flex-direction:column;
        }
        .sidebar.collapsed{ width:80px; }
        .sidebar .brand{ padding:1rem 1.25rem; display:flex; align-items:center; gap:.75rem; }
        .sidebar .brand .toggle{ border:0; background:#0d6efd; color:#fff; padding:.5rem .6rem; border-radius:.5rem; }
        .sidebar .nav-link{ color:#d6d6d6; }
        .sidebar .nav-link:hover,.sidebar .nav-link:focus{ background:#0d6efd; color:#fff; }
        .sidebar .dropdown-menu{ background:#2b3035; }
        .sidebar .dropdown-item{ color:#fff; }
        .sidebar .dropdown-item:hover{ background:#0d6efd; }
        .sidebar.collapsed .text-label{ display:none; }

        .content{
            margin-left:280px; transition:margin-left .25s ease;
            padding:2rem 1rem; min-height:100vh;
        }
        .content.collapsed{ margin-left:80px; }

        .page-wrap{ max-width:1100px; margin:0 auto; }
        .logo-header{ display:block; max-width:320px; width:60%; height:auto; margin:.5rem auto 1rem; }
        .titulo-principal{ font-size:3rem; }
        .btn-personalizado{ background:#1872a2; color:#fff; border-color:transparent; }
        .btn-personalizado:hover{ background:#104b6b; color:#fff; }

        /* ===== Topbar (solo móvil) ===== */
        .topbar{
            position:fixed; top:0; left:0; right:0; height:56px;
            background:#fff; border-bottom:1px solid #e5e7eb;
            display:flex; align-items:center; padding:.5rem .75rem; z-index:1100;
        }
        .topbar-logo{ height:28px; width:auto; }

        /* ===== Mobile (≤768px) ===== */
        @media (max-width: 767.98px){
            /* sidebar como panel deslizable */
            .sidebar{
                left:-280px; width:280px;              /* oculto fuera de pantalla */
                display:flex !important;               /* re-mostrar aunque tenga d-none d-md-flex */
            }
            .sidebar.show{ left:0; }                 /* visible al togglear */
            .content{ margin-left:0; padding:1rem; padding-top:72px; }  /* deja espacio a la topbar */
            .page-wrap{ max-width:100%; }
            .titulo-principal{ font-size:2rem; text-align:center; }
            .logo-header{ max-width:220px; width:70%; }

            /* tabla compacta */
            .table{ font-size:.9rem; }
            /* Oculta Lote (col 4) y Zona (col 5) en XS para que no desborde */
            .table thead th:nth-child(4),
            .table tbody td:nth-child(4),
            .table thead th:nth-child(5),
            .table tbody td:nth-child(5){ display:none; }
        }


    </style>


</head>
<body>
<jsp:include page="/sidebar.jsp" />
<!-- Topbar solo en móvil -->
<header class="topbar d-md-none">
    <button id="btnMobileMenu" class="btn btn-primary me-2" aria-label="Menú">☰</button>
    <img src="<%=request.getContextPath()%>/Almacen/img/telitoLogo.png" alt="Telito" class="topbar-logo">
    <span class="ms-2 fw-bold">Inventario</span>
</header>

<main id="main" class="content">
    <div class="page-wrap ">

        <h1 class="text-primary fw-bold titulo-principal text-center mb-5">
            <i class="bi bi-file-earmark-spreadsheet-fill me-2"></i> CARGA MASIVA DE ENTRADAS
        </h1>

        <div class="card-container">
            <%-- Mensajes de Redirección (Éxito o Error General) --%>
            <% if (statusMessage != null && !statusMessage.isEmpty()) {
                String[] parts = statusMessage.split("\\|", 2);
                String status = parts.length>0 ? parts[0] : "success";
                String msg    = parts.length>1 ? parts[1] : "";
                String alertClass = "success".equalsIgnoreCase(status) ? "alert-success border-success" : "alert-danger border-danger";
                String icon = "success".equalsIgnoreCase(status) ? "bi-check-circle-fill" : "bi-x-octagon-fill";
            %>
            <div class="alert <%=alertClass%> alert-dismissible fade show shadow-sm" role="alert">
                <i class="bi <%=icon%> me-2"></i>
                <strong><%= "success".equalsIgnoreCase(status) ? "Éxito" : "Error" %>:</strong> <%= msg %>
                <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
            </div>
            <% } %>

            <%-- Formulario de Subida (Tarjeta Principal) --%>
            <div class="card shadow-lg mb-4">
                <div class="card-header bg-primary text-white">
                    <h5 class="mb-0"><i class="bi bi-cloud-arrow-up-fill me-2"></i> Subir Archivo Excel</h5>
                </div>
                <div class="card-body">
                    <p class="card-text text-muted">
                        Este proceso solo registra **ENTRADAS ('IN')**. Descargue la plantilla, la cual ya contiene los IDs internos (**no los modifique**). Solo ingrese la **CANTIDAD** y la **FECHA** (Formato YYYY-MM-DD) para los productos que desea ingresar.
                        <br><strong>⚠️ Si no desea ingresar un producto, borre la fila completa.</strong>
                    </p>

                    <div class="d-grid gap-2 mb-4">
                        <a href="<%=ctx%>/CargaMasivaServlet?accion=descargarPlantilla" class="btn btn-success btn-lg">
                            <i class="bi bi-download me-2"></i> Descargar Plantilla Excel
                        </a>
                    </div>

                    <hr>

                    <form method="post" action="<%=ctx%>/CargaMasivaServlet" enctype="multipart/form-data">
                        <input type="hidden" name="accion" value="validarYSubir">

                        <div class="mb-4">
                            <label for="archivoExcel" class="form-label fw-bold">Paso 2: Seleccionar Archivo (.xlsx o .xls)</label>
                            <input class="form-control form-control-lg" type="file" id="archivoExcel" name="archivoExcel" accept=".xlsx, .xls" required>
                        </div>

                        <div class="d-grid">
                            <button type="submit" class="btn btn-primary btn-lg">
                                <i class="bi bi-check-circle-fill me-2"></i> Validar Datos y Subir Movimientos
                            </button>
                        </div>
                    </form>
                </div>
            </div>

            <%-- Mensajes de Validación de Lotes (Errores Detallados) --%>
            <% if (erroresCarga != null && !erroresCarga.isEmpty()) { %>
            <div class="alert alert-danger shadow-sm border-danger">
                <h5 class="alert-heading text-danger">
                    <i class="bi bi-exclamation-triangle-fill me-2"></i> **ATENCIÓN: Errores de Validación**
                </h5>
                <p>Se encontraron **<%= erroresCarga.size() %>** errores. **No se registró ningún movimiento**. Corrija las siguientes líneas y vuelva a subir el archivo.</p>

                <hr>
                <h6>Detalle de Errores por Fila:</h6>
                <div class="error-list">
                    <ul class="list-unstyled mb-0">
                        <% for (String error : erroresCarga) { %>
                        <li class="text-danger"><i class="bi bi-dash-circle me-2"></i><%= error.replace("<br>","") %></li>
                        <% } %>
                    </ul>
                </div>
            </div>
            <% } %>
        </div>
    </div>
</main>

<script>
    // Desktop: colapsar sidebar (si tu sidebar.jsp pone un botón #btnToggle)
    const btn = document.getElementById('btnToggle');
    const sidebar = document.getElementById('sidebar');
    const main = document.getElementById('main');
    if (btn && sidebar && main) {
        btn.addEventListener('click', () => {
            sidebar.classList.toggle('collapsed');   // desktop
            main.classList.toggle('collapsed');
        });
    }

    // Móvil: abrir/cerrar panel deslizable
    const btnMobile = document.getElementById('btnMobileMenu');
    if (btnMobile && sidebar) {
        btnMobile.addEventListener('click', () => {
            sidebar.classList.toggle('show');        // mueve left:-280px -> 0
        });
    }
</script>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.0.2/dist/js/bootstrap.bundle.min.js"></script>

</body>
</html>