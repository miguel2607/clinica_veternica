package com.veterinaria.clinica_veternica.patterns.behavioral.template;

import com.veterinaria.clinica_veternica.domain.agenda.Cita;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Patrón Template Method: AtencionCirugia
 *
 * Implementación concreta para atención de cirugías.
 * Define los pasos específicos para este tipo de atención.
 *
 * Justificación:
 * - Implementa el template para cirugías
 * - Define pasos específicos: preparación quirúrgica, anestesia, cirugía, recuperación
 *
 * @author Clínica Veterinaria Team
 * @version 1.0
 * @since 2025-11-04
 */
@Slf4j
@Component
public class AtencionCirugia extends AtencionTemplate {

    @Override
    protected void prepararAtencion(Cita cita) {
        log.info("Preparando cirugía para mascota: {}", cita.getMascota().getNombre());
        // Preparación específica para cirugía:
        // 1. Verificar ayuno previo
        // 2. Preparar quirófano
        // 3. Verificar disponibilidad de insumos
        // 4. Preparar equipo quirúrgico
    }

    @Override
    protected void validarPreAtencion(Cita cita) {
        super.validarPreAtencion(cita);
        log.debug("Validaciones adicionales para cirugía");
        // Validaciones específicas de cirugía:
        // - Verificar que la mascota cumple requisitos (ayuno, etc.)
        // - Verificar disponibilidad de quirófano
        // - Verificar stock de insumos necesarios
    }

    @Override
    protected void ejecutarAtencion(Cita cita) {
        log.info("Ejecutando cirugía para cita ID: {}", cita.getIdCita());
        // Pasos específicos de cirugía:
        // 1. Aplicar anestesia
        // 2. Realizar procedimiento quirúrgico
        // 3. Monitoreo durante cirugía
        // 4. Cierre y sutura
    }

    @Override
    protected void finalizarAtencion(Cita cita) {
        super.finalizarAtencion(cita);
        log.info("Cirugía finalizada. Iniciando proceso de recuperación.");
        // Pasos adicionales específicos de cirugía:
        // 1. Monitoreo post-operatorio
        // 2. Instrucciones de cuidado post-cirugía
        // 3. Programar seguimiento
    }
}

