package com.veterinaria.clinica_veternica.dto.request.clinico;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de Request para crear/actualizar una Historia Clínica.
 *
 * @author Clínica Veterinaria Team
 * @version 1.0
 * @since 2025-11-03
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HistoriaClinicaRequestDTO {

    /**
     * ID de la mascota.
     */
    @NotNull(message = "El ID de la mascota es obligatorio")
    @Positive(message = "El ID de la mascota debe ser positivo")
    private Long idMascota;

    /**
     * Número único de historia clínica.
     */
    @NotBlank(message = "El número de historia es obligatorio")
    @Size(max = 50, message = "El número no puede exceder 50 caracteres")
    private String numeroHistoria;

    /**
     * Grupo sanguíneo de la mascota.
     */
    @Size(max = 10, message = "El grupo sanguíneo no puede exceder 10 caracteres")
    private String grupoSanguineo;

    /**
     * Alergias conocidas.
     */
    @Size(max = 1000, message = "Las alergias no pueden exceder 1000 caracteres")
    private String alergias;

    /**
     * Enfermedades crónicas.
     */
    @Size(max = 1000, message = "Las enfermedades no pueden exceder 1000 caracteres")
    private String enfermedadesCronicas;

    /**
     * Cirugías previas.
     */
    @Size(max = 1000, message = "Las cirugías no pueden exceder 1000 caracteres")
    private String cirugiasPrevias;

    /**
     * Medicamentos actuales.
     */
    @Size(max = 1000, message = "Los medicamentos no pueden exceder 1000 caracteres")
    private String medicamentosActuales;

    /**
     * Observaciones generales.
     */
    @Size(max = 2000, message = "Las observaciones no pueden exceder 2000 caracteres")
    private String observaciones;

    /**
     * Indica si la historia está activa.
     */
    private Boolean activo;
}
