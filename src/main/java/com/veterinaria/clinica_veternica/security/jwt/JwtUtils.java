package com.veterinaria.clinica_veternica.security.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * Utilidad para generar y validar tokens JWT.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class JwtUtils {

    private final JwtProperties jwtProperties;

    /**
     * Genera la clave secreta a partir de la cadena configurada.
     *
     * @return SecretKey para firmar tokens
     */
    private SecretKey getSigningKey() {
        byte[] keyBytes = jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Genera un token JWT para un usuario autenticado.
     *
     * @param authentication Objeto de autenticación de Spring Security
     * @return Token JWT
     */
    public String generateJwtToken(Authentication authentication) {
        UserDetails userPrincipal = (UserDetails) authentication.getPrincipal();
        return generateTokenFromUsername(userPrincipal.getUsername());
    }

    /**
     * Genera un token JWT a partir de un username.
     *
     * @param username Nombre de usuario
     * @return Token JWT
     */
    public String generateTokenFromUsername(String username) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtProperties.getExpiration());

        return Jwts.builder()
                .subject(username)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * Obtiene el username desde un token JWT.
     *
     * @param token Token JWT
     * @return Username
     */
    public String getUsernameFromJwtToken(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    /**
     * Valida un token JWT.
     *
     * @param authToken Token JWT a validar
     * @return true si el token es válido
     */
    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(authToken);
            return true;
        } catch (MalformedJwtException e) {
            log.error("Token JWT malformado: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            log.error("Token JWT expirado: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.error("Token JWT no soportado: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("JWT claims string está vacío: {}", e.getMessage());
        } catch (io.jsonwebtoken.security.SecurityException e) {
            log.error("Error de seguridad en token JWT: {}", e.getMessage());
        }
        return false;
    }

    /**
     * Extrae el token del header Authorization.
     *
     * @param headerAuth Header Authorization completo
     * @return Token JWT sin el prefijo "Bearer "
     */
    public String parseJwt(String headerAuth) {
        if (headerAuth != null && headerAuth.startsWith(jwtProperties.getTokenPrefix())) {
            return headerAuth.substring(jwtProperties.getTokenPrefix().length());
        }
        return null;
    }

    /**
     * Obtiene la fecha de expiración de un token.
     *
     * @param token Token JWT
     * @return Fecha de expiración
     */
    public Date getExpirationDateFromToken(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getExpiration();
    }

    /**
     * Verifica si un token ha expirado.
     *
     * @param token Token JWT
     * @return true si el token ha expirado
     */
    public boolean isTokenExpired(String token) {
        try {
            Date expiration = getExpirationDateFromToken(token);
            return expiration.before(new Date());
        } catch (JwtException e) {
            log.debug("Error JWT al verificar expiración del token: {}", e.getMessage());
            return true;
        } catch (IllegalArgumentException e) {
            log.debug("Argumento inválido al verificar expiración del token: {}", e.getMessage());
            return true;
        }
    }
}
