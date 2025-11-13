package com.veterinaria.clinica_veternica.domain.usuario;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 *
 * Representa la información común de empleados y personal de la clínica veterinaria.
 * Utiliza herencia con estrategia JOINED para mantener datos específicos en tablas separadas.
 *
 * Subclases:
 * - Veterinario: Profesional veterinario con licencia
 * - Administrador: Personal administrativo
 * - Recepcionista: Personal de recepción
 * - AuxiliarVeterinario: Asistente veterinario
 *
 * Patrón de diseño:
 * - Template Method: Define estructura común, subclases implementan detalles específicos
 *
 * @author Clínica Veterinaria Team
 * @version 1.0
 * @since 2025-11-02
 */
@Entity
@Table(name = "personal",
       indexes = {
           @Index(name = "idx_personal_documento", columnList = "documento"),
           @Index(name = "idx_personal_email", columnList = "correo"),
           @Index(name = "idx_personal_tipo", columnList = "tipo_personal")
       })
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "tipo_personal", discriminatorType = DiscriminatorType.STRING)
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@ToString
public abstract class Personal {

    /**
     * Identificador único del personal.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idPersonal;

    /**
     * Nombres del personal.
     */
    @NotBlank(message = "Los nombres son obligatorios")
    @Size(min = 2, max = 100, message = "Los nombres deben tener entre 2 y 100 caracteres")
    @Column(nullable = false, length = 100)
    private String nombres;

    /**
     * Apellidos del personal.
     */
    @NotBlank(message = "Los apellidos son obligatorios")
    @Size(min = 2, max = 100, message = "Los apellidos deben tener entre 2 y 100 caracteres")
    @Column(nullable = false, length = 100)
    private String apellidos;

    /**
     * Documento de identidad único.
     */
    @NotBlank(message = "El documento es obligatorio")
    @Size(min = 6, max = 20, message = "El documento debe tener entre 6 y 20 caracteres")
    @Column(nullable = false, unique = true, length = 20)
    private String documento;

    /**
     * Correo electrónico del personal.
     */
    @NotBlank(message = "El correo es obligatorio")
    @Email(message = "El correo debe ser válido")
    @Column(nullable = false, unique = true, length = 100)
    private String correo;

    /**
     * Número de teléfono de contacto.
     */
    @NotBlank(message = "El teléfono es obligatorio")
    @Pattern(regexp = "^\\+?\\d{10,13}$", message = "El teléfono debe ser válido")
    @Column(nullable = false, length = 15)
    private String telefono;

    /**
     * Dirección de residencia.
     */
    @Size(max = 200, message = "La dirección no puede exceder 200 caracteres")
    @Column(length = 200)
    private String direccion;

    /**
     * Estado activo/inactivo del personal.
     */
    @Column(nullable = false)
    @Builder.Default
    private Boolean activo = true;

    /**
     * Usuario asociado para acceso al sistema.
     */
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", referencedColumnName = "idUsuario")
    private Usuario usuario;

    /**
     * Fecha y hora de contratación.
     */
    @Column(nullable = false)
    @Builder.Default
    private LocalDateTime fechaContratacion = LocalDateTime.now();

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

    /**
     * Observaciones adicionales sobre el personal.
     */
    @Column(columnDefinition = "TEXT")
    private String observaciones;

    // ===================================================================
    // MÉTODOS DE NEGOCIO
    // ===================================================================

    /**
     * Obtiene el nombre completo del personal.
     *
     * @return Nombres y apellidos concatenados
     */
    public String getNombreCompleto() {
        return nombres + " " + apellidos;
    }

    /**
     * Activa el personal.
     */
    public void activar() {
        this.activo = true;
        if (usuario != null) {
            usuario.activar();
        }
    }

    /**
     * Desactiva el personal.
     */
    public void desactivar() {
        this.activo = false;
        if (usuario != null) {
            usuario.desactivar();
        }
    }

    /**
     * Verifica si el personal está activo.
     *
     * @return true si está activo
     */
    public boolean estaActivo() {
        return activo != null && activo;
    }

    /**
     * Obtiene el tipo de personal (método abstracto).
     * Cada subclase debe implementar este método.
     *
     * @return Tipo de personal (Veterinario, Administrador, etc.)
     */
    public abstract String getTipoPersonal();

    /**
     * Verifica si el personal puede realizar una acción específica.
     * Método template que puede ser sobrescrito por subclases.
     *
     * @param accion Acción a verificar
     * @return true si puede realizar la acción
     */
    public boolean puedeRealizarAccion(String accion) {
        return estaActivo() && usuario != null && usuario.puedeIniciarSesion();
    }

    // ===================================================================
    // EQUALS Y HASHCODE
    // ===================================================================

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Personal personal)) return false;
        return idPersonal != null && idPersonal.equals(personal.idPersonal);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
