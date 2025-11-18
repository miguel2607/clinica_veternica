package com.veterinaria.clinica_veternica.dto.response.facade;

import com.veterinaria.clinica_veternica.dto.response.agenda.CitaResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

/**
 * DTO para reporte de citas en un rango de fechas.
 * Incluye las citas y estadísticas del período.
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
public class ReporteCitasDTO {
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private List<CitaResponseDTO> citas;
    private Integer totalCitas;
    private Long citasAtendidas;
    private Long citasProgramadas;
    private Long citasCanceladas;
}
