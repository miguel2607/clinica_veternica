package com.veterinaria.clinica_veternica.patterns.behavioral.observer;

import com.veterinaria.clinica_veternica.domain.agenda.Cita;
import com.veterinaria.clinica_veternica.domain.agenda.EstadoCita;
import com.veterinaria.clinica_veternica.domain.comunicacion.Comunicacion;
import com.veterinaria.clinica_veternica.patterns.creational.abstractfactory.EmailNotificacionFactory;
import com.veterinaria.clinica_veternica.repository.ComunicacionRepository;
import com.veterinaria.clinica_veternica.service.EmailService;
import com.veterinaria.clinica_veternica.util.Constants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;

/**
 * Observador concreto que envía notificaciones cuando cambia el estado de una cita.
 *
 * @author Clínica Veterinaria Team
 * @version 1.0
 * @since 2025-11-04
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class NotificacionObserver implements CitaObserver {

    private final EmailNotificacionFactory emailFactory;
    private final EmailService emailService;
    private final ComunicacionRepository comunicacionRepository;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    @Override
    public void onCitaStateChanged(Cita cita, String estadoAnterior, String estadoNuevo) {
        log.info("Enviando notificación por cambio de estado de cita {}: {} -> {}", 
                 cita.getIdCita(), estadoAnterior, estadoNuevo);

        // Aquí se implementaría la lógica de envío de notificaciones
        // usando el Abstract Factory de notificaciones
        if (EstadoCita.CONFIRMADA.name().equals(estadoNuevo)) {
            enviarNotificacionConfirmacion(cita);
        } else if (EstadoCita.ATENDIDA.name().equals(estadoNuevo)) {
            enviarNotificacionAtencion(cita);
        }
    }

    @Override
    public void onCitaCreated(Cita cita) {
        log.info("Enviando notificación de creación de cita: {}", cita.getIdCita());
        enviarNotificacionCreacion(cita);
    }

    @Override
    public void onCitaCancelled(Cita cita, String motivo) {
        log.info("NotificacionObserver: Recibida notificación de cancelación de cita {}: {}", cita.getIdCita(), motivo);
        try {
            enviarNotificacionCancelacion(cita, motivo);
            log.info("NotificacionObserver: Notificaciones de cancelación enviadas exitosamente para cita {}", cita.getIdCita());
        } catch (Exception e) {
            log.error("NotificacionObserver: Error al enviar notificaciones de cancelación para cita {}: {}", 
                    cita.getIdCita(), e.getMessage(), e);
        }
    }

    private void enviarNotificacionConfirmacion(Cita cita) {
        try {
            String emailPropietario = cita.getMascota().getPropietario().getEmail();
            
            if (emailPropietario == null || emailPropietario.isBlank()) {
                log.warn("No se puede enviar notificación de confirmación de cita {}: el propietario no tiene email", 
                        cita.getIdCita());
                return;
            }
            
            String asunto = "✅ Cita Confirmada - " + cita.getMascota().getNombre();
            String contenido = String.format("""
                <p style="margin: 0 0 20px 0; font-size: 18px; color: #1f2937;">
                    Estimado/a <strong style="color: #3b82f6;">%s</strong>,
                </p>
                
                <p style="margin: 0 0 24px 0;">
                    Nos complace confirmarle que su cita para <strong style="color: #10b981;">%s</strong> ha sido <strong>confirmada</strong> exitosamente.
                </p>
                
                <table style="width: 100%%; border-collapse: collapse; background-color: #ffffff; border-radius: 8px; overflow: hidden; box-shadow: 0 2px 8px rgba(0,0,0,0.1); margin: 24px 0;">
                    <tr style="background: linear-gradient(135deg, #10b981 0%%, #059669 100%%);">
                        <td colspan="2" style="padding: 16px 20px; color: #ffffff; font-weight: 700; font-size: 16px;">
                            ✅ Cita Confirmada
                        </td>
                    </tr>
                    <tr>
                        <td style="padding: 14px 20px; color: #6b7280; font-weight: 600; width: 140px; border-bottom: 1px solid #e5e7eb;">🐾 Mascota:</td>
                        <td style="padding: 14px 20px; color: #1f2937; font-weight: 500; border-bottom: 1px solid #e5e7eb;">%s</td>
                    </tr>
                    <tr>
                        <td style="padding: 14px 20px; color: #6b7280; font-weight: 600; border-bottom: 1px solid #e5e7eb;">👨‍⚕️ Veterinario:</td>
                        <td style="padding: 14px 20px; color: #1f2937; font-weight: 500; border-bottom: 1px solid #e5e7eb;">%s</td>
                    </tr>
                    <tr>
                        <td style="padding: 14px 20px; color: #6b7280; font-weight: 600; border-bottom: 1px solid #e5e7eb;">📅 Fecha:</td>
                        <td style="padding: 14px 20px; color: #1f2937; font-weight: 500; border-bottom: 1px solid #e5e7eb;">%s</td>
                    </tr>
                    <tr>
                        <td style="padding: 14px 20px; color: #6b7280; font-weight: 600;">🕐 Hora:</td>
                        <td style="padding: 14px 20px; color: #1f2937; font-weight: 500;">%s</td>
                    </tr>
                </table>
                
                <div style="background: linear-gradient(135deg, #d1fae5 0%%, #a7f3d0 100%%); padding: 16px; border-radius: 8px; border-left: 4px solid #10b981; margin: 24px 0;">
                    <p style="margin: 0; color: #065f46; font-weight: 600; font-size: 14px;">
                        ✅ Su cita está confirmada. ¡Esperamos verlos pronto!
                    </p>
                </div>
                """,
                cita.getMascota().getPropietario().getNombreCompleto(),
                cita.getMascota().getNombre(),
                cita.getMascota().getNombre(),
                cita.getVeterinario().getNombreCompleto(),
                cita.getFechaCita().format(DATE_FORMATTER),
                cita.getHoraCita().format(TIME_FORMATTER)
            );
            
            String htmlFinal = emailService.generarTemplateHtml(asunto, contenido, "success");
            var mensajeNotificacion = emailFactory.crearMensaje(emailPropietario, asunto, htmlFinal);
            var enviador = emailFactory.crearEnviador();
            boolean enviado = enviador.enviar(mensajeNotificacion);
            
            // Guardar notificación en la base de datos
            guardarNotificacionEnBD(
                cita.getMascota().getPropietario().getNombreCompleto(),
                emailPropietario,
                cita.getMascota().getPropietario().getTelefono(),
                asunto,
                contenido,
                cita,
                enviado,
                enviador.getIdExterno()
            );
            
            log.info("Notificación de confirmación enviada: {}", emailPropietario);
        } catch (Exception e) {
            log.error("Error al enviar notificación de confirmación: {}", e.getMessage(), e);
        }
    }

    private void enviarNotificacionAtencion(Cita cita) {
        try {
            String emailPropietario = cita.getMascota().getPropietario().getEmail();
            
            if (emailPropietario == null || emailPropietario.isBlank()) {
                log.warn("No se puede enviar notificación de atención de cita {}: el propietario no tiene email", 
                        cita.getIdCita());
                return;
            }
            
            String asunto = "✅ Cita Atendida - " + cita.getMascota().getNombre();
            String contenido = String.format("""
                <p style="margin: 0 0 20px 0; font-size: 18px; color: #1f2937;">
                    Estimado/a <strong style="color: #3b82f6;">%s</strong>,
                </p>
                
                <p style="margin: 0 0 24px 0;">
                    Le informamos que la cita de su mascota <strong style="color: #10b981;">%s</strong> ha sido <strong>atendida</strong> exitosamente.
                </p>
                
                <table style="width: 100%%; border-collapse: collapse; background-color: #ffffff; border-radius: 8px; overflow: hidden; box-shadow: 0 2px 8px rgba(0,0,0,0.1); margin: 24px 0;">
                    <tr style="background: linear-gradient(135deg, #3b82f6 0%%, #2563eb 100%%);">
                        <td colspan="2" style="padding: 16px 20px; color: #ffffff; font-weight: 700; font-size: 16px;">
                            ✅ Cita Completada
                        </td>
                    </tr>
                    <tr>
                        <td style="padding: 14px 20px; color: #6b7280; font-weight: 600; width: 140px; border-bottom: 1px solid #e5e7eb;">🐾 Mascota:</td>
                        <td style="padding: 14px 20px; color: #1f2937; font-weight: 500; border-bottom: 1px solid #e5e7eb;">%s</td>
                    </tr>
                    <tr>
                        <td style="padding: 14px 20px; color: #6b7280; font-weight: 600; border-bottom: 1px solid #e5e7eb;">👨‍⚕️ Veterinario:</td>
                        <td style="padding: 14px 20px; color: #1f2937; font-weight: 500; border-bottom: 1px solid #e5e7eb;">%s</td>
                    </tr>
                    <tr>
                        <td style="padding: 14px 20px; color: #6b7280; font-weight: 600;">📅 Fecha de Atención:</td>
                        <td style="padding: 14px 20px; color: #1f2937; font-weight: 500;">%s</td>
                    </tr>
                </table>
                
                <div style="background: linear-gradient(135deg, #dbeafe 0%%, #bfdbfe 100%%); padding: 16px; border-radius: 8px; border-left: 4px solid #3b82f6; margin: 24px 0;">
                    <p style="margin: 0; color: #1e40af; font-weight: 600; font-size: 14px;">
                        📋 Para más detalles sobre la atención recibida, puede consultar la historia clínica de su mascota en nuestro sistema.
                    </p>
                </div>
                
                <p style="margin: 24px 0 0 0; color: #6b7280; font-size: 14px;">
                    Gracias por confiar en nosotros para el cuidado de su mascota. 🐕🐈
                </p>
                """,
                cita.getMascota().getPropietario().getNombreCompleto(),
                cita.getMascota().getNombre(),
                cita.getMascota().getNombre(),
                cita.getVeterinario().getNombreCompleto(),
                cita.getFechaCita().format(DATE_FORMATTER)
            );
            
            String htmlFinal = emailService.generarTemplateHtml(asunto, contenido, "info");
            var mensajeNotificacion = emailFactory.crearMensaje(emailPropietario, asunto, htmlFinal);
            var enviador = emailFactory.crearEnviador();
            boolean enviado = enviador.enviar(mensajeNotificacion);
            
            // Guardar notificación en la base de datos
            guardarNotificacionEnBD(
                cita.getMascota().getPropietario().getNombreCompleto(),
                emailPropietario,
                cita.getMascota().getPropietario().getTelefono(),
                asunto,
                contenido,
                cita,
                enviado,
                enviador.getIdExterno()
            );
            
            log.info("Notificación de atención enviada: {}", emailPropietario);
        } catch (Exception e) {
            log.error("Error al enviar notificación de atención: {}", e.getMessage(), e);
        }
    }

    private void enviarNotificacionCreacion(Cita cita) {
        try {
            // Notificación al propietario
            String emailPropietario = cita.getMascota().getPropietario().getEmail();
            if (emailPropietario != null && !emailPropietario.isBlank()) {
                String asunto = "✅ Cita Programada - " + cita.getMascota().getNombre();
                String contenido = String.format("""
                    <p style="margin: 0 0 20px 0; font-size: 18px; color: #1f2937;">
                        Estimado/a <strong style="color: #3b82f6;">%s</strong>,
                    </p>
                    
                    <p style="margin: 0 0 24px 0;">
                        Nos complace informarle que se ha programado exitosamente una cita para su mascota 
                        <strong style="color: #10b981;">%s</strong>.
                    </p>
                    
                    <table style="width: 100%%; border-collapse: collapse; background-color: #ffffff; border-radius: 8px; overflow: hidden; box-shadow: 0 2px 8px rgba(0,0,0,0.1); margin: 24px 0;">
                        <tr style="background: linear-gradient(135deg, #3b82f6 0%%, #2563eb 100%%);">
                            <td colspan="2" style="padding: 16px 20px; color: #ffffff; font-weight: 700; font-size: 16px;">
                                📋 Detalles de la Cita
                            </td>
                        </tr>
                        <tr>
                            <td style="padding: 14px 20px; color: #6b7280; font-weight: 600; width: 140px; border-bottom: 1px solid #e5e7eb;">🐾 Mascota:</td>
                            <td style="padding: 14px 20px; color: #1f2937; font-weight: 500; border-bottom: 1px solid #e5e7eb;">%s</td>
                        </tr>
                        <tr>
                            <td style="padding: 14px 20px; color: #6b7280; font-weight: 600; border-bottom: 1px solid #e5e7eb;">👨‍⚕️ Veterinario:</td>
                            <td style="padding: 14px 20px; color: #1f2937; font-weight: 500; border-bottom: 1px solid #e5e7eb;">%s</td>
                        </tr>
                        <tr>
                            <td style="padding: 14px 20px; color: #6b7280; font-weight: 600; border-bottom: 1px solid #e5e7eb;">🏥 Servicio:</td>
                            <td style="padding: 14px 20px; color: #1f2937; font-weight: 500; border-bottom: 1px solid #e5e7eb;">%s</td>
                        </tr>
                        <tr>
                            <td style="padding: 14px 20px; color: #6b7280; font-weight: 600; border-bottom: 1px solid #e5e7eb;">📅 Fecha:</td>
                            <td style="padding: 14px 20px; color: #1f2937; font-weight: 500; border-bottom: 1px solid #e5e7eb;">%s</td>
                        </tr>
                        <tr>
                            <td style="padding: 14px 20px; color: #6b7280; font-weight: 600; border-bottom: 1px solid #e5e7eb;">🕐 Hora:</td>
                            <td style="padding: 14px 20px; color: #1f2937; font-weight: 500; border-bottom: 1px solid #e5e7eb;">%s</td>
                        </tr>
                        <tr>
                            <td style="padding: 14px 20px; color: #6b7280; font-weight: 600;">📝 Motivo:</td>
                            <td style="padding: 14px 20px; color: #1f2937; font-weight: 500;">%s</td>
                        </tr>
                    </table>
                    
                    <div style="background: linear-gradient(135deg, #fef3c7 0%%, #fde68a 100%%); padding: 16px; border-radius: 8px; border-left: 4px solid #f59e0b; margin: 24px 0;">
                        <p style="margin: 0; color: #92400e; font-weight: 600; font-size: 14px;">
                            ⏰ Por favor, asegúrese de llegar 10 minutos antes de su cita.
                        </p>
                    </div>
                    
                    <p style="margin: 24px 0 0 0; color: #6b7280; font-size: 14px;">
                        Si necesita cancelar o reprogramar su cita, por favor contáctenos con al menos 24 horas de anticipación.
                    </p>
                    
                    <p style="margin: 20px 0 0 0; color: #1f2937; font-weight: 600;">
                        ¡Esperamos verlos pronto! 🐕🐈
                    </p>
                    """,
                    cita.getMascota().getPropietario().getNombreCompleto(),
                    cita.getMascota().getNombre(),
                    cita.getMascota().getNombre(),
                    cita.getVeterinario().getNombreCompleto(),
                    cita.getServicio().getNombre(),
                    cita.getFechaCita().format(DATE_FORMATTER),
                    cita.getHoraCita().format(TIME_FORMATTER),
                    cita.getMotivoConsulta() != null ? cita.getMotivoConsulta() : "No especificado"
                );
                
                String htmlFinal = emailService.generarTemplateHtml(asunto, contenido, "success");
                var mensajeNotificacion = emailFactory.crearMensaje(emailPropietario, asunto, htmlFinal);
                var enviador = emailFactory.crearEnviador();
                boolean enviado = enviador.enviar(mensajeNotificacion);
                
                // Guardar notificación en la base de datos
                guardarNotificacionEnBD(
                    cita.getMascota().getPropietario().getNombreCompleto(),
                    emailPropietario,
                    cita.getMascota().getPropietario().getTelefono(),
                    asunto,
                    contenido,
                    cita,
                    enviado,
                    enviado ? enviador.getIdExterno() : null
                );
                
                if (enviado) {
                    log.info("Notificación de creación de cita {} enviada al propietario: {}", 
                            cita.getIdCita(), emailPropietario);
                } else {
                    log.warn("Error al enviar notificación de creación de cita {} al propietario: {}", 
                            cita.getIdCita(), emailPropietario);
                }
            } else {
                log.warn("No se puede enviar notificación de cita {}: el propietario no tiene email", 
                        cita.getIdCita());
            }
            
            // Notificación al veterinario
            String emailVeterinario = cita.getVeterinario().getCorreo();
            if (emailVeterinario != null && !emailVeterinario.isBlank()) {
                String asunto = "📅 Nueva Cita Programada - " + cita.getMascota().getNombre();
                String contenido = String.format("""
                    <p style="margin: 0 0 20px 0; font-size: 18px; color: #1f2937;">
                        Estimado/a <strong style="color: #3b82f6;">Dr./Dra. %s</strong>,
                    </p>
                    
                    <p style="margin: 0 0 24px 0;">
                        Se ha programado una <strong>nueva cita</strong> en su agenda para el día 
                        <strong style="color: #10b981;">%s a las %s</strong>.
                    </p>
                    
                    <table style="width: 100%%; border-collapse: collapse; background-color: #ffffff; border-radius: 8px; overflow: hidden; box-shadow: 0 2px 8px rgba(0,0,0,0.1); margin: 24px 0;">
                        <tr style="background: linear-gradient(135deg, #3b82f6 0%%, #2563eb 100%%);">
                            <td colspan="2" style="padding: 16px 20px; color: #ffffff; font-weight: 700; font-size: 16px;">
                                📋 Detalles de la Nueva Cita
                            </td>
                        </tr>
                        <tr>
                            <td style="padding: 14px 20px; color: #6b7280; font-weight: 600; width: 140px; border-bottom: 1px solid #e5e7eb;">🐾 Mascota:</td>
                            <td style="padding: 14px 20px; color: #1f2937; font-weight: 500; border-bottom: 1px solid #e5e7eb;">%s</td>
                        </tr>
                        <tr>
                            <td style="padding: 14px 20px; color: #6b7280; font-weight: 600; border-bottom: 1px solid #e5e7eb;">👤 Propietario:</td>
                            <td style="padding: 14px 20px; color: #1f2937; font-weight: 500; border-bottom: 1px solid #e5e7eb;">%s</td>
                        </tr>
                        <tr>
                            <td style="padding: 14px 20px; color: #6b7280; font-weight: 600; border-bottom: 1px solid #e5e7eb;">📞 Teléfono:</td>
                            <td style="padding: 14px 20px; color: #1f2937; font-weight: 500; border-bottom: 1px solid #e5e7eb;">%s</td>
                        </tr>
                        <tr>
                            <td style="padding: 14px 20px; color: #6b7280; font-weight: 600; border-bottom: 1px solid #e5e7eb;">🏥 Servicio:</td>
                            <td style="padding: 14px 20px; color: #1f2937; font-weight: 500; border-bottom: 1px solid #e5e7eb;">%s</td>
                        </tr>
                        <tr>
                            <td style="padding: 14px 20px; color: #6b7280; font-weight: 600; border-bottom: 1px solid #e5e7eb;">📅 Fecha:</td>
                            <td style="padding: 14px 20px; color: #1f2937; font-weight: 500; border-bottom: 1px solid #e5e7eb;">%s</td>
                        </tr>
                        <tr>
                            <td style="padding: 14px 20px; color: #6b7280; font-weight: 600; border-bottom: 1px solid #e5e7eb;">🕐 Hora:</td>
                            <td style="padding: 14px 20px; color: #1f2937; font-weight: 500; border-bottom: 1px solid #e5e7eb;">%s</td>
                        </tr>
                        <tr>
                            <td style="padding: 14px 20px; color: #6b7280; font-weight: 600;">📝 Motivo de Consulta:</td>
                            <td style="padding: 14px 20px; color: #1f2937; font-weight: 500;">%s</td>
                        </tr>
                        %s
                    </table>
                    
                    <div style="background: linear-gradient(135deg, #dbeafe 0%%, #bfdbfe 100%%); padding: 16px; border-radius: 8px; border-left: 4px solid #3b82f6; margin: 24px 0;">
                        <p style="margin: 0; color: #1e40af; font-weight: 600; font-size: 14px;">
                            ⏰ Por favor, revise su agenda y confirme su disponibilidad. La cita está programada y esperando su confirmación.
                        </p>
                    </div>
                    
                    <p style="margin: 24px 0 0 0; color: #6b7280; font-size: 14px;">
                        Puede ver todos los detalles de esta cita y gestionar su agenda desde el sistema.
                    </p>
                    """,
                    cita.getVeterinario().getNombreCompleto(),
                    cita.getFechaCita().format(DATE_FORMATTER),
                    cita.getHoraCita().format(TIME_FORMATTER),
                    cita.getMascota().getNombre(),
                    cita.getMascota().getPropietario().getNombreCompleto(),
                    cita.getMascota().getPropietario().getTelefono() != null ? cita.getMascota().getPropietario().getTelefono() : "No disponible",
                    cita.getServicio().getNombre(),
                    cita.getFechaCita().format(DATE_FORMATTER),
                    cita.getHoraCita().format(TIME_FORMATTER),
                    cita.getMotivoConsulta() != null ? cita.getMotivoConsulta() : "No especificado",
                    cita.getEsEmergencia() != null && cita.getEsEmergencia() ? 
                        "<tr><td style=\"padding: 14px 20px; color: #dc2626; font-weight: 700; background-color: #fef2f2;\">🚨 Tipo:</td><td style=\"padding: 14px 20px; color: #dc2626; font-weight: 700; background-color: #fef2f2;\">EMERGENCIA</td></tr>" : ""
                );
                
                String htmlFinal = emailService.generarTemplateHtml(asunto, contenido, "info");
                var mensajeNotificacion = emailFactory.crearMensaje(emailVeterinario, asunto, htmlFinal);
                var enviador = emailFactory.crearEnviador();
                boolean enviado = enviador.enviar(mensajeNotificacion);
                
                // Guardar notificación en la base de datos
                guardarNotificacionEnBD(
                    cita.getVeterinario().getNombreCompleto(),
                    emailVeterinario,
                    null,
                    asunto,
                    contenido,
                    cita,
                    enviado,
                    enviado ? enviador.getIdExterno() : null
                );
                
                if (enviado) {
                    log.info("Notificación de creación de cita {} enviada al veterinario: {}", 
                            cita.getIdCita(), emailVeterinario);
                } else {
                    log.warn("Error al enviar notificación de creación de cita {} al veterinario: {}", 
                            cita.getIdCita(), emailVeterinario);
                }
            } else {
                log.warn("No se puede enviar notificación de cita {}: el veterinario no tiene email", 
                        cita.getIdCita());
            }
        } catch (Exception e) {
            log.error("Error al enviar notificación de creación de cita {}: {}", 
                    cita.getIdCita(), e.getMessage(), e);
        }
    }

    private void enviarNotificacionCancelacion(Cita cita, String motivo) {
        try {
            // Notificación al propietario
            String emailPropietario = cita.getMascota().getPropietario().getEmail();
            if (emailPropietario != null && !emailPropietario.isBlank()) {
                String asunto = "❌ Cita Cancelada - " + cita.getMascota().getNombre();
                String contenido = String.format("""
                    <p style="margin: 0 0 20px 0; font-size: 18px; color: #1f2937;">
                        Estimado/a <strong style="color: #3b82f6;">%s</strong>,
                    </p>
                    
                    <p style="margin: 0 0 24px 0;">
                        Lamentamos informarle que la cita programada para su mascota 
                        <strong style="color: #ef4444;">%s</strong> ha sido <strong>cancelada</strong>.
                    </p>
                    
                    <table style="width: 100%%; border-collapse: collapse; background-color: #ffffff; border-radius: 8px; overflow: hidden; box-shadow: 0 2px 8px rgba(0,0,0,0.1); margin: 24px 0;">
                        <tr style="background: linear-gradient(135deg, #ef4444 0%%, #dc2626 100%%);">
                            <td colspan="2" style="padding: 16px 20px; color: #ffffff; font-weight: 700; font-size: 16px;">
                                📋 Detalles de la Cita Cancelada
                            </td>
                        </tr>
                        <tr>
                            <td style="padding: 14px 20px; color: #6b7280; font-weight: 600; width: 140px; border-bottom: 1px solid #e5e7eb;">🐾 Mascota:</td>
                            <td style="padding: 14px 20px; color: #1f2937; font-weight: 500; border-bottom: 1px solid #e5e7eb;">%s</td>
                        </tr>
                        <tr>
                            <td style="padding: 14px 20px; color: #6b7280; font-weight: 600; border-bottom: 1px solid #e5e7eb;">👨‍⚕️ Veterinario:</td>
                            <td style="padding: 14px 20px; color: #1f2937; font-weight: 500; border-bottom: 1px solid #e5e7eb;">%s</td>
                        </tr>
                        <tr>
                            <td style="padding: 14px 20px; color: #6b7280; font-weight: 600; border-bottom: 1px solid #e5e7eb;">📅 Fecha:</td>
                            <td style="padding: 14px 20px; color: #1f2937; font-weight: 500; border-bottom: 1px solid #e5e7eb;">%s</td>
                        </tr>
                        <tr>
                            <td style="padding: 14px 20px; color: #6b7280; font-weight: 600; border-bottom: 1px solid #e5e7eb;">🕐 Hora:</td>
                            <td style="padding: 14px 20px; color: #1f2937; font-weight: 500; border-bottom: 1px solid #e5e7eb;">%s</td>
                        </tr>
                        <tr>
                            <td style="padding: 14px 20px; color: #6b7280; font-weight: 600;">📝 Motivo de Cancelación:</td>
                            <td style="padding: 14px 20px; color: #dc2626; font-weight: 600;">%s</td>
                        </tr>
                    </table>
                    
                    <div style="background: linear-gradient(135deg, #fef2f2 0%%, #fee2e2 100%%); padding: 16px; border-radius: 8px; border-left: 4px solid #ef4444; margin: 24px 0;">
                        <p style="margin: 0; color: #991b1b; font-weight: 600; font-size: 14px;">
                            💡 Si desea reprogramar su cita, por favor contáctenos lo antes posible.
                        </p>
                    </div>
                    
                    <p style="margin: 24px 0 0 0; color: #6b7280; font-size: 14px;">
                        Lamentamos cualquier inconveniente que esto pueda causar. Estamos a su disposición para ayudarle.
                    </p>
                    """,
                    cita.getMascota().getPropietario().getNombreCompleto(),
                    cita.getMascota().getNombre(),
                    cita.getMascota().getNombre(),
                    cita.getVeterinario().getNombreCompleto(),
                    cita.getFechaCita().format(DATE_FORMATTER),
                    cita.getHoraCita().format(TIME_FORMATTER),
                    motivo != null && !motivo.isBlank() ? motivo : "No especificado"
                );
                
                String htmlFinal = emailService.generarTemplateHtml(asunto, contenido, "error");
                var mensajeNotificacion = emailFactory.crearMensaje(emailPropietario, asunto, htmlFinal);
                var enviador = emailFactory.crearEnviador();
                boolean enviado = enviador.enviar(mensajeNotificacion);
                
                // Guardar notificación en la base de datos
                guardarNotificacionEnBD(
                    cita.getMascota().getPropietario().getNombreCompleto(),
                    emailPropietario,
                    cita.getMascota().getPropietario().getTelefono(),
                    asunto,
                    contenido,
                    cita,
                    enviado,
                    enviado ? enviador.getIdExterno() : null
                );
                
                log.info("Notificación de cancelación enviada al propietario: {}", emailPropietario);
            }
            
            // Notificación al veterinario
            String emailVeterinario = cita.getVeterinario().getCorreo();
            if (emailVeterinario != null && !emailVeterinario.isBlank()) {
                String asunto = "📅 Cita Cancelada - " + cita.getMascota().getNombre();
                String contenido = String.format("""
                    <p style="margin: 0 0 20px 0; font-size: 18px; color: #1f2937;">
                        Estimado/a <strong style="color: #3b82f6;">Dr./Dra. %s</strong>,
                    </p>
                    
                    <p style="margin: 0 0 24px 0;">
                        Le informamos que la cita programada para el día <strong>%s a las %s</strong> ha sido <strong>cancelada</strong>.
                    </p>
                    
                    <table style="width: 100%%; border-collapse: collapse; background-color: #ffffff; border-radius: 8px; overflow: hidden; box-shadow: 0 2px 8px rgba(0,0,0,0.1); margin: 24px 0;">
                        <tr style="background: linear-gradient(135deg, #f59e0b 0%%, #d97706 100%%);">
                            <td colspan="2" style="padding: 16px 20px; color: #ffffff; font-weight: 700; font-size: 16px;">
                                📋 Detalles de la Cita Cancelada
                            </td>
                        </tr>
                        <tr>
                            <td style="padding: 14px 20px; color: #6b7280; font-weight: 600; width: 140px; border-bottom: 1px solid #e5e7eb;">🐾 Mascota:</td>
                            <td style="padding: 14px 20px; color: #1f2937; font-weight: 500; border-bottom: 1px solid #e5e7eb;">%s</td>
                        </tr>
                        <tr>
                            <td style="padding: 14px 20px; color: #6b7280; font-weight: 600; border-bottom: 1px solid #e5e7eb;">👤 Propietario:</td>
                            <td style="padding: 14px 20px; color: #1f2937; font-weight: 500; border-bottom: 1px solid #e5e7eb;">%s</td>
                        </tr>
                        <tr>
                            <td style="padding: 14px 20px; color: #6b7280; font-weight: 600; border-bottom: 1px solid #e5e7eb;">🏥 Servicio:</td>
                            <td style="padding: 14px 20px; color: #1f2937; font-weight: 500; border-bottom: 1px solid #e5e7eb;">%s</td>
                        </tr>
                        <tr>
                            <td style="padding: 14px 20px; color: #6b7280; font-weight: 600; border-bottom: 1px solid #e5e7eb;">📅 Fecha:</td>
                            <td style="padding: 14px 20px; color: #1f2937; font-weight: 500; border-bottom: 1px solid #e5e7eb;">%s</td>
                        </tr>
                        <tr>
                            <td style="padding: 14px 20px; color: #6b7280; font-weight: 600; border-bottom: 1px solid #e5e7eb;">🕐 Hora:</td>
                            <td style="padding: 14px 20px; color: #1f2937; font-weight: 500; border-bottom: 1px solid #e5e7eb;">%s</td>
                        </tr>
                        <tr>
                            <td style="padding: 14px 20px; color: #6b7280; font-weight: 600;">📝 Motivo de Cancelación:</td>
                            <td style="padding: 14px 20px; color: #d97706; font-weight: 600;">%s</td>
                        </tr>
                    </table>
                    
                    <div style="background: linear-gradient(135deg, #fef3c7 0%%, #fde68a 100%%); padding: 16px; border-radius: 8px; border-left: 4px solid #f59e0b; margin: 24px 0;">
                        <p style="margin: 0; color: #92400e; font-weight: 600; font-size: 14px;">
                            ℹ️ Esta cancelación ha sido registrada en su agenda. Puede verificar su horario disponible en el sistema.
                        </p>
                    </div>
                    """,
                    cita.getVeterinario().getNombreCompleto(),
                    cita.getFechaCita().format(DATE_FORMATTER),
                    cita.getHoraCita().format(TIME_FORMATTER),
                    cita.getMascota().getNombre(),
                    cita.getMascota().getPropietario().getNombreCompleto(),
                    cita.getServicio().getNombre(),
                    cita.getFechaCita().format(DATE_FORMATTER),
                    cita.getHoraCita().format(TIME_FORMATTER),
                    motivo != null && !motivo.isBlank() ? motivo : "No especificado"
                );
                
                String htmlFinal = emailService.generarTemplateHtml(asunto, contenido, "warning");
                var mensajeNotificacion = emailFactory.crearMensaje(emailVeterinario, asunto, htmlFinal);
                var enviador = emailFactory.crearEnviador();
                boolean enviado = enviador.enviar(mensajeNotificacion);
                
                // Guardar notificación en la base de datos
                guardarNotificacionEnBD(
                    cita.getVeterinario().getNombreCompleto(),
                    emailVeterinario,
                    null,
                    asunto,
                    contenido,
                    cita,
                    enviado,
                    enviado ? enviador.getIdExterno() : null
                );
                
                log.info("Notificación de cancelación enviada al veterinario: {}", emailVeterinario);
            }
        } catch (Exception e) {
            log.error("Error al enviar notificación de cancelación de cita {}: {}", 
                    cita.getIdCita(), e.getMessage(), e);
        }
    }
    
    /**
     * Guarda una notificación en la base de datos.
     * 
     * @param nombreDestinatario Nombre del destinatario
     * @param emailDestinatario Email del destinatario
     * @param telefonoDestinatario Teléfono del destinatario (opcional)
     * @param asunto Asunto de la notificación
     * @param mensaje Mensaje de la notificación
     * @param cita Cita relacionada (opcional)
     * @param enviado Si fue enviada exitosamente
     * @param idExterno ID externo del proveedor (opcional)
     */
    @Transactional
    private void guardarNotificacionEnBD(String nombreDestinatario, String emailDestinatario, 
                                         String telefonoDestinatario, String asunto, String mensaje,
                                         Cita cita, boolean enviado, String idExterno) {
        try {
            // Limitar el mensaje a 2000 caracteres si es muy largo
            String mensajeLimitado = mensaje != null && mensaje.length() > 2000 
                ? mensaje.substring(0, 1997) + "..." 
                : mensaje;
            
            Comunicacion comunicacion = Comunicacion.builder()
                .tipo(Constants.ENTIDAD_NOTIFICACION)
                .canal("EMAIL")
                .destinatarioNombre(nombreDestinatario != null ? nombreDestinatario : "Usuario")
                .destinatarioEmail(emailDestinatario)
                .destinatarioTelefono(telefonoDestinatario)
                .asunto(asunto)
                .mensaje(mensajeLimitado)
                .cita(cita)
                .enviada(enviado)
                .build();
            
            if (enviado && idExterno != null) {
                comunicacion.marcarComoEnviada(idExterno);
            } else if (!enviado) {
                comunicacion.registrarFalloEnvio("Error al enviar notificación");
            }
            
            comunicacionRepository.save(comunicacion);
            log.debug("Notificación guardada en BD: ID={}, Asunto={}", 
                    comunicacion.getIdComunicacion(), asunto);
        } catch (Exception e) {
            log.error("Error al guardar notificación en BD: {}", e.getMessage(), e);
            // No propagamos la excepción para no interrumpir el flujo principal
        }
    }
}

