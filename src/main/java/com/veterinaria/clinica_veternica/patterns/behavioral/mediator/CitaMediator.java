package com.veterinaria.clinica_veternica.patterns.behavioral.mediator;

import com.veterinaria.clinica_veternica.domain.agenda.Cita;

/**
 * Patrón Mediator: CitaMediator (Interface)
 *
 * Define la interfaz para el mediador que coordina la comunicación
 * entre los diferentes componentes de una cita.
 *
 * Justificación:
 * - Reduce el acoplamiento entre componentes (Veterinario, Mascota, Horario, Servicio, Notificaciones)
 * - Centraliza la comunicación compleja
 * - Facilita agregar nuevos componentes sin modificar los existentes
 *
 * @author Clínica Veterinaria Team
 * @version 1.0
 * @since 2025-11-04
 */
public interface CitaMediator {

    /**
     * Crea una nueva cita coordinando todos los componentes.
     *
     * @param cita Cita a crear
     * @return Cita creada
     */
    Cita crearCita(Cita cita);

    /**
     * Confirma una cita coordinando notificaciones y actualizaciones.
     *
     * @param citaId ID de la cita a confirmar
     */
    void confirmarCita(Long citaId);

    /**
     * Cancela una cita coordinando notificaciones y liberación de recursos.
     *
     * @param citaId ID de la cita a cancelar
     * @param motivo Motivo de cancelación
     */
    void cancelarCita(Long citaId, String motivo);

    /**
     * Notifica a los componentes sobre un cambio en la cita.
     *
     * @param citaId ID de la cita
     * @param evento Tipo de evento
     */
    void notificarCambio(Long citaId, String evento);
}

