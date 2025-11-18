package com.veterinaria.clinica_veternica.patterns.behavioral.template;

import com.veterinaria.clinica_veternica.domain.agenda.Cita;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Patrón Template Method: AtencionEmergencia
 *
 * Implementación concreta para atención de emergencias.
 * Define los pasos específicos para este tipo de atención.
 *
 * Justificación:
 * - Implementa el template para emergencias
 * - Define pasos específicos: triage, estabilización, tratamiento de urgencia
 *
 * @author Clínica Veterinaria Team
 * @version 1.0
 * @since 2025-11-04
 */
@Slf4j
@Component
public class AtencionEmergencia extends AtencionTemplate {

    @Override
    protected void prepararAtencion(Cita cita) {
        log.warn("PREPARANDO ATENCIÓN DE EMERGENCIA para mascota: {}", cita.getMascota().getNombre());
        // Preparación específica para emergencia:
        // 1. Activar protocolo de emergencia
        // 2. Preparar equipo de emergencia
        // 3. Notificar personal de guardia
    }

    @Override
    protected void validarPreAtencion(Cita cita) {
        // En emergencias, las validaciones son mínimas
        log.warn("Validación rápida para emergencia - prioridad máxima");
        if (cita.getMascota() == null) {
            throw new IllegalStateException("La emergencia debe tener una mascota asociada");
        }
    }

    @Override
    protected void ejecutarAtencion(Cita cita) {
        log.warn("EJECUTANDO ATENCIÓN DE EMERGENCIA para cita ID: {}", cita.getIdCita());
        // Pasos específicos de emergencia:
        // 1. Triage (evaluación rápida de prioridad)
        // 2. Estabilización inmediata
        // 3. Tratamiento de urgencia
        // 4. Monitoreo continuo
    }

    @Override
    protected void finalizarAtencion(Cita cita) {
        super.finalizarAtencion(cita);
        log.info("Emergencia atendida. Continuar con monitoreo intensivo.");
        // Pasos adicionales específicos de emergencia:
        // 1. Monitoreo intensivo post-emergencia
        // 2. Evaluación de necesidad de hospitalización
        // 3. Instrucciones de cuidado crítico
    }
}

