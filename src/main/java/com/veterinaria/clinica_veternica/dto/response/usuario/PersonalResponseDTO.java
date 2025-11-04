package com.veterinaria.clinica_veternica.dto.response.usuario;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO de Response genérico para Personal
 * (Administrador, Recepcionista, Auxiliar Veterinario).
 *
 * @author Clínica Veterinaria Team
 * @version 1.0
 * @since 2025-11-03
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PersonalResponseDTO {

    /**
     * Identificador único del personal.
     */
    private Long idPersonal;

    /**
     * Nombres del personal.
     */
    private String nombres;

    /**
     * Apellidos del personal.
     */
    private String apellidos;

    /**
     * Nombre completo (nombres + apellidos).
     */
    private String nombreCompleto;

    /**
     * Documento de identidad.
     */
    private String documento;

    /**
     * Correo electrónico.
     */
    private String correo;

    /**
     * Teléfono de contacto.
     */
    private String telefono;

    /**
     * Dirección de residencia.
     */
    private String direccion;

    /**
     * Tipo de personal (ADMINISTRADOR, RECEPCIONISTA, AUXILIAR_VETERINARIO).
     */
    private String tipoPersonal;

    /**
     * Estado activo/inactivo.
     */
    private Boolean activo;

    /**
     * Información del usuario asociado.
     */
    private UsuarioSimpleDTO usuario;

    /**
     * Fecha y hora de creación.
     */
    private LocalDateTime fechaCreacion;

    /**
     * Fecha y hora de última modificación.
     */
    private LocalDateTime fechaModificacion;

    /**
     * DTO simplificado de Usuario.
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class UsuarioSimpleDTO {
        private Long idUsuario;
        private String username;
        private String email;
        private String rol;
        private Boolean estado;
    }
}
