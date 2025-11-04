package com.veterinaria.clinica_veternica.dto.response.clinico;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * DTO de Response para un Tratamiento.
 *
 * @author Clínica Veterinaria Team
 * @version 1.0
 * @since 2025-11-03
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TratamientoResponseDTO {

    private Long idTratamiento;
    private String medicamento;
    private String dosis;
    private String frecuencia;
    private String viaAdministracion;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private String instrucciones;
    private String observaciones;
    private String estado;
    private Integer diasTratamiento;
    private Integer diasRestantes;
    private VeterinarioSimpleDTO veterinario;
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
