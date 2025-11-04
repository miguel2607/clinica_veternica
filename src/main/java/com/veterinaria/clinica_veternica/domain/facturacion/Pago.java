package com.veterinaria.clinica_veternica.domain.facturacion;

import com.veterinaria.clinica_veternica.domain.usuario.Usuario;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entidad que representa un Pago realizado a una Factura.
 *
 * Registra los pagos parciales o totales aplicados a una factura.
 * Una factura puede tener múltiples pagos hasta completar el monto total.
 *
 * Implementa el patrón Strategy para diferentes métodos de pago.
 *
 * @author Clínica Veterinaria Team
 * @version 1.0
 * @since 2025-11-02
 */
@Entity
@Table(name = "pagos",
       indexes = {
           @Index(name = "idx_pago_factura", columnList = "id_factura"),
           @Index(name = "idx_pago_fecha", columnList = "fecha_pago"),
           @Index(name = "idx_pago_metodo", columnList = "metodo_pago")
       })
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"factura", "usuario"})
public class Pago {

    /**
     * Identificador único del pago.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idPago;

    /**
     * Factura a la que se aplica este pago.
     * Relación Many-to-One con Factura.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_factura", nullable = false)
    @NotNull(message = "La factura es obligatoria")
    private Factura factura;

    /**
     * Monto del pago.
     */
    @NotNull(message = "El monto es obligatorio")
    @DecimalMin(value = "0.01", message = "El monto debe ser mayor a 0")
    @Column(nullable = false)
    private BigDecimal monto;

    /**
     * Método de pago utilizado.
     */
    @NotNull(message = "El método de pago es obligatorio")
    @Enumerated(EnumType.STRING)
    @Column(name = "metodo_pago", nullable = false, length = 30)
    private MetodoPago metodoPago;

    /**
     * Fecha y hora del pago.
     */
    @NotNull(message = "La fecha de pago es obligatoria")
    @Column(nullable = false)
    @Builder.Default
    private LocalDateTime fechaPago = LocalDateTime.now();

    /**
     * Número de referencia del pago.
     * Puede ser número de cheque, transacción, aprobación de tarjeta, etc.
     */
    @Size(max = 100, message = "La referencia no puede exceder 100 caracteres")
    @Column(length = 100)
    private String numeroReferencia;

    /**
     * Número de autorización (para pagos con tarjeta).
     */
    @Size(max = 50, message = "El número de autorización no puede exceder 50 caracteres")
    @Column(length = 50)
    private String numeroAutorizacion;

    /**
     * Últimos 4 dígitos de la tarjeta (si aplica).
     */
    @Size(max = 4, message = "Los últimos 4 dígitos deben ser exactamente 4")
    @Column(length = 4)
    private String ultimos4Digitos;

    /**
     * Banco emisor (si aplica).
     */
    @Size(max = 100, message = "El banco no puede exceder 100 caracteres")
    @Column(length = 100)
    private String banco;

    /**
     * Nombre del titular de la tarjeta o cuenta (si aplica).
     */
    @Size(max = 150, message = "El titular no puede exceder 150 caracteres")
    @Column(length = 150)
    private String titular;

    /**
     * Comprobante de pago (número de recibo generado).
     */
    @Size(max = 50, message = "El comprobante no puede exceder 50 caracteres")
    @Column(length = 50)
    private String comprobante;

    /**
     * Observaciones del pago.
     */
    @Size(max = 500, message = "Las observaciones no pueden exceder 500 caracteres")
    @Column(length = 500)
    private String observaciones;

    /**
     * Usuario que registró el pago.
     * Relación Many-to-One con Usuario.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;

    /**
     * Indica si el pago fue anulado.
     */
    @Column(nullable = false)
    @Builder.Default
    private Boolean anulado = false;

    /**
     * Fecha de anulación del pago.
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
     * Fecha y hora de creación del registro.
     */
    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    // ===================================================================
    // MÉTODOS DE NEGOCIO
    // ===================================================================

    /**
     * Genera un número de comprobante automáticamente.
     * Formato: PAG-{año}-{timestamp}
     */
    @PrePersist
    public void generarComprobante() {
        if (comprobante == null) {
            this.comprobante = String.format("PAG-%d-%d",
                java.time.Year.now().getValue(),
                System.currentTimeMillis() % 1000000);
        }
    }

    /**
     * Verifica si el pago es válido (no anulado).
     *
     * @return true si es válido
     */
    public boolean esValido() {
        return !anulado;
    }

    /**
     * Verifica si el pago fue con tarjeta.
     *
     * @return true si fue con tarjeta
     */
    public boolean esConTarjeta() {
        return metodoPago == MetodoPago.TARJETA_CREDITO ||
               metodoPago == MetodoPago.TARJETA_DEBITO;
    }

    /**
     * Verifica si el pago fue en efectivo.
     *
     * @return true si fue en efectivo
     */
    public boolean esEfectivo() {
        return metodoPago == MetodoPago.EFECTIVO;
    }

    /**
     * Verifica si el pago fue con transferencia.
     *
     * @return true si fue con transferencia
     */
    public boolean esTransferencia() {
        return metodoPago == MetodoPago.TRANSFERENCIA;
    }

    /**
     * Anula el pago.
     *
     * @param motivo Motivo de la anulación
     */
    public void anular(String motivo) {
        if (this.anulado) {
            throw new IllegalStateException("El pago ya está anulado");
        }
        this.anulado = true;
        this.fechaAnulacion = LocalDateTime.now();
        this.motivoAnulacion = motivo;
    }

    /**
     * Obtiene una descripción del método de pago con detalles.
     *
     * @return Descripción del pago
     */
    public String getDescripcionPago() {
        StringBuilder sb = new StringBuilder();
        sb.append(metodoPago.getDisplayName());

        if (esConTarjeta() && ultimos4Digitos != null) {
            sb.append(" ****").append(ultimos4Digitos);
        }

        if (numeroReferencia != null) {
            sb.append(" - Ref: ").append(numeroReferencia);
        }

        return sb.toString();
    }

    /**
     * Verifica si el pago requiere validación adicional.
     * Pagos mayores a 10000 requieren validación.
     *
     * @return true si requiere validación
     */
    public boolean requiereValidacion() {
        return monto != null && monto.compareTo(new BigDecimal("10000")) > 0;
    }

    /**
     * Obtiene el nombre del usuario que registró el pago.
     *
     * @return Nombre del usuario o "Sistema" si no hay usuario
     */
    public String getNombreUsuario() {
        return usuario != null ? usuario.getNombreCompleto() : "Sistema";
    }

    // ===================================================================
    // EQUALS Y HASHCODE
    // ===================================================================

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Pago pago)) return false;
        return idPago != null && idPago.equals(pago.idPago);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
