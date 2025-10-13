package com.example.telitobodeguero.daos;



import com.example.telitobodeguero.beans.*;

import com.example.telitobodeguero.beans.Movimiento;

import java.sql.*;
import java.util.ArrayList;
import java.time.ZoneId;
import java.time.Instant;

public class MovimientoDaoLogis {

    // -------------------------------------------------------------------------
    // 1. OBTENER ÚLTIMOS 5 MOVIMIENTOS (Dashboard)
    // -------------------------------------------------------------------------
    public ArrayList<com.example.telitobodeguero.beans.Movimiento> obtenerListaMovimientos() {
        ArrayList<Movimiento> listaMovimientos = new ArrayList<>();
        String user = "root";
        String pass = "12345678";
        String url  = "jdbc:mysql://127.0.0.1:3306/Bodega-Telito";

        String sql = "SELECT " +
                "M.fecha AS Fecha, " +
                "M.tipo AS Movimiento, " +
                "P.nombre AS NombreProducto, " +
                "M.cantidad AS Cantidad, L.idLote," +
                "Z.nombre AS NombreZona " +
                "FROM Movimiento M " +
                "INNER JOIN Lote L ON M.Lote_idLote = L.idLote " +
                "INNER JOIN Producto P ON L.Producto_idProducto = P.idProducto " +
                "INNER JOIN Zonas Z ON M.Zonas_idZonas = Z.idZonas " +
                "ORDER BY M.fecha DESC " +
                "LIMIT 5";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection conn = DriverManager.getConnection(url, user, pass);
                 PreparedStatement pstmt = conn.prepareStatement(sql);
                 ResultSet rs = pstmt.executeQuery()) {

                while (rs.next()) {
                    Movimiento mov = new Movimiento();

                    // Fecha robusta
                    Date sqlDate = rs.getDate("Fecha");
                    mov.setFecha(sqlDate != null ? sqlDate.toLocalDate(): null);

                    mov.setTipoMovimiento(rs.getString("Movimiento"));
                    mov.setCantidad(rs.getInt("Cantidad"));

                    //cambio para movimiento
                    Lote lote = new Lote();
                    lote.setIdLote(rs.getInt("idLote"));

                    Producto producto = new Producto();
                    producto.setNombre(rs.getString("NombreProducto"));

                    lote.setProducto(producto);
                    mov.setLote(lote);

//                    Producto p = new Producto();
//                    p.setNombre(rs.getString("NombreProducto"));
//                    mov.setProducto(p);

                    Zonas z = new Zonas();
                    z.setNombre(rs.getString("NombreZona"));
                    mov.setZona(z);

                    listaMovimientos.add(mov);
                }
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        return listaMovimientos;
    }


    // -------------------------------------------------------------------------
    // 2. CONTAR TOTAL DE MOVIMIENTOS
    // -------------------------------------------------------------------------
    public int contarTotalMovimientos() {
        String user = "root";
        String pass = "12345678";
        String url = "jdbc:mysql://127.0.0.1:3306/Bodega-Telito";
        String sql = "SELECT COUNT(*) AS total FROM Movimiento";
        int total = 0;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection conn = DriverManager.getConnection(url, user, pass);
                 PreparedStatement pstmt = conn.prepareStatement(sql);
                 ResultSet rs = pstmt.executeQuery()) {

                if (rs.next()) {
                    total = rs.getInt("total");
                }
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        return total;
    }


    // -------------------------------------------------------------------------
    // 3. OBTENER REPORTE DE MOVIMIENTOS FILTRADOS
    // -------------------------------------------------------------------------
    public ArrayList<Movimiento> obtenerReporteFiltrado(
            String fechaDesde, String fechaHasta, String tipoMovimiento,
            String nombreProducto, String nombreZona) {

        ArrayList<Movimiento> listaMovimientos = new ArrayList<>();
        String user = "root";
        String pass = "12345678";
        String url  = "jdbc:mysql://127.0.0.1:3306/Bodega-Telito";

        StringBuilder sql = new StringBuilder(
                "SELECT M.fecha AS Fecha, M.tipo AS Movimiento, " +
                        "P.nombre AS NombreProducto, L.idLote, M.cantidad AS Cantidad, Z.nombre AS NombreZona " +
                        "FROM Movimiento M " +
                        "INNER JOIN Lote L ON M.Lote_idLote = L.idLote " +
                        "INNER JOIN Producto P ON L.Producto_idProducto = P.idProducto " +
                        "INNER JOIN Zonas Z ON M.Zonas_idZonas = Z.idZonas " +
                        "WHERE 1=1 "
        );

        // Armado dinámico
        if (fechaDesde != null && !fechaDesde.isBlank()) sql.append("AND M.fecha >= ? ");
        if (fechaHasta != null && !fechaHasta.isBlank()) sql.append("AND M.fecha <= ? ");
        if (tipoMovimiento != null && !tipoMovimiento.isBlank() && !tipoMovimiento.equalsIgnoreCase("Todos")) sql.append("AND M.tipo = ? ");
        if (nombreProducto != null && !nombreProducto.isBlank() && !nombreProducto.equalsIgnoreCase("Todos")) sql.append("AND P.nombre = ? ");
        if (nombreZona != null && !nombreZona.isBlank() && !nombreZona.equalsIgnoreCase("Todos")) sql.append("AND Z.nombre = ? ");

        sql.append("ORDER BY M.fecha DESC");

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection conn = DriverManager.getConnection(url, user, pass);
                 PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {

                int idx = 1;

                if (fechaDesde != null && !fechaDesde.isBlank()) {
                    pstmt.setDate(idx++, Date.valueOf(fechaDesde)); // yyyy-MM-dd
                }
                if (fechaHasta != null && !fechaHasta.isBlank()) {
                    pstmt.setDate(idx++, Date.valueOf(fechaHasta));
                }
                if (tipoMovimiento != null && !tipoMovimiento.isBlank() && !tipoMovimiento.equalsIgnoreCase("Todos")) {
                    pstmt.setString(idx++, tipoMovimiento);
                }
                if (nombreProducto != null && !nombreProducto.isBlank() && !nombreProducto.equalsIgnoreCase("Todos")) {
                    pstmt.setString(idx++, nombreProducto);
                }
                if (nombreZona != null && !nombreZona.isBlank() && !nombreZona.equalsIgnoreCase("Todos")) {
                    pstmt.setString(idx++, nombreZona);
                }

                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        Movimiento mov = new Movimiento();

                        Date sqlDate = rs.getDate("Fecha");

                        mov.setFecha(sqlDate != null ? sqlDate.toLocalDate() : null);

                        mov.setTipoMovimiento(rs.getString("Movimiento"));
                        mov.setCantidad(rs.getInt("Cantidad"));


                        Lote lote = new Lote();
                        lote.setIdLote(rs.getInt("idLote"));

                        Producto producto = new Producto();
                        producto.setNombre(rs.getString("NombreProducto"));

                        lote.setProducto(producto);
                        mov.setLote(lote);


//                        Producto p = new Producto();
//                        p.setNombre(rs.getString("NombreProducto"));
//                        mov.setProducto(p);

                        Zonas z = new Zonas();
                        z.setNombre(rs.getString("NombreZona"));
                        mov.setZona(z);

                        listaMovimientos.add(mov);
                    }
                }
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        return listaMovimientos;
    }

    // Archivo: MovimientoDao.java

// ... (métodos existentes) ...

    // -------------------------------------------------------------------------
    // 4. CONTAR TOTAL DE MOVIMIENTOS POR TIPO (Entradas/Salidas)
    // -------------------------------------------------------------------------
    /**
     * Cuenta el total de movimientos de un tipo específico ('IN' o 'OUT').
     * @param tipo El tipo de movimiento a contar ('IN' o 'OUT').
     * @return El total de movimientos.
     */
    public int contarMovimientosPorTipo(String tipo) {
        String user = "root";
        String pass = "12345678";
        String url = "jdbc:mysql://127.0.0.1:3306/Bodega-Telito";
        // Contamos la SUMA de las cantidades de movimientos del tipo especificado
        String sql = "SELECT SUM(cantidad) AS total FROM Movimiento WHERE tipo = ?";
        int total = 0;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection conn = DriverManager.getConnection(url, user, pass);
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {

                pstmt.setString(1, tipo); // Seteamos 'IN' o 'OUT'

                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        // Usamos getInt, si el resultado es NULL (no hay movimientos) devuelve 0
                        total = rs.getInt("total");
                    }
                }
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        return total;
    }

}
