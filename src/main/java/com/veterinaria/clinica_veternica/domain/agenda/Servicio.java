package com.veterinaria.clinica_veternica.domain.agenda;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidad que representa un Servicio veterinario ofrecido por la clínica.
 *
 * Los servicios pueden ser de diferentes tipos: consultas, cirugías,
 * procedimientos diagnósticos, servicios estéticos, emergencias, etc.
 *
 * Cada servicio tiene un precio base, duración estimada y categoría.
 * Los precios pueden ser modificados dinámicamente mediante decoradores
 * (descuentos, urgencias, domicilio, etc.).
 *
 * @author Clínica Veterinaria Team
 * @version 1.0
 * @since 2025-11-02
 */
@Entity
@Table(name = "servicios",
       indexes = {
           @Index(name = "idx_servicio_nombre", columnList = "nombre"),
           @Index(name = "idx_servicio_tipo", columnList = "tipo_servicio"),
           @Index(name = "idx_servicio_categoria", columnList = "categoria"),
           @Index(name = "idx_servicio_activo", columnList = "activo")
       })
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = "citas")
public class Servicio {

    /**
     * Identificador único del servicio.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idServicio;

    /**
     * Nombre del servicio.
     */
    @NotBlank(message = "El nombre del servicio es obligatorio")
    @Size(min = 3, max = 150, message = "El nombre debe tener entre 3 y 150 caracteres")
    @Column(nullable = false, length = 150)
    private String nombre;

    /**
     * Descripción detallada del servicio.
     */
    @Size(max = 1000, message = "La descripción no puede exceder 1000 caracteres")
    @Column(length = 1000)
    private String descripcion;

    /**
     * Tipo de servicio (consulta, cirugía, diagnóstico, etc.).
     */
    @NotNull(message = "El tipo de servicio es obligatorio")
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_servicio", nullable = false, length = 30)
    private TipoServicio tipoServicio;

    /**
     * Categoría del servicio (clínico, quirúrgico, estético, emergencia).
     */
    @NotNull(message = "La categoría es obligatoria")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private CategoriaServicio categoria;

    /**
     * Precio base del servicio.
     * El precio final puede variar con descuentos, urgencias, etc.
     */
    @NotNull(message = "El precio es obligatorio")
    @DecimalMin(value = "0.0", inclusive = false, message = "El precio debe ser mayor a 0")
    @Column(nullable = false)
    private BigDecimal precio;

    /**
     * Duración estimada del servicio en minutos.
     */
    @NotNull(message = "La duración es obligatoria")
    @Min(value = 5, message = "La duración mínima es 5 minutos")
    @Max(value = 480, message = "La duración máxima es 480 minutos (8 horas)")
    @Column(nullable = false)
    private Integer duracionEstimadaMinutos;

    /**
     * Indica si requiere anestesia.
     */
    @Column(nullable = false)
    @Builder.Default
    private Boolean requiereAnestesia = false;

    /**
     * Indica si requiere ayuno previo.
     */
    @Column(nullable = false)
    @Builder.Default
    private Boolean requiereAyuno = false;

    /**
     * Horas de ayuno requeridas (si aplica).
     */
    @Min(value = 0, message = "Las horas de ayuno no pueden ser negativas")
    @Column
    private Integer horasAyunoRequeridas;

    /**
     * Indica si requiere hospitalización post-servicio.
     */
    @Column(nullable = false)
    @Builder.Default
    private Boolean requiereHospitalizacion = false;

    /**
     * Indica si requiere cuidados especiales post-servicio.
     */
    @Column(nullable = false)
    @Builder.Default
    private Boolean requiereCuidadosEspeciales = false;

    /**
     * Descripción de los cuidados especiales requeridos.
     */
    @Size(max = 1000, message = "Los cuidados especiales no pueden exceder 1000 caracteres")
    @Column(length = 1000)
    private String cuidadosEspeciales;

    /**
     * Requisitos previos para el servicio.
     */
    @Size(max = 500, message = "Los requisitos no pueden exceder 500 caracteres")
    @Column(length = 500)
    private String requisitos;

    /**
     * Especies para las que está disponible el servicio.
     * Ejemplo: "Perro, Gato" o "Todas las especies".
     */
    @Size(max = 200, message = "Las especies no pueden exceder 200 caracteres")
    @Column(length = 200)
    private String especiesAplicables;

    /**
     * Edad mínima recomendada en meses (si aplica).
     */
    @Min(value = 0, message = "La edad mínima no puede ser negativa")
    @Column
    private Integer edadMinimaRecomendadaMeses;

    /**
     * Peso mínimo recomendado en kg (si aplica).
     */
    @DecimalMin(value = "0.0", message = "El peso mínimo no puede ser negativo")
    @Column
    private Double pesoMinimoRecomendadoKg;

    /**
     * Indica si el servicio está disponible para agendamiento.
     */
    @Column(nullable = false)
    @Builder.Default
    private Boolean activo = true;

    /**
     * Indica si el servicio está disponible para emergencias.
     */
    @Column(nullable = false)
    @Builder.Default
    private Boolean disponibleEmergencias = false;

    /**
     * Indica si el servicio está disponible a domicilio.
     */
    @Column(nullable = false)
    @Builder.Default
    private Boolean disponibleDomicilio = false;

    /**
     * Costo adicional por servicio a domicilio.
     */
    @DecimalMin(value = "0.0", message = "El costo adicional no puede ser negativo")
    @Column
    private BigDecimal costoAdicionalDomicilio;

    /**
     * Observaciones adicionales sobre el servicio.
     */
    @Size(max = 1000, message = "Las observaciones no pueden exceder 1000 caracteres")
    @Column(length = 1000)
    private String observaciones;

    /**
     * Citas que utilizan este servicio.
     * Relación One-to-Many con Cita.
     */
    @OneToMany(mappedBy = "servicio", cascade = CascadeType.ALL)
    @Builder.Default
    private List<Cita> citas = new ArrayList<>();

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
     * Obtiene la duración en horas.
     *
     * @return Duración en horas
     */
    public double getDuracionHoras() {
        if (duracionEstimadaMinutos == null) {
            return 0.0;
        }
        return Math.round((duracionEstimadaMinutos / 60.0) * 100.0) / 100.0;
    }

    /**
     * Verifica si el servicio es de consulta.
     *
     * @return true si es consulta
     */
    public boolean esConsulta() {
        return tipoServicio == TipoServicio.CONSULTA;
    }

    /**
     * Verifica si el servicio es quirúrgico.
     *
     * @return true si es cirugía
     */
    public boolean esCirugia() {
        return tipoServicio == TipoServicio.CIRUGIA;
    }

    /**
     * Verifica si el servicio es de emergencia.
     *
     * @return true si es emergencia
     */
    public boolean esEmergencia() {
        return tipoServicio == TipoServicio.EMERGENCIA;
    }

    /**
     * Verifica si el servicio requiere preparación previa.
     *
     * @return true si requiere preparación
     */
    public boolean requierePreparacion() {
        return requiereAyuno || requiereAnestesia || (requisitos != null && !requisitos.isBlank());
    }

    /**
     * Calcula el precio con recargo de emergencia (50% adicional).
     *
     * @return Precio con recargo de emergencia
     */
    public BigDecimal getPrecioEmergencia() {
        return precio.multiply(new BigDecimal("1.5"));
    }

    /**
     * Calcula el precio con recargo de domicilio.
     *
     * @return Precio con recargo de domicilio
     */
    public BigDecimal getPrecioDomicilio() {
        if (costoAdicionalDomicilio != null) {
            return precio.add(costoAdicionalDomicilio);
        }
        return precio;
    }

    /**
     * Aplica un descuento al precio base.
     *
     * @param porcentajeDescuento Porcentaje de descuento (0-100)
     * @return Precio con descuento
     */
    public BigDecimal getPrecioConDescuento(double porcentajeDescuento) {
        if (porcentajeDescuento < 0 || porcentajeDescuento > 100) {
            throw new IllegalArgumentException("El porcentaje debe estar entre 0 y 100");
        }

        BigDecimal descuento = precio.multiply(BigDecimal.valueOf(porcentajeDescuento / 100));
        return precio.subtract(descuento);
    }

    /**
     * Verifica si el servicio es aplicable para una especie específica.
     *
     * @param especie Nombre de la especie
     * @return true si es aplicable
     */
    public boolean esAplicableParaEspecie(String especie) {
        if (especiesAplicables == null || especiesAplicables.isBlank()) {
            return true; // Si no se especifican especies, aplica para todas
        }

        if (especiesAplicables.equalsIgnoreCase("Todas las especies") ||
            especiesAplicables.equalsIgnoreCase("Todas")) {
            return true;
        }

        if (especie == null) {
            return false;
        }
        return especiesAplicables.toLowerCase().contains(especie.toLowerCase());
    }

    /**
     * Verifica si el servicio es apto para una mascota según edad y peso.
     *
     * @param edadMeses Edad de la mascota en meses
     * @param pesoKg Peso de la mascota en kg
     * @return true si cumple los requisitos
     */
    public boolean esAptoParaMascota(Integer edadMeses, Double pesoKg) {
        if (edadMinimaRecomendadaMeses != null && edadMeses != null 
                && edadMeses < edadMinimaRecomendadaMeses) {
            return false;
        }
        return pesoMinimoRecomendadoKg == null || pesoKg == null 
                || pesoKg >= pesoMinimoRecomendadoKg;
    }

    /**
     * Agrega una cita al servicio.
     *
     * @param cita Cita a agregar
     */
    public void agregarCita(Cita cita) {
        citas.add(cita);
        cita.setServicio(this);
    }

    /**
     * Obtiene el número de citas con este servicio.
     *
     * @return Cantidad de citas
     */
    public int getCantidadCitas() {
        return citas != null ? citas.size() : 0;
    }

    /**
     * Activa el servicio.
     */
    public void activar() {
        this.activo = true;
    }

    /**
     * Desactiva el servicio.
     */
    public void desactivar() {
        this.activo = false;
    }

    /**
     * Habilita disponibilidad para emergencias.
     */
    public void habilitarEmergencias() {
        this.disponibleEmergencias = true;
    }

    /**
     * Deshabilita disponibilidad para emergencias.
     */
    public void deshabilitarEmergencias() {
        this.disponibleEmergencias = false;
    }

    /**
     * Habilita disponibilidad a domicilio.
     */
    public void habilitarDomicilio() {
        this.disponibleDomicilio = true;
    }

    /**
     * Deshabilita disponibilidad a domicilio.
     */
    public void deshabilitarDomicilio() {
        this.disponibleDomicilio = false;
    }

    // ===================================================================
    // EQUALS Y HASHCODE
    // ===================================================================

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Servicio servicio)) return false;
        return idServicio != null && idServicio.equals(servicio.idServicio);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
