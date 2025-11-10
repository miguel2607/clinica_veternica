package com.veterinaria.clinica_veternica.domain.facturacion;

import com.veterinaria.clinica_veternica.domain.agenda.Cita;
import com.veterinaria.clinica_veternica.domain.paciente.Propietario;
import com.veterinaria.clinica_veternica.domain.usuario.Usuario;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidad que representa una Factura.
 *
 * Documento que registra la venta de servicios y productos a un cliente.
 * Una factura puede contener múltiples detalles (servicios/productos).
 *
 * Utiliza el patrón Builder para construcción paso a paso.
 * Utiliza el patrón Strategy para diferentes métodos de pago.
 *
 * @author Clínica Veterinaria Team
 * @version 1.0
 * @since 2025-11-02
 */
@Entity
@Table(name = "facturas",
       indexes = {
           @Index(name = "idx_factura_numero", columnList = "numero_factura"),
           @Index(name = "idx_factura_propietario", columnList = "id_propietario"),
           @Index(name = "idx_factura_fecha", columnList = "fecha_emision"),
           @Index(name = "idx_factura_cita", columnList = "id_cita"),
           @Index(name = "idx_factura_pagada", columnList = "pagada")
       },
       uniqueConstraints = {
           @UniqueConstraint(name = "uk_factura_numero", columnNames = "numero_factura")
       })
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"propietario", "cita", "detalles", "pagos", "usuario"})
public class Factura {

    /**
     * Identificador único de la factura.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idFactura;

    /**
     * Número único de la factura.
     * Formato: FAC-{año}-{número secuencial}
     */
    @Column(name = "numero_factura", nullable = false, unique = true, length = 50)
    private String numeroFactura;

    /**
     * Propietario al que se factura.
     * Relación Many-to-One con Propietario.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_propietario", nullable = false)
    @NotNull(message = "El propietario es obligatorio")
    private Propietario propietario;

    /**
     * Cita asociada a esta factura (si aplica).
     * Relación Many-to-One con Cita.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_cita")
    private Cita cita;

    /**
     * Fecha de emisión de la factura.
     */
    @NotNull(message = "La fecha de emisión es obligatoria")
    @Column(nullable = false)
    @Builder.Default
    private LocalDate fechaEmision = LocalDate.now();

    /**
     * Fecha de vencimiento para el pago.
     */
    @Column
    private LocalDate fechaVencimiento;

    /**
     * Subtotal (suma de todos los detalles sin impuestos).
     */
    @NotNull(message = "El subtotal es obligatorio")
    @DecimalMin(value = "0.0", message = "El subtotal no puede ser negativo")
    @Column(nullable = false)
    @Builder.Default
    private BigDecimal subtotal = BigDecimal.ZERO;

    /**
     * Porcentaje de descuento aplicado (0-100).
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
     * Porcentaje de impuesto (IVA, IGV, etc.).
     */
    @DecimalMin(value = "0.0", message = "El impuesto no puede ser negativo")
    @DecimalMax(value = "100.0", message = "El impuesto no puede exceder 100%")
    @Column
    @Builder.Default
    private BigDecimal porcentajeImpuesto = BigDecimal.ZERO;

    /**
     * Monto del impuesto calculado.
     */
    @Column
    @Builder.Default
    private BigDecimal montoImpuesto = BigDecimal.ZERO;

    /**
     * Total a pagar (subtotal - descuento + impuesto).
     */
    @NotNull(message = "El total es obligatorio")
    @DecimalMin(value = "0.0", message = "El total no puede ser negativo")
    @Column(nullable = false)
    @Builder.Default
    private BigDecimal total = BigDecimal.ZERO;

    /**
     * Monto total pagado hasta el momento.
     */
    @Column
    @Builder.Default
    private BigDecimal montoPagado = BigDecimal.ZERO;

    /**
     * Saldo pendiente de pago.
     */
    @Column
    @Builder.Default
    private BigDecimal saldoPendiente = BigDecimal.ZERO;

    /**
     * Indica si la factura está completamente pagada.
     */
    @Column(nullable = false)
    @Builder.Default
    private Boolean pagada = false;

    /**
     * Fecha de pago completo.
     */
    @Column
    private LocalDateTime fechaPago;

    /**
     * Indica si la factura fue anulada.
     */
    @Column(nullable = false)
    @Builder.Default
    private Boolean anulada = false;

    /**
     * Fecha de anulación.
     */
    @Column
    private LocalDateTime fechaAnulacion;

    /**
     * Motivo de anulación.
     */
    @Size(max = 500, message = "El motivo de anulación no puede exceder 500 caracteres")
    @Column(length = 500)
    private String motivoAnulacion;

    /**
     * Observaciones de la factura.
     */
    @Size(max = 1000, message = "Las observaciones no pueden exceder 1000 caracteres")
    @Column(length = 1000)
    private String observaciones;

    /**
     * Usuario que emitió la factura.
     * Relación Many-to-One con Usuario.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;

    /**
     * Detalles de la factura.
     * Relación One-to-Many con DetalleFactura.
     */
    @OneToMany(mappedBy = "factura", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<DetalleFactura> detalles = new ArrayList<>();

    /**
     * Pagos aplicados a esta factura.
     * Relación One-to-Many con Pago.
     */
    @OneToMany(mappedBy = "factura", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("fechaPago DESC")
    @Builder.Default
    private List<Pago> pagos = new ArrayList<>();

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
     * Inicializa la factura antes de persistir.
     * Genera número de factura y calcula totales.
     */
    @PrePersist
    public void inicializarFactura() {
        // Generar número de factura si no existe
        if (numeroFactura == null) {
            this.numeroFactura = String.format("FAC-%d-%d",
                java.time.Year.now().getValue(),
                System.currentTimeMillis() % 1000000);
        }
        // Calcular totales
        calcularTotales();
    }

    /**
     * Calcula totales de la factura.
     * Se ejecuta antes de actualizar.
     */
    @PreUpdate
    public void calcularTotales() {
        // Calcular subtotal desde los detalles
        if (detalles != null && !detalles.isEmpty()) {
            this.subtotal = detalles.stream()
                .map(DetalleFactura::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        }

        // Calcular descuento
        if (porcentajeDescuento != null && porcentajeDescuento.compareTo(BigDecimal.ZERO) > 0) {
            this.montoDescuento = subtotal.multiply(porcentajeDescuento)
                .divide(com.veterinaria.clinica_veternica.util.Constants.PORCENTAJE_DIVISOR, 2, java.math.RoundingMode.HALF_UP);
        } else {
            this.montoDescuento = BigDecimal.ZERO;
        }

        // Calcular base imponible
        BigDecimal baseImponible = subtotal.subtract(montoDescuento);

        // Calcular impuesto
        if (porcentajeImpuesto != null && porcentajeImpuesto.compareTo(BigDecimal.ZERO) > 0) {
            this.montoImpuesto = baseImponible.multiply(porcentajeImpuesto)
                .divide(com.veterinaria.clinica_veternica.util.Constants.PORCENTAJE_DIVISOR, 2, java.math.RoundingMode.HALF_UP);
        } else {
            this.montoImpuesto = BigDecimal.ZERO;
        }

        // Calcular total
        this.total = baseImponible.add(montoImpuesto);

        // Calcular saldo pendiente
        this.saldoPendiente = this.total.subtract(this.montoPagado);

        // Verificar si está pagada
        this.pagada = this.saldoPendiente.compareTo(BigDecimal.ZERO) <= 0;
    }

    /**
     * Agrega un detalle a la factura.
     *
     * @param detalle Detalle a agregar
     */
    public void agregarDetalle(DetalleFactura detalle) {
        detalles.add(detalle);
        detalle.setFactura(this);
        calcularTotales();
    }

    /**
     * Elimina un detalle de la factura.
     *
     * @param detalle Detalle a eliminar
     */
    public void eliminarDetalle(DetalleFactura detalle) {
        detalles.remove(detalle);
        detalle.setFactura(null);
        calcularTotales();
    }

    /**
     * Agrega un pago a la factura.
     *
     * @param pago Pago a agregar
     */
    public void agregarPago(Pago pago) {
        pagos.add(pago);
        pago.setFactura(this);
        this.montoPagado = this.montoPagado.add(pago.getMonto());
        calcularTotales();

        if (this.pagada && this.fechaPago == null) {
            this.fechaPago = LocalDateTime.now();
        }
    }

    /**
     * Anula la factura.
     *
     * @param motivo Motivo de la anulación
     */
    public void anular(String motivo) {
        if (this.anulada) {
            throw new IllegalStateException("La factura ya está anulada");
        }
        this.anulada = true;
        this.fechaAnulacion = LocalDateTime.now();
        this.motivoAnulacion = motivo;
    }

    /**
     * Verifica si la factura está vencida.
     *
     * @return true si está vencida
     */
    public boolean estaVencida() {
        if (pagada || fechaVencimiento == null) {
            return false;
        }
        return fechaVencimiento.isBefore(LocalDate.now());
    }

    /**
     * Obtiene los días de vencimiento (negativo si está vencida).
     *
     * @return Días hasta o desde el vencimiento
     */
    public long getDiasVencimiento() {
        if (fechaVencimiento == null) {
            return 0;
        }
        return java.time.temporal.ChronoUnit.DAYS.between(LocalDate.now(), fechaVencimiento);
    }

    /**
     * Verifica si la factura es válida (no anulada).
     *
     * @return true si es válida
     */
    public boolean esValida() {
        return !anulada;
    }

    /**
     * Obtiene el porcentaje pagado de la factura.
     *
     * @return Porcentaje pagado (0-100)
     */
    public Double getPorcentajePagado() {
        if (total == null || total.compareTo(BigDecimal.ZERO) == 0) {
            return 0.0;
        }
        return montoPagado.divide(total, 4, java.math.RoundingMode.HALF_UP)
            .multiply(new BigDecimal("100"))
            .doubleValue();
    }

    /**
     * Obtiene la cantidad de detalles.
     *
     * @return Cantidad de detalles
     */
    public int getCantidadDetalles() {
        return detalles != null ? detalles.size() : 0;
    }

    /**
     * Obtiene la cantidad de pagos.
     *
     * @return Cantidad de pagos
     */
    public int getCantidadPagos() {
        return pagos != null ? pagos.size() : 0;
    }

    // ===================================================================
    // EQUALS Y HASHCODE
    // ===================================================================

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Factura factura)) return false;
        return idFactura != null && idFactura.equals(factura.idFactura);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
