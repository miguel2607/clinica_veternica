package com.veterinaria.clinica_veternica.dto.request.facturacion;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * DTO de Request para crear/actualizar una Factura.
 *
 * @author Clínica Veterinaria Team
 * @version 1.0
 * @since 2025-11-04
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FacturaRequestDTO {

    /**
     * ID del propietario al que se factura.
     */
    @NotNull(message = "El propietario es obligatorio")
    private Long idPropietario;

    /**
     * ID de la cita asociada (si aplica).
     */
    private Long idCita;

    /**
     * Fecha de emisión de la factura.
     */
    private LocalDate fechaEmision;

    /**
     * Fecha de vencimiento para el pago.
     */
    private LocalDate fechaVencimiento;

    /**
     * Porcentaje de descuento aplicado (0-100).
     */
    @DecimalMin(value = "0.0", message = "El descuento no puede ser negativo")
    @DecimalMax(value = "100.0", message = "El descuento no puede exceder 100%")
    private BigDecimal porcentajeDescuento;

    /**
     * Porcentaje de impuesto (IVA, IGV, etc.).
     */
    @DecimalMin(value = "0.0", message = "El impuesto no puede ser negativo")
    @DecimalMax(value = "100.0", message = "El impuesto no puede exceder 100%")
    private BigDecimal porcentajeImpuesto;

    /**
     * Observaciones de la factura.
     */
    @Size(max = 1000, message = "Las observaciones no pueden exceder 1000 caracteres")
    private String observaciones;

    /**
     * ID del usuario que emite la factura.
     */
    private Long idUsuario;

    /**
     * Detalles de la factura.
     */
    @NotNull(message = "Los detalles son obligatorios")
    @Size(min = 1, message = "Debe haber al menos un detalle")
    private List<DetalleFacturaRequestDTO> detalles;
}
