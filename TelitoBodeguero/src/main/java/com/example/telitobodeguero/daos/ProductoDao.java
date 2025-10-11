package com.example.telitobodeguero.daos;
import com.example.telitobodeguero.beans.Producto;
import com.example.telitobodeguero.beans.Zonas;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductoDao{
    public ArrayList<Producto> obtenerProductos() {
        ArrayList<Producto> listaProductos =new ArrayList();

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        String user = "root";
        String pass = "12345678";
        String url = "jdbc:mysql://localhost:3306/bodega-telito";
        //String sql = "SELECT p.sku as SKU from producto p";
        String sql = "SELECT p.sku as SKU, "+
                "p.nombre as Nombre, "+
                "coalesce(sum(CASE WHEN m.tipo = 'IN' THEN m.cantidad ELSE -m.cantidad END),0) AS Stock, "+

                "group_concat(distinct l.idLote) as Lotes, " +
                "z.idZonas, z.nombre as Zona " +
                "FROM  producto p " +
                "INNER JOIN lote l on p.idProducto = l.Producto_idProducto " +
                "INNER JOIN movimiento m on l.idLote = m.Lote_idLote " +
                "INNER JOIN zonas z on z.idZonas = m.Zonas_idZonas " +

                "WHERE z.nombre = 'Oeste' " +
                "GROUP BY p.idProducto, z.nombre";

        try (Connection conn = DriverManager.getConnection(url, user, pass);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)){

            while (rs.next()){
                Producto producto = new Producto();
                producto.setSku(rs.getString(1));
                producto.setNombre(rs.getString(2));
//                producto.setDistrito(rs.getString(3));
                producto.setStock(rs.getInt(3));
                producto.setLotes(rs.getString(4));
                Zonas  zonas = new Zonas();
                zonas.setIdZonas(rs.getInt("idZonas"));
                zonas.setNombre(rs.getString("Zona"));
                producto.setZona(zonas);


                listaProductos.add(producto);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return listaProductos;
    }

    public List<Producto> listarVisiblesPorProductor(int idProductor) throws SQLException {
        String sql = """
            SELECT 
                p.idProducto,
                p.sku,
                p.nombre,
                p.precio,
                (COALESCE(p.stock,0) - COALESCE(ls.sum_cant,0)) AS stock_total
            FROM Producto p
            LEFT JOIN (
                SELECT Producto_idProducto, SUM(cantidad) AS sum_cant
                FROM Lote
                WHERE Usuarios_idUsuarios = ?
                GROUP BY Producto_idProducto
            ) ls ON ls.Producto_idProducto = p.idProducto
            WHERE (COALESCE(p.stock,0) - COALESCE(ls.sum_cant,0)) > 0
            ORDER BY p.nombre
        """;

        List<Producto> lista = new ArrayList<>();
        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, idProductor);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Producto p = new Producto();
                    p.setIdProducto(rs.getInt("idProducto"));
                    p.setSku(rs.getString("sku"));
                    p.setNombre(rs.getString("nombre"));
                    p.setPrecio(rs.getString("precio"));   // <-- cambio: String
                    p.setStock(rs.getInt("stock_total"));  // usamos 'stock' para el total calculado
                    lista.add(p);
                }
            }
        }
        System.out.println("[ProductoDao] listarVisiblesPorProductor -> filas=" + lista.size());
        return lista;
    }

    /** Catálogo completo (útil para combos, etc.) */
    public List<Producto> listarPorProductor(int idProductor) throws SQLException {
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
                p.setPrecio(rs.getString("precio"));   // <-- cambio: String
                p.setStock(rs.getInt("stock"));
                lista.add(p);
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
                p.setPrecio(rs.getString("precio"));   // <-- cambio: String
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
                    p.setPrecio(rs.getString("precio"));  // <-- cambio: String
                    p.setStock(rs.getInt("stock"));
                    return p;
                }
            }
        }
        return null;
    }

    public void crear(Producto p) throws SQLException {
        String sql = "INSERT INTO Producto (sku, nombre, precio, stock) VALUES (?,?,?,?)";
        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, p.getSku());
            ps.setString(2, p.getNombre());
            ps.setString(3, p.getPrecio());  // <-- cambio: String
            ps.setInt(4, p.getStock());
            ps.executeUpdate();
        }
    }

    public void actualizar(Producto p) throws SQLException {
        String sql = "UPDATE Producto SET sku=?, nombre=?, precio=?, stock=? WHERE idProducto=?";
        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, p.getSku());
            ps.setString(2, p.getNombre());
            ps.setString(3, p.getPrecio());  // <-- cambio: String
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

    /**
     * Stock restante (Producto.stock - SUM(lotes del usuario)) para validar en LoteServlet.
     */
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
