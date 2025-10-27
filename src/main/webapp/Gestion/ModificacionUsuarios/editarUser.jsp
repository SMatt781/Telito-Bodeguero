<%@ page import="java.util.Date" %>
<%@ page import="com.example.telitobodeguero.beans.Usuarios" %>
<%@ page import="com.example.telitobodeguero.beans.Distritos" %>
<%@ page import="com.example.telitobodeguero.beans.Roles" %>
<jsp:useBean id="listaDistritos" type="java.util.ArrayList<com.example.telitobodeguero.beans.Distritos>" scope="request" />
<jsp:useBean id="listaRoles" type="java.util.ArrayList<com.example.telitobodeguero.beans.Roles>" scope="request" />

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    Usuarios usuario = (Usuarios) request.getAttribute("usuario");
    if (usuario == null) {
        response.sendRedirect(request.getContextPath() + "/ListaUsuariosServlet");
        return;
    }
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
            position:fixed; inset:0 auto 0 0;
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

        /* ===== CARD DEL FORM ===== */
        .form-card{
            background:#fff; border-radius:1rem; padding:1.5rem;
            box-shadow:0 8px 20px rgba(0,0,0,.12);
            max-width:900px;
        }
        .section-title{
            color:#1f2d3d;
            font-weight:600;
        }
        .help-text {
            font-size: .8rem;
            color: #6c757d;
            margin-top: .25rem;
        }
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

        <!-- Importante: seguimos usando action=crear en tu servlet, pero con hidden id -->
        <form method="POST"
              action="<%=request.getContextPath()%>/ListaUsuariosServlet?action=crear"
              class="form-card">

            <input type="hidden" name="id" value="<%=usuario.getIdUsuarios()%>"/>

            <!-- FILA: Nombre / Apellido -->
            <div class="row g-3">
                <!-- Nombre -->
                <div class="col-12 col-lg-6">
                    <div class="input-group">
                        <span class="input-group-text" id="lblNombre">Nombre</span>
                        <input type="text"
                               class="form-control"
                               name="nombre"
                               aria-labelledby="lblNombre"
                               placeholder="Ej. Juan"
                               value="<%=usuario.getNombre()%>"
                               required>
                    </div>
                </div>

                <!-- Apellido -->
                <div class="col-12 col-lg-6">
                    <div class="input-group">
                        <span class="input-group-text" id="lblApellido">Apellido</span>
                        <input type="text"
                               class="form-control"
                               name="apellido"
                               aria-labelledby="lblApellido"
                               placeholder="Ej. Pérez"
                               value="<%=usuario.getApellido()%>"
                               required>
                    </div>
                </div>
            </div>

            <!-- FILA: Correo / Distrito -->
            <div class="row g-3 mt-3">
                <!-- Correo -->
                <div class="col-12 col-lg-7">
                    <div class="input-group">
                        <span class="input-group-text" id="lblCorreo">Correo</span>
                        <input type="email"
                               class="form-control"
                               name="correo"
                               aria-labelledby="lblCorreo"
                               value="<%=usuario.getCorreo()%>"
                               required>
                    </div>
                    <div class="help-text">Este correo será usado para el inicio de sesión.</div>
                </div>

                <!-- Distrito -->
                <div class="col-12 col-lg-5">
                    <div class="input-group">
                        <span class="input-group-text" id="lblDistrito">Distrito</span>
                        <select class="form-select"
                                name="distrito_id"
                                id="distrito_id"
                                aria-labelledby="lblDistrito">
                            <option disabled>Seleccione...</option>
                            <% for (Distritos d : listaDistritos) { %>
                            <option value="<%=d.getIdDistritos()%>"
                                    <%= (usuario.getDistrito() != null
                                            && usuario.getDistrito().getIdDistritos() == d.getIdDistritos())
                                            ? "selected" : "" %>>
                                <%=d.getNombre()%>
                            </option>
                            <% } %>
                        </select>
                    </div>
                </div>
            </div>

            <!-- FILA: Contraseña / Rol -->
            <div class="row g-3 mt-3">
                <!-- Contraseña nueva -->
                <div class="col-12 col-lg-7">
                    <div class="input-group">
                        <span class="input-group-text" id="lblPass">Nueva contraseña</span>
                        <input type="password"
                               class="form-control"
                               name="contrasenha"
                               id="contrasenhaInput"
                               aria-labelledby="lblPass"
                               placeholder="********"
                               autocomplete="new-password">
                    </div>

                    <div class="form-check mt-2">
                        <input type="checkbox"
                               class="form-check-input"
                               id="togglePassCheck">
                        <label class="form-check-label" for="togglePassCheck">
                            Mostrar contraseña
                        </label>
                    </div>

                    <div class="help-text">
                        Si no quieres cambiar la contraseña, deja este campo vacío.
                    </div>
                </div>

                <!-- Rol -->
                <div class="col-12 col-lg-5">
                    <div class="input-group">
                        <span class="input-group-text" id="lblRol">Rol</span>
                        <select class="form-select"
                                name="rol_id"
                                id="rol_id"
                                aria-labelledby="lblRol">
                            <option disabled>Seleccione...</option>
                            <% for (Roles r : listaRoles) { %>
                            <option value="<%=r.getIdRoles()%>"
                                    <%= (usuario.getRol() != null
                                            && usuario.getRol().getIdRoles() == r.getIdRoles())
                                            ? "selected" : "" %>>
                                <%=r.getNombre()%>
                            </option>
                            <% } %>
                        </select>
                    </div>
                </div>
            </div>

            <!-- BOTONES -->
            <div class="mt-4 d-flex gap-2">
                <button type="submit" class="btn btn-primary">Guardar cambios</button>
                <a class="btn btn-outline-secondary"
                   href="<%=request.getContextPath()%>/ListaUsuariosServlet">
                    Cancelar
                </a>
            </div>

        </form>
    </div>
</main>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.0.2/dist/js/bootstrap.bundle.min.js"></script>
<script>
    // Toggle del sidebar (protegido por si en esta vista no está el botón)
    const btn = document.getElementById('btnToggle');
    const sidebar = document.getElementById('sidebar');
    const main = document.getElementById('main');
    if (btn && sidebar && main) {
        btn.addEventListener('click', () => {
            sidebar.classList.toggle('collapsed');
            main.classList.toggle('collapsed');
        });
    }

    // Mostrar / ocultar nueva contraseña
    const passInput = document.getElementById('contrasenhaInput');
    const passToggle = document.getElementById('togglePassCheck');

    if (passInput && passToggle) {
        passToggle.addEventListener('change', () => {
            passInput.type = passToggle.checked ? 'text' : 'password';
        });
    }
</script>
</body>
</html>
