package com.veterinaria.clinica_veternica.domain.agenda;

/**
 * Enum que representa los tipos específicos de servicios veterinarios.
 *
 * Define servicios concretos que se ofrecen en la clínica.
 * Cada tipo está asociado a una categoría y tiene características específicas.
 *
 * Tipos de servicios:
 * - CONSULTA_GENERAL: Revisión general de salud
 * - VACUNACION: Aplicación de vacunas
 * - DESPARASITACION: Tratamiento antiparasitario
 * - CIRUGIA: Procedimientos quirúrgicos
 * - BANO: Servicio de baño e higiene
 * - PELUQUERIA: Corte y arreglo estético
 *
 * @author Clínica Veterinaria Team
 * @version 1.0
 * @since 2025-11-02
 */
@SuppressWarnings("unused")
public enum TipoServicio {
    /**
     * Consulta general veterinaria.
     * Categoría: CLINICO
     * Duración estimada: 30 minutos
     */
    CONSULTA_GENERAL("Consulta General", CategoriaServicio.CLINICO, 30),

    /**
     * Vacunación.
     * Categoría: CLINICO
     * Duración estimada: 15 minutos
     */
    VACUNACION("Vacunación", CategoriaServicio.CLINICO, 15),

    /**
     * Desparasitación.
     * Categoría: CLINICO
     * Duración estimada: 15 minutos
     */
    DESPARASITACION("Desparasitación", CategoriaServicio.CLINICO, 15),

    /**
     * Cirugía.
     * Categoría: QUIRURGICO
     * Duración estimada: 120 minutos
     */
    CIRUGIA("Cirugía", CategoriaServicio.QUIRURGICO, 120),

    /**
     * Baño e higiene.
     * Categoría: ESTETICO
     * Duración estimada: 45 minutos
     */
    BANO("Baño", CategoriaServicio.ESTETICO, 45),

    /**
     * Peluquería y corte.
     * Categoría: ESTETICO
     * Duración estimada: 60 minutos
     */
    PELUQUERIA("Peluquería", CategoriaServicio.ESTETICO, 60),

    /**
     * Control de salud.
     * Categoría: CLINICO
     * Duración estimada: 20 minutos
     */
    CONTROL_SALUD("Control de Salud", CategoriaServicio.CLINICO, 20),

    /**
     * Examen de laboratorio.
     * Categoría: CLINICO
     * Duración estimada: 30 minutos
     */
    EXAMEN_LABORATORIO("Examen de Laboratorio", CategoriaServicio.CLINICO, 30),

    /**
     * Radiografía.
     * Categoría: CLINICO
     * Duración estimada: 30 minutos
     */
    RADIOGRAFIA("Radiografía", CategoriaServicio.CLINICO, 30),

    /**
     * Ecografía.
     * Categoría: CLINICO
     * Duración estimada: 40 minutos
     */
    ECOGRAFIA("Ecografía", CategoriaServicio.CLINICO, 40),

    /**
     * Esterilización.
     * Categoría: QUIRURGICO
     * Duración estimada: 90 minutos
     */
    ESTERILIZACION("Esterilización", CategoriaServicio.QUIRURGICO, 90),

    /**
     * Limpieza dental.
     * Categoría: CLINICO
     * Duración estimada: 45 minutos
     */
    LIMPIEZA_DENTAL("Limpieza Dental", CategoriaServicio.CLINICO, 45),

    /**
     * Hospitalización.
     * Categoría: CLINICO
     * Duración estimada: Variable (por día)
     */
    HOSPITALIZACION("Hospitalización", CategoriaServicio.CLINICO, 1440), // 24 horas

    /**
     * Consulta médica (alias de consulta general).
     * Categoría: CLINICO
     * Duración estimada: 30 minutos
     */
    CONSULTA("Consulta", CategoriaServicio.CLINICO, 30),

    /**
     * Atención de emergencia.
     * Categoría: EMERGENCIA
     * Duración estimada: Variable (60 minutos base)
     */
    EMERGENCIA("Emergencia", CategoriaServicio.EMERGENCIA, 60);

    private final String displayName;
    private final CategoriaServicio categoria;
    private final int duracionEstimadaMinutos;

    /**
     * Constructor del enum.
     *
     * @param displayName Nombre descriptivo del servicio
     * @param categoria Categoría a la que pertenece
     * @param duracionEstimadaMinutos Duración estimada en minutos
     */
    TipoServicio(String displayName, CategoriaServicio categoria, int duracionEstimadaMinutos) {
        this.displayName = displayName;
        this.categoria = categoria;
        this.duracionEstimadaMinutos = duracionEstimadaMinutos;
    }

    /**
     * Obtiene el nombre descriptivo del servicio.
     *
     * @return Nombre del servicio
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Obtiene la categoría del servicio.
     *
     * @return Categoría del servicio
     */
    public CategoriaServicio getCategoria() {
        return categoria;
    }

    /**
     * Obtiene la duración estimada en minutos.
     *
     * @return Duración en minutos
     */
    public int getDuracionEstimadaMinutos() {
        return duracionEstimadaMinutos;
    }

    /**
     * Verifica si el servicio requiere insumos médicos.
     *
     * @return true si requiere insumos
     */
    public boolean requiereInsumos() {
        return this == VACUNACION ||
               this == DESPARASITACION ||
               this == CIRUGIA ||
               this == ESTERILIZACION;
    }

    /**
     * Verifica si el servicio es de emergencia.
     *
     * @return true si es servicio de emergencia
     */
    public boolean esEmergencia() {
        return categoria == CategoriaServicio.EMERGENCIA;
    }
}
