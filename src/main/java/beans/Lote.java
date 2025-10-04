package beans;

import java.time.LocalDate;

public class Lote {
    private int idLote;
    private LocalDate fechaVencimiento;
    private String ubicacion;
    private int productoId;
    private int cantidad;
    private int usuarioId;
    private String productoNombre;

    // 🔹 Nuevo campo para SKU
    private String productoSku;

    // --- Getters y Setters ---
    public int getIdLote() {
        return idLote;
    }

    public void setIdLote(int idLote) {
        this.idLote = idLote;
    }

    public LocalDate getFechaVencimiento() {
        return fechaVencimiento;
    }

    public void setFechaVencimiento(LocalDate fechaVencimiento) {
        this.fechaVencimiento = fechaVencimiento;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }

    public int getProductoId() {
        return productoId;
    }

    public void setProductoId(int productoId) {
        this.productoId = productoId;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public int getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(int usuarioId) {
        this.usuarioId = usuarioId;
    }

    public String getProductoNombre() {
        return productoNombre;
    }

    public void setProductoNombre(String productoNombre) {
        this.productoNombre = productoNombre;
    }

    // 🔹 Getter y Setter del SKU
    public String getProductoSku() {
        return productoSku;
    }

    public void setProductoSku(String productoSku) {
        this.productoSku = productoSku;
    }
}
