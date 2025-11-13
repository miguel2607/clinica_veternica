package com.veterinaria.clinica_veternica.domain.clinico;

import com.veterinaria.clinica_veternica.domain.paciente.Mascota;
import com.veterinaria.clinica_veternica.domain.usuario.Veterinario;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entidad que representa una Receta Médica.
 *
 * Prescripción formal de medicamentos y tratamientos emitida por un veterinario.
 * Contiene la información necesaria para que el propietario pueda adquirir
 * y administrar correctamente los medicamentos prescritos.
 *
 * Características:
 * - Receta asociada a una historia clínica y mascota
 * - Registro del veterinario que emitió la receta
 * - Lista de medicamentos prescritos
 * - Dosificación e indicaciones detalladas
 * - Fecha de emisión y vigencia
 * - Estado de la receta (activa, vencida, cancelada)
 *
 * Relaciones:
 * - Many-to-One con HistoriaClinica
 * - Many-to-One con Mascota
 * - Many-to-One con Veterinario
 *
 * @author Clínica Veterinaria Team
 * @version 1.0
 * @since 2025-11-02
 */
@Entity
@Table(name = "recetas_medicas",
       indexes = {
           @Index(name = "idx_receta_historia", columnList = "id_historia_clinica"),
           @Index(name = "idx_receta_mascota", columnList = "id_mascota"),
           @Index(name = "idx_receta_veterinario", columnList = "id_veterinario"),
           @Index(name = "idx_receta_fecha_emision", columnList = "fecha_emision"),
           @Index(name = "idx_receta_vigente", columnList = "vigente"),
           @Index(name = "idx_receta_numero", columnList = "numero_receta")
       })
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"historiaClinica", "mascota", "veterinario"})
public class RecetaMedica {

    /**
     * Constantes para tipos de receta.
     */
    private static final String TIPO_CONTROLADA = "CONTROLADA";
    private static final String TIPO_ESTUPEFACIENTE = "ESTUPEFACIENTE";
    
    /**
     * Constantes para prioridades.
     */
    private static final String PRIORIDAD_URGENTE = "URGENTE";
    private static final String PRIORIDAD_ALTA = "ALTA";

    /**
     * Identificador único de la receta médica.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idReceta;

    /**
     * Número único de la receta (formato: RX-YYYYMMDD-XXXX).
     */
    @Size(max = 50, message = "El número de receta no puede exceder 50 caracteres")
    @Column(unique = true, length = 50)
    private String numeroReceta;

    /**
     * Historia clínica a la que pertenece esta receta.
     * Relación Many-to-One con HistoriaClinica.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_historia_clinica", nullable = false)
    @NotNull(message = "La historia clínica es obligatoria")
    private HistoriaClinica historiaClinica;

    /**
     * Mascota para la cual se emite la receta.
     * Relación Many-to-One con Mascota.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_mascota", nullable = false)
    @NotNull(message = "La mascota es obligatoria")
    private Mascota mascota;

    /**
     * Veterinario que emitió la receta.
     * Relación Many-to-One con Veterinario.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_veterinario", nullable = false)
    @NotNull(message = "El veterinario es obligatorio")
    private Veterinario veterinario;

    /**
     * Fecha de emisión de la receta.
     */
    @NotNull(message = "La fecha de emisión es obligatoria")
    @Column(nullable = false)
    private LocalDate fechaEmision;

    /**
     * Fecha de vigencia/vencimiento de la receta.
     * Generalmente 30 días desde la emisión.
     */
    @Column
    private LocalDate fechaVigencia;

    /**
     * Diagnóstico o motivo de la receta.
     */
    @NotBlank(message = "El diagnóstico es obligatorio")
    @Size(min = 10, max = 500, message = "El diagnóstico debe tener entre 10 y 500 caracteres")
    @Column(nullable = false, length = 500)
    private String diagnostico;

    /**
     * Medicamentos prescritos.
     * Lista de medicamentos con dosificación (formato JSON o texto estructurado).
     */
    @NotBlank(message = "Los medicamentos son obligatorios")
    @Size(min = 10, max = 3000, message = "Los medicamentos deben tener entre 10 y 3000 caracteres")
    @Column(nullable = false, length = 3000)
    private String medicamentos;

    /**
     * Indicaciones generales de administración.
     */
    @NotBlank(message = "Las indicaciones son obligatorias")
    @Size(min = 10, max = 2000, message = "Las indicaciones deben tener entre 10 y 2000 caracteres")
    @Column(nullable = false, length = 2000)
    private String indicaciones;

    /**
     * Recomendaciones adicionales para el propietario.
     */
    @Size(max = 1000, message = "Las recomendaciones no pueden exceder 1000 caracteres")
    @Column(length = 1000)
    private String recomendaciones;

    /**
     * Advertencias o precauciones.
     */
    @Size(max = 1000, message = "Las advertencias no pueden exceder 1000 caracteres")
    @Column(length = 1000)
    private String advertencias;

    /**
     * Efectos secundarios a monitorear.
     */
    @Size(max = 1000, message = "Los efectos secundarios no pueden exceder 1000 caracteres")
    @Column(length = 1000)
    private String efectosSecundarios;

    /**
     * Duración estimada del tratamiento en días.
     */
    @Min(value = 1, message = "La duración debe ser al menos 1 día")
    @Max(value = 365, message = "La duración no puede exceder 365 días")
    @Column
    private Integer duracionDias;

    /**
     * Tipo de receta.
     * Valores: SIMPLE, CONTROLADA, ESTUPEFACIENTE, MAGISTRAL
     */
    @NotBlank(message = "El tipo de receta es obligatorio")
    @Size(max = 30, message = "El tipo no puede exceder 30 caracteres")
    @Column(nullable = false, length = 30)
    @Builder.Default
    private String tipoReceta = "SIMPLE";

    /**
     * Número de repeticiones permitidas de la receta.
     */
    @Min(value = 0, message = "Las repeticiones no pueden ser negativas")
    @Max(value = 10, message = "Las repeticiones no pueden exceder 10")
    @Column(nullable = false)
    @Builder.Default
    private Integer repeticionesPermitidas = 0;

    /**
     * Número de veces que se ha dispensado la receta.
     */
    @Min(value = 0, message = "Las dispensaciones no pueden ser negativas")
    @Column(nullable = false)
    @Builder.Default
    private Integer vecesDispensada = 0;

    /**
     * Indica si la receta está vigente.
     */
    @Column(nullable = false)
    @Builder.Default
    private Boolean vigente = true;

    /**
     * Indica si la receta fue dispensada/surtida.
     */
    @Column(nullable = false)
    @Builder.Default
    private Boolean dispensada = false;

    /**
     * Fecha de primera dispensación.
     */
    @Column
    private LocalDate fechaPrimeraDispensacion;

    /**
     * Fecha de última dispensación.
     */
    @Column
    private LocalDate fechaUltimaDispensacion;

    /**
     * Farmacia donde se dispensó (si aplica).
     */
    @Size(max = 200, message = "La farmacia no puede exceder 200 caracteres")
    @Column(length = 200)
    private String farmacia;

    /**
     * Costo total estimado de los medicamentos.
     */
    @DecimalMin(value = "0.0", message = "El costo debe ser positivo")
    @Column
    private Double costoEstimado;

    /**
     * Observaciones del veterinario.
     */
    @Size(max = 1000, message = "Las observaciones no pueden exceder 1000 caracteres")
    @Column(length = 1000)
    private String observaciones;

    /**
     * Notas internas (no visibles en la impresión).
     */
    @Size(max = 500, message = "Las notas internas no pueden exceder 500 caracteres")
    @Column(length = 500)
    private String notasInternas;

    /**
     * Motivo de cancelación (si fue cancelada).
     */
    @Size(max = 500, message = "El motivo de cancelación no puede exceder 500 caracteres")
    @Column(length = 500)
    private String motivoCancelacion;

    /**
     * Fecha de cancelación.
     */
    @Column
    private LocalDateTime fechaCancelacion;

    /**
     * Prioridad de la receta.
     * Valores: BAJA, NORMAL, ALTA, URGENTE
     */
    @NotBlank(message = "La prioridad es obligatoria")
    @Size(max = 20, message = "La prioridad no puede exceder 20 caracteres")
    @Column(nullable = false, length = 20)
    @Builder.Default
    private String prioridad = "NORMAL";

    /**
     * Indica si requiere seguimiento médico.
     */
    @Column(nullable = false)
    @Builder.Default
    private Boolean requiereSeguimiento = false;

    /**
     * Fecha programada de seguimiento.
     */
    @Column
    private LocalDate fechaSeguimiento;

    /**
     * Firma digital o código de verificación del veterinario.
     */
    @Size(max = 200, message = "La firma digital no puede exceder 200 caracteres")
    @Column(length = 200)
    private String firmaDigital;

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
     * Verifica si la receta está vigente y no ha vencido.
     *
     * @return true si está vigente
     */
    public boolean estaVigente() {
        if (vigente == null || !vigente) {
            return false;
        }
        if (fechaVigencia == null) {
            return true;
        }
        return !LocalDate.now().isAfter(fechaVigencia);
    }

    /**
     * Verifica si la receta ha vencido.
     *
     * @return true si ha vencido
     */
    public boolean haVencido() {
        if (fechaVigencia == null) {
            return false;
        }
        return LocalDate.now().isAfter(fechaVigencia);
    }

    /**
     * Verifica si la receta está próxima a vencer (dentro de 7 días).
     *
     * @return true si está próxima a vencer
     */
    public boolean proximaAVencer() {
        if (fechaVigencia == null || haVencido()) {
            return false;
        }
        LocalDate limiteProximo = LocalDate.now().plusDays(7);
        return fechaVigencia.isBefore(limiteProximo);
    }

    /**
     * Verifica si la receta fue dispensada.
     *
     * @return true si fue dispensada
     */
    public boolean fueDispensada() {
        return dispensada != null && dispensada;
    }

    /**
     * Verifica si puede ser dispensada nuevamente.
     *
     * @return true si tiene repeticiones disponibles
     */
    public boolean puedeDispensarseNuevamente() {
        return estaVigente() &&
               repeticionesPermitidas != null &&
               vecesDispensada != null &&
               vecesDispensada < repeticionesPermitidas;
    }

    /**
     * Obtiene el número de dispensaciones restantes.
     *
     * @return Dispensaciones restantes
     */
    public int getDispensacionesRestantes() {
        if (repeticionesPermitidas == null || vecesDispensada == null) {
            return 0;
        }
        int restantes = repeticionesPermitidas - vecesDispensada;
        return Math.max(0, restantes);
    }

    /**
     * Verifica si es una receta controlada.
     *
     * @return true si es controlada o estupefaciente
     */
    public boolean esControlada() {
        return TIPO_CONTROLADA.equalsIgnoreCase(tipoReceta) ||
               TIPO_ESTUPEFACIENTE.equalsIgnoreCase(tipoReceta);
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
     * Registra una dispensación de la receta.
     *
     * @param farmacia Nombre de la farmacia
     */
    public void registrarDispensacion(String farmacia) {
        if (vecesDispensada == null) {
            vecesDispensada = 0;
        }

        if (vecesDispensada == 0) {
            this.fechaPrimeraDispensacion = LocalDate.now();
            this.dispensada = true;
        }

        this.vecesDispensada++;
        this.fechaUltimaDispensacion = LocalDate.now();
        this.farmacia = farmacia;

        // Si se agotaron las repeticiones, marcar como no vigente
        if (vecesDispensada >= repeticionesPermitidas) {
            this.vigente = false;
        }
    }

    /**
     * Cancela la receta.
     *
     * @param motivo Motivo de la cancelación
     */
    public void cancelar(String motivo) {
        this.vigente = false;
        this.motivoCancelacion = motivo;
        this.fechaCancelacion = LocalDateTime.now();
    }

    /**
     * Calcula la fecha de vigencia basada en la emisión.
     * Por defecto: 30 días para recetas simples, 15 días para controladas.
     */
    public void calcularFechaVigencia() {
        if (fechaEmision != null && fechaVigencia == null) {
            int diasVigencia = esControlada() 
                    ? com.veterinaria.clinica_veternica.util.Constants.DIAS_VIGENCIA_RECETA_CONTROLADA
                    : com.veterinaria.clinica_veternica.util.Constants.DIAS_VIGENCIA_RECETA_SIMPLE;
            this.fechaVigencia = fechaEmision.plusDays(diasVigencia);
        }
    }

    /**
     * Genera el número de receta único.
     *
     * @param secuencial Número secuencial del día
     */
    public void generarNumeroReceta(int secuencial) {
        if (numeroReceta == null || numeroReceta.isBlank()) {
            String fecha = LocalDate.now().toString().replace("-", "");
            this.numeroReceta = String.format("RX-%s-%04d", fecha, secuencial);
        }
    }

    /**
     * Programa fecha de seguimiento.
     *
     * @param diasHasta Días hasta el seguimiento
     */
    public void programarSeguimiento(int diasHasta) {
        this.requiereSeguimiento = true;
        this.fechaSeguimiento = LocalDate.now().plusDays(diasHasta);
    }

    /**
     * Calcula los días de vigencia restantes.
     *
     * @return Días restantes, o null si no hay fecha de vigencia
     */
    public Long getDiasVigenciaRestantes() {
        if (fechaVigencia == null) {
            return null;
        }
        if (haVencido()) {
            return 0L;
        }
        return java.time.temporal.ChronoUnit.DAYS.between(LocalDate.now(), fechaVigencia);
    }

    /**
     * Obtiene un resumen de la receta.
     *
     * @return Resumen
     */
    public String getResumen() {
        return String.format("Receta %s - %s - %s (Vigente: %s)",
            numeroReceta != null ? numeroReceta : "Sin número",
            mascota != null ? mascota.getNombre() : "Sin mascota",
            veterinario != null ? veterinario.getNombreCompleto() : "Sin veterinario",
            estaVigente() ? "Sí" : "No");
    }

    /**
     * Verifica si la receta es reciente (últimos 7 días).
     *
     * @return true si es reciente
     */
    public boolean esReciente() {
        if (fechaEmision == null) {
            return false;
        }
        LocalDate hace7Dias = LocalDate.now().minusDays(7);
        return fechaEmision.isAfter(hace7Dias);
    }

    // ===================================================================
    // LIFECYCLE CALLBACKS
    // ===================================================================

    /**
     * Se ejecuta antes de persistir la entidad.
     * Calcula fecha de vigencia si no está establecida.
     */
    @PrePersist
    public void prePersist() {
        calcularFechaVigencia();
    }

    // ===================================================================
    // EQUALS Y HASHCODE
    // ===================================================================

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RecetaMedica that)) return false;
        return idReceta != null && idReceta.equals(that.idReceta);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
