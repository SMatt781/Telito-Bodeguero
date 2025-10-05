<%@ page import="java.util.Date" %>
<%@ page import="com.example.telitobodeguero.beans.Usuarios" %>
<%@ page import="com.example.telitobodeguero.beans.Distritos" %>
<%@ page import="com.example.telitobodeguero.beans.Roles" %>
<jsp:useBean id="listaDistritos" type="java.util.ArrayList<com.example.telitobodeguero.beans.Distritos>" scope="request" />
<jsp:useBean id="listaRoles" type="java.util.ArrayList<com.example.telitobodeguero.beans.Roles>" scope="request" />

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
<jsp:include page="/sidebar.jsp" />


<!-- ===== Contenido principal ===== -->
<main class="main" id="main">
    <div class="container-fluid">
        <div class="mb-2">
            <button type="button" class="btn btn-outline-secondary btn-sm" onclick="history.back()">
                &larr; Volver
            </button>
        </div>
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
                        <select class="form-select" name="distrito_id" id="distrito_id">
                            <option selected disabled>Seleccione...</option>
                            <% for (Distritos distritos : listaDistritos) { %>
                            <option value="<%=distritos.getIdDistritos()%>" <%=usuario.getDistrito().getIdDistritos() == (distritos.getIdDistritos()) ? "selected": "" %>><%=distritos.getNombre()%></option>
                            <% }%>
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
                        <select class="form-select" name="rol_id" id="rol_id">
                            <option selected>Seleccione...</option>
                            <% for (Roles roles : listaRoles) {%>
                            <option value="<%=roles.getIdRoles()%>" <%=usuario.getRol().getIdRoles() == roles.getIdRoles() ? "selected" : "" %>><%=roles.getNombre()%></option>
                            <% }%>
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

