<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    // Datos que te pasa el servlet al abrir el formulario
    String sku        = (String) request.getAttribute("sku");
    String lote       = (String) request.getAttribute("loteId");
    String zonaNombre = (String) request.getAttribute("zonaNombre");
    String prodNombre = (String) request.getAttribute("prodNombre");

    // Zona desde sesión (ya la guardas en el login)
    Integer zonaIdSes = (Integer) session.getAttribute("zonaIdActual");

    if (zonaNombre == null) zonaNombre = "Zona";
    String ctx = request.getContextPath();
    String error  = (String) request.getAttribute("error");

    String bloqueId = (String) request.getAttribute("bloqueId");
    String ubicacion = (String) request.getAttribute("ubicacion");
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width,initial-scale=1" />
    <title>Registrar Incidencia · Almacén</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.0.2/dist/css/bootstrap.min.css" rel="stylesheet" />

    <style>
        /* ===== Desktop (default) ===== */
        body{ min-height:100vh; background:#f8f9fa; overflow-x:hidden; }

        /* Sidebar (incluido vía jsp:include) */
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

        /* Contenido principal */
        .content{
            margin-left:280px; transition:margin-left .25s ease;
            padding:2rem 1rem; min-height:100vh;
        }
        .content.collapsed{ margin-left:80px; }

        /* Contenedor centrado */
        .page-wrap{ max-width:720px; margin:0 auto; }
        .titulo-principal{ font-size:2.1rem; }

        .form-card{ background:#fff; border:1px solid #e9ecef; border-radius:.75rem; padding:1.25rem; }

        /* Topbar (móvil) */
        .topbar{
            position:fixed; top:0; left:0; right:0; height:56px;
            background:#fff; border-bottom:1px solid #e5e7eb;
            display:flex; align-items:center; padding:.5rem .75rem; z-index:1100;
        }
        .topbar-logo{ height:28px; width:auto; }

        /* ===== Mobile (≤768px) ===== */
        @media (max-width: 767.98px){
            .sidebar{
                left:-280px; width:280px;
                display:flex !important;
            }
            .sidebar.show{ left:0; }
            .content{ margin-left:0; padding:1rem; padding-top:72px; }
            .page-wrap{ max-width:100%; }
            .titulo-principal{ font-size:1.8rem; text-align:center; }
        }
    </style>
</head>
<body>

<!-- Sidebar -->
<jsp:include page="/sidebar.jsp" />

<!-- Topbar móvil -->
<header class="topbar d-md-none">
    <button id="btnMobileMenu" class="btn btn-primary me-2" aria-label="Menú">☰</button>
    <span class="ms-2 fw-bold">Registrar incidencia</span>
</header>

<!-- Contenido -->
<main id="main" class="content">
    <div class="page-wrap">
        <h1 class="text-primary fw-bold titulo-principal mb-3">Reporte de incidencia</h1>

        <% if (error != null) { %>
        <div class="alert alert-danger" role="alert"><%= error %></div>
        <% } %>

        <div class="form-card">
            <form action="<%=ctx%>/AlmacenServlet" method="post" novalidate>
                <input type="hidden" name="accion"     value="registrarIncidencia" />
                <input type="hidden" name="estado"     value="REGISTRADA" />
                <input type="hidden" name="prodNombre" value="<%= prodNombre!=null? prodNombre : "" %>" />
                <!-- Si tu servlet ya lee la zona desde sesión, este hidden es opcional -->
                <input type="hidden" name="idZona"     value="<%= zonaIdSes!=null? zonaIdSes : 1 %>" />
                <input type="hidden" name="bloqueId" value="<%= bloqueId!=null?bloqueId:"" %>" />
                <input type="hidden" name="loteId" value="<%= lote %>" />
                <input type="hidden" name="fase"   value="grabar">
                <div class="row g-3">
                    <div class="col-12 col-md-6">
                        <label for="SKU" class="form-label">SKU</label>
                        <input type="text" id="SKU" name="sku" class="form-control"
                               value="<%= sku!=null? sku : "" %>" readonly />
                    </div>

                    <div class="col-12 col-md-6">
                        <label class="form-label">Zona - Distrito</label>
                        <input type="text" class="form-control" value="<%= zonaNombre %>" readonly />
                    </div>

                    <div class="col-12 col-md-6">
                        <label for="tipoIncidencia" class="form-label">Tipo</label>
                        <select id="tipoIncidencia" name="tipoInc" class="form-select" required>
                            <option value="" selected disabled>Selecciona tipo</option>
                            <option value="FALTANTE">Faltante</option>
                            <option value="VENCIDO">Vencido</option>
                            <option value="DAÑO">Daño</option>
                        </select>
                    </div>

                    <div class="col-12 col-md-6">
                        <label for="cantidadProducto" class="form-label">Cantidad</label>
                        <input type="number" id="cantidadProducto" name="cantidadInc" class="form-control"
                               min="1" step="1" required />
                    </div>



                    <div class="col-12 col-md-6">
                        <label for="ubicacion" class="form-label">Ubicación</label>
                        <input type="text" class="form-control" id="ubicacion" name="ubicacion"
                               value="<%= ubicacion!=null? ubicacion : "" %>" readonly>
                    </div>

                    <div class="col-12">
                        <label for="descripcion" class="form-label">Descripción</label>
                        <textarea id="descripcion" name="descripcionInc" rows="3" class="form-control"
                                  placeholder="Describe brevemente la incidencia" required></textarea>
                    </div>
                </div>

                <div class="d-flex justify-content-end mt-4">
                    <a href="<%=ctx%>/AlmacenServlet" class="btn btn-outline-secondary me-2">Cancelar</a>
                    <button type="submit" class="btn btn-primary">Registrar incidencia</button>
                </div>
            </form>
        </div>
    </div>
</main>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.0.2/dist/js/bootstrap.bundle.min.js"></script>
<script>
    // Desktop: colapsar sidebar si existe botón #btnToggle
    const btn = document.getElementById('btnToggle');
    const sidebar = document.getElementById('sidebar');
    const main = document.getElementById('main');
    if (btn && sidebar && main) {
        btn.addEventListener('click', () => {
            sidebar.classList.toggle('collapsed');
            main.classList.toggle('collapsed');
        });
    }

    // Móvil: abrir/cerrar sidebar como off-canvas
    const btnMobile = document.getElementById('btnMobileMenu');
    if (btnMobile && sidebar) {
        btnMobile.addEventListener('click', () => {
            sidebar.classList.toggle('show');
        });
    }
</script>
</body>
</html>
