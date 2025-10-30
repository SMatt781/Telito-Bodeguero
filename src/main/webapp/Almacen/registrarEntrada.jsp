<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.time.LocalDate" %>

<%
    // Valores que llegan desde el servlet (cuando vienes desde la tabla)
    String sku  = (String) request.getAttribute("sku");
    String lote = (String) request.getAttribute("loteId");
    String zonaNombre = (String) request.getAttribute("zonaNombre");
    String prodNombre = (String) request.getAttribute("prodNombre");

    // Zona desde sesión (lo que ya configuramos en login)
    Integer zonaIdSes = (Integer) session.getAttribute("zonaIdActual");

    // Fallbacks
    if (zonaNombre == null) zonaNombre = "Zona";
    String hoy = LocalDate.now().toString();
    String ctx = request.getContextPath();
    String error = (String) request.getAttribute("error");

    //nuevo después del cambio
    String bloqueId = (String) request.getAttribute("bloqueId");
    String ubicacion = (String) request.getAttribute("ubicacion");

%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Registrar Entrada · Almacén</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.0.2/dist/css/bootstrap.min.css" rel="stylesheet">

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
        .page-wrap{ max-width:800px; margin:0 auto; }

        .titulo-principal{ font-size:2.2rem; }
        .form-card{ background:#fff; border:1px solid #e9ecef; border-radius:.75rem; padding:1.5rem; }

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
    <span class="ms-2 fw-bold">Registrar entrada</span>
</header>

<!-- Contenido -->
<main id="main" class="content">
    <div class="page-wrap">
        <h1 class="text-primary fw-bold titulo-principal mb-3">Registro de entrada</h1>
        <% if (error != null) { %>
        <div class="alert alert-danger" role="alert"><%= error %></div>
        <% } %>

        <div class="form-card">
            <form action="<%=ctx%>/AlmacenServlet" method="post" novalidate>
                <input type="hidden" name="accion" value="registrarMovimiento">
                <input type="hidden" name="tipo"   value="IN">
                <input type="hidden" name="fase"   value="grabar">
                <input type="hidden" name="idZona" value="<%= zonaIdSes!=null? zonaIdSes : 1 %>">
                <input type="hidden" name="prodNombre" value="<%= prodNombre!=null?prodNombre:"" %>" />
                <input type="hidden" name="zonaNombre" value="<%= zonaNombre!=null?zonaNombre:"" %>" />
                <input type="hidden" name="bloqueId" value="<%= bloqueId!=null?bloqueId:"" %>" />
                <input type="hidden" name="loteId" value="<%= request.getAttribute("loteId")!=null? request.getAttribute("loteId") : "" %>" />
                <div class="row g-3">
                    <div class="col-12 col-md-6">
                        <label for="SKU" class="form-label">SKU</label>
                        <input type="text" class="form-control" id="SKU" name="sku"
                               value="<%= sku!=null? sku : "" %>" readonly>
                    </div>

                    <div class="col-12 col-md-6">
                        <label class="form-label">Zona - Distrito</label>
                        <input type="text" class="form-control" value="<%= zonaNombre %>" readonly>
                    </div>

                    <div class="col-12 col-md-6">
                        <label for="fechaEntrada" class="form-label">Fecha</label>
                        <input type="date" class="form-control" id="fechaEntrada" name="fechaRegistro"
                               value="<%= hoy %>" required>
                    </div>

                    <div class="col-12 col-md-6">
                        <label for="cantidadProducto" class="form-label">Cantidad</label>
                        <input type="number" class="form-control" id="cantidadProducto" name="cantidad"
                               min="1" step="1" required>
                    </div>

                    <div class="col-12 col-md-6">
                        <label for="ubicacion" class="form-label">Ubicación</label>
                        <input type="text" class="form-control" id="ubicacion" name="ubicacion"
                               value="<%= ubicacion!=null? ubicacion : "" %>" readonly>
                    </div>


                </div>

                <div class="d-flex justify-content-end mt-4">
                    <a href="<%=ctx%>/AlmacenServlet?accion=verInventario" class="btn btn-outline-secondary me-2">Cancelar</a>
                    <button type="submit" class="btn btn-primary">Guardar ENTRADA</button>
                </div>
            </form>
        </div>
    </div>
</main>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.0.2/dist/js/bootstrap.bundle.min.js"></script>
<script>
    // Desktop: colapsar sidebar (si tu sidebar.jsp trae #btnToggle)
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
