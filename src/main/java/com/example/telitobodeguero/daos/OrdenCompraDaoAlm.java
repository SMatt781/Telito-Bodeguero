package com.example.telitobodeguero.daos;

import com.example.telitobodeguero.beans.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;

public class OrdenCompraDaoAlm extends BaseDao{

    public ArrayList<OrdenCompra> getOCEnTransito(int idZona){
        ArrayList<OrdenCompra> enTransito = new ArrayList<>();

        String sql = """
                SELECT
                    oc.idOrdenCompra AS 'ID',
                    p.nombre AS 'NOMBRE',
                    oci.cantidad AS 'CANTIDAD',
                    oc.fecha_llegada AS 'FECHA LLEGADA',
                    oc.estado AS 'ESTADO',
                    oci.lote_idLote as 'LOTE ID'
                FROM
                    ordencompra oc
                JOIN
                    ordencompraitem oci ON oc.idOrdenCompra = oci.OrdenCompra_idOrdenCompra
                JOIN
                    producto p ON oci.Producto_idProducto = p.idProducto
                WHERE
                    /* Filtro 1: Solo estado "EN TRANSITO" */
                    oc.estado = 'EN TRANSITO'
                    /* Filtro 2: Solo Zona Norte (idZonas = 1) */
                    AND oc.Zonas_idZonas = ?
                    /* Filtro 3: Fecha de llegada entre hoy y los próximos 2 días */
                    AND oc.fecha_llegada BETWEEN CURDATE() AND DATE_ADD(CURDATE(), INTERVAL 2 DAY);
                """;

        try (Connection conn = this.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)){
            pstmt.setInt(1, idZona);

            try (ResultSet rs = pstmt.executeQuery()){
                while(rs.next()){
                    OrdenCompra o = new OrdenCompra();
                    o.setCodigoOrdenCompra(rs.getInt(1));
                    Producto p = new Producto();
                    p.setNombre(rs.getString(2));
                    o.setCantidad(rs.getInt(3));
                    o.setFechaLlegada(LocalDate.parse(rs.getString(4)));
                    o.setEstado(rs.getString(5));
                    Lote lote = new Lote();
                    lote.setIdLote(rs.getInt(6));
                    o.setLote(lote);
                    o.setProducto(p);
                    enTransito.add(o);
                }


            }

        }catch (SQLException e){
            e.printStackTrace();
        }
        return enTransito;
    }

    public ArrayList<OrdenCompra> getOCCompletadas(int idZona){
        ArrayList<OrdenCompra> yaLlegaronCompletadas = new ArrayList<>();

        String sql = """
                SELECT
                    oc.idOrdenCompra AS ID_ORDEN_DE_COMPRA,
                    p.nombre AS PRODUCTO_NOMBRE,
                    oci.cantidad AS CANTIDAD,
                    oc.estado AS ESTADO,
                    oci.lote_idLote as LOTEID
                FROM
                    ordencompra oc
                JOIN
                    ordencompraitem oci ON oc.idOrdenCompra = oci.OrdenCompra_idOrdenCompra
                JOIN
                    producto p ON oci.Producto_idProducto = p.idProducto
                WHERE
                    /* Filtro 1: Solo estado 'COMPLETADO' */
                    oc.estado = 'COMPLETADA'
                
                    /* Filtro 2: Solo Zona 1 */
                    AND oc.Zonas_idZonas = ?
                ORDER BY
                    oc.idOrdenCompra DESC;
                """;

        try (Connection conn = this.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)){
            pstmt.setInt(1, idZona);

            try (ResultSet rs = pstmt.executeQuery()){
                while(rs.next()){
                    OrdenCompra o = new OrdenCompra();
                    o.setCodigoOrdenCompra(rs.getInt(1));
                    Producto p = new Producto();
                    p.setNombre(rs.getString(2));
                    o.setCantidad(rs.getInt(3));
                    //o.setFechaLlegada(LocalDate.parse(rs.getString(4)));
                    o.setEstado(rs.getString(4));
                    Lote lote = new Lote();
                    lote.setIdLote(rs.getInt(5));
                    o.setLote(lote);
                    o.setProducto(p);
                    yaLlegaronCompletadas.add(o);
                }

            }

        }catch (SQLException e){
            e.printStackTrace();
        }
        return yaLlegaronCompletadas;
    }

    public ArrayList<OrdenCompra> getOCRegistradas(int idZona){
        ArrayList<OrdenCompra> registradasEnAlmacen = new ArrayList<>();

        String sql = """
                SELECT
                    oc.idOrdenCompra AS ID_ORDEN_DE_COMPRA,
                    p.nombre AS NOMBRE_DEL_PRODUCTO,
                    oci.cantidad AS CANTIDAD,
                    oc.estado AS ESTADO,
                    oci.lote_idLote as LOTEID
                FROM
                    ordencompra oc
                JOIN
                    ordencompraitem oci ON oc.idOrdenCompra = oci.OrdenCompra_idOrdenCompra
                JOIN
                    producto p ON oci.Producto_idProducto = p.idProducto
                WHERE
                    /* Filtro 1: Solo estado 'REGISTRADO' */
                    oc.estado = 'REGISTRADA'
                
                    /* Filtro 2: Solo Zona 1 */
                    AND oc.Zonas_idZonas = ?
                ORDER BY\s
                    oc.idOrdenCompra DESC;
                """;

        try (Connection conn = this.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)){
            pstmt.setInt(1, idZona);

            try (ResultSet rs = pstmt.executeQuery()){
                while(rs.next()){
                    OrdenCompra o = new OrdenCompra();
                    o.setCodigoOrdenCompra(rs.getInt(1));
                    Producto p = new Producto();
                    p.setNombre(rs.getString(2));
                    o.setCantidad(rs.getInt(3));
                    //o.setFechaLlegada(LocalDate.parse(rs.getString(4)));
                    o.setEstado(rs.getString(4));
                    Lote lote = new Lote();
                    lote.setIdLote(rs.getInt(5));
                    o.setLote(lote);
                    o.setProducto(p);
                    registradasEnAlmacen.add(o);
                }


            }

        }catch (SQLException e){
            e.printStackTrace();
        }
        return registradasEnAlmacen;
    }

    public boolean marcarCompletada(int idOC) {
        String sql = "UPDATE ordencompra SET estado='COMPLETADA' WHERE idOrdenCompra=?";
        try (Connection cn = getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, idOC);
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            e.printStackTrace(); return false;
        }
    }

    public boolean marcarRegistrada(int idOC) {
        String sql = "UPDATE ordencompra SET estado='REGISTRADA' WHERE idOrdenCompra=?";
        try (Connection cn = getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, idOC);
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            e.printStackTrace(); return false;
        }
    }

    public Bloque buscarBloqueDisponible(int idZona, int cantidadRequerida){
        String sql = """
            SELECT
                b.idBloque,
                b.codigo,
                /* CapacidadTotal (300) - Stock Ocupado */
                (b.capacidad - COALESCE(SUM(CASE WHEN m.tipo = 'IN' THEN m.cantidad ELSE -m.cantidad END), 0)) AS CapacidadDisponible
            FROM
                bloque b
            LEFT JOIN
                movimiento m ON b.idBloque = m.Bloque_id
            WHERE
                b.Zonas_idZonas = ? AND b.activo = 1 /* Solo bloques activos en la Zona */
            GROUP BY
                b.idBloque, b.codigo, b.capacidad
            HAVING
                CapacidadDisponible >= ? 
            ORDER BY
                CapacidadDisponible ASC /* Prioriza bloques más llenos (mejor uso del espacio) */
            LIMIT 1;
            """;
        try (Connection conn = this.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idZona);
            pstmt.setInt(2, cantidadRequerida);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Bloque b = new Bloque();
                    b.setIdBloque(rs.getInt("idBloque"));
                    b.setCodigo(rs.getString("codigo"));
                    return b;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;

    }

    public int obtenerUltimoNumeroBloque(int idZona) {
        // La consulta extrae solo la parte numérica (asumiendo formato BLOCKXXX) y busca el MAX.
        String sql = """
            SELECT 
                MAX(CAST(SUBSTRING(b.codigo, 6) AS UNSIGNED)) AS ultimoNumero 
            FROM 
                bloque b 
            WHERE 
                Zonas_idZonas = ?;
            """;

        try (Connection conn = this.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idZona);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    // Si no hay bloques, MAX devuelve NULL, que se traduce a 0 si la columna es INT.
                    // Usamos Optional o getInt, pero si la columna es UNSIGNED, getInt(NULL) es 0.
                    return rs.getInt("ultimoNumero");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0; // Por defecto, si falla o no hay datos.
    }

    public Bloque crearNuevoBloqueSecuencial(int idZona) {
        // 1. Obtener el siguiente número
        int ultimoNumero = obtenerUltimoNumeroBloque(idZona);
        int siguienteNumero = ultimoNumero + 1;

        // 2. Generar el nuevo código (ej: 1 -> BLOCK001)
        String nuevoCodigo = String.format("BLOCK%03d", siguienteNumero); // Formato con relleno de ceros (001)

        // 3. Insertar el nuevo bloque (solo codigo y Zonas_idZonas)
        String sqlInsert = """
            INSERT INTO bloque (codigo, Zonas_idZonas) 
            VALUES (?, ?);
            """;

        // Asumimos que la capacidad por defecto es 300 (según tu CREATE TABLE)
        final int CAPACIDAD_POR_DEFECTO = 300;

        try (Connection conn = this.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sqlInsert, PreparedStatement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, nuevoCodigo);
            pstmt.setInt(2, idZona);

            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                // Obtener el ID generado
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        Bloque nuevoBloque = new Bloque();
                        nuevoBloque.setIdBloque(rs.getInt(1)); // El ID autoincremental
                        nuevoBloque.setCodigo(nuevoCodigo);
                        nuevoBloque.setCapacidad(CAPACIDAD_POR_DEFECTO); // Se asume el valor por defecto
                        return nuevoBloque;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // Error en la inserción
    }


}



