package com.veterinaria.clinica_veternica.dto.response.comunicacion;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO de Response para una Notificación.
 *
 * @author Clínica Veterinaria Team
 * @version 1.0
 * @since 2025-11-04
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificacionResponseDTO {

    private Long idNotificacion;
    private String tipo;
    private String canal;
    private String destinatarioNombre;
    private String destinatarioEmail;
    private String destinatarioTelefono;
    private String asunto;
    private String mensaje;
    private String prioridad;
    private Long idEntidadRelacionada;
    private String tipoEntidadRelacionada;
    private Boolean enviada;
    private LocalDateTime fechaEnvio;
    private Boolean leida;
    private LocalDateTime fechaLectura;
    private Integer intentosEnvio;
    private Integer maxIntentos;
    private String mensajeError;
    private String idExterno;
    private Long idUsuario;
    private Boolean seAgotaronIntentos;
    private Boolean puedeReintentar;
    private Boolean esEmail;
    private Boolean esSMS;
    private Boolean esWhatsApp;
    private Boolean esPush;
    private Boolean esUrgente;
    private Boolean esAltaPrioridad;
    private Long minutosDesdeCreacion;
    private Boolean estaPendiente;
    private String resumen;
    private LocalDateTime fechaCreacion;
}
