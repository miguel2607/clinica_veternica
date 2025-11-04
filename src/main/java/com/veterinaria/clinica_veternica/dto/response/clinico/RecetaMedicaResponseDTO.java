package com.veterinaria.clinica_veternica.dto.response.clinico;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * DTO de Response para una Receta Médica.
 *
 * @author Clínica Veterinaria Team
 * @version 1.0
 * @since 2025-11-03
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecetaMedicaResponseDTO {

    private Long idReceta;
    private String numeroReceta;
    private String nombreMedicamento;
    private String concentracion;
    private String dosis;
    private String frecuencia;
    private Integer duracionDias;
    private String viaAdministracion;
    private String indicaciones;
    private LocalDate fechaEmision;
    private LocalDate fechaVigencia;
    private Integer numeroRepeticiones;
    private Boolean vigente;
    private Boolean dispensada;
    private VeterinarioSimpleDTO veterinario;
    private LocalDateTime fechaCreacion;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class VeterinarioSimpleDTO {
        private Long idPersonal;
        private String nombreCompleto;
        private String registroProfesional;
    }
}
