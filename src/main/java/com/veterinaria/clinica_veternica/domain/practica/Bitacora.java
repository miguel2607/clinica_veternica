package com.veterinaria.clinica_veternica.domain.practica;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Entidad que representa una Bitácora de actividades del Estudiante.
 *
 * Registro diario de actividades realizadas por el estudiante durante su práctica.
 * Permite llevar un seguimiento detallado del trabajo realizado, las horas invertidas
 * y las competencias desarrolladas.
 *
 * La bitácora es fundamental para:
 * - Registrar horas de práctica
 * - Documentar aprendizajes
 * - Evidenciar competencias desarrolladas
 * - Evaluación continua del desempeño
 *
 * @author Clínica Veterinaria Team
 * @version 1.0
 * @since 2025-11-02
 */
@Entity
@Table(name = "bitacoras",
       indexes = {
           @Index(name = "idx_bitacora_estudiante", columnList = "id_estudiante"),
           @Index(name = "idx_bitacora_fecha", columnList = "fecha")
       })
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = "estudiante")
public class Bitacora {

    /**
     * Identificador único de la bitácora.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idBitacora;

    /**
     * Estudiante que registra la bitácora.
     * Relación Many-to-One con Estudiante.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_estudiante", nullable = false)
    @NotNull(message = "El estudiante es obligatorio")
    private Estudiante estudiante;

    /**
     * Fecha de la actividad registrada.
     */
    @NotNull(message = "La fecha es obligatoria")
    @PastOrPresent(message = "La fecha no puede ser futura")
    @Column(nullable = false)
    private LocalDate fecha;

    /**
     * Hora de inicio de la actividad.
     */
    @NotNull(message = "La hora de inicio es obligatoria")
    @Column(nullable = false)
    private LocalTime horaInicio;

    /**
     * Hora de fin de la actividad.
     */
    @NotNull(message = "La hora de fin es obligatoria")
    @Column(nullable = false)
    private LocalTime horaFin;

    /**
     * Duración total en horas (calculada automáticamente).
     */
    @Column(nullable = false)
    private Double horasDuracion;

    /**
     * Tipo de actividad realizada.
     * Ejemplos: Consulta General, Cirugía, Hospitalización, Laboratorio, etc.
     */
    @NotBlank(message = "El tipo de actividad es obligatorio")
    @Size(max = 100, message = "El tipo de actividad no puede exceder 100 caracteres")
    @Column(nullable = false, length = 100)
    private String tipoActividad;

    /**
     * Descripción detallada de las actividades realizadas.
     */
    @NotBlank(message = "La descripción es obligatoria")
    @Size(min = 20, max = 2000, message = "La descripción debe tener entre 20 y 2000 caracteres")
    @Column(nullable = false, length = 2000)
    private String descripcionActividades;

    /**
     * Procedimientos observados o realizados.
     */
    @Size(max = 1000, message = "Los procedimientos no pueden exceder 1000 caracteres")
    @Column(length = 1000)
    private String procedimientosRealizados;

    /**
     * Casos clínicos atendidos (especies y patologías).
     */
    @Size(max = 1000, message = "Los casos clínicos no pueden exceder 1000 caracteres")
    @Column(length = 1000)
    private String casosClinicos;

    /**
     * Aprendizajes obtenidos durante la actividad.
     */
    @Size(max = 1000, message = "Los aprendizajes no pueden exceder 1000 caracteres")
    @Column(length = 1000)
    private String aprendizajes;

    /**
     * Dificultades encontradas.
     */
    @Size(max = 1000, message = "Las dificultades no pueden exceder 1000 caracteres")
    @Column(length = 1000)
    private String dificultades;

    /**
     * Competencias desarrolladas o reforzadas.
     */
    @Size(max = 500, message = "Las competencias no pueden exceder 500 caracteres")
    @Column(length = 500)
    private String competenciasDesarrolladas;

    /**
     * Observaciones adicionales.
     */
    @Size(max = 1000, message = "Las observaciones no pueden exceder 1000 caracteres")
    @Column(length = 1000)
    private String observaciones;

    /**
     * Indica si la bitácora fue revisada por el supervisor.
     */
    @Column(nullable = false)
    @Builder.Default
    private Boolean revisada = false;

    /**
     * Fecha de revisión por el supervisor.
     */
    @Column
    private LocalDate fechaRevision;

    /**
     * Comentarios del supervisor sobre la bitácora.
     */
    @Size(max = 1000, message = "Los comentarios no pueden exceder 1000 caracteres")
    @Column(length = 1000)
    private String comentariosSupervisor;

    /**
     * Calificación del supervisor sobre la actividad registrada (0-5).
     */
    @DecimalMin(value = "0.0", message = "La calificación no puede ser menor a 0")
    @DecimalMax(value = "5.0", message = "La calificación no puede ser mayor a 5")
    @Column
    private Double calificacion;

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
     * Calcula la duración en horas entre hora de inicio y fin.
     * Se ejecuta antes de persistir o actualizar.
     */
    @PrePersist
    @PreUpdate
    public void calcularDuracion() {
        if (horaInicio != null && horaFin != null) {
            // Calcular diferencia en minutos y convertir a horas
            long minutosInicio = horaInicio.toSecondOfDay() / 60;
            long minutosFin = horaFin.toSecondOfDay() / 60;
            long diferenciaMinutos = minutosFin - minutosInicio;

            // Si horaFin es menor que horaInicio, asumimos que cruza medianoche
            if (diferenciaMinutos < 0) {
                diferenciaMinutos += 24 * 60; // Agregar 24 horas en minutos
            }

            this.horasDuracion = Math.round((diferenciaMinutos / 60.0) * 100.0) / 100.0;
        }
    }

    /**
     * Valida que la hora de fin sea posterior a la hora de inicio.
     *
     * @throws IllegalArgumentException si la validación falla
     */
    public void validarHoras() {
        if (horaInicio != null && horaFin != null) {
            long minutosInicio = horaInicio.toSecondOfDay() / 60;
            long minutosFin = horaFin.toSecondOfDay() / 60;

            if (minutosFin <= minutosInicio && minutosFin != minutosInicio) {
                // Permitir cruce de medianoche, pero validar que tenga sentido
                long diferenciaMinutos = (24 * 60) + minutosFin - minutosInicio;
                if (diferenciaMinutos > 12 * 60) { // Más de 12 horas
                    throw new IllegalArgumentException(
                        "La duración de la actividad no puede exceder 12 horas");
                }
            }
        }
    }

    /**
     * Marca la bitácora como revisada por el supervisor.
     *
     * @param comentarios Comentarios del supervisor
     * @param calificacion Calificación otorgada
     */
    public void revisar(String comentarios, Double calificacion) {
        this.revisada = true;
        this.fechaRevision = LocalDate.now();
        this.comentariosSupervisor = comentarios;
        this.calificacion = calificacion;
    }

    /**
     * Verifica si la bitácora está completa (todos los campos obligatorios).
     *
     * @return true si está completa
     */
    public boolean estaCompleta() {
        return fecha != null && horaInicio != null && horaFin != null &&
               tipoActividad != null && !tipoActividad.isBlank() &&
               descripcionActividades != null && !descripcionActividades.isBlank() &&
               descripcionActividades.length() >= 20;
    }

    /**
     * Verifica si la actividad fue de medio turno (menos de 4 horas).
     *
     * @return true si es medio turno
     */
    public boolean esMedioTurno() {
        return horasDuracion != null && horasDuracion < 4.0;
    }

    /**
     * Verifica si la actividad fue de turno completo (4-8 horas).
     *
     * @return true si es turno completo
     */
    public boolean esTurnoCompleto() {
        return horasDuracion != null && horasDuracion >= 4.0 && horasDuracion <= 8.0;
    }

    /**
     * Verifica si la actividad fue de turno extendido (más de 8 horas).
     *
     * @return true si es turno extendido
     */
    public boolean esTurnoExtendido() {
        return horasDuracion != null && horasDuracion > 8.0;
    }

    /**
     * Obtiene un resumen corto de la bitácora.
     *
     * @return Resumen de la bitácora
     */
    public String getResumen() {
        return String.format("%s - %s (%s horas) - %s",
            fecha.toString(),
            tipoActividad,
            horasDuracion != null ? horasDuracion.toString() : "0",
            revisada ? "Revisada" : "Pendiente revisión");
    }

    // ===================================================================
    // EQUALS Y HASHCODE
    // ===================================================================

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Bitacora bitacora)) return false;
        return idBitacora != null && idBitacora.equals(bitacora.idBitacora);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
