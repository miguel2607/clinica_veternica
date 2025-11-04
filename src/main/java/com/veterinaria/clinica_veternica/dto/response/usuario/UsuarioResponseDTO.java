package com.veterinaria.clinica_veternica.dto.response.usuario;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO de Response para un Usuario.
 *
 * @author Clínica Veterinaria Team
 * @version 1.0
 * @since 2025-11-03
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsuarioResponseDTO {

    /**
     * Identificador único del usuario.
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
     * Estado del usuario (activo/inactivo).
     */
    private Boolean estado;

    /**
     * Última fecha de inicio de sesión.
     */
    private LocalDateTime ultimoLogin;

    /**
     * Número de intentos fallidos de login.
     */
    private Integer intentosFallidos;

    /**
     * Indica si la cuenta está bloqueada temporalmente.
     */
    private Boolean bloqueado;

    /**
     * Fecha y hora de creación.
     */
    private LocalDateTime fechaCreacion;

    /**
     * Fecha y hora de última modificación.
     */
    private LocalDateTime fechaModificacion;
}
