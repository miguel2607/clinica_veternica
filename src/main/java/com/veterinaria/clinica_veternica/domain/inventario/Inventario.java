package com.veterinaria.clinica_veternica.domain.inventario;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entidad que representa el Inventario consolidado.
 *
 * Vista de resumen del estado actual del inventario para análisis y reportes.
 * Esta entidad puede ser calculada o materializada según necesidades de rendimiento.
 *
 * Utiliza el patrón Proxy para control de acceso y caché.
 *
 * @author Clínica Veterinaria Team
 * @version 1.0
 * @since 2025-11-02
 */
@Entity
@Table(name = "inventario_consolidado",
       indexes = {
           @Index(name = "idx_inventario_insumo", columnList = "id_insumo"),
           @Index(name = "idx_inventario_fecha", columnList = "fecha_actualizacion")
       })
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = "insumo")
public class Inventario {

    /**
     * Identificador único del registro de inventario.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idInventario;

    /**
     * Insumo al que corresponde este registro de inventario.
     * Relación One-to-One con Insumo.
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_insumo", nullable = false, unique = true)
    @NotNull(message = "El insumo es obligatorio")
    private Insumo insumo;

    /**
     * Cantidad actual en stock (redundante con Insumo para optimización).
     */
    @Column(nullable = false)
    @Builder.Default
    private Integer cantidadActual = 0;

    /**
     * Valor total del inventario (precio compra * cantidad).
     */
    @Column
    @Builder.Default
    private BigDecimal valorTotal = BigDecimal.ZERO;

    /**
     * Cantidad de entradas totales (histórico).
     */
    @Column(nullable = false)
    @Builder.Default
    private Integer totalEntradas = 0;

    /**
     * Cantidad de salidas totales (histórico).
     */
    @Column(nullable = false)
    @Builder.Default
    private Integer totalSalidas = 0;

    /**
     * Valor total de entradas (histórico).
     */
    @Column
    @Builder.Default
    private BigDecimal valorEntradas = BigDecimal.ZERO;

    /**
     * Valor total de salidas (histórico).
     */
    @Column
    @Builder.Default
    private BigDecimal valorSalidas = BigDecimal.ZERO;

    /**
     * Promedio de consumo mensual (calculado).
     */
    @Column
    private Double promedioConsumoMensual;

    /**
     * Días estimados de stock disponible.
     */
    @Column
    private Integer diasStockDisponible;

    /**
     * Fecha de última entrada.
     */
    @Column
    private LocalDateTime fechaUltimaEntrada;

    /**
     * Fecha de última salida.
     */
    @Column
    private LocalDateTime fechaUltimaSalida;

    /**
     * Indica si el insumo requiere reorden (stock bajo).
     */
    @Column(nullable = false)
    @Builder.Default
    private Boolean requiereReorden = false;

    /**
     * Fecha y hora de última actualización del inventario.
     */
    @Column(nullable = false)
    @Builder.Default
    private LocalDateTime fechaActualizacion = LocalDateTime.now();

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
     * Actualiza el valor total del inventario.
     */
    @PrePersist
    @PreUpdate
    public void calcularValorTotal() {
        if (insumo != null && cantidadActual != null) {
            BigDecimal precioCompra = insumo.getPrecioCompra();
            if (precioCompra != null) {
                this.valorTotal = precioCompra.multiply(BigDecimal.valueOf(cantidadActual));
            }
        }
        this.fechaActualizacion = LocalDateTime.now();
    }

    /**
     * Registra una entrada al inventario.
     *
     * @param cantidad Cantidad entrante
     * @param valor Valor de la entrada
     */
    public void registrarEntrada(int cantidad, BigDecimal valor) {
        this.cantidadActual += cantidad;
        this.totalEntradas += cantidad;
        if (valor != null) {
            this.valorEntradas = this.valorEntradas.add(valor);
        }
        this.fechaUltimaEntrada = LocalDateTime.now();
        this.fechaActualizacion = LocalDateTime.now();
        actualizarRequiereReorden();
        calcularDiasStockDisponible();
    }

    /**
     * Registra una salida del inventario.
     *
     * @param cantidad Cantidad saliente
     * @param valor Valor de la salida
     */
    public void registrarSalida(int cantidad, BigDecimal valor) {
        this.cantidadActual -= cantidad;
        this.totalSalidas += cantidad;
        if (valor != null) {
            this.valorSalidas = this.valorSalidas.add(valor);
        }
        this.fechaUltimaSalida = LocalDateTime.now();
        this.fechaActualizacion = LocalDateTime.now();
        actualizarRequiereReorden();
        calcularDiasStockDisponible();
    }

    /**
     * Actualiza el indicador de reorden según el stock mínimo.
     */
    private void actualizarRequiereReorden() {
        if (insumo != null && insumo.getStockMinimo() != null) {
            this.requiereReorden = this.cantidadActual <= insumo.getStockMinimo();
        }
    }

    /**
     * Calcula los días de stock disponible según el consumo promedio.
     */
    private void calcularDiasStockDisponible() {
        if (promedioConsumoMensual != null && promedioConsumoMensual > 0) {
            double consumoDiario = promedioConsumoMensual / 30.0;
            this.diasStockDisponible = (int) Math.floor(cantidadActual / consumoDiario);
        }
    }

    /**
     * Calcula el promedio de consumo mensual.
     *
     * @param meses Número de meses a considerar
     */
    public void calcularPromedioConsumo(int meses) {
        if (meses > 0 && totalSalidas != null) {
            this.promedioConsumoMensual = totalSalidas.doubleValue() / meses;
            calcularDiasStockDisponible();
        }
    }

    /**
     * Obtiene el índice de rotación de inventario.
     *
     * @return Índice de rotación (salidas / promedio stock)
     */
    public Double getIndiceRotacion() {
        if (cantidadActual == null || cantidadActual == 0 || totalSalidas == null || totalSalidas == 0) {
            return 0.0;
        }
        return totalSalidas.doubleValue() / cantidadActual.doubleValue();
    }

    /**
     * Verifica si el inventario está en nivel crítico.
     *
     * @return true si está crítico (0 unidades o menor al mínimo)
     */
    public boolean esNivelCritico() {
        return (cantidadActual != null && cantidadActual == 0) || com.veterinaria.clinica_veternica.util.Constants.isTrue(requiereReorden);
    }

    /**
     * Obtiene el margen bruto del inventario.
     *
     * @return Margen bruto (valor entradas - valor salidas)
     */
    public BigDecimal getMargenBruto() {
        if (valorEntradas == null || valorSalidas == null) {
            return BigDecimal.ZERO;
        }
        return valorEntradas.subtract(valorSalidas);
    }

    /**
     * Verifica si el insumo tiene movimiento reciente (últimos 30 días).
     *
     * @return true si tuvo movimiento reciente
     */
    public boolean tieneMovimientoReciente() {
        LocalDateTime hace30Dias = LocalDateTime.now().minusDays(com.veterinaria.clinica_veternica.util.Constants.DIAS_POR_MES);
        return (fechaUltimaEntrada != null && fechaUltimaEntrada.isAfter(hace30Dias)) ||
               (fechaUltimaSalida != null && fechaUltimaSalida.isAfter(hace30Dias));
    }

    /**
     * Obtiene la cantidad disponible (alias de cantidadActual).
     * Método de conveniencia para compatibilidad con código existente.
     *
     * @return Cantidad disponible en stock
     */
    public Integer getCantidadDisponible() {
        return cantidadActual;
    }

    /**
     * Obtiene el stock mínimo del insumo asociado.
     * Método de conveniencia que accede al Insumo relacionado.
     *
     * @return Stock mínimo o null si no está disponible
     */
    public Integer getStockMinimo() {
        return insumo != null ? insumo.getStockMinimo() : null;
    }

    /**
     * Obtiene el stock máximo del insumo asociado.
     * Método de conveniencia que accede al Insumo relacionado.
     *
     * @return Stock máximo o null si no está disponible
     */
    public Integer getStockMaximo() {
        return insumo != null ? insumo.getStockMaximo() : null;
    }

    // ===================================================================
    // EQUALS Y HASHCODE
    // ===================================================================

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Inventario that)) return false;
        return idInventario != null && idInventario.equals(that.idInventario);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
