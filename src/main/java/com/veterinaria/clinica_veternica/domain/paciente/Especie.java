package com.veterinaria.clinica_veternica.domain.paciente;

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
 * Entidad que representa una Especie animal.
 *
 * Catálogo de especies de animales que la clínica puede atender.
 * Ejemplos: Perro, Gato, Ave, Conejo, Hamster, Reptil, etc.
 *
 * Una especie puede tener múltiples razas asociadas.
 *
 * Esta entidad se cachea frecuentemente ya que cambia muy poco.
 *
 * @author Clínica Veterinaria Team
 * @version 1.0
 * @since 2025-11-02
 */
@Entity
@Table(name = "especies",
       indexes = {
           @Index(name = "idx_especie_nombre", columnList = "nombre")
       })
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = "razas")
public class Especie {

    /**
     * Identificador único de la especie.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idEspecie;

    /**
     * Nombre de la especie.
     * Debe ser único en el sistema.
     */
    @NotBlank(message = "El nombre de la especie es obligatorio")
    @Size(min = 2, max = 50, message = "El nombre debe tener entre 2 y 50 caracteres")
    @Column(nullable = false, unique = true, length = 50)
    private String nombre;

    /**
     * Descripción de la especie.
     */
    @Size(max = 200, message = "La descripción no puede exceder 200 caracteres")
    @Column(length = 200)
    private String descripcion;

    /**
     * Indica si la especie está activa.
     * Especies inactivas no aparecen en selección de nuevas mascotas.
     */
    @Column(nullable = false)
    @Builder.Default
    private Boolean activo = true;

    /**
     * Razas pertenecientes a esta especie.
     */
    @OneToMany(mappedBy = "especie", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Raza> razas = new ArrayList<>();

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
     * Agrega una raza a la especie.
     *
     * @param raza Raza a agregar
     */
    public void agregarRaza(Raza raza) {
        razas.add(raza);
        raza.setEspecie(this);
    }

    /**
     * Elimina una raza de la especie.
     *
     * @param raza Raza a eliminar
     */
    public void eliminarRaza(Raza raza) {
        razas.remove(raza);
        raza.setEspecie(null);
    }

    /**
     * Activa la especie.
     */
    public void activar() {
        this.activo = true;
    }

    /**
     * Desactiva la especie.
     */
    public void desactivar() {
        this.activo = false;
    }

    /**
     * Obtiene el número de razas asociadas.
     *
     * @return Cantidad de razas
     */
    public int getCantidadRazas() {
        return razas != null ? razas.size() : 0;
    }

    // ===================================================================
    // EQUALS Y HASHCODE
    // ===================================================================

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Especie especie)) return false;
        return idEspecie != null && idEspecie.equals(especie.idEspecie);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
