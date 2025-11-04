package com.veterinaria.clinica_veternica.domain.inventario;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidad que representa un Tipo de Insumo.
 *
 * Clasificación de insumos para organizar el inventario.
 * Ejemplos: Medicamentos, Material Quirúrgico, Alimentos, Productos de Limpieza, etc.
 *
 * @author Clínica Veterinaria Team
 * @version 1.0
 * @since 2025-11-02
 */
@Entity
@Table(name = "tipos_insumo",
       indexes = {
           @Index(name = "idx_tipo_insumo_nombre", columnList = "nombre")
       },
       uniqueConstraints = {
           @UniqueConstraint(name = "uk_tipo_insumo_nombre", columnNames = "nombre")
       })
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = "insumos")
public class TipoInsumo {

    /**
     * Identificador único del tipo de insumo.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idTipoInsumo;

    /**
     * Nombre del tipo de insumo.
     */
    @NotBlank(message = "El nombre del tipo de insumo es obligatorio")
    @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres")
    @Column(nullable = false, unique = true, length = 100)
    private String nombre;

    /**
     * Descripción del tipo de insumo.
     */
    @Size(max = 500, message = "La descripción no puede exceder 500 caracteres")
    @Column(length = 500)
    private String descripcion;

    /**
     * Indica si el tipo requiere control especial (por ejemplo, medicamentos controlados).
     */
    @Column(nullable = false)
    @Builder.Default
    private Boolean requiereControlEspecial = false;

    /**
     * Indica si el tipo está activo.
     */
    @Column(nullable = false)
    @Builder.Default
    private Boolean activo = true;

    /**
     * Insumos de este tipo.
     * Relación One-to-Many con Insumo.
     */
    @OneToMany(mappedBy = "tipoInsumo", cascade = CascadeType.ALL)
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
     * Agrega un insumo al tipo.
     *
     * @param insumo Insumo a agregar
     */
    public void agregarInsumo(Insumo insumo) {
        insumos.add(insumo);
        insumo.setTipoInsumo(this);
    }

    /**
     * Elimina un insumo del tipo.
     *
     * @param insumo Insumo a eliminar
     */
    public void eliminarInsumo(Insumo insumo) {
        insumos.remove(insumo);
        insumo.setTipoInsumo(null);
    }

    /**
     * Obtiene la cantidad de insumos de este tipo.
     *
     * @return Cantidad de insumos
     */
    public int getCantidadInsumos() {
        return insumos != null ? insumos.size() : 0;
    }

    /**
     * Activa el tipo de insumo.
     */
    public void activar() {
        this.activo = true;
    }

    /**
     * Desactiva el tipo de insumo.
     */
    public void desactivar() {
        this.activo = false;
    }

    // ===================================================================
    // EQUALS Y HASHCODE
    // ===================================================================

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TipoInsumo that)) return false;
        return idTipoInsumo != null && idTipoInsumo.equals(that.idTipoInsumo);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
