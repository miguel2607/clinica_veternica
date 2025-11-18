package com.veterinaria.clinica_veternica.patterns.structural.adapter;

import com.veterinaria.clinica_veternica.exception.BusinessException;
import com.veterinaria.clinica_veternica.exception.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * Adapter concreto para Stripe.
 *
 * Adapta la API de Stripe a la interfaz común PaymentGatewayAdapter.
 *
 * @author Clínica Veterinaria Team
 * @version 1.0
 * @since 2025-11-04
 */
@Slf4j
@Component
public class StripePaymentAdapter implements PaymentGatewayAdapter {

    @Override
    public String procesarPago(BigDecimal monto, String referencia) throws ValidationException, BusinessException {
        log.info("Procesando pago con Stripe: monto={}, referencia={}", monto, referencia);
        
        if (monto == null || monto.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValidationException("El monto debe ser mayor a cero", "monto", "Monto inválido");
        }

        if (referencia == null || referencia.isBlank()) {
            throw new ValidationException("La referencia del pago es requerida", "referencia", "Referencia inválida");
        }
        
        // Aquí se integraría con la API real de Stripe
        // Por ahora simulamos el procesamiento
        String transactionId = "stripe_" + System.currentTimeMillis();
        
        log.info("Pago procesado exitosamente con Stripe: {}", transactionId);
        return transactionId;
    }

    @Override
    public String verificarEstado(String transactionId) throws ValidationException, BusinessException {
        log.debug("Verificando estado de transacción Stripe: {}", transactionId);
        
        if (transactionId == null || transactionId.isBlank()) {
            throw new ValidationException("El ID de transacción es requerido", "transactionId", "ID inválido");
        }
        
        // Aquí se consultaría el estado real en Stripe
        return "COMPLETADO";
    }

    @Override
    public String getNombrePasarela() {
        return "Stripe";
    }
}

