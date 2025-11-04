package com.veterinaria.clinica_veternica.util;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Clase de utilidades para construcción de respuestas HTTP estandarizadas.
 *
 * Proporciona métodos helper para crear respuestas consistentes en toda la API,
 * incluyendo respuestas de éxito, error y paginadas.
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
        response.put("success", true);
        response.put("timestamp", DateUtils.getCurrentDateTime().toString());
        response.put("data", data);
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
        response.put("success", true);
        response.put("timestamp", DateUtils.getCurrentDateTime().toString());
        response.put("message", message);
        response.put("data", data);
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
        response.put("success", true);
        response.put("timestamp", DateUtils.getCurrentDateTime().toString());
        response.put("message", message);
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
        response.put("success", true);
        response.put("timestamp", DateUtils.getCurrentDateTime().toString());
        response.put("message", "Recurso creado exitosamente");
        response.put("data", data);
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
        response.put("success", true);
        response.put("timestamp", DateUtils.getCurrentDateTime().toString());
        response.put("message", message);
        response.put("data", data);
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
        response.put("success", false);
        response.put("timestamp", DateUtils.getCurrentDateTime().toString());
        response.put("error", message);
        response.put("status", status.value());
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
        response.put("success", false);
        response.put("timestamp", DateUtils.getCurrentDateTime().toString());
        response.put("error", message);
        response.put("details", details);
        response.put("status", status.value());
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

    // ===================================================================
    // RESPUESTAS PAGINADAS
    // ===================================================================

    /**
     * Crea una respuesta paginada con metadata de paginación.
     *
     * @param page Página de resultados de Spring Data
     * @param <T> Tipo de dato de los elementos
     * @return ResponseEntity con datos paginados y metadata
     */
    public static <T> ResponseEntity<Map<String, Object>> paginated(Page<T> page) {
        Map<String, Object> response = new HashMap<>();
        Map<String, Object> pagination = new HashMap<>();

        // Metadata de paginación
        pagination.put("currentPage", page.getNumber());
        pagination.put("totalPages", page.getTotalPages());
        pagination.put("totalElements", page.getTotalElements());
        pagination.put("pageSize", page.getSize());
        pagination.put("hasNext", page.hasNext());
        pagination.put("hasPrevious", page.hasPrevious());
        pagination.put("isFirst", page.isFirst());
        pagination.put("isLast", page.isLast());

        response.put("success", true);
        response.put("timestamp", DateUtils.getCurrentDateTime().toString());
        response.put("data", page.getContent());
        response.put("pagination", pagination);

        return ResponseEntity.ok(response);
    }

    /**
     * Crea una respuesta paginada con mensaje personalizado.
     *
     * @param page Página de resultados de Spring Data
     * @param message Mensaje personalizado
     * @param <T> Tipo de dato de los elementos
     * @return ResponseEntity con datos paginados, mensaje y metadata
     */
    public static <T> ResponseEntity<Map<String, Object>> paginated(Page<T> page, String message) {
        Map<String, Object> response = new HashMap<>();
        Map<String, Object> pagination = new HashMap<>();

        pagination.put("currentPage", page.getNumber());
        pagination.put("totalPages", page.getTotalPages());
        pagination.put("totalElements", page.getTotalElements());
        pagination.put("pageSize", page.getSize());
        pagination.put("hasNext", page.hasNext());
        pagination.put("hasPrevious", page.hasPrevious());
        pagination.put("isFirst", page.isFirst());
        pagination.put("isLast", page.isLast());

        response.put("success", true);
        response.put("timestamp", DateUtils.getCurrentDateTime().toString());
        response.put("message", message);
        response.put("data", page.getContent());
        response.put("pagination", pagination);

        return ResponseEntity.ok(response);
    }

    // ===================================================================
    // UTILIDADES DE PAGINACIÓN
    // ===================================================================

    /**
     * Crea un objeto Pageable con valores por defecto o proporcionados.
     *
     * @param page Número de página (opcional, default: 0)
     * @param size Tamaño de página (opcional, default: 20)
     * @param sortBy Campo de ordenamiento (opcional, default: "id")
     * @param sortDir Dirección de ordenamiento (opcional, default: "ASC")
     * @return Pageable configurado
     */
    public static Pageable createPageable(Integer page, Integer size, String sortBy, String sortDir) {
        int pageNumber = (page != null && page >= 0) ? page : Constants.DEFAULT_PAGE_NUMBER;
        int pageSize = (size != null && size > 0 && size <= Constants.MAX_PAGE_SIZE)
                       ? size
                       : Constants.DEFAULT_PAGE_SIZE;
        String sortField = (sortBy != null && !sortBy.trim().isEmpty())
                           ? sortBy
                           : Constants.DEFAULT_SORT_BY;
        Sort.Direction direction = (sortDir != null && sortDir.equalsIgnoreCase("DESC"))
                                   ? Sort.Direction.DESC
                                   : Sort.Direction.ASC;

        return PageRequest.of(pageNumber, pageSize, Sort.by(direction, sortField));
    }

    /**
     * Crea un objeto Pageable básico con valores por defecto.
     *
     * @return Pageable con valores por defecto (page: 0, size: 20, sort: id ASC)
     */
    public static Pageable createDefaultPageable() {
        return PageRequest.of(
            Constants.DEFAULT_PAGE_NUMBER,
            Constants.DEFAULT_PAGE_SIZE,
            Sort.by(Sort.Direction.ASC, Constants.DEFAULT_SORT_BY)
        );
    }
}
