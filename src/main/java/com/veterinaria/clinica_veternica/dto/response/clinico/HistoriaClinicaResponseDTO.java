package com.veterinaria.clinica_veternica.dto.response.clinico;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO de Response para una Historia Clínica.
 *
 * @author Clínica Veterinaria Team
 * @version 1.0
 * @since 2025-11-03
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HistoriaClinicaResponseDTO {

    /**
     * Identificador único de la historia clínica.
     */
    private Long idHistoriaClinica;

    /**
     * Información de la mascota.
     */
    private MascotaSimpleDTO mascota;

    /**
     * Número único de historia clínica.
     */
    private String numeroHistoria;

    /**
     * Grupo sanguíneo de la mascota.
     */
    private String grupoSanguineo;

    /**
     * Alergias conocidas.
     */
    private String alergias;

    /**
     * Enfermedades crónicas.
     */
    private String enfermedadesCronicas;

    /**
     * Cirugías previas.
     */
    private String cirugiasPrevias;

    /**
     * Medicamentos actuales.
     */
    private String medicamentosActuales;

    /**
     * Observaciones generales.
     */
    private String observaciones;

    /**
     * Indica si la historia está activa.
     */
    private Boolean activo;

    /**
     * Cantidad de evoluciones registradas.
     */
    private Integer cantidadEvoluciones;

    /**
     * Cantidad de tratamientos registrados.
     */
    private Integer cantidadTratamientos;

    /**
     * Cantidad de vacunas aplicadas.
     */
    private Integer cantidadVacunas;

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
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class MascotaSimpleDTO {
        private Long idMascota;
        private String nombre;
        private String especie;
        private String propietarioNombre;
    }
}
