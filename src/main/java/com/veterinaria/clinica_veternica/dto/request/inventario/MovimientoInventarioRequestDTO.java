package com.veterinaria.clinica_veternica.dto.request.inventario;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * DTO de Request para crear un Movimiento de Inventario.
 *
 * @author Clínica Veterinaria Team
 * @version 1.0
 * @since 2025-11-04
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MovimientoInventarioRequestDTO {

    /**
     * ID del insumo relacionado con el movimiento.
     */
    @NotNull(message = "El insumo es obligatorio")
    private Long idInsumo;

    /**
     * Tipo de movimiento (ENTRADA, SALIDA, AJUSTE).
     */
    @NotBlank(message = "El tipo de movimiento es obligatorio")
    @Pattern(regexp = "^(ENTRADA|SALIDA|AJUSTE)$", message = "Tipo inválido. Use: ENTRADA, SALIDA, AJUSTE")
    private String tipoMovimiento;

    /**
     * Motivo específico del movimiento.
     */
    @NotBlank(message = "El motivo es obligatorio")
    @Size(max = 50, message = "El motivo no puede exceder 50 caracteres")
    private String motivo;

    /**
     * Cantidad del movimiento.
     */
    @NotNull(message = "La cantidad es obligatoria")
    private Integer cantidad;

    /**
     * Precio unitario en el momento del movimiento.
     */
    @DecimalMin(value = "0.0", message = "El precio no puede ser negativo")
    private BigDecimal precioUnitario;

    /**
     * Número de factura o documento asociado (si aplica).
     */
    @Size(max = 50, message = "El número de documento no puede exceder 50 caracteres")
    private String numeroDocumento;

    /**
     * Lote del insumo en este movimiento.
     */
    @Size(max = 50, message = "El lote no puede exceder 50 caracteres")
    private String lote;

    /**
     * Fecha de vencimiento del lote (si aplica).
     */
    private LocalDate fechaVencimiento;

    /**
     * ID del usuario que realizó el movimiento.
     */
    private Long idUsuario;

    /**
     * Observaciones del movimiento.
     */
    @Size(max = 500, message = "Las observaciones no pueden exceder 500 caracteres")
    private String observaciones;

    /**
     * Fecha y hora del movimiento.
     */
    private LocalDateTime fechaMovimiento;
}
