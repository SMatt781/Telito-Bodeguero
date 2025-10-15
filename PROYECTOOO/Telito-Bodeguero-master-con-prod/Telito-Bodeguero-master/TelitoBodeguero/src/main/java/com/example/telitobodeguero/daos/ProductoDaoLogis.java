package com.example.telitobodeguero.daos;

import com.example.telitobodeguero.beans.Lote;
import com.example.telitobodeguero.beans.Producto;
import com.example.telitobodeguero.beans.Zonas;

import java.sql.*;
import java.util.ArrayList;

public class ProductoDaoLogis {
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
    public ArrayList<Producto> obtenerListaProductos(String busquedaTermino, String ordenFiltro) {
        ArrayList<Producto> listaProductos = new ArrayList<>();
        String user = "root";
        String pass = "12345678";
        String url  = "jdbc:mysql://127.0.0.1:3306/Bodega-Telito";

        // SQL: LoteID (último lote) + ZonaNombre (por usuario del lote; fallback por OC)
        StringBuilder sql = new StringBuilder(
                "SELECT " +
                        "  P.idProducto, P.sku, P.nombre, P.stock AS stock, P.stockMinimo, " +
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
                        "WHERE EXISTS ( " +
                        "  SELECT 1 FROM Lote Lx " +
                        "  WHERE Lx.Producto_idProducto = P.idProducto " +
                        "    AND Lx.ubicacion LIKE 'Almacén%' " +
                        ") "
        );

        // Filtro de búsqueda (SKU o Nombre)
        boolean hayBusqueda = (busquedaTermino != null && !busquedaTermino.isBlank());
        if (hayBusqueda) {
            sql.append("AND (P.sku LIKE ? OR P.nombre LIKE ?) ");
        }

        // Ordenamiento
        sql.append("ORDER BY ");
        if (ordenFiltro != null) {
            if (ordenFiltro.equalsIgnoreCase("stock_asc")) {
                sql.append("stock ASC");
            } else if (ordenFiltro.equalsIgnoreCase("stock_desc")) {
                sql.append("stock DESC");
            } else if (ordenFiltro.equalsIgnoreCase("nombre_desc")) {
                sql.append("P.nombre DESC");
            } else {
                sql.append("P.nombre ASC");
            }
        } else {
            sql.append("P.nombre ASC");
        }

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection conn = DriverManager.getConnection(url, user, pass);
                 PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {

                int paramIndex = 1;
                if (hayBusqueda) {
                    String like = "%" + busquedaTermino.trim() + "%";
                    pstmt.setString(paramIndex++, like);
                    pstmt.setString(paramIndex++, like);
                }

                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        Producto p = new Producto();
                        p.setIdProducto(rs.getInt("idProducto"));
                        p.setSku(rs.getString("sku"));
                        p.setNombre(rs.getString("nombre"));
                        p.setStock(rs.getInt("stock"));

                        // Evita ClassCastException (Long vs Integer)
                        Number loteNum = (Number) rs.getObject("LoteID");

                        Lote lote = new Lote();
                        if (loteNum != null) lote.setIdLote(loteNum.intValue());
                        p.setLote(lote);

                        // Zona: nombre directamente
                        String zonaNombre = rs.getString("ZonaNombre");
                        Zonas zona = new Zonas();
                        zona.setNombre(zonaNombre);  // <- usar nombre
                        p.setZona(zona);

                        listaProductos.add(p);
                    }
                }
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }

        return listaProductos;
    }
}
