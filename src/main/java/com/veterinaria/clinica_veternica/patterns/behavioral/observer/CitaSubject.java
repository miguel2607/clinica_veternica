package com.veterinaria.clinica_veternica.patterns.behavioral.observer;

import com.veterinaria.clinica_veternica.domain.agenda.Cita;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Patrón Observer: CitaSubject
 *
 * Mantiene una lista de observadores y notifica automáticamente
 * cuando ocurren cambios en las citas.
 *
 * @author Clínica Veterinaria Team
 * @version 1.0
 * @since 2025-11-04
 */
@Slf4j
@Component
public class CitaSubject {

    private static final String MSG_ERROR_NOTIFICAR = "Error al notificar observador {}: {}";
    private static final String MSG_ERROR_INESPERADO = "Error inesperado al notificar observador {}: {}";

    private final List<CitaObserver> observers = new ArrayList<>();

    /**
     * Agrega un observador.
     */
    public void addObserver(CitaObserver observer) {
        if (observer != null && !observers.contains(observer)) {
            observers.add(observer);
            log.debug("Observador agregado: {}", observer.getClass().getSimpleName());
        }
    }

    /**
     * Elimina un observador.
     */
    public void removeObserver(CitaObserver observer) {
        observers.remove(observer);
        log.debug("Observador eliminado: {}", observer.getClass().getSimpleName());
    }

    /**
     * Notifica a todos los observadores sobre un cambio de estado.
     */
    public void notifyStateChanged(Cita cita, String estadoAnterior, String estadoNuevo) {
        log.debug("Notificando cambio de estado de cita {}: {} -> {}", 
                 cita.getIdCita(), estadoAnterior, estadoNuevo);
        
        for (CitaObserver observer : observers) {
            try {
                observer.onCitaStateChanged(cita, estadoAnterior, estadoNuevo);
            } catch (IllegalArgumentException | IllegalStateException e) {
                log.error(MSG_ERROR_NOTIFICAR, 
                         observer.getClass().getSimpleName(), e.getMessage(), e);
            } catch (RuntimeException e) {
                log.error(MSG_ERROR_INESPERADO, 
                         observer.getClass().getSimpleName(), e.getMessage(), e);
            }
        }
    }

    /**
     * Notifica a todos los observadores sobre una nueva cita.
     */
    public void notifyCitaCreated(Cita cita) {
        log.debug("Notificando creación de cita: {}", cita.getIdCita());
        
        for (CitaObserver observer : observers) {
            try {
                observer.onCitaCreated(cita);
            } catch (IllegalArgumentException | IllegalStateException e) {
                log.error(MSG_ERROR_NOTIFICAR, 
                         observer.getClass().getSimpleName(), e.getMessage(), e);
            } catch (RuntimeException e) {
                log.error(MSG_ERROR_INESPERADO, 
                         observer.getClass().getSimpleName(), e.getMessage(), e);
            }
        }
    }

    /**
     * Notifica a todos los observadores sobre una cita cancelada.
     */
    public void notifyCitaCancelled(Cita cita, String motivo) {
        log.debug("Notificando cancelación de cita {}: {}", cita.getIdCita(), motivo);
        
        for (CitaObserver observer : observers) {
            try {
                observer.onCitaCancelled(cita, motivo);
            } catch (IllegalArgumentException | IllegalStateException e) {
                log.error(MSG_ERROR_NOTIFICAR, 
                         observer.getClass().getSimpleName(), e.getMessage(), e);
            } catch (RuntimeException e) {
                log.error(MSG_ERROR_INESPERADO, 
                         observer.getClass().getSimpleName(), e.getMessage(), e);
            }
        }
    }
}

