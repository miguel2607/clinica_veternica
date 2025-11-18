package com.veterinaria.clinica_veternica.dto.response.usuario;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO de Response para un Recepcionista.
 * Incluye datos de Personal y específicos de Recepcionista.
 *
 * @author Clínica Veterinaria Team
 * @version 1.0
 * @since 2025-11-12
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecepcionistaResponseDTO {

    /**
     * Identificador único del recepcionista.
     */
    private Long idPersonal;

    /**
     * Nombres del recepcionista.
     */
    private String nombres;

    /**
     * Apellidos del recepcionista.
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

