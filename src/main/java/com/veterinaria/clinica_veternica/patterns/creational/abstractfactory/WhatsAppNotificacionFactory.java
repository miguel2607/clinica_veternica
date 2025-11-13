package com.veterinaria.clinica_veternica.patterns.creational.abstractfactory;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.regex.Pattern;

/**
 * Factory concreto para notificaciones por WhatsApp.
 *
 * Crea la familia de objetos relacionados para enviar notificaciones por WhatsApp.
 *
 * @author Clínica Veterinaria Team
 * @version 1.0
 * @since 2025-11-04
 */
@Slf4j
@Component
public class WhatsAppNotificacionFactory implements NotificacionFactory {

    private static final String CANAL = "WHATSAPP";
    private static final Pattern PHONE_PATTERN = Pattern.compile("^[+]?\\d{7,15}$");
    private static final int MAX_WHATSAPP_LENGTH = 4096;

    @Override
    public MensajeNotificacion crearMensaje(String destinatario, String asunto, String contenido) {
        // WhatsApp puede incluir el asunto en el contenido
        String contenidoFinal = asunto != null ?
                "*" + asunto + "*\n\n" + contenido : contenido;

        if (contenidoFinal.length() > MAX_WHATSAPP_LENGTH) {
            contenidoFinal = contenidoFinal.substring(0, MAX_WHATSAPP_LENGTH - 3) + "...";
            log.warn("Contenido WhatsApp truncado a {} caracteres", MAX_WHATSAPP_LENGTH);
        }

        return MensajeNotificacion.builder()
                .id(UUID.randomUUID().toString())
                .canal(CANAL)
                .destinatario(destinatario)
                .asunto(asunto)
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
                    mensajeError = "El número de WhatsApp no puede estar vacío";
                    return false;
                }
                String normalizado = normalizar(destinatario);
                if (!PHONE_PATTERN.matcher(normalizado).matches()) {
                    mensajeError = "Formato de número inválido. Debe incluir código de país";
                    return false;
                }
                // WhatsApp requiere código de país
                if (!normalizado.startsWith("+")) {
                    mensajeError = "El número debe incluir código de país (ej: +57)";
                    return false;
                }
                return true;
            }

            @Override
            public String normalizar(String destinatario) {
                if (destinatario == null) return null;
                // Eliminar espacios, guiones, paréntesis
                String normalizado = destinatario.replaceAll("[\\s\\-()]", "");
                // Agregar + si no lo tiene
                if (!normalizado.startsWith("+") && normalizado.matches("^\\d{10,15}$")) {
                    normalizado = "+" + normalizado;
                }
                return normalizado;
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
                    log.info("Enviando WhatsApp a: {} - {} caracteres",
                            mensaje.getDestinatario(), mensaje.getTamano());

                    // Simulación de envío (en producción, integrar con WhatsApp Business API)
                    // NOTA: Para producción, integrar con WhatsApp Business API

                    this.idExterno = "WA-" + UUID.randomUUID();
                    this.estadoEnvio = "ENVIADO";

                    log.info("WhatsApp enviado exitosamente. ID: {}", idExterno);
                    return true;

                } catch (IllegalArgumentException | IllegalStateException e) {
                    log.error("Error de validación al enviar WhatsApp: {}", e.getMessage(), e);
                    this.estadoEnvio = "ERROR";
                    this.mensajeError = e.getMessage();
                    return false;
                } catch (RuntimeException e) {
                    log.error("Error al enviar WhatsApp: {}", e.getMessage(), e);
                    this.estadoEnvio = "ERROR";
                    this.mensajeError = "Error al procesar el envío de WhatsApp";
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
        // En producción, verificar si WhatsApp Business API está configurada
        return true;
    }

    @Override
    public double getCostoEnvio() {
        return 150.0; // Costo medio-bajo para WhatsApp
    }
}
