package com.example.telitobodeguero.beans;

public class Alertas {
    private int idAlertas;
    private String mensaje;
    private String tipoAlerta;
    private int Producto_idProducto;
    private Producto producto;
    private Zonas zonas;

    public int getIdAlertas() {
        return idAlertas;
    }

    public void setIdAlertas(int idAlertas) {
        this.idAlertas = idAlertas;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public String getTipoAlerta() {
        return tipoAlerta;
    }

    public void setTipoAlerta(String tipoAlerta) {
        this.tipoAlerta = tipoAlerta;
    }

    public int getProducto_idProducto() {
        return Producto_idProducto;
    }

    public void setProducto_idProducto(int producto_idProducto) {
        Producto_idProducto = producto_idProducto;
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    public Zonas getZonas() {
        return zonas;
    }

    public void setZonas(Zonas zonas) {
        this.zonas = zonas;
    }
}
