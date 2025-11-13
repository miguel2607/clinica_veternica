package com.veterinaria.clinica_veternica.domain.agenda;

import com.veterinaria.clinica_veternica.domain.usuario.Veterinario;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Entidad que representa el Horario de disponibilidad de un Veterinario.
 *
 * Define los días y horas en que un veterinario está disponible para atender citas.
 * Un veterinario puede tener múltiples horarios para diferentes días de la semana.
 *
 * Los horarios se utilizan para:
 * - Validar disponibilidad al agendar citas
 * - Mostrar calendario de veterinarios disponibles
 * - Planificar la capacidad de atención
 *
 * @author Clínica Veterinaria Team
 * @version 1.0
 * @since 2025-11-02
 */
@Entity
@Table(name = "horarios",
       indexes = {
           @Index(name = "idx_horario_veterinario", columnList = "id_veterinario"),
           @Index(name = "idx_horario_dia", columnList = "dia_semana"),
           @Index(name = "idx_horario_activo", columnList = "activo")
       },
       uniqueConstraints = {
           @UniqueConstraint(
               name = "uk_horario_veterinario_dia_hora",
               columnNames = {"id_veterinario", "dia_semana", "hora_inicio", "hora_fin"}
           )
       })
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = "veterinario")
public class Horario {

    /**
     * Identificador único del horario.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idHorario;

    /**
     * Veterinario al que pertenece este horario.
     * Relación Many-to-One con Veterinario.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_veterinario", nullable = false)
    @NotNull(message = "El veterinario es obligatorio")
    private Veterinario veterinario;

    /**
     * Día de la semana.
     * Valores: MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY.
     */
    @NotNull(message = "El día de la semana es obligatorio")
    @Enumerated(EnumType.STRING)
    @Column(name = "dia_semana", nullable = false, length = 20)
    private DayOfWeek diaSemana;

    /**
     * Hora de inicio de disponibilidad.
     */
    @NotNull(message = "La hora de inicio es obligatoria")
    @Column(nullable = false)
    private LocalTime horaInicio;

    /**
     * Hora de fin de disponibilidad.
     */
    @NotNull(message = "La hora de fin es obligatoria")
    @Column(nullable = false)
    private LocalTime horaFin;

    /**
     * Duración de cada cita en minutos.
     * Ejemplo: 30 minutos, 45 minutos, 1 hora (60).
     */
    @NotNull(message = "La duración de cita es obligatoria")
    @Min(value = 15, message = "La duración mínima es 15 minutos")
    @Max(value = 240, message = "La duración máxima es 240 minutos (4 horas)")
    @Column(nullable = false)
    @Builder.Default
    private Integer duracionCitaMinutos = 30;

    /**
     * Número máximo de citas simultáneas.
     * Normalmente 1, pero puede ser mayor en consultas grupales o urgencias.
     */
    @NotNull(message = "El máximo de citas simultáneas es obligatorio")
    @Min(value = 1, message = "Debe permitir al menos 1 cita")
    @Max(value = 10, message = "El máximo de citas simultáneas no puede exceder 10")
    @Column(nullable = false)
    @Builder.Default
    private Integer maxCitasSimultaneas = 1;

    /**
     * Indica si el horario está activo.
     * Horarios inactivos no se consideran para agendar nuevas citas.
     */
    @Column(nullable = false)
    @Builder.Default
    private Boolean activo = true;

    /**
     * Observaciones sobre el horario.
     * Ejemplo: "Solo emergencias", "Cirugías los miércoles", etc.
     */
    @Size(max = 500, message = "Las observaciones no pueden exceder 500 caracteres")
    @Column(length = 500)
    private String observaciones;

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
     * Valida que la hora de fin sea posterior a la hora de inicio.
     * Se ejecuta antes de persistir o actualizar.
     */
    @PrePersist
    @PreUpdate
    public void validarHoras() {
        if (horaInicio != null && horaFin != null) {
            if (!horaFin.isAfter(horaInicio)) {
                throw new IllegalArgumentException(
                    "La hora de fin debe ser posterior a la hora de inicio");
            }

            // Validar que la duración del horario permita al menos una cita
            long minutosDisponibles = ((long) horaFin.toSecondOfDay() - horaInicio.toSecondOfDay()) / 60;
            if (minutosDisponibles < duracionCitaMinutos) {
                throw new IllegalArgumentException(
                    "El horario debe tener al menos la duración de una cita");
            }
        }
    }

    /**
     * Calcula el número máximo de citas que se pueden agendar en este horario.
     *
     * @return Cantidad máxima de citas
     */
    public int getCapacidadMaximaCitas() {
        if (horaInicio == null || horaFin == null || duracionCitaMinutos == null) {
            return 0;
        }

        long minutosDisponibles = ((long) horaFin.toSecondOfDay() - horaInicio.toSecondOfDay()) / 60;
        int slotsDisponibles = (int) (minutosDisponibles / duracionCitaMinutos);

        return slotsDisponibles * maxCitasSimultaneas;
    }

    /**
     * Obtiene la duración total del horario en horas.
     *
     * @return Duración en horas
     */
    public double getDuracionHoras() {
        if (horaInicio == null || horaFin == null) {
            return 0.0;
        }

        long segundos = (long) horaFin.toSecondOfDay() - horaInicio.toSecondOfDay();
        return Math.round((segundos / 3600.0) * 100.0) / 100.0;
    }

    /**
     * Obtiene la duración total del horario en minutos.
     *
     * @return Duración en minutos
     */
    public long getDuracionMinutos() {
        if (horaInicio == null || horaFin == null) {
            return 0;
        }

        return ((long) horaFin.toSecondOfDay() - horaInicio.toSecondOfDay()) / 60;
    }

    /**
     * Verifica si un horario específico está dentro de este horario de disponibilidad.
     *
     * @param hora Hora a verificar
     * @return true si está dentro del horario
     */
    public boolean estaEnHorario(LocalTime hora) {
        if (hora == null || horaInicio == null || horaFin == null) {
            return false;
        }

        return !hora.isBefore(horaInicio) && hora.isBefore(horaFin);
    }

    /**
     * Verifica si hay traslape con otro horario.
     *
     * @param otroHorario Otro horario a comparar
     * @return true si hay traslape
     */
    public boolean seTraslapa(Horario otroHorario) {
        if (otroHorario == null || !this.diaSemana.equals(otroHorario.getDiaSemana())) {
            return false;
        }

        return !(this.horaFin.isBefore(otroHorario.getHoraInicio()) ||
                 this.horaInicio.isAfter(otroHorario.getHoraFin()) ||
                 this.horaFin.equals(otroHorario.getHoraInicio()) ||
                 this.horaInicio.equals(otroHorario.getHoraFin()));
    }

    /**
     * Activa el horario.
     */
    public void activar() {
        this.activo = true;
    }

    /**
     * Desactiva el horario.
     */
    public void desactivar() {
        this.activo = false;
    }

    /**
     * Obtiene el nombre del día en español.
     *
     * @return Nombre del día
     */
    public String getNombreDia() {
        return switch (diaSemana) {
            case MONDAY -> "Lunes";
            case TUESDAY -> "Martes";
            case WEDNESDAY -> "Miércoles";
            case THURSDAY -> "Jueves";
            case FRIDAY -> "Viernes";
            case SATURDAY -> "Sábado";
            case SUNDAY -> "Domingo";
        };
    }

    /**
     * Obtiene una descripción legible del horario.
     *
     * @return Descripción del horario
     */
    public String getDescripcion() {
        return String.format("%s de %s a %s (%d min por cita)",
            getNombreDia(),
            horaInicio != null ? horaInicio.toString() : "??:??",
            horaFin != null ? horaFin.toString() : "??:??",
            duracionCitaMinutos != null ? duracionCitaMinutos : 0);
    }

    // ===================================================================
    // EQUALS Y HASHCODE
    // ===================================================================

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Horario horario)) return false;
        return idHorario != null && idHorario.equals(horario.idHorario);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
