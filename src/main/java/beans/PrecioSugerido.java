package beans;

import java.math.BigDecimal;

public class PrecioSugerido {
    private int idProducto;
    private String sku;
    private String nombre;
    private BigDecimal precioUnitario;
    private BigDecimal precioSugerido;

    public int getIdProducto() { return idProducto; }
    public void setIdProducto(int idProducto) { this.idProducto = idProducto; }

    public String getSku() { return sku; }
    public void setSku(String sku) { this.sku = sku; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public BigDecimal getPrecioUnitario() { return precioUnitario; }
    public void setPrecioUnitario(BigDecimal precioUnitario) { this.precioUnitario = precioUnitario; }

    public BigDecimal getPrecioSugerido() { return precioSugerido; }
    public void setPrecioSugerido(BigDecimal precioSugerido) { this.precioSugerido = precioSugerido; }
}

