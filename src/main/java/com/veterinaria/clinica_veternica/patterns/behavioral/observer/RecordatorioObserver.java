package com.veterinaria.clinica_veternica.patterns.behavioral.observer;

import com.veterinaria.clinica_veternica.domain.agenda.Cita;
import com.veterinaria.clinica_veternica.domain.agenda.EstadoCita;
import com.veterinaria.clinica_veternica.domain.comunicacion.Comunicacion;
import com.veterinaria.clinica_veternica.patterns.creational.abstractfactory.NotificacionFactory;
import com.veterinaria.clinica_veternica.patterns.creational.abstractfactory.EmailNotificacionFactory;
import com.veterinaria.clinica_veternica.patterns.creational.singleton.ConfigurationManager;
import com.veterinaria.clinica_veternica.repository.ComunicacionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * Patrón Observer: RecordatorioObserver
 *
 * Observador que programa y envía recordatorios automáticos de citas
 * a los propietarios de las mascotas.
 *
 * PROPÓSITO:
 * - Programa recordatorios automáticos para citas programadas
 * - Envía recordatorios en diferentes momentos (24h antes, 2h antes)
 * - Reduce las ausencias a citas mediante recordatorios proactivos
 * - Mejora la experiencia del cliente
 *
 * ESTRATEGIA DE RECORDATORIOS:
 * - 24 horas antes: Recordatorio inicial
 * - 2 horas antes: Recordatorio de confirmación
 * - 1 hora antes: Recordatorio final (opcional)
 *
 * Justificación:
 * - Reduce significativamente las ausencias a citas
 * - Mejora la gestión de la agenda
 * - Proporciona un servicio proactivo a los clientes
 * - Automatiza una tarea que sería manual
 *
 * @author Clínica Veterinaria Team
 * @version 1.0
 * @since 2025-01-27
 */
@Slf4j
@Component
public class RecordatorioObserver implements CitaObserver {

    private final ComunicacionRepository comunicacionRepository;
    private final EmailNotificacionFactory emailFactory;
    private final RecordatorioObserver self;
    private final ConfigurationManager configurationManager;

    public RecordatorioObserver(ComunicacionRepository comunicacionRepository,
                                EmailNotificacionFactory emailFactory,
                                @Lazy RecordatorioObserver self,
                                ConfigurationManager configurationManager) {
        this.comunicacionRepository = comunicacionRepository;
        this.emailFactory = emailFactory;
        this.self = self;
        this.configurationManager = configurationManager;
    }

    /**
     * Se invoca cuando cambia el estado de una cita.
     * Programa recordatorios si la cita es confirmada.
     *
     * PROPÓSITO: Programa recordatorios automáticos cuando una cita es confirmada.
     */
    @Override
    public void onCitaStateChanged(Cita cita, String estadoAnterior, String estadoNuevo) {
        log.debug("RecordatorioObserver: Cambio de estado de cita {}: {} -> {}", 
                cita.getIdCita(), estadoAnterior, estadoNuevo);

        // Solo programar recordatorios para citas confirmadas
        if (EstadoCita.CONFIRMADA.name().equals(estadoNuevo)) {
            self.programarRecordatorios(cita);
        } else if (EstadoCita.CANCELADA.name().equals(estadoNuevo) ||
                   EstadoCita.ATENDIDA.name().equals(estadoNuevo)) {
            // Cancelar recordatorios pendientes si la cita se cancela o atiende
            cancelarRecordatoriosPendientes(cita);
        }
    }

    /**
     * Se invoca cuando se crea una nueva cita.
     * Programa recordatorios si la cita está confirmada.
     *
     * PROPÓSITO: Programa recordatorios para nuevas citas confirmadas.
     */
    @Override
    public void onCitaCreated(Cita cita) {
        log.debug("RecordatorioObserver: Nueva cita creada: {}", cita.getIdCita());

        if (cita.getEstado() == EstadoCita.CONFIRMADA) {
            self.programarRecordatorios(cita);
        }
    }

    /**
     * Se invoca cuando se cancela una cita.
     * Cancela todos los recordatorios programados.
     *
     * PROPÓSITO: Evita enviar recordatorios para citas canceladas.
     */
    @Override
    public void onCitaCancelled(Cita cita, String motivo) {
        log.info("RecordatorioObserver: Cita cancelada {}. Cancelando recordatorios pendientes", 
                cita.getIdCita());
        cancelarRecordatoriosPendientes(cita);
    }

    /**
     * Programa recordatorios automáticos para una cita.
     *
     * PROPÓSITO: Crea recordatorios programados para diferentes momentos antes de la cita.
     *
     * @param cita Cita para la cual programar recordatorios
     */
    @Async
    public void programarRecordatorios(Cita cita) {
        log.info("Programando recordatorios para cita: {}", cita.getIdCita());

        // Verificar si los recordatorios automáticos están habilitados
        Boolean recordatoriosHabilitados = configurationManager.getRecordatoriosAutomaticos();
        if (recordatoriosHabilitados == null || !recordatoriosHabilitados) {
            log.info("Recordatorios automáticos deshabilitados en configuración");
            return;
        }

        LocalDateTime fechaHoraCita = LocalDateTime.of(cita.getFechaCita(), cita.getHoraCita());
        LocalDateTime ahora = LocalDateTime.now();

        // Calcular tiempo hasta la cita
        long horasHastaCita = ChronoUnit.HOURS.between(ahora, fechaHoraCita);

        // Obtener horas de anticipación desde configuración (por defecto 24 horas)
        Integer horasAnticipacion = configurationManager.getHorasAnticipacionRecordatorio();

        // Recordatorio principal (configurado en sistema, por defecto 24 horas antes)
        if (horasHastaCita >= horasAnticipacion) {
            LocalDateTime fechaRecordatorioPrincipal = fechaHoraCita.minusHours(horasAnticipacion);
            crearRecordatorio(cita, fechaRecordatorioPrincipal,
                String.format("Recordatorio de cita - %d horas antes", horasAnticipacion));
            log.debug("Recordatorio principal programado para {}h antes: {}", horasAnticipacion, fechaRecordatorioPrincipal);
        }

        // Recordatorio 2 horas antes
        if (horasHastaCita >= 2) {
            LocalDateTime fechaRecordatorio2h = fechaHoraCita.minusHours(2);
            crearRecordatorio(cita, fechaRecordatorio2h, "Recordatorio de cita - 2 horas antes");
            log.debug("Recordatorio programado para 2h antes: {}", fechaRecordatorio2h);
        }

        // Recordatorio 1 hora antes (opcional, solo si la cita es en más de 1 hora)
        if (horasHastaCita >= 1) {
            LocalDateTime fechaRecordatorio1h = fechaHoraCita.minusHours(1);
            crearRecordatorio(cita, fechaRecordatorio1h, "Recordatorio de cita - 1 hora antes");
            log.debug("Recordatorio programado para 1h antes: {}", fechaRecordatorio1h);
        }

        log.info("Recordatorios programados exitosamente para cita: {} (usando ConfigurationManager)", cita.getIdCita());
    }

    /**
     * Crea un recordatorio programado.
     *
     * PROPÓSITO: Crea la entidad Comunicacion que será procesada por un scheduler.
     *
     * @param cita Cita asociada
     * @param fechaEnvio Fecha y hora en que se debe enviar el recordatorio
     * @param mensaje Mensaje del recordatorio
     */
    private void crearRecordatorio(Cita cita, LocalDateTime fechaEnvio, String mensaje) {
        Comunicacion comunicacion = Comunicacion.builder()
                .tipo("RECORDATORIO")
                .canal("EMAIL")
                .destinatarioNombre(cita.getMascota().getPropietario().getNombreCompleto())
                .destinatarioEmail(cita.getMascota().getPropietario().getEmail())
                .asunto("Recordatorio de Cita")
                .mensaje(mensaje)
                .cita(cita)
                .fechaProgramadaEnvio(fechaEnvio)
                .enviada(false)
                .intentosEnvio(0)
                .maxIntentos(3)
                .build();

        comunicacionRepository.save(comunicacion);
        log.debug("Recordatorio creado: {} para fecha {}", comunicacion.getIdComunicacion(), fechaEnvio);
    }

    /**
     * Cancela todos los recordatorios pendientes de una cita.
     *
     * PROPÓSITO: Evita enviar recordatorios para citas que ya no están activas.
     *
     * @param cita Cita cuyos recordatorios se deben cancelar
     */
    private void cancelarRecordatoriosPendientes(Cita cita) {
        comunicacionRepository.findByTipo("RECORDATORIO").stream()
                .filter(c -> c.getCita() != null && c.getCita().getIdCita().equals(cita.getIdCita()))
                .filter(c -> !com.veterinaria.clinica_veternica.util.Constants.isTrue(c.getEnviada()))
                .forEach(comunicacion -> {
                    comunicacion.setEnviada(true);
                    comunicacion.setFechaEnvio(LocalDateTime.now());
                    comunicacion.setMensajeError("Cancelado: Cita cancelada o atendida");
                    comunicacionRepository.save(comunicacion);
                    log.debug("Recordatorio cancelado: {}", comunicacion.getIdComunicacion());
                });
    }

    /**
     * Envía un recordatorio inmediatamente.
     * Este método es llamado por el scheduler cuando es hora de enviar un recordatorio.
     *
     * PROPÓSITO: Procesa y envía recordatorios programados.
     *
     * @param comunicacion Comunicación (recordatorio) a enviar
     */
    @Async
    public void enviarRecordatorio(Comunicacion comunicacion) {
        if (com.veterinaria.clinica_veternica.util.Constants.isTrue(comunicacion.getEnviada())) {
            log.debug("Recordatorio {} ya fue enviado", comunicacion.getIdComunicacion());
            return;
        }

        log.info("Enviando recordatorio {} para cita {}", 
                comunicacion.getIdComunicacion(), 
                comunicacion.getCita() != null ? comunicacion.getCita().getIdCita() : "N/A");

        try {
            // Obtener información de la cita
            Cita cita = comunicacion.getCita();
            if (cita == null) {
                log.warn("Comunicación {} no tiene cita asociada", comunicacion.getIdComunicacion());
                return;
            }

            String emailPropietario = cita.getMascota().getPropietario().getEmail();
            String nombreMascota = cita.getMascota().getNombre();
            String nombrePropietario = cita.getMascota().getPropietario().getNombreCompleto();

            // Construir mensaje
            String mensaje = String.format("""
                    Estimado/a %s,
                    
                    Le recordamos que tiene una cita programada para su mascota %s:
                    Fecha: %s
                    Hora: %s
                    Veterinario: %s
                    Servicio: %s
                    
                    Por favor confirme su asistencia.
                    
                    Saludos,
                    Clínica Veterinaria""",
                    nombrePropietario,
                    nombreMascota,
                    cita.getFechaCita(),
                    cita.getHoraCita(),
                    cita.getVeterinario().getNombreCompleto(),
                    cita.getServicio().getNombre()
            );

            // Enviar notificación usando Abstract Factory
            var mensajeNotificacion = emailFactory.crearMensaje(emailPropietario, "Recordatorio de Cita", mensaje);
            var enviador = emailFactory.crearEnviador();
            enviador.enviar(mensajeNotificacion);

            // Marcar como enviado
            comunicacion.marcarComoEnviada("EMAIL-" + System.currentTimeMillis());
            comunicacionRepository.save(comunicacion);

            log.info("Recordatorio enviado exitosamente: {}", comunicacion.getIdComunicacion());

        } catch (IllegalArgumentException | IllegalStateException e) {
            log.error("Error de validación al enviar recordatorio {}: {}", 
                    comunicacion.getIdComunicacion(), e.getMessage(), e);
            comunicacion.registrarFalloEnvio("Error de validación: " + e.getMessage());
            comunicacionRepository.save(comunicacion);
        } catch (RuntimeException e) {
            log.error("Error al enviar recordatorio {}: {}", 
                    comunicacion.getIdComunicacion(), e.getMessage(), e);
            comunicacion.registrarFalloEnvio("Error: " + e.getMessage());
            comunicacionRepository.save(comunicacion);
        }
    }
}

