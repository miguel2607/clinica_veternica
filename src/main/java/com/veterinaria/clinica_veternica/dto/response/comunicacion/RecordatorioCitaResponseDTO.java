package com.veterinaria.clinica_veternica.dto.response.comunicacion;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO de Response para un Recordatorio de Cita.
 *
 * @author Clínica Veterinaria Team
 * @version 1.0
 * @since 2025-11-04
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecordatorioCitaResponseDTO {

    private Long idRecordatorio;
    private Long idCita;
    private String tipoRecordatorio;
    private String canal;
    private String destinatario;
    private String emailDestinatario;
    private String telefonoDestinatario;
    private String mensaje;
    private LocalDateTime fechaProgramadaEnvio;
    private Boolean enviado;
    private LocalDateTime fechaEnvio;
    private Boolean confirmado;
    private LocalDateTime fechaConfirmacion;
    private String tokenConfirmacion;
    private Integer intentosEnvio;
    private Integer maxIntentos;
    private String mensajeError;
    private String idExterno;
    private Boolean seAgotaronIntentos;
    private Boolean puedeReintentar;
    private Boolean estaListoParaEnviar;
    private Boolean esRecordatorioInicial;
    private Boolean esRecordatorioFinal;
    private Boolean requiereConfirmacion;
    private Long horasHastaEnvio;
    private Boolean estaVencido;
    private LocalDateTime fechaCreacion;
}
