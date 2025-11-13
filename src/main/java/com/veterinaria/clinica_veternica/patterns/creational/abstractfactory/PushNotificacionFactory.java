package com.veterinaria.clinica_veternica.patterns.creational.abstractfactory;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Factory concreto para notificaciones Push.
 *
 * Crea la familia de objetos relacionados para enviar notificaciones push
 * a dispositivos móviles (iOS/Android).
 *
 * @author Clínica Veterinaria Team
 * @version 1.0
 * @since 2025-11-04
 */
@Slf4j
@Component
public class PushNotificacionFactory implements NotificacionFactory {

    private static final String CANAL = "PUSH";
    private static final int MAX_TITLE_LENGTH = 65;
    private static final int MAX_BODY_LENGTH = 240;

    @Override
    public MensajeNotificacion crearMensaje(String destinatario, String asunto, String contenido) {
        // Push tiene límites de longitud
        String asuntoFinal = asunto;
        if (asunto != null && asunto.length() > MAX_TITLE_LENGTH) {
            asuntoFinal = asunto.substring(0, MAX_TITLE_LENGTH - 3) + "...";
        }

        String contenidoFinal = contenido;
        if (contenido.length() > MAX_BODY_LENGTH) {
            contenidoFinal = contenido.substring(0, MAX_BODY_LENGTH - 3) + "...";
        }

        return MensajeNotificacion.builder()
                .id(UUID.randomUUID().toString())
                .canal(CANAL)
                .destinatario(destinatario) // Token del dispositivo
                .asunto(asuntoFinal)
                .contenido(contenidoFinal)
                .fechaCreacion(LocalDateTime.now())
                .prioridad("NORMAL")
                .build();
    }

    @Override
    public ValidadorDestinatario crearValidador() {
        return new ValidadorDestinatario() {
            private String mensajeError;

            @Override
            public boolean esValido(String destinatario) {
                if (destinatario == null || destinatario.isBlank()) {
                    mensajeError = "El token del dispositivo no puede estar vacío";
                    return false;
                }
                // Validación básica de token (en producción, validar formato específico)
                if (destinatario.length() < 20) {
                    mensajeError = "Token de dispositivo inválido";
                    return false;
                }
                return true;
            }

            @Override
            public String normalizar(String destinatario) {
                return destinatario != null ? destinatario.trim() : null;
            }

            @Override
            public String getMensajeError() {
                return mensajeError;
            }
        };
    }

    @Override
    public EnviadorNotificacion crearEnviador() {
        return new EnviadorNotificacion() {
            private String estadoEnvio;
            private String idExterno;
            private String mensajeError;

            @Override
            public boolean enviar(MensajeNotificacion mensaje) {
                try {
                    log.info("Enviando Push a: {} - Título: {}",
                            mensaje.getDestinatario().substring(0, Math.min(10, mensaje.getDestinatario().length())),
                            mensaje.getAsunto());

                    // Simulación de envío (en producción, integrar con FCM, APNs, etc)
                    // NOTA: Para producción, integrar con Firebase Cloud Messaging / Apple Push Notification Service

                    this.idExterno = "PUSH-" + UUID.randomUUID();
                    this.estadoEnvio = "ENVIADO";

                    log.info("Push enviada exitosamente. ID: {}", idExterno);
                    return true;

                } catch (IllegalArgumentException | IllegalStateException e) {
                    log.error("Error de validación al enviar Push: {}", e.getMessage(), e);
                    this.estadoEnvio = "ERROR";
                    this.mensajeError = e.getMessage();
                    return false;
                } catch (RuntimeException e) {
                    log.error("Error al enviar Push: {}", e.getMessage(), e);
                    this.estadoEnvio = "ERROR";
                    this.mensajeError = "Error al procesar el envío de notificación push";
                    return false;
                }
            }

            @Override
            public String getEstadoEnvio() {
                return estadoEnvio;
            }

            @Override
            public String getIdExterno() {
                return idExterno;
            }

            @Override
            public String getMensajeError() {
                return mensajeError;
            }
        };
    }

    @Override
    public String getNombreCanal() {
        return CANAL;
    }

    @Override
    public boolean estaDisponible() {
        // En producción, verificar si FCM/APNs están configurados
        return true;
    }

    @Override
    public double getCostoEnvio() {
        return 0.0; // Sin costo directo (gratis con límites)
    }
}
