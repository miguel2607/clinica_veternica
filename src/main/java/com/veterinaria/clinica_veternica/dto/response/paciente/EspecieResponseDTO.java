package com.veterinaria.clinica_veternica.dto.response.paciente;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO de Response para una Especie.
 *
 * @author Clínica Veterinaria Team
 * @version 1.0
 * @since 2025-11-03
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EspecieResponseDTO {

    /**
     * Identificador único de la especie.
     */
    private Long idEspecie;

    /**
     * Nombre de la especie.
     */
    private String nombre;

    /**
     * Descripción de la especie.
     */
    private String descripcion;

    /**
     * Indica si la especie está activa.
     */
    private Boolean activo;

    /**
     * Cantidad de razas asociadas a esta especie.
     */
    private Integer cantidadRazas;

    /**
     * Fecha y hora de creación del registro.
     */
    private LocalDateTime fechaCreacion;

    /**
     * Fecha y hora de última modificación.
     */
    private LocalDateTime fechaModificacion;
}
