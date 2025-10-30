package com.example.telitobodeguero.daos;

import com.example.telitobodeguero.beans.*;

import java.sql.*;
import java.util.ArrayList;

public class IncidenciaDao extends BaseDao{

    public ArrayList<Incidencia> obtenerIncidencias(int idZona){
        ArrayList<Incidencia> listaInc = new ArrayList<>();

        String sql = """   
                SELECT
                    i.idIncidencia AS 'ID',
                    p.sku AS 'SKU',
                    p.nombre AS 'NOMBRE PRODUCTO',
                    i.tipo AS 'TIPO',
                    i.cantidad AS 'CANTIDAD',
                    b.codigo AS 'BLOQUE',
                    z.nombre AS 'ZONA',
                    i.descripcion AS 'DESCRIPCION',
                    i.estado AS 'ESTADO',
                    l.idLote AS 'ID_LOTE',
                    b.idBloque 
                FROM incidencia i
                JOIN lote l
                    ON i.Lote_idLote = l.idLote
                JOIN producto p
                    ON l.Producto_idProducto = p.idProducto
                LEFT JOIN (
                    SELECT DISTINCT
                        Lote_idLote,
                        Bloque_id,
                        Zonas_idZonas
                    FROM movimiento
                ) AS m
                    ON i.Lote_idLote = m.Lote_idLote
                LEFT JOIN bloque b
                    ON m.Bloque_id = b.idBloque
                LEFT JOIN zonas z
                    ON m.Zonas_idZonas = z.idZonas
                WHERE z.idZonas = ?;   -- ejemplo: zona norte
                               
                    """;

        try (Connection conn = this.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)){
            pstmt.setInt(1, idZona);

            try (ResultSet rs = pstmt.executeQuery()){
                while (rs.next()) {
                    Incidencia i = new Incidencia();
                    Producto p = new Producto();
                    Zonas z = new Zonas();
                    String idIncStr = rs.getString(1);
                    i.setIdIncidencia(Integer.parseInt(idIncStr));
                    p.setSku(rs.getString(2));
                    p.setNombre(rs.getString(3));
                    z.setIdZonas(idZona);
                    i.setZona(z);
                    i.setProducto(p);

                    String idLoteStr = rs.getString(10);
                    i.setLote_idLote(Integer.parseInt(idLoteStr));

                    i.setTipoIncidencia(rs.getString(4));
                    i.setCantidad(rs.getInt(5));
                    i.setUbicacionTemporal(rs.getString(6));
                    i.setDescripcion(rs.getString(8));
                    i.setEstado(rs.getString(9));
                    i.setIdUbicacion(Integer.parseInt(rs.getString(11)));
                    listaInc.add(i);

                }
            }

        }catch (SQLException e){
            e.printStackTrace();
        }

        return listaInc;
    }


    public boolean mantener(String estado, int idIncidencia){
        //sentencia

        String sqlUpdate = "UPDATE Incidencia SET estado =  ? WHERE idIncidencia = ?";

        boolean exitoMantener = false;

        Connection conn = null;

        try{
            conn = this.getConnection();
            conn.setAutoCommit(false);

            //para editar estado
            try(PreparedStatement ps1 = conn.prepareStatement(sqlUpdate)){
                ps1.setString(1, estado);
                ps1.setInt(2, idIncidencia);
                ps1.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
                throw e;
            }
            conn.commit();
            exitoMantener=true;


        }catch(SQLException e){
            System.err.println("Error en la Transacción. Mensaje: " + e.getMessage());
            e.printStackTrace();

            if (conn != null) {
                try {
                    conn.rollback();
                    System.out.println("Transacción deshecha (rollback) exitosamente.");
                } catch (SQLException ex) {
                    System.err.println("Error al intentar deshacer la transacción: " + ex.getMessage());
                }
            }

        }finally{
            if (conn != null) {
                try {
                    conn.setAutoCommit(true); // Restaurar el comportamiento de auto-commit
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

        }
        return exitoMantener;
    }
    //-----------para QUITAR-----------------
    public boolean marcarQuitada(int idInc) {
        String sql = "UPDATE incidencia SET estado='QUITADA' WHERE idIncidencia=?";
        try (Connection cn = getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, idInc);
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            e.printStackTrace(); return false;
        }
    }

    public Integer obtenerStockFila(int loteId, int bloqueId, int zonaId) {
        String sql = """
        SELECT COALESCE(SUM(
                 CASE WHEN m.tipo='IN' THEN m.cantidad
                      WHEN m.tipo='OUT' THEN -m.cantidad
                      ELSE 0 END
               ),0) AS stockFila
        FROM movimiento m
        WHERE m.Lote_idLote   = ?
          AND m.Bloque_id     = ?
          AND m.Zonas_idZonas = ?
    """;
        try (Connection cn = this.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, loteId);
            ps.setInt(2, bloqueId);
            ps.setInt(3, zonaId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt("stockFila");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0; // por seguridad
    }








}
