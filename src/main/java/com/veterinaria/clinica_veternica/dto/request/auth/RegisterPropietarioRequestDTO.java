package com.veterinaria.clinica_veternica.dto.request.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para solicitud de registro de propietario (público).
 * Incluye datos del usuario y del propietario.
 *
 * @author Clínica Veterinaria Team
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Datos para registro de nuevo propietario")
public class RegisterPropietarioRequestDTO {

    // Datos del usuario
    @NotBlank(message = "El username es obligatorio")
    @Size(min = 3, max = 50, message = "El username debe tener entre 3 y 50 caracteres")
    @Schema(description = "Nombre de usuario único", example = "juan.perez")
    private String username;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El email debe ser válido")
    @Size(max = 100, message = "El email no puede exceder 100 caracteres")
    @Schema(description = "Correo electrónico", example = "juan.perez@example.com")
    private String email;

    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
    @Schema(description = "Contraseña", example = "password123")
    private String password;

    // Datos del propietario
    @NotBlank(message = "El documento es obligatorio")
    @Size(min = 6, max = 20, message = "El documento debe tener entre 6 y 20 caracteres")
    @Schema(description = "Número de documento", example = "1234567890")
    private String documento;

    @NotBlank(message = "El tipo de documento es obligatorio")
    @Size(max = 30, message = "El tipo de documento no puede exceder 30 caracteres")
    @Schema(description = "Tipo de documento", example = "CC")
    private String tipoDocumento;

    @NotBlank(message = "Los nombres son obligatorios")
    @Size(min = 2, max = 100, message = "Los nombres deben tener entre 2 y 100 caracteres")
    @Schema(description = "Nombres del propietario", example = "Juan")
    private String nombres;

    @NotBlank(message = "Los apellidos son obligatorios")
    @Size(min = 2, max = 100, message = "Los apellidos deben tener entre 2 y 100 caracteres")
    @Schema(description = "Apellidos del propietario", example = "Pérez García")
    private String apellidos;

    @NotBlank(message = "El teléfono es obligatorio")
    @Pattern(regexp = "^[+]?\\d{7,15}$", message = "Formato de teléfono inválido")
    @Schema(description = "Teléfono de contacto", example = "3001234567")
    private String telefono;

    @Size(max = 300, message = "La dirección no puede exceder 300 caracteres")
    @Schema(description = "Dirección de residencia", example = "Calle 123 #45-67")
    private String direccion;
}

