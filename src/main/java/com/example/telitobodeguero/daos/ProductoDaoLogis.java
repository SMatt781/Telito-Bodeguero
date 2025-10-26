package com.example.telitobodeguero.daos;

import com.example.telitobodeguero.beans.*;

import java.sql.*;
import java.util.ArrayList;

public class ProductoDaoLogis extends BaseDao {
    // NOTA: BaseDao debe proporcionar el método getConnection()

    // -------------------------------------------------------------------------
    // 1. OBTENER TOP 5 PRODUCTOS CON STOCK BAJO (INCLUYE ZONA)
    //    Devuelve solo SKU, Nombre, Stock (restante) y Zona.
    // -------------------------------------------------------------------------
    /**
     * Obtiene los 5 productos con menor stock remanente,
     * siempre y cuando el stock restante sea menor que el stock mínimo configurado.
     * Incluye la primera Zona asociada para mostrar en la tabla de resumen.
     * @return Lista de Producto (con stock_restante y Zona asociados).
     */
    public ArrayList<Producto> obtenerTop5ProductosStockBajo() {
        ArrayList<Producto> listaProductos = new ArrayList<>();

        String sql = """
            SELECT 
                p.idProducto, 
                p.sku, 
                p.nombre AS nombre_producto, 
                p.stockMinimo, -- Incluimos el stock mínimo del producto
                z.nombre AS zona_nombre,
                -- Cálculo del stock real en esa zona
                SUM(
                    CASE 
                        WHEN m.tipo = 'IN'  THEN m.cantidad
                        WHEN m.tipo = 'OUT' THEN -m.cantidad
                        ELSE 0
                    END
                ) AS stock_total_zona
            FROM movimiento m
            INNER JOIN lote l      ON m.Lote_idLote = l.idLote
            INNER JOIN producto p  ON l.Producto_idProducto = p.idProducto
            INNER JOIN zonas z     ON m.Zonas_idZonas = z.idZonas
            GROUP BY 
                p.idProducto, 
                p.sku, 
                p.nombre, 
                p.stockMinimo,
                z.idZonas, 
                z.nombre
            HAVING 
                stock_total_zona <= p.stockMinimo -- FILTRO CRÍTICO
            ORDER BY
                stock_total_zona ASC;
            """;

        try (Connection conn = this.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {

                Producto producto = new Producto();
                producto.setIdProducto(rs.getInt("idProducto"));
                producto.setSku(rs.getString("sku"));
                producto.setNombre(rs.getString("nombre_producto"));

                // Mapeamos el stock calculado por zona al campo 'stock' del bean
                producto.setStock(rs.getInt("stock_total_zona"));

                // Mapeamos el stock mínimo
                producto.setStockMinimo(rs.getInt("stockMinimo"));

                // Mapeamos la zona
                Zonas zona = new Zonas();
                zona.setNombre(rs.getString("zona_nombre"));
                producto.setZona(zona);

                listaProductos.add(producto);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return listaProductos;
    }

    // Método para contar el total de productos/zonas en stock bajo
    public int contarTotalProductosStockBajo() {
        int total = 0;

        // La consulta es similar a la anterior, pero solo necesitamos contar las filas que cumplen la condición
        String sql = """
            SELECT 
                COUNT(*) AS total_alertas
            FROM (
                SELECT 
                    p.idProducto, 
                    p.stockMinimo, 
                    z.idZonas,
                    SUM(
                        CASE 
                            WHEN m.tipo = 'IN'  THEN m.cantidad
                            WHEN m.tipo = 'OUT' THEN -m.cantidad
                            ELSE 0
                        END
                    ) AS stock_total_zona
                FROM movimiento m
                INNER JOIN lote l      ON m.Lote_idLote = l.idLote
                INNER JOIN producto p  ON l.Producto_idProducto = p.idProducto
                INNER JOIN zonas z     ON m.Zonas_idZonas = z.idZonas
                GROUP BY 
                    p.idProducto, 
                    p.stockMinimo,
                    z.idZonas
                HAVING 
                    stock_total_zona <= p.stockMinimo -- FILTRO CRÍTICO
            ) AS AlertasStock;
            """;

        try (Connection conn = this.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                total = rs.getInt("total_alertas");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return total;
    }


    // 2. MÉTODO CORREGIDO: obtenerListaProductos()
    public ArrayList<Producto> obtenerListaProductos() {
        ArrayList<Producto> listaProductos = new ArrayList<>();

        String sql =
                "SELECT p.idProducto AS IdProducto, " +
                        "       p.sku AS sku, " +
                        "       p.nombre AS nombre_producto, " +
                        "       z.nombre AS zona, " +
                        "       SUM(CASE " +
                        "           WHEN m.tipo = 'IN'  THEN m.cantidad " +
                        "           WHEN m.tipo = 'OUT' THEN -m.cantidad " +
                        "           ELSE 0 END) AS stock_total_zona " +
                        "FROM movimiento m " +
                        "INNER JOIN lote l ON m.Lote_idLote = l.idLote " +
                        "INNER JOIN producto p ON l.Producto_idProducto = p.idProducto " +
                        "INNER JOIN zonas z ON m.Zonas_idZonas = z.idZonas " +
                        "GROUP BY p.idProducto, p.sku, p.nombre, z.idZonas, z.nombre";

        try (Connection conn = this.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {

                Producto producto = new Producto();
                producto.setIdProducto(rs.getInt("IdProducto"));
                producto.setSku(rs.getString("sku"));
                producto.setNombre(rs.getString("nombre_producto"));

                // acá usamos stock del bean Producto para guardar stock_total_zona calculado
                producto.setStock(rs.getInt("stock_total_zona"));

                // zona
                Zonas zona = new Zonas();
                zona.setNombre(rs.getString("zona"));
                producto.setZona(zona);

                // lote: en este nivel agregado no hay un lote único claro -> lo dejamos null
                producto.setLote(null);

                listaProductos.add(producto);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return listaProductos;
    }

}
