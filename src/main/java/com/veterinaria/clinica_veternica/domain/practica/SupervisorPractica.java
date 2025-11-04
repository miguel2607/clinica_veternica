package com.veterinaria.clinica_veternica.domain.practica;

import com.veterinaria.clinica_veternica.domain.usuario.Veterinario;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidad que representa un Supervisor de Práctica.
 *
 * Un supervisor es un veterinario titulado que supervisa y evalúa
 * a estudiantes en práctica profesional en la clínica.
 *
 * Esta entidad extiende la funcionalidad de Veterinario añadiendo
 * capacidades de supervisión y evaluación académica.
 *
 * @author Clínica Veterinaria Team
 * @version 1.0
 * @since 2025-11-02
 */
@Entity
@Table(name = "supervisores_practica",
       indexes = {
           @Index(name = "idx_supervisor_veterinario", columnList = "id_veterinario"),
           @Index(name = "idx_supervisor_activo", columnList = "activo")
       })
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"veterinario", "estudiantes", "evaluaciones"})
public class SupervisorPractica {

    /**
     * Identificador único del supervisor.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idSupervisor;

    /**
     * Veterinario que actúa como supervisor.
     * Relación One-to-One con Veterinario.
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_veterinario", nullable = false, unique = true)
    @NotNull(message = "El veterinario es obligatorio")
    private Veterinario veterinario;

    /**
     * Título académico del supervisor (PhD, Magíster, Especialista).
     */
    @Size(max = 100, message = "El título académico no puede exceder 100 caracteres")
    @Column(length = 100)
    private String tituloAcademico;

    /**
     * Áreas de especialización en las que puede supervisar.
     */
    @Size(max = 500, message = "Las áreas de especialización no pueden exceder 500 caracteres")
    @Column(length = 500)
    private String areasEspecializacion;

    /**
     * Número máximo de estudiantes que puede supervisar simultáneamente.
     */
    @Column
    @Builder.Default
    private Integer maxEstudiantesConcurrentes = 3;

    /**
     * Años de experiencia como supervisor.
     */
    @Column
    @Builder.Default
    private Integer añosExperienciaSupervision = 0;

    /**
     * Certificaciones docentes o pedagógicas.
     */
    @Size(max = 500, message = "Las certificaciones no pueden exceder 500 caracteres")
    @Column(length = 500)
    private String certificacionesDocentes;

    /**
     * Observaciones sobre el supervisor.
     */
    @Size(max = 1000, message = "Las observaciones no pueden exceder 1000 caracteres")
    @Column(length = 1000)
    private String observaciones;

    /**
     * Indica si el supervisor está activo.
     */
    @Column(nullable = false)
    @Builder.Default
    private Boolean activo = true;

    /**
     * Estudiantes supervisados actualmente.
     * Relación One-to-Many con Estudiante.
     */
    @OneToMany(mappedBy = "supervisor", cascade = CascadeType.ALL)
    @Builder.Default
    private List<Estudiante> estudiantes = new ArrayList<>();

    /**
     * Evaluaciones realizadas por el supervisor.
     * Relación One-to-Many con EvaluacionEstudiante.
     */
    @OneToMany(mappedBy = "supervisor", cascade = CascadeType.ALL)
    @Builder.Default
    private List<EvaluacionEstudiante> evaluaciones = new ArrayList<>();

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
     * Obtiene el nombre completo del supervisor (desde el veterinario asociado).
     *
     * @return Nombre completo
     */
    public String getNombreCompleto() {
        return veterinario != null ? veterinario.getNombreCompleto() : "Sin nombre";
    }

    /**
     * Agrega un estudiante al supervisor.
     *
     * @param estudiante Estudiante a agregar
     * @return true si se agregó exitosamente
     * @throws IllegalStateException si se alcanzó el máximo de estudiantes
     */
    public boolean agregarEstudiante(Estudiante estudiante) {
        if (getEstudiantesActivos() >= maxEstudiantesConcurrentes) {
            throw new IllegalStateException("El supervisor ha alcanzado el máximo de estudiantes concurrentes");
        }
        estudiantes.add(estudiante);
        estudiante.setSupervisor(this);
        return true;
    }

    /**
     * Elimina un estudiante del supervisor.
     *
     * @param estudiante Estudiante a eliminar
     */
    public void eliminarEstudiante(Estudiante estudiante) {
        estudiantes.remove(estudiante);
        estudiante.setSupervisor(null);
    }

    /**
     * Agrega una evaluación realizada por el supervisor.
     *
     * @param evaluacion Evaluación a agregar
     */
    public void agregarEvaluacion(EvaluacionEstudiante evaluacion) {
        evaluaciones.add(evaluacion);
        evaluacion.setSupervisor(this);
    }

    /**
     * Obtiene la cantidad de estudiantes activos supervisados.
     *
     * @return Número de estudiantes activos
     */
    public int getEstudiantesActivos() {
        if (estudiantes == null) {
            return 0;
        }
        return (int) estudiantes.stream()
            .filter(e -> e.getActivo() && e.estaPracticaVigente())
            .count();
    }

    /**
     * Obtiene el total de estudiantes supervisados (histórico).
     *
     * @return Total de estudiantes
     */
    public int getTotalEstudiantes() {
        return estudiantes != null ? estudiantes.size() : 0;
    }

    /**
     * Obtiene el total de evaluaciones realizadas.
     *
     * @return Cantidad de evaluaciones
     */
    public int getTotalEvaluaciones() {
        return evaluaciones != null ? evaluaciones.size() : 0;
    }

    /**
     * Verifica si puede aceptar más estudiantes.
     *
     * @return true si puede aceptar más estudiantes
     */
    public boolean puedeAceptarMasEstudiantes() {
        return getEstudiantesActivos() < maxEstudiantesConcurrentes;
    }

    /**
     * Calcula el promedio de calificaciones dadas en sus evaluaciones.
     *
     * @return Promedio de calificaciones
     */
    public Double getPromedioCalificacionesOtorgadas() {
        if (evaluaciones == null || evaluaciones.isEmpty()) {
            return 0.0;
        }
        return evaluaciones.stream()
            .mapToDouble(EvaluacionEstudiante::getCalificacion)
            .average()
            .orElse(0.0);
    }

    /**
     * Activa el supervisor.
     */
    public void activar() {
        this.activo = true;
    }

    /**
     * Desactiva el supervisor.
     */
    public void desactivar() {
        this.activo = false;
    }

    /**
     * Incrementa los años de experiencia en supervisión.
     */
    public void incrementarExperienciaSupervision() {
        this.añosExperienciaSupervision++;
    }

    // ===================================================================
    // EQUALS Y HASHCODE
    // ===================================================================

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SupervisorPractica that)) return false;
        return idSupervisor != null && idSupervisor.equals(that.idSupervisor);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
