package daos;

import beans.OrdenCompra;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class OrdenCompraDao {

    // Tabla "plana" OC + Items, filtrada por los productos del productor
    public List<OrdenCompra> listarOCConItemsParaProductor(int idProductor) throws SQLException {
        String sql = """
            SELECT oc.idOrdenCompra AS oc, oc.estado, oc.fecha_llegada,
                   oci.idItem AS item, oci.cantidad,
                   p.sku, p.nombre AS producto
            FROM OrdenCompra oc
            JOIN OrdenCompraItem oci ON oci.OrdenCompra_idOrdenCompra = oc.idOrdenCompra
            JOIN Producto p ON p.idProducto = oci.Producto_idProducto
            WHERE EXISTS (
              SELECT 1 FROM Lote l
              WHERE l.Producto_idProducto = p.idProducto
                AND l.Usuarios_idUsuarios = ?
            )
            ORDER BY oc.fecha_llegada, oc.idOrdenCompra, oci.idItem
            """;

        List<OrdenCompra> lista = new ArrayList<>();
        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, idProductor);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    OrdenCompra r = new OrdenCompra();
                    r.setIdOrdenCompra(rs.getInt("oc"));
                    r.setEstado(rs.getString("estado"));

                    Date f = rs.getDate("fecha_llegada");
                    r.setFechaLlegada(f == null ? null : f.toLocalDate());

                    r.setIdItem(rs.getInt("item"));
                    r.setCantidad(rs.getInt("cantidad"));
                    r.setSku(rs.getString("sku"));
                    r.setProducto(rs.getString("producto"));

                    lista.add(r);
                }
            }
        }
        return lista;
    }
}
