package com.example.telitobodeguero.dtos;

import java.time.LocalDate;

public class NotificacionLogisDTO {
    private NotificacionTipo tipo;
    private String titulo;
    private String mensaje;
    private LocalDate fechaRelevante;
    private String zonaNombre; // opcional, puede ser null o “—” si no aplica

    public NotificacionLogisDTO(NotificacionTipo tipo, String titulo, String mensaje,
                                LocalDate fechaRelevante, String zonaNombre) {
        this.tipo = tipo;
        this.titulo = titulo;
        this.mensaje = mensaje;
        this.fechaRelevante = fechaRelevante;
        this.zonaNombre = zonaNombre;
    }

    // Getters y Setters
    public NotificacionTipo getTipo() {
        return tipo;
    }

    public void setTipo(NotificacionTipo tipo) {
        this.tipo = tipo;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public LocalDate getFechaRelevante() {
        return fechaRelevante;
    }

    public void setFechaRelevante(LocalDate fechaRelevante) {
        this.fechaRelevante = fechaRelevante;
    }

    public String getZonaNombre() {
        return zonaNombre;
    }

    public void setZonaNombre(String zonaNombre) {
        this.zonaNombre = zonaNombre;
    }
}
