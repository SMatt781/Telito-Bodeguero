package com.example.telitobodeguero.daos;

import com.example.telitobodeguero.beans.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class ProductoDaoAlm extends BaseDao{
    public ArrayList<Producto> obtenerProductos(int idZona) {
        ArrayList<Producto> listaProductos =new ArrayList();

        //String sql = "SELECT p.sku as SKU from producto p";
        String sql = """
                SELECT p.sku AS SKU,
                       p.nombre AS NOMBRE_PRODUCTO,
                        SUM(
                               CASE
                                   WHEN m.tipo = 'IN'  THEN m.cantidad
                                   WHEN m.tipo = 'OUT' THEN -m.cantidad
                                   ELSE 0
                               END
                           ) AS STOCK,
                         b.codigo AS BLOQUE,
                         z.nombre AS ZONA,
                         p.idProducto,
                         z.idZonas,
                         b.idBloque,
                         l.idLote
                         FROM movimiento m
                         JOIN zonas z ON m.Zonas_idZonas = z.idZonas
                         JOIN lote l ON m.Lote_idLote = l.idLote
                         JOIN producto p ON l.Producto_idProducto = p.idProducto
                         LEFT JOIN bloque b ON m.Bloque_id = b.idBloque
                         WHERE z.idZonas = ?
                         GROUP BY
                           p.idProducto, p.sku, p.nombre,
                           z.idZonas, z.nombre,
                           b.codigo, b.idBloque,
                           l.idLote
                         HAVING STOCK <> 0
                         -- ORDER BY p.nombre ASC, l.idLote ASC, b.codigo ASC;
                          ORDER BY p.nombre ASC, l.idLote ASC;
                     """;





        try (Connection conn = this.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)){
            pstmt.setInt(1, idZona);

            try (ResultSet rs = pstmt.executeQuery()){
                while (rs.next()) {
                    Producto producto = new Producto();

                    producto.setSku(rs.getString(1));
                    producto.setNombre(rs.getString(2));

                    producto.setStock(rs.getInt(3));


                    Zonas zonas = new Zonas();

                    zonas.setIdZonas(idZona);
                    zonas.setNombre(rs.getString(5));
                    producto.setIdProducto(6);
                    Lote lote = new Lote();
                    lote.setIdLote(rs.getInt(9));
                    producto.setLote(lote);
                    producto.setUbicacionTemp(rs.getString(4));
                    producto.setIdBloque(rs.getInt(8));
                    producto.setZona(zonas);

                    listaProductos.add(producto);
                }
            }

        }catch (SQLException e){
            e.printStackTrace();
        }
        return listaProductos;
    }


    public Integer findProductoIdBySku(Connection conn, String sku) throws SQLException {
        String sql = "SELECT idProducto FROM Producto WHERE sku = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, sku);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : null;
            }
        }
    }

    public boolean lotePerteneceAProducto(Connection conn, int idLote, int idProducto) throws SQLException {
        String sql = "SELECT 1 FROM Lote WHERE idLote = ? AND Producto_idProducto = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idLote);
            ps.setInt(2, idProducto);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public ArrayList<Movimiento> obtenerInventarioParaPlantilla(int zonaId) {
        ArrayList<Movimiento> inventario = new ArrayList<>();

        String sql = """
        SELECT p.sku AS SKU,
               p.nombre AS NOMBRE_PRODUCTO,
               SUM(CASE WHEN m.tipo = 'IN' THEN m.cantidad WHEN m.tipo = 'OUT' THEN -m.cantidad ELSE 0 END) AS STOCK,
               b.codigo AS BLOQUE,
               z.nombre AS ZONA,
               p.idProducto,
               z.idZonas,
               b.idBloque,
               l.idLote
        FROM movimiento m
        JOIN zonas z ON m.Zonas_idZonas = z.idZonas
        JOIN lote l ON m.Lote_idLote = l.idLote
        JOIN producto p ON l.Producto_idProducto = p.idProducto
        LEFT JOIN bloque b ON m.Bloque_id = b.idBloque
        WHERE z.idZonas = ?
        GROUP BY
          p.idProducto, p.sku, p.nombre,
          z.idZonas, z.nombre,
          b.codigo, b.idBloque,
          l.idLote
        HAVING STOCK <> 0
        ORDER BY p.nombre ASC, l.idLote ASC;
        """;

        try (Connection conn = this.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, zonaId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Movimiento m = new Movimiento();

                    // Llenado de Beans (usamos Movimiento para transportar la fila completa)
                    Producto p = new Producto();
                    p.setSku(rs.getString(1));
                    p.setNombre(rs.getString(2));
                    p.setIdProducto(rs.getInt(6));
                    m.setProducto(p);

                    m.setCantidad(rs.getInt(3)); // STOCK actual

                    Lote l = new Lote();
                    l.setIdLote(rs.getInt(9));
                    m.setLote(l);

                    Bloque b = new Bloque();
                    b.setIdBloque(rs.getInt(8));
                    b.setCodigo(rs.getString(4));
                    m.setBloque(b);

                    Zonas z = new Zonas();
                    z.setIdZonas(zonaId);

                    m.setZona(z);

                    inventario.add(m);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return inventario;
    }





}
