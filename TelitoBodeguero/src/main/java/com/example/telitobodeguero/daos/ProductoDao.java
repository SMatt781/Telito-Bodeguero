package com.example.telitobodeguero.daos;
import com.example.telitobodeguero.beans.Producto;
import com.example.telitobodeguero.beans.Zonas;

import java.sql.*;
import java.util.ArrayList;

public class ProductoDao{
    public ArrayList<Producto> obtenerProductos() {
        ArrayList<Producto> listaProductos =new ArrayList();

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        String user = "root";
        String pass = "12345678";
        String url = "jdbc:mysql://localhost:3306/bodega-telito";
        //String sql = "SELECT p.sku as SKU from producto p";
        String sql = "SELECT p.sku as SKU, "+
                "p.nombre as Nombre, "+
                "coalesce(sum(CASE WHEN m.tipo = 'IN' THEN m.cantidad ELSE -m.cantidad END),0) AS Stock, "+

                "group_concat(distinct l.idLote) as Lotes, " +
                "z.idZonas, z.nombre as Zona " +
                "FROM  producto p " +
                "INNER JOIN lote l on p.idProducto = l.Producto_idProducto " +
                "INNER JOIN movimiento m on l.idLote = m.Lote_idLote " +
                "INNER JOIN zonas z on z.idZonas = m.Zonas_idZonas " +

                "WHERE z.nombre = 'Oeste' " +
                "GROUP BY p.idProducto, z.nombre";

        try (Connection conn = DriverManager.getConnection(url, user, pass);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)){

            while (rs.next()){
                Producto producto = new Producto();
                producto.setSku(rs.getString(1));
                producto.setNombre(rs.getString(2));
//                producto.setDistrito(rs.getString(3));
                producto.setStock(rs.getInt(3));
                producto.setLotes(rs.getString(4));
                Zonas  zonas = new Zonas();
                zonas.setIdZonas(rs.getInt("idZonas"));
                zonas.setNombre(rs.getString("Zona"));
                producto.setZona(zonas);


                listaProductos.add(producto);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return listaProductos;
    }
}
