package com.veterinaria.clinica_veternica.dto.request.paciente;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de Request para crear/actualizar una Raza.
 *
 * @author Clínica Veterinaria Team
 * @version 1.0
 * @since 2025-11-03
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RazaRequestDTO {

    /**
     * Nombre de la raza.
     */
    @NotBlank(message = "El nombre de la raza es obligatorio")
    @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres")
    private String nombre;

    /**
     * Descripción de la raza.
     */
    @Size(max = 300, message = "La descripción no puede exceder 300 caracteres")
    private String descripcion;

    /**
     * Características típicas de la raza.
     */
    @Size(max = 500, message = "Las características no pueden exceder 500 caracteres")
    private String caracteristicas;

    /**
     * Tamaño típico de la raza (Pequeño, Mediano, Grande, Extra Grande).
     */
    @Size(max = 20, message = "El tamaño no puede exceder 20 caracteres")
    private String tamanio;

    /**
     * Peso promedio en kg.
     */
    @Positive(message = "El peso promedio debe ser positivo")
    private Double pesoPromedio;

    /**
     * ID de la especie a la que pertenece.
     */
    @NotNull(message = "El ID de la especie es obligatorio")
    @Positive(message = "El ID de la especie debe ser positivo")
    private Long idEspecie;

    /**
     * Indica si la raza está activa.
     */
    private Boolean activo;
}
