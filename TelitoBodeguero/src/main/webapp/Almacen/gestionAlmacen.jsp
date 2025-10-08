<%--
  Created by IntelliJ IDEA.
  User: Labtel
  Date: 29/09/2025
  Time: 08:57
  To change this template use File | Settings | File Templates.
--%>
<%@ page import="com.example.telitobodeguero.beans.Producto" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@page import="java.util.ArrayList"%>
<jsp:useBean id = "listaProductos" scope="request" type="ArrayList<Producto>"/>
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
      position: relative;
    }
    .main-content.collapsed {
      margin-left: 80px;
    }
    .titulo-principal {
      font-size: 3rem;
    }
    .logo-header {
      display:block;
      max-width: 400px;
      width: 50%;
      height: auto;
      margin: 0.25rem auto 1rem;
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

<!--Parte del contenido-->
<div class="main-content" id="main-content">

  <img src="<%=request.getContextPath()%>/Almacen/img/telitoLogo.png"   class="logo-header">


  <div class="container-fluid d-flex flex-column justify-content-start">
    <h1 class="text-primary fw-bold titulo-principal">INVENTARIO GENERAL</h1>

    <div class="row py-3">
        <%--Por el momento--%>
<%--      <div class="col-9">--%>
<%--        <!--Barra buscar-->--%>
<%--        <input class="form-control form-control-lg" type="search" placeholder="Buscar..." aria-label="Search">--%>
<%--      </div>--%>

      <!--TABLA DE PRODUCTOS-->

      <div class="row py-3">
        <h3 class="text-secondary fw titulo-secundario">Tabla de Productos</h3>
        <div class="bd-example">
          <table class="table table-striped">
            <thead>
            <tr>
              <th scope="col" style="width: 10%;">SKU</th>
              <th scope="col" style="width: 25%;">Nombre</th>
              <th scope="col" style="width: 5%;">Stock</th>
              <th scope="col" style="width: 25%;">Lote</th>
              <th scope="col" style="width: 10%;">Zona</th>
              <th scope="col" style="width: 25%;">Acciones</th>
            </tr>
            </thead>

            <tbody>
            <% for (Producto prod : listaProductos) {%>
            <tr>
              <td><%= prod.getSku()%></td>
              <td><%=prod.getNombre()%></td>
              <td><%=prod.getStock()%> </td>
              <td><%=prod.getLotes()%> </td>
              <td><%=prod.getZona().getNombre()%> </td>

              <td>
                <div class="col">


                  <a href="AlmacenServlet?accion=mostrarRegistro&tipo=entrada&sku=<%= prod.getSku()%>&lote=<%= prod.getLotes()%>&zonaNombre=<%=prod.getZona().getNombre()%>"
                     type="button" class="btn btn-success rounded-circle">
                    <img src="<%=request.getContextPath()%>/Almacen/img/entrada.png" alt="Entrada" width="20" height="20" class="rounded-circle align-items-center d-flex">
                  </a>


                  <a href="AlmacenServlet?accion=mostrarRegistro&tipo=salida&sku=<%= prod.getSku()%>&lote=<%= prod.getLotes()%>&zonaNombre=<%=prod.getZona().getNombre()%>"
                     type="button" class="btn btn-personalizado rounded-circle">
                    <img src="<%=request.getContextPath()%>/Almacen/img/salida.png" alt="Salida" width="20" height="20" class="rounded-circle align-items-center d-flex">
                  </a>

                  <a href="AlmacenServlet?accion=mostrarIncidencia&sku=<%= prod.getSku()%>&lote=<%= prod.getLotes()%>&zonaNombre=<%=prod.getZona().getNombre()%>&prodNombre=<%=prod.getNombre()%>"
                     type="button" class="btn btn-danger rounded-circle">
                    <img src="<%=request.getContextPath()%>/Almacen/img/incidencia.png" alt="Incidencia" width="20" height="20" class="rounded-circle align-items-center d-flex">
                  </a>
                </div>
              </td>
            </tr>
            <%}%>
            </tbody>




          </table>
        </div>
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
