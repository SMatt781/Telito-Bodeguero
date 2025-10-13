<%--
  Created by IntelliJ IDEA.
  User: Labtel
  Date: 29/09/2025
  Time: 12:28
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@page import="java.time.LocalDate"%>

<%
  //recupero datos del request
  String sku = (String) request.getAttribute("sku");
  String lote = (String) request.getAttribute("lote");
  //zona fija a oeste
  String zona = (String) request.getAttribute("zonaNombre");
  String zonaId = (String) request.getAttribute("idZona");
  String prodNombre = (String) request.getAttribute("prodNombre");
//  String zona = "Oeste";
//  String zonaId = "2";

%>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Registro de Incidencia</title>
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.0.2/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-EVSTQN3/azprG1Anm3QDgpJLIm9Nao0Yz1ztcQTwFspd3yD65VohhpuuCOmLASjC" crossorigin="anonymous">

  <style>
    body {
      display: flex;
      margin: 0;
      overflow-x: hidden;
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
  </style>
</head>
<body>

<jsp:include page="/sidebar.jsp" />

<!--CONTENIDO-->


<main class="main" id="main">
  <h1 class="text-primary fw-bold titulo-principal">HEADER</h1>

  <div class="form-container d-flex flex-column align-items-center justify-content-center">
    <h1 class="text-secondary fw-bold titulo-principal">Reporte de incidencia</h1>

    <form action="AlmacenServlet" method="POST">

      <input type = "hidden" name="accion" value="registrarIncidencia">
      <input type="hidden" name="idZona" value="<%=zonaId%>">
      <input type="hidden" name="prodNombre" value="<%=prodNombre%>">
      <input type="hidden" name="estado" value="REGISTRADA">



      <div class="mx-5 mt-3 mb-3">
        <label for="SKU" class="form-label">SKU del producto:</label>
        <input type="text" class="form-control" id="SKU" name="sku"
               value="<%= sku !=null ? sku: "" %>" readonly>
      </div>

      <div class="mx-5 mt-3 mb-3">
        <label for="zonaDistrito" class="form-label">Zona-Distrito:</label>
        <input type="text" class="form-control" id="zonaDistrito"
               value="<%=zona%>" readonly>
      </div>



      <div class="mx-5 mt-3 mb-3">
        <label for="tipoInc" class="form-label">Tipo:</label>
        <select id="tipoIncidencia" name="tipoInc" class="form-select" required>
          <option value="" selected disabled>Selecciona tipo</option>
          <option value="FALTANTE" >Faltante</option>
          <option value="VENCIDO">Vencido</option>
          <option value="DAÑO">Daño</option>
        </select>

      </div>


      <div class="mx-5 mt-3 mb-3">
        <label for="cantidadProducto" class="form-label">Cantidad:</label>
        <input type="text" class="form-control" id="cantidadProducto" name="cantidadInc">
      </div>


      <div class="mx-5 mt-3 mb-3">
        <label for="lote" class="form-label">Lote:</label>
        <input type="text" class="form-control" id="lote" name="lote"
               value="<%= lote %>" >
      </div>

      <div class="mx-5 mt-3 mb-3">
        <label for="descripcion" class="form-label">Descripción:</label>
        <input type="text" class="form-control" id="descripcion" name="descripcionInc">
      </div>

      <div class="main content d-flex flex-column align-items-center justify-content-center mt-3 mb-5">
        <button type="submit" class="btn btn-primary ">Registrar incidencia</button>
      </div>

    </form>

  </div>
</main>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.0.2/dist/js/bootstrap.bundle.min.js" integrity="sha384-MrcW6ZMFYlzcLA8Nl+NtUVF0sA7MsXsP1UyJoMp4YLEuNSfAP+JcXn/tWtIaxVXM" crossorigin="anonymous"></script>

<script>
    // Toggle del sidebar
    const btn = document.getElementById('btnToggle');
    const sidebar = document.getElementById('sidebar');
    const main = document.getElementById('main');
    btn.addEventListener('click', () => {
        sidebar.classList.toggle('collapsed');
        main.classList.toggle('collapsed');
    });
  /*para la fecha*/
  document.addEventListener("DOMContentLoaded", () => {
    const fechaEntrada = document.getElementById("fechaEntrada");
    const hoy = new Date();
    const año = hoy.getFullYear();
    const mes = String(hoy.getMonth() + 1).padStart(2, '0');
    const dia = String(hoy.getDate()).padStart(2, '0');

    fechaEntrada.value = `${año}-${mes}-${dia}`;
  });
</script>
