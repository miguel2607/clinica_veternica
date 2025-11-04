package com.veterinaria.clinica_veternica.dto.response.facturacion;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO de Response para una Factura.
 *
 * @author Clínica Veterinaria Team
 * @version 1.0
 * @since 2025-11-04
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FacturaResponseDTO {

    private Long idFactura;
    private String numeroFactura;
    private Long idPropietario;
    private String nombrePropietario;
    private Long idCita;
    private LocalDate fechaEmision;
    private LocalDate fechaVencimiento;
    private BigDecimal subtotal;
    private BigDecimal porcentajeDescuento;
    private BigDecimal montoDescuento;
    private BigDecimal porcentajeImpuesto;
    private BigDecimal montoImpuesto;
    private BigDecimal total;
    private BigDecimal montoPagado;
    private BigDecimal saldoPendiente;
    private Boolean pagada;
    private LocalDateTime fechaPago;
    private Boolean anulada;
    private LocalDateTime fechaAnulacion;
    private String motivoAnulacion;
    private String observaciones;
    private Long idUsuario;
    private String nombreUsuario;
    private List<DetalleFacturaResponseDTO> detalles;
    private List<PagoResponseDTO> pagos;
    private Boolean estaVencida;
    private Long diasVencimiento;
    private Boolean esValida;
    private Double porcentajePagado;
    private Integer cantidadDetalles;
    private Integer cantidadPagos;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaModificacion;
}
