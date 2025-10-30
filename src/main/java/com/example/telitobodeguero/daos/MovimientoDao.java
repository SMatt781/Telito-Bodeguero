package com.example.telitobodeguero.daos;
import com.example.telitobodeguero.beans.*;

import java.sql.*;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class MovimientoDao extends BaseDao{


    public ArrayList<Movimiento> obtenerListaMovimientos(int idZona){
        ArrayList<Movimiento> listaMovimientos = new ArrayList<>();

        String sql = """
                    
                select m.fecha as "FECHA",
                    	m.tipo as "TIPO",
                        p.sku as "SKU",
                        m.cantidad as "CANTIDAD",
                        p.nombre as "NOMBRE"
                    from movimiento m
                    left join lote l on m.Lote_idLote=l.idLote
                    left join producto p on p.idProducto=l.Producto_idProducto
                    where m.fecha between date_sub(curdate(), interval 7 day) and curdate()
                    and m.Zonas_idZonas=?;
                    """;

        try (Connection conn = this.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)){
            pstmt.setInt(1, idZona);

            try (ResultSet rs = pstmt.executeQuery()){
                while (rs.next()) {
                    Movimiento mov = new Movimiento();
                    Producto producto = new Producto();
                    Zonas  zona = new Zonas();
                    Lote lote = new Lote();


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

//        String sql ="SELECT coalesce(sum(case when m.tipo = 'IN' then m.cantidad else -m.cantidad end),0) as 'Stock total' "+
//                "FROM movimiento m INNER JOIN lote l on m.Lote_idLote = l.idLote INNER JOIN zonas z on z.idZonas = m.Zonas_idZonas WHERE z.idZonas = ?";

        String sql = """
                    select
                     	SUM(CASE
                     			WHEN m.tipo = 'IN' THEN m.cantidad
                                 WHEN m.tipo = 'OUT' THEN -m.cantidad
                                 ELSE 0
                     		END) as "Stock Total"
                     from movimiento m
                     where m.Zonas_idZonas=?;
                     """;
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

        String sql = """
                    
                select count(m.tipo) as "IN"
                    from movimiento m
                    where m.Zonas_idZonas = ? -- filtro
                    and m.tipo = 'IN'
                    and m.fecha = current_date();
                    """;
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

        String sql = """
                    
                select count(m.tipo) as "OUT"
                    from movimiento m
                    where m.Zonas_idZonas = ? -- filtro
                    and m.tipo = 'OUT'
                    and m.fecha = current_date();
                    """;
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

                    if(min<0){
                        min = 0;
                    }
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

    //para el registro BACKUP
    public int obtenerEspacioLibre(int bloqueId) {
        int espacioLibre = 0;
        String sql = """
        SELECT b.capacidad - COALESCE(SUM(
          CASE WHEN m.tipo='IN' THEN m.cantidad
               WHEN m.tipo='OUT' THEN -m.cantidad
               ELSE 0 END
        ),0) AS espacioLibre
        FROM bloque b
        LEFT JOIN movimiento m ON m.Bloque_id = b.idBloque
        WHERE b.idBloque = ?
        GROUP BY b.capacidad
    """;

        try (Connection conn = this.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, bloqueId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    espacioLibre = rs.getInt("espacioLibre");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return espacioLibre;
    }




    public String registrarEntradaBackup(Movimiento mov, int bloqueId) {
        StringBuilder errores = new StringBuilder();

        if (mov == null) return "Movimiento nulo.";

        if ((mov.getCantidad()) < 0) {
            errores.append("• La cantidad debe ser mayor que 0.<br>");
        }

        if ((mov.getCantidad()) == 0) {
            errores.append("• Debe ingresar una cantidad.<br>");
        }
        if (mov.getFecha() == null) {
            errores.append("• La fecha no puede estar vacía.<br>");
        }
        if (mov.getLote() == null || mov.getLote().getIdLote() <= 0) {
            errores.append("• Lote inválido.<br>");
        }
        if (mov.getZona() == null || mov.getZona().getIdZonas() <= 0) {
            errores.append("• Zona inválida.<br>");
        }

        if(mov.getBloque().getCodigo() == null){
            errores.append("• Debe ingresar una ubicación.<br>");
        }

        // Si hay errores, se devuelven todos concatenados
        if (errores.length() > 0) {
            return errores.toString();
        }

        try (Connection cn = this.getConnection()) {
            int espacioLibre = obtenerEspacioLibre(bloqueId);

            if (mov.getCantidad() > espacioLibre) {
                return "No puede ingresar más de lo permitido. Espacio libre: " + espacioLibre + " unidades.";
            }

            String sql = """
            INSERT INTO movimiento (tipo, cantidad, fecha, Lote_idLote, Zonas_idZonas, Bloque_id)
            VALUES ('IN', ?, ?, ?, ?, ?)
        """;
            try (PreparedStatement ps = cn.prepareStatement(sql)) {
                ps.setInt(1, mov.getCantidad());
                ps.setDate(2, java.sql.Date.valueOf(mov.getFecha()));
                ps.setInt(3, mov.getLote().getIdLote());
                ps.setInt(4, mov.getZona().getIdZonas());
                ps.setInt(5, bloqueId);
                ps.executeUpdate();
            }

            return "ok";
        } catch (SQLException e) {
            e.printStackTrace();
            return "Error en base de datos: " + e.getMessage();
        }
    }
//------------ FIN REGISTRAR ENTRADA-------------

    //-----------ENTRADA DINAMICA PARA OC
    public String registrarEntradaDinamica(Movimiento mov, int idOrdenCompra) {
        // 1. Inserción del Movimiento
        String sqlMov = """
            INSERT INTO movimiento (
                fecha, tipo, cantidad, Lote_idLote, Bloque_id, Zonas_idZonas
            ) VALUES (?, 'IN', ?, ?, ?, ?);
            """;
        // 2. Actualización de la Orden de Compra
        // Se sugiere el estado 'INGRESADO' para distinguirla de 'COMPLETADO'
        String sqlOc = "UPDATE ordencompra SET estado = 'REGISTRADA' WHERE idOrdenCompra = ?";

        // 💡 CONEXIÓN Y TRANSACCIÓN
        try (Connection conn = this.getConnection()) {
            // Iniciar Transacción
            conn.setAutoCommit(false);

            // --- 1. Registrar Movimiento ---
            try (PreparedStatement pstmtMov = conn.prepareStatement(sqlMov)) {

                // Formatear LocalDate a String (MySQL lo acepta)
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

                pstmtMov.setString(1, mov.getFecha().format(formatter)); // fecha
                pstmtMov.setInt(2, mov.getCantidad());                  // cantidad
                pstmtMov.setInt(3, mov.getLote().getIdLote());          // Lote_idLote
                pstmtMov.setInt(4, mov.getBloque().getIdBloque());      // Bloque_id (asignado dinámicamente)
                pstmtMov.setInt(5, mov.getZona().getIdZonas());         // Zonas_idZonas

                int rowsAffectedMov = pstmtMov.executeUpdate();
                if (rowsAffectedMov != 1) {
                    throw new SQLException("Error al insertar el movimiento.");
                }
            }

            // --- 2. Actualizar Orden de Compra ---
            try (PreparedStatement pstmtOc = conn.prepareStatement(sqlOc)) {
                pstmtOc.setInt(1, idOrdenCompra);
                int rowsAffectedOc = pstmtOc.executeUpdate();
                if (rowsAffectedOc != 1) {
                    throw new SQLException("Error al actualizar el estado de la Orden de Compra.");
                }
            }

            // Finalizar Transacción
            conn.commit();
            return "ok";

        } catch (SQLException e) {
            // Revertir Transacción en caso de error
            e.printStackTrace();
            // Aquí deberías tener un bloque catch que maneje la reversión (rollback)
            // Ya que no tengo el código de tu BaseDao, asumo que esto se manejaría en un bloque finally
            // o con la implementación adecuada de try-with-resources que maneje la conexión.

            // Devuelve el mensaje de error de la base de datos
            return "Error SQL: " + e.getMessage();
        }
    }


    //-------REGISTRAR SALIDA----------
    public String registrarSalidaConBloque(Movimiento mov, int bloqueId, Integer stockFila) {
        StringBuilder errores = new StringBuilder();

        if (mov == null) return "Movimiento nulo.";

        if ((mov.getCantidad()) < 0) {
            errores.append("• La cantidad debe ser mayor que 0.<br>");
        }

        if ((mov.getCantidad()) == 0) {
            errores.append("• Debe ingresar una cantidad.<br>");
        }
        if (mov.getFecha() == null) {
            errores.append("• La fecha no puede estar vacía.<br>");
        }
        if (mov.getLote() == null || mov.getLote().getIdLote() <= 0) {
            errores.append("• Lote inválido.<br>");
        }
        if (mov.getZona() == null || mov.getZona().getIdZonas() <= 0) {
            errores.append("• Zona inválida.<br>");
        }

        if(mov.getBloque().getCodigo() == null){
            errores.append("• Debe ingresar una ubicación.<br>");
        }

        // Si hay errores, se devuelven todos concatenados
        if (errores.length() > 0) {
            return errores.toString();
        }

        // Validación contra el "stock de la fila" que viene de la tabla (si lo envías)
        if (stockFila != null && mov.getCantidad() > stockFila) {
            errores.append("• No puede retirar más de lo disponible en esta fila (")
                    .append(stockFila).append(" unidades).<br>");
        }

        if (errores.length() > 0) return errores.toString();

        try (Connection cn = this.getConnection()) {
            cn.setAutoCommit(false);

            // 1) Verificar bloque (existe / activo) y pertenencia a la zona del usuario
            Integer zonaDelBloque = null;
            String sqlBloque = """
            SELECT Zonas_idZonas
            FROM bloque
            WHERE idBloque = ? AND activo = 1
            FOR UPDATE
        """;
            try (PreparedStatement ps = cn.prepareStatement(sqlBloque)) {
                ps.setInt(1, bloqueId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        zonaDelBloque = rs.getInt("Zonas_idZonas");
                    } else {
                        errores.append("• El bloque no existe o está inactivo.<br>");
                    }
                }
            }
            if (errores.length() == 0 && !zonaDelBloque.equals(mov.getZona().getIdZonas())) {
                errores.append("• El bloque no pertenece a la zona del usuario.<br>");
            }
            if (errores.length() > 0) {
                cn.rollback();
                return errores.toString();
            }

            // 2) Validar stock real en el bloque (evitar saldos negativos físicos)
            int stockBloque = obtenerStockBloque(cn, bloqueId); // FOR UPDATE
            if (mov.getCantidad() > stockBloque) {
                cn.rollback();
                return "• No puede retirar más de lo que hay en el bloque (" + stockBloque + " unidades).<br>";
            }

            // 3) Insertar movimiento OUT
            String sqlIns = """
            INSERT INTO movimiento (tipo, cantidad, fecha, Lote_idLote, Zonas_idZonas, Bloque_id)
            VALUES ('OUT', ?, ?, ?, ?, ?)
        """;
            try (PreparedStatement ps = cn.prepareStatement(sqlIns)) {
                ps.setInt(1, mov.getCantidad());
                ps.setDate(2, java.sql.Date.valueOf(mov.getFecha()));
                ps.setInt(3, mov.getLote().getIdLote());
                ps.setInt(4, mov.getZona().getIdZonas());
                ps.setInt(5, bloqueId);
                ps.executeUpdate();
            }

            cn.commit();
            return "ok";

        } catch (SQLException e) {
            e.printStackTrace();
            return "Error en base de datos: " + e.getMessage();
        }
    }

    private int obtenerStockBloque(Connection cn, int bloqueId) throws SQLException {
        String sql = """
        SELECT COALESCE(SUM(
            CASE WHEN tipo='IN' THEN cantidad
                 WHEN tipo='OUT' THEN -cantidad
                 ELSE 0 END
        ), 0) AS stockBloque
        FROM movimiento
        WHERE Bloque_id = ?
        FOR UPDATE
    """;
        try (PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, bloqueId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt("stockBloque");
            }
        }
        return 0;
    }



    public String registrarIncidencia(Incidencia incidencia){

        StringBuilder errores = new StringBuilder();

        // ===== Validaciones de datos (sin tocar BD) =====
        if (incidencia == null) return "Error interno: incidencia nula.";
        String tipo = incidencia.getTipoIncidencia();
        String estado = incidencia.getEstado();
        Integer cantidad = incidencia.getCantidad();
        Integer loteId = incidencia.getLote_idLote();
        String descripcion = incidencia.getDescripcion() != null ? incidencia.getDescripcion().trim() : "";

        if (tipo == null || tipo.isBlank()) {
            errores.append("• Debe seleccionar un tipo (FALTANTE, VENCIDO o DAÑO).<br>");
        } else {
            String up = tipo.trim().toUpperCase();
            if (!(up.equals("FALTANTE") || up.equals("VENCIDO") || up.equals("DAÑO"))) {
                errores.append("• Tipo inválido. Use: FALTANTE, VENCIDO o DAÑO.<br>");
            } else {
                tipo = up; // normaliza
            }
        }

        if (estado == null || estado.isBlank()) {
            errores.append("• Debe seleccionar un estado (QUITADA, MANTENIDA o REGISTRADA).<br>");
        } else {
            String up = estado.trim().toUpperCase();
            if (!(up.equals("QUITADA") || up.equals("MANTENIDA") || up.equals("REGISTRADA"))) {
                errores.append("• Estado inválido. Use: QUITADA, MANTENIDA o REGISTRADA.<br>");
            } else {
                estado = up; // normaliza
            }
        }

        if (cantidad == null || cantidad <= 0) {
            errores.append("• La cantidad debe ser mayor que 0.<br>");
        }

        if (loteId == null || loteId <= 0) {
            errores.append("• Lote inválido.<br>");
        }

        if (errores.length() > 0) return errores.toString();


        // Asegúrate de incluir la columna Zonas_idZonas si la estás usando en el bean


        Connection conn = null;

        try{
            conn = this.getConnection();
            conn.setAutoCommit(false); // 1. Desactivar auto-commit

            String sql = "INSERT INTO Incidencia (tipo, cantidad, descripcion, Lote_idLote, estado) " +
                    "VALUES (?, ?, ?, ?,?)";

            // 2. Ejecutar la sentencia SQL
            try(PreparedStatement ps = conn.prepareStatement(sql)){
                ps.setString(1, tipo);
                ps.setInt(2, cantidad);
                ps.setString(3, descripcion);
                ps.setInt(4, loteId);
                ps.setString(5, estado);
                ps.executeUpdate();

            }
            conn.commit();
            return "ok";
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
                return "Error en base de datos: " + e.getMessage();
            }

        } finally {
            // 5. RESTAURAR y CERRAR
            if (conn != null) {
                try {
                    conn.setAutoCommit(true); // Restaurar auto-commit
                    conn.close();             // Cerrar la conexión
                } catch (SQLException e) {

                }
            }
        }

        return "Error interno desconocido al procesar la incidencia.";

    }


    public static class FilaEntrada {
        public String sku;
        public int loteId;
        public LocalDate fecha;
        public int cantidad;
        public LocalDate fechaVenc; // opcional (no crea lote)
    }

    private final ProductoDaoAlm pdao = new ProductoDaoAlm();

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

    private int obtenerEspacioLibreParaCarga(Connection conn, int bloqueId) throws SQLException {
        String sqlCapacidad = "SELECT (b.capacidad - COALESCE(SUM(CASE WHEN m.tipo = 'IN' THEN m.cantidad ELSE -m.cantidad END), 0)) AS espacio_libre " +
                "FROM bloque b LEFT JOIN movimiento m ON b.idBloque = m.Bloque_id " +
                "WHERE b.idBloque = ? GROUP BY b.idBloque, b.capacidad";
        try (PreparedStatement ps = conn.prepareStatement(sqlCapacidad)) {
            ps.setInt(1, bloqueId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("espacio_libre");
                }
            }
        }
        return 0;
    }

    /**
     * Nuevo método: Valida un movimiento individual de ENTRADA ('IN') para la carga masiva.
     * Implementa la lógica de registrarEntradaBackup.
     * * @param conn Conexión activa para la validación de espacio libre.
     * @param mov El movimiento con todos los IDs y datos de entrada.
     * @param filaNum El número de fila para reportar errores.
     * @return "ok" si es válido, o el mensaje de error de la fila.
     */
    public String validarMovimientoEntradaMasiva(Connection conn, Movimiento mov, int filaNum) throws SQLException {
        StringBuilder errores = new StringBuilder();
        int bloqueId = mov.getBloque().getIdBloque();
        String prefix = "• Fila " + filaNum + ": ";

        // Validaciones de formato/existencia de datos (cantidad > 0, fecha, IDs)
        if (mov.getCantidad() <= 0) {
            errores.append(prefix).append("La cantidad debe ser mayor que 0.<br>");
        }
        if (mov.getFecha() == null) {
            errores.append(prefix).append("La fecha no puede estar vacía o el formato es incorrecto.<br>");
        }
        if (mov.getLote() == null || mov.getLote().getIdLote() <= 0) {
            errores.append(prefix).append("Lote ID interno inválido.<br>");
        }
        if (mov.getZona() == null || mov.getZona().getIdZonas() <= 0) {
            errores.append(prefix).append("Zona ID interno inválido.<br>");
        }
        if (bloqueId <= 0) {
            errores.append(prefix).append("Bloque ID interno inválido.<br>");
        }

        if (errores.length() > 0) {
            return errores.toString();
        }

        // Validación de Capacidad
        int espacioLibre = obtenerEspacioLibreParaCarga(conn, bloqueId);

        if (mov.getCantidad() > espacioLibre) {
            return prefix + "No puede ingresar más de lo permitido. Espacio libre: " + espacioLibre + " unidades en " + mov.getBloque().getCodigo() + ".";
        }

        return "ok";
    }

    /**
     * Nuevo método: Registra todos los movimientos de ENTRADA en una sola transacción (Bulk Insert y Update).
     * * @param movimientos Lista de movimientos ya validados.
     * @param zonaId ID de la zona.
     * @return "ok" si es exitoso, o el mensaje de error de la transacción.
     */
    public String registrarMovimientosEntradaMasiva(List<Movimiento> movimientos, int zonaId) {
        // SQL para INSERT en movimiento (Tipo fijo 'IN')
        String sqlInsertMov = """
            INSERT INTO movimiento (tipo, cantidad, fecha, Lote_idLote, Zonas_idZonas, Bloque_id)
            VALUES ('IN', ?, ?, ?, ?, ?)
        """;
        // SQL para actualizar la cantidad total en el Lote (suma)
//        String sqlUpdateLote = "UPDATE lote SET cantidad = cantidad + ? WHERE idLote = ?";

        Connection conn = null;
        try {
            conn = this.getConnection();
            conn.setAutoCommit(false); //  INICIAR TRANSACCIÓN

            try (PreparedStatement psIns = conn.prepareStatement(sqlInsertMov);) {

                for (Movimiento m : movimientos) {
                    // A. Insertar Movimiento
                    psIns.setInt(1, m.getCantidad());
                    psIns.setDate(2, java.sql.Date.valueOf(m.getFecha()));
                    psIns.setInt(3, m.getLote().getIdLote());
                    psIns.setInt(4, zonaId);
                    psIns.setInt(5, m.getBloque().getIdBloque());
                    psIns.addBatch();


                }

                psIns.executeBatch();


                conn.commit(); //  COMMIT de la transacción
                return "ok";
            }
        } catch (SQLException e) {
            e.printStackTrace();
            try { if (conn != null) conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            return "Error en base de datos al registrar: " + e.getMessage();
        } finally {
            try { if (conn != null) conn.setAutoCommit(true); conn.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
    }







}



