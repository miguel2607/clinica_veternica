package com.veterinaria.clinica_veternica.exception;

import com.veterinaria.clinica_veternica.dto.response.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Manejador global de excepciones para toda la aplicación.
 * Captura y procesa todas las excepciones, devolviendo respuestas consistentes.
 * 
 * Excluye los paquetes de SpringDoc OpenAPI para evitar conflictos.
 *
 * @author Clínica Veterinaria Team
 * @version 1.0
 * @since 2025-11-03
 */
@Slf4j
@RestControllerAdvice(basePackages = "com.veterinaria.clinica_veternica.controller")
public class GlobalExceptionHandler {

    /**
     * Maneja ResourceNotFoundException (404 Not Found).
     *
     * @param ex Excepción
     * @param request Request web
     * @return ResponseEntity con ErrorResponse
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(
            ResourceNotFoundException ex,
            WebRequest request) {

        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.NOT_FOUND.value())
                .error(HttpStatus.NOT_FOUND.getReasonPhrase())
                .message(ex.getMessage())
                .path(getPath(request))
                .traceId(generateTraceId())
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    /**
     * Maneja UnauthorizedException (401 Unauthorized).
     *
     * @param ex Excepción
     * @param request Request web
     * @return ResponseEntity con ErrorResponse
     */
    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ErrorResponse> handleUnauthorizedException(
            UnauthorizedException ex,
            WebRequest request) {

        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.UNAUTHORIZED.value())
                .error(HttpStatus.UNAUTHORIZED.getReasonPhrase())
                .message(ex.getMessage())
                .path(getPath(request))
                .traceId(generateTraceId())
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
    }

    /**
     * Maneja ValidationException (400 Bad Request).
     *
     * @param ex Excepción
     * @param request Request web
     * @return ResponseEntity con ErrorResponse
     */
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(
            ValidationException ex,
            WebRequest request) {

        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .message(ex.getMessage())
                .path(getPath(request))
                .validationErrors(ex.getErrors())
                .traceId(generateTraceId())
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Maneja BusinessException (422 Unprocessable Entity).
     *
     * @param ex Excepción
     * @param request Request web
     * @return ResponseEntity con ErrorResponse
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(
            BusinessException ex,
            WebRequest request) {

        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.UNPROCESSABLE_ENTITY.value())
                .error(HttpStatus.UNPROCESSABLE_ENTITY.getReasonPhrase())
                .message(ex.getMessage())
                .path(getPath(request))
                .traceId(generateTraceId())
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.UNPROCESSABLE_ENTITY);
    }

    /**
     * Maneja MethodArgumentNotValidException (errores de @Valid en RequestBody).
     * Se lanza automáticamente cuando fallan las validaciones de Bean Validation.
     *
     * @param ex Excepción
     * @param request Request web
     * @return ResponseEntity con ErrorResponse
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            WebRequest request) {

        Map<String, String> validationErrors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            validationErrors.put(fieldName, errorMessage);
        });

        List<String> errorMessages = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .toList();

        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .message("Error de validación en los datos enviados")
                .path(getPath(request))
                .validationErrors(validationErrors)
                .errors(errorMessages)
                .traceId(generateTraceId())
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Maneja IllegalArgumentException (400 Bad Request).
     *
     * @param ex Excepción
     * @param request Request web
     * @return ResponseEntity con ErrorResponse
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(
            IllegalArgumentException ex,
            WebRequest request) {

        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .message(ex.getMessage())
                .path(getPath(request))
                .traceId(generateTraceId())
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Maneja HttpMessageNotReadableException (400 Bad Request).
     * Se lanza cuando hay errores al deserializar el JSON del request body.
     * Por ejemplo: formato de fecha/hora incorrecto, tipos incompatibles, etc.
     *
     * @param ex Excepción
     * @param request Request web
     * @return ResponseEntity con ErrorResponse
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex,
            WebRequest request) {

        String mensaje = "Error al procesar el JSON enviado. ";
        String causa = ex.getCause() != null ? ex.getCause().getMessage() : ex.getMessage();

        if (causa != null) {
            if (causa.contains("LocalTime")) {
                mensaje += "El formato de hora debe ser HH:mm:ss, HH:mm, H:mm, H (ej: '09:00:00', '09:00', '9:00', '9') " +
                          "o formato 12 horas: h:mm a, h a (ej: '9:00 AM', '9 PM', '9:30 PM', '9AM'). Valor recibido no válido.";
            } else if (causa.contains("LocalDate")) {
                mensaje += "El formato de fecha debe ser yyyy-MM-dd (ej: '2025-11-15'). Valor recibido no válido.";
            } else if (causa.contains("LocalDateTime")) {
                mensaje += "El formato de fecha y hora debe ser yyyy-MM-ddTHH:mm:ss (ej: '2025-11-15T09:00:00'). Valor recibido no válido.";
            } else if (causa.contains("Cannot deserialize")) {
                mensaje += "Error de formato en los datos enviados: " + causa;
            } else {
                mensaje += causa;
            }
        } else {
            mensaje += "Verifique que el formato del JSON sea correcto.";
        }

        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .message(mensaje)
                .path(getPath(request))
                .traceId(generateTraceId())
                .build();

        log.warn("Error al deserializar JSON: {}", causa);

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Maneja cualquier otra excepción no capturada específicamente (500 Internal Server Error).
     *
     * @param ex Excepción
     * @param request Request web
     * @return ResponseEntity con ErrorResponse
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGlobalException(
            Exception ex,
            WebRequest request) {

        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .error(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
                .message("Ha ocurrido un error interno en el servidor")
                .path(getPath(request))
                .traceId(generateTraceId())
                .build();

        // Log del error usando SLF4J
        log.error("Error no manejado en el sistema", ex);

        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Extrae la ruta de la petición.
     *
     * @param request Request web
     * @return Ruta de la petición
     */
    private String getPath(WebRequest request) {
        String description = request.getDescription(false);
        return description.replace("uri=", "");
    }

    /**
     * Genera un ID único para tracking de errores.
     *
     * @return Trace ID único
     */
    private String generateTraceId() {
        return UUID.randomUUID().toString();
    }
}
