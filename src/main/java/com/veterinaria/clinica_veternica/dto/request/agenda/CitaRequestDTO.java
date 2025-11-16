package com.veterinaria.clinica_veternica.dto.request.agenda;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * DTO de Request para crear/actualizar una Cita.
 *
 * @author Clínica Veterinaria Team
 * @version 1.0
 * @since 2025-11-03
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CitaRequestDTO {

    /**
     * ID de la mascota.
     */
    @NotNull(message = "El ID de la mascota es obligatorio")
    @Positive(message = "El ID de la mascota debe ser positivo")
    private Long idMascota;

    /**
     * ID del veterinario.
     */
    @NotNull(message = "El ID del veterinario es obligatorio")
    @Positive(message = "El ID del veterinario debe ser positivo")
    private Long idVeterinario;

    /**
     * ID del servicio.
     */
    @NotNull(message = "El ID del servicio es obligatorio")
    @Positive(message = "El ID del servicio debe ser positivo")
    private Long idServicio;

    /**
     * Fecha de la cita.
     */
    @NotNull(message = "La fecha de la cita es obligatoria")
    @FutureOrPresent(message = "La fecha de la cita no puede ser en el pasado")
    private LocalDate fechaCita;

    /**
     * Hora de la cita.
     * Formatos soportados: 
     * - 24 horas: HH:mm:ss, HH:mm, H:mm, H (ej: "09:00:00", "09:00", "9:00", "9")
     * - 12 horas: h:mm a, h a (ej: "9:00 AM", "9 PM", "9:30 PM", "9AM")
     */
    @NotNull(message = "La hora de la cita es obligatoria")
    private LocalTime horaCita;

    /**
     * Motivo de la consulta.
     */
    @NotBlank(message = "El motivo es obligatorio")
    @Size(min = 5, max = 500, message = "El motivo debe tener entre 5 y 500 caracteres")
    private String motivo;

    /**
     * Observaciones adicionales.
     */
    @Size(max = 1000, message = "Las observaciones no pueden exceder 1000 caracteres")
    private String observaciones;

    /**
     * Estado de la cita (PROGRAMADA, CONFIRMADA, ATENDIDA, CANCELADA, NO_ASISTIO).
     */
    private String estado;

    /**
     * Indica si es una cita de emergencia.
     */
    private Boolean esEmergencia;
}
