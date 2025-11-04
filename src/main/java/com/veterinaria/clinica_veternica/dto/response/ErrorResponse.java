package com.veterinaria.clinica_veternica.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * DTO estándar para respuestas de error.
 * Proporciona una estructura consistente para todos los errores de la API.
 *
 * @author Clínica Veterinaria Team
 * @version 1.0
 * @since 2025-11-03
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ErrorResponse {

    /**
     * Timestamp del error.
     */
    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();

    /**
     * Código de estado HTTP.
     */
    private Integer status;

    /**
     * Nombre del error HTTP (ej: "Bad Request", "Not Found").
     */
    private String error;

    /**
     * Mensaje descriptivo del error.
     */
    private String message;

    /**
     * Ruta de la petición que causó el error.
     */
    private String path;

    /**
     * Errores de validación específicos (para errores de validación de campos).
     */
    private Map<String, String> validationErrors;

    /**
     * Lista de mensajes de error adicionales.
     */
    private List<String> errors;

    /**
     * Trace ID para seguimiento de errores (útil para logging y debugging).
     */
    private String traceId;

    /**
     * Constructor simple para errores básicos.
     *
     * @param status Código HTTP
     * @param error Nombre del error
     * @param message Mensaje del error
     * @param path Ruta de la petición
     */
    public ErrorResponse(Integer status, String error, String message, String path) {
        this.timestamp = LocalDateTime.now();
        this.status = status;
        this.error = error;
        this.message = message;
        this.path = path;
    }
}
