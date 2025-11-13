package com.veterinaria.clinica_veternica.patterns.creational.abstractfactory;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.regex.Pattern;

/**
 * Factory concreto para notificaciones por SMS.
 *
 * Crea la familia de objetos relacionados para enviar notificaciones por SMS.
 *
 * @author Clínica Veterinaria Team
 * @version 1.0
 * @since 2025-11-04
 */
@Slf4j
@Component
public class SMSNotificacionFactory implements NotificacionFactory {

    private static final String CANAL = "SMS";
    private static final Pattern PHONE_PATTERN = Pattern.compile("^[+]?\\d{7,15}$");
    private static final int MAX_SMS_LENGTH = 160;

    @Override
    public MensajeNotificacion crearMensaje(String destinatario, String asunto, String contenido) {
        // SMS no usa asunto, se incluye en el contenido si es necesario
        String contenidoFinal = contenido;
        if (contenido.length() > MAX_SMS_LENGTH) {
            contenidoFinal = contenido.substring(0, MAX_SMS_LENGTH - 3) + "...";
            log.warn("Contenido SMS truncado a {} caracteres", MAX_SMS_LENGTH);
        }

        return MensajeNotificacion.builder()
                .id(UUID.randomUUID().toString())
                .canal(CANAL)
                .destinatario(destinatario)
                .asunto(null) // SMS no tiene asunto
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
                    mensajeError = "El número de teléfono no puede estar vacío";
                    return false;
                }
                String normalizado = normalizar(destinatario);
                if (!PHONE_PATTERN.matcher(normalizado).matches()) {
                    mensajeError = "Formato de teléfono inválido. Debe tener entre 7 y 15 dígitos";
                    return false;
                }
                return true;
            }

            @Override
            public String normalizar(String destinatario) {
                if (destinatario == null) return null;
                // Eliminar espacios, guiones, paréntesis
                return destinatario.replaceAll("[\\s\\-()]", "");
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
                    log.info("Enviando SMS a: {} - {} caracteres",
                            mensaje.getDestinatario(), mensaje.getTamano());

                    // Simulación de envío (en producción, integrar con Twilio, AWS SNS, etc)
                    // NOTA: Para producción, integrar con servicio real de SMS (Twilio, AWS SNS, etc.)

                    this.idExterno = "SMS-" + UUID.randomUUID();
                    this.estadoEnvio = "ENVIADO";

                    log.info("SMS enviado exitosamente. ID: {}", idExterno);
                    return true;

                } catch (IllegalArgumentException | IllegalStateException e) {
                    log.error("Error de validación al enviar SMS: {}", e.getMessage(), e);
                    this.estadoEnvio = "ERROR";
                    this.mensajeError = e.getMessage();
                    return false;
                } catch (RuntimeException e) {
                    log.error("Error al enviar SMS: {}", e.getMessage(), e);
                    this.estadoEnvio = "ERROR";
                    this.mensajeError = "Error al procesar el envío de SMS";
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
        // En producción, verificar si el servicio de SMS está configurado
        return true;
    }

    @Override
    public double getCostoEnvio() {
        return 200.0; // Costo medio para SMS
    }
}
