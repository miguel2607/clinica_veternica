package com.veterinaria.clinica_veternica.dto.response.agenda;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * DTO de Response para una Cita.
 *
 * @author Clínica Veterinaria Team
 * @version 1.0
 * @since 2025-11-03
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CitaResponseDTO {

    /**
     * Identificador único de la cita.
     */
    private Long idCita;

    /**
     * Información de la mascota.
     */
    private MascotaSimpleDTO mascota;

    /**
     * Información del veterinario.
     */
    private VeterinarioSimpleDTO veterinario;

    /**
     * Información del servicio.
     */
    private ServicioSimpleDTO servicio;

    /**
     * Fecha de la cita.
     */
    private LocalDate fechaCita;

    /**
     * Hora de la cita.
     */
    private LocalTime horaCita;

    /**
     * Fecha y hora combinadas.
     */
    private LocalDateTime fechaHora;

    /**
     * Motivo de la consulta.
     */
    private String motivo;

    /**
     * Observaciones adicionales.
     */
    private String observaciones;

    /**
     * Estado de la cita.
     */
    private String estado;

    /**
     * Indica si es una cita de emergencia.
     */
    private Boolean esEmergencia;

    /**
     * Fecha y hora de creación.
     */
    private LocalDateTime fechaCreacion;

    /**
     * Fecha y hora de última modificación.
     */
    private LocalDateTime fechaModificacion;

    /**
     * DTO simplificado de Mascota.
     */
    @Schema(description = "Información simplificada de la mascota")
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class MascotaSimpleDTO {
        private Long idMascota;
        private String nombre;
        private String especie;
        private String propietarioNombre;
        private String propietarioTelefono;
    }

    /**
     * DTO simplificado de Veterinario.
     */
    @Schema(description = "Información simplificada del veterinario")
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class VeterinarioSimpleDTO {
        private Long idPersonal;
        private String nombreCompleto;
        private String especialidad;
    }

    /**
     * DTO simplificado de Servicio.
     */
    @Schema(description = "Información simplificada del servicio")
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ServicioSimpleDTO {
        private Long idServicio;
        private String nombre;
        private String tipoServicio;
        private BigDecimal precio;
        private Integer duracionMinutos;
    }
}
