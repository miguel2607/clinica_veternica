package com.veterinaria.clinica_veternica.dto.request.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para solicitud de reseteo de contraseña por username (público).
 *
 * @author Clínica Veterinaria Team
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Datos para resetear la contraseña usando el nombre de usuario")
public class ResetPasswordByUsernameRequestDTO {

    @NotBlank(message = "El nombre de usuario es obligatorio")
    @Schema(description = "Nombre de usuario", example = "juan.perez")
    private String username;

    @NotBlank(message = "La nueva contraseña es obligatoria")
    @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
    @Schema(description = "Nueva contraseña", example = "newPassword123")
    private String nuevaPassword;
}

