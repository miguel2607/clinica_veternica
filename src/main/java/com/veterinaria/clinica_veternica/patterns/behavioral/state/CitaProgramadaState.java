package com.veterinaria.clinica_veternica.patterns.behavioral.state;

import com.veterinaria.clinica_veternica.domain.agenda.Cita;
import com.veterinaria.clinica_veternica.domain.agenda.EstadoCita;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Estado concreto: Cita Programada
 *
 * Estado inicial de una cita. Permite confirmar o cancelar.
 *
 * @author Clínica Veterinaria Team
 * @version 1.0
 * @since 2025-11-04
 */
@Slf4j
@Component
public class CitaProgramadaState implements CitaState {

    @Override
    public void confirmar(Cita cita) {
        log.info("Confirmando cita {} desde estado PROGRAMADA", cita.getIdCita());
        cita.setEstado(EstadoCita.CONFIRMADA);
        cita.setFechaConfirmacion(LocalDateTime.now());
    }

    @Override
    public void atender(Cita cita) {
        throw new IllegalStateException("No se puede atender una cita que solo está programada. Debe confirmarse primero.");
    }

    @Override
    public void cancelar(Cita cita, String motivo) {
        log.info("Cancelando cita {} desde estado PROGRAMADA: {}", cita.getIdCita(), motivo);
        cita.setEstado(EstadoCita.CANCELADA);
        cita.setFechaCancelacion(LocalDateTime.now());
        cita.setMotivoCancelacion(motivo);
    }

    @Override
    public String getNombreEstado() {
        return "PROGRAMADA";
    }
}

