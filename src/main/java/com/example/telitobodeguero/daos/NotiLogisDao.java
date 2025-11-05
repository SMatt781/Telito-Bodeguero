package com.example.telitobodeguero.daos;

import com.example.telitobodeguero.dtos.NotificacionLogisDTO;
import com.example.telitobodeguero.dtos.NotificacionTipo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;

public class NotiLogisDao extends BaseDao{

    public ArrayList<NotificacionLogisDTO> getNotificacionesStockBajoPorMovimientos(Integer zonaId, int umbralGlobal) {
        ArrayList<NotificacionLogisDTO> lista = new ArrayList<>();

        String baseSql =
                "SELECT p.idProducto AS IdProducto, " +
                        "       p.sku AS sku, " +
                        "       p.nombre AS nombre_producto, " +
                        "       z.nombre AS zona, " +
                        "       SUM(CASE " +
                        "           WHEN UPPER(m.tipo) = 'IN'  THEN m.cantidad " +
                        "           WHEN UPPER(m.tipo) = 'OUT' THEN -m.cantidad " +
                        "           ELSE 0 END) AS stock_total_zona " +
                        "FROM movimiento m " +
                        "INNER JOIN lote l ON m.Lote_idLote = l.idLote " +
                        "INNER JOIN producto p ON l.Producto_idProducto = p.idProducto " +
                        "INNER JOIN zonas z ON m.Zonas_idZonas = z.idZonas ";

        // Filtro por zona (opcional)
        String where = (zonaId != null) ? "WHERE z.idZonas = ? " : "";

        String groupHavingOrder =
                "GROUP BY p.idProducto, p.sku, p.nombre, z.idZonas, z.nombre " +
                        "HAVING stock_total_zona < ? " +
                        "ORDER BY (? - stock_total_zona) DESC";

        String sql = baseSql + where + groupHavingOrder;

        try (Connection conn = this.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            int idx = 1;
            if (zonaId != null) {
                ps.setInt(idx++, zonaId);
            }
            ps.setInt(idx++, umbralGlobal);
            ps.setInt(idx,   umbralGlobal);

            try (ResultSet rs = ps.executeQuery()) {
                final java.time.LocalDate hoy = java.time.LocalDate.now();

                while (rs.next()) {
                    int idProd        = rs.getInt("IdProducto");
                    String nombreProd = rs.getString("nombre_producto");
                    String zonaNombre = rs.getString("zona");
                    int stockAct      = rs.getInt("stock_total_zona");

                    String titulo  = "Stock bajo: " + nombreProd + " (#" + idProd + ")";
                    String mensaje = "Stock: " + stockAct + "  |  Umbral: " + umbralGlobal;

                    lista.add(new NotificacionLogisDTO(
                            NotificacionTipo.STOCK_BAJO,
                            titulo,
                            mensaje,
                            hoy,
                            zonaNombre  // puede ser null si no filtras por zona
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
        return lista;
    }

}
