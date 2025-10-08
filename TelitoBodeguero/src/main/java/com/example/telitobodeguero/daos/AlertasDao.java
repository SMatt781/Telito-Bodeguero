package com.example.telitobodeguero.daos;

import com.example.telitobodeguero.beans.Alertas;
import com.example.telitobodeguero.beans.Producto;
import com.example.telitobodeguero.beans.Zonas;

import java.sql.*;
import java.util.ArrayList;

public class AlertasDao {
    public ArrayList<Alertas> obtenerListaAlertas() {
        ArrayList<Alertas> listaAlertas = new ArrayList<>();
        try {
            String user = "root";
            String pass = "root";
            String url = "jdbc:mysql://localhost:3306/bodega-telito";

            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(url, user, pass);
            Statement stmt = conn.createStatement();

            ResultSet rs = stmt.executeQuery(
                    "SELECT a.idAlertas, a.mensaje, a.tipoAlerta, a.Producto_idProducto," +
                            "       p.idProducto AS producto_id, p.sku, p.nombre AS producto_nombre, p.Stock_productoId AS stock, z.idZonas, z.nombre as zonas_nombre " +
                            "FROM alertas a " +
                            "INNER JOIN producto p ON a.Producto_idProducto = p.idProducto " +
                            " INNER JOIN lote l on p.idProducto = l.Producto_idProducto " +
                            "INNER JOIN usuarios u ON l.Usuarios_idUsuarios = u.idUsuarios INNER JOIN distritos d ON d.idDistritos = u.Distritos_idDistritos INNER JOIN zonas z on d.Zonas_idZonas = z.idZonas"
            );
            while (rs.next()){
                Alertas alerta = new Alertas();
                alerta.setIdAlertas(rs.getInt(1));
                alerta.setMensaje(rs.getString(2));
                alerta.setTipoAlerta(rs.getString(3));
                alerta.setProducto_idProducto(rs.getInt(4));

                Producto  producto = new Producto();
                producto.setIdProducto(rs.getInt("producto_id"));
                producto.setSku(rs.getString("sku"));
                producto.setNombre(rs.getString("producto_nombre"));
                producto.setStock(rs.getInt("stock"));
                alerta.setProducto(producto);

                Zonas zonas = new Zonas();
                zonas.setIdZonas(rs.getInt("idZonas"));
                zonas.setNombre(rs.getString("zonas_nombre"));
                alerta.setZonas(zonas);
                listaAlertas.add(alerta);

            }
        }catch (ClassNotFoundException | SQLException e){
            e.printStackTrace();
        }
        return listaAlertas;
    }
}
