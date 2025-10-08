package com.example.telitobodeguero.daos;

import com.example.telitobodeguero.beans.*;

import java.sql.*;
import java.util.ArrayList;

public class IncidenciaDao extends BaseDao{

    public ArrayList<Incidencia> obtenerIncidencias(int idZona){
        ArrayList<Incidencia> listaInc = new ArrayList<>();

        String sql = "SELECT i.idIncidencia as idInc, " +
                "p.sku AS SKU, " +
                "    p.nombre AS Nombre, " +
                "    i.tipo AS Tipo, " +
                "    i.cantidad AS Cantidad, " +
                "    i.Lote_idLote as Lote, "+
                "    i.descripcion AS Descripcion, " +
                "    i.estado AS Estado " +
                "FROM incidencia i " +
                "INNER JOIN lote l ON l.idLote = i.Lote_idLote " +
                "INNER JOIN producto p ON p.idProducto = l.Producto_idProducto " +
                "WHERE EXISTS ( " +
                "        SELECT 1 " +
                "        FROM movimiento m " +
                "        WHERE m.Lote_idLote = l.idLote  " +
                "        AND m.Zonas_idZonas = ? )";

        try (Connection conn = this.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)){
            pstmt.setInt(1, idZona);

            try (ResultSet rs = pstmt.executeQuery()){
                while (rs.next()) {
                    Incidencia i = new Incidencia();
                    Producto p = new Producto();
                    Zonas z = new Zonas();
                    p.setSku(rs.getString("SKU"));
                    p.setNombre(rs.getString("Nombre"));
                    z.setIdZonas(idZona);
                    i.setZona(z);
                    i.setProducto(p);
                    String IDincStr = rs.getString("idInc");
                    String idLoteStr = rs.getString("Lote");
                    i.setLote_idLote(Integer.parseInt(idLoteStr));
                    i.setIdIncidencia(Integer.parseInt(IDincStr));
                    i.setTipoIncidencia(rs.getString("Tipo"));
                    i.setCantidad(rs.getInt("Cantidad"));
                    i.setDescripcion(rs.getString("Descripcion"));
                    i.setEstado(rs.getString("Estado"));

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

    public boolean quitar(Movimiento mov, String estado, int idIncidencia){
        //sentencias
        String sqlInsert = "INSERT INTO `Movimiento` (`tipo`, `cantidad`, `fecha`, `Lote_idLote`, `Zonas_idZonas`) " +
                "VALUES (?,?,?,?,?)";
        String sqlUpdateLote = "UPDATE lote SET cantidad = cantidad - ? WHERE idLote = ?";
        String sqlUpdateState = "UPDATE Incidencia SET estado =  ? WHERE idIncidencia = ?";

        boolean exitoQuitar = false;

        Connection conn = null;

        try{
            conn = this.getConnection();
            conn.setAutoCommit(false);
            //para insertar movimiento
            try(PreparedStatement ps1 = conn.prepareStatement(sqlInsert)){
                ps1.setString(1,mov.getTipoMovimiento());
                ps1.setInt(2,mov.getCantidad());
                ps1.setDate(3,Date.valueOf(mov.getFecha()));
                ps1.setInt(4,mov.getLote().getIdLote());
                ps1.setInt(5,mov.getZona().getIdZonas());
                ps1.executeUpdate();

            } catch (SQLException e) {
                e.printStackTrace();
            }

            try(PreparedStatement ps2 = conn.prepareStatement(sqlUpdateLote)){
                ps2.setInt(1,mov.getCantidad());
                ps2.setInt(2,mov.getLote().getIdLote());
                ps2.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }

            //para editar estado
            try(PreparedStatement ps3 = conn.prepareStatement(sqlUpdateState)){
                ps3.setString(1, estado);
                ps3.setInt(2, idIncidencia);
                ps3.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
                throw e;
            }
            conn.commit();
            exitoQuitar=true;


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
        return exitoQuitar;
    }





}
