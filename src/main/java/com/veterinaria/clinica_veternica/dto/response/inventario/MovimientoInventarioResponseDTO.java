package com.veterinaria.clinica_veternica.dto.response.inventario;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * DTO de Response para un Movimiento de Inventario.
 *
 * @author Clínica Veterinaria Team
 * @version 1.0
 * @since 2025-11-04
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MovimientoInventarioResponseDTO {

    private Long idMovimiento;
    private Long idInsumo;
    private String nombreInsumo;
    private String codigoInsumo;
    private String tipoMovimiento;
    private String motivo;
    private Integer cantidad;
    private Integer stockAnterior;
    private Integer stockNuevo;
    private BigDecimal precioUnitario;
    private BigDecimal costoTotal;
    private String numeroDocumento;
    private String lote;
    private LocalDate fechaVencimiento;
    private Long idUsuario;
    private String nombreUsuario;
    private String observaciones;
    private LocalDateTime fechaMovimiento;
    private Boolean esEntrada;
    private Boolean esSalida;
    private Boolean esAjuste;
    private Boolean esPorVencimiento;
    private Boolean esPorPerdida;
    private String resumen;
    private Boolean requiereAprobacion;
    private LocalDateTime fechaCreacion;
}
