package com.example.telitobodeguero.daos;

import java.sql.*;

public class ZonaDao extends BaseDao {
    public Integer obtenerZonaIdPorUsuario(int idUsuario) throws SQLException {
        String sql = """
            SELECT z.idZonas
            FROM usuarios u
            JOIN distritos d ON u.Distritos_idDistritos = d.idDistritos
            JOIN zonas     z ON d.Zonas_idZonas        = z.idZonas
            WHERE u.idUsuarios = ? AND (u.activo IS NULL OR u.activo = 1)
        """;
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : null;
            }
        }
    }

}
