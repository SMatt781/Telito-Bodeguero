package com.example.telitobodeguero.daos;
import com.example.telitobodeguero.beans.Incidencia;
import com.example.telitobodeguero.beans.Movimiento;

import java.sql.*;

public class MovimientoDao {
    public boolean registrarEntrada(Movimiento movimiento, String sku, String fechaVencimiento, int idUsuarioSesion){
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        String url = "jdbc:mysql://localhost:3306/bodega-telito";
        String user = "root";
        String pass = "12345678";


        boolean exito = false;

        String sqlActLote = "UPDATE lote SET cantidad = cantidad + ? WHERE idLote = ?"; // CRÍTICO: Asegúrate de que esta línea esté definida.
        String sqlMov = "INSERT INTO Movimiento (tipo, cantidad, fecha, Lote_idLote, Zonas_idZonas) VALUES (?,?,?,?,?)";



        try (Connection conn = DriverManager.getConnection(url, user, pass);){

            conn.setAutoCommit(false);

            int idLoteFinal = movimiento.getLote_idLote();

            if(idLoteFinal <= 0) {
                int idProducto = this.obtenerIdProductoPorSku(conn, sku);

                //para insertar nuevo lote
                String sqlInsertLote = "INSERT INTO lote (fechaVencimiento, Producto_idProducto, Usuarios_idUsuarios, cantidad) VALUES (?, ?, ?, ?)";

                try (PreparedStatement psLote = conn.prepareStatement(sqlInsertLote, Statement.RETURN_GENERATED_KEYS)) {
                    psLote.setString(1, fechaVencimiento);
                    psLote.setInt(2, idProducto);
                    psLote.setInt(3, idUsuarioSesion);
                    psLote.setInt(4, movimiento.getCantidad());
                    psLote.executeUpdate();

                    try (ResultSet rs = psLote.getGeneratedKeys()) {
                        if (rs.next()) {
                            idLoteFinal = rs.getInt(1);
                        } else {
                            throw new SQLException("Error al obtener ID de Lote generado.");
                        }
                    }

                }
            }else{

                try(PreparedStatement pstmtLote = conn.prepareStatement(sqlActLote)){
                    pstmtLote.setInt(1, movimiento.getCantidad());
                    pstmtLote.setInt(2, idLoteFinal);
                    pstmtLote.executeUpdate();
                }
            }

            //String sqlMov = "INSERT INTO Movimiento (tipo, cantidad, fecha, Lote_idLote, Zonas_idZonas) VALUES (?,?,?,?,?)";
            try (PreparedStatement pstmtMov = conn.prepareStatement(sqlMov)){
                pstmtMov.setString(1, "IN");
                pstmtMov.setInt(2, movimiento.getCantidad());
                //pstmtMov.setString(3, movimiento.getFecha());
                pstmtMov.setInt(4, idLoteFinal);
                pstmtMov.setInt(5, movimiento.getZonas_idZonas());
                pstmtMov.executeUpdate();
            }
            conn.commit();
            exito = true;


        }catch(SQLException e){
            e.printStackTrace();
            exito = false;
        }

        return exito;

    }

    public int obtenerIdProductoPorSku(Connection conn, String sku) throws SQLException {
        // Nota: La BD URL de ProductoDao es 'bodega-telito', aquí usamos 'hr'.
        // Asegúrate de que tu URL en MovimientoDao sea la correcta ('bodega-telito' parece la adecuada)
        String sql = "SELECT idProducto FROM producto WHERE SKU = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, sku);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("idProducto");
                }
            }
        }
        throw new SQLException("Error: El SKU proporcionado (" + sku + ") no fue encontrado.");
    }

    public boolean registrarSalida(Movimiento movimiento){

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        String url = "jdbc:mysql://localhost:3306/bodega-telito";
        String user = "root";
        String pass = "12345678";


        boolean exito = false;

        try(Connection conn = DriverManager.getConnection(url,user,pass);){
            //para verificar el stock actual del lote
            String sqlConsult = "SELECT cantidad FROM lote WHERE idLote = ?";
            String sqlSalida = "INSERT INTO Movimiento (tipo, cantidad, fecha, Lote_idLote, Zonas_idZonas) VALUES (?,?,?,?,?)";
            String sqlActLoteOut = "UPDATE lote SET cantidad = cantidad - ? WHERE idLote = ?";


            conn.setAutoCommit(false);
            int loteId = movimiento.getLote_idLote();
            int cantidadSalida = movimiento.getCantidad();

            //primero se verifica stock suficiente
            int stockActual = 0;
            try (PreparedStatement psCheck = conn.prepareStatement(sqlConsult)){
                psCheck.setInt(1, loteId);
                try (ResultSet rsCheck = psCheck.executeQuery()) {
                    if (rsCheck.next()) {
                        stockActual = rsCheck.getInt("cantidad");
                    }else{
                        //si el lote no existe en la base de datos
                        System.out.println("Error: El Lote con ID " + loteId + " no existe.");
                        return false;
                    }
                }
            }

            if(stockActual<cantidadSalida){
                //no hay suficiente stock
                System.out.println("Error: Stock insuficiente. Solo hay " + stockActual + " unidades disponibles para el lote " + loteId);
                return false;
            }

            //se registra el mov
            try (PreparedStatement pstmtSalida = conn.prepareStatement(sqlSalida)) {
                pstmtSalida.setString(1, "OUT");
                pstmtSalida.setInt(2, cantidadSalida);
                //pstmtSalida.setString(3, movimiento.getFecha());
                pstmtSalida.setInt(4, loteId);
                pstmtSalida.setInt(5, movimiento.getZonas_idZonas());
                pstmtSalida.executeUpdate();
            }

            //despues se decrementa el lote
            try(PreparedStatement pstmtLote = conn.prepareStatement(sqlActLoteOut)){
                pstmtLote.setInt(1, cantidadSalida);
                pstmtLote.setInt(2, loteId);
                pstmtLote.executeUpdate();
            }

            conn.commit();
            exito = true;

        }catch (SQLException e){
            e.printStackTrace();
            exito = false;
        }

        return exito;
    }


    public boolean registrarIncidencia(Incidencia incidencia){
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        String url = "jdbc:mysql://localhost:3306/bodega-telito";
        String user = "root";
        String pass = "12345678";



        if (incidencia == null || incidencia.getLote_idLote() <= 0 ||
                incidencia.getTipoIncidencia() == null || incidencia.getTipoIncidencia().isEmpty()) {
            return false;
        }






        try (Connection conn = DriverManager.getConnection(url,user,pass) ;){

            //String sqlCheckLote = "SELECT cantidad FROM lote WHERE idLote = ?";
            String sqlInsertIncidencia = "INSERT INTO Incidencia (tipo, cantidad, descripcion, Lote_idLote) VALUES (?,?,?,?)";
            //String sqlUpdtLoteInc = "UPDATE lote SET cantidad = cantidad - ? WHERE idLote = ?";





            //registramos la incidencia
            try (PreparedStatement pstmtInsertIncidencia = conn.prepareStatement(sqlInsertIncidencia)){
                pstmtInsertIncidencia.setString(1,incidencia.getTipoIncidencia());
                pstmtInsertIncidencia.setInt(2,incidencia.getCantidad());
                pstmtInsertIncidencia.setString(3,incidencia.getDescripcion());
                pstmtInsertIncidencia.setInt(4,incidencia.getLote_idLote());
                pstmtInsertIncidencia.executeUpdate();

                int inserted = pstmtInsertIncidencia.executeUpdate();

                return inserted ==1;
            }



        }catch(SQLException e){
            e.printStackTrace();
            return false;
        }


    }



}



