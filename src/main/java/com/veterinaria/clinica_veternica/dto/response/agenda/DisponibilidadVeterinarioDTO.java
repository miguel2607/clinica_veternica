package com.veterinaria.clinica_veternica.dto.response.agenda;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * DTO de Response para disponibilidad de un veterinario en una fecha específica.
 *
 * @author Clínica Veterinaria Team
 * @version 1.0
 * @since 2025-01-XX
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DisponibilidadVeterinarioDTO {

    /**
     * ID del veterinario.
     */
    private Long idVeterinario;

    /**
     * Nombre completo del veterinario.
     */
    private String nombreVeterinario;

    /**
     * Fecha consultada.
     */
    private LocalDate fecha;

    /**
     * Día de la semana.
     */
    private String diaSemana;

    /**
     * Indica si el veterinario tiene horarios activos para este día.
     */
    private Boolean tieneHorarios;

    /**
     * Horarios del veterinario para este día de la semana.
     */
    private List<HorarioDisponibleDTO> horarios;

    /**
     * Slots de tiempo disponibles.
     */
    private List<SlotDisponibleDTO> slotsDisponibles;

    /**
     * Citas ya agendadas para este día.
     */
    private List<CitaOcupadaDTO> citasOcupadas;

    /**
     * DTO para un horario disponible.
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class HorarioDisponibleDTO {
        private Long idHorario;
        private LocalTime horaInicio;
        private LocalTime horaFin;
        private Integer duracionCitaMinutos;
        private Boolean activo;
    }

    /**
     * DTO para un slot de tiempo disponible.
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SlotDisponibleDTO {
        private LocalTime hora;
        private Boolean disponible;
        private String motivoNoDisponible; // "OCUPADO", "FUERA_HORARIO", etc.
    }

    /**
     * DTO para una cita ocupada.
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CitaOcupadaDTO {
        private Long idCita;
        private LocalTime hora;
        private String estado;
        private String nombreMascota;
        private String nombreServicio;
    }
}

