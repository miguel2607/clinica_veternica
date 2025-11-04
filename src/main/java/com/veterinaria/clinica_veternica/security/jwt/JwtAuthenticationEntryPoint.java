package com.veterinaria.clinica_veternica.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.veterinaria.clinica_veternica.dto.response.ErrorResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;

/**
 * Manejador de errores de autenticación.
 * Se ejecuta cuando un usuario no autenticado intenta acceder a un recurso protegido.
 */
@Component
@Slf4j
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper = new ObjectMapper();

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

        log.error("Error de autenticación: {}", authException.getMessage());

        // Configurar la respuesta HTTP
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        // Crear el objeto de error
        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpServletResponse.SC_UNAUTHORIZED)
                .error("Unauthorized")
                .message("Error de autenticación: " + authException.getMessage())
                .path(request.getServletPath())
                .build();

        // Escribir la respuesta JSON
        response.getOutputStream()
                .println(objectMapper.writeValueAsString(errorResponse));
    }
}
