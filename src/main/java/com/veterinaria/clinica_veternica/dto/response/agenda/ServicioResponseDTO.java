package com.veterinaria.clinica_veternica.dto.response.agenda;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO de Response para un Servicio.
 *
 * @author Clínica Veterinaria Team
 * @version 1.0
 * @since 2025-11-03
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ServicioResponseDTO {

    /**
     * Identificador único del servicio.
     */
    private Long idServicio;

    /**
     * Nombre del servicio.
     */
    private String nombre;

    /**
     * Descripción detallada del servicio.
     */
    private String descripcion;

    /**
     * Tipo de servicio.
     */
    private String tipoServicio;

    /**
     * Categoría del servicio.
     */
    private String categoria;

    /**
     * Precio base del servicio.
     */
    private BigDecimal precio;

    /**
     * Duración estimada en minutos.
     */
    private Integer duracionMinutos;

    /**
     * Duración formateada (ej: "1 hora 30 minutos").
     */
    private String duracionFormateada;

    /**
     * Indica si el servicio requiere preparación previa.
     */
    private Boolean requierePreparacion;

    /**
     * Instrucciones de preparación.
     */
    private String instruccionesPreparacion;

    /**
     * Indica si el servicio está activo.
     */
    private Boolean activo;

    /**
     * Cantidad de citas programadas con este servicio.
     */
    private Integer cantidadCitas;

    /**
     * Fecha y hora de creación.
     */
    private LocalDateTime fechaCreacion;

    /**
     * Fecha y hora de última modificación.
     */
    private LocalDateTime fechaModificacion;
}
