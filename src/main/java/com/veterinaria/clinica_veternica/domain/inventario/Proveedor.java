package com.veterinaria.clinica_veternica.domain.inventario;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidad que representa un Proveedor de insumos.
 *
 * Almacena información de los proveedores de la clínica para
 * gestión de compras y órdenes de pedido.
 *
 * @author Clínica Veterinaria Team
 * @version 1.0
 * @since 2025-11-02
 */
@Entity
@Table(name = "proveedores",
       indexes = {
           @Index(name = "idx_proveedor_nombre", columnList = "nombre_empresa"),
           @Index(name = "idx_proveedor_documento", columnList = "documento"),
           @Index(name = "idx_proveedor_activo", columnList = "activo")
       },
       uniqueConstraints = {
           @UniqueConstraint(name = "uk_proveedor_documento", columnNames = "documento"),
           @UniqueConstraint(name = "uk_proveedor_email", columnNames = "email")
       })
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = "insumos")
public class Proveedor {

    /**
     * Identificador único del proveedor.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idProveedor;

    /**
     * Nombre de la empresa proveedora.
     */
    @NotBlank(message = "El nombre de la empresa es obligatorio")
    @Size(min = 2, max = 200, message = "El nombre debe tener entre 2 y 200 caracteres")
    @Column(name = "nombre_empresa", nullable = false, length = 200)
    private String nombreEmpresa;

    /**
     * Documento de identificación (RUC, NIT, etc.).
     */
    @NotBlank(message = "El documento es obligatorio")
    @Size(min = 6, max = 20, message = "El documento debe tener entre 6 y 20 caracteres")
    @Column(nullable = false, unique = true, length = 20)
    private String documento;

    /**
     * Tipo de documento (RUC, NIT, DNI, etc.).
     */
    @Size(max = 30, message = "El tipo de documento no puede exceder 30 caracteres")
    @Column(length = 30)
    private String tipoDocumento;

    /**
     * Nombre de la persona de contacto.
     */
    @Size(max = 150, message = "El nombre de contacto no puede exceder 150 caracteres")
    @Column(length = 150)
    private String nombreContacto;

    /**
     * Teléfono principal.
     */
    @NotBlank(message = "El teléfono es obligatorio")
    @Pattern(regexp = "^[+]?\\d{7,15}$", message = "Formato de teléfono inválido")
    @Column(nullable = false, length = 15)
    private String telefono;

    /**
     * Teléfono secundario.
     */
    @Pattern(regexp = "^[+]?\\d{7,15}$", message = "Formato de teléfono inválido")
    @Column(length = 15)
    private String telefonoSecundario;

    /**
     * Correo electrónico.
     */
    @NotBlank(message = "El email es obligatorio")
    @Email(message = "Formato de email inválido")
    @Size(max = 100, message = "El email no puede exceder 100 caracteres")
    @Column(nullable = false, unique = true, length = 100)
    private String email;

    /**
     * Dirección física.
     */
    @Size(max = 300, message = "La dirección no puede exceder 300 caracteres")
    @Column(length = 300)
    private String direccion;

    /**
     * Ciudad.
     */
    @Size(max = 100, message = "La ciudad no puede exceder 100 caracteres")
    @Column(length = 100)
    private String ciudad;

    /**
     * País.
     */
    @Size(max = 100, message = "El país no puede exceder 100 caracteres")
    @Column(length = 100)
    private String pais;

    /**
     * Sitio web del proveedor.
     */
    @Size(max = 200, message = "El sitio web no puede exceder 200 caracteres")
    @Column(length = 200)
    private String sitioWeb;

    /**
     * Categorías de productos que ofrece.
     */
    @Size(max = 500, message = "Las categorías no pueden exceder 500 caracteres")
    @Column(length = 500)
    private String categoriasProductos;

    /**
     * Condiciones de pago acordadas.
     * Ejemplo: "30 días", "Contado", "15 días".
     */
    @Size(max = 200, message = "Las condiciones de pago no pueden exceder 200 caracteres")
    @Column(length = 200)
    private String condicionesPago;

    /**
     * Tiempo de entrega estimado en días.
     */
    @Min(value = 0, message = "El tiempo de entrega no puede ser negativo")
    @Column
    private Integer tiempoEntregaDias;

    /**
     * Calificación del proveedor (1-5 estrellas).
     */
    @Min(value = 1, message = "La calificación mínima es 1")
    @Max(value = 5, message = "La calificación máxima es 5")
    @Column
    private Integer calificacion;

    /**
     * Observaciones sobre el proveedor.
     */
    @Size(max = 1000, message = "Las observaciones no pueden exceder 1000 caracteres")
    @Column(length = 1000)
    private String observaciones;

    /**
     * Indica si el proveedor está activo.
     */
    @Column(nullable = false)
    @Builder.Default
    private Boolean activo = true;

    /**
     * Insumos suministrados por este proveedor.
     * Relación One-to-Many con Insumo.
     */
    @OneToMany(mappedBy = "proveedor", cascade = CascadeType.ALL)
    @Builder.Default
    private List<Insumo> insumos = new ArrayList<>();

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
     * Agrega un insumo al proveedor.
     *
     * @param insumo Insumo a agregar
     */
    public void agregarInsumo(Insumo insumo) {
        insumos.add(insumo);
        insumo.setProveedor(this);
    }

    /**
     * Elimina un insumo del proveedor.
     *
     * @param insumo Insumo a eliminar
     */
    public void eliminarInsumo(Insumo insumo) {
        insumos.remove(insumo);
        insumo.setProveedor(null);
    }

    /**
     * Obtiene la cantidad de insumos del proveedor.
     *
     * @return Cantidad de insumos
     */
    public int getCantidadInsumos() {
        return insumos != null ? insumos.size() : 0;
    }

    /**
     * Verifica si el proveedor es confiable (calificación >= 4).
     *
     * @return true si es confiable
     */
    public boolean esConfiable() {
        return calificacion != null && calificacion >= 4;
    }

    /**
     * Verifica si tiene entrega rápida (menos de 7 días).
     *
     * @return true si tiene entrega rápida
     */
    public boolean tieneEntregaRapida() {
        return tiempoEntregaDias != null && tiempoEntregaDias <= 7;
    }

    /**
     * Activa el proveedor.
     */
    public void activar() {
        this.activo = true;
    }

    /**
     * Desactiva el proveedor.
     */
    public void desactivar() {
        this.activo = false;
    }

    /**
     * Obtiene información de contacto completa.
     *
     * @return Información de contacto
     */
    public String getInfoContacto() {
        StringBuilder sb = new StringBuilder();
        if (nombreContacto != null) {
            sb.append(nombreContacto).append(" - ");
        }
        sb.append(telefono);
        if (email != null) {
            sb.append(" - ").append(email);
        }
        return sb.toString();
    }

    // ===================================================================
    // EQUALS Y HASHCODE
    // ===================================================================

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Proveedor that)) return false;
        return idProveedor != null && idProveedor.equals(that.idProveedor);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
