package com.veterinaria.clinica_veternica.dto.request.agenda;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO de Request para crear/actualizar un Servicio.
 *
 * @author Clínica Veterinaria Team
 * @version 1.0
 * @since 2025-11-03
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ServicioRequestDTO {

    /**
     * Nombre del servicio.
     */
    @NotBlank(message = "El nombre del servicio es obligatorio")
    @Size(min = 3, max = 150, message = "El nombre debe tener entre 3 y 150 caracteres")
    private String nombre;

    /**
     * Descripción detallada del servicio.
     */
    @Size(max = 1000, message = "La descripción no puede exceder 1000 caracteres")
    private String descripcion;

    /**
     * Tipo de servicio (CONSULTA, CIRUGIA, DIAGNOSTICO, ESTETICO, EMERGENCIA, etc.).
     */
    @NotBlank(message = "El tipo de servicio es obligatorio")
    private String tipoServicio;

    /**
     * Categoría del servicio (CLINICO, QUIRURGICO, ESTETICO, EMERGENCIA).
     */
    @NotBlank(message = "La categoría es obligatoria")
    private String categoria;

    /**
     * Precio base del servicio.
     */
    @NotNull(message = "El precio es obligatorio")
    @DecimalMin(value = "0.0", inclusive = false, message = "El precio debe ser mayor a 0")
    private BigDecimal precio;

    /**
     * Duración estimada en minutos.
     */
    @NotNull(message = "La duración es obligatoria")
    @Min(value = 1, message = "La duración debe ser al menos 1 minuto")
    private Integer duracionMinutos;

    /**
     * Indica si el servicio requiere preparación previa.
     */
    private Boolean requierePreparacion;

    /**
     * Instrucciones de preparación.
     */
    @Size(max = 500, message = "Las instrucciones no pueden exceder 500 caracteres")
    private String instruccionesPreparacion;

    /**
     * Indica si el servicio está activo.
     */
    private Boolean activo;
}
