package com.veterinaria.clinica_veternica.dto.request.facturacion;

import com.veterinaria.clinica_veternica.domain.facturacion.MetodoPago;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO de Request para crear/registrar un Pago.
 *
 * @author Clínica Veterinaria Team
 * @version 1.0
 * @since 2025-11-04
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PagoRequestDTO {

    /**
     * ID de la factura a la que se aplica el pago.
     */
    @NotNull(message = "La factura es obligatoria")
    private Long idFactura;

    /**
     * Monto del pago.
     */
    @NotNull(message = "El monto es obligatorio")
    @DecimalMin(value = "0.01", message = "El monto debe ser mayor a 0")
    private BigDecimal monto;

    /**
     * Método de pago utilizado.
     */
    @NotNull(message = "El método de pago es obligatorio")
    private MetodoPago metodoPago;

    /**
     * Fecha y hora del pago.
     */
    private LocalDateTime fechaPago;

    /**
     * Número de referencia del pago.
     */
    @Size(max = 100, message = "La referencia no puede exceder 100 caracteres")
    private String numeroReferencia;

    /**
     * Número de autorización (para pagos con tarjeta).
     */
    @Size(max = 50, message = "El número de autorización no puede exceder 50 caracteres")
    private String numeroAutorizacion;

    /**
     * Últimos 4 dígitos de la tarjeta (si aplica).
     */
    @Size(max = 4, message = "Los últimos 4 dígitos deben ser exactamente 4")
    @Pattern(regexp = "^[0-9]{4}$", message = "Deben ser exactamente 4 dígitos")
    private String ultimos4Digitos;

    /**
     * Banco emisor (si aplica).
     */
    @Size(max = 100, message = "El banco no puede exceder 100 caracteres")
    private String banco;

    /**
     * Nombre del titular de la tarjeta o cuenta (si aplica).
     */
    @Size(max = 150, message = "El titular no puede exceder 150 caracteres")
    private String titular;

    /**
     * Observaciones del pago.
     */
    @Size(max = 500, message = "Las observaciones no pueden exceder 500 caracteres")
    private String observaciones;

    /**
     * ID del usuario que registra el pago.
     */
    private Long idUsuario;
}
