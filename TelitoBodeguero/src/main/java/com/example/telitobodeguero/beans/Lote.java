package com.example.telitobodeguero.beans;

public class Lote {
    private int idLote;
    private String fechaVencimiento;
    private String ubicacion;
    //private String Producto_idProducto;
    private Producto producto;
    private int cantidad;
    private int Usuarios_idUsuarios;

    public int getIdLote() {
        return idLote;
    }

    public void setIdLote(int idLote) {
        this.idLote = idLote;
    }

    public String getFechaVencimiento() {
        return fechaVencimiento;
    }

    public void setFechaVencimiento(String fechaVencimiento) {
        this.fechaVencimiento = fechaVencimiento;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }

//    public String getProducto_idProducto() {
//        return Producto_idProducto;
//    }
//
//    public void setProducto_idProducto(String producto_idProducto) {
//        Producto_idProducto = producto_idProducto;
//    }


    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public int getUsuarios_idUsuarios() {
        return Usuarios_idUsuarios;
    }

    public void setUsuarios_idUsuarios(int usuarios_idUsuarios) {
        Usuarios_idUsuarios = usuarios_idUsuarios;
    }
}
