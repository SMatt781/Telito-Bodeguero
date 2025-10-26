package com.example.telitobodeguero.filters;

import com.example.telitobodeguero.beans.Usuarios;
import com.example.telitobodeguero.daos.ListaPermisosDao;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.Set;

@WebFilter("/*")
public class AuthFilter implements Filter {

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        String path = request.getRequestURI().substring(request.getContextPath().length());

        // ✅ Rutas públicas (no se filtran)
        boolean esPublico =
                path.equals("/index.jsp") ||
                        path.equals("/LoginServlet") ||
                        path.startsWith("/css/") ||
                        path.startsWith("/js/") ||
                        path.startsWith("/images/") ||
                        path.startsWith("/vendor/") ||
                        path.startsWith("/Almacen/img/") ||
                        path.startsWith("/Logistica/images/") ||
                        path.startsWith("/Productor/images/");

        if (esPublico) {
            chain.doFilter(request, response);
            return;
        }

        // ❌ Si no hay sesión o usuario, redirige al login
        HttpSession sesion = request.getSession(false);
        if (sesion == null || sesion.getAttribute("usuarioLog") == null) {
            request.getSession(true).setAttribute("flash_warn", "Debes iniciar sesión.");
            response.sendRedirect(request.getContextPath() + "/index.jsp");
            return;
        }

        // 🧠 Anti-cache (para que el botón Atrás no muestre páginas protegidas)
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);

        // 🔄 Recargar permisos frescos (sidebar actualizado)
        Usuarios u = (Usuarios) sesion.getAttribute("usuarioLog");
        if (u != null && u.getRol() != null) {
            int roleId = u.getRol().getIdRoles();
            Set<Integer> permisosFresh = new ListaPermisosDao().permisosActivosDeRol(roleId);
            request.setAttribute("permisosRol", permisosFresh);
        }

        // ✅ Continuar con la petición normal
        chain.doFilter(request, response);
    }
}
