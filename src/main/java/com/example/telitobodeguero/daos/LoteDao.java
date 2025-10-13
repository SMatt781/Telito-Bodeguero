package com.example.telitobodeguero.daos;

import com.example.telitobodeguero.beans.Lote;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LoteDao {

    /* =========================
       HELPERS
       ========================= */

    // Convierte "yyyy-MM-dd" a java.sql.Date (acepta null o vacío)
    private Date toSqlDate(String s) {
        return (s == null || s.isBlank()) ? null : Date.valueOf(s);
    }

    /** Stock disponible para el usuario = Producto.stock - SUM(lotes del usuario) */
    public int stockDisponible(String productoId, int usuarioId) throws SQLException {
        String sql = """
            SELECT (p.stock - COALESCE(SUM(l.cantidad),0)) AS disponible
            FROM Producto p
            LEFT JOIN Lote l
              ON l.Producto_idProducto = p.idProducto
             AND l.Usuarios_idUsuarios = ?
            WHERE p.idProducto = ?
            GROUP BY p.stock
        """;
        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, usuarioId);
            // Si Producto_idProducto es String pero guarda el id numérico, parseamos:
            ps.setInt(2, Integer.parseInt(productoId));
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt("disponible");
            }
        }
        return 0;
    }

    /** Cantidad actual de un lote (para validar incrementos en editar) */
    public int cantidadActualDelLote(int idLote, int usuarioId) throws SQLException {
        String sql = "SELECT cantidad FROM Lote WHERE idLote=? AND Usuarios_idUsuarios=?";
        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, idLote);
            ps.setInt(2, usuarioId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        }
        return 0;
    }

    /* =========================
       LISTADOS Y OBTENER
       ========================= */

    public List<Lote> listarPorProductor(Integer idProductor, String idProductoOpt) throws SQLException {
        String base = """
            SELECT l.idLote, l.fechaVencimiento, l.ubicacion, l.Producto_idProducto,
                   l.cantidad, l.Usuarios_idUsuarios, p.nombre AS producto, p.sku AS sku
            FROM Lote l
            LEFT JOIN Producto p ON p.idProducto = l.Producto_idProducto
            WHERE l.Usuarios_idUsuarios = ?
            """;
        String order = " ORDER BY l.fechaVencimiento";
        List<Lote> lista = new ArrayList<>();

        String sql = (idProductoOpt == null || idProductoOpt.isBlank())
                ? base + order
                : base + " AND l.Producto_idProducto = ?" + order;

        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, idProductor);
            if (idProductoOpt != null && !idProductoOpt.isBlank()) {
                ps.setInt(2, Integer.parseInt(idProductoOpt));
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Lote l = new Lote();
                    Date fv = rs.getDate("fechaVencimiento");
                    l.setIdLote(rs.getInt("idLote"));
                    l.setFechaVencimiento(fv == null ? null : fv.toString()); // String
                    l.setUbicacion(rs.getString("ubicacion"));
                    l.setProducto_idProducto(String.valueOf(rs.getInt("Producto_idProducto"))); // String
                    l.setCantidad(rs.getInt("cantidad"));
                    l.setUsuarios_idUsuarios(rs.getInt("Usuarios_idUsuarios"));
                    l.setProductoNombre(rs.getString("producto"));
                    // si luego necesitas SKU, añade un campo en el bean y setéalo aquí
                    lista.add(l);
                }
            }
        }
        return lista;
    }

    public Lote obtenerPorIdYProductor(int idLote, int idProductor) throws SQLException {
        String sql = """
            SELECT l.idLote, l.fechaVencimiento, l.ubicacion, l.Producto_idProducto,
                   l.cantidad, l.Usuarios_idUsuarios
            FROM Lote l
            WHERE l.idLote = ? AND l.Usuarios_idUsuarios = ?
            """;
        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, idLote);
            ps.setInt(2, idProductor);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Lote l = new Lote();
                    Date fv = rs.getDate("fechaVencimiento");
                    l.setIdLote(rs.getInt("idLote"));
                    l.setFechaVencimiento(fv == null ? null : fv.toString()); // String
                    l.setUbicacion(rs.getString("ubicacion"));
                    l.setProducto_idProducto(String.valueOf(rs.getInt("Producto_idProducto"))); // String
                    l.setCantidad(rs.getInt("cantidad"));
                    l.setUsuarios_idUsuarios(rs.getInt("Usuarios_idUsuarios"));
                    return l;
                }
            }
        }
        return null;
    }

    /* =========================
       CREAR / ACTUALIZAR / BORRAR
       ========================= */

    /** Lanza SQLException con SQLState 45000 si no hay stock suficiente */
    public void crear(Lote l) throws SQLException {
        int disponible = stockDisponible(l.getProducto_idProducto(), l.getUsuarios_idUsuarios());
        if (l.getCantidad() > disponible) {
            throw new SQLException("Stock insuficiente: solicitado=" + l.getCantidad() +
                    ", disponible=" + disponible, "45000");
        }
        String sql = """
            INSERT INTO Lote (fechaVencimiento, ubicacion, Producto_idProducto, cantidad, Usuarios_idUsuarios)
            VALUES (?,?,?,?,?)
            """;
        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setDate(1, toSqlDate(l.getFechaVencimiento()));       // String -> Date
            ps.setString(2, l.getUbicacion());
            ps.setInt(3, Integer.parseInt(l.getProducto_idProducto())); // String -> int
            ps.setInt(4, l.getCantidad());
            ps.setInt(5, l.getUsuarios_idUsuarios());
            ps.executeUpdate();
        }
    }

    /** Valida incremento: disponible + cantidadActual permite crecer sin pasarse */
    public void actualizar(Lote l) throws SQLException {
        int actual = cantidadActualDelLote(l.getIdLote(), l.getUsuarios_idUsuarios());
        int disponible = stockDisponible(l.getProducto_idProducto(), l.getUsuarios_idUsuarios());
        int maxPermitido = disponible + actual;

        if (l.getCantidad() > maxPermitido) {
            throw new SQLException("Stock insuficiente al actualizar: solicitado=" + l.getCantidad() +
                    ", máximo permitido=" + maxPermitido, "45000");
        }

        String sql = """
            UPDATE Lote
            SET fechaVencimiento=?, ubicacion=?, cantidad=?
            WHERE idLote=? AND Usuarios_idUsuarios=?
            """;
        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setDate(1, toSqlDate(l.getFechaVencimiento())); // String -> Date
            ps.setString(2, l.getUbicacion());
            ps.setInt(3, l.getCantidad());
            ps.setInt(4, l.getIdLote());
            ps.setInt(5, l.getUsuarios_idUsuarios());
            ps.executeUpdate();
        }
    }

    public void borrar(int idLote, int idProductor) throws SQLException {
        String sql = "DELETE FROM Lote WHERE idLote=? AND Usuarios_idUsuarios=?";
        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, idLote);
            ps.setInt(2, idProductor);
            ps.executeUpdate();
        }
    }
}



