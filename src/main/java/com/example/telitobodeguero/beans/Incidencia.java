package com.example.telitobodeguero.beans;

public class Incidencia {

    private int idIncidencia;
    private String tipoIncidencia;
    private int cantidad;
    private String descripcion;
    private int Lote_idLote ;
    private Zonas zona;
    private Producto producto;
    private String estado;
    //puesto por almacen, por favor no tocar
    private String ubicacionTemporal;
    private int idUbicacion;

    public String getUbicacionTemporal() {
        return ubicacionTemporal;
    }

    public void setUbicacionTemporal(String ubicacionTemporal) {
        this.ubicacionTemporal = ubicacionTemporal;
    }

    public int getIdUbicacion() {
        return idUbicacion;
    }

    public void setIdUbicacion(int idUbicacion) {
        this.idUbicacion = idUbicacion;
    }


    public int getIdIncidencia() {
        return idIncidencia;
    }

    public void setIdIncidencia(int idIncidencia) {
        this.idIncidencia = idIncidencia;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    public Zonas getZona() {
        return zona;
    }

    public void setZona(Zonas zona) {
        this.zona = zona;
    }

    public String getTipoIncidencia() {
        return tipoIncidencia;
    }

    public void setTipoIncidencia(String tipoIncidencia) {
        this.tipoIncidencia = tipoIncidencia;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public int getLote_idLote() {
        return Lote_idLote;
    }

    public void setLote_idLote(int lote_idLote) {
        Lote_idLote = lote_idLote;
    }
}
