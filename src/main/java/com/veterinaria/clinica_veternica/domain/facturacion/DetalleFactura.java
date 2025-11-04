package com.veterinaria.clinica_veternica.domain.facturacion;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entidad que representa un Detalle de Factura.
 *
 * Línea individual dentro de una factura que especifica un producto o servicio vendido.
 * Cada detalle contiene: descripción, cantidad, precio unitario y subtotal.
 *
 * @author Clínica Veterinaria Team
 * @version 1.0
 * @since 2025-11-02
 */
@Entity
@Table(name = "detalles_factura",
       indexes = {
           @Index(name = "idx_detalle_factura", columnList = "id_factura")
       })
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = "factura")
public class DetalleFactura {

    /**
     * Identificador único del detalle.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idDetalle;

    /**
     * Factura a la que pertenece este detalle.
     * Relación Many-to-One con Factura.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_factura", nullable = false)
    @NotNull(message = "La factura es obligatoria")
    private Factura factura;

    /**
     * Tipo de item (SERVICIO, INSUMO, PRODUCTO).
     */
    @NotBlank(message = "El tipo de item es obligatorio")
    @Size(max = 20, message = "El tipo no puede exceder 20 caracteres")
    @Column(nullable = false, length = 20)
    private String tipoItem;

    /**
     * ID del item relacionado (idServicio, idInsumo, etc.).
     */
    @Column
    private Long idItemRelacionado;

    /**
     * Descripción del producto o servicio.
     */
    @NotBlank(message = "La descripción es obligatoria")
    @Size(min = 2, max = 500, message = "La descripción debe tener entre 2 y 500 caracteres")
    @Column(nullable = false, length = 500)
    private String descripcion;

    /**
     * Cantidad del producto o servicio.
     */
    @NotNull(message = "La cantidad es obligatoria")
    @Min(value = 1, message = "La cantidad mínima es 1")
    @Column(nullable = false)
    private Integer cantidad;

    /**
     * Precio unitario.
     */
    @NotNull(message = "El precio unitario es obligatorio")
    @DecimalMin(value = "0.0", message = "El precio no puede ser negativo")
    @Column(nullable = false)
    private BigDecimal precioUnitario;

    /**
     * Porcentaje de descuento aplicado a este detalle (0-100).
     */
    @DecimalMin(value = "0.0", message = "El descuento no puede ser negativo")
    @DecimalMax(value = "100.0", message = "El descuento no puede exceder 100%")
    @Column
    @Builder.Default
    private BigDecimal porcentajeDescuento = BigDecimal.ZERO;

    /**
     * Monto del descuento calculado.
     */
    @Column
    @Builder.Default
    private BigDecimal montoDescuento = BigDecimal.ZERO;

    /**
     * Subtotal del detalle (cantidad * precioUnitario - descuento).
     */
    @NotNull(message = "El subtotal es obligatorio")
    @Column(nullable = false)
    @Builder.Default
    private BigDecimal subtotal = BigDecimal.ZERO;

    /**
     * Observaciones específicas del detalle.
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

    // ===================================================================
    // MÉTODOS DE NEGOCIO
    // ===================================================================

    /**
     * Calcula el subtotal del detalle.
     * Se ejecuta antes de persistir o actualizar.
     */
    @PrePersist
    @PreUpdate
    public void calcularSubtotal() {
        if (cantidad != null && precioUnitario != null) {
            // Calcular precio total sin descuento
            BigDecimal totalSinDescuento = precioUnitario.multiply(BigDecimal.valueOf(cantidad));

            // Calcular descuento
            if (porcentajeDescuento != null && porcentajeDescuento.compareTo(BigDecimal.ZERO) > 0) {
                this.montoDescuento = totalSinDescuento.multiply(porcentajeDescuento)
                    .divide(new BigDecimal("100"), 2, java.math.RoundingMode.HALF_UP);
            } else {
                this.montoDescuento = BigDecimal.ZERO;
            }

            // Calcular subtotal final
            this.subtotal = totalSinDescuento.subtract(montoDescuento);
        }
    }

    /**
     * Obtiene el precio total sin descuento.
     *
     * @return Precio total (cantidad * precio unitario)
     */
    public BigDecimal getPrecioTotal() {
        if (cantidad == null || precioUnitario == null) {
            return BigDecimal.ZERO;
        }
        return precioUnitario.multiply(BigDecimal.valueOf(cantidad));
    }

    /**
     * Verifica si el detalle tiene descuento aplicado.
     *
     * @return true si tiene descuento
     */
    public boolean tieneDescuento() {
        return porcentajeDescuento != null &&
               porcentajeDescuento.compareTo(BigDecimal.ZERO) > 0;
    }

    /**
     * Verifica si el detalle es de tipo servicio.
     *
     * @return true si es servicio
     */
    public boolean esServicio() {
        return "SERVICIO".equalsIgnoreCase(tipoItem);
    }

    /**
     * Verifica si el detalle es de tipo insumo.
     *
     * @return true si es insumo
     */
    public boolean esInsumo() {
        return "INSUMO".equalsIgnoreCase(tipoItem) ||
               "PRODUCTO".equalsIgnoreCase(tipoItem);
    }

    /**
     * Actualiza la cantidad y recalcula el subtotal.
     *
     * @param nuevaCantidad Nueva cantidad
     */
    public void actualizarCantidad(int nuevaCantidad) {
        if (nuevaCantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor a 0");
        }
        this.cantidad = nuevaCantidad;
        calcularSubtotal();
    }

    /**
     * Actualiza el precio unitario y recalcula el subtotal.
     *
     * @param nuevoPrecio Nuevo precio unitario
     */
    public void actualizarPrecio(BigDecimal nuevoPrecio) {
        if (nuevoPrecio.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("El precio no puede ser negativo");
        }
        this.precioUnitario = nuevoPrecio;
        calcularSubtotal();
    }

    /**
     * Aplica un descuento al detalle y recalcula el subtotal.
     *
     * @param porcentaje Porcentaje de descuento (0-100)
     */
    public void aplicarDescuento(BigDecimal porcentaje) {
        if (porcentaje.compareTo(BigDecimal.ZERO) < 0 ||
            porcentaje.compareTo(new BigDecimal("100")) > 0) {
            throw new IllegalArgumentException("El porcentaje debe estar entre 0 y 100");
        }
        this.porcentajeDescuento = porcentaje;
        calcularSubtotal();
    }

    /**
     * Quita el descuento del detalle.
     */
    public void quitarDescuento() {
        this.porcentajeDescuento = BigDecimal.ZERO;
        this.montoDescuento = BigDecimal.ZERO;
        calcularSubtotal();
    }

    // ===================================================================
    // EQUALS Y HASHCODE
    // ===================================================================

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DetalleFactura that)) return false;
        return idDetalle != null && idDetalle.equals(that.idDetalle);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
