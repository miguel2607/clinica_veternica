package com.veterinaria.clinica_veternica.dto.request.usuario;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para solicitud de reseteo de contraseña (solo admin).
 *
 * @author Clínica Veterinaria Team
 * @version 1.0
 * @since 2025-11-06
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Datos para resetear la contraseña de un usuario (solo administrador)")
public class ResetearPasswordRequestDTO {

    @NotBlank(message = "La nueva contraseña es obligatoria")
    @Schema(description = "Nueva contraseña para el usuario", example = "newPassword123")
    private String nuevaPassword;
}







