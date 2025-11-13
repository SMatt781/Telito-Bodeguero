package com.example.telitobodeguero.daos;

import com.example.telitobodeguero.beans.Producto;
import com.example.telitobodeguero.beans.Zonas;
// Asume que DB está importado y es accesible
// import com.example.telitobodeguero.daos.DB;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductoDao {

    // ==========================================================
    //               MÉTODO PARA GUARDAR PRODUCTO
    // ==========================================================

    /**
     * Guarda un nuevo producto en la base de datos.
     */
    public void crear(Producto p, int idProductor) throws SQLException {

        // generar SKU automáticamente
        String nuevoSKU = generarNuevoSKU();

        String sql = "INSERT INTO Producto (sku, nombre, precio, stock) VALUES (?,?,?,?)";

        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, nuevoSKU);                 // ← ya no viene del formulario
            ps.setString(2, p.getNombre());
            ps.setDouble(3, p.getPrecio());
            ps.setInt(4, p.getStock());

            ps.executeUpdate();
        }
    }


    // ==========================================================
    //              MÉTODOS DE LISTADO Y BÚSQUEDA
    // ==========================================================

    /**
     * 🎯 NUEVO: Lista SÓLO los productos que el productor logeado tiene registrados en algún lote.
     * Esto soporta el listado principal en el doGet del Servlet.
     */
    public String generarNuevoSKU() {
        String nuevo = "PROD-0001";

        String sql = "SELECT sku FROM Producto ORDER BY idProducto DESC LIMIT 1";

        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                String ultimoSKU = rs.getString("sku"); // PROD-0045
                int numero = Integer.parseInt(ultimoSKU.substring(5)); // 45
                numero++;
                String num = String.format("%04d", numero); // 0046
                nuevo = "PROD-" + num;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return nuevo;
    }
    public String obtenerUltimoSku() throws SQLException {
        String sql = "SELECT sku FROM Producto ORDER BY idProducto DESC LIMIT 1";

        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return rs.getString("sku");
            }
        }
        return null;
    }

    public List<Producto> listarPorProductor(int idProductor) throws SQLException {
        List<Producto> lista = new ArrayList<>();

        String sql = """
            SELECT DISTINCT
                   p.idProducto, 
                   p.sku, 
                   p.nombre, 
                   p.precio, 
                   p.stock 
            FROM Producto p
            JOIN Lote l ON p.idProducto = l.Producto_idProducto
            WHERE l.Usuarios_idUsuarios = ?
            ORDER BY p.nombre
        """;

        try (Connection conn = DB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idProductor);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Producto p = new Producto();
                    p.setIdProducto(rs.getInt("idProducto"));
                    p.setSku(rs.getString("sku"));
                    p.setNombre(rs.getString("nombre"));
                    p.setPrecio(rs.getDouble("precio"));
                    p.setStock(rs.getInt("stock"));
                    lista.add(p);
                }
            }
        }
        return lista;
    }

    /**
     * Lista productos visibles para el productor (los que puede vender: tiene lote o no tienen lote de nadie).
     */
    public List<Producto> listarVisiblesPorProductor(int idProductor) throws SQLException {
        List<Producto> lista = new ArrayList<>();

        String sql = """
        SELECT DISTINCT
            p.idProducto, p.sku, p.nombre, p.precio, p.stock
        FROM Producto p
        LEFT JOIN Lote l ON l.Producto_idProducto = p.idProducto
        WHERE l.Usuarios_idUsuarios = ? OR l.Producto_idProducto IS NULL
        ORDER BY p.nombre
        """;

        try (Connection conn = DB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idProductor);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Producto p = new Producto();
                    p.setIdProducto(rs.getInt("idProducto"));
                    p.setSku(rs.getString("sku"));
                    p.setNombre(rs.getString("nombre"));
                    p.setPrecio(rs.getDouble("precio"));
                    p.setStock(rs.getInt("stock"));
                    lista.add(p);
                }
            }
        }
        return lista;
    }


    public List<Producto> listarProductosConLotesPorProductor(int idUsuario) throws SQLException {
        String sql = """
        SELECT 
            p.idProducto,
            p.sku,
            p.nombre,
            COALESCE(SUM(CASE WHEN m.tipo = 'IN' THEN m.cantidad ELSE -m.cantidad END), 0) AS stock,
            GROUP_CONCAT(DISTINCT l.idLote) AS lotes,
            z.idZonas,
            z.nombre AS zona
        FROM producto p
        INNER JOIN lote l ON p.idProducto = l.Producto_idProducto
        INNER JOIN movimiento m ON l.idLote = m.Lote_idLote
        INNER JOIN zonas z ON z.idZonas = m.Zonas_idZonas
        WHERE l.Usuarios_idUsuarios = ?
        GROUP BY p.idProducto, z.idZonas
        ORDER BY p.nombre
    """;
        List<Producto> lista = new ArrayList<>();
        try (Connection conn = DB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idUsuario);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Producto producto = new Producto();
                    producto.setIdProducto(rs.getInt("idProducto"));
                    producto.setSku(rs.getString("sku"));
                    producto.setNombre(rs.getString("nombre"));
                    producto.setStock(rs.getInt("stock"));
                    producto.setLotes(rs.getString("lotes"));

                    Zonas zonas = new Zonas();
                    zonas.setIdZonas(rs.getInt("idZonas"));
                    zonas.setNombre(rs.getString("zona"));
                    producto.setZona(zonas);

                    lista.add(producto);
                }
            }
        }

        return lista;
    }

    /** Precios sugeridos (todo el catálogo) */
    public List<Producto> listarTodos() throws SQLException {
        String sql = """
            SELECT idProducto, sku, nombre, precio, stock
            FROM Producto
            ORDER BY nombre
        """;
        List<Producto> lista = new ArrayList<>();
        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Producto p = new Producto();
                p.setIdProducto(rs.getInt("idProducto"));
                p.setSku(rs.getString("sku"));
                p.setNombre(rs.getString("nombre"));
                p.setPrecio(rs.getDouble("precio"));
                p.setStock(rs.getInt("stock"));
                lista.add(p);
            }
        }
        return lista;
    }

    public Producto obtenerPorId(int id) throws SQLException {
        String sql = "SELECT idProducto, sku, nombre, precio, stock FROM Producto WHERE idProducto = ?";
        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Producto p = new Producto();
                    p.setIdProducto(rs.getInt("idProducto"));
                    p.setSku(rs.getString("sku"));
                    p.setNombre(rs.getString("nombre"));
                    p.setPrecio(rs.getDouble("precio"));
                    p.setStock(rs.getInt("stock"));
                    return p;
                }
            }
        }
        return null;
    }

    // ==========================================================
    //               MÉTODOS DE ACTUALIZACIÓN/BORRADO
    // ==========================================================

    public void actualizar(Producto p) throws SQLException {
        String sql = "UPDATE Producto SET sku=?, nombre=?, precio=?, stock=? WHERE idProducto=?";
        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, p.getSku());
            ps.setString(2, p.getNombre());
            ps.setDouble(3, p.getPrecio());
            ps.setInt(4, p.getStock());
            ps.setInt(5, p.getIdProducto());
            ps.executeUpdate();
        }
    }

    public void borrar(int id) throws SQLException {
        String sql = "DELETE FROM Producto WHERE idProducto=?";
        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    // ==========================================================
    //                 MÉTODOS DE VALIDACIÓN/STOCK
    // ==========================================================

    /**
     * 🔒 NUEVO: Verifica si un productor (idProductor) tiene al menos un lote del producto (idProducto).
     * Esencial para la validación de seguridad en el doPost.
     */
    public boolean esPropiedadDeProductor(int idProducto, int idProductor) throws SQLException {

        String sql = """
            SELECT 1
            FROM Lote l
            WHERE l.Producto_idProducto = ? AND l.Usuarios_idUsuarios = ?
            LIMIT 1
        """;

        try (Connection conn = DB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idProducto);
            pstmt.setInt(2, idProductor);

            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        }
    }


    public int stockRestanteParaUsuario(int idProductor, int idProducto) throws SQLException {
        String sql = """
            SELECT (COALESCE(p.stock,0) - COALESCE(ls.sum_cant,0)) AS stock_total
            FROM Producto p
            LEFT JOIN (
                SELECT Producto_idProducto, SUM(cantidad) AS sum_cant
                FROM Lote
                WHERE Usuarios_idUsuarios = ?
                GROUP BY Producto_idProducto
            ) ls ON ls.Producto_idProducto = p.idProducto
            WHERE p.idProducto = ?
        """;
        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, idProductor);
            ps.setInt(2, idProducto);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int v = rs.getInt("stock_total");
                    return Math.max(0, v);
                }
            }
        }
        return 0; // fallback seguro
    }
}