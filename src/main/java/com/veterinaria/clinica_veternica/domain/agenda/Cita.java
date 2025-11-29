package com.veterinaria.clinica_veternica.domain.agenda;

import com.veterinaria.clinica_veternica.domain.paciente.Mascota;
import com.veterinaria.clinica_veternica.domain.usuario.Veterinario;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Entidad que representa una Cita veterinaria.
 *
 * Una cita es el agendamiento de un servicio veterinario para una mascota específica,
 * atendida por un veterinario en una fecha y hora determinadas.
 *
 * La cita es el núcleo del sistema de gestión de la clínica y se relaciona con:
 * - Mascota (paciente)
 * - Veterinario (profesional que atiende)
 * - Servicio (tipo de atención)
 * - Historia clínica (registro de atención)
 *
 * Estados posibles: PROGRAMADA, CONFIRMADA, ATENDIDA, CANCELADA, NO_ASISTIO
 *
 * @author Clínica Veterinaria Team
 * @version 1.0
 * @since 2025-11-02
 */
@Entity
@Table(name = "citas",
       indexes = {
           @Index(name = "idx_cita_mascota", columnList = "id_mascota"),
           @Index(name = "idx_cita_veterinario", columnList = "id_veterinario"),
           @Index(name = "idx_cita_servicio", columnList = "id_servicio"),
           @Index(name = "idx_cita_fecha", columnList = "fecha_cita"),
           @Index(name = "idx_cita_estado", columnList = "estado"),
           @Index(name = "idx_cita_fecha_hora", columnList = "fecha_cita, hora_cita")
       })
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"mascota", "veterinario", "servicio"})
public class Cita {

    /**
     * Identificador único de la cita.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idCita;

    /**
     * Mascota que será atendida.
     * Relación Many-to-One con Mascota.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_mascota", nullable = false)
    @NotNull(message = "La mascota es obligatoria")
    private Mascota mascota;

    /**
     * Veterinario que atenderá la cita.
     * Relación Many-to-One con Veterinario.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_veterinario", nullable = false)
    @NotNull(message = "El veterinario es obligatorio")
    private Veterinario veterinario;

    /**
     * Servicio a realizar en la cita.
     * Relación Many-to-One con Servicio.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_servicio", nullable = false)
    @NotNull(message = "El servicio es obligatorio")
    private Servicio servicio;

    /**
     * Fecha de la cita.
     * La validación @FutureOrPresent solo aplica para citas nuevas o en estados pendientes.
     * Para citas atendidas, se permite fechas pasadas ya que es normal que una cita atendida tenga fecha pasada.
     */
    @NotNull(message = "La fecha de la cita es obligatoria")
    @Column(nullable = false)
    private LocalDate fechaCita;

    /**
     * Hora de la cita.
     */
    @NotNull(message = "La hora de la cita es obligatoria")
    @Column(nullable = false)
    private LocalTime horaCita;

    /**
     * Duración estimada de la cita en minutos.
     * Puede diferir de la duración estándar del servicio.
     */
    @Min(value = 5, message = "La duración mínima es 5 minutos")
    @Column(nullable = false)
    private Integer duracionEstimadaMinutos;

    /**
     * Estado actual de la cita.
     */
    @NotNull(message = "El estado es obligatorio")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private EstadoCita estado = EstadoCita.PROGRAMADA;

    /**
     * Motivo de la consulta descrito por el propietario.
     */
    @NotBlank(message = "El motivo de consulta es obligatorio")
    @Size(min = 10, max = 1000, message = "El motivo debe tener entre 10 y 1000 caracteres")
    @Column(nullable = false, length = 1000)
    private String motivoConsulta;

    /**
     * Observaciones adicionales sobre la cita.
     */
    @Size(max = 1000, message = "Las observaciones no pueden exceder 1000 caracteres")
    @Column(length = 1000)
    private String observaciones;

    /**
     * Indica si es una cita de emergencia.
     */
    @Column(nullable = false)
    @Builder.Default
    private Boolean esEmergencia = false;

    /**
     * Indica si es una cita a domicilio.
     */
    @Column(nullable = false)
    @Builder.Default
    private Boolean esDomicilio = false;

    /**
     * Dirección para cita a domicilio (si aplica).
     */
    @Size(max = 300, message = "La dirección no puede exceder 300 caracteres")
    @Column(length = 300)
    private String direccionDomicilio;

    /**
     * Precio acordado para la cita.
     * Puede incluir descuentos, urgencias, domicilio, etc.
     * Se calcula desde el servicio base + ajustes.
     */
    @NotNull(message = "El precio es obligatorio")
    @DecimalMin(value = "0.0", message = "El precio no puede ser negativo")
    @Column(nullable = false)
    private BigDecimal precioFinal;

    /**
     * Fecha y hora de confirmación de la cita.
     */
    @Column
    private LocalDateTime fechaConfirmacion;

    /**
     * Fecha y hora de inicio real de atención.
     */
    @Column
    private LocalDateTime fechaHoraInicioAtencion;

    /**
     * Fecha y hora de fin real de atención.
     */
    @Column
    private LocalDateTime fechaHoraFinAtencion;

    /**
     * Fecha y hora de cancelación (si aplica).
     */
    @Column
    private LocalDateTime fechaCancelacion;

    /**
     * Motivo de cancelación (si aplica).
     */
    @Size(max = 500, message = "El motivo de cancelación no puede exceder 500 caracteres")
    @Column(length = 500)
    private String motivoCancelacion;

    /**
     * Usuario que canceló la cita.
     */
    @Size(max = 100, message = "El campo no puede exceder 100 caracteres")
    @Column(length = 100)
    private String canceladaPor;

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
     * Obtiene la hora de fin estimada (calculada).
     */
    public LocalTime getHoraFinEstimada() {
        if (horaCita != null && duracionEstimadaMinutos != null) {
            return horaCita.plusMinutes(duracionEstimadaMinutos);
        }
        return null;
    }

    /**
     * Obtiene la duración real en minutos (calculada).
     */
    public Integer getDuracionRealMinutos() {
        if (fechaHoraInicioAtencion != null && fechaHoraFinAtencion != null) {
            long minutos = java.time.Duration.between(
                fechaHoraInicioAtencion, fechaHoraFinAtencion).toMinutes();
            return (int) minutos;
        }
        return null;
    }

    /**
     * Confirma la cita.
     */
    public void confirmar() {
        if (this.estado != EstadoCita.PROGRAMADA) {
            throw new IllegalStateException("Solo se pueden confirmar citas programadas");
        }
        this.estado = EstadoCita.CONFIRMADA;
        this.fechaConfirmacion = LocalDateTime.now();
    }

    /**
     * Marca la cita como atendida y registra tiempos de atención.
     */
    public void marcarComoAtendida() {
        if (this.estado != EstadoCita.CONFIRMADA && this.estado != EstadoCita.PROGRAMADA && this.estado != EstadoCita.EN_ATENCION) {
            throw new IllegalStateException("Solo se pueden atender citas confirmadas, programadas o en atención. Estado actual: " + this.estado);
        }

        this.estado = EstadoCita.ATENDIDA;

        if (this.fechaHoraInicioAtencion == null) {
            this.fechaHoraInicioAtencion = LocalDateTime.now();
        }

        this.fechaHoraFinAtencion = LocalDateTime.now();
    }

    /**
     * Registra el inicio de la atención.
     * Cambia el estado a EN_ATENCION y registra la fecha/hora de inicio.
     */
    public void iniciarAtencion() {
        if (this.estado != EstadoCita.CONFIRMADA && this.estado != EstadoCita.PROGRAMADA) {
            throw new IllegalStateException("Solo se pueden iniciar citas confirmadas o programadas. Estado actual: " + this.estado);
        }
        this.estado = EstadoCita.EN_ATENCION;
        this.fechaHoraInicioAtencion = LocalDateTime.now();
    }

    /**
     * Registra el fin de la atención.
     */
    public void finalizarAtencion() {
        this.fechaHoraFinAtencion = LocalDateTime.now();
        marcarComoAtendida();
    }

    /**
     * Cancela la cita.
     *
     * @param motivo Motivo de la cancelación
     * @param usuario Usuario que cancela
     */
    public void cancelar(String motivo, String usuario) {
        if (this.estado == EstadoCita.ATENDIDA) {
            throw new IllegalStateException("No se puede cancelar una cita ya atendida");
        }
        if (this.estado == EstadoCita.CANCELADA) {
            throw new IllegalStateException("La cita ya está cancelada");
        }

        this.estado = EstadoCita.CANCELADA;
        this.fechaCancelacion = LocalDateTime.now();
        this.motivoCancelacion = motivo;
        this.canceladaPor = usuario;
    }

    /**
     * Marca la cita como no asistida.
     */
    public void marcarComoNoAsistio() {
        if (this.estado != EstadoCita.CONFIRMADA && this.estado != EstadoCita.PROGRAMADA) {
            throw new IllegalStateException("Solo se puede marcar como no asistida citas confirmadas o programadas");
        }
        this.estado = EstadoCita.NO_ASISTIO;
    }


    /**
     * Verifica si la cita está pendiente (programada o confirmada).
     *
     * @return true si está pendiente
     */
    public boolean estaPendiente() {
        return estado == EstadoCita.PROGRAMADA || estado == EstadoCita.CONFIRMADA;
    }

    /**
     * Verifica si la cita puede ser cancelada.
     *
     * @return true si puede cancelarse
     */
    public boolean puedeCancelarse() {
        return estado != EstadoCita.ATENDIDA && estado != EstadoCita.CANCELADA;
    }

    /**
     * Verifica si la cita puede ser reprogramada.
     *
     * @return true si puede reprogramarse
     */
    public boolean puedeReprogramarse() {
        return estado == EstadoCita.PROGRAMADA || estado == EstadoCita.CONFIRMADA;
    }

    /**
     * Verifica si la cita es para hoy.
     *
     * @return true si es hoy
     */
    public boolean esHoy() {
        return fechaCita != null && fechaCita.equals(LocalDate.now());
    }

    /**
     * Verifica si la cita ya pasó.
     *
     * @return true si ya pasó
     */
    public boolean yaPaso() {
        if (fechaCita == null) return false;

        LocalDate hoy = LocalDate.now();
        if (fechaCita.isBefore(hoy)) {
            return true;
        }

        if (fechaCita.equals(hoy) && horaCita != null) {
            return horaCita.isBefore(LocalTime.now());
        }

        return false;
    }

    /**
     * Obtiene el nombre del propietario desde la mascota.
     *
     * @return Nombre del propietario
     */
    public String getNombrePropietario() {
        return mascota != null && mascota.getPropietario() != null
            ? mascota.getPropietario().getNombreCompleto()
            : "Sin propietario";
    }

    /**
     * Obtiene el teléfono del propietario desde la mascota.
     *
     * @return Teléfono del propietario
     */
    public String getTelefonoPropietario() {
        return mascota != null && mascota.getPropietario() != null
            ? mascota.getPropietario().getTelefono()
            : null;
    }

    /**
     * Obtiene el email del propietario desde la mascota.
     *
     * @return Email del propietario
     */
    public String getEmailPropietario() {
        return mascota != null && mascota.getPropietario() != null
            ? mascota.getPropietario().getEmail()
            : null;
    }

    // ===================================================================
    // EQUALS Y HASHCODE
    // ===================================================================

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Cita cita)) return false;
        return idCita != null && idCita.equals(cita.idCita);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
