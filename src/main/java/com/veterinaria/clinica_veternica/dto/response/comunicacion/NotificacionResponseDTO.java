package com.veterinaria.clinica_veternica.dto.response.comunicacion;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO de Response para una notificación.
 *
 * @author Clínica Veterinaria Team
 * @version 1.0
 * @since 2025-01-XX
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificacionResponseDTO {

    private Long idComunicacion;
    private Long idUsuario;
    private String nombreUsuario;
    private String emailUsuario;
    private String telefonoUsuario;
    private String tipo;
    private String canal;
    private String motivo;
    private String asunto;
    private String mensaje;
    private Boolean enviada;
    private LocalDateTime fechaEnvio;
    private String idExterno;
    private String estadoEnvio;
    private String mensajeError;
    private LocalDateTime fechaCreacion;
}

