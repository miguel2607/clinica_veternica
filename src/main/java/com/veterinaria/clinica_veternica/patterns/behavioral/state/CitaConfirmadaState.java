package com.veterinaria.clinica_veternica.patterns.behavioral.state;

import com.veterinaria.clinica_veternica.domain.agenda.Cita;
import com.veterinaria.clinica_veternica.domain.agenda.EstadoCita;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Estado concreto: Cita Confirmada
 *
 * Estado de una cita confirmada. Permite atender o cancelar.
 *
 * @author Clínica Veterinaria Team
 * @version 1.0
 * @since 2025-11-04
 */
@Slf4j
@Component
public class CitaConfirmadaState implements CitaState {

    @Override
    public void confirmar(Cita cita) {
        log.warn("La cita {} ya está confirmada", cita.getIdCita());
        // Ya está confirmada, no hacer nada
    }

    @Override
    public void atender(Cita cita) {
        log.info("Atendiendo cita {} desde estado CONFIRMADA", cita.getIdCita());
        cita.setEstado(EstadoCita.ATENDIDA);
        cita.setFechaHoraInicioAtencion(LocalDateTime.now());
    }

    @Override
    public void cancelar(Cita cita, String motivo) {
        log.info("Cancelando cita {} desde estado CONFIRMADA: {}", cita.getIdCita(), motivo);
        cita.setEstado(EstadoCita.CANCELADA);
        cita.setFechaCancelacion(LocalDateTime.now());
        cita.setMotivoCancelacion(motivo);
    }

    @Override
    public String getNombreEstado() {
        return "CONFIRMADA";
    }
}

