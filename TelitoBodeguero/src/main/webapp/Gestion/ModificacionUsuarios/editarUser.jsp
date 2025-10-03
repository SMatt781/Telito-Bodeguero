<%@ page import="java.util.Date" %>
<%@ page import="com.example.telitobodeguero.beans.Usuarios" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    Usuarios usuario = (Usuarios) request.getAttribute("usuario");
    if (usuario == null) {  // seguridad
        response.sendRedirect(request.getContextPath() + "/ListaUsuariosServlet");
        return;
    }
    Integer rolSel = (usuario.getRol() != null) ? usuario.getRol().getIdRoles() : null;
    Integer distSel = (usuario.getDistrito() != null) ? usuario.getDistrito().getIdDistritos() : null;
%>
<!doctype html>
<html lang="es">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Telito - Admin | Editar usuario</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.0.2/dist/css/bootstrap.min.css" rel="stylesheet">

    <style>
        /* ===== LAYOUT ===== */
        body{ margin:0; background:#f3f5f7; }
        .sidebar{
            position:fixed; inset:0 auto 0 0; width:280px; background:#212529; color:#fff;
            z-index:1000; transition:width .25s ease; overflow-y:auto;
        }
        .sidebar.collapsed{ width:80px; }
        .sidebar .brand{ padding:1rem 1.25rem; display:flex; align-items:center; gap:.75rem; }
        .sidebar .toggle{ border:0; background:#0d6efd; color:#fff; padding:.5rem .6rem; border-radius:.5rem; }
        .sidebar .nav-link{ color:#d6d6d6; }
        .sidebar .nav-link:hover{ background:#0d6efd; color:#fff; }
        .text-label{ display:inline; }
        .sidebar.collapsed .text-label{ display:none; }

        .main{ margin-left:280px; transition:margin-left .25s ease; min-height:100vh; padding:2rem; }

        /* ===== FORM CARD ===== */
        .form-card{ background:#fff; border-radius:1rem; padding:1.5rem; box-shadow:0 8px 20px rgba(0,0,0,.12); }
        .section-title{ color:#1f2d3d; }
    </style>
</head>
<body>

<!-- ===== Sidebar izquierda ===== -->
<aside class="sidebar" id="sidebar">
    <div class="brand">
        <button class="toggle" id="btnToggle" aria-label="Alternar menú">&#9776;</button>
        <span class="h5 mb-0 text-label">Bienvenido - Admin</span>
    </div>
    <hr class="text-secondary my-2">
    <ul class="nav nav-pills flex-column px-2">
        <li class="nav-item mb-1">
            <a class="nav-link" href="<%=request.getContextPath()%>/index.html"><span class="text-label">Inicio</span></a>
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
        <h1 class="section-title h3 mb-3">Editar usuario</h1>


        <form method="POST" action="<%=request.getContextPath()%>/ListaUsuariosServlet?action=crear" class="form-card">
            <input type="hidden" name="id" value="<%=usuario.getIdUsuarios()%>"/>

            <!-- Nombre y apellido -->
            <div class="mb-3">
                <label class="form-label">Nombre y apellido</label>
                <div class="input-group">
                    <input type="text" name="nombre"   class="form-control" placeholder="Nombre"
                           value="<%=usuario.getNombre()%>" aria-label="First name" required>
                    <input type="text" name="apellido" class="form-control" placeholder="Apellido"
                           value="<%=usuario.getApellido()%>" aria-label="Last name" required>
                </div>
            </div>

            <!-- Email + Distrito -->
            <div class="row g-3">
                <div class="col-12 col-lg-7">
                    <div class="mb-1">
                        <label class="form-label">Email</label>
                        <input type="email" name="correo" class="form-control"
                               value="<%=usuario.getCorreo()%>" required>
                    </div>
                </div>
                <div class="col-12 col-lg-5">
                    <label class="form-label">Distrito</label>
                    <div class="input-group">
                        <label class="input-group-text" for="distrito">Distrito</label>
                        <select class="form-select" name="distrito_id" id="distrito" required>
                            <option value="" disabled>Seleccione...</option>
                            <option value="1"  <%= (distSel!=null && distSel==1)? "selected": "" %>>Ancón</option>
                            <option value="2"  <%= (distSel!=null && distSel==2)? "selected": "" %>>Santa Rosa</option>
                            <option value="3"  <%= (distSel!=null && distSel==3)? "selected": "" %>>Carabayllo</option>
                            <option value="4"  <%= (distSel!=null && distSel==4)? "selected": "" %>>Puente Piedra</option>
                            <option value="5"  <%= (distSel!=null && distSel==5)? "selected": "" %>>Comas</option>
                            <option value="6"  <%= (distSel!=null && distSel==6)? "selected": "" %>>Los Olivos</option>
                            <option value="7"  <%= (distSel!=null && distSel==7)? "selected": "" %>>San Martín de Porres</option>
                            <option value="8"  <%= (distSel!=null && distSel==8)? "selected": "" %>>Independencia</option>
                            <option value="9"  <%= (distSel!=null && distSel==9)? "selected": "" %>>San Juan de Miraflores</option>
                            <option value="10" <%= (distSel!=null && distSel==10)? "selected": "" %>>Villa María de Triunfo</option>
                            <option value="11" <%= (distSel!=null && distSel==11)? "selected": "" %>>Villa el Salvador</option>
                            <option value="12" <%= (distSel!=null && distSel==12)? "selected": "" %>>Pachacamac</option>
                            <option value="13" <%= (distSel!=null && distSel==13)? "selected": "" %>>Lurín</option>
                            <option value="14" <%= (distSel!=null && distSel==14)? "selected": "" %>>Punta Hermosa</option>
                            <option value="15" <%= (distSel!=null && distSel==15)? "selected": "" %>>Punta Negra</option>
                            <option value="16" <%= (distSel!=null && distSel==16)? "selected": "" %>>San Bartolo</option>
                            <option value="17" <%= (distSel!=null && distSel==17)? "selected": "" %>>Santa María del Mar</option>
                            <option value="18" <%= (distSel!=null && distSel==18)? "selected": "" %>>Pucusana</option>
                            <option value="19" <%= (distSel!=null && distSel==19)? "selected": "" %>>San Juan de Lurigancho</option>
                            <option value="20" <%= (distSel!=null && distSel==20)? "selected": "" %>>Lurigancho/Chosica</option>
                            <option value="21" <%= (distSel!=null && distSel==21)? "selected": "" %>>Ate</option>
                            <option value="22" <%= (distSel!=null && distSel==22)? "selected": "" %>>El Agustino</option>
                            <option value="23" <%= (distSel!=null && distSel==23)? "selected": "" %>>Santa Anita</option>
                            <option value="24" <%= (distSel!=null && distSel==24)? "selected": "" %>>La Molina</option>
                            <option value="25" <%= (distSel!=null && distSel==25)? "selected": "" %>>Cieneguilla</option>
                            <option value="26" <%= (distSel!=null && distSel==26)? "selected": "" %>>Rímac</option>
                            <option value="27" <%= (distSel!=null && distSel==27)? "selected": "" %>>Cercado de Lima</option>
                            <option value="28" <%= (distSel!=null && distSel==28)? "selected": "" %>>Breña</option>
                            <option value="29" <%= (distSel!=null && distSel==29)? "selected": "" %>>Pueblo Libre</option>
                            <option value="30" <%= (distSel!=null && distSel==30)? "selected": "" %>>Magdalena</option>
                            <option value="31" <%= (distSel!=null && distSel==31)? "selected": "" %>>Jesús María</option>
                            <option value="32" <%= (distSel!=null && distSel==32)? "selected": "" %>>La Victoria</option>
                            <option value="33" <%= (distSel!=null && distSel==33)? "selected": "" %>>Lince</option>
                            <option value="34" <%= (distSel!=null && distSel==34)? "selected": "" %>>San Isidro</option>
                            <option value="35" <%= (distSel!=null && distSel==35)? "selected": "" %>>San Miguel</option>
                            <option value="36" <%= (distSel!=null && distSel==36)? "selected": "" %>>Surquillo</option>
                            <option value="37" <%= (distSel!=null && distSel==37)? "selected": "" %>>San Borja</option>
                            <option value="38" <%= (distSel!=null && distSel==38)? "selected": "" %>>Santiago de Surco</option>
                            <option value="39" <%= (distSel!=null && distSel==39)? "selected": "" %>>Barranco</option>
                            <option value="40" <%= (distSel!=null && distSel==40)? "selected": "" %>>Chorrillos</option>
                            <option value="41" <%= (distSel!=null && distSel==41)? "selected": "" %>>San Luis</option>
                            <option value="42" <%= (distSel!=null && distSel==42)? "selected": "" %>>Miraflores</option>
                        </select>
                    </div>
                </div>
            </div>

            <!-- Password + Rol -->
            <div class="row g-3 mt-1">
                <div class="col-12 col-lg-7">
                    <label class="form-label">Password (dejar vacío para no cambiar)</label>
                    <input type="password" name="contrasenha" class="form-control" autocomplete="new-password">
                </div>
                <div class="col-12 col-lg-5">
                    <label class="form-label">Rol</label>
                    <div class="input-group">
                        <label class="input-group-text" for="rol">Rol</label>
                        <select class="form-select" name="rol_id" id="rol" required>
                            <option value="" disabled>Seleccione...</option>
                            <option value="1" <%= (rolSel!=null && rolSel==1)? "selected": "" %>>Administrador</option>
                            <option value="2" <%= (rolSel!=null && rolSel==2)? "selected": "" %>>Logística</option>
                            <option value="3" <%= (rolSel!=null && rolSel==3)? "selected": "" %>>Almacén</option>
                            <option value="4" <%= (rolSel!=null && rolSel==4)? "selected": "" %>>Productor</option>
                        </select>
                    </div>
                </div>
            </div>

            <!-- Botón -->
            <div class="mt-4 d-flex gap-2">
                <button type="submit" class="btn btn-primary">Guardar cambios</button>
                <a class="btn btn-outline-secondary" href="<%=request.getContextPath()%>/ListaUsuariosServlet">Cancelar</a>
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

