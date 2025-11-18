package com.veterinaria.clinica_veternica.patterns.behavioral.state;

import com.veterinaria.clinica_veternica.domain.agenda.Cita;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Estado concreto: Cita Cancelada
 *
 * Estado final de una cita cancelada. No permite más cambios.
 *
 * @author Clínica Veterinaria Team
 * @version 1.0
 * @since 2025-11-04
 */
@Slf4j
@Component
public class CitaCanceladaState implements CitaState {

    @Override
    public void confirmar(Cita cita) {
        throw new IllegalStateException("No se puede confirmar una cita que fue cancelada.");
    }

    @Override
    public void atender(Cita cita) {
        throw new IllegalStateException("No se puede atender una cita que fue cancelada.");
    }

    @Override
    public void cancelar(Cita cita, String motivo) {
        log.warn("La cita {} ya está cancelada", cita.getIdCita());
        // Ya está cancelada, no hacer nada
    }

    @Override
    public String getNombreEstado() {
        return "CANCELADA";
    }
}

