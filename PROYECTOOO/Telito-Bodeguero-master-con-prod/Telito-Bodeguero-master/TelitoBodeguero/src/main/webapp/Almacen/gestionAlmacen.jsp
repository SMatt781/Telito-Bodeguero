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
<jsp:include page="/sidebar.jsp" />

<!--Parte del contenido-->

<main class="main" id="main">
  <img src="<%=request.getContextPath()%>/Almacen/img/telitoLogo.png"   class="logo-header">


  <main class="container-fluid d-flex flex-column justify-content-start">
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
  </main>
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

    /*para el popover*/
    const popoverTriggerList = document.querySelectorAll('[data-bs-toggle="popover"]')
    const popoverList = [...popoverTriggerList].map(popoverTriggerEl => new bootstrap.Popover(popoverTriggerEl))
  </script>

</body>
</html>
