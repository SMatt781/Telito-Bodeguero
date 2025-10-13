package com.example.telitobodeguero.beans;

public class Roles_has_Permisos{
    private int Roles_idRoles;
    private int Permisos_idPermisos;
    private boolean activacion;
    private Roles rol;
    private Permisos permiso;


    public int getRoles_idRoles() {
        return Roles_idRoles;
    }

    public void setRoles_idRoles(int roles_idRoles) {
        Roles_idRoles = roles_idRoles;
    }

    public int getPermisos_idPermisos() {
        return Permisos_idPermisos;
    }

    public void setPermisos_idPermisos(int permisos_idPermisos) {
        Permisos_idPermisos = permisos_idPermisos;
    }

    public boolean isActivacion() {
        return activacion;
    }

    public void setActivacion(boolean activacion) {
        this.activacion = activacion;
    }

    public Roles getRol() {
        return rol;
    }

    public void setRol(Roles rol) {
        this.rol = rol;
    }

    public Permisos getPermiso() {
        return permiso;
    }

    public void setPermiso(Permisos permiso) {
        this.permiso = permiso;
    }
}
