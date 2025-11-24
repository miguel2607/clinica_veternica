package com.veterinaria.clinica_veternica.dto.request.usuario;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para solicitud de bloqueo de usuario.
 *
 * @author Clínica Veterinaria Team
 * @version 1.0
 * @since 2025-11-06
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Datos para bloquear un usuario")
public class BloquearUsuarioRequestDTO {

    @Schema(description = "Motivo del bloqueo", example = "Violación de políticas de seguridad")
    private String motivo;
}
































