package com.veterinaria.clinica_veternica.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Excepción lanzada cuando ocurre un error de lógica de negocio.
 * Retorna HTTP 422 Unprocessable Entity.
 *
 * Ejemplos de uso:
 * - Intentar agendar una cita cuando no hay disponibilidad
 * - Intentar dar de alta a una mascota que ya está activa
 * - Stock insuficiente para realizar una operación
 *
 * @author Clínica Veterinaria Team
 * @version 1.0
 * @since 2025-11-03
 */
@ResponseStatus(value = HttpStatus.UNPROCESSABLE_ENTITY)
@SuppressWarnings("unused")
public class BusinessException extends RuntimeException {

    private final String errorCode;

    /**
     * Constructor con mensaje personalizado.
     *
     * @param message Mensaje del error
     */
    public BusinessException(String message) {
        super(message);
        this.errorCode = null;
    }

    /**
     * Constructor con mensaje y código de error.
     *
     * @param message Mensaje del error
     * @param errorCode Código del error (para identificación específica)
     */
    public BusinessException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    /**
     * Constructor con mensaje y causa.
     *
     * @param message Mensaje del error
     * @param cause Causa del error
     */
    public BusinessException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = null;
    }

    /**
     * Constructor con mensaje, código de error y causa.
     *
     * @param message Mensaje del error
     * @param errorCode Código del error
     * @param cause Causa del error
     */
    public BusinessException(String message, String errorCode, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}
