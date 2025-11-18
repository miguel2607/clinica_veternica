package com.veterinaria.clinica_veternica.patterns.behavioral.observer;

import com.veterinaria.clinica_veternica.domain.agenda.Cita;
import com.veterinaria.clinica_veternica.domain.agenda.EstadoCita;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

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
        log.info("Enviando notificación de cancelación de cita {}: {}", cita.getIdCita(), motivo);
        enviarNotificacionCancelacion(cita);
    }

    private void enviarNotificacionConfirmacion(Cita cita) {
        // Implementar envío de notificación de confirmación
        log.debug("Notificación de confirmación enviada para cita: {}", cita.getIdCita());
    }

    private void enviarNotificacionAtencion(Cita cita) {
        // Implementar envío de notificación de atención completada
        log.debug("Notificación de atención enviada para cita: {}", cita.getIdCita());
    }

    private void enviarNotificacionCreacion(Cita cita) {
        // Implementar envío de notificación de creación
        log.debug("Notificación de creación enviada para cita: {}", cita.getIdCita());
    }

    private void enviarNotificacionCancelacion(Cita cita) {
        // Implementar envío de notificación de cancelación
        log.debug("Notificación de cancelación enviada para cita: {}", cita.getIdCita());
    }
}

