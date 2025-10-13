<%--
  Created by IntelliJ IDEA.
  User: Labtel
  Date: 30/09/2025
  Time: 12:31
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%@ page import="com.example.telitobodeguero.beans.Movimiento" %>
<%@ page import="java.util.ArrayList" %>
<%--<jsp:useBean id="listaMovs"  scope="request" type="java.util.ArrayList"/>--%>
<%--<jsp:useBean type="java.util.ArrayList<beans.Movimiento>" scope="request" id="listaMovs" />--%>
<%
    ArrayList<Movimiento> lista = (ArrayList<Movimiento>) request.getAttribute("listaMovs");
    String stockTotal = String.valueOf(request.getAttribute("stockTotal"));
    String movEntradaHoy = String.valueOf(request.getAttribute("inToday"));
    String movSalidaHoy =  String.valueOf(request.getAttribute("outToday"));
    String bajoMin =  String.valueOf(request.getAttribute("min"));

%>
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
    <h1 class="text-primary fw-bold titulo-principal">BIENVENIDO, ALMACÉN!</h1>


    <h2 class="text-secondary fw-bold mb-3">Inicio </h2>

    <!-- KPIs mínimos -->
    <div class="row g-2 mb-3">
        <div class="col-12 col-md-4">
            <div class="p-3 bg-white border rounded">
                <div class="small text-muted">Stock total</div>
                <div class="fs-4 fw-bold" id="kpi-stock"><%=stockTotal%></div>
            </div>
        </div>
        <div class="col-12 col-md-4">
            <div class="p-3 bg-white border rounded">
                <div class="small text-muted">Movimientos hoy</div>
                <div class="fs-5 fw-bold" id="kpi-mov">Entrada: <%=movEntradaHoy%> - Salida: <%=movSalidaHoy%> </div>
            </div>
        </div>
        <div class="col-12 col-md-4">
            <div class="p-3 bg-white border rounded">
                <div class="small text-muted">Bajo mínimo</div>
                <div class="fs-4 fw-bold text-danger" id="kpi-min"><%=bajoMin%></div>
            </div>
        </div>
    </div>

    <!-- Acciones rápidas -->
    <%--  <div class="d-flex flex-wrap gap-2 mb-3">--%>
    <%--    <a href="registroEntrada.html" class="btn btn-outline-success btn-sm">Registrar ENTRADA</a>--%>
    <%--    <a href="registroSalida.html" class="btn btn-outline-primary btn-sm">Registrar SALIDA</a>--%>
    <%--    <a href="carga.html" class="btn btn-outline-warning btn-sm">Carga masiva</a>--%>
    <%--  </div>--%>

    <!--TABLA MOV RECIENTES -->


    <div class="row py-3">
        <h3 class="text-secondary fw titulo-secundario">Tabla de Movimientos Recientes</h3>
        <div class="bd-example">
            <table class="table table-striped">
                <thead>
                <tr>
                    <th scope="col" style="width: 10%;">FECHA</th>
                    <th scope="col" style="width: 25%;">TIPO</th>
                    <th scope="col" style="width: 10%;">SKU</th>
                    <th scope="col" style="width: 25%;">CANTIDAD</th>
                    <th scope="col" style="width: 25%;">PRODUCTO</th>
                </tr>
                </thead>
                <tbody>
                <tr>
                    <%
                        for (Movimiento mov : lista) {
                    %>
                    <td ><%= mov.getFecha()%></td>
                    <td><%=mov.getTipoMovimiento()%></td>
                    <td><%=mov.getLote().getProducto().getSku()%></td>
                    <td><%=mov.getCantidad()%></td>
                    <td><%=mov.getLote().getProducto().getNombre()%></td>

                </tr>

                <%
                    }

                %>



                </tbody>
            </table>
        </div>
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

    /*para el popover*/
    const popoverTriggerList = document.querySelectorAll('[data-bs-toggle="popover"]')
    const popoverList = [...popoverTriggerList].map(popoverTriggerEl => new bootstrap.Popover(popoverTriggerEl))
</script>
</body>
</html>
