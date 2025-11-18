package com.veterinaria.clinica_veternica.dto.request.comunicacion;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de Request para crear una notificación.
 *
 * @author Clínica Veterinaria Team
 * @version 1.0
 * @since 2025-01-XX
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificacionRequestDTO {

    /**
     * ID del usuario destinatario.
     */
    @NotNull(message = "El ID del usuario es obligatorio")
    @Positive(message = "El ID del usuario debe ser positivo")
    private Long idUsuario;

    /**
     * Motivo de la notificación.
     */
    @NotBlank(message = "El motivo es obligatorio")
    @Size(min = 5, max = 200, message = "El motivo debe tener entre 5 y 200 caracteres")
    private String motivo;

    /**
     * Canal de envío.
     * Valores: EMAIL, SMS, WHATSAPP, PUSH
     */
    @NotBlank(message = "El canal es obligatorio")
    @Pattern(regexp = "^(EMAIL|SMS|WHATSAPP|PUSH)$", 
             message = "El canal debe ser: EMAIL, SMS, WHATSAPP o PUSH")
    private String canal;

    /**
     * Mensaje adicional (opcional).
     */
    @Size(max = 1000, message = "El mensaje no puede exceder 1000 caracteres")
    private String mensaje;
}

