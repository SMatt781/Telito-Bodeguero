// src/main/java/com/example/telitobodeguero/daos/NotificacionAdminDao.java
package com.example.telitobodeguero.daos;

import com.example.telitobodeguero.beans.OrdenCompra;

import java.util.ArrayList;
import java.util.List;

public class NotificacionesAdminDao {

    private final OrdenCompraDao ordenCompraDao = new OrdenCompraDao();

    /**
     * Lista de órdenes relevantes para Admin SIN tocar BD ni cambiar tu DAO:
     * Usa tu método existente: obtenerOrdenCompra(estadoFiltro, terminoBusquedaProveedor)
     * - estadoFiltro: null o "Todos" para traer todas
     * - terminoBusquedaProveedor: null para no filtrar por proveedor
     */
    public List<OrdenCompra> listarOrdenesParaAdmin() {
        // Trae TODO (mismo método que ya usas en logística)
        ArrayList<OrdenCompra> lista = ordenCompraDao.obtenerOrdenCompra(null, null);

        // Si quisieras solo estados que te interesan en notificaciones “activas”, puedes filtrar aquí:
        // lista.removeIf(oc -> oc.getEstado() == null ||
        //       !(oc.getEstado().equalsIgnoreCase("Enviada")
        //      || oc.getEstado().equalsIgnoreCase("Recibido")
        //      || oc.getEstado().equalsIgnoreCase("En tránsito")
        //      || oc.getEstado().equalsIgnoreCase("En transito")
        //      || oc.getEstado().equalsIgnoreCase("Registrado")
        //      || oc.getEstado().equalsIgnoreCase("Completado")));

        return lista;
    }

    /**
     * Genera el texto para la notificación de cambio de estado
     * (mantiene variantes “En tránsito” / “En transito”).
     */
    public String generarMensajeCambioEstado(String estadoAnterior, String estadoNuevo, int idOrden) {
        // Normaliza espacios/acentos leves para comparar pero muestra lo que venga
        String nuevoNorm = normalizar(estadoNuevo);
        String base = "OC #" + idOrden + " cambió de estado: \"" + estadoAnterior + "\" → \"" + estadoNuevo + "\"";

        switch (nuevoNorm) {
            case "enviada":
            case "enviado":
                return "📤 " + base + ". Pedido recién enviado.";
            case "recibido":
            case "recibida":
                return "📥 " + base + ". Lote recibido en almacén.";
            case "en transito":
            case "en tránsito":
                return "🚚 " + base + ". Lote en tránsito.";
            case "registrado":
            case "registrada":
                return "🗂️ " + base + ". Ingresado en sistema.";
            case "completado":
            case "completada":
                return "✅ " + base + ". Proceso finalizado.";
            default:
                return "ℹ️ " + base;
        }
    }

    // ---- helpers ----
    private String normalizar(String s) {
        if (s == null) return "";
        s = s.trim().toLowerCase();
        // unifica “tránsito”/“transito”
        s = s.replace("tránsito", "transito");
        return s;
    }
}
