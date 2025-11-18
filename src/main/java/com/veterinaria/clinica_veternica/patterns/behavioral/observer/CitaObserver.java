package com.veterinaria.clinica_veternica.patterns.behavioral.observer;

import com.veterinaria.clinica_veternica.domain.agenda.Cita;

/**
 * Patrón Observer: CitaObserver (Interface)
 *
 * Define la interfaz para observadores que reaccionan a cambios
 * en el estado de las citas.
 *
 * Justificación:
 * - Permite notificar automáticamente a múltiples componentes
 *   cuando cambia el estado de una cita
 * - Desacopla los observadores del sujeto
 * - Facilita agregar nuevos tipos de notificaciones sin modificar código existente
 *
 * Observadores típicos:
 * - NotificacionObserver: Envía notificaciones al propietario
 * - RecordatorioObserver: Programa recordatorios
 * - InventarioObserver: Actualiza inventario si se requiere
 * - AuditoriaObserver: Registra cambios para auditoría
 *
 * @author Clínica Veterinaria Team
 * @version 1.0
 * @since 2025-11-04
 */
public interface CitaObserver {

    /**
     * Se invoca cuando cambia el estado de una cita.
     *
     * @param cita Cita que cambió de estado
     * @param estadoAnterior Estado anterior de la cita
     * @param estadoNuevo Nuevo estado de la cita
     */
    void onCitaStateChanged(Cita cita, String estadoAnterior, String estadoNuevo);

    /**
     * Se invoca cuando se crea una nueva cita.
     *
     * @param cita Nueva cita creada
     */
    void onCitaCreated(Cita cita);

    /**
     * Se invoca cuando se cancela una cita.
     *
     * @param cita Cita cancelada
     * @param motivo Motivo de cancelación
     */
    void onCitaCancelled(Cita cita, String motivo);
}

