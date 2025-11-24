package com.veterinaria.clinica_veternica.config;

import com.veterinaria.clinica_veternica.patterns.behavioral.observer.AuditoriaObserver;
import com.veterinaria.clinica_veternica.patterns.behavioral.observer.CitaSubject;
import com.veterinaria.clinica_veternica.patterns.behavioral.observer.NotificacionObserver;
import com.veterinaria.clinica_veternica.patterns.behavioral.observer.RecordatorioObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;

/**
 * Configuración para registrar Observers en el patrón Observer.
 *
 * PROPÓSITO:
 * - Registra automáticamente todos los observers en CitaSubject al iniciar la aplicación
 * - Activa el patrón Observer para notificaciones automáticas
 * - Asegura que los eventos de citas sean procesados por todos los observers
 *
 * OBSERVERS REGISTRADOS:
 * - AuditoriaObserver: Registra eventos en el sistema de auditoría
 * - NotificacionObserver: Envía notificaciones automáticas a usuarios
 * - RecordatorioObserver: Crea recordatorios para citas programadas
 * - InventarioObserver: Actualiza inventario cuando se usan insumos
 *
 * @author Clínica Veterinaria Team
 * @version 1.0
 * @since 2025-11-18
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class ObserverConfiguration {

    private final CitaSubject citaSubject;
    private final AuditoriaObserver auditoriaObserver;
    private final NotificacionObserver notificacionObserver;
    private final RecordatorioObserver recordatorioObserver;

    /**
     * Registra todos los observers en CitaSubject al iniciar la aplicación.
     *
     * Este método_se ejecuta automáticamente después de la construcción del bean,
     * asegurando que todos los observers estén activos desde el inicio.
     *
     * NOTA: InventarioObserver no se registra aquí porque no implementa CitaObserver.
     * InventarioObserver se gestiona de forma independiente para eventos de inventario.
     */
    @PostConstruct
    public void registrarObservers() {
        citaSubject.addObserver(auditoriaObserver);
        citaSubject.addObserver(notificacionObserver);
        citaSubject.addObserver(recordatorioObserver);

        log.info("✓ Observer Pattern activado: 3 observers registrados exitosamente");
        log.debug("Observers registrados: AuditoriaObserver, NotificacionObserver, RecordatorioObserver");
    }
}
