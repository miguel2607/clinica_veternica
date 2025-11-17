package com.veterinaria.clinica_veternica.util;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

/**
 * Clase de utilidades para construcción de respuestas HTTP estandarizadas.
 *
 * Proporciona métodos helper para crear respuestas consistentes en toda la API,
 * incluyendo respuestas de éxito y error.
 *
 * Beneficios:
 * - Respuestas uniformes en toda la API
 * - Reduce código duplicado en controllers
 * - Facilita el consumo del API por el frontend
 * - Incluye metadata útil (timestamp, status)
 *
 * @author Clínica Veterinaria Team
 * @version 1.0
 * @since 2025-11-02
 */
public final class ResponseUtils {

    // Constantes para claves de respuesta
    private static final String KEY_SUCCESS = "success";
    private static final String KEY_TIMESTAMP = "timestamp";
    private static final String KEY_MESSAGE = "message";
    private static final String KEY_DATA = "data";
    private static final String KEY_ERROR = "error";
    private static final String KEY_DETAILS = "details";
    private static final String KEY_STATUS = "status";

    // Constructor privado para evitar instanciación
    private ResponseUtils() {
        throw new UnsupportedOperationException("Esta es una clase de utilidad y no puede ser instanciada");
    }

    // ===================================================================
    // RESPUESTAS DE ÉXITO
    // ===================================================================

    /**
     * Crea una respuesta exitosa con datos.
     *
     * @param data Datos a incluir en la respuesta
     * @param <T> Tipo de dato
     * @return ResponseEntity con status 200 OK
     */
    public static <T> ResponseEntity<Map<String, Object>> success(T data) {
        Map<String, Object> response = new HashMap<>();
        response.put(KEY_SUCCESS, true);
        response.put(KEY_TIMESTAMP, DateUtils.getCurrentDateTime().toString());
        response.put(KEY_DATA, data);
        return ResponseEntity.ok(response);
    }

    /**
     * Crea una respuesta exitosa con datos y mensaje personalizado.
     *
     * @param data Datos a incluir en la respuesta
     * @param message Mensaje descriptivo
     * @param <T> Tipo de dato
     * @return ResponseEntity con status 200 OK
     */
    public static <T> ResponseEntity<Map<String, Object>> success(T data, String message) {
        Map<String, Object> response = new HashMap<>();
        response.put(KEY_SUCCESS, true);
        response.put(KEY_TIMESTAMP, DateUtils.getCurrentDateTime().toString());
        response.put(KEY_MESSAGE, message);
        response.put(KEY_DATA, data);
        return ResponseEntity.ok(response);
    }

    /**
     * Crea una respuesta exitosa simple con solo un mensaje.
     *
     * @param message Mensaje descriptivo
     * @return ResponseEntity con status 200 OK
     */
    public static ResponseEntity<Map<String, Object>> successMessage(String message) {
        Map<String, Object> response = new HashMap<>();
        response.put(KEY_SUCCESS, true);
        response.put(KEY_TIMESTAMP, DateUtils.getCurrentDateTime().toString());
        response.put(KEY_MESSAGE, message);
        return ResponseEntity.ok(response);
    }

    /**
     * Crea una respuesta exitosa para operación de creación.
     *
     * @param data Datos del recurso creado
     * @param <T> Tipo de dato
     * @return ResponseEntity con status 201 CREATED
     */
    public static <T> ResponseEntity<Map<String, Object>> created(T data) {
        Map<String, Object> response = new HashMap<>();
        response.put(KEY_SUCCESS, true);
        response.put(KEY_TIMESTAMP, DateUtils.getCurrentDateTime().toString());
        response.put(KEY_MESSAGE, "Recurso creado exitosamente");
        response.put(KEY_DATA, data);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Crea una respuesta exitosa para operación de creación con mensaje personalizado.
     *
     * @param data Datos del recurso creado
     * @param message Mensaje personalizado
     * @param <T> Tipo de dato
     * @return ResponseEntity con status 201 CREATED
     */
    public static <T> ResponseEntity<Map<String, Object>> created(T data, String message) {
        Map<String, Object> response = new HashMap<>();
        response.put(KEY_SUCCESS, true);
        response.put(KEY_TIMESTAMP, DateUtils.getCurrentDateTime().toString());
        response.put(KEY_MESSAGE, message);
        response.put(KEY_DATA, data);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // ===================================================================
    // RESPUESTAS DE ERROR
    // ===================================================================

    /**
     * Crea una respuesta de error con mensaje.
     *
     * @param message Mensaje de error
     * @param status HttpStatus del error
     * @return ResponseEntity con el status especificado
     */
    public static ResponseEntity<Map<String, Object>> error(String message, HttpStatus status) {
        Map<String, Object> response = new HashMap<>();
        response.put(KEY_SUCCESS, false);
        response.put(KEY_TIMESTAMP, DateUtils.getCurrentDateTime().toString());
        response.put(KEY_ERROR, message);
        response.put(KEY_STATUS, status.value());
        return ResponseEntity.status(status).body(response);
    }

    /**
     * Crea una respuesta de error con mensaje y detalles adicionales.
     *
     * @param message Mensaje principal de error
     * @param details Detalles adicionales del error
     * @param status HttpStatus del error
     * @return ResponseEntity con el status especificado
     */
    public static ResponseEntity<Map<String, Object>> error(String message, Object details, HttpStatus status) {
        Map<String, Object> response = new HashMap<>();
        response.put(KEY_SUCCESS, false);
        response.put(KEY_TIMESTAMP, DateUtils.getCurrentDateTime().toString());
        response.put(KEY_ERROR, message);
        response.put(KEY_DETAILS, details);
        response.put(KEY_STATUS, status.value());
        return ResponseEntity.status(status).body(response);
    }

    /**
     * Crea una respuesta para recurso no encontrado (404).
     *
     * @param resourceName Nombre del recurso no encontrado
     * @return ResponseEntity con status 404 NOT FOUND
     */
    public static ResponseEntity<Map<String, Object>> notFound(String resourceName) {
        String message = resourceName + " no encontrado";
        return error(message, HttpStatus.NOT_FOUND);
    }

    /**
     * Crea una respuesta para error de validación (400).
     *
     * @param details Detalles de los errores de validación
     * @return ResponseEntity con status 400 BAD REQUEST
     */
    public static ResponseEntity<Map<String, Object>> badRequest(Object details) {
        return error("Error de validación", details, HttpStatus.BAD_REQUEST);
    }

    /**
     * Crea una respuesta para error de autenticación (401).
     *
     * @param message Mensaje de error de autenticación
     * @return ResponseEntity con status 401 UNAUTHORIZED
     */
    public static ResponseEntity<Map<String, Object>> unauthorized(String message) {
        return error(message != null ? message : "No autorizado", HttpStatus.UNAUTHORIZED);
    }

    /**
     * Crea una respuesta para error de permisos (403).
     *
     * @param message Mensaje de error de permisos
     * @return ResponseEntity con status 403 FORBIDDEN
     */
    public static ResponseEntity<Map<String, Object>> forbidden(String message) {
        return error(message != null ? message : "Acceso denegado", HttpStatus.FORBIDDEN);
    }

    /**
     * Crea una respuesta para conflicto (409).
     *
     * @param message Mensaje del conflicto
     * @return ResponseEntity con status 409 CONFLICT
     */
    public static ResponseEntity<Map<String, Object>> conflict(String message) {
        return error(message, HttpStatus.CONFLICT);
    }

    /**
     * Crea una respuesta para error interno del servidor (500).
     *
     * @param message Mensaje del error
     * @return ResponseEntity con status 500 INTERNAL SERVER ERROR
     */
    public static ResponseEntity<Map<String, Object>> internalError(String message) {
        return error(message != null ? message : "Error interno del servidor",
                     HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
