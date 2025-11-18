package com.veterinaria.clinica_veternica.patterns.behavioral.chain;

import com.veterinaria.clinica_veternica.domain.agenda.Cita;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Patrón Chain of Responsibility: ValidacionDatosHandler
 *
 * Valida que los datos básicos de la cita sean correctos.
 *
 * Justificación:
 * - Primera validación en la cadena
 * - Verifica datos obligatorios antes de validaciones más complejas
 *
 * @author Clínica Veterinaria Team
 * @version 1.0
 * @since 2025-11-04
 */
@Slf4j
@Component
public class ValidacionDatosHandler extends ValidacionHandler {

    @Override
    protected boolean validarEspecifico(Cita cita) throws RuntimeException {
        log.debug("Validando datos básicos de la cita");

        if (cita.getMascota() == null) {
            throw new IllegalArgumentException("La cita debe tener una mascota asociada");
        }

        if (cita.getVeterinario() == null) {
            throw new IllegalArgumentException("La cita debe tener un veterinario asignado");
        }

        if (cita.getServicio() == null) {
            throw new IllegalArgumentException("La cita debe tener un servicio definido");
        }

        if (cita.getFechaCita() == null) {
            throw new IllegalArgumentException("La cita debe tener una fecha");
        }

        if (cita.getHoraCita() == null) {
            throw new IllegalArgumentException("La cita debe tener una hora");
        }

        if (cita.getMotivoConsulta() == null || cita.getMotivoConsulta().isBlank()) {
            throw new IllegalArgumentException("La cita debe tener un motivo de consulta");
        }

        log.debug("Validación de datos básicos: OK");
        return true;
    }
}

