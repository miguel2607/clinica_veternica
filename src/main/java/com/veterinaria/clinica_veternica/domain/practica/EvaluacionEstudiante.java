package com.veterinaria.clinica_veternica.domain.practica;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entidad que representa una Evaluación de un Estudiante en práctica.
 *
 * Registro de evaluaciones periódicas realizadas por el supervisor
 * para medir el desempeño y progreso del estudiante durante su práctica.
 *
 * Las evaluaciones incluyen diferentes criterios como:
 * - Conocimientos técnicos
 * - Habilidades prácticas
 * - Actitud profesional
 * - Trabajo en equipo
 * - Puntualidad y responsabilidad
 *
 * @author Clínica Veterinaria Team
 * @version 1.0
 * @since 2025-11-02
 */
@Entity
@Table(name = "evaluaciones_estudiante",
       indexes = {
           @Index(name = "idx_evaluacion_estudiante", columnList = "id_estudiante"),
           @Index(name = "idx_evaluacion_supervisor", columnList = "id_supervisor"),
           @Index(name = "idx_evaluacion_fecha", columnList = "fecha_evaluacion")
       })
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"estudiante", "supervisor"})
public class EvaluacionEstudiante {

    /**
     * Identificador único de la evaluación.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idEvaluacion;

    /**
     * Estudiante evaluado.
     * Relación Many-to-One con Estudiante.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_estudiante", nullable = false)
    @NotNull(message = "El estudiante es obligatorio")
    private Estudiante estudiante;

    /**
     * Supervisor que realiza la evaluación.
     * Relación Many-to-One con SupervisorPractica.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_supervisor", nullable = false)
    @NotNull(message = "El supervisor es obligatorio")
    private SupervisorPractica supervisor;

    /**
     * Fecha de la evaluación.
     */
    @NotNull(message = "La fecha de evaluación es obligatoria")
    @Column(nullable = false)
    private LocalDate fechaEvaluacion;

    /**
     * Período evaluado (ej: "Semana 1-2", "Mes 1", "Evaluación Final").
     */
    @NotBlank(message = "El período es obligatorio")
    @Size(max = 100, message = "El período no puede exceder 100 caracteres")
    @Column(nullable = false, length = 100)
    private String periodo;

    /**
     * Calificación de conocimientos técnicos (0-5).
     */
    @NotNull(message = "La calificación de conocimientos es obligatoria")
    @DecimalMin(value = "0.0", message = "La calificación no puede ser menor a 0")
    @DecimalMax(value = "5.0", message = "La calificación no puede ser mayor a 5")
    @Column(nullable = false)
    private Double conocimientosTecnicos;

    /**
     * Calificación de habilidades prácticas (0-5).
     */
    @NotNull(message = "La calificación de habilidades prácticas es obligatoria")
    @DecimalMin(value = "0.0", message = "La calificación no puede ser menor a 0")
    @DecimalMax(value = "5.0", message = "La calificación no puede ser mayor a 5")
    @Column(nullable = false)
    private Double habilidadesPracticas;

    /**
     * Calificación de actitud profesional (0-5).
     */
    @NotNull(message = "La calificación de actitud profesional es obligatoria")
    @DecimalMin(value = "0.0", message = "La calificación no puede ser menor a 0")
    @DecimalMax(value = "5.0", message = "La calificación no puede ser mayor a 5")
    @Column(nullable = false)
    private Double actitudProfesional;

    /**
     * Calificación de trabajo en equipo (0-5).
     */
    @NotNull(message = "La calificación de trabajo en equipo es obligatoria")
    @DecimalMin(value = "0.0", message = "La calificación no puede ser menor a 0")
    @DecimalMax(value = "5.0", message = "La calificación no puede ser mayor a 5")
    @Column(nullable = false)
    private Double trabajoEquipo;

    /**
     * Calificación de puntualidad y responsabilidad (0-5).
     */
    @NotNull(message = "La calificación de puntualidad es obligatoria")
    @DecimalMin(value = "0.0", message = "La calificación no puede ser menor a 0")
    @DecimalMax(value = "5.0", message = "La calificación no puede ser mayor a 5")
    @Column(nullable = false)
    private Double puntualidadResponsabilidad;

    /**
     * Calificación final promedio (0-5).
     * Se calcula automáticamente como el promedio de todos los criterios.
     */
    @Column(nullable = false)
    private Double calificacion;

    /**
     * Fortalezas identificadas en el estudiante.
     */
    @Size(max = 1000, message = "Las fortalezas no pueden exceder 1000 caracteres")
    @Column(length = 1000)
    private String fortalezas;

    /**
     * Áreas de mejora identificadas.
     */
    @Size(max = 1000, message = "Las áreas de mejora no pueden exceder 1000 caracteres")
    @Column(length = 1000)
    private String areasMejora;

    /**
     * Recomendaciones del supervisor.
     */
    @Size(max = 1000, message = "Las recomendaciones no pueden exceder 1000 caracteres")
    @Column(length = 1000)
    private String recomendaciones;

    /**
     * Observaciones generales de la evaluación.
     */
    @Size(max = 2000, message = "Las observaciones no pueden exceder 2000 caracteres")
    @Column(length = 2000)
    private String observaciones;

    /**
     * Indica si es una evaluación final.
     */
    @Column(nullable = false)
    @Builder.Default
    private Boolean evaluacionFinal = false;

    /**
     * Fecha y hora de creación del registro.
     */
    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    /**
     * Fecha y hora de última modificación.
     */
    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime fechaModificacion;

    // ===================================================================
    // MÉTODOS DE NEGOCIO
    // ===================================================================

    /**
     * Calcula y actualiza la calificación final como promedio de todos los criterios.
     */
    @PrePersist
    @PreUpdate
    public void calcularCalificacionFinal() {
        if (conocimientosTecnicos != null && habilidadesPracticas != null &&
            actitudProfesional != null && trabajoEquipo != null &&
            puntualidadResponsabilidad != null) {

            this.calificacion = (conocimientosTecnicos + habilidadesPracticas +
                               actitudProfesional + trabajoEquipo +
                               puntualidadResponsabilidad) / 5.0;

            // Redondear a 2 decimales
            this.calificacion = Math.round(this.calificacion * 100.0) / 100.0;
        }
    }

    /**
     * Verifica si la evaluación es aprobada (calificación >= 3.0).
     *
     * @return true si es aprobada
     */
    public boolean esAprobada() {
        return calificacion != null && calificacion >= 3.0;
    }

    /**
     * Obtiene el concepto según la calificación.
     *
     * @return Concepto (Excelente, Sobresaliente, Aceptable, Insuficiente, Deficiente)
     */
    public String getConcepto() {
        if (calificacion == null) {
            return "Sin calificar";
        }
        if (calificacion >= 4.5) return "Excelente";
        if (calificacion >= 4.0) return "Sobresaliente";
        if (calificacion >= 3.0) return "Aceptable";
        if (calificacion >= 2.0) return "Insuficiente";
        return "Deficiente";
    }

    /**
     * Marca la evaluación como final.
     */
    public void marcarComoFinal() {
        this.evaluacionFinal = true;
    }

    /**
     * Obtiene el criterio con menor calificación.
     *
     * @return Nombre del criterio con menor calificación
     */
    public String getCriterioMasBajo() {
        double minimo = Math.min(Math.min(Math.min(Math.min(
            conocimientosTecnicos, habilidadesPracticas),
            actitudProfesional), trabajoEquipo),
            puntualidadResponsabilidad);

        if (minimo == conocimientosTecnicos) return "Conocimientos Técnicos";
        if (minimo == habilidadesPracticas) return "Habilidades Prácticas";
        if (minimo == actitudProfesional) return "Actitud Profesional";
        if (minimo == trabajoEquipo) return "Trabajo en Equipo";
        return "Puntualidad y Responsabilidad";
    }

    /**
     * Obtiene el criterio con mayor calificación.
     *
     * @return Nombre del criterio con mayor calificación
     */
    public String getCriterioMasAlto() {
        double maximo = Math.max(Math.max(Math.max(Math.max(
            conocimientosTecnicos, habilidadesPracticas),
            actitudProfesional), trabajoEquipo),
            puntualidadResponsabilidad);

        if (maximo == conocimientosTecnicos) return "Conocimientos Técnicos";
        if (maximo == habilidadesPracticas) return "Habilidades Prácticas";
        if (maximo == actitudProfesional) return "Actitud Profesional";
        if (maximo == trabajoEquipo) return "Trabajo en Equipo";
        return "Puntualidad y Responsabilidad";
    }

    // ===================================================================
    // EQUALS Y HASHCODE
    // ===================================================================

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EvaluacionEstudiante that)) return false;
        return idEvaluacion != null && idEvaluacion.equals(that.idEvaluacion);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
