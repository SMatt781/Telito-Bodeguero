package com.example.telitobodeguero.daos;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NotificacionProdDao {

    private static final String USER = "root";
    private static final String PASS = "12345678";
    private static final String URL = "jdbc:mysql://127.0.0.1:3306/bodega-telito";

    private Connection getConnection() throws SQLException {
        try { Class.forName("com.mysql.cj.jdbc.Driver"); }
        catch (ClassNotFoundException e) { throw new SQLException("Driver no encontrado", e); }
        return DriverManager.getConnection(URL, USER, PASS);
    }

    public List<String> obtenerNotificacionesPorProductor(int idProductor) {
        List<String> notificaciones = new ArrayList<>();

        String sql = """
            SELECT idOrdenCompra, estado, fecha_llegada 
            FROM OrdenCompra
            WHERE idProveedor = ?
            ORDER BY fecha_llegada DESC
        """;

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idProductor);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String estado = rs.getString("estado");
                    int idOrden = rs.getInt("idOrdenCompra");
                    String mensaje = generarMensajeNotificacion(estado, idOrden);
                    if (mensaje != null) notificaciones.add(mensaje);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return notificaciones;
    }

    private String generarMensajeNotificacion(String estado, int idOrden) {
        switch (estado) {
            case "Enviada":
                return "📦 Se te envió una nueva orden de compra #" + idOrden + ". Revisa tus Órdenes de Compra.";
            case "Recibido":
                return "🚛 La orden #" + idOrden + " fue recibida. Debes despachar el lote correspondiente.";
            case "En tránsito":
                return "🛣️ La orden #" + idOrden + " está en tránsito hacia el almacén.";
            default:
                return null; // no mostrar otras
        }
    }
}
