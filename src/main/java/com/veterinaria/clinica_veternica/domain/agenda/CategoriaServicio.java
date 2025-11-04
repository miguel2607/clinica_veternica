package com.veterinaria.clinica_veternica.domain.agenda;

/**
 * Enum que representa las categorías de servicios veterinarios.
 *
 * Clasifica los servicios según su naturaleza y complejidad.
 * Se utiliza para organizar servicios y aplicar políticas específicas.
 *
 * Categorías:
 * - CLINICO: Servicios clínicos generales (consultas, diagnósticos)
 * - QUIRURGICO: Procedimientos quirúrgicos que requieren cirugía
 * - ESTETICO: Servicios estéticos (baño, peluquería, corte de uñas)
 * - EMERGENCIA: Atención de emergencia urgente
 *
 * @author Clínica Veterinaria Team
 * @version 1.0
 * @since 2025-11-02
 */
public enum CategoriaServicio {
    /**
     * Servicios clínicos generales.
     * Incluye consultas, diagnósticos, tratamientos no quirúrgicos.
     */
    CLINICO("Clínico", "Servicios clínicos y de diagnóstico"),

    /**
     * Servicios quirúrgicos.
     * Procedimientos que requieren cirugía y anestesia.
     */
    QUIRURGICO("Quirúrgico", "Procedimientos quirúrgicos"),

    /**
     * Servicios estéticos.
     * Cuidado estético y de higiene de la mascota.
     */
    ESTETICO("Estético", "Servicios de peluquería y estética"),

    /**
     * Servicios de emergencia.
     * Atención urgente que requiere prioridad.
     */
    EMERGENCIA("Emergencia", "Atención de emergencia urgente");

    private final String displayName;
    private final String description;

    /**
     * Constructor del enum.
     *
     * @param displayName Nombre descriptivo de la categoría
     * @param description Descripción de la categoría
     */
    CategoriaServicio(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    /**
     * Obtiene el nombre descriptivo de la categoría.
     *
     * @return Nombre de la categoría
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Obtiene la descripción de la categoría.
     *
     * @return Descripción de la categoría
     */
    public String getDescription() {
        return description;
    }

    /**
     * Verifica si la categoría requiere veterinario especializado.
     *
     * @return true si requiere especialista
     */
    public boolean requiresSpecialist() {
        return this == QUIRURGICO || this == EMERGENCIA;
    }
}
