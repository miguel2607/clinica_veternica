package com.veterinaria.clinica_veternica.domain.clinico;

import com.veterinaria.clinica_veternica.domain.usuario.Veterinario;
import com.veterinaria.clinica_veternica.domain.inventario.Insumo;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entidad que representa un Tratamiento médico.
 *
 * Registra los tratamientos aplicados o prescritos a una mascota
 * como parte de su atención médica. Incluye medicamentos, terapias,
 * procedimientos y otros tipos de intervenciones terapéuticas.
 *
 * Características:
 * - Tratamientos asociados a una historia clínica
 * - Registro del veterinario responsable
 * - Fecha de inicio y fin del tratamiento
 * - Tipo de tratamiento (medicamento, terapia, procedimiento)
 * - Dosificación y frecuencia
 * - Estado del tratamiento (activo, completado, suspendido)
 *
 * Relaciones:
 * - Many-to-One con HistoriaClinica
 * - Many-to-One con Veterinario
 * - Many-to-One con Insumo (medicamento utilizado)
 *
 * @author Clínica Veterinaria Team
 * @version 1.0
 * @since 2025-11-02
 */
@Entity
@Table(name = "tratamientos",
       indexes = {
           @Index(name = "idx_tratamiento_historia", columnList = "id_historia_clinica"),
           @Index(name = "idx_tratamiento_veterinario", columnList = "id_veterinario"),
           @Index(name = "idx_tratamiento_insumo", columnList = "id_insumo"),
           @Index(name = "idx_tratamiento_activo", columnList = "activo"),
           @Index(name = "idx_tratamiento_fecha_inicio", columnList = "fecha_inicio"),
           @Index(name = "idx_tratamiento_tipo", columnList = "tipo_tratamiento")
       })
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"historiaClinica", "veterinario", "insumo"})
public class Tratamiento {

    /**
     * Constantes para estados del tratamiento.
     */
    private static final String ESTADO_ACTIVO = "ACTIVO";
    private static final String ESTADO_COMPLETADO = "COMPLETADO";
    private static final String ESTADO_SUSPENDIDO = "SUSPENDIDO";
    private static final String ESTADO_CANCELADO = "CANCELADO";
    
    /**
     * Constantes para tipos de tratamiento.
     */
    private static final String TIPO_MEDICAMENTO = "MEDICAMENTO";
    private static final String TIPO_TERAPIA = "TERAPIA";
    private static final String TIPO_PROCEDIMIENTO = "PROCEDIMIENTO";
    private static final String TIPO_CIRUGIA = "CIRUGIA";
    
    /**
     * Constantes para prioridades.
     */
    private static final String PRIORIDAD_URGENTE = "URGENTE";
    private static final String PRIORIDAD_ALTA = "ALTA";
    private static final String PRIORIDAD_NORMAL = "NORMAL";

    /**
     * Identificador único del tratamiento.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idTratamiento;

    /**
     * Historia clínica a la que pertenece este tratamiento.
     * Relación Many-to-One con HistoriaClinica.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_historia_clinica", nullable = false)
    @NotNull(message = "La historia clínica es obligatoria")
    private HistoriaClinica historiaClinica;

    /**
     * Veterinario que prescribió el tratamiento.
     * Relación Many-to-One con Veterinario.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_veterinario", nullable = false)
    @NotNull(message = "El veterinario es obligatorio")
    private Veterinario veterinario;

    /**
     * Insumo/medicamento utilizado (si aplica).
     * Relación Many-to-One con Insumo.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_insumo")
    private Insumo insumo;

    /**
     * Tipo de tratamiento.
     * Valores: MEDICAMENTO, TERAPIA, PROCEDIMIENTO, CIRUGIA, DIETA, OTRO
     */
    @NotBlank(message = "El tipo de tratamiento es obligatorio")
    @Size(max = 30, message = "El tipo no puede exceder 30 caracteres")
    @Column(nullable = false, length = 30)
    private String tipoTratamiento;

    /**
     * Nombre del tratamiento o medicamento.
     */
    @NotBlank(message = "El nombre del tratamiento es obligatorio")
    @Size(min = 3, max = 200, message = "El nombre debe tener entre 3 y 200 caracteres")
    @Column(nullable = false, length = 200)
    private String nombre;

    /**
     * Descripción detallada del tratamiento.
     */
    @NotBlank(message = "La descripción es obligatoria")
    @Size(min = 10, max = 1000, message = "La descripción debe tener entre 10 y 1000 caracteres")
    @Column(nullable = false, length = 1000)
    private String descripcion;

    /**
     * Dosificación (ej: "10 mg cada 12 horas").
     */
    @Size(max = 200, message = "La dosificación no puede exceder 200 caracteres")
    @Column(length = 200)
    private String dosificacion;

    /**
     * Frecuencia de administración (ej: "Cada 8 horas", "Diario", "Semanal").
     */
    @Size(max = 100, message = "La frecuencia no puede exceder 100 caracteres")
    @Column(length = 100)
    private String frecuencia;

    /**
     * Vía de administración.
     * Valores: ORAL, INYECTABLE, TOPICA, INTRAVENOSA, SUBCUTANEA, INTRAMUSCULAR, OTRA
     */
    @Size(max = 30, message = "La vía no puede exceder 30 caracteres")
    @Column(length = 30)
    private String viaAdministracion;

    /**
     * Duración del tratamiento en días.
     */
    @Min(value = 1, message = "La duración debe ser al menos 1 día")
    @Max(value = 365, message = "La duración no puede exceder 365 días")
    @Column
    private Integer duracionDias;

    /**
     * Fecha de inicio del tratamiento.
     */
    @NotNull(message = "La fecha de inicio es obligatoria")
    @Column(nullable = false)
    private LocalDate fechaInicio;

    /**
     * Fecha de fin del tratamiento (estimada o real).
     */
    @Column
    private LocalDate fechaFin;

    /**
     * Fecha real de finalización del tratamiento.
     */
    @Column
    private LocalDate fechaFinalizacion;

    /**
     * Cantidad total prescrita.
     */
    @DecimalMin(value = "0.0", message = "La cantidad debe ser positiva")
    @Column
    private Double cantidad;

    /**
     * Unidad de medida (ml, mg, comprimidos, etc.).
     */
    @Size(max = 20, message = "La unidad no puede exceder 20 caracteres")
    @Column(length = 20)
    private String unidad;

    /**
     * Indicaciones especiales para el tratamiento.
     */
    @Size(max = 500, message = "Las indicaciones no pueden exceder 500 caracteres")
    @Column(length = 500)
    private String indicaciones;

    /**
     * Precauciones o advertencias.
     */
    @Size(max = 500, message = "Las precauciones no pueden exceder 500 caracteres")
    @Column(length = 500)
    private String precauciones;

    /**
     * Efectos adversos observados o esperados.
     */
    @Size(max = 500, message = "Los efectos adversos no pueden exceder 500 caracteres")
    @Column(length = 500)
    private String efectosAdversos;

    /**
     * Motivo de la prescripción.
     */
    @Size(max = 500, message = "El motivo no puede exceder 500 caracteres")
    @Column(length = 500)
    private String motivo;

    /**
     * Objetivo terapéutico esperado.
     */
    @Size(max = 500, message = "El objetivo no puede exceder 500 caracteres")
    @Column(length = 500)
    private String objetivoTerapeutico;

    /**
     * Estado del tratamiento.
     * Valores: ACTIVO, COMPLETADO, SUSPENDIDO, CANCELADO
     */
    @NotBlank(message = "El estado es obligatorio")
    @Size(max = 20, message = "El estado no puede exceder 20 caracteres")
    @Column(nullable = false, length = 20)
    @Builder.Default
    private String estado = ESTADO_ACTIVO;

    /**
     * Motivo de suspensión o cancelación (si aplica).
     */
    @Size(max = 500, message = "El motivo de suspensión no puede exceder 500 caracteres")
    @Column(length = 500)
    private String motivoSuspension;

    /**
     * Prioridad del tratamiento.
     * Valores: BAJA, NORMAL, ALTA, URGENTE
     */
    @NotBlank(message = "La prioridad es obligatoria")
    @Size(max = 20, message = "La prioridad no puede exceder 20 caracteres")
    @Column(nullable = false, length = 20)
    @Builder.Default
    private String prioridad = PRIORIDAD_NORMAL;

    /**
     * Costo del tratamiento.
     */
    @DecimalMin(value = "0.0", message = "El costo debe ser positivo")
    @Column
    private Double costo;

    /**
     * Observaciones adicionales.
     */
    @Size(max = 1000, message = "Las observaciones no pueden exceder 1000 caracteres")
    @Column(length = 1000)
    private String observaciones;

    /**
     * Indica si el tratamiento está activo.
     */
    @Column(nullable = false)
    @Builder.Default
    private Boolean activo = true;

    /**
     * Indica si el tratamiento se debe continuar en casa.
     */
    @Column(nullable = false)
    @Builder.Default
    private Boolean continuarEnCasa = false;

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
     * Verifica si el tratamiento está activo.
     *
     * @return true si está activo
     */
    public boolean estaActivo() {
        return activo != null && activo && ESTADO_ACTIVO.equalsIgnoreCase(estado);
    }

    /**
     * Verifica si el tratamiento está completado.
     *
     * @return true si está completado
     */
    public boolean estaCompletado() {
        return ESTADO_COMPLETADO.equalsIgnoreCase(estado);
    }

    /**
     * Verifica si el tratamiento está suspendido.
     *
     * @return true si está suspendido
     */
    public boolean estaSuspendido() {
        return ESTADO_SUSPENDIDO.equalsIgnoreCase(estado);
    }

    /**
     * Verifica si el tratamiento está cancelado.
     *
     * @return true si está cancelado
     */
    public boolean estaCancelado() {
        return ESTADO_CANCELADO.equalsIgnoreCase(estado);
    }

    /**
     * Verifica si es un tratamiento con medicamento.
     *
     * @return true si es medicamento
     */
    public boolean esMedicamento() {
        return TIPO_MEDICAMENTO.equalsIgnoreCase(tipoTratamiento);
    }

    /**
     * Verifica si es una terapia.
     *
     * @return true si es terapia
     */
    public boolean esTerapia() {
        return TIPO_TERAPIA.equalsIgnoreCase(tipoTratamiento);
    }

    /**
     * Verifica si es un procedimiento.
     *
     * @return true si es procedimiento
     */
    public boolean esProcedimiento() {
        return TIPO_PROCEDIMIENTO.equalsIgnoreCase(tipoTratamiento);
    }

    /**
     * Verifica si es una cirugía.
     *
     * @return true si es cirugía
     */
    public boolean esCirugia() {
        return TIPO_CIRUGIA.equalsIgnoreCase(tipoTratamiento);
    }

    /**
     * Verifica si es urgente.
     *
     * @return true si la prioridad es URGENTE
     */
    public boolean esUrgente() {
        return PRIORIDAD_URGENTE.equalsIgnoreCase(prioridad);
    }

    /**
     * Verifica si es de alta prioridad.
     *
     * @return true si es alta o urgente
     */
    public boolean esAltaPrioridad() {
        return PRIORIDAD_ALTA.equalsIgnoreCase(prioridad) || esUrgente();
    }

    /**
     * Verifica si el tratamiento ha finalizado.
     *
     * @return true si la fecha de fin ha pasado
     */
    public boolean haFinalizado() {
        if (fechaFin == null) {
            return false;
        }
        return LocalDate.now().isAfter(fechaFin);
    }

    /**
     * Calcula los días transcurridos desde el inicio.
     *
     * @return Días transcurridos
     */
    public long getDiasTranscurridos() {
        if (fechaInicio == null) {
            return 0;
        }
        return java.time.temporal.ChronoUnit.DAYS.between(fechaInicio, LocalDate.now());
    }

    /**
     * Calcula los días restantes hasta el fin.
     *
     * @return Días restantes, o null si no hay fecha fin
     */
    public Long getDiasRestantes() {
        if (fechaFin == null) {
            return null;
        }
        long dias = java.time.temporal.ChronoUnit.DAYS.between(LocalDate.now(), fechaFin);
        return dias < 0 ? 0 : dias;
    }

    /**
     * Calcula el porcentaje de avance del tratamiento.
     *
     * @return Porcentaje (0-100), o null si no se puede calcular
     */
    public Double getPorcentajeAvance() {
        if (fechaInicio == null || fechaFin == null) {
            return null;
        }

        long duracionTotal = java.time.temporal.ChronoUnit.DAYS.between(fechaInicio, fechaFin);
        if (duracionTotal <= 0) {
            return 100.0;
        }

        long diasTranscurridos = getDiasTranscurridos();
        double porcentaje = (diasTranscurridos * 100.0) / duracionTotal;

        return Math.clamp(porcentaje, 0.0, 100.0);
    }

    /**
     * Verifica si tiene efectos adversos registrados.
     *
     * @return true si tiene efectos adversos
     */
    public boolean tieneEfectosAdversos() {
        return efectosAdversos != null && !efectosAdversos.isBlank();
    }

    /**
     * Verifica si tiene insumo asociado.
     *
     * @return true si tiene insumo
     */
    public boolean tieneInsumo() {
        return insumo != null;
    }

    /**
     * Marca el tratamiento como completado.
     */
    public void completar() {
        this.estado = ESTADO_COMPLETADO;
        this.fechaFinalizacion = LocalDate.now();
        this.activo = false;
    }

    /**
     * Suspende el tratamiento.
     *
     * @param motivo Motivo de la suspensión
     */
    public void suspender(String motivo) {
        this.estado = ESTADO_SUSPENDIDO;
        this.motivoSuspension = motivo;
        this.activo = false;
    }

    /**
     * Cancela el tratamiento.
     *
     * @param motivo Motivo de la cancelación
     */
    public void cancelar(String motivo) {
        this.estado = ESTADO_CANCELADO;
        this.motivoSuspension = motivo;
        this.activo = false;
    }

    /**
     * Reactiva el tratamiento.
     */
    public void reactivar() {
        this.estado = ESTADO_ACTIVO;
        this.activo = true;
        this.motivoSuspension = null;
    }

    /**
     * Extiende el tratamiento.
     *
     * @param diasExtension Días adicionales
     */
    public void extender(int diasExtension) {
        if (fechaFin != null) {
            this.fechaFin = this.fechaFin.plusDays(diasExtension);
        }
        if (duracionDias != null) {
            this.duracionDias += diasExtension;
        }
    }

    /**
     * Calcula la fecha de fin estimada basada en la duración.
     */
    public void calcularFechaFin() {
        if (fechaInicio != null && duracionDias != null && duracionDias > 0) {
            this.fechaFin = fechaInicio.plusDays(duracionDias);
        }
    }

    /**
     * Obtiene un resumen del tratamiento.
     *
     * @return Resumen
     */
    public String getResumen() {
        return String.format("%s - %s (%s) - Estado: %s",
            nombre,
            tipoTratamiento,
            veterinario != null ? veterinario.getNombreCompleto() : "Sin veterinario",
            estado);
    }

    /**
     * Verifica si el tratamiento está próximo a finalizar (dentro de 3 días).
     *
     * @return true si está próximo a finalizar
     */
    public boolean proximoAFinalizar() {
        Long diasRestantes = getDiasRestantes();
        return diasRestantes != null && diasRestantes <= 3 && diasRestantes > 0;
    }

    // ===================================================================
    // EQUALS Y HASHCODE
    // ===================================================================

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Tratamiento that)) return false;
        return idTratamiento != null && idTratamiento.equals(that.idTratamiento);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
