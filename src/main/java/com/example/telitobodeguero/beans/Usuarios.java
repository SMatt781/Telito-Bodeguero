package com.example.telitobodeguero.beans;

public class Usuarios {
    private int idUsuarios;
    private String nombre;
    private String apellido;
    private String correo;
    private String contrasenha;
    private int Roles_idRoles;
    private int Distritos_idDistritos;
    private Roles rol;
    private Distritos distrito;
    private Boolean activo;

    public int getIdUsuarios() {
        return idUsuarios;
    }

    public void setIdUsuarios(int idUsuarios) {
        this.idUsuarios = idUsuarios;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getContrasenha() {
        return contrasenha;
    }

    public void setContrasenha(String contrasenha) {
        this.contrasenha = contrasenha;
    }

    public int getRoles_idRoles() {
        return Roles_idRoles;
    }

    public void setRoles_idRoles(int roles_idRoles) {
        Roles_idRoles = roles_idRoles;
    }

    public int getDistritos_idDistritos() {
        return Distritos_idDistritos;
    }

    public void setDistritos_idDistritos(int distritos_idDistritos) {
        Distritos_idDistritos = distritos_idDistritos;
    }

    public Roles getRol() {
        return rol;
    }

    public void setRol(Roles rol) {
        this.rol = rol;
    }

    public Distritos getDistrito() {
        return distrito;
    }

    public void setDistrito(Distritos distrito) {
        this.distrito = distrito;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }
}
