package com.veterinaria.clinica_veternica.domain.practica;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidad que representa un Estudiante en práctica.
 *
 * Estudiantes de medicina veterinaria que realizan sus prácticas profesionales
 * en la clínica bajo supervisión de veterinarios titulados.
 *
 * @author Clínica Veterinaria Team
 * @version 1.0
 * @since 2025-11-02
 */
@Entity
@Table(name = "estudiantes",
       indexes = {
           @Index(name = "idx_estudiante_documento", columnList = "documento"),
           @Index(name = "idx_estudiante_email", columnList = "email"),
           @Index(name = "idx_estudiante_universidad", columnList = "universidad"),
           @Index(name = "idx_estudiante_activo", columnList = "activo")
       },
       uniqueConstraints = {
           @UniqueConstraint(name = "uk_estudiante_documento", columnNames = "documento"),
           @UniqueConstraint(name = "uk_estudiante_email", columnNames = "email")
       })
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"supervisor", "evaluaciones", "bitacoras"})
public class Estudiante {

    /**
     * Identificador único del estudiante.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idEstudiante;

    /**
     * Número de documento de identificación.
     */
    @NotBlank(message = "El documento es obligatorio")
    @Size(min = 6, max = 20, message = "El documento debe tener entre 6 y 20 caracteres")
    @Column(nullable = false, unique = true, length = 20)
    private String documento;

    /**
     * Tipo de documento (DNI, Cédula, Pasaporte, etc.).
     */
    @NotBlank(message = "El tipo de documento es obligatorio")
    @Size(max = 30, message = "El tipo de documento no puede exceder 30 caracteres")
    @Column(nullable = false, length = 30)
    private String tipoDocumento;

    /**
     * Nombres del estudiante.
     */
    @NotBlank(message = "Los nombres son obligatorios")
    @Size(min = 2, max = 100, message = "Los nombres deben tener entre 2 y 100 caracteres")
    @Column(nullable = false, length = 100)
    private String nombres;

    /**
     * Apellidos del estudiante.
     */
    @NotBlank(message = "Los apellidos son obligatorios")
    @Size(min = 2, max = 100, message = "Los apellidos deben tener entre 2 y 100 caracteres")
    @Column(nullable = false, length = 100)
    private String apellidos;

    /**
     * Fecha de nacimiento del estudiante.
     */
    @Past(message = "La fecha de nacimiento debe ser en el pasado")
    @NotNull(message = "La fecha de nacimiento es obligatoria")
    @Column(nullable = false)
    private LocalDate fechaNacimiento;

    /**
     * Correo electrónico del estudiante.
     */
    @NotBlank(message = "El email es obligatorio")
    @Email(message = "Formato de email inválido")
    @Size(max = 100, message = "El email no puede exceder 100 caracteres")
    @Column(nullable = false, unique = true, length = 100)
    private String email;

    /**
     * Teléfono de contacto.
     */
    @NotBlank(message = "El teléfono es obligatorio")
    @Pattern(regexp = "^[+]?[0-9]{7,15}$", message = "Formato de teléfono inválido")
    @Column(nullable = false, length = 15)
    private String telefono;

    /**
     * Universidad de procedencia.
     */
    @NotBlank(message = "La universidad es obligatoria")
    @Size(max = 150, message = "El nombre de la universidad no puede exceder 150 caracteres")
    @Column(nullable = false, length = 150)
    private String universidad;

    /**
     * Semestre actual que cursa.
     */
    @Min(value = 1, message = "El semestre debe ser al menos 1")
    @Max(value = 12, message = "El semestre no puede ser mayor a 12")
    @Column
    private Integer semestre;

    /**
     * Código estudiantil de la universidad.
     */
    @Size(max = 30, message = "El código estudiantil no puede exceder 30 caracteres")
    @Column(length = 30)
    private String codigoEstudiantil;

    /**
     * Fecha de inicio de la práctica.
     */
    @NotNull(message = "La fecha de inicio es obligatoria")
    @Column(nullable = false)
    private LocalDate fechaInicioPractica;

    /**
     * Fecha de fin de la práctica.
     */
    @NotNull(message = "La fecha de fin es obligatoria")
    @Column(nullable = false)
    private LocalDate fechaFinPractica;

    /**
     * Horas totales de práctica requeridas.
     */
    @Min(value = 1, message = "Las horas de práctica deben ser al menos 1")
    @Column
    private Integer horasPracticaRequeridas;

    /**
     * Horas completadas hasta el momento.
     */
    @Min(value = 0, message = "Las horas completadas no pueden ser negativas")
    @Column
    @Builder.Default
    private Integer horasPracticaCompletadas = 0;

    /**
     * Promedio general del estudiante (0-5).
     */
    @DecimalMin(value = "0.0", message = "El promedio no puede ser menor a 0")
    @DecimalMax(value = "5.0", message = "El promedio no puede ser mayor a 5")
    @Column
    private Double promedioGeneral;

    /**
     * Áreas de interés del estudiante.
     */
    @Size(max = 500, message = "Las áreas de interés no pueden exceder 500 caracteres")
    @Column(length = 500)
    private String areasInteres;

    /**
     * Observaciones generales sobre el estudiante.
     */
    @Size(max = 1000, message = "Las observaciones no pueden exceder 1000 caracteres")
    @Column(length = 1000)
    private String observaciones;

    /**
     * Indica si el estudiante está activo.
     */
    @Column(nullable = false)
    @Builder.Default
    private Boolean activo = true;

    /**
     * Supervisor asignado al estudiante.
     * Relación Many-to-One con SupervisorPractica.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_supervisor")
    private SupervisorPractica supervisor;

    /**
     * Evaluaciones del estudiante.
     * Relación One-to-Many con EvaluacionEstudiante.
     */
    @OneToMany(mappedBy = "estudiante", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<EvaluacionEstudiante> evaluaciones = new ArrayList<>();

    /**
     * Bitácoras del estudiante.
     * Relación One-to-Many con Bitacora.
     */
    @OneToMany(mappedBy = "estudiante", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Bitacora> bitacoras = new ArrayList<>();

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
     * Obtiene el nombre completo del estudiante.
     *
     * @return Nombres y apellidos
     */
    public String getNombreCompleto() {
        return nombres + " " + apellidos;
    }

    /**
     * Calcula el porcentaje de práctica completado.
     *
     * @return Porcentaje completado (0-100)
     */
    public Double getPorcentajePracticaCompletado() {
        if (horasPracticaRequeridas == null || horasPracticaRequeridas == 0) {
            return 0.0;
        }
        return (horasPracticaCompletadas * 100.0) / horasPracticaRequeridas;
    }

    /**
     * Verifica si ha completado las horas de práctica.
     *
     * @return true si completó las horas requeridas
     */
    public boolean haCompletadoPractica() {
        if (horasPracticaRequeridas == null || horasPracticaCompletadas == null) {
            return false;
        }
        return horasPracticaCompletadas >= horasPracticaRequeridas;
    }

    /**
     * Agrega horas de práctica completadas.
     *
     * @param horas Horas a agregar
     */
    public void agregarHorasPractica(int horas) {
        if (horas > 0) {
            this.horasPracticaCompletadas = (this.horasPracticaCompletadas == null ? 0 : this.horasPracticaCompletadas) + horas;
        }
    }

    /**
     * Agrega una evaluación al estudiante.
     *
     * @param evaluacion Evaluación a agregar
     */
    public void agregarEvaluacion(EvaluacionEstudiante evaluacion) {
        evaluaciones.add(evaluacion);
        evaluacion.setEstudiante(this);
    }

    /**
     * Elimina una evaluación del estudiante.
     *
     * @param evaluacion Evaluación a eliminar
     */
    public void eliminarEvaluacion(EvaluacionEstudiante evaluacion) {
        evaluaciones.remove(evaluacion);
        evaluacion.setEstudiante(null);
    }

    /**
     * Agrega una bitácora al estudiante.
     *
     * @param bitacora Bitácora a agregar
     */
    public void agregarBitacora(Bitacora bitacora) {
        bitacoras.add(bitacora);
        bitacora.setEstudiante(this);
    }

    /**
     * Elimina una bitácora del estudiante.
     *
     * @param bitacora Bitácora a eliminar
     */
    public void eliminarBitacora(Bitacora bitacora) {
        bitacoras.remove(bitacora);
        bitacora.setEstudiante(null);
    }

    /**
     * Calcula el promedio de evaluaciones.
     *
     * @return Promedio de evaluaciones o 0 si no hay evaluaciones
     */
    public Double getPromedioEvaluaciones() {
        if (evaluaciones == null || evaluaciones.isEmpty()) {
            return 0.0;
        }
        return evaluaciones.stream()
            .mapToDouble(EvaluacionEstudiante::getCalificacion)
            .average()
            .orElse(0.0);
    }

    /**
     * Obtiene el número de evaluaciones realizadas.
     *
     * @return Cantidad de evaluaciones
     */
    public int getCantidadEvaluaciones() {
        return evaluaciones != null ? evaluaciones.size() : 0;
    }

    /**
     * Obtiene el número de bitácoras registradas.
     *
     * @return Cantidad de bitácoras
     */
    public int getCantidadBitacoras() {
        return bitacoras != null ? bitacoras.size() : 0;
    }

    /**
     * Activa el estudiante.
     */
    public void activar() {
        this.activo = true;
    }

    /**
     * Desactiva el estudiante.
     */
    public void desactivar() {
        this.activo = false;
    }

    /**
     * Verifica si la práctica está vigente.
     *
     * @return true si la fecha actual está entre inicio y fin de práctica
     */
    public boolean estaPracticaVigente() {
        LocalDate hoy = LocalDate.now();
        return !hoy.isBefore(fechaInicioPractica) && !hoy.isAfter(fechaFinPractica);
    }

    // ===================================================================
    // EQUALS Y HASHCODE
    // ===================================================================

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Estudiante that)) return false;
        return idEstudiante != null && idEstudiante.equals(that.idEstudiante);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
