package com.veterinaria.clinica_veternica.patterns.behavioral.template;

import com.veterinaria.clinica_veternica.domain.agenda.Cita;
import lombok.extern.slf4j.Slf4j;

/**
 * Patrón Template Method: AtencionTemplate (Abstract)
 *
 * Define el esqueleto del algoritmo de atención veterinaria,
 * delegando pasos específicos a las subclases.
 *
 * Justificación:
 * - Define el flujo estándar de atención (preparación, atención, finalización)
 * - Permite variar pasos específicos según el tipo de atención
 * - Evita duplicación de código en el flujo común
 *
 * @author Clínica Veterinaria Team
 * @version 1.0
 * @since 2025-11-04
 */
@Slf4j
public abstract class AtencionTemplate {

    /**
     * Procesa la atención completa de una cita.
     * Este es el método template que define el flujo.
     *
     * @param cita Cita a atender
     */
    public final void procesarAtencion(Cita cita) {
        log.info("Iniciando proceso de atención para cita ID: {}", cita.getIdCita());

        // Paso 1: Preparación (puede variar según tipo de atención)
        prepararAtencion(cita);

        // Paso 2: Validaciones previas
        validarPreAtencion(cita);

        // Paso 3: Ejecutar atención (varía según tipo)
        ejecutarAtencion(cita);

        // Paso 4: Post-atención (puede variar según tipo)
        finalizarAtencion(cita);

        // Paso 5: Registro (común para todos)
        registrarAtencion(cita);

        log.info("Proceso de atención completado para cita ID: {}", cita.getIdCita());
    }

    /**
     * Prepara la atención antes de iniciar.
     * Debe ser implementado por las subclases.
     *
     * @param cita Cita a preparar
     */
    protected abstract void prepararAtencion(Cita cita);

    /**
     * Valida condiciones previas a la atención.
     * Puede ser sobrescrito por las subclases.
     *
     * @param cita Cita a validar
     */
    protected void validarPreAtencion(Cita cita) {
        log.debug("Validando condiciones previas para cita ID: {}", cita.getIdCita());
        if (cita.getMascota() == null) {
            throw new IllegalStateException("La cita debe tener una mascota asociada");
        }
        if (cita.getVeterinario() == null) {
            throw new IllegalStateException("La cita debe tener un veterinario asignado");
        }
    }

    /**
     * Ejecuta la atención específica.
     * Debe ser implementado por las subclases.
     *
     * @param cita Cita a atender
     */
    protected abstract void ejecutarAtencion(Cita cita);

    /**
     * Finaliza la atención después de ejecutarla.
     * Puede ser sobrescrito por las subclases.
     *
     * @param cita Cita atendida
     */
    protected void finalizarAtencion(Cita cita) {
        log.debug("Finalizando atención para cita ID: {}", cita.getIdCita());
        cita.marcarComoAtendida();
    }

    /**
     * Registra la atención en el sistema.
     * Método común para todos los tipos de atención.
     *
     * @param cita Cita atendida
     */
    protected void registrarAtencion(Cita cita) {
        log.info("Registrando atención completada para cita ID: {}", cita.getIdCita());
        // Aquí se podría registrar en historial, generar factura, etc.
    }
}

