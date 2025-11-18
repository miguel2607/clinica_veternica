package com.veterinaria.clinica_veternica.domain.inventario;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;


/**
 * Entidad que representa un Insumo del inventario.
 *
 * Incluye medicamentos, materiales quirúrgicos, alimentos, productos de limpieza,
 * y cualquier otro producto necesario para el funcionamiento de la clínica.
 *
 * Implementa el patrón Observer para alertas de stock bajo.
 *
 * @author Clínica Veterinaria Team
 * @version 1.0
 * @since 2025-11-02
 */
@Entity
@Table(name = "insumos",
       indexes = {
           @Index(name = "idx_insumo_nombre", columnList = "nombre"),
           @Index(name = "idx_insumo_codigo", columnList = "codigo"),
           @Index(name = "idx_insumo_tipo", columnList = "id_tipo_insumo"),
           @Index(name = "idx_insumo_estado", columnList = "estado")
       },
       uniqueConstraints = {
           @UniqueConstraint(name = "uk_insumo_codigo", columnNames = "codigo")
       })
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"tipoInsumo"})
public class Insumo {

    /**
     * Identificador único del insumo.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idInsumo;

    /**
     * Código único del insumo (SKU, código de barras, etc.).
     */
    @NotBlank(message = "El código es obligatorio")
    @Size(min = 2, max = 50, message = "El código debe tener entre 2 y 50 caracteres")
    @Column(nullable = false, unique = true, length = 50)
    private String codigo;

    /**
     * Nombre del insumo.
     */
    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 2, max = 200, message = "El nombre debe tener entre 2 y 200 caracteres")
    @Column(nullable = false, length = 200)
    private String nombre;

    /**
     * Descripción del insumo.
     */
    @Size(max = 1000, message = "La descripción no puede exceder 1000 caracteres")
    @Column(length = 1000)
    private String descripcion;

    /**
     * Tipo de insumo.
     * Relación Many-to-One con TipoInsumo.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_tipo_insumo", nullable = false)
    @NotNull(message = "El tipo de insumo es obligatorio")
    private TipoInsumo tipoInsumo;

    /**
     * Unidad de medida (Unidad, Caja, Kg, Litro, etc.).
     */
    @NotBlank(message = "La unidad de medida es obligatoria")
    @Size(max = 50, message = "La unidad de medida no puede exceder 50 caracteres")
    @Column(nullable = false, length = 50)
    private String unidadMedida;

    /**
     * Cantidad actual en stock.
     */
    @NotNull(message = "La cantidad en stock es obligatoria")
    @Min(value = 0, message = "La cantidad no puede ser negativa")
    @Column(nullable = false)
    @Builder.Default
    private Integer cantidadStock = 0;

    /**
     * Stock mínimo (punto de reorden).
     * Cuando el stock llega a este nivel, se debe generar una alerta.
     */
    @NotNull(message = "El stock mínimo es obligatorio")
    @Min(value = 0, message = "El stock mínimo no puede ser negativo")
    @Column(nullable = false)
    private Integer stockMinimo;

    /**
     * Stock máximo permitido.
     */
    @Min(value = 0, message = "El stock máximo no puede ser negativo")
    @Column
    private Integer stockMaximo;

    /**
     * Precio unitario de compra.
     */
    @NotNull(message = "El precio de compra es obligatorio")
    @DecimalMin(value = "0.0", message = "El precio no puede ser negativo")
    @Column(nullable = false)
    private BigDecimal precioCompra;

    /**
     * Precio unitario de venta (si aplica).
     */
    @DecimalMin(value = "0.0", message = "El precio no puede ser negativo")
    @Column
    private BigDecimal precioVenta;

    /**
     * Lote del insumo.
     */
    @Size(max = 50, message = "El lote no puede exceder 50 caracteres")
    @Column(length = 50)
    private String lote;

    /**
     * Fecha de vencimiento.
     */
    @Column
    private LocalDate fechaVencimiento;

    /**
     * Fecha de última compra.
     */
    @Column
    private LocalDate fechaUltimaCompra;

    /**
     * Ubicación física en el almacén.
     */
    @Size(max = 100, message = "La ubicación no puede exceder 100 caracteres")
    @Column(length = 100)
    private String ubicacion;

    /**
     * Estado del insumo.
     */
    @NotNull(message = "El estado es obligatorio")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private EstadoInsumo estado = EstadoInsumo.DISPONIBLE;

    /**
     * Indica si requiere refrigeración.
     */
    @Column(nullable = false)
    @Builder.Default
    private Boolean requiereRefrigeracion = false;

    /**
     * Indica si requiere receta médica para su uso.
     */
    @Column(nullable = false)
    @Builder.Default
    private Boolean requiereReceta = false;

    /**
     * Observaciones sobre el insumo.
     */
    @Size(max = 1000, message = "Las observaciones no pueden exceder 1000 caracteres")
    @Column(length = 1000)
    private String observaciones;

    /**
     * Indica si el insumo está activo.
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
     * Verifica si el stock está bajo (por debajo del mínimo).
     *
     * @return true si el stock está bajo
     */
    public boolean esStockBajo() {
        return cantidadStock != null && stockMinimo != null && cantidadStock <= stockMinimo;
    }

    /**
     * Verifica si el stock está en nivel crítico (0).
     *
     * @return true si no hay stock
     */
    public boolean estaSinStock() {
        return cantidadStock == null || cantidadStock == 0;
    }

    /**
     * Verifica si el insumo está próximo a vencer (menos de 30 días).
     *
     * @return true si está próximo a vencer
     */
    public boolean estaProximoAVencer() {
        if (fechaVencimiento == null) {
            return false;
        }
        LocalDate hoy = LocalDate.now();
        LocalDate fechaLimite = hoy.plusDays(30);
        return !fechaVencimiento.isAfter(fechaLimite);
    }

    /**
     * Verifica si el insumo está vencido.
     *
     * @return true si está vencido
     */
    public boolean estaVencido() {
        if (fechaVencimiento == null) {
            return false;
        }
        return fechaVencimiento.isBefore(LocalDate.now());
    }

    /**
     * Calcula el margen de ganancia (precio venta - precio compra).
     *
     * @return Margen de ganancia
     */
    public BigDecimal getMargenGanancia() {
        if (precioVenta == null || precioCompra == null) {
            return BigDecimal.ZERO;
        }
        return precioVenta.subtract(precioCompra);
    }

    /**
     * Calcula el porcentaje de margen de ganancia.
     *
     * @return Porcentaje de margen
     */
    public Double getPorcentajeMargen() {
        if (precioVenta == null || precioCompra == null || precioCompra.compareTo(BigDecimal.ZERO) == 0) {
            return 0.0;
        }
        return getMargenGanancia().divide(precioCompra, 4, java.math.RoundingMode.HALF_UP)
            .multiply(new BigDecimal("100"))
            .doubleValue();
    }

    /**
     * Calcula el valor total del inventario (precio compra * cantidad).
     *
     * @return Valor total
     */
    public BigDecimal getValorTotalInventario() {
        if (precioCompra == null || cantidadStock == null) {
            return BigDecimal.ZERO;
        }
        return precioCompra.multiply(BigDecimal.valueOf(cantidadStock));
    }

    /**
     * Incrementa el stock.
     *
     * @param cantidad Cantidad a incrementar
     */
    public void incrementarStock(int cantidad) {
        if (cantidad > 0) {
            this.cantidadStock = (this.cantidadStock == null ? 0 : this.cantidadStock) + cantidad;
            this.fechaUltimaCompra = LocalDate.now();
        }
    }

    /**
     * Decrementa el stock.
     *
     * @param cantidad Cantidad a decrementar
     * @throws IllegalArgumentException si no hay suficiente stock
     */
    public void decrementarStock(int cantidad) {
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser positiva");
        }
        if (this.cantidadStock == null || this.cantidadStock < cantidad) {
            throw new IllegalArgumentException("Stock insuficiente");
        }
        this.cantidadStock -= cantidad;
    }

    /**
     * Activa el insumo.
     */
    public void activar() {
        this.activo = true;
    }

    /**
     * Desactiva el insumo.
     */
    public void desactivar() {
        this.activo = false;
    }

    /**
     * Marca el insumo como agotado.
     */
    public void marcarComoAgotado() {
        this.estado = EstadoInsumo.AGOTADO;
        this.cantidadStock = 0;
    }

    /**
     * Marca el insumo como disponible.
     */
    public void marcarComoDisponible() {
        this.estado = EstadoInsumo.DISPONIBLE;
    }

    // ===================================================================
    // EQUALS Y HASHCODE
    // ===================================================================

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Insumo insumo)) return false;
        return idInsumo != null && idInsumo.equals(insumo.idInsumo);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
