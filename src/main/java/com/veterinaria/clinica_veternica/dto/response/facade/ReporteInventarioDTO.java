package com.veterinaria.clinica_veternica.dto.response.facade;

import com.veterinaria.clinica_veternica.dto.response.inventario.InventarioResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * DTO para reporte completo de inventario.
 * Incluye todos los insumos con sus stocks y valorización.
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
public class ReporteInventarioDTO {
    private List<InventarioResponseDTO> inventarios;
    private Integer totalItems;
    private List<InventarioResponseDTO> stockBajo;
    private Integer totalStockBajo;
    private BigDecimal valorTotalInventario;
}
