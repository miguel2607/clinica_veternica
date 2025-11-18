package com.veterinaria.clinica_veternica.patterns.behavioral.chain;

import com.veterinaria.clinica_veternica.domain.agenda.Cita;
import com.veterinaria.clinica_veternica.exception.ValidationException;
import lombok.extern.slf4j.Slf4j;

/**
 * Patrón Chain of Responsibility: ValidacionHandler (Abstract)
 *
 * Handler base para la cadena de validaciones de citas.
 * Cada handler valida un aspecto específico y pasa al siguiente si es exitoso.
 *
 * Justificación:
 * - Permite encadenar múltiples validaciones de forma flexible
 * - Facilita agregar o quitar validaciones sin modificar código existente
 * - Separa responsabilidades de validación
 *
 * @author Clínica Veterinaria Team
 * @version 1.0
 * @since 2025-11-04
 */
@Slf4j
public abstract class ValidacionHandler {

    protected ValidacionHandler siguienteHandler;

    /**
     * Establece el siguiente handler en la cadena.
     *
     * @param handler Siguiente handler
     * @return El handler para encadenamiento fluido
     */
    public ValidacionHandler setSiguiente(ValidacionHandler handler) {
        this.siguienteHandler = handler;
        return handler;
    }

    /**
     * Procesa la validación. Si pasa, delega al siguiente handler.
     *
     * @param cita Cita a validar
     * @return true si todas las validaciones pasan
     * @throws ValidationException Si alguna validación falla
     */
    public final boolean validar(Cita cita) throws ValidationException {
        log.debug("Ejecutando validación: {}", this.getClass().getSimpleName());

        // Ejecutar validación específica
        if (!validarEspecifico(cita)) {
            return false;
        }

        // Si hay siguiente handler, continuar la cadena
        if (siguienteHandler != null) {
            return siguienteHandler.validar(cita);
        }

        // Si no hay más handlers, todas las validaciones pasaron
        return true;
    }

    /**
     * Realiza la validación específica de este handler.
     * Debe ser implementado por las subclases.
     *
     * @param cita Cita a validar
     * @return true si la validación pasa
     * @throws ValidationException Si la validación falla
     */
    protected abstract boolean validarEspecifico(Cita cita) throws ValidationException;
}

