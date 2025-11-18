package com.veterinaria.clinica_veternica.patterns.behavioral.state;

import com.veterinaria.clinica_veternica.domain.agenda.Cita;

/**
 * Patrón State: CitaState (Interface)
 *
 * Define la interfaz para los diferentes estados de una cita.
 * El comportamiento de una cita cambia según su estado actual.
 *
 * Justificación:
 * - El comportamiento de una cita cambia según su estado
 * - Evita múltiples condicionales en el código
 * - Facilita agregar nuevos estados sin modificar código existente
 * - Encapsula la lógica específica de cada estado
 *
 * Estados implementados:
 * - CitaProgramadaState: Estado inicial de la cita
 * - CitaConfirmadaState: Cita confirmada por el propietario
 * - CitaAtendidaState: Cita ya atendida
 * - CitaCanceladaState: Cita cancelada
 *
 * @author Clínica Veterinaria Team
 * @version 1.0
 * @since 2025-11-04
 */
public interface CitaState {

    /**
     * Confirma la cita.
     *
     * @param cita Cita a confirmar
     */
    void confirmar(Cita cita);

    /**
     * Marca la cita como atendida.
     *
     * @param cita Cita a marcar como atendida
     */
    void atender(Cita cita);

    /**
     * Cancela la cita.
     *
     * @param cita Cita a cancelar
     * @param motivo Motivo de cancelación
     */
    void cancelar(Cita cita, String motivo);

    /**
     * Obtiene el nombre del estado.
     *
     * @return Nombre del estado
     */
    String getNombreEstado();
}

