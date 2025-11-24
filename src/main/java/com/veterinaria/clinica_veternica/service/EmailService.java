package com.veterinaria.clinica_veternica.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.AuthenticationFailedException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

/**
 * Servicio para envío de correos electrónicos usando Spring Mail.
 *
 * @author Clínica Veterinaria Team
 * @version 1.0
 * @since 2025-11-21
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${app.email.from-name:Clínica Veterinaria}")
    private String fromName;

    /**
     * Envía un correo electrónico simple (texto plano convertido a HTML con template profesional).
     *
     * @param to      Dirección de correo del destinatario
     * @param subject Asunto del correo
     * @param text    Contenido del correo (se convertirá a HTML)
     * @return true si se envió correctamente, false en caso contrario
     */
    public boolean enviarEmailSimple(String to, String subject, String text) {
        try {
            // Convertir texto plano a HTML usando el template profesional
            String htmlContent = text.replace("\n", "<br/>");
            String htmlFinal = generarTemplateHtml(subject, "<p>" + htmlContent + "</p>", "info");
            
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail, fromName);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlFinal, true); // true para indicar que es HTML

            mailSender.send(message);
            log.info("Correo enviado exitosamente a: {} - Asunto: {}", to, subject);
            return true;
        } catch (MailSendException e) {
            // Verificar si la causa es un error de autenticación
            Throwable cause = e.getCause();
            if (cause instanceof AuthenticationFailedException || 
                (cause != null && cause.getMessage() != null && 
                 (cause.getMessage().contains("authentication") || 
                  cause.getMessage().contains("Authentication failed") ||
                  cause.getMessage().contains("535")))) {
                log.error("Error de autenticación al enviar correo a {}: {}. " +
                        "Verifique que esté usando una 'App Password' de Gmail, no la contraseña normal.", 
                        to, e.getMessage(), e);
            } else {
                log.error("Error al enviar correo a {}: {}. Verifique la configuración SMTP y la conexión a internet.", 
                        to, e.getMessage(), e);
                if (cause != null) {
                    log.error("Causa raíz: {}", cause.getMessage());
                }
            }
            return false;
        } catch (MailException e) {
            log.error("Error de Spring Mail al enviar correo a {}: {} - Tipo: {}", 
                    to, e.getMessage(), e.getClass().getSimpleName(), e);
            return false;
        } catch (Exception e) {
            log.error("Error inesperado al enviar correo a {}: {} - Tipo: {}", 
                    to, e.getMessage(), e.getClass().getSimpleName(), e);
            return false;
        }
    }

    /**
     * Envía un correo electrónico HTML.
     *
     * @param to      Dirección de correo del destinatario
     * @param subject Asunto del correo
     * @param htmlContent Contenido HTML del correo
     * @return true si se envió correctamente, false en caso contrario
     */
    public boolean enviarEmailHtml(String to, String subject, String htmlContent) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail, fromName);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);

            mailSender.send(message);
            log.info("Correo HTML enviado exitosamente a: {} - Asunto: {}", to, subject);
            return true;
        } catch (MessagingException e) {
            log.error("Error al enviar correo HTML a {}: {}", to, e.getMessage(), e);
            return false;
        } catch (Exception e) {
            log.error("Error inesperado al enviar correo HTML a {}: {}", to, e.getMessage(), e);
            return false;
        }
    }

    /**
     * Envía un correo a múltiples destinatarios.
     *
     * @param to      Array de direcciones de correo
     * @param subject Asunto del correo
     * @param text    Contenido del correo
     * @return true si se envió correctamente, false en caso contrario
     */
    public boolean enviarEmailMultiple(String[] to, String subject, String text) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);

            mailSender.send(message);
            log.info("Correo enviado exitosamente a {} destinatarios - Asunto: {}", to.length, subject);
            return true;
        } catch (Exception e) {
            log.error("Error al enviar correo a múltiples destinatarios: {}", e.getMessage(), e);
            return false;
        }
    }

    /**
     * Genera un template HTML profesional y moderno para emails.
     *
     * @param titulo    Título del email
     * @param contenido Contenido principal del email (puede contener HTML)
     * @param tipo      Tipo de email: "info", "success", "warning", "error"
     * @return HTML formateado
     */
    public String generarTemplateHtml(String titulo, String contenido, String tipo) {
        String colorPrimario;
        String colorSecundario;
        String icono;
        
        switch (tipo != null ? tipo.toLowerCase() : "info") {
            case "success" -> {
                colorPrimario = "#10b981";
                colorSecundario = "#059669";
                icono = "✅";
            }
            case "warning" -> {
                colorPrimario = "#f59e0b";
                colorSecundario = "#d97706";
                icono = "⚠️";
            }
            case "error" -> {
                colorPrimario = "#ef4444";
                colorSecundario = "#dc2626";
                icono = "❌";
            }
            default -> {
                colorPrimario = "#3b82f6";
                colorSecundario = "#2563eb";
                icono = "ℹ️";
            }
        }

        return String.format("""
            <!DOCTYPE html>
            <html lang="es">
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <meta http-equiv="X-UA-Compatible" content="IE=edge">
                <title>%s</title>
            </head>
            <body style="margin: 0; padding: 0; font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif; background: linear-gradient(135deg, #667eea 0%%, #764ba2 100%%); background-color: #f3f4f6;">
                <table role="presentation" style="width: 100%%; border-collapse: collapse; background: linear-gradient(135deg, #667eea 0%%, #764ba2 100%%); padding: 40px 20px; min-height: 100vh;">
                    <tr>
                        <td align="center" style="padding: 20px 0;">
                            <table role="presentation" style="max-width: 650px; width: 100%%; border-collapse: collapse; background-color: #ffffff; border-radius: 16px; box-shadow: 0 20px 60px rgba(0,0,0,0.3); overflow: hidden;">
                                <!-- Header con gradiente -->
                                <tr>
                                    <td style="background: linear-gradient(135deg, %s 0%%, %s 100%%); padding: 40px 30px; text-align: center; position: relative;">
                                        <div style="position: absolute; top: 0; left: 0; right: 0; bottom: 0; background: url('data:image/svg+xml,<svg xmlns=\"http://www.w3.org/2000/svg\" viewBox=\"0 0 100 100\"><circle cx=\"50\" cy=\"50\" r=\"2\" fill=\"rgba(255,255,255,0.1)\"/></svg>') repeat; opacity: 0.3;"></div>
                                        <div style="position: relative; z-index: 1;">
                                            <div style="font-size: 48px; margin-bottom: 10px;">🐾</div>
                                            <h1 style="margin: 0; color: #ffffff; font-size: 28px; font-weight: 700; letter-spacing: -0.5px; text-shadow: 0 2px 4px rgba(0,0,0,0.2);">
                                                Clínica Veterinaria
                                            </h1>
                                            <p style="margin: 8px 0 0 0; color: rgba(255,255,255,0.9); font-size: 14px; font-weight: 400;">
                                                Cuidando a tus mejores amigos
                                            </p>
                                        </div>
                                    </td>
                                </tr>
                                
                                <!-- Icono de tipo -->
                                <tr>
                                    <td style="padding: 0; text-align: center; background-color: #ffffff;">
                                        <div style="display: inline-block; width: 80px; height: 80px; background: linear-gradient(135deg, %s 0%%, %s 100%%); border-radius: 50%%; margin: -40px auto 20px; box-shadow: 0 8px 16px rgba(0,0,0,0.15); display: flex; align-items: center; justify-content: center; font-size: 36px;">
                                            %s
                                        </div>
                                    </td>
                                </tr>
                                
                                <!-- Content -->
                                <tr>
                                    <td style="padding: 0 30px 30px 30px;">
                                        <h2 style="margin: 0 0 24px 0; color: #1f2937; font-size: 24px; font-weight: 700; text-align: center; letter-spacing: -0.5px;">
                                            %s
                                        </h2>
                                        <div style="color: #4b5563; font-size: 16px; line-height: 1.8; background-color: #f9fafb; padding: 24px; border-radius: 12px; border-left: 4px solid %s;">
                                            %s
                                        </div>
                                    </td>
                                </tr>
                                
                                <!-- Footer -->
                                <tr>
                                    <td style="background: linear-gradient(to bottom, #f9fafb 0%%, #ffffff 100%%); padding: 30px; text-align: center; border-top: 1px solid #e5e7eb;">
                                        <div style="margin-bottom: 16px;">
                                            <a href="mailto:%s" style="display: inline-block; padding: 12px 24px; background: linear-gradient(135deg, %s 0%%, %s 100%%); color: #ffffff; text-decoration: none; border-radius: 8px; font-weight: 600; font-size: 14px; box-shadow: 0 4px 12px rgba(0,0,0,0.15); transition: transform 0.2s;">
                                                📧 Contáctanos
                                            </a>
                                        </div>
                                        <p style="margin: 16px 0 0 0; color: #6b7280; font-size: 13px; line-height: 1.6;">
                                            Este es un correo automático generado por nuestro sistema.<br>
                                            Por favor, no responda directamente a este mensaje.
                                        </p>
                                        <p style="margin: 12px 0 0 0; color: #9ca3af; font-size: 12px;">
                                            © %d Clínica Veterinaria. Todos los derechos reservados.
                                        </p>
                                    </td>
                                </tr>
                            </table>
                        </td>
                    </tr>
                </table>
            </body>
            </html>
            """, titulo, colorPrimario, colorSecundario, colorPrimario, colorSecundario, icono, titulo, colorPrimario, contenido, fromEmail, colorPrimario, colorSecundario, java.time.Year.now().getValue());
    }

    /**
     * Envía un correo HTML con template profesional.
     *
     * @param to        Dirección de correo del destinatario
     * @param subject   Asunto del correo
     * @param titulo    Título del email
     * @param contenido Contenido principal (puede contener HTML)
     * @param tipo      Tipo de email: "info", "success", "warning", "error"
     * @return true si se envió correctamente, false en caso contrario
     */
    public boolean enviarEmailTemplate(String to, String subject, String titulo, String contenido, String tipo) {
        String htmlContent = generarTemplateHtml(titulo, contenido, tipo);
        return enviarEmailHtml(to, subject, htmlContent);
    }
}

