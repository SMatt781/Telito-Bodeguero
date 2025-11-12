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

        // ===== FILTRO ACTIVADO =====
        // Se eliminan todas las órdenes que NO están en el flujo que quieres ver.
        lista.removeIf(oc -> {
            String estado = normalizar(oc.getEstado());
            if (estado.isEmpty()) return true; // Quita nulos o vacíos

            switch (estado) {
                case "enviada":
                case "enviado":
                case "recibido":
                case "recibida":
                case "en transito":
                case "en tránsito":
                case "registrado":
                case "registrada":
                case "completado":
                case "completada":
                    return false; // NO la borres, la queremos ver
                default:
                    return true; // Borra "Generada", "Cancelada", etc.
            }
        });

        return lista;
    }

    /**
     * NUEVO MÉTODO: Genera un mensaje basado en el estado ACTUAL de la orden.
     */
    public String getMensajeEstadoActual(OrdenCompra oc) {
        if (oc == null || oc.getEstado() == null) {
            return "Orden sin estado.";
        }

        String estadoNorm = normalizar(oc.getEstado());
        // Asumo que tu bean OrdenCompra tiene un getIdOrdenCompra()
        int idOrden = oc.getCodigoOrdenCompra();
        String base = "OC #" + idOrden + " está en estado: \"" + oc.getEstado() + "\"";

        switch (estadoNorm) {
            case "enviada":
            case "enviado":
                return "📤 " + base;
            case "recibido":
            case "recibida":
                return "📥 " + base;
            case "en transito":
            case "en tránsito":
                return "🚚 " + base;
            case "registrado":
            case "registrada":
                return "🗂️ " + base;
            case "completado":
            case "completada":
                return "✅ " + base;
            default:
                return "ℹ️ " + base;
        }
    }

    /**
     * Genera el texto para la notificación de cambio de estado
     * (mantiene variantes “En tránsito” / “En transito”).
     * (Este método ya no lo usará el servlet, pero puede servir para otros fines)
     */
    public String generarMensajeCambioEstado(String estadoAnterior, String estadoNuevo, int idOrden) {
        // ... (tu código original sin cambios) ...
        String nuevoNorm = normalizar(estadoNuevo);
        String base = "OC #" + idOrden + " cambió de estado: \"" + estadoAnterior + "\" → \"" + estadoNuevo + "\"";

        switch (nuevoNorm) {
            // ... (el resto de tu switch) ...
            default:
                return "ℹ️ " + base;
        }
    }

    // ---- helpers ----
    // (Lo mantengo 'private' porque solo se usa dentro de esta clase)
    private String normalizar(String s) {
        if (s == null) return "";
        s = s.trim().toLowerCase();
        // unifica “tránsito”/“transito”
        s = s.replace("tránsito", "transito");
        return s;
    }
}
