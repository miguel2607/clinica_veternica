package com.veterinaria.clinica_veternica.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.HashMap;
import java.util.Map;

/**
 * Excepción lanzada cuando falla la validación de datos.
 * Retorna HTTP 400 Bad Request.
 *
 * @author Clínica Veterinaria Team
 * @version 1.0
 * @since 2025-11-03
 */
@ResponseStatus(value = HttpStatus.BAD_REQUEST)
@SuppressWarnings("unused")
public class ValidationException extends RuntimeException {

    private final Map<String, String> errors;

    /**
     * Constructor con mensaje personalizado.
     *
     * @param message Mensaje del error
     */
    public ValidationException(String message) {
        super(message);
        this.errors = new HashMap<>();
    }

    /**
     * Constructor con mensaje y mapa de errores de validación.
     *
     * @param message Mensaje general del error
     * @param errors Mapa de errores por campo
     */
    public ValidationException(String message, Map<String, String> errors) {
        super(message);
        this.errors = errors;
    }

    /**
     * Constructor con mensaje, campo y error específico.
     *
     * @param message Mensaje general del error
     * @param fieldName Nombre del campo con error
     * @param fieldError Descripción del error del campo
     */
    public ValidationException(String message, String fieldName, String fieldError) {
        super(message);
        this.errors = new HashMap<>();
        this.errors.put(fieldName, fieldError);
    }

    /**
     * Constructor con mensaje y causa.
     *
     * @param message Mensaje del error
     * @param cause Causa del error
     */
    public ValidationException(String message, Throwable cause) {
        super(message, cause);
        this.errors = new HashMap<>();
    }

    /**
     * Agrega un error de validación específico.
     *
     * @param fieldName Nombre del campo
     * @param errorMessage Mensaje de error
     */
    public void addError(String fieldName, String errorMessage) {
        this.errors.put(fieldName, errorMessage);
    }

    public Map<String, String> getErrors() {
        return errors;
    }
}
