package com.example.telitobodeguero.utils;

import com.example.telitobodeguero.beans.Roles_has_Permisos;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Set;

public class Auth {
    /**
     * Por si el usuario logueado tiene un permiso activo.
     * @param request HttpServletRequest (para leer la sesión actual)
     * @param permisoId ID del permiso a verificar (según la tabla Permisos)
     * @return true si lo tiene activo, false si no
     */
    @SuppressWarnings("unchecked")
    public static boolean can(HttpServletRequest request, int permisoId) {
        var ses = request.getSession(false);
        if (ses == null) return false;

        Object o = ses.getAttribute("permisosRol");
        if (o == null) return false;

        // Caso 1: lo guarda como Set<Integer>
        if (o instanceof Set) {
            return ((Set<Integer>) o).contains(permisoId);
        }

        // Caso 2 (compatibilidad): lo guarda como List<Roles_has_Permisos>
        if (o instanceof List) {
            for (Roles_has_Permisos rp : (List<Roles_has_Permisos>) o) {
                if (rp.getPermisos_idPermisos() == permisoId && rp.isActivacion()) return true;
            }
        }
        return false;
    }
}
