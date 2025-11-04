package com.veterinaria.clinica_veternica.dto.request.facturacion;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO de Request para crear/actualizar un Detalle de Factura.
 *
 * @author Clínica Veterinaria Team
 * @version 1.0
 * @since 2025-11-04
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DetalleFacturaRequestDTO {

    /**
     * Tipo de item (SERVICIO, INSUMO, PRODUCTO).
     */
    @NotBlank(message = "El tipo de item es obligatorio")
    @Size(max = 20, message = "El tipo no puede exceder 20 caracteres")
    private String tipoItem;

    /**
     * ID del item relacionado (idServicio, idInsumo, etc.).
     */
    private Long idItemRelacionado;

    /**
     * Descripción del producto o servicio.
     */
    @NotBlank(message = "La descripción es obligatoria")
    @Size(min = 2, max = 500, message = "La descripción debe tener entre 2 y 500 caracteres")
    private String descripcion;

    /**
     * Cantidad del producto o servicio.
     */
    @NotNull(message = "La cantidad es obligatoria")
    @Min(value = 1, message = "La cantidad mínima es 1")
    private Integer cantidad;

    /**
     * Precio unitario.
     */
    @NotNull(message = "El precio unitario es obligatorio")
    @DecimalMin(value = "0.0", message = "El precio no puede ser negativo")
    private BigDecimal precioUnitario;

    /**
     * Porcentaje de descuento aplicado a este detalle (0-100).
     */
    @DecimalMin(value = "0.0", message = "El descuento no puede ser negativo")
    @DecimalMax(value = "100.0", message = "El descuento no puede exceder 100%")
    private BigDecimal porcentajeDescuento;

    /**
     * Observaciones específicas del detalle.
     */
    @Size(max = 500, message = "Las observaciones no pueden exceder 500 caracteres")
    private String observaciones;
}
