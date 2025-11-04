package com.veterinaria.clinica_veternica.dto.response.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para respuesta de inicio de sesión exitoso.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginResponseDTO {

    /**
     * Token JWT de acceso.
     */
    private String token;

    /**
     * Tipo de token (siempre "Bearer").
     */
    @Builder.Default
    private String type = "Bearer";

    /**
     * ID del usuario autenticado.
     */
    private Long idUsuario;

    /**
     * Nombre de usuario.
     */
    private String username;

    /**
     * Email del usuario.
     */
    private String email;

    /**
     * Rol del usuario.
     */
    private String rol;

    /**
     * Tiempo de expiración del token en milisegundos.
     */
    private Long expiresIn;
}
