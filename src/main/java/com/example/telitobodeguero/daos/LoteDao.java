package com.example.telitobodeguero.daos;

import com.example.telitobodeguero.beans.Lote;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.sql.*;

public class LoteDao extends BaseDao{

    // NOTA: Se asume que el Bean Lote tiene métodos setFechaVencimiento(String o LocalDate)
    // y setProducto_idProducto(String o int) para compatibilidad con tus métodos.

    // ==========================================================
    // 1. MÉTODO CRÍTICO: Listar Lotes disponibles para despacho (Para el ComboBox)
    // ==========================================================
    /**
     * Lista los lotes disponibles (cantidad > 0) para un producto y productor específicos,
     * ordenados por fecha de vencimiento (FEFO).
     */
    public List<Lote> listarLotesPorProducto(int idProducto, int idProductor) throws SQLException {
        List<Lote> lotes = new ArrayList<>();

        String sql = "SELECT idLote, fechaVencimiento, cantidad " +
                "FROM Lote " +
                "WHERE Producto_idProducto = ? AND Usuarios_idUsuarios = ? AND cantidad > 0 " +
                "ORDER BY fechaVencimiento ASC";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idProducto);
            pstmt.setInt(2, idProductor);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Lote lote = new Lote();
                    lote.setIdLote(rs.getInt("idLote"));
                    lote.setCantidad(rs.getInt("cantidad"));

                    // Mapeo robusto de la fecha (asumiendo que tu bean Lote puede manejar String o Date/LocalDate)
                    Date fechaSql = rs.getDate("fechaVencimiento");
                    if (fechaSql != null) {
                        lote.setFechaVencimiento(fechaSql.toString());
                    }

                    lotes.add(lote);
                }
            }
        }
        return lotes;
    }

    // ==========================================================
    // 2. MÉTODO CRÍTICO: Reducir Stock Lote (Para la Transacción de Despacho) 🟢
    // ==========================================================
    /**
     * Reduce la cantidad de un lote específico. Debe usarse DENTRO de una transacción.
     * @param conn Conexión abierta y con AutoCommit=false.
     * @param idLote ID del lote a descontar.
     * @param cantidadDescontar Cantidad a restar.
     */
    public void reducirStockLote(Connection conn, int idLote, int cantidadDescontar) throws SQLException {
        // La condición 'cantidad >= ?' asegura que no se descuente más del stock real.
        String sql = "UPDATE Lote SET cantidad = cantidad - ? WHERE idLote = ? AND cantidad >= ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, cantidadDescontar);
            pstmt.setInt(2, idLote);
            pstmt.setInt(3, cantidadDescontar);

            int filasAfectadas = pstmt.executeUpdate();
            if (filasAfectadas == 0) {
                // Lanza excepción que será atrapada por el Servlet para ejecutar el ROLLBACK
                throw new SQLException("Error de stock: No se pudo descontar el stock del lote " + idLote + ". Cantidad insuficiente o lote no encontrado.");
            }
        }
    }


    // ==========================================================
    // 3. MÉTODO: Listar por Productor (Tus métodos existentes)
    // ==========================================================
    public List<Lote> listarPorProductor(int idUsuario, String idProductoFiltro) throws SQLException {
        List<Lote> lista = new ArrayList<>();

        Integer idProdFiltro = null;
        boolean tieneFiltro = false;

        if (idProductoFiltro != null && !idProductoFiltro.trim().isEmpty()) {
            try {
                idProdFiltro = Integer.parseInt(idProductoFiltro.trim());
                tieneFiltro = true;
            } catch (NumberFormatException e) {
                System.err.println("Advertencia: El filtro de idProducto no es un número válido: " + idProductoFiltro);
            }
        }

        StringBuilder sql = new StringBuilder();
        sql.append("SELECT l.idLote, l.fechaVencimiento, ");
        sql.append("l.Producto_idProducto, p.nombre AS producto, l.cantidad, l.Usuarios_idUsuarios ");
        sql.append("FROM Lote l ");
        sql.append("INNER JOIN Producto p ON l.Producto_idProducto = p.idProducto ");
        sql.append("WHERE l.Usuarios_idUsuarios = ? ");

        if (tieneFiltro) {
            sql.append("AND l.Producto_idProducto = ? ");
        }

        sql.append("ORDER BY l.fechaVencimiento ASC, p.nombre");

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {

            pstmt.setInt(1, idUsuario);
            int paramIndex = 2;

            if (tieneFiltro) {
                pstmt.setInt(paramIndex, idProdFiltro);
            }

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Lote lote = new Lote();
                    lote.setIdLote(rs.getInt("idLote"));

                    Date fechaSql = rs.getDate("fechaVencimiento");
                    lote.setFechaVencimiento(fechaSql != null ? fechaSql.toString() : null);

                    lote.setProducto_idProducto(String.valueOf(rs.getInt("Producto_idProducto")));
                    lote.setProductoNombre(rs.getString("producto"));
                    lote.setCantidad(rs.getInt("cantidad"));
                    lote.setUsuarios_idUsuarios(rs.getInt("Usuarios_idUsuarios"));
                    lista.add(lote);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error en listarPorProductor: " + e.getMessage());
            throw e;
        }
        return lista;
    }

    // ==========================================================
    // 4. MÉTODO: Obtener por ID (Tus métodos existentes)
    // ==========================================================
    public Lote obtenerPorId(int idLote) throws SQLException {
        Lote lote = null;
        String sql = "SELECT l.idLote, l.fechaVencimiento, l.Producto_idProducto, p.nombre AS producto, l.cantidad, l.Usuarios_idUsuarios " +
                "FROM Lote l INNER JOIN Producto p ON l.Producto_idProducto = p.idProducto " +
                "WHERE l.idLote = ?";

        // Corregido: Usar getConnection() de BaseDao en lugar de DB.getConnection()
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idLote);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    lote = new Lote();
                    lote.setIdLote(rs.getInt("idLote"));
                    lote.setFechaVencimiento(rs.getString("fechaVencimiento"));
                    lote.setProducto_idProducto(String.valueOf(rs.getInt("Producto_idProducto")));
                    lote.setProductoNombre(rs.getString("producto"));
                    lote.setCantidad(rs.getInt("cantidad"));
                    lote.setUsuarios_idUsuarios(rs.getInt("Usuarios_idUsuarios"));
                }
            }
        }
        return lote;
    }

    // ==========================================================
    // 5. MÉTODO AUXILIAR: Obtener Cantidad Actual del Lote
    // ==========================================================
    public int obtenerCantidadActual(int idLote) throws SQLException {
        String sql = "SELECT cantidad FROM Lote WHERE idLote = ?";
        int cantidadActual = 0;

        // Corregido: Usar getConnection() de BaseDao en lugar de DB.getConnection()
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idLote);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    cantidadActual = rs.getInt("cantidad");
                }
            }
        }
        return cantidadActual;
    }

    // ==========================================================
    // 6. MÉTODO: Crear Nuevo Lote (CON DESCUENTO DE STOCK en Producto)
    // ==========================================================
    public void crearLote(Lote lote) throws SQLException {
        Connection conn = null;

        // Corregido: Faltaba un parámetro en la sentencia SQL original (solo hay 4 '?' y se asignan 5)
        String sqlLote = "INSERT INTO Lote (Producto_idProducto, Usuarios_idUsuarios, cantidad, fechaVencimiento) " +
                "VALUES (?, ?, ?, ?)";

        String sqlStock = "UPDATE Producto SET stock = stock - ? WHERE idProducto = ?";

        try {
            // Corregido: Usar getConnection() de BaseDao en lugar de DB.getConnection()
            conn = getConnection();
            conn.setAutoCommit(false); // INICIO DE TRANSACCIÓN

            // --- PASO 1: INSERTAR LOTE ---
            try (PreparedStatement pstmtLote = conn.prepareStatement(sqlLote, Statement.RETURN_GENERATED_KEYS)) {

                pstmtLote.setString(1, lote.getProducto_idProducto());
                pstmtLote.setInt(2, lote.getUsuarios_idUsuarios());
                pstmtLote.setInt(3, lote.getCantidad());
                pstmtLote.setString(4, lote.getFechaVencimiento()); // Corregido el índice

                pstmtLote.executeUpdate();

                try (ResultSet rs = pstmtLote.getGeneratedKeys()) {
                    if (rs.next()) {
                        lote.setIdLote(rs.getInt(1));
                    }
                }
            }

            // --- PASO 2: REDUCIR STOCK DEL PRODUCTO ---
            try (PreparedStatement pstmtStock = conn.prepareStatement(sqlStock)) {

                int cantidadLote = lote.getCantidad();
                int idProducto = Integer.parseInt(lote.getProducto_idProducto());

                pstmtStock.setInt(1, cantidadLote);
                pstmtStock.setInt(2, idProducto);

                pstmtStock.executeUpdate();
            }

            conn.commit(); // Éxito: CONFIRMAR TRANSACCIÓN

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    System.err.println("Error durante rollback: " + ex.getMessage());
                }
            }
            System.err.println("Error al crear lote y actualizar stock: " + e.getMessage());
            throw e;

        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException ex) {
                    System.err.println("Error al cerrar conexión: " + ex.getMessage());
                }
            }
        }
    }

    // ==========================================================
    // 7. MÉTODO: Actualizar Lote Existente (CON AJUSTE DE STOCK en Producto)
    // ==========================================================
    public void actualizarLote(Lote lote) throws SQLException {
        Connection conn = null;

        String sqlLote = "UPDATE Lote SET cantidad = ?, fechaVencimiento = ? " +
                "WHERE idLote = ? AND Usuarios_idUsuarios = ?";

        String sqlStock = "UPDATE Producto SET stock = stock - ? WHERE idProducto = ?";

        int cantidadAnterior = obtenerCantidadActual(lote.getIdLote());
        int diferenciaCantidad = lote.getCantidad() - cantidadAnterior;

        try {
            // Corregido: Usar getConnection() de BaseDao en lugar de DB.getConnection()
            conn = getConnection();
            conn.setAutoCommit(false); // INICIO DE TRANSACCIÓN

            // --- PASO 1: ACTUALIZAR LOTE ---
            try (PreparedStatement pstmtLote = conn.prepareStatement(sqlLote)) {

                pstmtLote.setInt(1, lote.getCantidad());
                pstmtLote.setString(2, lote.getFechaVencimiento()); // Corregido el índice
                pstmtLote.setInt(3, lote.getIdLote());
                pstmtLote.setInt(4, lote.getUsuarios_idUsuarios());

                pstmtLote.executeUpdate();
            }

            // --- PASO 2: AJUSTAR STOCK DEL PRODUCTO ---
            if (diferenciaCantidad != 0) {
                try (PreparedStatement pstmtStock = conn.prepareStatement(sqlStock)) {

                    int idProducto = Integer.parseInt(lote.getProducto_idProducto());

                    pstmtStock.setInt(1, diferenciaCantidad); // Positivo resta, Negativo suma
                    pstmtStock.setInt(2, idProducto);

                    pstmtStock.executeUpdate();
                }
            }

            conn.commit(); // Éxito: CONFIRMAR TRANSACCIÓN

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    System.err.println("Error durante rollback: " + ex.getMessage());
                }
            }
            System.err.println("Error al actualizar lote y ajustar stock: " + e.getMessage());
            throw e;

        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException ex) {
                    System.err.println("Error al cerrar conexión: " + ex.getMessage());
                }
            }
        }
    }

    // ==========================================================
    // 8. MÉTODO Eliminar Lote (CON DEVOLUCIÓN DE STOCK a Producto)
    // ==========================================================
    public void eliminarLote(int idLote, int idUsuario) throws SQLException {
        Connection conn = null;

        Lote loteAEliminar = obtenerPorId(idLote);
        if (loteAEliminar == null || loteAEliminar.getUsuarios_idUsuarios() != idUsuario) {
            throw new SQLException("Lote no encontrado o no autorizado para eliminar.");
        }

        String sqlLote = "DELETE FROM Lote WHERE idLote = ? AND Usuarios_idUsuarios = ?";
        String sqlStock = "UPDATE Producto SET stock = stock + ? WHERE idProducto = ?";

        try {
            // Corregido: Usar getConnection() de BaseDao en lugar de DB.getConnection()
            conn = getConnection();
            conn.setAutoCommit(false); // INICIO DE TRANSACCIÓN

            // --- PASO 1: DEVOLVER STOCK ---
            try (PreparedStatement pstmtStock = conn.prepareStatement(sqlStock)) {

                int cantidadDevuelta = loteAEliminar.getCantidad();
                int idProducto = Integer.parseInt(loteAEliminar.getProducto_idProducto());

                pstmtStock.setInt(1, cantidadDevuelta); // Sumar la cantidad del lote
                pstmtStock.setInt(2, idProducto);

                pstmtStock.executeUpdate();
            }

            // --- PASO 2: ELIMINAR LOTE ---
            try (PreparedStatement pstmtLote = conn.prepareStatement(sqlLote)) {
                pstmtLote.setInt(1, idLote);
                pstmtLote.setInt(2, idUsuario);

                pstmtLote.executeUpdate();
            }

            conn.commit(); // Éxito: CONFIRMAR TRANSACCIÓN

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    System.err.println("Error durante rollback: " + ex.getMessage());
                }
            }
            System.err.println("Error al eliminar lote y devolver stock: " + e.getMessage());
            throw e;

        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException ex) {
                    System.err.println("Error al cerrar conexión: " + ex.getMessage());
                }
            }
        }
    }

}