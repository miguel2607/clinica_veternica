package com.veterinaria.clinica_veternica.dto.request.agenda;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

/**
 * DTO de Request para crear/actualizar un Horario.
 *
 * @author Clínica Veterinaria Team
 * @version 1.0
 * @since 2025-11-03
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HorarioRequestDTO {

    /**
     * ID del veterinario.
     */
    @NotNull(message = "El ID del veterinario es obligatorio")
    @Positive(message = "El ID del veterinario debe ser positivo")
    private Long idVeterinario;

    /**
     * Día de la semana (MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY).
     */
    @NotBlank(message = "El día de la semana es obligatorio")
    private String diaSemana;

    /**
     * Hora de inicio de disponibilidad.
     */
    @NotNull(message = "La hora de inicio es obligatoria")
    private LocalTime horaInicio;

    /**
     * Hora de fin de disponibilidad.
     */
    @NotNull(message = "La hora de fin es obligatoria")
    private LocalTime horaFin;

    /**
     * Indica si el horario está activo.
     */
    private Boolean activo;
}
