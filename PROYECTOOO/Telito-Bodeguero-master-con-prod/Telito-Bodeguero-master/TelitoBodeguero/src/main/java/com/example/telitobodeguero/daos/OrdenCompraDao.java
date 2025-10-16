package com.example.telitobodeguero.daos;

import com.example.telitobodeguero.beans.*;
import com.example.telitobodeguero.beans.OrdenCompra;
import com.example.telitobodeguero.beans.Producto;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


public class OrdenCompraDao {

    // -------------------------------------------------------------------------
    // 1. OBTENER ORDENES DE COMPRA (Listar y Mapear Zona)
    // -------------------------------------------------------------------------
    public ArrayList<OrdenCompra> obtenerOrdenCompra(String estadoFiltro, String terminoBusquedaProveedor) {
        ArrayList<OrdenCompra> listaOrdenCompra = new ArrayList<>();
        String user = "root";
        String pass = "12345678";
        String url  = "jdbc:mysql://127.0.0.1:3306/Bodega-Telito";

        StringBuilder sql = new StringBuilder(
                "SELECT " +
                        "  oc.idOrdenCompra AS CodigoOrdenCompra, " +
                        "  CONCAT(u.nombre,' ',u.apellido) AS Proveedor, " +
                        "  p.nombre AS Producto, " +
                        "  oci.cantidad AS Cantidad, " +
                        "  oc.fecha_llegada AS FechaLlegada, " +
                        "  oc.estado AS Estado, " +
                        "  z.nombre AS NombreZona " + // <-- Nueva columna
                        "FROM OrdenCompra oc " +
                        "JOIN OrdenCompraItem oci ON oc.idOrdenCompra = oci.OrdenCompra_idOrdenCompra " +
                        "JOIN Producto p          ON oci.Producto_idProducto = p.idProducto " +
                        // Subconsulta: tomamos 1 solo lote (el más reciente por id) para cada producto
                        "JOIN ( " +
                        "   SELECT Producto_idProducto, MAX(idLote) AS idLote " +
                        "   FROM Lote " +
                        "   GROUP BY Producto_idProducto " +
                        ") lmax ON lmax.Producto_idProducto = p.idProducto " +
                        "JOIN Lote l ON l.idLote = lmax.idLote " +
                        "JOIN Usuarios u ON u.idUsuarios = l.Usuarios_idUsuarios " +
                        "LEFT JOIN Zonas z ON oc.Zonas_idZonas = z.idZonas " + // <-- Nuevo JOIN
                        "WHERE u.Roles_idRoles = 4 " // solo productores
        );

        boolean hayEstado = (estadoFiltro != null && !estadoFiltro.isBlank());
        if (hayEstado) {
            sql.append(" AND oc.estado = ? ");
        }

        boolean hayFiltroProveedor = (terminoBusquedaProveedor != null && !terminoBusquedaProveedor.isBlank());
        if (hayFiltroProveedor) {
            sql.append(" AND (u.nombre LIKE ? OR u.apellido LIKE ?) ");
        }

        sql.append(" ORDER BY oc.fecha_llegada ASC, oc.idOrdenCompra DESC");

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection conn = DriverManager.getConnection(url, user, pass);
                 PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {

                int paramIndex = 1;
                if (hayEstado) {
                    pstmt.setString(paramIndex++, estadoFiltro.trim());
                }
                if (hayFiltroProveedor) {
                    String filtro = "%" + terminoBusquedaProveedor.trim() + "%";
                    pstmt.setString(paramIndex++, filtro);
                    pstmt.setString(paramIndex++, filtro);
                }

                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        OrdenCompra oc = new OrdenCompra();
                        oc.setCodigoOrdenCompra(rs.getInt("CodigoOrdenCompra"));
                        oc.setNombreProveedor(rs.getString("Proveedor"));

                        Producto p = new Producto();
                        p.setNombre(rs.getString("Producto"));
                        oc.setProducto(p);

                        oc.setFechaLlegada(rs.getObject("FechaLlegada", LocalDate.class));

                        oc.setCantidad(rs.getInt("Cantidad"));
                        oc.setEstado(rs.getString("Estado"));

                        // Mapeo de la nueva Zona
                        Zonas z = new Zonas();
                        z.setNombre(rs.getString("NombreZona"));
                        oc.setZona(z);

                        listaOrdenCompra.add(oc);
                    }
                }
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        return listaOrdenCompra;
    }


    // -------------------------------------------------------------------------
    // 2. BORRAR ORDEN
    // -------------------------------------------------------------------------
    public void borrarOrden(int idOrden) {
        // [CÓDIGO DE borrarOrden se mantiene igual]
        String user = "root";
        String pass = "12345678";
        String url  = "jdbc:mysql://127.0.0.1:3306/Bodega-Telito";

        String delItems = "DELETE FROM OrdenCompraItem WHERE OrdenCompra_idOrdenCompra = ?";
        String delOrden = "DELETE FROM OrdenCompra WHERE idOrdenCompra = ?";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection conn = DriverManager.getConnection(url, user, pass)) {
                conn.setAutoCommit(false);
                try (PreparedStatement p1 = conn.prepareStatement(delItems);
                     PreparedStatement p2 = conn.prepareStatement(delOrden)) {

                    p1.setInt(1, idOrden);
                    p1.executeUpdate();

                    p2.setInt(1, idOrden);
                    p2.executeUpdate();

                    conn.commit();
                } catch (SQLException ex) {
                    conn.rollback();
                    throw ex;
                } finally {
                    conn.setAutoCommit(true);
                }
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    // -------------------------------------------------------------------------
    // 3. CREAR ORDEN (Se añade idZona)
    // -------------------------------------------------------------------------
    public void crearOrden(int idProducto, int cantidad, String fechaLlegada, int idZona) { // <-- NUEVO PARAMETRO
        String user = "root";
        String pass = "12345678";
        String url  = "jdbc:mysql://127.0.0.1:3306/Bodega-Telito";

        // SQL: Se añade Zonas_idZonas a la inserción
        String sqlOrden = "INSERT INTO OrdenCompra (estado, fecha_llegada, Zonas_idZonas) VALUES ('Enviada', ?, ?)";
        String sqlItem  = "INSERT INTO OrdenCompraItem (OrdenCompra_idOrdenCompra, Producto_idProducto, cantidad) VALUES (?, ?, ?)";

        Connection conn = null;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(url, user, pass);
            conn.setAutoCommit(false);

            int idGenerado = -1;
            try (PreparedStatement pstmtOrden = conn.prepareStatement(sqlOrden, Statement.RETURN_GENERATED_KEYS)) {

                pstmtOrden.setString(1, fechaLlegada);
                pstmtOrden.setInt(2, idZona); // <-- Seteamos el ID de la Zona
                pstmtOrden.executeUpdate();

                try (ResultSet rs = pstmtOrden.getGeneratedKeys()) {
                    if (rs.next()) idGenerado = rs.getInt(1);
                }
            }

            if (idGenerado != -1) {
                try (PreparedStatement pstmtItem = conn.prepareStatement(sqlItem)) {
                    pstmtItem.setInt(1, idGenerado);
                    pstmtItem.setInt(2, idProducto);
                    pstmtItem.setInt(3, cantidad);
                    pstmtItem.executeUpdate();
                }
            }

            conn.commit();

        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
            throw new RuntimeException("Error al crear orden: " + e.getMessage(), e);
        } finally {
            if (conn != null) try { conn.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
    }


    // -------------------------------------------------------------------------
    // 4. OBTENER PRODUCTOS (Para form_crear)
    // -------------------------------------------------------------------------
    public ArrayList<Producto> obtenerProductos() {
        // [CÓDIGO DE obtenerProductos se mantiene igual]
        ArrayList<Producto> listaProductos = new ArrayList<>();
        String user = "root";
        String pass = "12345678";
        String url  = "jdbc:mysql://127.0.0.1:3306/Bodega-Telito";

        String sql = "SELECT idProducto, nombre FROM Producto";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection conn = DriverManager.getConnection(url, user, pass);
                 PreparedStatement pstmt = conn.prepareStatement(sql);
                 ResultSet rs = pstmt.executeQuery()) {

                while (rs.next()) {
                    Producto p = new Producto();
                    p.setIdProducto(rs.getInt("idProducto"));
                    p.setNombre(rs.getString("nombre"));
                    listaProductos.add(p);
                }
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        return listaProductos;
    }

    // -------------------------------------------------------------------------
    // 5. OBTENER PROVEEDOR ID POR PRODUCTO
    // -------------------------------------------------------------------------
    public int obtenerProveedorIdPorProducto(int idProducto) {
        // [CÓDIGO DE obtenerProveedorIdPorProducto se mantiene igual]
        String user = "root";
        String pass = "12345678";
        String url  = "jdbc:mysql://127.0.0.1:3306/Bodega-Telito";
        int idProveedor = -1;

        // Buscamos el ID del Usuario (Proveedor) que tiene el producto en algún Lote
        String sql = "SELECT DISTINCT l.Usuarios_idUsuarios " +
                "FROM Lote l " +
                "WHERE l.Producto_idProducto = ?";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection conn = DriverManager.getConnection(url, user, pass);
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {

                pstmt.setInt(1, idProducto);

                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        idProveedor = rs.getInt("Usuarios_idUsuarios");
                    }
                }
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        return idProveedor;
    }

    // -------------------------------------------------------------------------
    // 6. CONTAR PRODUCTOS EN TRÁNSITO
    // -------------------------------------------------------------------------
    public int contarOrdenesEnTransito() {
        String user = "root";
        String pass = "12345678";
        String url  = "jdbc:mysql://127.0.0.1:3306/Bodega-Telito";

        // ✅ La condición clave: Contar órdenes cuya columna 'estado' es 'Enviada'
        String sql = "SELECT COUNT(*) FROM OrdenCompra WHERE estado = 'En tránsito'";
        int total = 0;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            try (Connection conn = DriverManager.getConnection(url, user, pass);
                 PreparedStatement pstmt = conn.prepareStatement(sql);
                 ResultSet rs = pstmt.executeQuery()) {

                if (rs.next()) {
                    total = rs.getInt(1); // El COUNT(*) siempre es la primera columna
                }
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        return total;
    }

    // -------------------------------------------------------------------------
    // 7. OBTENER LISTA DE ZONAS (NUEVO MÉTODO)
    // -------------------------------------------------------------------------
    /**
     * Obtiene todas las zonas de la tabla Zonas para el dropdown de creación.
     */
    public ArrayList<Zonas> obtenerListaZonas() {
        ArrayList<Zonas> listaZonas = new ArrayList<>();
        String user = "root";
        String pass = "12345678";
        String url  = "jdbc:mysql://127.0.0.1:3306/Bodega-Telito";

        String sql = "SELECT idZonas, nombre FROM Zonas ORDER BY nombre";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection conn = DriverManager.getConnection(url, user, pass);
                 PreparedStatement pstmt = conn.prepareStatement(sql);
                 ResultSet rs = pstmt.executeQuery()) {

                while (rs.next()) {
                    Zonas z = new Zonas();
                    z.setIdZonas(rs.getInt("idZonas"));
                    z.setNombre(rs.getString("nombre"));
                    listaZonas.add(z);
                }
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        return listaZonas;
    }


    public List<OrdenCompra> listarOCConItemsParaProductor(int idProductor) throws SQLException {
        String sql = """
            SELECT 
                   oc.idOrdenCompra AS oc,
                   oc.estado,
                   oc.fecha_llegada,
                   oci.cantidad,
                   p.idProducto AS idProducto,
                   p.sku,
                   p.nombre AS producto
            FROM OrdenCompra oc
            JOIN OrdenCompraItem oci 
                 ON oci.OrdenCompra_idOrdenCompra = oc.idOrdenCompra
            JOIN Producto p 
                 ON p.idProducto = oci.Producto_idProducto
            WHERE EXISTS (
                SELECT 1 
                FROM Lote l
                WHERE l.Producto_idProducto = p.idProducto
                  AND l.Usuarios_idUsuarios = ?
            )
            ORDER BY oc.fecha_llegada, oc.idOrdenCompra, oci.idItem
            """;

        List<OrdenCompra> lista = new ArrayList<>();
        // Asumiendo que DB.getConnection() te proporciona una conexión válida
        // Nota: Esta parte del código no usa los parámetros user, pass, url definidos al inicio de la clase.
        // Si tienes una clase DB, asegúrate de que funcione o cámbiala por DriverManager.getConnection(url, user, pass)
        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, idProductor);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    OrdenCompra r = new OrdenCompra();

                    // Bean OrdenCompra
                    r.setCodigoOrdenCompra(rs.getInt("oc"));
                    r.setEstado(rs.getString("estado"));

                    Date f = rs.getDate("fecha_llegada");
                    r.setFechaLlegada(f == null ? null : f.toLocalDate());

                    r.setCantidad(rs.getInt("cantidad"));

                    // Producto anidado (objeto)
                    Producto prod = new Producto();
                    prod.setIdProducto(rs.getInt("idProducto"));
                    prod.setSku(rs.getString("sku"));
                    prod.setNombre(rs.getString("producto"));
                    r.setProducto(prod); // usa setProducto(Producto)

                    // Aquí no se mapea la Zona porque esta consulta es para la vista del Productor
                    // y el requerimiento usualmente no incluye la zona en esa vista.
                    // Si la necesitaras, deberías agregarla al SQL y mapearla aquí.

                    lista.add(r);
                }
            }
        }
        return lista;
    }

}
