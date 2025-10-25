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
    public void generarAlertasDesdeStock() {
        String sqlProductos = "SELECT idProducto, nombre, stock, stockMinimo FROM producto";
        String sqlInsert = "INSERT INTO alertas (mensaje, tipoAlerta, Producto_idProducto, Zonas_idZonas) " +
                "VALUES (?, ?, ?, ?)";

        try (Connection conn = DB.getConnection();
             PreparedStatement psProductos = conn.prepareStatement(sqlProductos);
             ResultSet rs = psProductos.executeQuery()) {

            // Reiniciar tabla y contador de AUTO_INCREMENT
            conn.prepareStatement("TRUNCATE TABLE alertas").executeUpdate();

            while (rs.next()) {
                Producto producto = new Producto();
                producto.setIdProducto(rs.getInt("idProducto"));
                producto.setNombre(rs.getString("nombre"));
                producto.setStock(rs.getInt("stock"));
                producto.setStockMinimo(rs.getInt("stockMinimo"));

                String tipoAlerta = null;
                if (producto.getStock() == 0) {
                    tipoAlerta = "Stock nulo";
                } else if (producto.getStock() < producto.getStockMinimo()) {
                    tipoAlerta = "Stock bajo";
                }

                if (tipoAlerta != null) {
                    int zonaId = obtenerZonaPorProducto(producto.getIdProducto(), conn);
                    try (PreparedStatement psInsert = conn.prepareStatement(sqlInsert)) {
                        psInsert.setString(1, producto.getNombre() + " en " + tipoAlerta.toLowerCase());
                        psInsert.setString(2, tipoAlerta);
                        psInsert.setInt(3, producto.getIdProducto());
                        psInsert.setInt(4, zonaId);
                        psInsert.executeUpdate();
                        System.out.println("Alerta insertada: " + producto.getNombre() + " -> " + tipoAlerta);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    // Obtiene la zona real del producto a través de lote y movimiento
    private int obtenerZonaPorProducto(int idProducto, Connection conn) throws SQLException {
        String sql = "SELECT m.Zonas_idZonas FROM movimiento m " +
                "JOIN lote l ON m.Lote_idLote = l.idLote " +
                "WHERE l.Producto_idProducto = ? " +
                "ORDER BY m.fecha DESC LIMIT 1";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idProducto);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("Zonas_idZonas");
                }
            }
        }
        return 1; // fallback si no se encuentra zona
    }

    // Obtiene la lista de alertas con producto y zona
    public ArrayList<Alertas> obtenerListaAlertas() {
        ArrayList<Alertas> listaAlertas = new ArrayList<>();
        String sql = "SELECT a.idAlertas, a.mensaje, a.tipoAlerta, a.Producto_idProducto, a.Zonas_idZonas, " +
                "       p.idProducto AS producto_id, p.sku, p.nombre AS producto_nombre, p.precio, p.stock, p.stockMinimo, " +
                "       z.idZonas AS zona_id, z.nombre AS zona_nombre " +
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
                producto.setPrecio(Double.parseDouble(String.valueOf(rs.getBigDecimal("precio"))));
                producto.setStock(rs.getInt("stock"));
                producto.setStockMinimo(rs.getInt("stockMinimo"));
                alerta.setProducto(producto);

                Zonas zona = new Zonas();
                zona.setIdZonas(rs.getInt("zona_id"));
                zona.setNombre(rs.getString("zona_nombre"));
                alerta.setZonas(zona);

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
