package com.example.telitobodeguero.daos;

import com.example.telitobodeguero.dtos.NotificacionAlmDTO;
import com.example.telitobodeguero.dtos.NotificacionTipo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;

public class NotiAlmDao extends BaseDao{
    //metodo para las oc en transito que estan por llegar (proximso 2 dias)
    public ArrayList<NotificacionAlmDTO> getNotificacionesOrdenesLlegada(int idZona){
        ArrayList<NotificacionAlmDTO> lista = new ArrayList<>();
        //ArrayList<NotificacionAlmDTO> lista = null;
        String sql = """
                select oc.idOrdenCompra, oc.fecha_llegada, z.nombre
                from ordencompra oc
                join zonas z on oc.Zonas_idZonas = z.idZonas
                where oc.estado = 'EN TRANSITO'
                and oc.fecha_llegada between curdate() and date_add(curdate(), interval 2 day)
                and oc.Zonas_idZonas = ?;
                """;

        try(Connection conn = this.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setInt(1, idZona);


            try(ResultSet rs = ps.executeQuery()){
                while(rs.next()){
                    String titulo = "Orden por llegar: #"+ rs.getInt(1);
                    LocalDate fecha = rs.getDate(2).toLocalDate();
                    String mensaje = "La orden de compra #"+rs.getInt(1)+
                            " está programada para llegar el "+fecha+".";
                    String zona = rs.getString(3);

                    lista.add(new NotificacionAlmDTO(NotificacionTipo.ORDEN_LLEGADA, titulo, mensaje, fecha, zona));


                }
            }


        }catch(SQLException e){
            e.printStackTrace();
            return null;
        }
        return lista;
    }


    // para las órdenes retrasadas
    public ArrayList<NotificacionAlmDTO> getNotificacionesOrdenesRetrasadas(int idZona){
        ArrayList<NotificacionAlmDTO> lista = new ArrayList<>();
        String sql = "SELECT oc.idOrdenCompra, oc.fecha_llegada, z.nombre AS nombreZona " +
                "FROM ordencompra oc " +
                "JOIN zonas z ON oc.Zonas_idZonas = z.idZonas " +
                "WHERE oc.estado = 'EN TRANSITO' " + // Ajusta estados finales
                "AND oc.fecha_llegada < CURDATE() " +
                "AND oc.Zonas_idZonas = ?";

        try(Connection conn  = this.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setInt(1, idZona);

            try(ResultSet rs = ps.executeQuery()){
                while(rs.next()){
                    String titulo = "Orden Retrasada: #" + rs.getInt("idOrdenCompra");
                    LocalDate fecha = rs.getDate("fecha_llegada").toLocalDate();
                    String mensaje = "La orden #" + rs.getInt("idOrdenCompra") +
                            " (EN TRANSITO) tenía fecha de llegada " + fecha + " y aún no se registra como recibida.";
                    String zona = rs.getString("nombreZona");

                    lista.add(new NotificacionAlmDTO(NotificacionTipo.ORDEN_RETRASO, titulo, mensaje, fecha, zona));
                }
            }

        }catch (SQLException e){
            e.printStackTrace();
        }
        return lista;
    }

    //alerta por capacidad crítica de bloques
    public ArrayList<NotificacionAlmDTO> getNotificacionesBloquesCriticos(int idZona){
        ArrayList<NotificacionAlmDTO> lista = new ArrayList<>();

        String sql = "SELECT " +
                "    b.codigo, z.nombre AS nombreZona, " +
                "    (COALESCE(SUM(CASE WHEN m.tipo = 'IN' THEN m.cantidad ELSE -m.cantidad END), 0) / b.capacidad) * 100 AS porcentaje_uso " +
                "FROM bloque b " +
                "JOIN zonas z ON b.Zonas_idZonas = z.idZonas " +
                "LEFT JOIN movimiento m ON b.idBloque = m.Bloque_id " +
                "WHERE b.activo = 1 AND b.Zonas_idZonas = ? " +
                "GROUP BY b.idBloque, b.codigo, z.nombre, b.capacidad " +
                "HAVING porcentaje_uso >= 90.0";

        try(Connection conn = this.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setInt(1, idZona);

            try(ResultSet rs = ps.executeQuery()){
                while(rs.next()){
                    double porcentaje = rs.getDouble("porcentaje_uso");
                    String titulo = "Capacidad Crítica: Bloque " + rs.getString("codigo");
                    String mensaje = "El bloque " + rs.getString("codigo") +
                            " ha alcanzado el " + String.format("%.0f", porcentaje) + "% de su capacidad.";
                    String zona = rs.getString("nombreZona");

                    lista.add(new NotificacionAlmDTO(NotificacionTipo.BLOQUE_CRITICO, titulo, mensaje, LocalDate.now(), zona));
                }
            }

        }catch(SQLException e){
            e.printStackTrace();
            return null;
        }
        return lista;
    }


    public ArrayList<NotificacionAlmDTO> getNotificacionesIncidencias(int idZona){
        ArrayList<NotificacionAlmDTO> lista = new ArrayList<>();
        String sql = "SELECT i.idIncidencia, i.tipo, i.descripcion, z.nombre AS nombreZona " +
                "FROM incidencia i " +
                "JOIN lote l ON i.Lote_idLote = l.idLote " +
                "JOIN usuarios u ON l.Usuarios_idUsuarios = u.idUsuarios " +
                "JOIN distritos d ON u.Distritos_idDistritos = d.idDistritos " +
                "JOIN zonas z ON d.Zonas_idZonas = z.idZonas " +
                "WHERE i.estado = 'REGISTRADA' AND d.Zonas_idZonas = ?"; // Asumimos 'REGISTRADA' es "nueva"

        try (Connection conn = this.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idZona);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String titulo = "Nueva Incidencia: " + rs.getString("tipo");
                    String mensaje = "ID #" + rs.getInt("idIncidencia") + ": " + rs.getString("descripcion");
                    String zona = rs.getString("nombreZona");

                    lista.add(new NotificacionAlmDTO(NotificacionTipo.INCIDENCIA_NUEVA, titulo, mensaje, LocalDate.now(), zona));
                }
            }
        }catch (SQLException e){
            e.printStackTrace();
            return null;
        }
        return lista;

    }

}

