package com.veterinaria.clinica_veternica.patterns.behavioral.template;

import com.veterinaria.clinica_veternica.domain.agenda.Cita;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Patrón Template Method: AtencionConsultaGeneral
 *
 * Implementación concreta para atención de consultas generales.
 * Define los pasos específicos para este tipo de atención.
 *
 * Justificación:
 * - Implementa el template para consultas generales
 * - Define pasos específicos: revisión básica, diagnóstico, recomendaciones
 *
 * @author Clínica Veterinaria Team
 * @version 1.0
 * @since 2025-11-04
 */
@Slf4j
@Component
public class AtencionConsultaGeneral extends AtencionTemplate {

    @Override
    protected void prepararAtencion(Cita cita) {
        // Validar que la mascota existe antes de acceder a sus propiedades
        if (cita.getMascota() != null) {
            log.info("Preparando consulta general para mascota: {}", cita.getMascota().getNombre());
        } else {
            log.warn("Preparando consulta general sin mascota asociada");
        }
        // Preparación específica para consulta general
        // Ej: verificar historial, preparar equipo básico
    }

    @Override
    protected void ejecutarAtencion(Cita cita) {
        log.info("Ejecutando consulta general para cita ID: {}", cita.getIdCita());
        // Pasos específicos de consulta general:
        // 1. Revisión física básica
        // 2. Análisis de síntomas
        // 3. Diagnóstico
        // 4. Recomendaciones
    }

    @Override
    protected void finalizarAtencion(Cita cita) {
        super.finalizarAtencion(cita);
        log.info("Consulta general finalizada. Generando recomendaciones para el propietario.");
        // Pasos adicionales específicos de consulta general
    }
}

