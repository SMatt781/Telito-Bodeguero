package com.example.telitobodeguero.daos;
import com.example.telitobodeguero.beans.*;

import java.sql.*;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

public class MovimientoDao extends BaseDao{


    public ArrayList<Movimiento> obtenerListaMovimientos(int idZona){
        ArrayList<Movimiento> listaMovimientos = new ArrayList<>();

        String sql = "SELECT m.fecha as FECHA, "+
                "m.tipo as TIPO,  "+
                "p.sku as SKU, " +
                "m.cantidad as CANTIDAD, "+
                "p.nombre as PRODUCTO " +

                "FROM movimiento m " +
                "JOIN lote l ON l.idLote = m.Lote_idLote " +
                "JOIN producto p ON p.idProducto = l.Producto_idProducto " +
                "JOIN zonas z ON z.idZonas = m.Zonas_idZonas " +
                "WHERE m.fecha BETWEEN CURRENT_DATE - INTERVAL 15 DAY AND CURRENT_DATE " +
                "AND z.idZonas = ? " +
                "ORDER BY m.fecha DESC";

        try (Connection conn = this.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)){
            pstmt.setInt(1, idZona);

            try (ResultSet rs = pstmt.executeQuery()){
                while (rs.next()) {
                    Movimiento mov = new Movimiento();
                    Producto producto = new Producto();
                    Zonas  zona = new Zonas();
                    Lote lote = new Lote();

//                    Instant instant = (rs.getDate(1)).toInstant();
//                    ZoneId zonaAyuda = ZoneId.systemDefault();
//                    LocalDate fecha = instant.atZone(zonaAyuda).toLocalDate();
                    java.sql.Date d = rs.getDate(1);
                    mov.setFecha(d != null ? d.toLocalDate() : null);

                    mov.setTipoMovimiento(rs.getString(2));


                    producto.setSku(rs.getString(3));
                    //producto.setIdProducto(rs.getInt("p.idProducto"));

                    mov.setCantidad(rs.getInt(4));

                    producto.setNombre(rs.getString(5));

                    zona.setIdZonas(idZona);
                    mov.setZona(zona);

                    //lote.setIdLote(rs.getInt("l.idLote"));

                    lote.setProducto(producto);
                    mov.setLote(lote);

                    listaMovimientos.add(mov);
                }
            }

        }catch (SQLException e){
            e.printStackTrace();
        }

        return listaMovimientos;
    }

    public int getStockTotal(int idZona){
        int stockTotal = 0;

        String sql ="SELECT coalesce(sum(case when m.tipo = 'IN' then m.cantidad else -m.cantidad end),0) as 'Stock total' "+
                "FROM movimiento m INNER JOIN lote l on m.Lote_idLote = l.idLote INNER JOIN zonas z on z.idZonas = m.Zonas_idZonas WHERE z.idZonas = ?";

        try (Connection conn = this.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)){
            pstmt.setInt(1, idZona);

            try (ResultSet rs = pstmt.executeQuery()){
                while (rs.next()) {
                    stockTotal =  rs.getInt(1);
                }
            }

        }catch (SQLException e){
            e.printStackTrace();
        }
        return stockTotal;
    }

    public int getInToday(int idZona){
        int inToday = 0;

        String sql = "SELECT COUNT(m.idRegistro)  " +
                "FROM movimiento m " +
                "JOIN zonas z ON z.idZonas = m.Zonas_idZonas " +
                "WHERE month(current_date())=month(m.fecha) " +
                " AND m.tipo = 'IN' " +
                "    AND z.idZonas=?";
        try (Connection conn = this.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)){
            pstmt.setInt(1, idZona);

            try (ResultSet rs = pstmt.executeQuery()){
                while (rs.next()) {
                    inToday =  rs.getInt(1);
                }
            }

        }catch (SQLException e){
            e.printStackTrace();
        }
        return inToday;
    }

    public int getOutToday(int idZona){
        int outToday = 0;

        String sql = "SELECT COUNT(m.idRegistro) " +
                "FROM movimiento m " +
                "JOIN zonas z ON z.idZonas = m.Zonas_idZonas " +
                "WHERE month(current_date())=month(m.fecha) " +
                " AND m.tipo = 'OUT' " +
                "    AND z.idZonas = ?";
        try (Connection conn = this.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)){
            pstmt.setInt(1, idZona);

            try (ResultSet rs = pstmt.executeQuery()){
                while (rs.next()) {
                    outToday =  rs.getInt(1);
                }
            }

        }catch (SQLException e){
            e.printStackTrace();
        }
        return outToday;
    }

    public int getMin(int idZona){
        int min = 0;

        String sql = "SELECT MIN(t.stock_calculado) as 'Bajo mínimo' " +
                "FROM (SELECT p.idProducto, " +
                "        COALESCE(SUM(CASE WHEN m.tipo = 'IN' THEN m.cantidad ELSE -m.cantidad END), 0) AS stock_calculado " +
                " FROM producto p " +
                "    LEFT JOIN lote l ON p.idProducto = l.Producto_idProducto " +
                "    LEFT JOIN movimiento m ON l.idLote = m.Lote_idLote " +
                "    LEFT JOIN zonas z ON z.idZonas = m.Zonas_idZonas " +
                "    WHERE z.idZonas = ? " +
                "    GROUP BY p.idProducto) t " +
                "    WHERE t.stock_calculado <= 15";

        try (Connection conn = this.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)){
            pstmt.setInt(1, idZona);

            try (ResultSet rs = pstmt.executeQuery()){
                while (rs.next()) {
                    min =  rs.getInt(1);
                }
            }

        }catch (SQLException e){
            e.printStackTrace();
        }
        return min;
    }


    public boolean registrarEntrada(Movimiento movimiento){

        //sentencias
        String sqlInsert = "INSERT INTO `Movimiento` (`tipo`, `cantidad`, `fecha`, `Lote_idLote`, `Zonas_idZonas`) " +
                "VALUES (?,?,?,?,?)";
        String sqlUpdate = "UPDATE lote SET cantidad = cantidad + ? WHERE idLote = ?";

        boolean exitoIn = false;

        Connection conn = null;

        try{
            conn = this.getConnection();
            conn.setAutoCommit(false);

            //para insertar movimiento
            try(PreparedStatement ps1 = conn.prepareStatement(sqlInsert)){
                ps1.setString(1,movimiento.getTipoMovimiento());
                ps1.setInt(2,movimiento.getCantidad());
                ps1.setDate(3,Date.valueOf(movimiento.getFecha()));
                ps1.setInt(4,movimiento.getLote().getIdLote());
                ps1.setInt(5,movimiento.getZona().getIdZonas());
                ps1.executeUpdate();

            } catch (SQLException e) {
                e.printStackTrace();
            }

            try(PreparedStatement ps2 = conn.prepareStatement(sqlUpdate)){
                ps2.setInt(1,movimiento.getCantidad());
                ps2.setInt(2,movimiento.getLote().getIdLote());
                ps2.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            conn.commit();
            exitoIn=true;

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
        return exitoIn;
    }

    public boolean registrarSalida(Movimiento movimiento){
        //sentencias
        String sqlInsert = "INSERT INTO `Movimiento` (`tipo`, `cantidad`, `fecha`, `Lote_idLote`, `Zonas_idZonas`) " +
                "VALUES (?,?,?,?,?)";
        String sqlUpdate = "UPDATE lote SET cantidad = cantidad - ? WHERE idLote = ?";

        boolean exitoOut = false;

        Connection conn = null;

        try{
            conn = this.getConnection();
            conn.setAutoCommit(false);

            //para insertar movimiento
            try(PreparedStatement ps1 = conn.prepareStatement(sqlInsert)){
                ps1.setString(1,movimiento.getTipoMovimiento());
                ps1.setInt(2,movimiento.getCantidad());
                ps1.setDate(3,Date.valueOf(movimiento.getFecha()));
                ps1.setInt(4,movimiento.getLote().getIdLote());
                ps1.setInt(5,movimiento.getZona().getIdZonas());
                ps1.executeUpdate();

            } catch (SQLException e) {
                e.printStackTrace();
            }

            try(PreparedStatement ps2 = conn.prepareStatement(sqlUpdate)){
                ps2.setInt(1,movimiento.getCantidad());
                ps2.setInt(2,movimiento.getLote().getIdLote());
                ps2.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            conn.commit();
            exitoOut=true;

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
        return exitoOut;
    }

    public boolean registrarIncidencia(Incidencia incidencia){


        String sql = "INSERT INTO Incidencia (tipo, cantidad, descripcion, Lote_idLote) " +
                "VALUES (?, ?, ?, ?,?)"; // Asegúrate de incluir la columna Zonas_idZonas si la estás usando en el bean

        boolean exito = false; // Bandera de éxito
        Connection conn = null;

        try{
            conn = this.getConnection();
            conn.setAutoCommit(false); // 1. Desactivar auto-commit

            // 2. Ejecutar la sentencia SQL
            try(PreparedStatement ps = conn.prepareStatement(sql)){
                ps.setString(1, incidencia.getTipoIncidencia());
                ps.setInt(2, incidencia.getCantidad());
                ps.setString(3, incidencia.getDescripcion());
                ps.setInt(4, incidencia.getLote_idLote());

                // Si tu tabla tiene Zonas_idZonas:
//                ps.setInt(5, incidencia.getZona().getIdZonas());

                int filasAfectadas = ps.executeUpdate(); // Ejecutar y obtener el resultado

                if (filasAfectadas > 0) {
                    // 3. Confirmar la transacción SOLO si la ejecución fue exitosa
                    conn.commit();
                    exito = true;
                } else {
                    // Si la ejecución no afectó filas, deshacer por seguridad
                    conn.rollback();
                }
            }
            // Eliminamos el catch interno para que cualquier SQLException salte al catch principal

        }catch(SQLException e){
            // 4. ROLLBACK: Deshacer los cambios si hubo un error SQL
            System.err.println("Error en la Transacción de Incidencia. Mensaje: " + e.getMessage());
            e.printStackTrace();
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }

        } finally {
            // 5. RESTAURAR y CERRAR
            if (conn != null) {
                try {
                    conn.setAutoCommit(true); // Restaurar auto-commit
                    conn.close();             // Cerrar la conexión
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return exito;

    }


    public static class FilaEntrada {
        public String sku;
        public int loteId;
        public LocalDate fecha;
        public int cantidad;
        public LocalDate fechaVenc; // opcional (no crea lote)
    }

    private final ProductoDao pdao = new ProductoDao();

    public int registrarEntradasMasivasSoloLotesExistentes(List<FilaEntrada> filas,
                                                           int zonaId, int usuarioId,
                                                           List<String> errores) throws SQLException {
        if (filas == null || filas.isEmpty()) return 0;

        String sqlIns = "INSERT INTO Movimiento (tipo, cantidad, fecha, Lote_idLote, Zonas_idZonas) VALUES ('IN', ?, ?, ?, ?)";
        String sqlUpd = "UPDATE Lote SET cantidad = cantidad + ? WHERE idLote = ?";

        int ok = 0;
        try (Connection conn = getConnection();
             PreparedStatement psIns = conn.prepareStatement(sqlIns);
             PreparedStatement psUpd = conn.prepareStatement(sqlUpd)) {

            conn.setAutoCommit(false);
            int i = 0;

            for (FilaEntrada f : filas) {
                i++;
                try {
                    Integer idProd = pdao.findProductoIdBySku(conn, f.sku);
                    if (idProd == null) { if (errores!=null) errores.add("Fila "+i+": SKU no existe"); continue; }
                    if (!pdao.lotePerteneceAProducto(conn, f.loteId, idProd)) {
                        if (errores!=null) errores.add("Fila "+i+": lote no pertenece al SKU");
                        continue;
                    }
                    if (f.cantidad <= 0 || f.fecha == null) {
                        if (errores!=null) errores.add("Fila "+i+": cantidad/fecha inválidas");
                        continue;
                    }

                    psIns.setInt(1, f.cantidad);
                    psIns.setDate(2, Date.valueOf(f.fecha));
                    psIns.setInt(3, f.loteId);
                    psIns.setInt(4, zonaId);
                    psIns.addBatch();

                    psUpd.setInt(1, f.cantidad);
                    psUpd.setInt(2, f.loteId);
                    psUpd.addBatch();

                    ok++;
                } catch (Exception ex) {
                    if (errores!=null) errores.add("Fila "+i+": " + ex.getMessage());
                }
            }

            psIns.executeBatch();
            psUpd.executeBatch();
            conn.commit();
            conn.setAutoCommit(true);
            return ok;
        }
    }







}



