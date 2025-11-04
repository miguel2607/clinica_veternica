package com.veterinaria.clinica_veternica.dto.request.comunicacion;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO de Request para crear un Recordatorio de Cita.
 *
 * @author Clínica Veterinaria Team
 * @version 1.0
 * @since 2025-11-04
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecordatorioCitaRequestDTO {

    /**
     * ID de la cita.
     */
    @NotNull(message = "La cita es obligatoria")
    private Long idCita;

    /**
     * Tipo de recordatorio (INICIAL, FINAL, CONFIRMACION).
     */
    @NotBlank(message = "El tipo de recordatorio es obligatorio")
    @Size(max = 30, message = "El tipo no puede exceder 30 caracteres")
    private String tipoRecordatorio;

    /**
     * Canal de envío (EMAIL, SMS, WHATSAPP).
     */
    @NotBlank(message = "El canal es obligatorio")
    @Size(max = 20, message = "El canal no puede exceder 20 caracteres")
    private String canal;

    /**
     * Nombre del destinatario.
     */
    @NotBlank(message = "El destinatario es obligatorio")
    @Size(max = 200, message = "El destinatario no puede exceder 200 caracteres")
    private String destinatario;

    /**
     * Email del destinatario.
     */
    @Email(message = "Formato de email inválido")
    @Size(max = 100, message = "El email no puede exceder 100 caracteres")
    private String emailDestinatario;

    /**
     * Teléfono del destinatario.
     */
    @Pattern(regexp = "^[+]?[0-9]{7,15}$", message = "Formato de teléfono inválido")
    private String telefonoDestinatario;

    /**
     * Mensaje del recordatorio.
     */
    @NotBlank(message = "El mensaje es obligatorio")
    @Size(min = 20, max = 1000, message = "El mensaje debe tener entre 20 y 1000 caracteres")
    private String mensaje;

    /**
     * Fecha y hora programada para el envío.
     */
    @NotNull(message = "La fecha programada es obligatoria")
    private LocalDateTime fechaProgramadaEnvio;

    /**
     * Máximo número de intentos.
     */
    private Integer maxIntentos;
}
