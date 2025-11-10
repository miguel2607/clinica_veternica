package com.veterinaria.clinica_veternica.dto.response.inventario;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO de Response para el Inventario consolidado.
 *
 * @author Clínica Veterinaria Team
 * @version 1.0
 * @since 2025-11-04
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventarioResponseDTO {

    private Long idInventario;
    private Long idInsumo;
    private String nombreInsumo;
    private String codigoInsumo;
    private Integer cantidadActual;
    private BigDecimal valorTotal;
    private Integer totalEntradas;
    private Integer totalSalidas;
    private BigDecimal valorEntradas;
    private BigDecimal valorSalidas;
    private Double promedioConsumoMensual;
    private Integer diasStockDisponible;
    private LocalDateTime fechaUltimaEntrada;
    private LocalDateTime fechaUltimaSalida;
    private Boolean requiereReorden;
    private LocalDateTime fechaActualizacion;
    private Double indiceRotacion;
    private Boolean esNivelCritico;
    private BigDecimal margenBruto;
    private Boolean tieneMovimientoReciente;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaModificacion;

    // Métodos de conveniencia para compatibilidad
    /**
     * Obtiene la cantidad disponible (alias de cantidadActual).
     */
    public Integer getCantidadDisponible() {
        return cantidadActual;
    }

    /**
     * Obtiene el stock mínimo del insumo asociado.
     * Nota: Este valor debería venir del Insumo, pero por ahora retornamos null.
     * En producción, debería mapearse desde el Insumo.
     */
    public Integer getStockMinimo() {
        // TODO: Mapear desde el Insumo asociado
        return null;
    }

    /**
     * Obtiene el stock máximo del insumo asociado.
     * Nota: Este valor debería venir del Insumo, pero por ahora retornamos null.
     * En producción, debería mapearse desde el Insumo.
     */
    public Integer getStockMaximo() {
        // TODO: Mapear desde el Insumo asociado
        return null;
    }

    /**
     * Obtiene el precio unitario del insumo asociado.
     * Nota: Este valor debería venir del Insumo, pero por ahora retornamos null.
     * En producción, debería mapearse desde el Insumo.
     */
    public BigDecimal getPrecioUnitario() {
        // TODO: Mapear desde el Insumo asociado
        return null;
    }
}
