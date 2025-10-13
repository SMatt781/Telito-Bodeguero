//package com.example.telitobodeguero.beans;
//
//import java.time.LocalDate;
//
//public class Movimiento {
//    private int idRegistro;
//    private String tipoMovimiento;
//    private LocalDate fecha;
//    private int cantidad;
//    private int Lote_idLote;
//    private int Zonas_idZonas;
//    private Producto producto;
//    private Zonas zona;
//
//    public Zonas getZona() {
//        return zona;
//    }
//    public void setZona(Zonas zona) {
//        this.zona = zona;
//    }
//
//    public Producto getProducto() {
//        return producto;
//    }
//    public void setProducto(Producto producto) {
//        this.producto = producto;
//    }
//
//    public int getIdRegistro() {
//        return idRegistro;
//    }
//
//    public void setIdRegistro(int idRegistro) {
//        this.idRegistro = idRegistro;
//    }
//
//    public String getTipoMovimiento() {
//        return tipoMovimiento;
//    }
//
//    public void setTipoMovimiento(String tipo) {
//        this.tipoMovimiento = tipo;
//    }
//
//    public LocalDate getFecha() {
//        return fecha;
//    }
//
//    public void setFecha(LocalDate fecha) {
//        this.fecha = fecha;
//    }
//
//    public int getCantidad() {
//        return cantidad;
//    }
//
//    public void setCantidad(int cantidad) {
//        this.cantidad = cantidad;
//    }
//
//    public int getLote_idLote() {
//        return Lote_idLote;
//    }
//
//    public void setLote_idLote(int lote_idLote) {
//        Lote_idLote = lote_idLote;
//    }
//
//    public int getZonas_idZonas() {
//        return Zonas_idZonas;
//    }
//
//    public void setZonas_idZonas(int zonas_idZonas) {
//        Zonas_idZonas = zonas_idZonas;
//    }
//
//
//}

package com.example.telitobodeguero.beans;

import java.time.LocalDate;

public class Movimiento {
    private int idRegistro;
    private String tipoMovimiento;
    private LocalDate fecha;
    private int cantidad;
    //    private int Lote_idLote;
//    private int Zonas_idZonas;
    private Lote lote;
    private Producto producto;
    private Zonas zona;

    public Zonas getZona() {
        return zona;
    }
    public void setZona(Zonas zona) {
        this.zona = zona;
    }

    public Producto getProducto() {
        return producto;
    }
    public void setProducto(Producto producto) {
        this.producto = producto;
    }


    public Lote getLote() {
        return lote;
    }

    public void setLote(Lote lote) {
        this.lote = lote;
    }

    public int getIdRegistro() {
        return idRegistro;
    }

    public void setIdRegistro(int idRegistro) {
        this.idRegistro = idRegistro;
    }

    public String getTipoMovimiento() {
        return tipoMovimiento;
    }

    public void setTipoMovimiento(String tipo) {
        this.tipoMovimiento = tipo;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }
//
//    public int getLote_idLote() {
//        return Lote_idLote;
//    }
//
//    public void setLote_idLote(int lote_idLote) {
//        Lote_idLote = lote_idLote;
//    }
//
//    public int getZonas_idZonas() {
//        return Zonas_idZonas;
//    }
//
//    public void setZonas_idZonas(int zonas_idZonas) {
//        Zonas_idZonas = zonas_idZonas;
//    }


}

