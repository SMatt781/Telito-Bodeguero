<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.*, java.util.Map" %>

<%
    String ctx = request.getContextPath();
    List<Map<String, String>> notificaciones =
            (List<Map<String, String>>) request.getAttribute("notificaciones");
%>

<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1" />
    <title>Notificaciones · Productor</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.0.2/dist/css/bootstrap.min.css"
          rel="stylesheet" crossorigin="anonymous" />
    <link rel="stylesheet"
          href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.4/css/all.min.css">

    <style>
        /* ===== BASE GENERAL ===== */
        body{
            min-height:100vh;
            background:#f7fafc;
            overflow-x:hidden;
            display:flex;
            font-family:'Inter', system-ui, sans-serif;
        }

        /* ===== SIDEBAR ===== */
        .sidebar{
            position:fixed;
            top:0; left:0; bottom:0;
            width:280px;
            background:#212529;
            color:#fff;
            transition:width .25s ease;
            z-index:1000;
            overflow-y:auto;
        }

        .sidebar.collapsed{ width:80px; }
        .sidebar.collapsed .text-label{ display:none; }

        .sidebar .brand{
            padding:1rem 1.25rem;
            display:flex;
            align-items:center;
            gap:.75rem;
        }

        /* 🔵 BOTÓN TOGGLE — MISMO DISEÑO DEL PRIMER SIDEBAR */
        .sidebar .brand .toggle{
            border:0;
            background:#0d6efd;  /* azul */
            color:#fff;          /* icono blanco */
            padding:.5rem .6rem;
            border-radius:.5rem;
            font-size:1.1rem;
            cursor:pointer;
        }

        /* ===== MAIN ===== */
        .main{
            margin-left:280px;
            transition:margin-left .25s ease;
            padding:2rem;
            width:100%;
        }

        .main.collapsed{ margin-left:80px; }

        /* ===== TITULOS ===== */
        .titulo{
            font-size:2.1rem;
            color:#2d3748;
            font-weight:800;
            margin-bottom:5px;
        }

        .subtitulo{
            color:#718096;
            margin-bottom:20px;
        }

        /* ===== ALERTAS ===== */
        .alert-card{
            display:flex;
            align-items:flex-start;
            padding:16px;
            margin-bottom:18px;
            border-radius:6px;
            box-shadow:0 1px 3px rgba(0,0,0,.1);
            border-top:4px solid;
            background:#fff;
            transition:transform .15s ease;
        }

        .alert-card:hover{ transform:translateY(-3px); }

        .alert-icon{
            font-size:1.4rem;
            margin-right:12px;
            min-width:30px;
            padding-top:3px;
        }

        .alert-content strong{
            font-size:1.1rem;
            display:block;
            color:#2d3748;
            margin-bottom:4px;
        }

        .alert-content p{
            margin:0 0 6px 0;
            color:#4a5568;
        }

        .alert-meta{
            font-size:.85rem;
            color:#718096;
        }

        /* ===== VARIANTES ===== */
        .success{ background:#f0fdf4; border-top-color:#22c55e; }
        .success .alert-icon, .success strong{ color:#166534; }

        .warning{ background:#fffbeb; border-top-color:#f59e0b; }
        .warning .alert-icon, .warning strong{ color:#854d0e; }

        .info{ background:#eff6ff; border-top-color:#3b82f6; }
        .info .alert-icon, .info strong{ color:#1e40af; }

        .error{ background:#fef2f2; border-top-color:#ef4444; }
        .error .alert-icon, .error strong{ color:#991b1b; }

        /* SIN NOTIFICACIONES */
        .no-noti{
            text-align:center;
            color:#718096;
            padding:40px;
            border:1px dashed #cbd5e0;
            background:#fff;
            border-radius:8px;
            max-width:600px;
            margin:auto;
        }

        /* =============================
                 RESPONSIVE
           ============================= */
        @media(max-width:991.98px){
            .sidebar{
                width:0 !important;
                overflow:hidden !important;
            }

            .main{
                margin-left:0 !important;
                padding:1.3rem;
            }

            .alert-card{
                flex-direction:column;
                gap:.75rem;
            }
        }
    </style>

</head>

<body>

<jsp:include page="/sidebar.jsp" />

<main class="main" id="main">

    <h1 class="titulo">Notificaciones</h1>
    <p class="subtitulo">Aquí puedes ver todas tus alertas recientes.</p>

    <!-- LISTA -->
    <%
        if (notificaciones == null || notificaciones.isEmpty()) {
    %>

    <div class="no-noti">
        🎉 ¡Todo en orden! No tienes notificaciones nuevas.
    </div>

    <% } else {

        for (Map<String,String> noti : notificaciones) {

            String estado = noti.get("estado");
            String css = "info";
            String icon = "fas fa-info-circle";

            if ("Enviada".equals(estado)) {
                css = "success"; icon = "fas fa-check-circle";
            } else if ("Recibido".equals(estado)) {
                css = "info"; icon = "fas fa-box";
            } else if ("En tránsito".equals(estado)) {
                css = "warning"; icon = "fas fa-truck";
            }
    %>

    <div class="alert-card <%= css %>">
        <i class="alert-icon <%= icon %>"></i>

        <div class="alert-content">
            <strong><%= noti.get("mensaje") %></strong>
            <div class="alert-meta">
                Estado: <%= estado %>
            </div>
        </div>
    </div>

    <% } } %>

</main>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.0.2/dist/js/bootstrap.bundle.min.js"></script>
<script>
    const btn = document.getElementById('btnToggle');
    const sidebar = document.getElementById('sidebar');
    const main = document.getElementById('main');

    if(btn){
        btn.addEventListener('click',()=>{
            sidebar.classList.toggle('collapsed');
            main.classList.toggle('collapsed');
        });
    }
</script>

</body>
</html>
