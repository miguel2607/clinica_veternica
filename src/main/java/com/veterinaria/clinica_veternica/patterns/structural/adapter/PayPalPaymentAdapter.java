package com.veterinaria.clinica_veternica.patterns.structural.adapter;

import com.veterinaria.clinica_veternica.exception.BusinessException;
import com.veterinaria.clinica_veternica.exception.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * Patrón Adapter: PayPalPaymentAdapter
 *
 * Adaptador para la pasarela de pago PayPal.
 * Adapta la API específica de PayPal a la interfaz común PaymentGatewayAdapter.
 *
 * Justificación:
 * - PayPal tiene una API diferente a otras pasarelas
 * - El adapter unifica la interfaz para que el código de negocio
 *   no dependa de la implementación específica de PayPal
 *
 * @author Clínica Veterinaria Team
 * @version 1.0
 * @since 2025-11-04
 */
@Slf4j
@Component
public class PayPalPaymentAdapter implements PaymentGatewayAdapter {

    private static final String NOMBRE_PASARELA = "PayPal";

    @Override
    public String procesarPago(BigDecimal monto, String referencia) throws ValidationException, BusinessException {
        log.info("Procesando pago de {} a través de PayPal para referencia: {}", monto, referencia);

        // Simulación de integración con PayPal API
        // En producción, aquí se haría la llamada real a la API de PayPal
        if (monto == null || monto.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValidationException("El monto debe ser mayor a cero", "monto", "Monto inválido");
        }

        if (referencia == null || referencia.isBlank()) {
            throw new ValidationException("La referencia del pago es requerida", "referencia", "Referencia inválida");
        }

        // Simulación: generar ID de transacción
        String transactionId = "PAYPAL-" + System.currentTimeMillis() + "-" + referencia.hashCode();

        log.info("Pago procesado exitosamente. Transaction ID: {}", transactionId);
        return transactionId;
    }

    @Override
    public String verificarEstado(String transactionId) throws ValidationException, BusinessException {
        log.debug("Verificando estado de transacción PayPal: {}", transactionId);

        if (transactionId == null || transactionId.isBlank()) {
            throw new ValidationException("El ID de transacción es requerido", "transactionId", "ID inválido");
        }

        if (!transactionId.startsWith("PAYPAL-")) {
            throw new ValidationException("ID de transacción inválido para PayPal", "transactionId", "Formato inválido");
        }

        // Simulación: verificar estado en PayPal
        // En producción, aquí se haría la llamada real a la API de PayPal
        return "COMPLETADO";
    }

    @Override
    public String getNombrePasarela() {
        return NOMBRE_PASARELA;
    }
}

