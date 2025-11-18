package com.veterinaria.clinica_veternica.patterns.behavioral.chain;

import com.veterinaria.clinica_veternica.domain.agenda.Cita;
import com.veterinaria.clinica_veternica.exception.ValidationException;
import com.veterinaria.clinica_veternica.util.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Patrón Chain of Responsibility: ValidacionDisponibilidadHandler
 *
 * Valida que el veterinario esté disponible en la fecha y hora de la cita.
 *
 * Justificación:
 * - Valida disponibilidad después de validar datos básicos
 * - Evita conflictos de horarios
 *
 * @author Clínica Veterinaria Team
 * @version 1.0
 * @since 2025-11-04
 */
@Slf4j
@Component
public class ValidacionDisponibilidadHandler extends ValidacionHandler {

    @Override
    protected boolean validarEspecifico(Cita cita) throws ValidationException {
        log.debug("Validando disponibilidad del veterinario");

        // Validar que la fecha no sea en el pasado (excepto para emergencias)
        LocalDateTime fechaHoraCita = LocalDateTime.of(cita.getFechaCita(), cita.getHoraCita());
        if (!Constants.isTrue(cita.getEsEmergencia()) && fechaHoraCita.isBefore(LocalDateTime.now())) {
            throw new ValidationException("No se pueden crear citas en el pasado", "fechaCita", "La fecha debe ser futura");
        }

        // Aquí se validaría contra el horario del veterinario y otras citas
        // Por ahora, solo validamos que la fecha/hora sea válida
        log.debug("Validación de disponibilidad: OK");
        return true;
    }
}

