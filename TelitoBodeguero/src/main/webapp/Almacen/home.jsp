<%--
  Created by IntelliJ IDEA.
  User: Labtel
  Date: 30/09/2025
  Time: 12:31
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Gestión de Inventarios</title>
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.0.2/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-EVSTQN3/azprG1Anm3QDgpJLIm9Nao0Yz1ztcQTwFspd3yD65VohhpuuCOmLASjC" crossorigin="anonymous">

  <style>
    body {
      display: flex;
      height: 100vh;
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
    /*
    .sidebar-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        padding: 1.5rem 1rem;
    }*/
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
    .main-content.collapsed {
      margin-left: 80px;
    }
    .titulo-principal {
      font-size: 3rem;
    }
  </style>

  <style>
    .btn-personalizado {
      background-color: #1872a2; /* Color Naranja-Rojo */
      color: #ffffff; /* Texto Blanco */
      border-color: #114d6e00;
    }

    .btn-personalizado:hover {
      background-color: #104b6bfe; /* Un color más oscuro al pasar el mouse */
      border-color: #c7003800;
    }
  </style>

</head>


<body>

<!--Parte de slidebar-->
<div class="d-flex flex-column flex-shrink-0 p-3 text-white bg-dark sidebar" id="sidebar">

  <div class="sidebar-header">

    <button class="btn btn-primary" id="toggleButton" aria-label="Toggle Sidebar">
      &#9776;
    </button>

    <a href="#" class="d-flex align-items-center text-white text-decoration-none">
      <svg class="bi me-2" width="40" height="32"><use xlink:href="#bootstrap"/></svg>
      <span class="fs-4 sidebar-text"> Telito Bodeguero</span>
    </a>

  </div>

  <hr>
  <ul class="nav nav-pills flex-column mb-auto">
    <li>
      <a href="index_yo.html" class="nav-link text-white">
        <img src="inicio.png" width="25" height="25" class="me-2">
        <span class="sidebar-text">Inicio</span>
      </a>
    </li>

    <li>
      <a href="gestion.html" class="nav-link text-white">
        <img src="indexGestion2.png" width="25" height="25" class="me-2">
        <span class="sidebar-text">Gestion de inventarios</span>
      </a>
    </li>

    <li>
      <a href="carga.html" class="nav-link text-white">
        <img src="indexCarga.png" width="25" height="25" class="me-2">
        <span class="sidebar-text">Carga masiva de datos</span>
      </a>
    </li>


  </ul>

  <div class="dropdown mt-auto">
    <a href="#" class="d-flex align-items-center text-white text-decoration-none dropdown-toggle" id="dropdownUser1" data-bs-toggle="dropdown" aria-expanded="false">
      <img src="indexUsuario.webp" alt="" width="32" height="32" class="rounded-circle me-2">
      <strong class="sidebar-text">Usuario</strong>
    </a>
    <ul class="dropdown-menu dropdown-menu-dark text-small shadow" aria-labelledby="dropdownUser1">
      <li><a class="dropdown-item" href="#">Cerrar sesión</a></li>
    </ul>
  </div>
</div>

<!--Parte del contenido-->
<div class="main-content" id="main-content">
  <h1 class="text-primary fw-bold titulo-principal">BIENVENIDO, ALMACÉN!</h1>


  <h2 class="text-secondary fw-bold mb-3">Inicio </h2>

  <!-- KPIs mínimos -->
  <div class="row g-2 mb-3">
    <div class="col-12 col-md-4">
      <div class="p-3 bg-white border rounded">
        <div class="small text-muted">Stock total</div>
        <div class="fs-4 fw-bold" id="kpi-stock">6,450</div>
      </div>
    </div>
    <div class="col-12 col-md-4">
      <div class="p-3 bg-white border rounded">
        <div class="small text-muted">Movimientos hoy</div>
        <div class="fs-5 fw-bold" id="kpi-mov">Entrada: 12 - Salida: 9 </div>
      </div>
    </div>
    <div class="col-12 col-md-4">
      <div class="p-3 bg-white border rounded">
        <div class="small text-muted">Bajo mínimo</div>
        <div class="fs-4 fw-bold text-danger" id="kpi-min">18</div>
      </div>
    </div>
  </div>

  <!-- Acciones rápidas -->
  <div class="d-flex flex-wrap gap-2 mb-3">
    <a href="registroEntrada.html" class="btn btn-outline-success btn-sm">Registrar ENTRADA</a>
    <a href="registroSalida.html" class="btn btn-outline-primary btn-sm">Registrar SALIDA</a>
    <a href="carga.html" class="btn btn-outline-warning btn-sm">Carga masiva</a>
  </div>

  <!--TABLA MOV RECIENTES -->


  <div class="row py-3">
    <h3 class="text-secondary fw titulo-secundario">Tabla de Productos</h3>
    <div class="bd-example">
      <table class="table table-striped">
        <thead>
        <tr>
          <th scope="col" style="width: 10%;">FECHA</th>
          <th scope="col" style="width: 25%;">TIPO</th>
          <th scope="col" style="width: 10%;">ID</th>
          <th scope="col" style="width: 25%;">CANTIDAD</th>
          <th scope="col" style="width: 25%;">PRODUCTO</th>
        </tr>
        </thead>
        <tbody>
        <tr>
          <th scope="row">30/08/2025</td>
          <td>ENTRADA</td>
          <td>A001</td>
          <td>23</td>
          <td>Aceite Vegetal 1L</td>


        </tr>

        <tr>
          <th scope="row">30/08/2025</td>
          <td>SALIDA</td>
          <td>E090</td>
          <td>12</td>
          <td>Leche Entera 1L</td>

        </tr>

        <tr>
          <th scope="row">01/09/2025</td>
          <td>ENTRADA</td>
          <td>H207</td>
          <td>35</td>
          <td>Detergente 1kg</td>
        </tr>
        </tbody>
      </table>
    </div>
  </div>





</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.0.2/dist/js/bootstrap.bundle.min.js" integrity="sha384-MrcW6ZMFYlzcLA8Nl+NtUVF0sA7MsXsP1UyJoMp4YLEuNSfAP+JcXn/tWtIaxVXM" crossorigin="anonymous"></script>

<script>
  /*para el slidebar*/
  const toggleButton = document.getElementById('toggleButton');
  const sidebar = document.getElementById('sidebar');
  const mainContent = document.getElementById('main-content');

  toggleButton.addEventListener('click', () => {
    sidebar.classList.toggle('collapsed');
    mainContent.classList.toggle('collapsed');
  });

  /*para el popover*/
  const popoverTriggerList = document.querySelectorAll('[data-bs-toggle="popover"]')
  const popoverList = [...popoverTriggerList].map(popoverTriggerEl => new bootstrap.Popover(popoverTriggerEl))
</script>
</body>
</html>
