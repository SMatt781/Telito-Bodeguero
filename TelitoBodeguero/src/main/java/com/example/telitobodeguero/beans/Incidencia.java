package com.example.telitobodeguero.beans;

public class Incidencia {

    private String tipoIncidencia;
    private int cantidad;
    private String descripcion;
    private int Lote_idLote ;

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
