package com.example.telitobodeguero.daos;

import com.example.telitobodeguero.beans.Lote;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class LoteDao {

    // ==========================================================
    // MÉTODO: Listar
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

        sql.append("ORDER BY l.fechaVencimiento DESC, p.nombre");

        try (Connection conn = DB.getConnection();
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
                    lote.setFechaVencimiento(rs.getString("fechaVencimiento"));
                    // La columna 'ubicacion' se sigue leyendo del DAO aunque no se muestre en el JSP, para el UPDATE.
                    lote.setProducto_idProducto(rs.getString("Producto_idProducto"));
                    lote.setProductoNombre(rs.getString("producto"));
                    lote.setCantidad(rs.getInt("cantidad"));
                    lote.setUsuarios_idUsuarios(rs.getInt("Usuarios_idUsuarios"));
                    lista.add(lote);
                }
            }
        }
        return lista;
    }

    // ==========================================================
    // MÉTODO: Obtener por ID
    // ==========================================================
    public Lote obtenerPorId(int idLote) throws SQLException {
        Lote lote = null;
        String sql = "SELECT l.idLote, l.fechaVencimiento, l.Producto_idProducto, p.nombre AS producto, l.cantidad, l.Usuarios_idUsuarios " +
                "FROM Lote l INNER JOIN Producto p ON l.Producto_idProducto = p.idProducto " +
                "WHERE l.idLote = ?";

        try (Connection conn = DB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idLote);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    lote = new Lote();
                    lote.setIdLote(rs.getInt("idLote"));
                    lote.setFechaVencimiento(rs.getString("fechaVencimiento"));
                    lote.setProducto_idProducto(rs.getString("Producto_idProducto"));
                    lote.setProductoNombre(rs.getString("producto"));
                    lote.setCantidad(rs.getInt("cantidad"));
                    lote.setUsuarios_idUsuarios(rs.getInt("Usuarios_idUsuarios"));
                }
            }
        }
        return lote;
    }

    // ==========================================================
    // MÉTODO AUXILIAR: Obtener Cantidad Actual del Lote 🟢 NUEVO
    // ==========================================================
    public int obtenerCantidadActual(int idLote) throws SQLException {
        String sql = "SELECT cantidad FROM Lote WHERE idLote = ?";
        int cantidadActual = 0;

        try (Connection conn = DB.getConnection();
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
    // MÉTODO: Crear Nuevo Lote (CON DESCUENTO DE STOCK)
    // ==========================================================
    public void crearLote(Lote lote) throws SQLException {
        Connection conn = null;

        String sqlLote = "INSERT INTO Lote (Producto_idProducto, Usuarios_idUsuarios, cantidad, fechaVencimiento) " +
                "VALUES (?, ?, ?, ?, ?)";

        String sqlStock = "UPDATE Producto SET stock = stock - ? WHERE idProducto = ?";

        try {
            conn = DB.getConnection();
            conn.setAutoCommit(false); // INICIO DE TRANSACCIÓN

            // --- PASO 1: INSERTAR LOTE ---
            try (PreparedStatement pstmtLote = conn.prepareStatement(sqlLote, Statement.RETURN_GENERATED_KEYS)) {

                pstmtLote.setString(1, lote.getProducto_idProducto());
                pstmtLote.setInt(2, lote.getUsuarios_idUsuarios());
                pstmtLote.setInt(3, lote.getCantidad());
                pstmtLote.setString(5, lote.getFechaVencimiento());

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
                    conn.rollback(); // Fallo: DESHACER TRANSACCIÓN
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
    // MÉTODO: Actualizar Lote Existente (CON AJUSTE DE STOCK) 🟢 MODIFICADO
    // ==========================================================
    public void actualizarLote(Lote lote) throws SQLException {
        Connection conn = null;

        String sqlLote = "UPDATE Lote SET cantidad = ?, fechaVencimiento = ? " +
                "WHERE idLote = ? AND Usuarios_idUsuarios = ?";

        // stock = stock - (cantidadNueva - cantidadAnterior)
        String sqlStock = "UPDATE Producto SET stock = stock - ? WHERE idProducto = ?";

        int cantidadAnterior = obtenerCantidadActual(lote.getIdLote());
        int diferenciaCantidad = lote.getCantidad() - cantidadAnterior;

        try {
            conn = DB.getConnection();
            conn.setAutoCommit(false); // INICIO DE TRANSACCIÓN

            // --- PASO 1: ACTUALIZAR LOTE ---
            try (PreparedStatement pstmtLote = conn.prepareStatement(sqlLote)) {

                pstmtLote.setInt(1, lote.getCantidad());
                pstmtLote.setString(3, lote.getFechaVencimiento());
                pstmtLote.setInt(4, lote.getIdLote());
                pstmtLote.setInt(5, lote.getUsuarios_idUsuarios());

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
                    conn.rollback(); // Fallo: DESHACER TRANSACCIÓN
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
    // MÉTODO Eliminar Lote (CON DEVOLUCIÓN DE STOCK) 🟢 MODIFICADO
    // ==========================================================
    public void eliminarLote(int idLote, int idUsuario) throws SQLException {
        Connection conn = null;

        Lote loteAEliminar = obtenerPorId(idLote);
        if (loteAEliminar == null || loteAEliminar.getUsuarios_idUsuarios() != idUsuario) {
            throw new SQLException("Lote no encontrado o no autorizado para eliminar.");
        }

        String sqlLote = "DELETE FROM Lote WHERE idLote = ? AND Usuarios_idUsuarios = ?";
        // stock = stock + cantidad eliminada
        String sqlStock = "UPDATE Producto SET stock = stock + ? WHERE idProducto = ?";

        try {
            conn = DB.getConnection();
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
                    conn.rollback(); // Fallo: DESHACER TRANSACCIÓN
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