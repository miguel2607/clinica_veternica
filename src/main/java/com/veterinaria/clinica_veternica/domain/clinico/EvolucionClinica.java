package com.veterinaria.clinica_veternica.domain.clinico;

import com.veterinaria.clinica_veternica.domain.usuario.Veterinario;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * Entidad que representa una Evolución Clínica.
 *
 * Registra el progreso y cambios en el estado de salud de una mascota
 * durante el tratamiento o seguimiento médico. Cada evolución documenta
 * observaciones, cambios en síntomas, respuesta a tratamientos, etc.
 *
 * Características:
 * - Evoluciones cronológicas asociadas a una historia clínica
 * - Registro del veterinario que realizó la evaluación
 * - Signos vitales actualizados
 * - Observaciones y diagnóstico actualizado
 * - Estado del paciente
 *
 * Relaciones:
 * - Many-to-One con HistoriaClinica
 * - Many-to-One con Veterinario
 *
 * @author Clínica Veterinaria Team
 * @version 1.0
 * @since 2025-11-02
 */
@Entity
@Table(name = "evoluciones_clinicas",
       indexes = {
           @Index(name = "idx_evolucion_historia", columnList = "id_historia_clinica"),
           @Index(name = "idx_evolucion_veterinario", columnList = "id_veterinario"),
           @Index(name = "idx_evolucion_fecha", columnList = "fecha_evolucion"),
           @Index(name = "idx_evolucion_tipo", columnList = "tipo_evolucion")
       })
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"historiaClinica", "veterinario"})
public class EvolucionClinica {

    /**
     * Constantes para estados del paciente.
     */
    private static final String ESTADO_ESTABLE = "ESTABLE";
    private static final String ESTADO_MEJORANDO = "MEJORANDO";
    private static final String ESTADO_EMPEORANDO = "EMPEORANDO";
    private static final String ESTADO_CRITICO = "CRITICO";
    
    /**
     * Constantes para tipos de evolución.
     */
    private static final String TIPO_EMERGENCIA = "EMERGENCIA";
    private static final String TIPO_SEGUIMIENTO = "SEGUIMIENTO";

    /**
     * Identificador único de la evolución clínica.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idEvolucion;

    /**
     * Historia clínica a la que pertenece esta evolución.
     * Relación Many-to-One con HistoriaClinica.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_historia_clinica", nullable = false)
    @NotNull(message = "La historia clínica es obligatoria")
    private HistoriaClinica historiaClinica;

    /**
     * Veterinario que registró esta evolución.
     * Relación Many-to-One con Veterinario.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_veterinario", nullable = false)
    @NotNull(message = "El veterinario es obligatorio")
    private Veterinario veterinario;

    /**
     * Fecha y hora de la evolución.
     */
    @NotNull(message = "La fecha de evolución es obligatoria")
    @Column(nullable = false)
    private LocalDateTime fechaEvolucion;

    /**
     * Tipo de evolución.
     * Valores: SEGUIMIENTO, CONTROL, EMERGENCIA, ALTA, INTERCONSULTA
     */
    @NotBlank(message = "El tipo de evolución es obligatorio")
    @Size(max = 30, message = "El tipo no puede exceder 30 caracteres")
    @Column(nullable = false, length = 30)
    private String tipoEvolucion;

    /**
     * Descripción detallada de la evolución.
     * Observaciones del veterinario sobre el estado actual.
     */
    @NotBlank(message = "La descripción es obligatoria")
    @Size(min = 20, max = 2000, message = "La descripción debe tener entre 20 y 2000 caracteres")
    @Column(nullable = false, length = 2000)
    private String descripcion;

    /**
     * Motivo de la consulta o razón por la que se realiza la evolución.
     */
    @Size(max = 2000, message = "El motivo de consulta no puede exceder 2000 caracteres")
    @Column(length = 2000)
    private String motivoConsulta;

    /**
     * Hallazgos del examen físico o clínico realizado.
     */
    @Size(max = 3000, message = "Los hallazgos del examen no pueden exceder 3000 caracteres")
    @Column(length = 3000)
    private String hallazgosExamen;

    /**
     * Diagnóstico actualizado o impresión clínica.
     */
    @Size(max = 1000, message = "El diagnóstico no puede exceder 1000 caracteres")
    @Column(length = 1000)
    private String diagnostico;

    /**
     * Estado general del paciente.
     * Valores: ESTABLE, MEJORANDO, ESTACIONARIO, EMPEORANDO, CRITICO
     */
    @NotBlank(message = "El estado del paciente es obligatorio")
    @Size(max = 20, message = "El estado no puede exceder 20 caracteres")
    @Column(nullable = false, length = 20)
    private String estadoPaciente;

    /**
     * Temperatura corporal en grados Celsius.
     */
    @DecimalMin(value = "35.0", message = "La temperatura debe ser al menos 35°C")
    @DecimalMax(value = "45.0", message = "La temperatura no puede exceder 45°C")
    @Column
    private Double temperatura;

    /**
     * Frecuencia cardíaca en latidos por minuto.
     */
    @Min(value = 20, message = "La frecuencia cardíaca debe ser al menos 20 lpm")
    @Max(value = 300, message = "La frecuencia cardíaca no puede exceder 300 lpm")
    @Column
    private Integer frecuenciaCardiaca;

    /**
     * Frecuencia respiratoria en respiraciones por minuto.
     */
    @Min(value = 5, message = "La frecuencia respiratoria debe ser al menos 5 rpm")
    @Max(value = 100, message = "La frecuencia respiratoria no puede exceder 100 rpm")
    @Column
    private Integer frecuenciaRespiratoria;

    /**
     * Peso actual de la mascota en kilogramos.
     */
    @DecimalMin(value = "0.1", message = "El peso debe ser al menos 0.1 kg")
    @DecimalMax(value = "500.0", message = "El peso no puede exceder 500 kg")
    @Column
    private Double peso;

    /**
     * Nivel de dolor evaluado (escala 0-10).
     * 0 = Sin dolor, 10 = Dolor severo
     */
    @Min(value = 0, message = "El nivel de dolor debe ser entre 0 y 10")
    @Max(value = 10, message = "El nivel de dolor debe ser entre 0 y 10")
    @Column
    private Integer nivelDolor;

    /**
     * Condición corporal (escala 1-9).
     * 1 = Emaciado, 5 = Ideal, 9 = Obeso
     */
    @Min(value = 1, message = "La condición corporal debe ser entre 1 y 9")
    @Max(value = 9, message = "La condición corporal debe ser entre 1 y 9")
    @Column
    private Integer condicionCorporal;

    /**
     * Observaciones adicionales.
     */
    @Size(max = 1000, message = "Las observaciones no pueden exceder 1000 caracteres")
    @Column(length = 1000)
    private String observaciones;

    /**
     * Plan de tratamiento o próximos pasos.
     */
    @Size(max = 1000, message = "El plan no puede exceder 1000 caracteres")
    @Column(length = 1000)
    private String plan;

    /**
     * Indicaciones para el propietario.
     */
    @Size(max = 500, message = "Las indicaciones no pueden exceder 500 caracteres")
    @Column(length = 500)
    private String indicacionesPropietario;

    /**
     * Próxima cita de control programada.
     */
    @Column
    private LocalDateTime proximaRevision;

    /**
     * Indica si esta evolución representa el alta médica.
     */
    @Column(nullable = false)
    @Builder.Default
    private Boolean esAlta = false;

    /**
     * Motivo del alta (si aplica).
     */
    @Size(max = 500, message = "El motivo del alta no puede exceder 500 caracteres")
    @Column(length = 500)
    private String motivoAlta;

    /**
     * Archivos adjuntos (imágenes, estudios, etc.).
     * Nombres de archivos separados por coma.
     */
    @Size(max = 500, message = "Los adjuntos no pueden exceder 500 caracteres")
    @Column(length = 500)
    private String archivosAdjuntos;

    /**
     * Indica si esta evolución está activa.
     */
    @Column(nullable = false)
    @Builder.Default
    private Boolean activo = true;

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
     * Verifica si el paciente está estable.
     *
     * @return true si el estado es ESTABLE
     */
    public boolean estaEstable() {
        return ESTADO_ESTABLE.equalsIgnoreCase(estadoPaciente);
    }

    /**
     * Verifica si el paciente está mejorando.
     *
     * @return true si el estado es MEJORANDO
     */
    public boolean estaMejorando() {
        return ESTADO_MEJORANDO.equalsIgnoreCase(estadoPaciente);
    }

    /**
     * Verifica si el paciente está empeorando.
     *
     * @return true si el estado es EMPEORANDO
     */
    public boolean estaEmpeorando() {
        return ESTADO_EMPEORANDO.equalsIgnoreCase(estadoPaciente);
    }

    /**
     * Verifica si el paciente está en estado crítico.
     *
     * @return true si el estado es CRITICO
     */
    public boolean estaCritico() {
        return ESTADO_CRITICO.equalsIgnoreCase(estadoPaciente);
    }

    /**
     * Verifica si es una evolución de emergencia.
     *
     * @return true si el tipo es EMERGENCIA
     */
    public boolean esEmergencia() {
        return TIPO_EMERGENCIA.equalsIgnoreCase(tipoEvolucion);
    }

    /**
     * Verifica si es una evolución de seguimiento.
     *
     * @return true si el tipo es SEGUIMIENTO
     */
    public boolean esSeguimiento() {
        return TIPO_SEGUIMIENTO.equalsIgnoreCase(tipoEvolucion);
    }

    /**
     * Verifica si tiene signos vitales registrados.
     *
     * @return true si tiene al menos un signo vital
     */
    public boolean tieneSignosVitales() {
        return temperatura != null ||
               frecuenciaCardiaca != null ||
               frecuenciaRespiratoria != null;
    }

    /**
     * Verifica si los signos vitales están completos.
     *
     * @return true si tiene todos los signos vitales
     */
    public boolean tieneSignosVitalesCompletos() {
        return temperatura != null &&
               frecuenciaCardiaca != null &&
               frecuenciaRespiratoria != null;
    }

    /**
     * Verifica si la temperatura está en rango normal para perros/gatos.
     * Rango normal: 37.5°C - 39.2°C
     *
     * @return true si está en rango normal
     */
    public boolean temperaturaEnRangoNormal() {
        if (temperatura == null) {
            return false;
        }
        return temperatura >= 37.5 && temperatura <= 39.2;
    }

    /**
     * Verifica si tiene dolor significativo (nivel >= 5).
     *
     * @return true si tiene dolor significativo
     */
    public boolean tieneDolorSignificativo() {
        return nivelDolor != null && nivelDolor >= 5;
    }

    /**
     * Verifica si tiene sobrepeso (condición corporal >= 7).
     *
     * @return true si tiene sobrepeso
     */
    public boolean tieneSobrepeso() {
        return condicionCorporal != null && condicionCorporal >= 7;
    }

    /**
     * Verifica si está bajo de peso (condición corporal <= 3).
     *
     * @return true si está bajo de peso
     */
    public boolean estaBajoDePeso() {
        return condicionCorporal != null && condicionCorporal <= 3;
    }

    /**
     * Verifica si requiere próxima revisión.
     *
     * @return true si tiene próxima revisión programada
     */
    public boolean requiereProximaRevision() {
        return proximaRevision != null;
    }

    /**
     * Verifica si la próxima revisión está próxima (dentro de 7 días).
     *
     * @return true si está próxima
     */
    public boolean proximaRevisionProxima() {
        if (proximaRevision == null) {
            return false;
        }
        LocalDateTime limiteProximo = LocalDateTime.now().plusDays(7);
        return proximaRevision.isBefore(limiteProximo);
    }

    /**
     * Verifica si tiene archivos adjuntos.
     *
     * @return true si tiene adjuntos
     */
    public boolean tieneArchivosAdjuntos() {
        return archivosAdjuntos != null && !archivosAdjuntos.isBlank();
    }

    /**
     * Obtiene el número de archivos adjuntos.
     *
     * @return Número de archivos
     */
    public int getNumeroArchivosAdjuntos() {
        if (!tieneArchivosAdjuntos()) {
            return 0;
        }
        return archivosAdjuntos.split(",").length;
    }

    /**
     * Marca esta evolución como alta médica.
     *
     * @param motivo Motivo del alta
     */
    public void marcarComoAlta(String motivo) {
        this.esAlta = true;
        this.motivoAlta = motivo;
        this.estadoPaciente = "ESTABLE";
    }

    /**
     * Programa próxima revisión.
     *
     * @param diasHasta Días hasta la próxima revisión
     */
    public void programarProximaRevision(int diasHasta) {
        this.proximaRevision = LocalDateTime.now().plusDays(diasHasta);
    }

    /**
     * Desactiva esta evolución.
     */
    public void desactivar() {
        this.activo = false;
    }

    /**
     * Activa esta evolución.
     */
    public void activar() {
        this.activo = true;
    }

    /**
     * Obtiene un resumen de la evolución.
     *
     * @return Resumen
     */
    public String getResumen() {
        return String.format("%s - %s - %s (Paciente: %s)",
            fechaEvolucion.toLocalDate(),
            tipoEvolucion,
            veterinario != null ? veterinario.getNombreCompleto() : "Sin veterinario",
            estadoPaciente);
    }

    /**
     * Verifica si esta evolución es reciente (últimas 24 horas).
     *
     * @return true si es reciente
     */
    public boolean esReciente() {
        if (fechaEvolucion == null) {
            return false;
        }
        LocalDateTime hace24Horas = LocalDateTime.now().minusHours(24);
        return fechaEvolucion.isAfter(hace24Horas);
    }

    // ===================================================================
    // EQUALS Y HASHCODE
    // ===================================================================

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EvolucionClinica that)) return false;
        return idEvolucion != null && idEvolucion.equals(that.idEvolucion);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
