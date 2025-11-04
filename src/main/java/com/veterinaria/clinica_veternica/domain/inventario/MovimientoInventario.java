package com.veterinaria.clinica_veternica.domain.inventario;

import com.veterinaria.clinica_veternica.domain.usuario.Usuario;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entidad que representa un Movimiento de Inventario.
 *
 * Registra todas las entradas y salidas de insumos del inventario,
 * permitiendo trazabilidad completa y auditoría.
 *
 * Tipos de movimiento:
 * - ENTRADA: Compra, donación, devolución
 * - SALIDA: Uso en servicio, venta, pérdida, vencimiento
 * - AJUSTE: Corrección de inventario
 *
 * Implementa el patrón Command para operaciones reversibles.
 *
 * @author Clínica Veterinaria Team
 * @version 1.0
 * @since 2025-11-02
 */
@Entity
@Table(name = "movimientos_inventario",
       indexes = {
           @Index(name = "idx_movimiento_insumo", columnList = "id_insumo"),
           @Index(name = "idx_movimiento_fecha", columnList = "fecha_movimiento"),
           @Index(name = "idx_movimiento_tipo", columnList = "tipo_movimiento"),
           @Index(name = "idx_movimiento_usuario", columnList = "id_usuario")
       })
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"insumo", "usuario"})
public class MovimientoInventario {

    /**
     * Identificador único del movimiento.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idMovimiento;

    /**
     * Insumo relacionado con el movimiento.
     * Relación Many-to-One con Insumo.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_insumo", nullable = false)
    @NotNull(message = "El insumo es obligatorio")
    private Insumo insumo;

    /**
     * Tipo de movimiento.
     */
    @NotBlank(message = "El tipo de movimiento es obligatorio")
    @Pattern(regexp = "^(ENTRADA|SALIDA|AJUSTE)$", message = "Tipo inválido. Use: ENTRADA, SALIDA, AJUSTE")
    @Column(name = "tipo_movimiento", nullable = false, length = 20)
    private String tipoMovimiento;

    /**
     * Motivo específico del movimiento.
     * Para ENTRADA: COMPRA, DONACION, DEVOLUCION
     * Para SALIDA: USO_SERVICIO, VENTA, PERDIDA, VENCIMIENTO
     * Para AJUSTE: INVENTARIO, CORRECCION
     */
    @NotBlank(message = "El motivo es obligatorio")
    @Size(max = 50, message = "El motivo no puede exceder 50 caracteres")
    @Column(nullable = false, length = 50)
    private String motivo;

    /**
     * Cantidad del movimiento.
     * Positiva para entradas y ajustes positivos, negativa para salidas y ajustes negativos.
     */
    @NotNull(message = "La cantidad es obligatoria")
    @Column(nullable = false)
    private Integer cantidad;

    /**
     * Stock anterior antes del movimiento.
     */
    @NotNull(message = "El stock anterior es obligatorio")
    @Column(nullable = false)
    private Integer stockAnterior;

    /**
     * Stock nuevo después del movimiento.
     */
    @NotNull(message = "El stock nuevo es obligatorio")
    @Column(nullable = false)
    private Integer stockNuevo;

    /**
     * Precio unitario en el momento del movimiento.
     */
    @DecimalMin(value = "0.0", message = "El precio no puede ser negativo")
    @Column
    private BigDecimal precioUnitario;

    /**
     * Costo total del movimiento (precio * cantidad).
     */
    @DecimalMin(value = "0.0", message = "El costo total no puede ser negativo")
    @Column
    private BigDecimal costoTotal;

    /**
     * Número de factura o documento asociado (si aplica).
     */
    @Size(max = 50, message = "El número de documento no puede exceder 50 caracteres")
    @Column(length = 50)
    private String numeroDocumento;

    /**
     * Lote del insumo en este movimiento.
     */
    @Size(max = 50, message = "El lote no puede exceder 50 caracteres")
    @Column(length = 50)
    private String lote;

    /**
     * Fecha de vencimiento del lote (si aplica).
     */
    @Column
    private java.time.LocalDate fechaVencimiento;

    /**
     * Usuario que realizó el movimiento.
     * Relación Many-to-One con Usuario.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;

    /**
     * Observaciones del movimiento.
     */
    @Size(max = 500, message = "Las observaciones no pueden exceder 500 caracteres")
    @Column(length = 500)
    private String observaciones;

    /**
     * Fecha y hora del movimiento.
     */
    @NotNull(message = "La fecha del movimiento es obligatoria")
    @Column(nullable = false)
    @Builder.Default
    private LocalDateTime fechaMovimiento = LocalDateTime.now();

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
     * Calcula el costo total del movimiento.
     * Se ejecuta antes de persistir o actualizar.
     */
    @PrePersist
    @PreUpdate
    public void calcularCostoTotal() {
        if (precioUnitario != null && cantidad != null) {
            this.costoTotal = precioUnitario.multiply(BigDecimal.valueOf(Math.abs(cantidad)));
        }
    }

    /**
     * Verifica si es un movimiento de entrada.
     *
     * @return true si es entrada
     */
    public boolean esEntrada() {
        return "ENTRADA".equals(tipoMovimiento);
    }

    /**
     * Verifica si es un movimiento de salida.
     *
     * @return true si es salida
     */
    public boolean esSalida() {
        return "SALIDA".equals(tipoMovimiento);
    }

    /**
     * Verifica si es un ajuste.
     *
     * @return true si es ajuste
     */
    public boolean esAjuste() {
        return "AJUSTE".equals(tipoMovimiento);
    }

    /**
     * Verifica si el movimiento fue por vencimiento.
     *
     * @return true si fue por vencimiento
     */
    public boolean esPorVencimiento() {
        return "VENCIMIENTO".equals(motivo);
    }

    /**
     * Verifica si el movimiento fue por pérdida.
     *
     * @return true si fue por pérdida
     */
    public boolean esPorPerdida() {
        return "PERDIDA".equals(motivo);
    }

    /**
     * Obtiene el nombre del usuario que realizó el movimiento.
     *
     * @return Nombre del usuario o "Sistema" si no hay usuario
     */
    public String getNombreUsuario() {
        return usuario != null ? usuario.getNombreCompleto() : "Sistema";
    }

    /**
     * Obtiene un resumen del movimiento.
     *
     * @return Resumen del movimiento
     */
    public String getResumen() {
        String simbolo = esEntrada() ? "+" : "-";
        return String.format("%s %s%d %s - %s",
            tipoMovimiento,
            simbolo,
            Math.abs(cantidad),
            insumo != null ? insumo.getNombre() : "Insumo desconocido",
            motivo);
    }

    /**
     * Verifica si el movimiento requiere aprobación (grandes cantidades o costos altos).
     *
     * @return true si requiere aprobación
     */
    public boolean requiereAprobacion() {
        if (costoTotal == null) {
            return false;
        }
        // Movimientos mayores a 1000 o con más de 100 unidades requieren aprobación
        return costoTotal.compareTo(new BigDecimal("1000")) > 0 ||
               (cantidad != null && Math.abs(cantidad) > 100);
    }

    // ===================================================================
    // EQUALS Y HASHCODE
    // ===================================================================

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MovimientoInventario that)) return false;
        return idMovimiento != null && idMovimiento.equals(that.idMovimiento);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
