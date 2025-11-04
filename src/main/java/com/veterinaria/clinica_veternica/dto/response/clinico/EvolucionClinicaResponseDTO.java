package com.veterinaria.clinica_veternica.dto.response.clinico;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO de Response para una Evolución Clínica.
 *
 * @author Clínica Veterinaria Team
 * @version 1.0
 * @since 2025-11-03
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EvolucionClinicaResponseDTO {

    private Long idEvolucion;
    private String tipoEvolucion;
    private String motivoConsulta;
    private String hallazgosExamen;
    private String diagnostico;
    private String planTratamiento;
    private String observaciones;
    private Double peso;
    private Double temperatura;
    private Integer frecuenciaCardiaca;
    private Integer frecuenciaRespiratoria;
    private Boolean temperaturaEnRangoNormal;
    private Boolean signosVitalesEstables;
    private VeterinarioSimpleDTO veterinario;
    private LocalDateTime fechaEvolucion;
    private LocalDateTime fechaCreacion;

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
