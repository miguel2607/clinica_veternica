package com.veterinaria.clinica_veternica.domain.clinico;

import com.veterinaria.clinica_veternica.domain.paciente.Mascota;
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
 * Entidad que representa una Vacunación.
 *
 * Registra las vacunas aplicadas a las mascotas como parte de su
 * plan de prevención y salud. Mantiene el historial completo de inmunización
 * y gestiona las fechas de refuerzo o dosis siguientes.
 *
 * Características:
 * - Registro de vacunas aplicadas por mascota
 * - Historial de inmunización completo
 * - Gestión de esquemas de vacunación
 * - Control de fechas de próximas dosis
 * - Verificación de certificados de vacunación
 *
 * Relaciones:
 * - Many-to-One con HistoriaClinica
 * - Many-to-One con Mascota
 * - Many-to-One con Veterinario
 * - Many-to-One con Insumo (vacuna utilizada)
 *
 * @author Clínica Veterinaria Team
 * @version 1.0
 * @since 2025-11-02
 */
@Entity
@Table(name = "vacunaciones",
       indexes = {
           @Index(name = "idx_vacunacion_historia", columnList = "id_historia_clinica"),
           @Index(name = "idx_vacunacion_mascota", columnList = "id_mascota"),
           @Index(name = "idx_vacunacion_veterinario", columnList = "id_veterinario"),
           @Index(name = "idx_vacunacion_insumo", columnList = "id_insumo"),
           @Index(name = "idx_vacunacion_fecha", columnList = "fecha_aplicacion"),
           @Index(name = "idx_vacunacion_proxima", columnList = "fecha_proxima_dosis"),
           @Index(name = "idx_vacunacion_nombre", columnList = "nombre_vacuna")
       })
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"historiaClinica", "mascota", "veterinario", "insumo"})
public class Vacunacion {

    /**
     * Constantes para tipos de reacción.
     */
    private static final String REACCION_SEVERA = "SEVERA";
    
    /**
     * Constantes para tipos de vacuna.
     */
    private static final String TIPO_ANTIRRABICA = "ANTIRRABICA";

    /**
     * Identificador único de la vacunación.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idVacunacion;

    /**
     * Historia clínica a la que pertenece esta vacunación.
     * Relación Many-to-One con HistoriaClinica.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_historia_clinica", nullable = false)
    @NotNull(message = "La historia clínica es obligatoria")
    private HistoriaClinica historiaClinica;

    /**
     * Mascota que recibió la vacuna.
     * Relación Many-to-One con Mascota.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_mascota", nullable = false)
    @NotNull(message = "La mascota es obligatoria")
    private Mascota mascota;

    /**
     * Veterinario que aplicó la vacuna.
     * Relación Many-to-One con Veterinario.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_veterinario", nullable = false)
    @NotNull(message = "El veterinario es obligatorio")
    private Veterinario veterinario;

    /**
     * Insumo/vacuna utilizada (del inventario).
     * Relación Many-to-One con Insumo.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_insumo")
    private Insumo insumo;

    /**
     * Nombre de la vacuna.
     */
    @NotBlank(message = "El nombre de la vacuna es obligatorio")
    @Size(min = 3, max = 150, message = "El nombre debe tener entre 3 y 150 caracteres")
    @Column(nullable = false, length = 150)
    private String nombreVacuna;

    /**
     * Tipo o categoría de la vacuna.
     * Valores: VIRAL, BACTERIANA, POLIVALENTE, ANTIRRABICA, OTRA
     */
    @NotBlank(message = "El tipo de vacuna es obligatorio")
    @Size(max = 30, message = "El tipo no puede exceder 30 caracteres")
    @Column(nullable = false, length = 30)
    private String tipoVacuna;

    /**
     * Enfermedad(es) que previene.
     */
    @NotBlank(message = "Las enfermedades prevenidas son obligatorias")
    @Size(min = 5, max = 500, message = "Las enfermedades deben tener entre 5 y 500 caracteres")
    @Column(nullable = false, length = 500)
    private String enfermedadesPrevenidas;

    /**
     * Laboratorio fabricante de la vacuna.
     */
    @Size(max = 100, message = "El laboratorio no puede exceder 100 caracteres")
    @Column(length = 100)
    private String laboratorio;

    /**
     * Número de lote de la vacuna.
     */
    @NotBlank(message = "El lote es obligatorio")
    @Size(min = 3, max = 50, message = "El lote debe tener entre 3 y 50 caracteres")
    @Column(nullable = false, length = 50)
    private String lote;

    /**
     * Número de serie de la vacuna (si aplica).
     */
    @Size(max = 50, message = "El número de serie no puede exceder 50 caracteres")
    @Column(length = 50)
    private String numeroSerie;

    /**
     * Fecha de fabricación de la vacuna.
     */
    @Column
    private LocalDate fechaFabricacion;

    /**
     * Fecha de vencimiento de la vacuna.
     */
    @Column
    private LocalDate fechaVencimiento;

    /**
     * Fecha de aplicación de la vacuna.
     */
    @NotNull(message = "La fecha de aplicación es obligatoria")
    @Column(nullable = false)
    private LocalDate fechaAplicacion;

    /**
     * Dosis aplicada (en ml o unidades).
     */
    @DecimalMin(value = "0.1", message = "La dosis debe ser al menos 0.1")
    @DecimalMax(value = "100.0", message = "La dosis no puede exceder 100")
    @Column
    private Double dosis;

    /**
     * Unidad de medida de la dosis.
     * Valores: ML, CC, UI (Unidades Internacionales)
     */
    @Size(max = 10, message = "La unidad no puede exceder 10 caracteres")
    @Column(length = 10)
    private String unidadDosis;

    /**
     * Vía de administración.
     * Valores: SUBCUTANEA, INTRAMUSCULAR, INTRANASAL, ORAL
     */
    @NotBlank(message = "La vía de administración es obligatoria")
    @Size(max = 30, message = "La vía no puede exceder 30 caracteres")
    @Column(nullable = false, length = 30)
    private String viaAdministracion;

    /**
     * Sitio anatómico de aplicación.
     */
    @Size(max = 100, message = "El sitio de aplicación no puede exceder 100 caracteres")
    @Column(length = 100)
    private String sitioAplicacion;

    /**
     * Número de dosis en el esquema de vacunación.
     * Ej: 1ra dosis, 2da dosis, refuerzo
     */
    @Min(value = 1, message = "El número de dosis debe ser al menos 1")
    @Max(value = 10, message = "El número de dosis no puede exceder 10")
    @Column
    private Integer numeroDosis;

    /**
     * Total de dosis en el esquema completo.
     */
    @Min(value = 1, message = "El total de dosis debe ser al menos 1")
    @Max(value = 10, message = "El total de dosis no puede exceder 10")
    @Column
    private Integer totalDosisEsquema;

    /**
     * Fecha programada para la próxima dosis.
     */
    @Column
    private LocalDate fechaProximaDosis;

    /**
     * Indica si el esquema de vacunación está completo.
     */
    @Column(nullable = false)
    @Builder.Default
    private Boolean esquemaCompleto = false;

    /**
     * Peso de la mascota al momento de la vacunación (en kg).
     */
    @DecimalMin(value = "0.1", message = "El peso debe ser al menos 0.1 kg")
    @DecimalMax(value = "500.0", message = "El peso no puede exceder 500 kg")
    @Column
    private Double pesoMascota;

    /**
     * Edad de la mascota en el momento de vacunación (en meses).
     */
    @Min(value = 0, message = "La edad no puede ser negativa")
    @Max(value = 300, message = "La edad no puede exceder 300 meses")
    @Column
    private Integer edadMeses;

    /**
     * Reacción adversa observada (si hubo).
     */
    @Size(max = 1000, message = "La reacción adversa no puede exceder 1000 caracteres")
    @Column(length = 1000)
    private String reaccionAdversa;

    /**
     * Indica si hubo reacción adversa.
     */
    @Column(nullable = false)
    @Builder.Default
    private Boolean huboReaccion = false;

    /**
     * Tipo de reacción adversa.
     * Valores: LEVE, MODERADA, SEVERA
     */
    @Size(max = 20, message = "El tipo de reacción no puede exceder 20 caracteres")
    @Column(length = 20)
    private String tipoReaccion;

    /**
     * Tratamiento aplicado por reacción adversa.
     */
    @Size(max = 500, message = "El tratamiento no puede exceder 500 caracteres")
    @Column(length = 500)
    private String tratamientoReaccion;

    /**
     * Observaciones adicionales.
     */
    @Size(max = 1000, message = "Las observaciones no pueden exceder 1000 caracteres")
    @Column(length = 1000)
    private String observaciones;

    /**
     * Número de certificado de vacunación emitido.
     */
    @Size(max = 50, message = "El número de certificado no puede exceder 50 caracteres")
    @Column(unique = true, length = 50)
    private String numeroCertificado;

    /**
     * Fecha de emisión del certificado.
     */
    @Column
    private LocalDate fechaCertificado;

    /**
     * Indica si esta vacunación es obligatoria por ley.
     */
    @Column(nullable = false)
    @Builder.Default
    private Boolean obligatoria = false;

    /**
     * Indica si la vacunación está vigente.
     */
    @Column(nullable = false)
    @Builder.Default
    private Boolean vigente = true;

    /**
     * Costo de la vacunación.
     */
    @DecimalMin(value = "0.0", message = "El costo debe ser positivo")
    @Column
    private Double costo;

    /**
     * Duración de la inmunidad en meses.
     * Ej: 12 meses para vacunas anuales
     */
    @Min(value = 1, message = "La duración debe ser al menos 1 mes")
    @Max(value = 60, message = "La duración no puede exceder 60 meses")
    @Column
    private Integer duracionInmunidadMeses;

    /**
     * Fecha estimada de fin de inmunidad.
     */
    @Column
    private LocalDate fechaFinInmunidad;

    /**
     * Temperatura de almacenamiento verificada (en °C).
     */
    @DecimalMin(value = "-20.0", message = "La temperatura debe ser al menos -20°C")
    @DecimalMax(value = "25.0", message = "La temperatura no puede exceder 25°C")
    @Column
    private Double temperaturaAlmacenamiento;

    /**
     * Indica si la vacuna requiere cadena de frío.
     */
    @Column(nullable = false)
    @Builder.Default
    private Boolean requiereCadenaFrio = true;

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
     * Verifica si la vacunación está vigente.
     *
     * @return true si está vigente
     */
    public boolean estaVigente() {
        if (vigente == null || !vigente) {
            return false;
        }
        if (fechaFinInmunidad == null) {
            return true;
        }
        return !LocalDate.now().isAfter(fechaFinInmunidad);
    }

    /**
     * Verifica si la inmunidad ha vencido.
     *
     * @return true si ha vencido
     */
    public boolean inmunidadVencida() {
        if (fechaFinInmunidad == null) {
            return false;
        }
        return LocalDate.now().isAfter(fechaFinInmunidad);
    }

    /**
     * Verifica si la vacuna está vencida (lote).
     *
     * @return true si está vencida
     */
    public boolean loteVencido() {
        if (fechaVencimiento == null) {
            return false;
        }
        return LocalDate.now().isAfter(fechaVencimiento);
    }

    /**
     * Verifica si hubo reacción adversa.
     *
     * @return true si hubo reacción
     */
    public boolean tuvoReaccion() {
        return huboReaccion != null && huboReaccion;
    }

    /**
     * Verifica si la reacción fue severa.
     *
     * @return true si fue severa
     */
    public boolean reaccionSevera() {
        return tuvoReaccion() && REACCION_SEVERA.equalsIgnoreCase(tipoReaccion);
    }

    /**
     * Verifica si el esquema está completo.
     *
     * @return true si está completo
     */
    public boolean esEsquemaCompleto() {
        return esquemaCompleto != null && esquemaCompleto;
    }

    /**
     * Verifica si necesita próxima dosis.
     *
     * @return true si necesita próxima dosis
     */
    public boolean necesitaProximaDosis() {
        return !esEsquemaCompleto() && fechaProximaDosis != null;
    }

    /**
     * Verifica si la próxima dosis está próxima (dentro de 30 días).
     *
     * @return true si está próxima
     */
    public boolean proximaDosisProxima() {
        if (fechaProximaDosis == null) {
            return false;
        }
        LocalDate limite = LocalDate.now().plusDays(30);
        return fechaProximaDosis.isBefore(limite) && !fechaProximaDosis.isBefore(LocalDate.now());
    }

    /**
     * Verifica si la próxima dosis está atrasada.
     *
     * @return true si está atrasada
     */
    public boolean proximaDosisAtrasada() {
        if (fechaProximaDosis == null) {
            return false;
        }
        return LocalDate.now().isAfter(fechaProximaDosis);
    }

    /**
     * Verifica si es vacuna antirrábica.
     *
     * @return true si es antirrábica
     */
    public boolean esAntirrabica() {
        return TIPO_ANTIRRABICA.equalsIgnoreCase(tipoVacuna) ||
               (nombreVacuna != null && nombreVacuna.toLowerCase().contains("rabia"));
    }

    /**
     * Verifica si tiene certificado emitido.
     *
     * @return true si tiene certificado
     */
    public boolean tieneCertificado() {
        return numeroCertificado != null && !numeroCertificado.isBlank();
    }

    /**
     * Calcula el porcentaje de avance del esquema.
     *
     * @return Porcentaje (0-100)
     */
    public Double getPorcentajeEsquema() {
        if (totalDosisEsquema == null || totalDosisEsquema == 0 || numeroDosis == null) {
            return 0.0;
        }
        double porcentaje = (numeroDosis * 100.0) / totalDosisEsquema;
        return Math.clamp(porcentaje, 0.0, 100.0);
    }

    /**
     * Obtiene las dosis restantes del esquema.
     *
     * @return Dosis restantes
     */
    public int getDosisRestantes() {
        if (totalDosisEsquema == null || numeroDosis == null) {
            return 0;
        }
        int restantes = totalDosisEsquema - numeroDosis;
        return Math.max(0, restantes);
    }

    /**
     * Calcula la fecha de fin de inmunidad.
     */
    public void calcularFechaFinInmunidad() {
        if (fechaAplicacion != null && duracionInmunidadMeses != null && duracionInmunidadMeses > 0) {
            this.fechaFinInmunidad = fechaAplicacion.plusMonths(duracionInmunidadMeses);
        }
    }

    /**
     * Registra una reacción adversa.
     *
     * @param reaccion Descripción de la reacción
     * @param tipo Tipo de reacción (LEVE, MODERADA, SEVERA)
     * @param tratamiento Tratamiento aplicado
     */
    public void registrarReaccionAdversa(String reaccion, String tipo, String tratamiento) {
        this.huboReaccion = true;
        this.reaccionAdversa = reaccion;
        this.tipoReaccion = tipo;
        this.tratamientoReaccion = tratamiento;
    }

    /**
     * Genera el número de certificado.
     *
     * @param secuencial Número secuencial
     */
    public void generarCertificado(int secuencial) {
        if (numeroCertificado == null || numeroCertificado.isBlank()) {
            String fecha = LocalDate.now().toString().replace("-", "");
            this.numeroCertificado = String.format("VAC-%s-%04d", fecha, secuencial);
            this.fechaCertificado = LocalDate.now();
        }
    }

    /**
     * Marca el esquema como completo.
     */
    public void marcarEsquemaCompleto() {
        this.esquemaCompleto = true;
        this.fechaProximaDosis = null;
    }

    /**
     * Programa la próxima dosis.
     *
     * @param diasHasta Días hasta la próxima dosis
     */
    public void programarProximaDosis(int diasHasta) {
        this.fechaProximaDosis = LocalDate.now().plusDays(diasHasta);
    }

    /**
     * Invalida la vacunación.
     *
     * @param motivo Motivo de invalidación
     */
    public void invalidar(String motivo) {
        this.vigente = false;
        if (observaciones == null || observaciones.isBlank()) {
            this.observaciones = "Invalidado: " + motivo;
        } else {
            this.observaciones += " | Invalidado: " + motivo;
        }
    }

    /**
     * Calcula los días hasta la próxima dosis.
     *
     * @return Días hasta la próxima dosis, o null si no hay próxima dosis
     */
    public Long getDiasHastaProximaDosis() {
        if (fechaProximaDosis == null) {
            return null;
        }
        return java.time.temporal.ChronoUnit.DAYS.between(LocalDate.now(), fechaProximaDosis);
    }

    /**
     * Calcula los días hasta el fin de inmunidad.
     *
     * @return Días restantes, o null si no hay fecha fin
     */
    public Long getDiasInmunidadRestantes() {
        if (fechaFinInmunidad == null) {
            return null;
        }
        if (inmunidadVencida()) {
            return 0L;
        }
        return java.time.temporal.ChronoUnit.DAYS.between(LocalDate.now(), fechaFinInmunidad);
    }

    /**
     * Obtiene un resumen de la vacunación.
     *
     * @return Resumen
     */
    public String getResumen() {
        return String.format("%s - %s - Dosis %d/%d (Esquema %s)",
            nombreVacuna,
            mascota != null ? mascota.getNombre() : "Sin mascota",
            numeroDosis != null ? numeroDosis : 0,
            totalDosisEsquema != null ? totalDosisEsquema : 0,
            esEsquemaCompleto() ? "Completo" : "Incompleto");
    }

    /**
     * Verifica si la vacunación es reciente (últimos 30 días).
     *
     * @return true si es reciente
     */
    public boolean esReciente() {
        if (fechaAplicacion == null) {
            return false;
        }
        LocalDate hace30Dias = LocalDate.now().minusDays(30);
        return fechaAplicacion.isAfter(hace30Dias);
    }

    // ===================================================================
    // LIFECYCLE CALLBACKS
    // ===================================================================

    /**
     * Se ejecuta antes de persistir la entidad.
     * Calcula fecha de fin de inmunidad si no está establecida.
     */
    @PrePersist
    public void prePersist() {
        calcularFechaFinInmunidad();
    }

    // ===================================================================
    // EQUALS Y HASHCODE
    // ===================================================================

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Vacunacion that)) return false;
        return idVacunacion != null && idVacunacion.equals(that.idVacunacion);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
