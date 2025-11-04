package com.veterinaria.clinica_veternica.domain.paciente;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidad que representa una Raza animal.
 *
 * Catálogo de razas de animales asociadas a una especie.
 * Ejemplos: Golden Retriever, Poodle, Persa (para gatos), etc.
 *
 * Una raza pertenece a una única especie pero puede tener múltiples mascotas.
 *
 * Esta entidad se cachea frecuentemente ya que cambia muy poco.
 *
 * @author Clínica Veterinaria Team
 * @version 1.0
 * @since 2025-11-02
 */
@Entity
@Table(name = "razas",
       indexes = {
           @Index(name = "idx_raza_nombre", columnList = "nombre"),
           @Index(name = "idx_raza_especie", columnList = "id_especie")
       })
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"especie", "mascotas"})
public class Raza {

    /**
     * Identificador único de la raza.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idRaza;

    /**
     * Nombre de la raza.
     */
    @NotBlank(message = "El nombre de la raza es obligatorio")
    @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres")
    @Column(nullable = false, length = 100)
    private String nombre;

    /**
     * Descripción de la raza.
     */
    @Size(max = 300, message = "La descripción no puede exceder 300 caracteres")
    @Column(length = 300)
    private String descripcion;

    /**
     * Características típicas de la raza.
     */
    @Size(max = 500, message = "Las características no pueden exceder 500 caracteres")
    @Column(length = 500)
    private String caracteristicas;

    /**
     * Tamaño típico de la raza (Pequeño, Mediano, Grande, Extra Grande).
     */
    @Size(max = 20, message = "El tamaño no puede exceder 20 caracteres")
    @Column(length = 20)
    private String tamanio;

    /**
     * Peso promedio en kg.
     */
    @Column
    private Double pesoPromedio;

    /**
     * Indica si la raza está activa.
     * Razas inactivas no aparecen en selección de nuevas mascotas.
     */
    @Column(nullable = false)
    @Builder.Default
    private Boolean activo = true;

    /**
     * Especie a la que pertenece esta raza.
     * Relación Many-to-One con Especie.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_especie", nullable = false)
    @NotNull(message = "La especie es obligatoria")
    private Especie especie;

    /**
     * Mascotas de esta raza.
     * Relación One-to-Many con Mascota.
     */
    @OneToMany(mappedBy = "raza", cascade = CascadeType.ALL)
    @Builder.Default
    private List<Mascota> mascotas = new ArrayList<>();

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
     * Activa la raza.
     */
    public void activar() {
        this.activo = true;
    }

    /**
     * Desactiva la raza.
     */
    public void desactivar() {
        this.activo = false;
    }

    /**
     * Agrega una mascota a la raza.
     *
     * @param mascota Mascota a agregar
     */
    public void agregarMascota(Mascota mascota) {
        mascotas.add(mascota);
        mascota.setRaza(this);
    }

    /**
     * Elimina una mascota de la raza.
     *
     * @param mascota Mascota a eliminar
     */
    public void eliminarMascota(Mascota mascota) {
        mascotas.remove(mascota);
        mascota.setRaza(null);
    }

    /**
     * Obtiene el número de mascotas de esta raza.
     *
     * @return Cantidad de mascotas
     */
    public int getCantidadMascotas() {
        return mascotas != null ? mascotas.size() : 0;
    }

    /**
     * Obtiene el nombre completo (Especie - Raza).
     *
     * @return Nombre completo
     */
    public String getNombreCompleto() {
        return (especie != null ? especie.getNombre() : "Sin especie") + " - " + nombre;
    }

    // ===================================================================
    // EQUALS Y HASHCODE
    // ===================================================================

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Raza raza)) return false;
        return idRaza != null && idRaza.equals(raza.idRaza);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
