package com.veterinaria.clinica_veternica.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.veterinaria.clinica_veternica.dto.response.ErrorResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.OffsetDateTime;

/**
 * Manejador de errores de autenticación JWT.
 * Se ejecuta cuando un usuario no autenticado intenta acceder a un recurso protegido.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper; // ObjectMapper de Spring Boot (ya con soporte JavaTimeModule)

    /**
     * Maneja errores de autenticación retornando un JSON con el error.
     *
     * @param request Petición HTTP
     * @param response Respuesta HTTP
     * @param authException Excepción de autenticación
     */
    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException)
            throws IOException, ServletException {

        log.warn("Error de autenticación: {}", authException.getMessage());

        // Configurar la respuesta HTTP
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        // Crear el objeto de error con marca de tiempo ISO-8601
        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(OffsetDateTime.now().toLocalDateTime()) // más estándar para APIs
                .status(HttpServletResponse.SC_UNAUTHORIZED)
                .error("Unauthorized")
                .message("Error de autenticación: " + authException.getMessage())
                .path(request.getRequestURI())
                .build();

        // Escribir la respuesta JSON en la salida
        objectMapper.writeValue(response.getOutputStream(), errorResponse);
    }
}
