package com.example.telitobodeguero.daos;

import com.example.telitobodeguero.beans.Alertas;
import com.example.telitobodeguero.beans.Producto;
import com.example.telitobodeguero.beans.Zonas;

import java.sql.*;
import java.util.ArrayList;

public class InicioAdminDao {

    public ArrayList<Object[]> obtenerGastosPorZona() {
        ArrayList<Object[]> listaGastos = new ArrayList<>();
        String sql = "SELECT z.nombre AS zona, SUM(p.precio * m.cantidad) AS gastoTotal " +
                "FROM movimiento m " +
                "JOIN lote l ON m.Lote_idLote = l.idLote " +
                "JOIN producto p ON l.Producto_idProducto = p.idProducto " +
                "JOIN zonas z ON m.Zonas_idZonas = z.idZonas " +
                "WHERE m.tipo = 'IN' " +
                "GROUP BY z.nombre";

        try (Connection conn = DB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                String zona = rs.getString("zona");
                double gasto = rs.getDouble("gastoTotal");
                listaGastos.add(new Object[]{zona, gasto});
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return listaGastos;
    }


    public int obtenerCantidadIncidencias() {
        String sql = "SELECT COUNT(*) FROM incidencia";

        try (Connection conn = DB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (Exception e) {
            System.out.println("Error al contar incidencias: " + e.getMessage());
            e.printStackTrace();
        }

        return 0;
    }




    //de acá tambien se sacan los valores para el cuadrito, la operacion se hace en el servlet
    public ArrayList<Alertas> obtenerListaAlertas() {
        ArrayList<Alertas> listaAlertas = new ArrayList<>();
        String sql = "SELECT a.idAlertas, a.mensaje, a.tipoAlerta, a.Producto_idProducto, " +
                "       p.idProducto AS producto_id, p.sku, p.nombre AS producto_nombre, p.stock, " +
                "       z.idZonas, z.nombre AS zonas_nombre " +
                "FROM alertas a " +
                "JOIN producto p ON a.Producto_idProducto = p.idProducto " +
                "JOIN zonas z ON a.Zonas_idZonas = z.idZonas";

        try (Connection conn = DB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Alertas alerta = new Alertas();
                alerta.setIdAlertas(rs.getInt("idAlertas"));
                alerta.setMensaje(rs.getString("mensaje"));
                alerta.setTipoAlerta(rs.getString("tipoAlerta"));
                alerta.setProducto_idProducto(rs.getInt("Producto_idProducto"));

                Producto producto = new Producto();
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

            System.out.println("Total alertas obtenidas: " + listaAlertas.size());

        } catch (Exception e) {
            System.out.println("Error al obtener alertas: " + e.getMessage());
            e.printStackTrace();
        }

        return listaAlertas;
    }


    public int contarUsuariosActivos() {
        int cantidad = 0;
        String sql = "SELECT COUNT(*) FROM Usuarios WHERE activo = true";

        try (Connection conn = DB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            if (rs.next()) {
                cantidad = rs.getInt(1);
            }

        } catch (Exception e) {
            System.out.println("Error al contar usuarios activos: " + e.getMessage());
            e.printStackTrace();
        }

        return cantidad;
    }
}
