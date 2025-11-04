package com.veterinaria.clinica_veternica.dto.request.comunicacion;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de Request para enviar un Correo Electrónico.
 *
 * @author Clínica Veterinaria Team
 * @version 1.0
 * @since 2025-11-04
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CorreoRequestDTO {

    /**
     * Tipo de correo (RECORDATORIO, CONFIRMACION, FACTURA, NOTIFICACION, MARKETING, SISTEMA).
     */
    @NotBlank(message = "El tipo es obligatorio")
    @Size(max = 30, message = "El tipo no puede exceder 30 caracteres")
    private String tipoCorreo;

    /**
     * Email del remitente.
     */
    @NotBlank(message = "El remitente es obligatorio")
    @Email(message = "Formato de email inválido")
    @Size(max = 100, message = "El email no puede exceder 100 caracteres")
    private String remitente;

    /**
     * Nombre del remitente.
     */
    @Size(max = 150, message = "El nombre del remitente no puede exceder 150 caracteres")
    private String nombreRemitente;

    /**
     * Email del destinatario.
     */
    @NotBlank(message = "El destinatario es obligatorio")
    @Email(message = "Formato de email inválido")
    @Size(max = 100, message = "El email no puede exceder 100 caracteres")
    private String destinatario;

    /**
     * Nombre del destinatario.
     */
    @Size(max = 150, message = "El nombre del destinatario no puede exceder 150 caracteres")
    private String nombreDestinatario;

    /**
     * Emails en copia (CC) - separados por coma.
     */
    @Size(max = 500, message = "Los destinatarios CC no pueden exceder 500 caracteres")
    private String destinatariosCC;

    /**
     * Emails en copia oculta (BCC) - separados por coma.
     */
    @Size(max = 500, message = "Los destinatarios BCC no pueden exceder 500 caracteres")
    private String destinatariosBCC;

    /**
     * Asunto del correo.
     */
    @NotBlank(message = "El asunto es obligatorio")
    @Size(min = 3, max = 200, message = "El asunto debe tener entre 3 y 200 caracteres")
    private String asunto;

    /**
     * Cuerpo del correo en texto plano.
     */
    @Size(max = 5000, message = "El cuerpo no puede exceder 5000 caracteres")
    private String cuerpoTexto;

    /**
     * Cuerpo del correo en HTML.
     */
    @Size(max = 10000, message = "El cuerpo HTML no puede exceder 10000 caracteres")
    private String cuerpoHTML;

    /**
     * Archivos adjuntos (nombres separados por coma).
     */
    @Size(max = 500, message = "Los adjuntos no pueden exceder 500 caracteres")
    private String adjuntos;

    /**
     * Tamaño total de adjuntos en bytes.
     */
    private Long tamanioAdjuntos;

    /**
     * Prioridad del correo (BAJA, NORMAL, ALTA).
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
     * Proveedor de envío (SMTP, SendGrid, AWS SES, etc.).
     */
    @Size(max = 50, message = "El proveedor no puede exceder 50 caracteres")
    private String proveedor;

    /**
     * Máximo número de intentos.
     */
    private Integer maxIntentos;
}
