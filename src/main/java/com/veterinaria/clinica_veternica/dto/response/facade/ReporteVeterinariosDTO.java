package com.veterinaria.clinica_veternica.dto.response.facade;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;


/**
 * DTO para reporte de atenciones por veterinario.
 * Incluye estadísticas de cada veterinario en un período.
 * Reemplaza el uso de Map<String, Object> para type-safety.
 *
 * @author Clínica Veterinaria Team
 * @version 2.0
 * @since 2025-11-17
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReporteVeterinariosDTO {
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private List<EstadisticaVeterinarioDTO> estadisticasPorVeterinario;
    private Long totalAtenciones;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EstadisticaVeterinarioDTO {
        private Long idVeterinario;
        private String nombreVeterinario;
        private String especialidad;
        private Long totalCitasAtendidas;
        private Long totalCitasProgramadas;
    }
}
