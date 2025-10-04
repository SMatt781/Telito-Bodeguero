package daos;

import beans.Lote;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class LoteDao {

    /* =========================
       HELPERS DE CÁLCULO
       ========================= */

    /** Stock disponible para el usuario = Producto.stock - SUM(lotes del usuario) */
    public int stockDisponible(int productoId, int usuarioId) throws SQLException {
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
            ps.setInt(2, productoId);
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

    public List<Lote> listarPorProductor(Integer idProductor, Integer idProductoOpt) throws SQLException {
        String base = """
            SELECT l.idLote, l.fechaVencimiento, l.ubicacion, l.Producto_idProducto,
                   l.cantidad, l.Usuarios_idUsuarios, p.nombre AS producto, p.sku AS sku
            FROM Lote l
            LEFT JOIN Producto p ON p.idProducto = l.Producto_idProducto
            WHERE l.Usuarios_idUsuarios = ?
            """;
        String order = " ORDER BY l.fechaVencimiento";
        List<Lote> lista = new ArrayList<>();

        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement(
                     idProductoOpt == null ? base + order : (base + " AND l.Producto_idProducto = ?" + order))) {

            ps.setInt(1, idProductor);
            if (idProductoOpt != null) ps.setInt(2, idProductoOpt);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Lote l = new Lote();
                    Date fv = rs.getDate("fechaVencimiento");
                    l.setIdLote(rs.getInt("idLote"));
                    l.setFechaVencimiento(fv == null ? null : fv.toLocalDate());
                    l.setUbicacion(rs.getString("ubicacion"));
                    l.setProductoId(rs.getInt("Producto_idProducto"));
                    l.setCantidad(rs.getInt("cantidad"));
                    l.setUsuarioId(rs.getInt("Usuarios_idUsuarios"));
                    l.setProductoNombre(rs.getString("producto"));
                    // si tu bean tiene sku:
                    try { l.getClass().getMethod("setProductoSku", String.class).invoke(l, rs.getString("sku")); } catch (Exception ignore) {}
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
                    l.setFechaVencimiento(fv == null ? null : fv.toLocalDate());
                    l.setUbicacion(rs.getString("ubicacion"));
                    l.setProductoId(rs.getInt("Producto_idProducto"));
                    l.setCantidad(rs.getInt("cantidad"));
                    l.setUsuarioId(rs.getInt("Usuarios_idUsuarios"));
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
        int disponible = stockDisponible(l.getProductoId(), l.getUsuarioId());
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
            ps.setDate(1, l.getFechaVencimiento() == null ? null : Date.valueOf(l.getFechaVencimiento()));
            ps.setString(2, l.getUbicacion());
            ps.setInt(3, l.getProductoId());
            ps.setInt(4, l.getCantidad());
            ps.setInt(5, l.getUsuarioId());
            ps.executeUpdate();
        }
    }

    /** Valida incremento: disponible + cantidadActual permite crecer sin pasarse */
    public void actualizar(Lote l) throws SQLException {
        int actual = cantidadActualDelLote(l.getIdLote(), l.getUsuarioId());
        int disponible = stockDisponible(l.getProductoId(), l.getUsuarioId());
        int maxPermitido = disponible + actual; // puedes subir hasta esto

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
            ps.setDate(1, l.getFechaVencimiento() == null ? null : Date.valueOf(l.getFechaVencimiento()));
            ps.setString(2, l.getUbicacion());
            ps.setInt(3, l.getCantidad());
            ps.setInt(4, l.getIdLote());
            ps.setInt(5, l.getUsuarioId());
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

