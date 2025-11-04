package com.veterinaria.clinica_veternica.dto.request.paciente;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de Request para crear/actualizar una Especie.
 *
 * @author Clínica Veterinaria Team
 * @version 1.0
 * @since 2025-11-03
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EspecieRequestDTO {

    /**
     * Nombre de la especie.
     */
    @NotBlank(message = "El nombre de la especie es obligatorio")
    @Size(min = 2, max = 50, message = "El nombre debe tener entre 2 y 50 caracteres")
    private String nombre;

    /**
     * Descripción de la especie.
     */
    @Size(max = 200, message = "La descripción no puede exceder 200 caracteres")
    private String descripcion;

    /**
     * Indica si la especie está activa.
     */
    private Boolean activo;
}
