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
        String url = "jdbc:mysql://127.0.0.1:3306/Bodega-Telito";

        // 🚨 CONSULTA SQL CORREGIDA: Usa subconsultas para obtener Lote y Zona ID del último movimiento 🚨
        String sql = "SELECT " +
                "    P.idProducto, P.sku, P.nombre, P.Stock_productoId, " +
                // SUBQUERY para obtener el ID del Lote (int) del último movimiento asociado a este Producto
                "    (SELECT Lote_idLote FROM Movimiento M " +
                "     INNER JOIN Lote L ON M.Lote_idLote = L.idLote " +
                "     WHERE L.Producto_idProducto = P.idProducto " +
                "     ORDER BY M.fecha DESC LIMIT 1) AS LoteID, " +
                // SUBQUERY para obtener el ID de la Zona (int) del último movimiento asociado a este Producto
                "    (SELECT Zonas_idZonas FROM Movimiento M " +
                "     INNER JOIN Lote L ON M.Lote_idLote = L.idLote " +
                "     WHERE L.Producto_idProducto = P.idProducto " +
                "     ORDER BY M.fecha DESC LIMIT 1) AS ZonaID " +
                "FROM Producto P " +
                "WHERE P.Stock_productoId <= P.stockMinimo " +
                "ORDER BY P.Stock_productoId ASC " +
                "LIMIT 5";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            try (Connection conn = DriverManager.getConnection(url, user, pass);
                 PreparedStatement pstmt = conn.prepareStatement(sql);
                 ResultSet rs = pstmt.executeQuery()) {

                while (rs.next()) {
                    Producto p = new Producto();

                    // Mapeo de datos principales
                    p.setIdProducto(rs.getInt("idProducto"));
                    p.setNombre(rs.getString("nombre"));
                    // ✅ CORREGIDO: Lee SKU como String
                    p.setSku(rs.getString("sku"));
                    p.setStock(rs.getInt("Stock_productoId"));

                    // Mapeo de IDs de Lote y Zona (usando los alias de la subconsulta)
                    Lote lote = new Lote();
                    lote.setIdLote(rs.getInt("LoteID"));
                    p.setLote(lote);
                    Zonas zona = new Zonas();
                    zona.setIdZonas(rs.getInt("ZonaID"));
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

        // Consulta SQL: Usa las columnas de tu DB (Stock_productoId y stockMinimo) para contar
        String sql = "SELECT COUNT(*) FROM Producto WHERE Stock_productoId <= stockMinimo";
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

    public ArrayList<Producto> obtenerListaProductos(String busquedaTermino, String ordenFiltro) {
        ArrayList<Producto> listaProductos = new ArrayList<>();
        String user = "root";
        String pass = "12345678";
        String url = "jdbc:mysql://127.0.0.1:3306/Bodega-Telito";

        // Modificamos la consulta para incluir las subconsultas de LoteID y ZonaID
        StringBuilder sql = new StringBuilder(
                "SELECT " +
                        "P.idProducto, P.sku, P.nombre, P.Stock_productoId AS stock, P.stockMinimo, " +

                        // INCLUIMOS SUBQUERY para obtener el ID del Lote (int) del último movimiento
                        "    (SELECT Lote_idLote FROM Movimiento M " +
                        "     INNER JOIN Lote L2 ON M.Lote_idLote = L2.idLote " +
                        "     WHERE L2.Producto_idProducto = P.idProducto " +
                        "     ORDER BY M.fecha DESC LIMIT 1) AS LoteID, " +

                        // INCLUIMOS SUBQUERY para obtener el ID de la Zona (int) del último movimiento
                        "    (SELECT Zonas_idZonas FROM Movimiento M " +
                        "     INNER JOIN Lote L3 ON M.Lote_idLote = L3.idLote " +
                        "     WHERE L3.Producto_idProducto = P.idProducto " +
                        "     ORDER BY M.fecha DESC LIMIT 1) AS ZonaID " +

                        "FROM Producto P " +
                        // Los JOINs se mantienen por si los necesitas para futuras consultas, pero no afectan la búsqueda actual
                        "LEFT JOIN Lote L ON L.Producto_idProducto = P.idProducto " +
                        "LEFT JOIN Usuarios U ON U.idUsuarios = L.Usuarios_idUsuarios " +
                        "WHERE 1=1 "
        );

        // 🚨 1. LÓGICA DE FILTRO POR BÚSQUEDA (SKU o NOMBRE) 🚨
        boolean hayBusqueda = (busquedaTermino != null && !busquedaTermino.isBlank());
        if (hayBusqueda) {
            // Filtramos por SKU o Nombre (ya que 'busquedaTermino' reemplazó al filtro de proveedor)
            sql.append("AND (P.sku LIKE ? OR P.nombre LIKE ?) ");
        }

        // 2. Ordenamiento
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

                // 🚨 SETEO DE PARÁMETROS DE BÚSQUEDA 🚨
                if (hayBusqueda) {
                    // Seteamos el mismo término de búsqueda dos veces, uno para SKU y otro para Nombre
                    pstmt.setString(paramIndex++, "%" + busquedaTermino.trim() + "%");
                    pstmt.setString(paramIndex++, "%" + busquedaTermino.trim() + "%");
                }

                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        Producto p = new Producto();

                        p.setIdProducto(rs.getInt("idProducto"));
                        p.setSku(rs.getString("sku"));
                        p.setNombre(rs.getString("nombre"));
                        p.setStock(rs.getInt("stock"));

                        // Mapeo de IDs de Lote y Zona
                        Lote  lote = new Lote();
                        lote.setIdLote(rs.getInt("LoteID"));
                        p.setLote(lote);
                        Zonas zona = new Zonas();
                        zona.setIdZonas(rs.getInt("ZonaID"));
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