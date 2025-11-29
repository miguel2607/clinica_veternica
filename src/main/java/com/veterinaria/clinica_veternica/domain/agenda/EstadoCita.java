package com.veterinaria.clinica_veternica.domain.agenda;

/**
 * Enum que representa los diferentes estados de una cita veterinaria.
 *
 * Define el ciclo de vida de una cita desde su creación hasta su finalización.
 * Se utiliza en conjunto con el patrón State para gestionar transiciones válidas.
 *
 * Estados disponibles:
 * - PROGRAMADA: Cita creada y agendada
 * - CONFIRMADA: Propietario confirmó asistencia
 * - ATENDIDA: Cita completada exitosamente
 * - CANCELADA: Cita cancelada por cualquier motivo
 *
 * Transiciones válidas:
 * PROGRAMADA -> CONFIRMADA -> ATENDIDA
 * PROGRAMADA -> CANCELADA
 * CONFIRMADA -> CANCELADA
 *
 * @author Clínica Veterinaria Team
 * @version 1.0
 * @since 2025-11-02
 */
public enum EstadoCita {
    /**
     * Cita programada y agendada.
     * Estado inicial cuando se crea una nueva cita.
     */
    PROGRAMADA("Programada", "La cita ha sido agendada exitosamente"),

    /**
     * Cita confirmada por el propietario.
     * El propietario ha confirmado que asistirá a la cita.
     */
    CONFIRMADA("Confirmada", "El propietario ha confirmado su asistencia"),

    /**
     * Cita en proceso de atención.
     * El veterinario ha iniciado la atención de la cita.
     */
    EN_ATENCION("En Atención", "La cita está siendo atendida actualmente"),

    /**
     * Cita completada y atendida.
     * La mascota fue atendida y se generó historia clínica.
     * Estado final exitoso.
     */
    ATENDIDA("Atendida", "La cita fue atendida exitosamente"),

    /**
     * Cita cancelada.
     * La cita fue cancelada por el propietario, veterinario o sistema.
     * Estado final.
     */
    CANCELADA("Cancelada", "La cita ha sido cancelada"),

    /**
     * El propietario no asistió a la cita.
     * La cita estaba programada/confirmada pero el propietario no se presentó.
     * Estado final.
     */
    NO_ASISTIO("No Asistió", "El propietario no asistió a la cita programada");

    private final String displayName;
    private final String description;

    /**
     * Constructor del enum.
     *
     * @param displayName Nombre descriptivo del estado
     * @param description Descripción detallada del estado
     */
    EstadoCita(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    /**
     * Obtiene el nombre descriptivo del estado.
     *
     * @return Nombre del estado para mostrar en UI
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Obtiene la descripción del estado.
     *
     * @return Descripción detallada del estado
     */
    public String getDescription() {
        return description;
    }

    /**
     * Verifica si el estado es final (no permite más transiciones).
     *
     * @return true si el estado es ATENDIDA o CANCELADA
     */
    public boolean isFinalState() {
        return this == ATENDIDA || this == CANCELADA || this == NO_ASISTIO;
    }

    /**
     * Verifica si se puede realizar una transición a otro estado.
     *
     * @param newState Nuevo estado deseado
     * @return true si la transición es válida
     */
    public boolean canTransitionTo(EstadoCita newState) {
        return switch (this) {
            case PROGRAMADA -> newState == CONFIRMADA || newState == CANCELADA || newState == NO_ASISTIO;
            case CONFIRMADA -> newState == EN_ATENCION || newState == ATENDIDA || newState == CANCELADA || newState == NO_ASISTIO;
            case EN_ATENCION -> newState == ATENDIDA || newState == CANCELADA;
            case ATENDIDA, CANCELADA, NO_ASISTIO -> false; // Estados finales, no permiten transiciones
        };
    }
}
