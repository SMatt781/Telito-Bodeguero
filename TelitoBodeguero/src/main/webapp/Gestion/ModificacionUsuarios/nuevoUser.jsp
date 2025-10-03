<%@ page import="java.util.Date" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!doctype html>
<html lang="es">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Telito - Admin | Nuevo usuario</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.0.2/dist/css/bootstrap.min.css" rel="stylesheet">

    <style>
        /* ===== LAYOUT ===== */
        body{ margin:0; background:#f3f5f7; }
        .sidebar{
            position:fixed; inset:0 auto 0 0;      /* top:0; left:0; bottom:0 */
            width:280px; background:#212529; color:#fff;
            z-index:1000; transition:width .25s ease; overflow-y:auto;
        }
        .sidebar.collapsed{ width:80px; }
        .sidebar .brand{ padding:1rem 1.25rem; display:flex; align-items:center; gap:.75rem; }
        .sidebar .toggle{ border:0; background:#0d6efd; color:#fff; padding:.5rem .6rem; border-radius:.5rem; }
        .sidebar .nav-link{ color:#d6d6d6; }
        .sidebar .nav-link:hover{ background:#0d6efd; color:#fff; }
        .text-label{ display:inline; }
        .sidebar.collapsed .text-label{ display:none; }

        .main{
            margin-left:280px; transition:margin-left .25s ease;
            min-height:100vh; padding:2rem;
        }
        .main.collapsed{ margin-left:80px; }

        /* ===== FORM CARD ===== */
        .form-card{
            background:#fff; border-radius:1rem; padding:1.5rem;
            box-shadow:0 8px 20px rgba(0,0,0,.12);
        }
        .section-title{ color:#1f2d3d; }
    </style>
</head>
<body>

<!-- ===== Sidebar izquierda ===== -->
<aside class="sidebar" id="sidebar">
    <div class="brand">
        <button class="toggle" id="btnToggle" aria-label="Alternar menú">&#9776;</button>
        <span class="h5 mb-0 text-label">Bienvenido - Adminn</span>
    </div>
    <hr class="text-secondary my-2">
    <ul class="nav nav-pills flex-column px-2">
        <li class="nav-item mb-1">
            <a class="nav-link" href="Admin_Inicio.jsp"><span class="text-label">Inicio</span></a>
        </li>
        <li class="nav-item mb-1">
            <a class="nav-link" href="<%=request.getContextPath()%>/ListaUsuariosServlet"><span class="text-label">Atrás</span></a>
        </li>
        <li class="nav-item mt-2">
            <a class="nav-link" href="index.jsp"><span class="text-label">Cerrar sesión</span></a>
        </li>
    </ul>
</aside>

<!-- ===== Contenido principal ===== -->
<main class="main" id="main">
    <div class="container-fluid">
        <h1 class="section-title h3 mb-3">Nuevo usuario</h1>

        <form method="POST" action="<%=request.getContextPath() %>/ListaUsuariosServlet?action=crear" class="form-card">
            <!-- Nombre y apellido -->
            <div class="mb-3">
                <label class="form-label">Nombre y apellido</label>
                <div class="input-group">
                    <input type="text" name="nombre" class="form-control" placeholder="Nombre" aria-label="First name">
                    <input type="text" name="apellido" class="form-control" placeholder="Apellido" aria-label="Last name">
                </div>
            </div>

            <!-- Email + Distrito -->
            <div class="row g-3">
                <div class="col-12 col-lg-7">
                    <div class="mb-1">
                        <label for="exampleInputEmail1" class="form-label">Email address</label>
                        <input type="email" name="correo" class="form-control" id="exampleInputEmail1" aria-describedby="emailHelp">
                        <div id="emailHelp" class="form-text">We'll never share your email with anyone else.</div>
                    </div>
                </div>
                <div class="col-12 col-lg-5">
                    <label class="form-label">Distrito</label>
                    <div class="input-group">
                        <label class="input-group-text" for="distrito">Distrito</label>
                        <select class="form-select" name="distrito_id" id="distrito">
                            <option selected disabled>Seleccione...</option>
                            <option value="1">Ancón</option>
                            <option value="2">Santa Rosa</option>
                            <option value="3">Carabayllo</option>
                            <option value="4">Puente Piedra</option>
                            <option value="5">Comas</option>
                            <option value="6">Los Olivos</option>
                            <option value="7">San Martín de Porres</option>
                            <option value="8">Independencia</option>
                            <option value="9">San Juan de Miraflores</option>
                            <option value="10">Villa María de Triunfo</option>
                            <option value="11">Villa el Salvador</option>
                            <option value="12">Pachacamac</option>
                            <option value="13">Lurín</option>
                            <option value="14">Punta Hermosa</option>
                            <option value="15">Punta Negra</option>
                            <option value="16">San Bartolo</option>
                            <option value="17">Santa María del Mar</option>
                            <option value="18">Pucusana</option>
                            <option value="19">San Juan de Lurigancho</option>
                            <option value="20">Lurigancho/Chosica</option>
                            <option value="21">Ate</option>
                            <option value="22">El Agustino</option>
                            <option value="23">Santa Anita</option>
                            <option value="24">La Molina</option>
                            <option value="25">Cieneguilla</option>
                            <option value="26">Rímac</option>
                            <option value="27">Cercado de Lima</option>
                            <option value="28">Breña</option>
                            <option value="29">Pueblo Libre</option>
                            <option value="30">Magdalena</option>
                            <option value="31">Jesús María</option>
                            <option value="32">La Victoria</option>
                            <option value="33">Lince</option>
                            <option value="34">San Isidro</option>
                            <option value="35">San Miguel</option>
                            <option value="36">Surquillo</option>
                            <option value="37">San Borja</option>
                            <option value="38">Santiago de Surco</option>
                            <option value="39">Barranco</option>
                            <option value="40">Chorrillos</option>
                            <option value="41">San Luis</option>
                            <option value="42">Miraflores</option>
                        </select>

                    </div>
                </div>
            </div>

            <!-- Password + Rol -->
            <div class="row g-3 mt-1">
                <div class="col-12 col-lg-7">
                    <label for="exampleInputPassword1" class="form-label">Password</label>
                    <input type="password" name="contrasenha" class="form-control" id="exampleInputPassword1">
                    <div class="form-check mt-2">
                        <input type="checkbox" class="form-check-input" id="exampleCheck1">
                        <label class="form-check-label" for="exampleCheck1">Check me out</label>
                    </div>
                </div>
                <div class="col-12 col-lg-5">
                    <label class="form-label">Rol</label>
                    <div class="input-group">
                        <label class="input-group-text" for="rol">Rol</label>
                        <select class="form-select" name="rol_id" id="rol">
                            <option selected>Seleccione...</option>
                            <option value="1">Administrador</option>
                            <option value="2">Logística</option>
                            <option value="3">Almacén</option>
                            <option value="4">Productor</option>
                        </select>
                    </div>
                </div>
            </div>

            <!-- Botón -->
            <div class="mt-4">
                <button type="submit" class="btn btn-primary">Submit</button>
            </div>
        </form>
    </div>
</main>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.0.2/dist/js/bootstrap.bundle.min.js"></script>
<script>
    // Toggle del sidebar
    const btn = document.getElementById('btnToggle');
    const sidebar = document.getElementById('sidebar');
    const main = document.getElementById('main');
    btn.addEventListener('click', () => {
        sidebar.classList.toggle('collapsed');
        main.classList.toggle('collapsed');
    });
</script>
</body>
</html>
