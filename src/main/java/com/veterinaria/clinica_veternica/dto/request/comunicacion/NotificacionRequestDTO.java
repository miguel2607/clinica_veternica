package com.veterinaria.clinica_veternica.dto.request.comunicacion;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de Request para crear/enviar una Notificación.
 *
 * @author Clínica Veterinaria Team
 * @version 1.0
 * @since 2025-11-04
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificacionRequestDTO {

    /**
     * Tipo de notificación (CITA, PAGO, INVENTARIO, SISTEMA, RECORDATORIO).
     */
    @NotBlank(message = "El tipo es obligatorio")
    @Size(max = 30, message = "El tipo no puede exceder 30 caracteres")
    private String tipo;

    /**
     * Canal de envío (EMAIL, SMS, WHATSAPP, PUSH).
     */
    @NotBlank(message = "El canal es obligatorio")
    @Size(max = 20, message = "El canal no puede exceder 20 caracteres")
    private String canal;

    /**
     * Nombre del destinatario.
     */
    @NotBlank(message = "El nombre del destinatario es obligatorio")
    @Size(max = 200, message = "El nombre no puede exceder 200 caracteres")
    private String destinatarioNombre;

    /**
     * Email del destinatario.
     */
    @Email(message = "Formato de email inválido")
    @Size(max = 100, message = "El email no puede exceder 100 caracteres")
    private String destinatarioEmail;

    /**
     * Teléfono del destinatario.
     */
    @Pattern(regexp = "^[+]?[0-9]{7,15}$", message = "Formato de teléfono inválido")
    private String destinatarioTelefono;

    /**
     * Asunto de la notificación.
     */
    @NotBlank(message = "El asunto es obligatorio")
    @Size(max = 200, message = "El asunto no puede exceder 200 caracteres")
    private String asunto;

    /**
     * Mensaje o contenido.
     */
    @NotBlank(message = "El mensaje es obligatorio")
    @Size(min = 10, max = 2000, message = "El mensaje debe tener entre 10 y 2000 caracteres")
    private String mensaje;

    /**
     * Prioridad (BAJA, NORMAL, ALTA, URGENTE).
     */
    private String prioridad;

    /**
     * ID de la entidad relacionada.
     */
    private Long idEntidadRelacionada;

    /**
     * Tipo de entidad relacionada.
     */
    @Size(max = 50, message = "El tipo de entidad no puede exceder 50 caracteres")
    private String tipoEntidadRelacionada;

    /**
     * ID del usuario que genera la notificación.
     */
    private Long idUsuario;

    /**
     * Máximo número de intentos permitidos.
     */
    private Integer maxIntentos;
}
