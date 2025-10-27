package com.example.telitobodeguero.daos;

import com.example.telitobodeguero.beans.*;
import com.example.telitobodeguero.beans.OrdenCompra;
import com.example.telitobodeguero.beans.Producto;
import com.example.telitobodeguero.beans.Zonas; // Asegúrate de tener este import
import com.example.telitobodeguero.beans.Usuarios; // Asegúrate de tener este import

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


public class OrdenCompraDao{


    // NOTA: Se asume que en una implementación real, estas credenciales estarían en un archivo de configuración.
    private static final String USER = "root";
    private static final String PASS = "12345678";
    private static final String URL = "jdbc:mysql://127.0.0.1:3306/bodega-telito";


    // Método auxiliar para obtener la conexión (reemplaza BaseDao.getConnection() si no heredas)
    private Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("Driver JDBC no encontrado", e);
        }
        return DriverManager.getConnection(URL, USER, PASS);
    }

    public Connection getOpenConnection() throws SQLException {
        return getConnection();
    }


    // ================================================================
    // 1. OBTENER ORDENES DE COMPRA (Logística)
    // ================================================================
    public ArrayList<OrdenCompra> obtenerOrdenCompra(String estadoFiltro, String terminoBusquedaProveedor) {
        ArrayList<OrdenCompra> listaOrdenCompra = new ArrayList<>();

        // Consulta SQL basada en el Query provisto por el usuario,
        // ajustando los alias de columna para coincidir con el código Java (e.g., Proveedor, NombreZona)
        StringBuilder sql = new StringBuilder(
                "SELECT " +
                        "  oc.idOrdenCompra AS CodigoOrdenCompra, " + // Mapea a CodigoOrdenCompra
                        "  CONCAT(u.nombre, ' ', u.apellido) AS Proveedor, " +
                        "  p.nombre AS Producto, " +
                        "  z.nombre AS NombreZona, " + // Mapea a NombreZona
                        "  oc.fecha_llegada AS FechaLlegada, " +
                        "  oci.cantidad AS Cantidad, " +
                        "  oc.estado AS Estado " +
                        "FROM ordencompra oc " +
                        "INNER JOIN ordencompraitem oci ON oc.idOrdenCompra = oci.OrdenCompra_idOrdenCompra " +
                        "INNER JOIN producto p ON oci.Producto_idProducto = p.idProducto " +
                        "INNER JOIN usuarios u ON oc.idProveedor = u.idUsuarios " + // USANDO oc.idProveedor (CORRECTO)
                        "INNER JOIN zonas z ON oc.Zonas_idZonas = z.idZonas " +
                        "WHERE 1=1 "
        );

        // Lógica de filtrado
        if (estadoFiltro != null && !estadoFiltro.isEmpty() && !"Todos".equalsIgnoreCase(estadoFiltro)) {
            sql.append("AND oc.estado = ? ");
        }

        if (terminoBusquedaProveedor != null && !terminoBusquedaProveedor.trim().isEmpty()) {
            // Usamos el mismo CONCAT que en el SELECT para filtrar
            sql.append("AND CONCAT(u.nombre,' ',u.apellido) LIKE ? ");
        }

        sql.append("ORDER BY oc.fecha_llegada DESC, oc.idOrdenCompra DESC");

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            int index = 1;
            if (estadoFiltro != null && !estadoFiltro.isEmpty() && !"Todos".equalsIgnoreCase(estadoFiltro)) {
                ps.setString(index++, estadoFiltro);
            }
            if (terminoBusquedaProveedor != null && !terminoBusquedaProveedor.trim().isEmpty()) {
                ps.setString(index++, "%" + terminoBusquedaProveedor.trim() + "%");
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    OrdenCompra oc = new OrdenCompra();
                    oc.setCodigoOrdenCompra(rs.getInt("CodigoOrdenCompra"));
                    oc.setNombreProveedor(rs.getString("Proveedor"));

                    Producto producto = new Producto();
                    producto.setNombre(rs.getString("Producto"));
                    oc.setProducto(producto);

                    Zonas zona = new Zonas();
                    zona.setNombre(rs.getString("NombreZona")); // Se lee NombreZona
                    oc.setZona(zona);

                    oc.setFechaLlegada(rs.getDate("FechaLlegada") != null ? rs.getDate("FechaLlegada").toLocalDate() : null);
                    oc.setCantidad(rs.getInt("Cantidad"));
                    oc.setEstado(rs.getString("Estado"));

                    listaOrdenCompra.add(oc);
                }
            }
        } catch (SQLException e) {
            // Mensaje de error mejorado para debug
            System.err.println("Error en obtenerOrdenCompra: " + e.getMessage());
            e.printStackTrace();
        }
        return listaOrdenCompra;
    }


    // ================================================================
    // 2. BORRAR ORDEN
    // ================================================================
    public void borrarOrden(int idOrden) {
        String delItems = "DELETE FROM OrdenCompraItem WHERE OrdenCompra_idOrdenCompra = ?";
        String delOrden = "DELETE FROM OrdenCompra WHERE idOrdenCompra = ?";
        Connection conn = null;

        try {
            conn = getConnection();
            conn.setAutoCommit(false);
            try (PreparedStatement p1 = conn.prepareStatement(delItems);
                 PreparedStatement p2 = conn.prepareStatement(delOrden)) {

                p1.setInt(1, idOrden);
                p1.executeUpdate();

                p2.setInt(1, idOrden);
                p2.executeUpdate();

                conn.commit();
            } catch (SQLException ex) {
                if (conn != null) conn.rollback();
                throw ex;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al borrar orden de compra: " + e.getMessage(), e);
        } finally {
            if (conn != null) try { conn.setAutoCommit(true); conn.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
    }

    // ================================================================
    // 3. CREAR ORDEN (con idProveedor)
    // ================================================================
    public void crearOrden(int idProducto, int cantidad, String fechaLlegada, int idZona, int idProveedor) {
        String sqlOrden = "INSERT INTO ordencompra (estado, fecha_llegada, Zonas_idZonas, idProveedor) VALUES ('Enviada', ?, ?, ?)";
        String sqlItem  = "INSERT INTO OrdenCompraItem (OrdenCompra_idOrdenCompra, Producto_idProducto, cantidad) VALUES (?, ?, ?)";
        Connection conn = null;

        try {
            conn = getConnection();
            conn.setAutoCommit(false);

            int idGenerado = -1;

            // 1. Insertar en ordencompra
            try (PreparedStatement pstmtOrden = conn.prepareStatement(sqlOrden, Statement.RETURN_GENERATED_KEYS)) {
                pstmtOrden.setString(1, fechaLlegada);
                pstmtOrden.setInt(2, idZona);
                pstmtOrden.setInt(3, idProveedor);
                pstmtOrden.executeUpdate();

                try (ResultSet rs = pstmtOrden.getGeneratedKeys()) {
                    if (rs.next()) idGenerado = rs.getInt(1);
                }
            }

            // 2. Insertar en OrdenCompraItem
            if (idGenerado != -1) {
                try (PreparedStatement pstmtItem = conn.prepareStatement(sqlItem)) {
                    pstmtItem.setInt(1, idGenerado);
                    pstmtItem.setInt(2, idProducto);
                    pstmtItem.setInt(3, cantidad);
                    pstmtItem.executeUpdate();
                }
            }

            conn.commit();

        } catch (SQLException e) {
            e.printStackTrace();
            if (conn != null) try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            throw new RuntimeException("Error al crear orden: " + e.getMessage(), e);
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    // ================================================================
    // 4. OBTENER PRODUCTOS
    // ================================================================
    public ArrayList<Producto> obtenerProductos() {
        ArrayList<Producto> listaProductos = new ArrayList<>();
        String sql = "SELECT idProducto, nombre FROM Producto";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Producto p = new Producto();
                p.setIdProducto(rs.getInt("idProducto"));
                p.setNombre(rs.getString("nombre"));
                listaProductos.add(p);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return listaProductos;
    }


    // ================================================================
    // 5. OBTENER PROVEEDOR POR PRODUCTO
    // ================================================================
    public int obtenerProveedorIdPorProducto(int idProducto) {
        int idProveedor = -1;
        String sql = "SELECT DISTINCT l.Usuarios_idUsuarios FROM Lote l WHERE l.Producto_idProducto = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idProducto);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) idProveedor = rs.getInt("Usuarios_idUsuarios");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return idProveedor;
    }

    // ================================================================
    // 6. CONTAR EN TRÁNSITO
    // ================================================================
    public int contarOrdenesEnTransito() {
        String sql = "SELECT COUNT(*) FROM OrdenCompra WHERE estado = 'En tránsito'";
        int total = 0;
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) total = rs.getInt(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return total;
    }


    // ================================================================
    // 7. LISTAR ZONAS
    // ================================================================
    public ArrayList<Zonas> obtenerListaZonas() {
        ArrayList<Zonas> listaZonas = new ArrayList<>();
        String sql = "SELECT idZonas, nombre FROM Zonas ORDER BY nombre";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Zonas z = new Zonas();
                z.setIdZonas(rs.getInt("idZonas"));
                z.setNombre(rs.getString("nombre"));
                listaZonas.add(z);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return listaZonas;
    }


    // ================================================================
    // 8. LISTAR ORDENES PARA PRODUCTOR (MODIFICADO: incluye idItem)
    // ================================================================
    public List<OrdenCompra> listarOCConItemsParaProductor(int idProductor) throws SQLException {
        String sql = """
         SELECT 
             oc.idOrdenCompra AS idOrdenCompra,
             oc.estado,
             oc.fecha_llegada,
             oci.idItem AS idItem,
             oci.cantidad,
             oci.lote_idLote,
             p.idProducto AS idProducto,
             p.sku,
             p.nombre AS producto
         FROM OrdenCompra oc
         JOIN OrdenCompraItem oci 
             ON oci.OrdenCompra_idOrdenCompra = oc.idOrdenCompra
         JOIN Producto p 
             ON p.idProducto = oci.Producto_idProducto
         WHERE 
             oc.estado IN ('Enviada', 'Recibido', 'En tránsito')
             AND oc.idProveedor = ?  -- ← FILTRO PRINCIPAL: Solo órdenes del proveedor
             AND EXISTS (  -- ← VALIDACIÓN OPCIONAL: Que el producto exista en sus lotes
                 SELECT 1 
                 FROM Lote l
                 WHERE l.Producto_idProducto = p.idProducto
                 AND l.Usuarios_idUsuarios = ?
             )
         ORDER BY oc.fecha_llegada, oc.idOrdenCompra, oci.idItem
         """;

        List<OrdenCompra> lista = new ArrayList<>();

        try (Connection c = getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, idProductor);  // Para oc.idProveedor
            ps.setInt(2, idProductor);  // Para el EXISTS de lotes
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    OrdenCompra r = new OrdenCompra();
                    r.setCodigoOrdenCompra(rs.getInt("idOrdenCompra"));
                    r.setEstado(rs.getString("estado"));
                    Date f = rs.getDate("fecha_llegada");
                    r.setFechaLlegada(f == null ? null : f.toLocalDate());
                    r.setCantidad(rs.getInt("cantidad"));
                    r.setIdItem(rs.getInt("idItem"));

                    // Mapear si ya tiene lote asignado
                    int loteId = rs.getInt("lote_idLote");
                    if (!rs.wasNull()) {
                        Lote lote = new Lote();
                        lote.setIdLote(loteId);
                        r.setLote(lote);
                    }

                    Producto prod = new Producto();
                    prod.setIdProducto(rs.getInt("idProducto"));
                    prod.setSku(rs.getString("sku"));
                    prod.setNombre(rs.getString("producto"));
                    r.setProducto(prod);

                    lista.add(r);
                }
            }
        }
        return lista;
    }

    // =========================================================================
// OBTENER PRODUCTORES POR PRODUCTO -> NO SE SI ELIMAR ESTO
// =========================================================================

    public ArrayList<Usuarios> obtenerProductoresPorProducto(int idProducto) {

        ArrayList<Usuarios> lista = new ArrayList<>();

        String user = "root";
        String pass = "12345678";
        String url = "jdbc:mysql://127.0.0.1:3306/bodega-telito"; // Usando la URL de arriba

        String sql =
                "SELECT DISTINCT u.idUsuarios, u.nombre, u.apellido " +
                        "FROM `lote` l " +
                        "JOIN `usuarios` u ON u.idUsuarios = l.Usuarios_idUsuarios " +
                        "WHERE l.Producto_idProducto = ? " +
                        "AND u.Roles_idRoles = 4 " +
                        "AND u.activo = 1";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            try (Connection conn = DriverManager.getConnection(url, user, pass);
                 PreparedStatement ps = conn.prepareStatement(sql)) {

                ps.setInt(1, idProducto);

                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        Usuarios prod = new Usuarios();
                        prod.setIdUsuarios(rs.getInt("idUsuarios"));
                        prod.setNombre(rs.getString("nombre"));
                        prod.setApellido(rs.getString("apellido"));
                        lista.add(prod);
                    }
                }
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }



    // =========================================================================
    // MÉTODO CORREGIDO 1: OBTENER PRODUCTOS POR ZONA
    // =========================================================================
    public ArrayList<Producto> obtenerProductosPorZona(int idZona) {
        ArrayList<Producto> lista = new ArrayList<>();

        // CORRECCIÓN: Usamos la relación Productor-Distrito-Zona para saber qué Productos
        // pueden ser ofrecidos por Productores en esa Zona, sin depender de la tabla 'movimiento'.
        String sql = """
            SELECT DISTINCT p.idProducto, p.nombre, p.sku, p.precio
            FROM producto p
            JOIN lote l ON p.idProducto = l.Producto_idProducto
            JOIN usuarios u ON u.idUsuarios = l.Usuarios_idUsuarios
            JOIN distritos d ON d.idDistritos = u.Distritos_idDistritos
            JOIN zonas z ON z.idZonas = d.Zonas_idZonas
            WHERE z.idZonas = ?
            AND u.Roles_idRoles = 4 -- Asegura que solo sean productores
            ORDER BY p.nombre ASC
            """;

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idZona);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Producto p = new Producto();
                    p.setIdProducto(rs.getInt("idProducto"));
                    p.setNombre(rs.getString("nombre"));
                    p.setSku(rs.getString("sku"));
                    p.setPrecio(rs.getDouble("precio"));
                    lista.add(p);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error en obtenerProductosPorZona: " + e.getMessage());
            e.printStackTrace();
        }

        return lista;
    }


    // =========================================================================
    // OBTENER PRODUCTORES POR PRODUCTO Y ZONA
    // =========================================================================
    public ArrayList<Usuarios> obtenerProductoresPorProductoYZona(int idProducto, int idZona) {
        ArrayList<Usuarios> lista = new ArrayList<>();

        // Consulta que filtra por Producto (lote) Y por Zona (a través de la ubicación del productor)
        String sql = """
            SELECT DISTINCT u.idUsuarios, u.nombre, u.apellido
            FROM lote l
            JOIN usuarios u ON l.Usuarios_idUsuarios = u.idUsuarios
            JOIN distritos d ON d.idDistritos = u.Distritos_idDistritos
            JOIN zonas z ON z.idZonas = d.Zonas_idZonas
            WHERE l.Producto_idProducto = ?
            AND z.idZonas = ?
            AND u.Roles_idRoles = 4
            AND u.activo = 1
            """;

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idProducto);
            ps.setInt(2, idZona);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Usuarios prod = new Usuarios();
                    prod.setIdUsuarios(rs.getInt("idUsuarios"));
                    prod.setNombre(rs.getString("nombre"));
                    prod.setApellido(rs.getString("apellido"));
                    lista.add(prod);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error en obtenerProductoresPorProductoYZona: " + e.getMessage());
            e.printStackTrace();
        }
        return lista;
    }

    // ================================================================
    // 9. ACTUALIZAR ESTADO
    // ================================================================
    public void actualizarEstadoSimple(int idOrden, String nuevoEstado) throws SQLException {
        String sql = "UPDATE OrdenCompra SET estado = ? WHERE idOrdenCompra = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nuevoEstado);
            ps.setInt(2, idOrden);
            ps.executeUpdate();
        }
    }

    // ================================================================
    // 10. (ELIMINADO / OBSOLETO) ASIGNAR LOTE Y CAMBIAR ESTADO
    // ================================================================
    /* // Mantenemos este espacio para el índice. El método es obsoleto porque trabaja con idOrdenCompra, no con idItem.
     */


    // ==========================================================
    // 11. NUEVO: Obtiene Detalle para Modal de Despacho (USADO EN doGet)
    // ==========================================================
    /**
     * Obtiene los datos del Ítem de la Orden (cantidad, producto, idItem)
     * junto a datos clave de la Orden (proveedor, idOrdenCompra) para el modal.
     */
    public OrdenCompra obtenerDetalleParaDespacho(int idItem) throws SQLException {
        OrdenCompra oc = null;

        String sql = "SELECT oci.idItem, oc.idOrdenCompra, oc.estado, oc.fecha_llegada, " +
                "p.nombre AS nombreProd, oci.cantidad, oci.Producto_idProducto, " +
                "u.nombre, u.apellido " +
                "FROM ordencompraitem oci " +
                "INNER JOIN ordencompra oc ON oci.OrdenCompra_idOrdenCompra = oc.idOrdenCompra " +
                "INNER JOIN producto p ON oci.Producto_idProducto = p.idProducto " +
                "INNER JOIN usuarios u ON oc.idProveedor = u.idUsuarios " +
                "WHERE oci.idItem = ?";

        try (Connection conn = this.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idItem);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    oc = new OrdenCompra();

                    // Llenar campos de Encabezado
                    oc.setCodigoOrdenCompra(rs.getInt("idOrdenCompra"));
                    oc.setEstado(rs.getString("estado"));
                    oc.setFechaLlegada(rs.getDate("fecha_llegada").toLocalDate());
                    oc.setNombreProveedor(rs.getString("nombre") + " " + rs.getString("apellido"));

                    // CLAVE: El idItem del detalle
                    oc.setIdItem(rs.getInt("idItem"));

                    // Llenar campos de Detalle
                    oc.setCantidad(rs.getInt("cantidad"));

                    // Configurar el Bean Producto
                    Producto p = new Producto();
                    p.setIdProducto(rs.getInt("Producto_idProducto"));
                    p.setNombre(rs.getString("nombreProd"));
                    oc.setProducto(p);
                }
            }
        }
        return oc;
    }


    // ==========================================================
    // 12. NUEVO: Asigna Lote al Detalle (USADO EN doPost - Transacción)
    // ==========================================================
    /**
     * Asigna el lote seleccionado a un ítem de la orden, debe usarse dentro de una transacción.
     * @param conn Conexión abierta y con AutoCommit=false.
     * @param idItem ID del detalle de la orden (ordencompraitem.idItem).
     * @param idLote ID del lote seleccionado (lote.idLote).
     */
    public void asignarLoteADetalle(Connection conn, int idItem, int idLote) throws SQLException {
        // Actualiza la columna lote_idLote
        String sql = "UPDATE ordencompraitem SET lote_idLote = ? WHERE idItem = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idLote);
            pstmt.setInt(2, idItem);

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected == 0) {
                // Esto forzará el rollback si falla
                throw new SQLException("Error: No se encontró el ítem de la orden para actualizar el lote (ID: " + idItem + ").");
            }
        }
    }
    public int obtenerIdOrdenDesdeItem(Connection conn, int idItem) throws SQLException {
        String sql = "SELECT OrdenCompra_idOrdenCompra FROM OrdenCompraItem WHERE idItem = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idItem);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("OrdenCompra_idOrdenCompra");
                }
            }
        }
        return -1; // O lanzar excepción si no se encuentra
    }
    // OrdenCompraDao.java
// ...
    /**
     * Verifica si todos los ítems de una orden han sido despachados
     * (es decir, tienen asignado un lote).
     */
    public boolean verificarTodosItemsDespachados(Connection conn, int idOrden) throws SQLException {
        // Si la cuenta de ítems con lote NULO (pendientes) es CERO, significa que todos están listos.
        String sql = "SELECT COUNT(*) FROM OrdenCompraItem WHERE OrdenCompra_idOrdenCompra = ? AND lote_idLote IS NULL";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idOrden);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    // Si COUNT(*) es 0, todos han sido despachados (lote_idLote IS NOT NULL)
                    return rs.getInt(1) == 0;
                }
            }
        }
        return false;
    }
// ...
//SÍ ES EL ULTIMO COMMIT
}