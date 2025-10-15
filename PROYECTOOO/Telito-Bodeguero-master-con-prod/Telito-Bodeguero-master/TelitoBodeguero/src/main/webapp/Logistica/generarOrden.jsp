<%--
  Created by IntelliJ IDEA.
  User: Usuario
  Date: 29/09/2025
  Time: 12:45
  To change this template use File | Settings | File Templates.
--%>
<%@ page import="com.example.telitobodeguero.beans.Usuarios" %>
<%@ page import="com.example.telitobodeguero.beans.Producto" %>
<%@ page import="java.util.ArrayList" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    // Asegúrate de castear la lista correctamente.
    ArrayList<Producto> listaProductos = (ArrayList<Producto>) request.getAttribute("listaProductos");
    ArrayList<Zonas> listaZonas = (ArrayList<Zonas>) request.getAttribute("listaZonas"); // <-- NUEVA LISTA
%>
<html>
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">

    <!-- Bootstrap CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.0.2/dist/css/bootstrap.min.css" rel="stylesheet" xintegrity="sha384-EVSTQN3/azprG1Anm3QDgpJLIm9Nao0Yz1ztcQTwFspd3yD65VohhpuuCOmLASjC" crossorigin="anonymous">

    <!-- FUENTES para títulos y texto -->
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;600;700&family=Poppins:wght@700;800&display=swap" rel="stylesheet">

    <title>Telito - Logística!</title>

    <style>
        /* hover original */
        .nav-link.text-white:hover { background-color:#0d6efd; color:#fff !important; }

        /* Tipografías */
        :root { --brand:#2e63f5; }
        body { font-family: 'Inter', system-ui, -apple-system, Segoe UI, Roboto, Helvetica, Arial, sans-serif; }
        aside { font-family: 'Inter', system-ui, -apple-system, Segoe UI, Roboto, Helvetica, Arial, sans-serif; }
        aside .fs-4 { font-family:'Poppins', Inter, sans-serif; font-weight:700; letter-spacing:.2px; }

        h1 {
            font-family:'Poppins', Inter, sans-serif;
            font-weight:800; color:var(--brand);
            letter-spacing:.3px; margin-bottom:1.25rem;
        }

        /* Cards verdes claras estilo mockup */
        .stat-card {
            background:#a8e6a1; color:#0b2e18;
            border:0; border-radius:24px;
            box-shadow:0 6px 16px rgba(0,0,0,.08);
        }
        .stat-card .card-body { padding:18px 22px; }
        .stat-card .card-title {
            font-family:'Poppins', Inter, sans-serif;
            font-size:1rem; font-weight:700; margin-bottom:.25rem;
        }
        .stat-card .card-text {
            font-family:'Poppins', Inter, sans-serif;
            font-size:1.75rem; font-weight:800; margin:0;
        }

        /* Centrado visual del bloque de 3 cards */
        .container.my-4 { max-width: 1050px; } /* solo ajusta ancho para centrar mejor el conjunto */

        /* Panel del gráfico (sin cambios estructurales) */
        .chart-panel {
            background:#fff; border:1.5px solid rgba(0,0,0,.22);
            border-radius:18px; padding:18px; box-shadow:0 8px 24px rgba(0,0,0,.06);
        }
        .chart-frame {
            height:220px; border:2px solid rgba(0,0,0,.45);
            border-radius:22px; display:flex; align-items:center; justify-content:center;
            color:#6b707c; font-weight:700;
        }
        .legend-line {
            display:flex; align-items:center; gap:12px;
            font-weight:700; color:#2a2e36; margin-top:12px;
        }
        .pill { display:inline-block;width:48px;height:16px;border-radius:999px }
        .pill.in { background:#47c776 }   /* Entrada */
        .pill.out { background:#d23c3c }  /* Salida */

        .btn-apply {
            background:#bfbfbf; color:#2a2e36;
            border:1px solid #a7a7a7; border-radius:999px; font-weight:700;
        }
        .btn-apply:hover { filter:brightness(.95); }
    </style>
</head>
<body>
<div class="d-flex">
    <!-- Columna izquierda -->
    <aside class="d-flex flex-column flex-shrink-0 p-3 text-white bg-dark"
           style="width:280px;min-height:100vh;">
        <a href="#" class="d-flex align-items-center mb-3 text-white text-decoration-none">
            <span class="fs-4">Telito bodeguero</span>
        </a>
        <hr>

        <ul class="nav nav-pills flex-column mb-auto">
            <li class="nav-item">
                <a class="nav-link text-white" href="${sessionScope.homeUrl}">Inicio</a>
            </li>

            <a href="javascript:history.back()" class="btn btn-outline-light w-100 text-start mt-3">
                &larr; Volver
            </a>
        </ul>

        <div class="mt-auto pt-3">
            <a href="#" class="d-flex align-items-center text-white text-decoration-none dropdown-toggle"
               id="dropdownUser1" data-bs-toggle="dropdown">
                <img src="https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSu4RsCfoBYE4gan-EGNAvN3uRY0x43GwyK5A&s"
                     width="32" height="32" class="rounded-circle me-2">
                <strong>Usuario</strong>
            </a>
            <ul class="dropdown-menu dropdown-menu-dark text-small shadow" aria-labelledby="dropdownUser1">
                <li><a class="dropdown-item" href="#">Cerrar sesión</a></li>
            </ul>
        </div>
    </aside>

    <main class="flex-grow-1 p-4" style="background:#f8f9fa;min-height:100vh;">
        <hr>
        <h1 class="text-uppercase">Nueva orden de compra</h1>
        <hr>

        <form method="POST" action="StockBajo_OrdenCompra?action=crear">

            <div class="card mt-3">
                <div class="card-body">
                    <h6 class="fw-bold mb-3">Producto</h6>
                    <div class="mb-3">
                        <select class="form-select" name="productoId" required>
                            <option selected disabled value="">Selecciona un producto...</option>
                            <% if (listaProductos != null) {
                                for (Producto prod : listaProductos) { %>
                            <option value="<%= prod.getIdProducto() %>">
                                <%= prod.getNombre() %>
                            </option>
                            <%     }
                            } %>
                        </select>
                    </div>

                    <!-- 🚨 NUEVO DESPLEGABLE PARA ZONA -->
                    <h6 class="fw-bold mb-3">Zona de Recepción</h6>
                    <div class="mb-3">
                        <select class="form-select" name="zonaId" required>
                            <option selected disabled value="">Selecciona la zona...</option>
                            <% if (listaZonas != null) {
                                for (Zonas zona : listaZonas) { %>
                            <option value="<%= zona.getIdZonas() %>">
                                <%= zona.getNombre() %>
                            </option>
                            <%     }
                            } %>
                        </select>
                    </div>

                    <h6 class="fw-bold mb-3">Fecha de llegada</h6>
                    <div class="mb-3">
                        <input type="date" class="form-control" name="fechaLlegada" required>
                    </div>

                    <h6 class="fw-bold mb-3">Cantidad</h6>
                    <div class="mb-3">
                        <input type="number" class="form-control" name="cantidad" placeholder="Ingrese cantidad" min="1" required>
                    </div>

                    <div class="d-flex justify-content-end">
                        <button type="submit" class="btn btn-primary">Generar orden</button>
                    </div>
                </div>
            </div>
        </form>
    </main>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.0.2/dist/js/bootstrap.bundle.min.js" xintegrity="sha384-MrcW6ZMFYlzcLA8Nl+NtUVF0sA7MsXsP1UyJoMp4YLEuNSfAP+JcXn/tWtIaxVXM" crossorigin="anonymous"></script>
</body>
</html>
