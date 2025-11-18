package com.veterinaria.clinica_veternica.patterns.behavioral.state;

import com.veterinaria.clinica_veternica.domain.agenda.Cita;
import com.veterinaria.clinica_veternica.domain.agenda.EstadoCita;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * Estado concreto: Cita Atendida
 *
 * Estado final de una cita atendida. No permite más cambios.
 *
 * @author Clínica Veterinaria Team
 * @version 1.0
 * @since 2025-11-04
 */
@Slf4j
@Component
public class CitaAtendidaState implements CitaState {

    @Override
    public void confirmar(Cita cita) {
        throw new IllegalStateException("No se puede confirmar una cita que ya fue atendida.");
    }

    @Override
    public void atender(Cita cita) {
        // Si ya está siendo atendida, solo registrar el fin
        if (cita.getFechaHoraInicioAtencion() != null && cita.getFechaHoraFinAtencion() == null) {
            log.info("Finalizando atención de cita {}", cita.getIdCita());
            cita.setFechaHoraFinAtencion(LocalDateTime.now());
            
            // La duración real se calcula automáticamente con getDuracionRealMinutos()
            // No es necesario almacenarla como campo
        } else {
            log.warn("La cita {} ya fue completamente atendida", cita.getIdCita());
        }
    }

    @Override
    public void cancelar(Cita cita, String motivo) {
        throw new IllegalStateException("No se puede cancelar una cita que ya fue atendida.");
    }

    @Override
    public String getNombreEstado() {
        return "ATENDIDA";
    }
}

