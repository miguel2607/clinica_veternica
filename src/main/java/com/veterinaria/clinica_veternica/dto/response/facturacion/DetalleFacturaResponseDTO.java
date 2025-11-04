package com.veterinaria.clinica_veternica.dto.response.facturacion;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO de Response para un Detalle de Factura.
 *
 * @author Clínica Veterinaria Team
 * @version 1.0
 * @since 2025-11-04
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DetalleFacturaResponseDTO {

    private Long idDetalle;
    private Long idFactura;
    private String tipoItem;
    private Long idItemRelacionado;
    private String descripcion;
    private Integer cantidad;
    private BigDecimal precioUnitario;
    private BigDecimal porcentajeDescuento;
    private BigDecimal montoDescuento;
    private BigDecimal subtotal;
    private String observaciones;
    private BigDecimal precioTotal;
    private Boolean tieneDescuento;
    private Boolean esServicio;
    private Boolean esInsumo;
    private LocalDateTime fechaCreacion;
}
