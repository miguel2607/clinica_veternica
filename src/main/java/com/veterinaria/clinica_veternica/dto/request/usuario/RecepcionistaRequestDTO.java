package com.veterinaria.clinica_veternica.dto.request.usuario;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de Request para crear/actualizar un Recepcionista.
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
public class RecepcionistaRequestDTO {

    // Datos de Personal
    /**
     * Nombres del recepcionista.
     */
    @NotBlank(message = "Los nombres son obligatorios")
    @Size(min = 2, max = 100, message = "Los nombres deben tener entre 2 y 100 caracteres")
    private String nombres;

    /**
     * Apellidos del recepcionista.
     */
    @NotBlank(message = "Los apellidos son obligatorios")
    @Size(min = 2, max = 100, message = "Los apellidos deben tener entre 2 y 100 caracteres")
    private String apellidos;

    /**
     * Documento de identidad.
     */
    @NotBlank(message = "El documento es obligatorio")
    @Size(min = 6, max = 20, message = "El documento debe tener entre 6 y 20 caracteres")
    private String documento;

    /**
     * Correo electrónico.
     */
    @NotBlank(message = "El correo es obligatorio")
    @Email(message = "El correo debe ser válido")
    private String correo;

    /**
     * Teléfono de contacto.
     */
    @NotBlank(message = "El teléfono es obligatorio")
    @Pattern(regexp = "^\\+?\\d{10,13}$", message = "El teléfono debe ser válido")
    private String telefono;

    /**
     * Dirección de residencia.
     */
    @Size(max = 200, message = "La dirección no puede exceder 200 caracteres")
    private String direccion;

    /**
     * Estado activo/inactivo.
     */
    private Boolean activo;

    // Datos de Usuario (opcional para creación completa)
    /**
     * Datos de usuario para acceso al sistema.
     */
    private UsuarioRequestDTO usuario;
}

