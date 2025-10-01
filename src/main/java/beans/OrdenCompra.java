package beans;

import java.time.LocalDate;

public class OrdenCompra {
    // Campos “clásicos” de la OC
    private int idOrdenCompra;
    private String estado;
    private LocalDate fechaLlegada;

    // Campos extra para pintar cada ÍTEM de la OC en la tabla
    // (quedan en null si no los usas en otro contexto)
    private Integer idItem;
    private Integer cantidad;
    private String sku;
    private String producto;

    // --- Getters/Setters ---
    public int getIdOrdenCompra() { return idOrdenCompra; }
    public void setIdOrdenCompra(int idOrdenCompra) { this.idOrdenCompra = idOrdenCompra; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public LocalDate getFechaLlegada() { return fechaLlegada; }
    public void setFechaLlegada(LocalDate fechaLlegada) { this.fechaLlegada = fechaLlegada; }

    public Integer getIdItem() { return idItem; }
    public void setIdItem(Integer idItem) { this.idItem = idItem; }

    public Integer getCantidad() { return cantidad; }
    public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }

    public String getSku() { return sku; }
    public void setSku(String sku) { this.sku = sku; }

    public String getProducto() { return producto; }
    public void setProducto(String producto) { this.producto = producto; }
}

