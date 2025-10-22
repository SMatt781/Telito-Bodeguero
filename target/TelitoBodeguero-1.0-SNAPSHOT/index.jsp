<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<!doctype html>
<html lang="es">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Telito - Admin | Iniciar sesión</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.0.2/dist/css/bootstrap.min.css" rel="stylesheet">

    <style>
        body{ margin:0; background:#f3f5f7; min-height:100vh; display:flex; }
        /* tarjeta centrada */
        .login-wrap{
            margin:auto; width:100%; max-width:420px; padding:2rem;
        }
        .form-card{
            background:#fff; border-radius:1rem; padding:1.75rem;
            box-shadow:0 8px 20px rgba(0,0,0,.12);
        }
        .brand-mini{
            display:flex; align-items:center; gap:.6rem; margin-bottom:1rem;
        }
        .brand-mini .dot{ width:10px; height:10px; border-radius:50%; background:#0d6efd; display:inline-block; }
        .muted{ color:#6c757d; }
        .logo-header {
            display: block; /* Necesario para que el margen automático funcione */
            margin: 0 auto 1.5rem; /* Centra horizontalmente y añade espacio abajo */
            max-width: 180px; /* Evita que el logo sea demasiado grande */
            height: auto; /* Mantiene la proporción de la imagen */
        }
    </style>
</head>
<body>

<div class="login-wrap">
    <img src="<%=request.getContextPath()%>/Almacen/img/telitoLogo.png"  alt="Logo de Telito"  class="logo-header">
    <div class="brand-mini">
        <span class="dot"></span>
        <h1 class="h5 m-0">Bienvenido a Telito</h1>
    </div>

    <div class="form-card">
        <h2 class="h4 mb-3">Iniciar sesión</h2>

        <!-- Mensaje de error (si el servlet lo setea) -->
        <%
            String loginError = (String) request.getAttribute("loginError");
            if (loginError != null && !loginError.isBlank()) {
        %>
        <div class="alert alert-danger" role="alert">
            <%= loginError %>
        </div>
        <% } %>

        <form method="POST" action="<%=request.getContextPath()%>/LoginServlet">
            <input type="hidden" name="accion" value="login"/>
            <div class="mb-3">
                <label class="form-label" for="correo">Correo</label>
                <input type="email" class="form-control" id="correo" name="correo"
                       placeholder="ejemplo@dominio.com" required>
            </div>

            <div class="mb-3">
                <label class="form-label" for="contrasenha">Contraseña</label>
                <div class="input-group">
                    <input type="password" class="form-control" id="contrasenha" name="contrasenha" required>
                    <button class="btn btn-outline-secondary" type="button" id="togglePass">Mostrar</button>
                </div>
                <div class="form-text muted">Usa tu correo y contraseña registrados.</div>
            </div>


            <button type="submit" class="btn btn-primary w-100">Ingresar</button>
        </form>

        <hr class="my-4">
        <div class="text-center muted small">
            © <%= new java.util.GregorianCalendar().get(java.util.Calendar.YEAR) %> Telito
        </div>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.0.2/dist/js/bootstrap.bundle.min.js"></script>
<script>
    // Mostrar/ocultar contraseña
    const btn = document.getElementById('togglePass');
    const pass = document.getElementById('contrasenha');
    btn.addEventListener('click', () => {
        const isText = pass.type === 'text';
        pass.type = isText ? 'password' : 'text';
        btn.textContent = isText ? 'Mostrar' : 'Ocultar';
    });
</script>
</body>
</html>
