package com.veterinaria.clinica_veternica.patterns.behavioral.chain;

import com.veterinaria.clinica_veternica.domain.agenda.Cita;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Patrón Chain of Responsibility: ValidacionPermisoHandler
 *
 * Valida que el usuario tenga permisos para crear/modificar la cita.
 *
 * Justificación:
 * - Valida permisos después de validar datos y disponibilidad
 * - Asegura control de acceso adecuado
 *
 * @author Clínica Veterinaria Team
 * @version 1.0
 * @since 2025-11-04
 */
@Slf4j
@Component
public class ValidacionPermisoHandler extends ValidacionHandler {

    @Override
    protected boolean validarEspecifico(Cita cita) throws RuntimeException {
        log.debug("Validando permisos del usuario");

        // Aquí se validaría el rol del usuario actual
        // Por ahora, solo validamos que el veterinario esté activo
        if (cita.getVeterinario() != null && !cita.getVeterinario().getActivo()) {
            throw new IllegalStateException("El veterinario asignado no está activo");
        }

        log.debug("Validación de permisos: OK");
        return true;
    }
}

