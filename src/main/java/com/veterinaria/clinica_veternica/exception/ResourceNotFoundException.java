package com.veterinaria.clinica_veternica.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Excepción lanzada cuando no se encuentra un recurso solicitado.
 * Retorna HTTP 404 Not Found.
 *
 * @author Clínica Veterinaria Team
 * @version 1.0
 * @since 2025-11-03
 */
@ResponseStatus(value = HttpStatus.NOT_FOUND)
@SuppressWarnings("unused")
public class ResourceNotFoundException extends RuntimeException {

    private final String resourceName;
    private final String fieldName;
    private final transient Object fieldValue;

    /**
     * Constructor con mensaje personalizado.
     *
     * @param message Mensaje del error
     */
    public ResourceNotFoundException(String message) {
        super(message);
        this.resourceName = null;
        this.fieldName = null;
        this.fieldValue = null;
    }

    /**
     * Constructor con nombre de recurso, campo y valor.
     *
     * @param resourceName Nombre del recurso (ej: "Mascota")
     * @param fieldName Nombre del campo (ej: "id")
     * @param fieldValue Valor del campo (ej: 123)
     */
    public ResourceNotFoundException(String resourceName, String fieldName, Object fieldValue) {
        super(String.format("%s no encontrado con %s: '%s'", resourceName, fieldName, fieldValue));
        this.resourceName = resourceName;
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
    }

    /**
     * Constructor con mensaje y causa.
     *
     * @param message Mensaje del error
     * @param cause Causa del error
     */
    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
        this.resourceName = null;
        this.fieldName = null;
        this.fieldValue = null;
    }

    public String getResourceName() {
        return resourceName;
    }

    public String getFieldName() {
        return fieldName;
    }

    public Object getFieldValue() {
        return fieldValue;
    }
}
