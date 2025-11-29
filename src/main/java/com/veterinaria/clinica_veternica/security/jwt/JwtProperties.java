package com.veterinaria.clinica_veternica.security.jwt;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Propiedades de configuración para JWT.
 * Se cargan desde application.properties con el prefijo "jwt".
 */
@Component
@ConfigurationProperties(prefix = "jwt")
@Getter
@Setter
public class JwtProperties {

    /**
     * Clave secreta para firmar los tokens JWT.
     * Se carga automáticamente desde el archivo .env (variable JWT_SECRET).
     */
    private String secret;

    /**
     * Tiempo de expiración del token en milisegundos.
     * Por defecto: 86400000 ms = 24 horas
     */
    private Long expiration = 86400000L;

    /**
     * Prefijo del token en el header.
     * Por defecto: "Bearer "
     */
    private String tokenPrefix = "Bearer ";

    /**
     * Nombre del header HTTP donde se envía el token.
     * Por defecto: "Authorization"
     */
    private String headerString = "Authorization";

    /**
     * Tiempo de expiración del refresh token en milisegundos.
     * Por defecto: 604800000 ms = 7 días
     */
    private Long refreshExpiration = 604800000L;
}
