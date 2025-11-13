package com.veterinaria.clinica_veternica.patterns.creational.abstractfactory;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Producto abstracto: Mensaje de Notificación
 *
 * Representa un mensaje de notificación genérico que puede ser
 * enviado por cualquier canal.
 *
 * @author Clínica Veterinaria Team
 * @version 1.0
 * @since 2025-11-04
 */
@Data
@Builder
public class MensajeNotificacion {

    /**
     * Constantes para prioridades.
     */
    private static final String PRIORIDAD_URGENTE = "URGENTE";
    private static final String PRIORIDAD_ALTA = "ALTA";

    private String id;
    private String canal;
    private String destinatario;
    private String asunto;
    private String contenido;
    private LocalDateTime fechaCreacion;
    private String prioridad;

    /**
     * Obtiene el tamaño del mensaje en caracteres.
     */
    public int getTamano() {
        return contenido != null ? contenido.length() : 0;
    }

    /**
     * Verifica si el mensaje es urgente.
     * @return true si el mensaje es urgente o de alta prioridad
     */
    @SuppressWarnings("unused")
    public boolean esUrgente() {
        return PRIORIDAD_URGENTE.equalsIgnoreCase(prioridad) || PRIORIDAD_ALTA.equalsIgnoreCase(prioridad);
    }

    /**
     * Genera un resumen del mensaje.
     */
    public String getResumen() {
        if (contenido == null || contenido.length() <= 50) {
            return contenido;
        }
        return contenido.substring(0, 50) + "...";
    }
}
