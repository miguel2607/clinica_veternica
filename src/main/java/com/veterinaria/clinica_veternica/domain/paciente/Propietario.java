package com.veterinaria.clinica_veternica.domain.paciente;

import com.veterinaria.clinica_veternica.domain.usuario.Usuario;
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
 * Entidad que representa un Propietario de mascotas.
 *
 * Un propietario puede tener múltiples mascotas registradas en la clínica.
 * Contiene información de contacto y datos personales relevantes para la atención veterinaria.
 *
 * @author Clínica Veterinaria Team
 * @version 1.0
 * @since 2025-11-02
 */
@Entity
@Table(name = "propietarios",
       indexes = {
           @Index(name = "idx_propietario_documento", columnList = "documento"),
           @Index(name = "idx_propietario_email", columnList = "email"),
           @Index(name = "idx_propietario_telefono", columnList = "telefono")
       },
       uniqueConstraints = {
           @UniqueConstraint(name = "uk_propietario_documento", columnNames = "documento"),
           @UniqueConstraint(name = "uk_propietario_email", columnNames = "email")
       })
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = "mascotas")
public class Propietario {

    /**
     * Identificador único del propietario.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idPropietario;

    /**
     * Número de documento de identificación (DNI, Cédula, Pasaporte).
     */
    @NotBlank(message = "El documento es obligatorio")
    @Size(min = 6, max = 20, message = "El documento debe tener entre 6 y 20 caracteres")
    @Column(nullable = false, unique = true, length = 20)
    private String documento;

    /**
     * Tipo de documento (DNI, Cédula, Pasaporte, etc.).
     */
    @NotBlank(message = "El tipo de documento es obligatorio")
    @Size(max = 30, message = "El tipo de documento no puede exceder 30 caracteres")
    @Column(nullable = false, length = 30)
    private String tipoDocumento;

    /**
     * Nombres del propietario.
     */
    @NotBlank(message = "Los nombres son obligatorios")
    @Size(min = 2, max = 100, message = "Los nombres deben tener entre 2 y 100 caracteres")
    @Column(nullable = false, length = 100)
    private String nombres;

    /**
     * Apellidos del propietario.
     */
    @NotBlank(message = "Los apellidos son obligatorios")
    @Size(min = 2, max = 100, message = "Los apellidos deben tener entre 2 y 100 caracteres")
    @Column(nullable = false, length = 100)
    private String apellidos;

    /**
     * Teléfono principal de contacto.
     */
    @NotBlank(message = "El teléfono es obligatorio")
    @Pattern(regexp = "^[+]?\\d{7,15}$", message = "Formato de teléfono inválido")
    @Column(nullable = false, length = 15)
    private String telefono;

    /**
     * Correo electrónico del propietario.
     */
    @NotBlank(message = "El email es obligatorio")
    @Email(message = "Formato de email inválido")
    @Size(max = 100, message = "El email no puede exceder 100 caracteres")
    @Column(nullable = false, unique = true, length = 100)
    private String email;

    /**
     * Dirección de residencia completa (incluye ciudad y código postal si aplica).
     */
    @Size(max = 300, message = "La dirección no puede exceder 300 caracteres")
    @Column(length = 300)
    private String direccion;

    /**
     * Observaciones o notas adicionales sobre el propietario.
     */
    @Size(max = 500, message = "Las observaciones no pueden exceder 500 caracteres")
    @Column(length = 500)
    private String observaciones;

    /**
     * Indica si el propietario está activo en el sistema.
     */
    @Column(nullable = false)
    @Builder.Default
    private Boolean activo = true;

    /**
     * Usuario asociado al propietario (si existe).
     * Permite que un propietario tenga credenciales de acceso al sistema.
     * Relación One-to-One con Usuario.
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", unique = true)
    private Usuario usuario;

    /**
     * Mascotas del propietario.
     * Relación One-to-Many con Mascota.
     */
    @OneToMany(mappedBy = "propietario", cascade = CascadeType.ALL, orphanRemoval = true)
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
     * Obtiene el nombre completo del propietario.
     *
     * @return Nombres y apellidos
     */
    public String getNombreCompleto() {
        return nombres + " " + apellidos;
    }

    /**
     * Agrega una mascota al propietario.
     *
     * @param mascota Mascota a agregar
     */
    public void agregarMascota(Mascota mascota) {
        mascotas.add(mascota);
        mascota.setPropietario(this);
    }

    /**
     * Elimina una mascota del propietario.
     *
     * @param mascota Mascota a eliminar
     */
    public void eliminarMascota(Mascota mascota) {
        mascotas.remove(mascota);
        mascota.setPropietario(null);
    }

    /**
     * Obtiene el número de mascotas del propietario.
     *
     * @return Cantidad de mascotas
     */
    public int getCantidadMascotas() {
        return mascotas != null ? mascotas.size() : 0;
    }

    /**
     * Activa el propietario.
     */
    public void activar() {
        this.activo = true;
    }

    /**
     * Desactiva el propietario.
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
        if (!(o instanceof Propietario that)) return false;
        return idPropietario != null && idPropietario.equals(that.idPropietario);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
