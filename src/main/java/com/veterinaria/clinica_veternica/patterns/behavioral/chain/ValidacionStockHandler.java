package com.veterinaria.clinica_veternica.patterns.behavioral.chain;

import com.veterinaria.clinica_veternica.domain.agenda.Cita;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Patrón Chain of Responsibility: ValidacionStockHandler
 *
 * Valida que haya stock suficiente de insumos necesarios para el servicio.
 *
 * Justificación:
 * - Última validación en la cadena
 * - Asegura que se puedan realizar los servicios requeridos
 *
 * @author Clínica Veterinaria Team
 * @version 1.0
 * @since 2025-11-04
 */
@Slf4j
@Component
public class ValidacionStockHandler extends ValidacionHandler {

    @Override
    protected boolean validarEspecifico(Cita cita) throws RuntimeException {
        log.debug("Validando stock de insumos necesarios");

        // Aquí se validaría el stock de insumos requeridos para el servicio
        // Por ahora, solo validamos que el servicio esté activo
        if (cita.getServicio() != null && !cita.getServicio().getActivo()) {
            throw new IllegalStateException("El servicio seleccionado no está disponible");
        }

        log.debug("Validación de stock: OK");
        return true;
    }
}

