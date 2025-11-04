package com.veterinaria.clinica_veternica.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Excepción lanzada cuando un usuario no está autorizado para realizar una acción.
 * Retorna HTTP 401 Unauthorized.
 *
 * @author Clínica Veterinaria Team
 * @version 1.0
 * @since 2025-11-03
 */
@ResponseStatus(value = HttpStatus.UNAUTHORIZED)
public class UnauthorizedException extends RuntimeException {

    /**
     * Constructor con mensaje personalizado.
     *
     * @param message Mensaje del error
     */
    public UnauthorizedException(String message) {
        super(message);
    }

    /**
     * Constructor con mensaje y causa.
     *
     * @param message Mensaje del error
     * @param cause Causa del error
     */
    public UnauthorizedException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructor por defecto con mensaje estándar.
     */
    public UnauthorizedException() {
        super("No está autorizado para realizar esta acción");
    }
}
