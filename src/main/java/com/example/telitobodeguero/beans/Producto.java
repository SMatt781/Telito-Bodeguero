package com.example.telitobodeguero.beans;

public class Producto {
    private int idProducto;
    private String sku;
    private String nombre;
    private int stock;
    private String lotes;
    private double precio;
    private int stockMinimo;
    private Lote lote; // Atributo objeto
    private Zonas zona;  // Atributo objeto (corregido a minúscula)
    // para almacen no borrar por favor
    private String ubicacionTemp;
    private int idBloque;

    public int getIdBloque() {
        return idBloque;
    }

    public void setIdBloque(int idBloque) {
        this.idBloque = idBloque;
    }

    public String getUbicacionTemp() {
        return ubicacionTemp;
    }

    public void setUbicacionTemp(String ubicacionTemp) {
        this.ubicacionTemp = ubicacionTemp;
    }


    // 1. Getter de Objeto (Corregido a 'zona' minúscula)
    public Zonas getZona() {
        return zona;
    }

    // 2. Setter de Objeto (Necesario si se asigna un objeto Zona completo)
    public void setZona(Zonas zona) {
        this.zona = zona;
    }


    public String getLotes() {
        return lotes;
    }

    public void setLotes(String lotes) {
        this.lotes = lotes;
    }

    public int getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(int idProducto) {
        this.idProducto = idProducto;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public Lote getLote() {
        return lote;
    }

    public void setLote(Lote lote) {
        this.lote = lote;
    }

    public int getStockMinimo() {
        return stockMinimo;
    }

    public void setStockMinimo(int stockMinimo) {
        this.stockMinimo = stockMinimo;
    }
}
