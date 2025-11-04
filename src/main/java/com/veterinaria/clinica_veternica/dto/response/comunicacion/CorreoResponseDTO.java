package com.veterinaria.clinica_veternica.dto.response.comunicacion;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO de Response para un Correo Electrónico.
 *
 * @author Clínica Veterinaria Team
 * @version 1.0
 * @since 2025-11-04
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CorreoResponseDTO {

    private Long idCorreo;
    private String tipoCorreo;
    private String remitente;
    private String nombreRemitente;
    private String destinatario;
    private String nombreDestinatario;
    private String destinatariosCC;
    private String destinatariosBCC;
    private String asunto;
    private String cuerpoTexto;
    private String cuerpoHTML;
    private String adjuntos;
    private Long tamanioAdjuntos;
    private String prioridad;
    private Boolean enviado;
    private LocalDateTime fechaEnvio;
    private Boolean abierto;
    private LocalDateTime fechaApertura;
    private Integer vecesAbierto;
    private Boolean huboClic;
    private LocalDateTime fechaPrimerClic;
    private Integer intentosEnvio;
    private Integer maxIntentos;
    private String mensajeError;
    private String idMensaje;
    private Long idEntidadRelacionada;
    private String tipoEntidadRelacionada;
    private String proveedor;
    private Boolean seAgotaronIntentos;
    private Boolean puedeReintentar;
    private Boolean tieneAdjuntos;
    private Boolean esHTML;
    private Boolean fueExitoso;
    private Double tasaApertura;
    private Long minutosHastaApertura;
    private Boolean esAltaPrioridad;
    private String resumen;
    private LocalDateTime fechaCreacion;
}
