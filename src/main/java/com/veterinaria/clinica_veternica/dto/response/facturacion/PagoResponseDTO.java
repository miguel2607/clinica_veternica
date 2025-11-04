package com.veterinaria.clinica_veternica.dto.response.facturacion;

import com.veterinaria.clinica_veternica.domain.facturacion.MetodoPago;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO de Response para un Pago.
 *
 * @author Clínica Veterinaria Team
 * @version 1.0
 * @since 2025-11-04
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PagoResponseDTO {

    private Long idPago;
    private Long idFactura;
    private String numeroFactura;
    private BigDecimal monto;
    private MetodoPago metodoPago;
    private LocalDateTime fechaPago;
    private String numeroReferencia;
    private String numeroAutorizacion;
    private String ultimos4Digitos;
    private String banco;
    private String titular;
    private String comprobante;
    private String observaciones;
    private Long idUsuario;
    private String nombreUsuario;
    private Boolean anulado;
    private LocalDateTime fechaAnulacion;
    private String motivoAnulacion;
    private Boolean esValido;
    private Boolean esConTarjeta;
    private Boolean esEfectivo;
    private Boolean esTransferencia;
    private String descripcionPago;
    private Boolean requiereValidacion;
    private LocalDateTime fechaCreacion;
}
