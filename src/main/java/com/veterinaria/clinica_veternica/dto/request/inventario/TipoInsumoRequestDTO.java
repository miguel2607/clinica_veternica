package com.veterinaria.clinica_veternica.dto.request.inventario;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de Request para crear/actualizar un Tipo de Insumo.
 *
 * @author Clínica Veterinaria Team
 * @version 1.0
 * @since 2025-11-04
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TipoInsumoRequestDTO {

    /**
     * Nombre del tipo de insumo.
     */
    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres")
    private String nombre;

    /**
     * Descripción del tipo de insumo.
     */
    @Size(max = 500, message = "La descripción no puede exceder 500 caracteres")
    private String descripcion;

    /**
     * Categoría general (MEDICAMENTO, MATERIAL_QUIRURGICO, ALIMENTO, etc.).
     */
    @Size(max = 50, message = "La categoría no puede exceder 50 caracteres")
    private String categoria;

    /**
     * Indica si el tipo está activo.
     */
    private Boolean activo;
}
