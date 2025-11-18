package com.veterinaria.clinica_veternica.patterns.structural.adapter;

import com.veterinaria.clinica_veternica.exception.BusinessException;
import com.veterinaria.clinica_veternica.exception.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests para los adaptadores de pasarelas de pago (Adapter Pattern)
 */
@SpringBootTest
class PaymentGatewayAdapterTest {

    @Autowired
    private PayPalPaymentAdapter payPalAdapter;

    @Autowired
    private StripePaymentAdapter stripeAdapter;

    private BigDecimal montoValido;
    private String referenciaValida;

    @BeforeEach
    void setUp() {
        montoValido = new BigDecimal("100.00");
        referenciaValida = "REF-123456";
    }

    @Test
    @DisplayName("PayPal - Debe procesar pago exitosamente")
    void testPayPalProcesarPago() throws ValidationException, BusinessException {
        String transactionId = payPalAdapter.procesarPago(montoValido, referenciaValida);

        assertNotNull(transactionId);
        assertTrue(transactionId.startsWith("PAYPAL-"), 
                "El transactionId debe comenzar con 'PAYPAL-'");
        assertEquals("PayPal", payPalAdapter.getNombrePasarela());
    }

    @Test
    @DisplayName("PayPal - Debe verificar estado de transacción")
    void testPayPalVerificarEstado() throws ValidationException, BusinessException {
        // Primero procesar un pago para obtener un transactionId válido
        String transactionId = payPalAdapter.procesarPago(montoValido, referenciaValida);
        
        // Verificar el estado usando el transactionId válido
        String estado = payPalAdapter.verificarEstado(transactionId);

        assertNotNull(estado);
        assertEquals("COMPLETADO", estado);
    }

    @Test
    @DisplayName("PayPal - Debe lanzar excepción con transactionId inválido")
    void testPayPalVerificarEstadoInvalido() {
        assertThrows(ValidationException.class, () -> {
            payPalAdapter.verificarEstado("INVALID-123");
        });
    }

    @Test
    @DisplayName("Stripe - Debe procesar pago exitosamente")
    void testStripeProcesarPago() throws ValidationException, BusinessException {
        String transactionId = stripeAdapter.procesarPago(montoValido, referenciaValida);

        assertNotNull(transactionId);
        assertTrue(transactionId.startsWith("stripe_"), 
                "El transactionId debe comenzar con 'stripe_'");
        assertEquals("Stripe", stripeAdapter.getNombrePasarela());
    }

    @Test
    @DisplayName("Stripe - Debe verificar estado de transacción")
    void testStripeVerificarEstado() throws ValidationException, BusinessException {
        // Primero procesar un pago para obtener un transactionId válido
        String transactionId = stripeAdapter.procesarPago(montoValido, referenciaValida);
        
        // Verificar el estado usando el transactionId válido
        String estado = stripeAdapter.verificarEstado(transactionId);

        assertNotNull(estado);
        assertEquals("COMPLETADO", estado);
    }

    @Test
    @DisplayName("Stripe - Debe lanzar excepción con transactionId inválido")
    void testStripeVerificarEstadoInvalido() {
        assertThrows(ValidationException.class, () -> {
            stripeAdapter.verificarEstado(null);
        });
    }

    @Test
    @DisplayName("PayPal - Debe lanzar excepción con monto cero")
    void testPayPalMontoCero() {
        assertThrows(ValidationException.class, () -> {
            payPalAdapter.procesarPago(BigDecimal.ZERO, referenciaValida);
        });
    }

    @Test
    @DisplayName("PayPal - Debe lanzar excepción con monto negativo")
    void testPayPalMontoNegativo() {
        BigDecimal montoNegativo = new BigDecimal("-10.00");
        assertThrows(ValidationException.class, () -> {
            payPalAdapter.procesarPago(montoNegativo, referenciaValida);
        });
    }

    @Test
    @DisplayName("Stripe - Debe lanzar excepción con monto cero")
    void testStripeMontoCero() {
        assertThrows(ValidationException.class, () -> {
            stripeAdapter.procesarPago(BigDecimal.ZERO, referenciaValida);
        });
    }

    @Test
    @DisplayName("Stripe - Debe lanzar excepción con monto negativo")
    void testStripeMontoNegativo() {
        BigDecimal montoNegativo = new BigDecimal("-10.00");
        assertThrows(ValidationException.class, () -> {
            stripeAdapter.procesarPago(montoNegativo, referenciaValida);
        });
    }

    @Test
    @DisplayName("PayPal - Debe lanzar excepción con referencia vacía")
    void testPayPalReferenciaVacia() {
        assertThrows(ValidationException.class, () -> {
            payPalAdapter.procesarPago(montoValido, "");
        });
    }

    @Test
    @DisplayName("PayPal - Debe lanzar excepción con referencia nula")
    void testPayPalReferenciaNula() {
        assertThrows(ValidationException.class, () -> {
            payPalAdapter.procesarPago(montoValido, null);
        });
    }

    @Test
    @DisplayName("Stripe - Debe lanzar excepción con referencia vacía")
    void testStripeReferenciaVacia() {
        assertThrows(ValidationException.class, () -> {
            stripeAdapter.procesarPago(montoValido, "");
        });
    }

    @Test
    @DisplayName("Stripe - Debe lanzar excepción con referencia nula")
    void testStripeReferenciaNula() {
        assertThrows(ValidationException.class, () -> {
            stripeAdapter.procesarPago(montoValido, null);
        });
    }
}

