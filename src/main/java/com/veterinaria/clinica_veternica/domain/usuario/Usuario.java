package com.veterinaria.clinica_veternica.domain.usuario;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * Entidad que representa un usuario del sistema.
 *
 * Almacena las credenciales y configuración de autenticación para acceder al sistema.
 * Se relaciona con Personal o Propietario según el rol.
 *
 * Características:
 * - Credenciales de acceso (username, password)
 * - Rol del usuario para control de acceso
 * - Estado activo/inactivo
 * - Auditoría de fechas de creación y modificación
 *
 * Seguridad:
 * - Password se almacena encriptado con BCrypt
 * - No se expone en respuestas API (se usa DTO)
 *
 * @author Clínica Veterinaria Team
 * @version 1.0
 * @since 2025-11-02
 */
@Entity
@Table(name = "usuarios",
       indexes = {
           @Index(name = "idx_usuario_username", columnList = "username"),
           @Index(name = "idx_usuario_email", columnList = "email"),
           @Index(name = "idx_usuario_rol", columnList = "rol")
       })
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = "password") // Excluir password del toString por seguridad
public class Usuario {

    /**
     * Identificador único del usuario.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idUsuario;

    /**
     * Nombre de usuario único para login.
     * Debe ser único en el sistema.
     */
    @NotBlank(message = "El username es obligatorio")
    @Size(min = 4, max = 50, message = "El username debe tener entre 4 y 50 caracteres")
    @Column(nullable = false, unique = true, length = 50)
    private String username;

    /**
     * Contraseña encriptada del usuario.
     * Se almacena con BCrypt, nunca en texto plano.
     */
    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres")
    @Column(nullable = false)
    private String password;

    /**
     * Email del usuario.
     * Debe ser único y válido.
     */
    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El email debe ser válido")
    @Column(nullable = false, unique = true, length = 100)
    private String email;

    /**
     * Rol del usuario en el sistema.
     * Define los permisos y accesos.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private RolUsuario rol;

    /**
     * Estado del usuario (activo/inactivo).
     * Solo usuarios activos pueden iniciar sesión.
     */
    @Column(nullable = false)
    @Builder.Default
    private Boolean estado = true;

    /**
     * Fecha y hora de creación del usuario.
     * Se establece automáticamente al crear.
     */
    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    /**
     * Fecha y hora de última modificación.
     * Se actualiza automáticamente al modificar.
     */
    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime fechaModificacion;

    /**
     * Fecha y hora del último login.
     * Se actualiza cada vez que el usuario inicia sesión.
     */
    @Column
    private LocalDateTime ultimoLogin;

    /**
     * Número de intentos fallidos de login.
     * Se usa para bloquear cuenta después de X intentos.
     */
    @Column(nullable = false)
    @Builder.Default
    private Integer intentosFallidos = 0;

    /**
     * Indica si la cuenta está bloqueada.
     * Se bloquea automáticamente después de múltiples intentos fallidos.
     */
    @Column(nullable = false)
    @Builder.Default
    private Boolean bloqueado = false;

    /**
     * Fecha y hora hasta la cual está bloqueado el usuario.
     * Si es null o pasada, el usuario no está bloqueado.
     */
    @Column
    private LocalDateTime fechaBloqueo;

    /**
     * Motivo del bloqueo del usuario.
     */
    @Size(max = 500, message = "El motivo de bloqueo no puede exceder 500 caracteres")
    @Column(length = 500)
    private String motivoBloqueo;

    // ===================================================================
    // MÉTODOS DE NEGOCIO
    // ===================================================================

    /**
     * Verifica si el usuario está activo y puede iniciar sesión.
     *
     * @return true si el usuario está activo, no bloqueado y el bloqueo ha expirado
     */
    public boolean puedeIniciarSesion() {
        if (estado == null || !estado || (bloqueado != null && bloqueado)) {
            return false;
        }

        // Verificar si el bloqueo ha expirado
        if (fechaBloqueo != null && LocalDateTime.now().isBefore(fechaBloqueo)) {
            return false;
        }

        // Si el bloqueo expiró, desbloquear automáticamente
        if (fechaBloqueo != null && LocalDateTime.now().isAfter(fechaBloqueo)) {
            desbloquear();
        }

        return true;
    }

    /**
     * Registra un intento fallido de login.
     * Bloquea la cuenta si se excede el número máximo de intentos.
     */
    public void registrarIntentoFallido() {
        intentosFallidos++;

        // Bloquear después de 5 intentos fallidos
        if (intentosFallidos >= 5) {
            bloquear(30); // Bloquear por 30 minutos
        }
    }

    /**
     * Registra un login exitoso.
     * Resetea los intentos fallidos y actualiza último login.
     */
    public void registrarLoginExitoso() {
        intentosFallidos = 0;
        ultimoLogin = LocalDateTime.now();
    }

    /**
     * Bloquea el usuario por un tiempo determinado.
     *
     * @param minutos Número de minutos de bloqueo
     */
    public void bloquear(int minutos) {
        bloqueado = true;
        fechaBloqueo = LocalDateTime.now().plusMinutes(minutos);
    }

    /**
     * Desbloquea el usuario.
     */
    public void desbloquear() {
        bloqueado = false;
        fechaBloqueo = null;
        intentosFallidos = 0;
    }

    /**
     * Activa el usuario.
     */
    public void activar() {
        estado = true;
    }

    /**
     * Desactiva el usuario.
     */
    public void desactivar() {
        estado = false;
    }

    /**
     * Verifica si el usuario tiene un rol específico.
     *
     * @param rolRequerido Rol a verificar
     * @return true si el usuario tiene el rol especificado
     */
    public boolean tieneRol(RolUsuario rolRequerido) {
        return this.rol == rolRequerido;
    }

    /**
     * Verifica si el usuario es administrador.
     *
     * @return true si el rol es ADMIN
     */
    public boolean esAdministrador() {
        return rol == RolUsuario.ADMIN;
    }

    /**
     * Obtiene el nombre completo del usuario (username o email).
     *
     * @return Nombre completo
     */
    public String getNombreCompleto() {
        return username;
    }

    /**
     * Verifica si el usuario es veterinario.
     *
     * @return true si el rol es VETERINARIO
     */
    public boolean esVeterinario() {
        return rol == RolUsuario.VETERINARIO;
    }

    /**
     * Verifica si el usuario es recepcionista.
     *
     * @return true si el rol es RECEPCIONISTA
     */
    public boolean esRecepcionista() {
        return rol == RolUsuario.RECEPCIONISTA;
    }

    // ===================================================================
    // EQUALS Y HASHCODE
    // ===================================================================

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Usuario usuario)) return false;
        return idUsuario != null && idUsuario.equals(usuario.idUsuario);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
