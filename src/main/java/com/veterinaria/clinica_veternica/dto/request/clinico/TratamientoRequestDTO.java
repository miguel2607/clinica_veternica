package com.veterinaria.clinica_veternica.dto.request.clinico;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * DTO de Request para crear/actualizar un Tratamiento.
 *
 * @author Clínica Veterinaria Team
 * @version 1.0
 * @since 2025-11-03
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TratamientoRequestDTO {

    @NotNull(message = "El ID de la historia clínica es obligatorio")
    @Positive
    private Long idHistoriaClinica;

    @NotNull(message = "El ID del veterinario es obligatorio")
    @Positive
    private Long idVeterinario;

    @NotBlank(message = "El medicamento es obligatorio")
    @Size(max = 200)
    private String medicamento;

    @NotBlank(message = "La dosis es obligatoria")
    @Size(max = 100)
    private String dosis;

    @NotBlank(message = "La frecuencia es obligatoria")
    @Size(max = 100)
    private String frecuencia;

    @NotBlank(message = "La vía de administración es obligatoria")
    @Size(max = 50)
    private String viaAdministracion;

    @NotNull(message = "La fecha de inicio es obligatoria")
    private LocalDate fechaInicio;

    @NotNull(message = "La fecha de fin es obligatoria")
    @Future(message = "La fecha de fin debe ser futura")
    private LocalDate fechaFin;

    @Size(max = 1000)
    private String instrucciones;

    @Size(max = 1000)
    private String observaciones;

    private String estado;
}
