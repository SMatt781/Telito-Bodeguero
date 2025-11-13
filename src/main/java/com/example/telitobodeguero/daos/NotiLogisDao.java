package com.example.telitobodeguero.daos;

import com.example.telitobodeguero.beans.OrdenCompra;
import com.example.telitobodeguero.dtos.NotificacionLogisDTO;
import com.example.telitobodeguero.dtos.NotificacionTipo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class NotiLogisDao extends BaseDao {

    // === NUEVO: reutilizamos también las órdenes de compra, igual que Admin ===
    private final OrdenCompraDao ordenCompraDao = new OrdenCompraDao();

    // ============================================================
    // 1) NOTIFICACIONES DE STOCK BAJO (TU LÓGICA ORIGINAL)
    // ============================================================
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
                final LocalDate hoy = LocalDate.now();

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

    // ============================================================
    // 2) NUEVO: NOTIFICACIONES DE CAMBIO / ESTADO DE ORDEN DE COMPRA
    //    (misma lógica que NotificacionesAdminDao, pero devolviendo DTO
    //     para la vista de Logística)
    // ============================================================

    /**
     * Lista de notificaciones de cambio/estado de OC para Logística.
     * Usa el mismo flujo que Admin: se parte de obtenerOrdenCompra(null, null)
     * y se filtran sólo los estados relevantes.
     */
    public List<NotificacionLogisDTO> getNotificacionesCambioEstadoOC() {
        List<NotificacionLogisDTO> lista = new ArrayList<>();

        // Reutilizamos el mismo método del DAO de órdenes
        ArrayList<OrdenCompra> ordenes = ordenCompraDao.obtenerOrdenCompra(null, null);

        if (ordenes == null || ordenes.isEmpty()) {
            return lista;
        }

        for (OrdenCompra oc : ordenes) {
            String estadoNorm = normalizar(oc.getEstado());
            if (estadoNorm.isEmpty()) continue;

            // Mismo filtro que en NotificacionesAdminDao.listarOrdenesParaAdmin()
            boolean estadoValido;
            switch (estadoNorm) {
                case "enviada":
                case "enviado":
                case "recibido":
                case "recibida":
                case "en transito":
                case "en tránsito":
                case "registrado":
                case "registrada":
                case "completado":
                case "completada":
                    estadoValido = true;
                    break;
                default:
                    estadoValido = false;
            }
            if (!estadoValido) continue;

            // Título y mensaje iguales a la lógica de admin
            int idOrden = oc.getCodigoOrdenCompra();
            String titulo  = "OC #" + idOrden;
            String mensaje = getMensajeEstadoActual(oc);  // misma lógica que en Admin

            // Fecha relevante: reaprovechamos fechaLlegada si existe, si no hoy
            LocalDate fecha;
            if (oc.getFechaLlegada() != null) {
                // asumiendo que es LocalDate o se puede mapear a LocalDate
                fecha = oc.getFechaLlegada();
            } else {
                fecha = LocalDate.now();
            }

            // Para logística, la zona puede no aplicar; dejamos null
            lista.add(new NotificacionLogisDTO(
                    NotificacionTipo.CAMBIO_ESTADO_OC,
                    titulo,
                    mensaje,
                    fecha,
                    null
            ));
        }

        return lista;
    }

    /**
     * Copiado de NotificacionesAdminDao.getMensajeEstadoActual(…)
     * para mantener exactamente la misma redacción de las notis.
     */
    public String getMensajeEstadoActual(OrdenCompra oc) {
        if (oc == null || oc.getEstado() == null) {
            return "Orden sin estado.";
        }

        String estadoNorm = normalizar(oc.getEstado());
        int idOrden = oc.getCodigoOrdenCompra();
        String base = "OC #" + idOrden + " está en estado: \"" + oc.getEstado() + "\"";

        switch (estadoNorm) {
            case "enviada":
            case "enviado":
                return "📤 " + base;
            case "recibido":
            case "recibida":
                return "📥 " + base;
            case "en transito":
            case "en tránsito":
                return "🚚 " + base;
            case "registrado":
            case "registrada":
                return "🗂️ " + base;
            case "completado":
            case "completada":
                return "✅ " + base;
            default:
                return "ℹ️ " + base;
        }
    }

    // ---- helpers ----
    private String normalizar(String s) {
        if (s == null) return "";
        s = s.trim().toLowerCase();
        // unifica “tránsito”/“transito”
        s = s.replace("tránsito", "transito");
        return s;
    }
}
