package com.veterinaria.clinica_veternica.dto.response.agenda;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * DTO de Response para un Horario.
 *
 * @author Clínica Veterinaria Team
 * @version 1.0
 * @since 2025-11-03
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HorarioResponseDTO {

    /**
     * Identificador único del horario.
     */
    private Long idHorario;

    /**
     * Información del veterinario.
     */
    private VeterinarioSimpleDTO veterinario;

    /**
     * Día de la semana.
     */
    private String diaSemana;

    /**
     * Hora de inicio de disponibilidad.
     */
    private LocalTime horaInicio;

    /**
     * Hora de fin de disponibilidad.
     */
    private LocalTime horaFin;

    /**
     * Duración en horas.
     */
    private Double duracionHoras;

    /**
     * Indica si el horario está activo.
     */
    private Boolean activo;

    /**
     * Fecha y hora de creación.
     */
    private LocalDateTime fechaCreacion;

    /**
     * Fecha y hora de última modificación.
     */
    private LocalDateTime fechaModificacion;

    /**
     * DTO simplificado de Veterinario.
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class VeterinarioSimpleDTO {
        private Long idPersonal;
        private String nombreCompleto;
        private String especialidad;
    }
}
