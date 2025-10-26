<%--
  Created by IntelliJ IDEA.
  User: Labtel
  Date: 29/09/2025
  Time: 08:57
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.example.telitobodeguero.beans.Producto" %>
<%@page import="java.util.ArrayList"%>
<jsp:useBean id = "listaProductos" scope="request" type="java.util.ArrayList<com.example.telitobodeguero.beans.Producto>"/>
<%
    String ctx = request.getContextPath();

    // Mensaje de éxito desde el redirect del servlet
    String msg   = request.getParameter("msg");
    String skuP  = request.getParameter("sku");
    String prodP = request.getParameter("prod");
    String cantP = request.getParameter("cant");

    String textoOk = null;
    if ("in_ok".equals(msg))  textoOk = "Entrada registrada: SKU " + (skuP!=null?skuP:"")
            + " · " + (prodP!=null?prodP:"")
            + " · Cantidad +" + (cantP!=null?cantP:"");
    if ("out_ok".equals(msg)) textoOk = "Salida registrada: SKU " + (skuP!=null?skuP:"")
            + " · " + (prodP!=null?prodP:"")
            + " · Cantidad -" + (cantP!=null?cantP:"");
%>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Gestión de Inventarios</title>
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.0.2/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-EVSTQN3/azprG1Anm3QDgpJLIm9Nao0Yz1ztcQTwFspd3yD65VohhpuuCOmLASjC" crossorigin="anonymous">

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
<!--Parte de slidebar-->
<jsp:include page="/sidebar.jsp" />

<!-- Topbar solo en móvil -->
<header class="topbar d-md-none">
    <button id="btnMobileMenu" class="btn btn-primary me-2" aria-label="Menú">☰</button>
    <img src="<%=request.getContextPath()%>/Almacen/img/telitoLogo.png" alt="Telito" class="topbar-logo">
    <span class="ms-2 fw-bold">Inventario</span>
</header>

<!-- Contenido -->
<main id="main" class="content">
    <div class="page-wrap">
        <img src="<%=request.getContextPath()%>/Almacen/img/telitoLogo.png" class="logo-header" alt="Telito">

        <h1 class="text-primary fw-bold titulo-principal text-center mb-4">INVENTARIO GENERAL</h1>

        <% if (textoOk != null) { %>
        <div class="alert alert-success alert-dismissible fade show" role="alert">
            <%= textoOk %>
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        </div>
        <% } %>

        <h3 class="text-secondary mb-3">Tabla de Productos</h3>

        <div class="table-responsive">
            <table class="table table-striped align-middle table-sm">
                <thead class="table-light">
                <tr>
                    <th style="width:10%">SKU</th>
                    <th style="width:30%">Nombre</th>
                    <th style="width:10%">Stock</th>
                    <th style="width:10%">Lote</th>
                    <th style="width:15%">Zona</th>
                    <th style="width:25%">Acciones</th>
                </tr>
                </thead>
                <tbody>
                <% for (Producto prod : listaProductos) { %>
                <tr>
                    <td><%= prod.getSku() %></td>
                    <td><%= prod.getNombre() %></td>
                    <td><%= prod.getStock() %></td>
                    <td><%= prod.getLotes() %></td>
                    <td><%= prod.getZona().getNombre() %></td>
                    <td>
                        <div class="d-flex gap-2">
                            <a href="<%=request.getContextPath()%>/AlmacenServlet?accion=mostrarRegistro&tipo=entrada&sku=<%= prod.getSku()%>&lote=<%= prod.getLotes()%>&zonaNombre=<%=prod.getZona().getNombre()%>"
                               class="btn btn-success rounded-circle" title="Entrada">
                                <img src="<%=request.getContextPath()%>/Almacen/img/entrada.png" width="20" height="20" alt="">
                            </a>
                            <a href="<%=request.getContextPath()%>/AlmacenServlet?accion=mostrarRegistro&tipo=salida&sku=<%= prod.getSku()%>&lote=<%= prod.getLotes()%>&zonaNombre=<%=prod.getZona().getNombre()%>"
                               class="btn btn-personalizado rounded-circle" title="Salida">
                                <img src="<%=request.getContextPath()%>/Almacen/img/salida.png" width="20" height="20" alt="">
                            </a>
                            <a href="<%=request.getContextPath()%>/AlmacenServlet?accion=mostrarIncidencia&sku=<%= prod.getSku()%>&lote=<%= prod.getLotes()%>&zonaNombre=<%=prod.getZona().getNombre()%>&prodNombre=<%=prod.getNombre()%>"
                               class="btn btn-danger rounded-circle" title="Incidencia">
                                <img src="<%=request.getContextPath()%>/Almacen/img/incidencia.png" width="20" height="20" alt="">
                            </a>
                        </div>
                    </td>
                </tr>
                <% } %>
                </tbody>
            </table>
        </div>
    </div>
</main>

  <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.0.2/dist/js/bootstrap.bundle.min.js" integrity="sha384-MrcW6ZMFYlzcLA8Nl+NtUVF0sA7MsXsP1UyJoMp4YLEuNSfAP+JcXn/tWtIaxVXM" crossorigin="anonymous"></script>

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


</body>
</html>
