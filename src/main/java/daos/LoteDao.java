package daos;

import beans.Lote;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class LoteDao {

    public List<Lote> listarPorProductor(Integer idProductor, Integer idProductoOpt) throws SQLException {
        String base = """
            SELECT l.idLote, l.fechaVencimiento, l.ubicacion, l.Producto_idProducto,
                   l.cantidad, l.Usuarios_idUsuarios, p.nombre AS producto
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
                    l.setIdLote(rs.getInt("idLote"));
                    Date fv = rs.getDate("fechaVencimiento");
                    l.setFechaVencimiento(fv == null ? null : fv.toLocalDate());
                    l.setUbicacion(rs.getString("ubicacion"));
                    l.setProductoId(rs.getInt("Producto_idProducto"));
                    l.setCantidad(rs.getInt("cantidad"));
                    l.setUsuarioId(rs.getInt("Usuarios_idUsuarios"));
                    l.setProductoNombre(rs.getString("producto"));
                    lista.add(l);
                }
            }
        }
        System.out.println("[LoteDao] encontrados=" + lista.size() +
                " (productor=" + idProductor + ", filtroProducto=" + idProductoOpt + ")");
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

    public void crear(Lote l) throws SQLException {
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

    public void actualizar(Lote l) throws SQLException {
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
