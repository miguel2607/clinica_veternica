package com.veterinaria.clinica_veternica.security.jwt;

import com.veterinaria.clinica_veternica.security.service.UserDetailsServiceImpl;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Filtro que intercepta cada petición HTTP para validar el token JWT.
 * Se ejecuta una vez por petición.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;
    private final UserDetailsServiceImpl userDetailsService;
    private final JwtProperties jwtProperties;

    /**
     * Filtra cada petición HTTP y valida el token JWT si está presente.
     *
     * @param request Petición HTTP
     * @param response Respuesta HTTP
     * @param filterChain Cadena de filtros
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        try {
            // Extraer el token JWT del header
            String jwt = parseJwt(request);

            // Validar y procesar el token
            if (jwt != null && jwtUtils.validateJwtToken(jwt)) {
                // Obtener el username del token
                String username = jwtUtils.getUsernameFromJwtToken(jwt);

                // Cargar los detalles del usuario desde la base de datos
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                // Crear el objeto de autenticación
                UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                    );

                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // Establecer la autenticación en el contexto de seguridad
                SecurityContextHolder.getContext().setAuthentication(authentication);

                log.debug("Usuario autenticado: {}", username);
            }
        } catch (JwtException | IllegalArgumentException e) {
            log.error("No se puede establecer la autenticación del usuario: {}", e.getMessage(), e);
        } catch (SecurityException e) {
            log.error("Error de seguridad durante la autenticación: {}", e.getMessage(), e);
        } catch (RuntimeException e) {
            log.error("Error inesperado durante la autenticación: {}", e.getMessage(), e);
        }

        // Continuar con la cadena de filtros
        filterChain.doFilter(request, response);
    }

    /**
     * Extrae el token JWT del header Authorization.
     *
     * @param request Petición HTTP
     * @return Token JWT sin el prefijo "Bearer "
     */
    private String parseJwt(HttpServletRequest request) {
        String headerAuth = request.getHeader(jwtProperties.getHeaderString());
        return jwtUtils.parseJwt(headerAuth);
    }
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String p = request.getRequestURI();
        return p.startsWith("/swagger-ui")
                || p.startsWith("/v3/api-docs")
                || p.startsWith("/api-docs")
                || p.equals("/error")
                || p.startsWith("/api/auth");
    }

}
