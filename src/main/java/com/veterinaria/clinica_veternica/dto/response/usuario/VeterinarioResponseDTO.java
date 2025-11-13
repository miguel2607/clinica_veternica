package com.veterinaria.clinica_veternica.dto.response.usuario;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO de Response para un Veterinario.
 * Incluye datos de Personal y específicos de Veterinario.
 *
 * @author Clínica Veterinaria Team
 * @version 1.0
 * @since 2025-11-03
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VeterinarioResponseDTO {

    /**
     * Identificador único del veterinario.
     */
    private Long idPersonal;

    /**
     * Nombres del veterinario.
     */
    private String nombres;

    /**
     * Apellidos del veterinario.
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
     * Especialidad del veterinario.
     */
    private String especialidad;

    /**
     * Número de registro profesional.
     */
    private String registroProfesional;

    /**
     * Años de experiencia profesional.
     */
    private Integer aniosExperiencia;

    /**
     * Estado activo/inactivo.
     */
    private Boolean activo;

    /**
     * Indica si está disponible para atención.
     */
    private Boolean disponible;

    /**
     * Cantidad de citas programadas.
     */
    private Integer cantidadCitas;

    /**
     * Cantidad de horarios registrados.
     */
    private Integer cantidadHorarios;

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
