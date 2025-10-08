package com.example.telitobodeguero.daos;
import com.example.telitobodeguero.beans.Lote;
import com.example.telitobodeguero.beans.Movimiento;
import com.example.telitobodeguero.beans.Producto;
import com.example.telitobodeguero.beans.Zonas;

import java.sql.*;
import java.util.ArrayList;

public class ProductoDao extends BaseDao{
    public ArrayList<Producto> obtenerProductos(int idZona) {
        ArrayList<Producto> listaProductos =new ArrayList();

        //String sql = "SELECT p.sku as SKU from producto p";
        String sql = "SELECT p.sku as SKU, "+
                "p.nombre as Nombre, "+
                "coalesce(sum(CASE WHEN m.tipo = 'IN' THEN m.cantidad ELSE -m.cantidad END),0) AS Stock, "+

                "group_concat(distinct l.idLote) as Lotes, " +
                "z.nombre as Zona " +
                "FROM  producto p " +
                "INNER JOIN lote l on p.idProducto = l.Producto_idProducto " +
                "INNER JOIN movimiento m on l.idLote = m.Lote_idLote " +
                "INNER JOIN zonas z on z.idZonas = m.Zonas_idZonas " +

                "WHERE z.idZonas = ? " +
                "GROUP BY p.idProducto, z.nombre";



        try (Connection conn = this.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)){
            pstmt.setInt(1, idZona);

            try (ResultSet rs = pstmt.executeQuery()){
                while (rs.next()) {
                    Producto producto = new Producto();

                    producto.setSku(rs.getString(1));
                    producto.setNombre(rs.getString(2));

                    producto.setStock(rs.getInt(3));
                    producto.setLotes(rs.getString(4));
                    Zonas  zonas = new Zonas();

                    zonas.setIdZonas(idZona);
                    zonas.setNombre(rs.getString(5));
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



}
