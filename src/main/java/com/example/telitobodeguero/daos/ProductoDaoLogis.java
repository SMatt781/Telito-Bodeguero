package com.example.telitobodeguero.daos;

import com.example.telitobodeguero.beans.*;

import java.sql.*;
import java.util.ArrayList;

public class ProductoDaoLogis extends BaseDao {
    public ArrayList<Producto> obtenerTop5ProductosStockBajo() {
        ArrayList<Producto> listaProductos = new ArrayList<>();

        String user = "root";
        String pass = "12345678";
        String url  = "jdbc:mysql://127.0.0.1:3306/Bodega-Telito";

        // Top 5 por stock (<= stockMinimo), mostrando LoteID (último lote) y ZonaNombre
        String sql =
                "SELECT " +
                        "  P.idProducto, P.sku, P.nombre, P.stock, " +
                        "  (SELECT L.idLote " +
                        "     FROM Lote L " +
                        "    WHERE L.Producto_idProducto = P.idProducto " +
                        "    ORDER BY L.fechaVencimiento DESC, L.idLote DESC " +
                        "    LIMIT 1) AS LoteID, " +
                        "  COALESCE( " +
                        "    (SELECT Z.nombre " +
                        "       FROM Lote L " +
                        "       JOIN Usuarios  U ON U.idUsuarios = L.Usuarios_idUsuarios " +
                        "       JOIN Distritos D ON D.idDistritos = U.Distritos_idDistritos " +
                        "       JOIN Zonas     Z ON Z.idZonas     = D.Zonas_idZonas " +
                        "      WHERE L.Producto_idProducto = P.idProducto " +
                        "      ORDER BY L.fechaVencimiento DESC, L.idLote DESC " +
                        "      LIMIT 1), " +
                        "    (SELECT Z2.nombre " +
                        "       FROM OrdenCompraItem OCI " +
                        "       JOIN OrdenCompra   OC  ON OC.idOrdenCompra = OCI.OrdenCompra_idOrdenCompra " +
                        "       JOIN Zonas         Z2  ON Z2.idZonas       = OC.Zonas_idZonas " +
                        "      WHERE OCI.Producto_idProducto = P.idProducto " +
                        "        AND OC.Zonas_idZonas IS NOT NULL " +
                        "      ORDER BY OC.fecha_llegada DESC, OC.idOrdenCompra DESC " +
                        "      LIMIT 1) " +
                        "  ) AS ZonaNombre " +
                        "FROM Producto P " +
                        "WHERE P.stock <= P.stockMinimo " +
                        "ORDER BY P.stock ASC " +
                        "LIMIT 5";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection conn = DriverManager.getConnection(url, user, pass);
                 PreparedStatement pstmt = conn.prepareStatement(sql);
                 ResultSet rs = pstmt.executeQuery()) {

                while (rs.next()) {
                    Producto p = new Producto();
                    p.setIdProducto(rs.getInt("idProducto"));
                    p.setSku(rs.getString("sku"));
                    p.setNombre(rs.getString("nombre"));
                    p.setStock(rs.getInt("stock"));

                    // LoteID puede venir como Long: usar Number para evitar ClassCastException
                    Number loteNum = (Number) rs.getObject("LoteID");
                    Lote lote = new Lote();
                    if (loteNum != null) lote.setIdLote(loteNum.intValue());
                    p.setLote(lote);

                    // Nombre de la zona directamente
                    String zonaNombre = rs.getString("ZonaNombre");
                    Zonas zona = new Zonas();
                    zona.setNombre(zonaNombre);   // usar nombre, no id
                    p.setZona(zona);

                    listaProductos.add(p);
                }
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }

        return listaProductos;
    }
    public int contarTotalProductosStockBajo() {
        String user = "root";
        String pass = "12345678";
        String url = "jdbc:mysql://127.0.0.1:3306/Bodega-Telito";

        // ✅ CORREGIDO: Cambiado Stock_productoId por stock
        String sql = "SELECT COUNT(*) FROM Producto WHERE stock <= stockMinimo";
        int total = 0;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            try (Connection conn = DriverManager.getConnection(url, user, pass);
                 PreparedStatement pstmt = conn.prepareStatement(sql);
                 ResultSet rs = pstmt.executeQuery()) {

                if (rs.next()) {
                    total = rs.getInt(1);
                }
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        return total;
    }

    // 2. MÉTODO CORREGIDO: obtenerListaProductos()
    public ArrayList<Producto> obtenerListaProductos() {
        ArrayList<Producto> listaProductos = new ArrayList<>();

        String sql =
                "SELECT p.idProducto AS IdProducto, " +
                        "       p.sku AS sku, " +
                        "       p.nombre AS nombre_producto, " +
                        "       z.nombre AS zona, " +
                        "       SUM(CASE " +
                        "           WHEN m.tipo = 'IN'  THEN m.cantidad " +
                        "           WHEN m.tipo = 'OUT' THEN -m.cantidad " +
                        "           ELSE 0 END) AS stock_total_zona " +
                        "FROM movimiento m " +
                        "INNER JOIN lote l ON m.Lote_idLote = l.idLote " +
                        "INNER JOIN producto p ON l.Producto_idProducto = p.idProducto " +
                        "INNER JOIN zonas z ON m.Zonas_idZonas = z.idZonas " +
                        "GROUP BY p.idProducto, p.sku, p.nombre, z.idZonas, z.nombre";

        try (Connection conn = this.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {

                Producto producto = new Producto();
                producto.setIdProducto(rs.getInt("IdProducto"));
                producto.setSku(rs.getString("sku"));
                producto.setNombre(rs.getString("nombre_producto"));

                // acá usamos stock del bean Producto para guardar stock_total_zona calculado
                producto.setStock(rs.getInt("stock_total_zona"));

                // zona
                Zonas zona = new Zonas();
                zona.setNombre(rs.getString("zona"));
                producto.setZona(zona);

                // lote: en este nivel agregado no hay un lote único claro -> lo dejamos null
                producto.setLote(null);

                listaProductos.add(producto);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return listaProductos;
    }

}
