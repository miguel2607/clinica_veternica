package com.veterinaria.clinica_veternica.patterns.structural.adapter;

import com.veterinaria.clinica_veternica.exception.BusinessException;
import com.veterinaria.clinica_veternica.exception.ValidationException;

import java.math.BigDecimal;

/**
 * Patrón Adapter: PaymentGatewayAdapter (Interface)
 *
 * Define la interfaz común para adaptar diferentes pasarelas de pago
 * externas a un formato unificado.
 *
 * Justificación:
 * - Diferentes APIs de pago tienen interfaces incompatibles
 * - El Adapter las unifica en una interfaz común
 * - Facilita cambiar de pasarela sin modificar código del negocio
 * - Permite usar múltiples pasarelas simultáneamente
 *
 * @author Clínica Veterinaria Team
 * @version 1.0
 * @since 2025-11-04
 */
public interface PaymentGatewayAdapter {

    /**
     * Procesa un pago a través de la pasarela.
     *
     * @param monto Monto a pagar
     * @param referencia Referencia del pago
     * @return ID de transacción de la pasarela
     * @throws ValidationException Si los datos del pago son inválidos
     * @throws BusinessException Si el pago falla por razones de negocio
     */
    String procesarPago(BigDecimal monto, String referencia) throws ValidationException, BusinessException;

    /**
     * Verifica el estado de una transacción.
     *
     * @param transactionId ID de la transacción
     * @return Estado de la transacción
     * @throws ValidationException Si el ID de transacción es inválido
     * @throws BusinessException Si hay un error al verificar el estado
     */
    String verificarEstado(String transactionId) throws ValidationException, BusinessException;

    /**
     * Obtiene el nombre de la pasarela.
     *
     * @return Nombre de la pasarela
     */
    String getNombrePasarela();
}

