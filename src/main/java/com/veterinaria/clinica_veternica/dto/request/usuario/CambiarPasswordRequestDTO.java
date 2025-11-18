package com.veterinaria.clinica_veternica.dto.request.usuario;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para solicitud de cambio de contraseña.
 *
 * @author Clínica Veterinaria Team
 * @version 1.0
 * @since 2025-11-06
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Datos para cambiar la contraseña de un usuario")
public class CambiarPasswordRequestDTO {

    @NotBlank(message = "La contraseña actual es obligatoria")
    @Schema(description = "Contraseña actual del usuario", example = "password123")
    private String passwordActual;

    @NotBlank(message = "La nueva contraseña es obligatoria")
    @Schema(description = "Nueva contraseña del usuario", example = "newPassword123")
    private String passwordNueva;
}







