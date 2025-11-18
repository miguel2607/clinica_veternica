package com.veterinaria.clinica_veternica.dto.response.facade;

import com.veterinaria.clinica_veternica.dto.response.agenda.CitaResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

/**
 * DTO para calendario de citas de un día específico.
 * Incluye todas las citas del día organizadas.
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
public class CalendarioCitasDTO {
    private LocalDate fecha;
    private List<CitaResponseDTO> citas;
    private Integer totalCitas;
    private Long citasAtendidas;
    private Long citasProgramadas;
    private Long citasCanceladas;
}
