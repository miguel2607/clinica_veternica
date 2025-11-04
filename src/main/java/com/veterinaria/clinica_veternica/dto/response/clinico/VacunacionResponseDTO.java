package com.veterinaria.clinica_veternica.dto.response.clinico;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * DTO de Response para una Vacunación.
 *
 * @author Clínica Veterinaria Team
 * @version 1.0
 * @since 2025-11-03
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VacunacionResponseDTO {

    private Long idVacunacion;
    private String nombreVacuna;
    private String laboratorio;
    private String lote;
    private LocalDate fechaAplicacion;
    private LocalDate fechaProximaDosis;
    private String viaAdministracion;
    private String observaciones;
    private Boolean esquemaCompleto;
    private Boolean vencida;
    private Integer diasHastaProximaDosis;
    private VeterinarioSimpleDTO veterinario;
    private LocalDateTime fechaCreacion;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class VeterinarioSimpleDTO {
        private Long idPersonal;
        private String nombreCompleto;
    }
}
