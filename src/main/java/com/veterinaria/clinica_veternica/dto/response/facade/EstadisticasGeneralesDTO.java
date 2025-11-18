package com.veterinaria.clinica_veternica.dto.response.facade;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para estadísticas generales del sistema.
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
public class EstadisticasGeneralesDTO {
    private Long totalPropietarios;
    private Long totalMascotas;
    private Long totalVeterinarios;
    private Long totalCitasProgramadas;
    private Long totalCitasHoy;
    private Integer insumosStockBajo;
}
