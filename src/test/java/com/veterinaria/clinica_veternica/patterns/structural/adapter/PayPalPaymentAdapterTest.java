package com.veterinaria.clinica_veternica.patterns.structural.adapter;

import com.veterinaria.clinica_veternica.exception.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class PayPalPaymentAdapterTest {

    @InjectMocks
    private PayPalPaymentAdapter payPalAdapter;

    @BeforeEach
    void setUp() {
        payPalAdapter = new PayPalPaymentAdapter();
    }

    @Test
    @DisplayName("Adapter - Debe procesar pago exitosamente")
    void debeProcesarPagoExitosamente() throws Exception {
        BigDecimal monto = BigDecimal.valueOf(100.00);
        String referencia = "REF-123";

        String transactionId = payPalAdapter.procesarPago(monto, referencia);

        assertNotNull(transactionId);
        assertTrue(transactionId.startsWith("PAYPAL-"));
    }

    @Test
    @DisplayName("Adapter - Debe lanzar excepción con monto inválido")
    void debeLanzarExcepcionMontoInvalido() {
        BigDecimal monto = BigDecimal.ZERO;
        String referencia = "REF-123";

        assertThrows(ValidationException.class, () -> payPalAdapter.procesarPago(monto, referencia));
    }

    @Test
    @DisplayName("Adapter - Debe lanzar excepción con referencia vacía")
    void debeLanzarExcepcionReferenciaVacia() {
        BigDecimal monto = BigDecimal.valueOf(100.00);
        String referencia = "";

        assertThrows(ValidationException.class, () -> payPalAdapter.procesarPago(monto, referencia));
    }

    @Test
    @DisplayName("Adapter - Debe verificar estado de transacción")
    void debeVerificarEstadoTransaccion() throws Exception {
        String transactionId = "PAYPAL-1234567890-12345";

        String estado = payPalAdapter.verificarEstado(transactionId);

        assertNotNull(estado);
        assertEquals("COMPLETADO", estado);
    }

    @Test
    @DisplayName("Adapter - Debe retornar nombre de pasarela")
    void debeRetornarNombrePasarela() {
        assertEquals("PayPal", payPalAdapter.getNombrePasarela());
    }
}

