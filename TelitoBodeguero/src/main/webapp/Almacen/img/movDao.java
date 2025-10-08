
// MovimientoDao.java (dentro del try con la conexión abierta)
// ...

// 1. OBTENER ID DE LOTE FINAL
int idLoteFinal = movimiento.getLote_idLote(); // Obtenido del bean

if (idLoteFinal <= 0) {
    // ESCENARIO 1: LOTE NUEVO
    
    // Obtener idProducto (implementación abajo)
    int idProducto = this.obtenerIdProductoPorSku(conn, sku); 

    // 2. INSERTAR NUEVO LOTE
    // Nota: El sqlInsertLote estaba vacío, lo corregimos aquí
    String sqlInsertLote = "INSERT INTO lote (fechaVencimiento, Producto_idProducto, Usuarios_idUsuarios, cantidad) VALUES (?, ?, ?, ?)";
    
    try (PreparedStatement psLote = conn.prepareStatement(sqlInsertLote, Statement.RETURN_GENERATED_KEYS)) {
        psLote.setString(1, fechaVencimiento); 
        psLote.setInt(2, idProducto);
        psLote.setInt(3, idUsuarioSesion);
        psLote.setInt(4, movimiento.getCantidad());
        psLote.executeUpdate();

        try (ResultSet rs = psLote.getGeneratedKeys()) {
            if (rs.next()) {
                idLoteFinal = rs.getInt(1); // ¡ID de lote generado!
            } else {
                throw new SQLException("Error al obtener ID de Lote generado.");
            }
        }
    }
} else { 
    // ESCENARIO 2: LOTE EXISTENTE
    
    // 2. ACTUALIZAR LOTE (Tu lógica actual)
    String sqlActLote = "UPDATE lote SET cantidad = cantidad + ? WHERE idLote = ?";
    try (PreparedStatement pstmtLote = conn.prepareStatement(sqlActLote)) {
        pstmtLote.setInt(1, movimiento.getCantidad());
        pstmtLote.setInt(2, idLoteFinal);
        pstmtLote.executeUpdate();
    }
}

// 3. INSERTAR MOVIMIENTO (USA EL ID FINAL)
String sqlMov = "INSERT INTO movimiento (tipo, cantidad, fecha, Lote_idLote, Zonas_idZonas) VALUES ('IN', ?, ?, ?, ?)";

try (PreparedStatement pstmtMov = conn.prepareStatement(sqlMov)) {
    pstmtMov.setInt(1, movimiento.getCantidad());
    pstmtMov.setString(2, movimiento.getFecha());
    pstmtMov.setInt(3, idLoteFinal); // Usa el ID corregido/generado
    pstmtMov.setInt(4, movimiento.getZonas_idZonas());
    pstmtMov.executeUpdate();
}

conn.commit();
exito = true;
// ... (continúa con el manejo de catch)