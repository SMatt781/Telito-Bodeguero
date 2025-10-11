package com.example.telitobodeguero.beans;

import java.time.LocalDate;

public class OrdenCompra {

    private int codigoOrdenCompra;
    private int cantidad;
    private String estado;
    private LocalDate fechaLlegada;
    private Producto producto;

    private String nombreProveedor;


    public int getCodigoOrdenCompra() {
        return codigoOrdenCompra;
    }
    public void setCodigoOrdenCompra(int codigoOrdenCompra) {
        this.codigoOrdenCompra = codigoOrdenCompra;
    }
    public String getNombreProveedor() {
        return nombreProveedor;
    }
    public void setNombreProveedor(String nombreProveedor) {
        this.nombreProveedor = nombreProveedor;
    }
    public Producto getProducto() {
        return producto;
    }
    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    // 🚨 2. Setter Híbrido para ID (CRUCIAL para DAOs que solo devuelven la FK) 🚨
    public void setProducto(int productoId) {
        // Asumiendo que Producto.java tiene setIdProducto
        Producto p = new Producto();
        p.setIdProducto(productoId);
        this.producto = p;
    }
    public LocalDate getFechaLlegada() {
        return fechaLlegada;
    }
    public void setFechaLlegada(LocalDate fechaLlegada) {
        this.fechaLlegada = fechaLlegada;
    }
    public int getCantidad() {
        return cantidad;
    }
    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }
    public String getEstado() {
        return estado;
    }
    public void setEstado(String estado) {
        this.estado = estado;
    }
}
