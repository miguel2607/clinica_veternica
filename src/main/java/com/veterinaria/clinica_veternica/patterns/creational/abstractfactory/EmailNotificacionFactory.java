package com.veterinaria.clinica_veternica.patterns.creational.abstractfactory;

import com.veterinaria.clinica_veternica.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.regex.Pattern;

/**
 * Factory concreto para notificaciones por EMAIL.
 *
 * Crea la familia de objetos relacionados para enviar notificaciones por email.
 *
 * @author Clínica Veterinaria Team
 * @version 1.0
 * @since 2025-11-04
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class EmailNotificacionFactory implements NotificacionFactory {

    private final EmailService emailService;

    private static final String CANAL = "EMAIL";
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );

    @Override
    public MensajeNotificacion crearMensaje(String destinatario, String asunto, String contenido) {
        return MensajeNotificacion.builder()
                .id(UUID.randomUUID().toString())
                .canal(CANAL)
                .destinatario(destinatario)
                .asunto(asunto)
                .contenido(contenido)
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
                    mensajeError = "El email no puede estar vacío";
                    return false;
                }
                if (!EMAIL_PATTERN.matcher(destinatario).matches()) {
                    mensajeError = "Formato de email inválido";
                    return false;
                }
                return true;
            }

            @Override
            public String normalizar(String destinatario) {
                return destinatario != null ? destinatario.toLowerCase().trim() : null;
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
                    log.info("Enviando EMAIL a: {} - Asunto: {}",
                            mensaje.getDestinatario(), mensaje.getAsunto());

                    // Determinar si el contenido ya es HTML o es texto plano
                    boolean esHtml = mensaje.getContenido() != null && 
                                    (mensaje.getContenido().contains("<html") || 
                                     mensaje.getContenido().contains("<div") ||
                                     mensaje.getContenido().contains("<p>"));

                    boolean enviado;
                    if (esHtml) {
                        // Si ya es HTML, enviar directamente
                        enviado = emailService.enviarEmailHtml(
                                mensaje.getDestinatario(),
                                mensaje.getAsunto(),
                                mensaje.getContenido()
                        );
                    } else {
                        // Si es texto plano, usar template HTML
                        // Convertir saltos de línea a <br> y párrafos a <p>
                        String contenidoHtml = mensaje.getContenido()
                                .replace("\n\n", "</p><p>")
                                .replace("\n", "<br>");
                        contenidoHtml = "<p>" + contenidoHtml + "</p>";
                        
                        enviado = emailService.enviarEmailTemplate(
                                mensaje.getDestinatario(),
                                mensaje.getAsunto(),
                                mensaje.getAsunto(), // Título igual al asunto
                                contenidoHtml,
                                "info"
                        );
                    }

                    if (enviado) {
                        this.idExterno = "EMAIL-" + UUID.randomUUID();
                        this.estadoEnvio = "ENVIADO";
                        log.info("EMAIL enviado exitosamente. ID: {}", idExterno);
                        return true;
                    } else {
                        this.estadoEnvio = "ERROR";
                        this.mensajeError = "No se pudo enviar el correo";
                        return false;
                    }

                } catch (IllegalArgumentException | IllegalStateException e) {
                    log.error("Error de validación al enviar EMAIL: {}", e.getMessage(), e);
                    this.estadoEnvio = "ERROR";
                    this.mensajeError = e.getMessage();
                    return false;
                } catch (RuntimeException e) {
                    log.error("Error al enviar EMAIL: {}", e.getMessage(), e);
                    this.estadoEnvio = "ERROR";
                    this.mensajeError = "Error al procesar el envío de email: " + e.getMessage();
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
        // En producción, verificar si el servicio de email está configurado
        return true;
    }

    @Override
    public double getCostoEnvio() {
        return 50.0; // Costo bajo para emails
    }
}
