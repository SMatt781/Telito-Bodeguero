<%--
  Created by IntelliJ IDEA.
  User: Labtel
  Date: 29/09/2025
  Time: 08:56
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
//  String zona = "Oeste";
//  String zonaId = "2";

%>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Registro de Entrada</title>
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.0.2/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-EVSTQN3/azprG1Anm3QDgpJLIm9Nao0Yz1ztcQTwFspd3yD65VohhpuuCOmLASjC" crossorigin="anonymous">

  <style>
    body {
      display: flex;
      margin: 0;
      overflow-x: hidden;
    }
    .sidebar {
      position: fixed;
      left: 0;
      top: 0;
      width: 280px;
      height: 100vh;
      transition: width 0.3s ease-in-out;
      background-color: #212529;
      z-index: 1000;

    }
    .sidebar.collapsed {
      width: 80px;
    }
    .sidebar.collapsed .sidebar-text {
      display: none;
    }

    .nav-link.text-white:hover {
      background-color: #0d6efd;
      color: #fff !important;
    }
    .main-content {
      flex-grow: 1;
      padding: 3rem;
      background-color: #f8f9fa;
      margin-left: 280px;
      transition: margin-left 0.3s ease-in-out;
      min-height: 100vh;
    }
    .main-content.collapsed {
      margin-left: 80px;
    }
    .titulo-principal {
      font-size: 3rem;
    }
  </style>
</head>
<body>

<div class="d-flex flex-column flex-shrink-0 p-3 text-white bg-dark sidebar" id="sidebar">

  <!--SLIDEBAR-->
  <div class="sidebar-header">

    <button class="btn btn-primary" id="toggleButton" aria-label="Toggle Sidebar">
      &#9776;
    </button>

    <a href="#" class="d-flex align-items-center text-white text-decoration-none">
      <svg class="bi me-2" width="40" height="32"><use xlink:href="#bootstrap"/></svg>
      <span class="fs-4 sidebar-text">Bienvenido, Almacén! :)</span>
    </a>

  </div>

  <hr>
  <ul class="nav nav-pills flex-column mb-auto">
      <li>
          <a href="<%=request.getContextPath()%>/AlmacenServlet" class="nav-link text-white">
              <img src="<%=request.getContextPath()%>/Almacen/img/inicio.png" width="25" height="25" class="me-2">
              <span class="sidebar-text">Inicio</span>
          </a>
      </li>

      <li>
          <a href="<%=request.getContextPath()%>/AlmacenServlet" class="nav-link text-white">
              <img src="<%=request.getContextPath()%>/Almacen/img/indexGestion2.png" width="25" height="25" class="me-2">
              <span class="sidebar-text">Gestion de inventarios</span>
          </a>
      </li>

      <li>
          <a href="<%=request.getContextPath()%>/CargaExcelServlet" class="nav-link text-white">
              <img src="<%=request.getContextPath()%>/Almacen/img/indexCarga.png" width="25" height="25" class="me-2">
              <span class="sidebar-text">Carga masiva de datos</span>
          </a>
      </li>
      <li>
          <a href="<%=request.getContextPath()%>/IncidenciaAlmServlet" class="nav-link text-white">
              <img src="<%=request.getContextPath()%>/Almacen/img/incidencia.png" width="25" height="25" class="me-2">
              <span class="sidebar-text">Incidencias </span>
          </a>
      </li>


  </ul>

  <div class="dropdown mt-auto">
    <a href="#" class="d-flex align-items-center text-white text-decoration-none dropdown-toggle" id="dropdownUser1" data-bs-toggle="dropdown" aria-expanded="false">
      <img src="<%=request.getContextPath()%>/Almacen/img/indexUsuario.webp" alt="" width="32" height="32" class="rounded-circle me-2">
      <strong class="sidebar-text">Usuario</strong>
    </a>
    <ul class="dropdown-menu dropdown-menu-dark text-small shadow" aria-labelledby="dropdownUser1">
      <li><a class="dropdown-item" href="#">Cerrar sesión</a></li>
    </ul>
  </div>
</div>



<!--CONTENIDO-->


<div class="main-content" id="main-content">
  <h1 class="text-primary fw-bold titulo-principal">HEADER</h1>

  <div class="form-container d-flex flex-column align-items-center justify-content-center">
    <h1 class="text-secondary fw-bold titulo-principal">Registro de entrada</h1>

    <form action="AlmacenServlet" method="POST">

      <input type = "hidden" name="accion" value="registrarMovimiento">
      <input type="hidden" name="tipo" value="IN">
      <input type="hidden" name="idZona" value="<%=zonaId%>">


      <div class="mx-5 mt-3 mb-3">
        <label for="SKU" class="form-label">SKU del producto:</label>
        <input type="text" class="form-control" id="SKU" name="sku"
               value="<%= sku != null ? sku : "" %>" readonly>
      </div>

      <div class="mx-5 mt-3 mb-3">
        <label for="zonaDistrito" class="form-label">Zona-Distrito:</label>
        <input type="text" class="form-control" id="zonaDistrito"
               value="<%= zona %>" readonly>
      </div>

      <div class="mx-5 mt-3 mb-3">
        <label for="fechaEntrada" class="form-label">Fecha:</label>
        <input type="date" class="form-control" id="fechaEntrada" name="fechaRegistro">
      </div>


      <div class="mx-5 mt-3 mb-3">
        <label for="cantidadProducto" class="form-label">Cantidad:</label>
        <input type="text" class="form-control" id="cantidadProducto" name="cantidad">
      </div>


      <div class="mx-5 mt-3 mb-3">
        <label for="lote" class="form-label">Lote:</label>
        <input type="text" class="form-control" id="lote" name="lote" value="<%= lote %>">
      </div>

      <div class="mx-5 mt-3 mb-3">
        <label for="fechaVencimiento" class="form-label">Fecha de Vencimiento:</label>
        <input type="date" class="form-control" id="fechaVencimiento" name="fechaVencimiento">
      </div>

      <div class="main content d-flex flex-column align-items-center justify-content-center mt-3 mb-5">
        <button type="submit" class="btn btn-primary ">Guardar ENTRADA</button>
      </div>

    </form>

  </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.0.2/dist/js/bootstrap.bundle.min.js" integrity="sha384-MrcW6ZMFYlzcLA8Nl+NtUVF0sA7MsXsP1UyJoMp4YLEuNSfAP+JcXn/tWtIaxVXM" crossorigin="anonymous"></script>

<script>
  const toggleButton = document.getElementById('toggleButton');
  const sidebar = document.getElementById('sidebar');
  const mainContent = document.getElementById('main-content');

  toggleButton.addEventListener('click', () => {
    sidebar.classList.toggle('collapsed');
    mainContent.classList.toggle('collapsed');
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
</body>
</html>
