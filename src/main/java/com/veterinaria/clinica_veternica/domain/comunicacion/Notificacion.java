package com.veterinaria.clinica_veternica.domain.comunicacion;

import com.veterinaria.clinica_veternica.domain.usuario.Usuario;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * Entidad que representa una Notificación.
 *
 * Sistema general de notificaciones para comunicar eventos importantes
 * a usuarios del sistema (propietarios, veterinarios, administradores).
 *
 * Tipos de notificación:
 * - CITA: Relacionadas con citas
 * - PAGO: Relacionadas con pagos y facturas
 * - INVENTARIO: Alertas de stock
 * - SISTEMA: Notificaciones generales del sistema
 *
 * Canales de envío:
 * - EMAIL, SMS, WHATSAPP, PUSH
 *
 * Implementa el patrón Observer para envío automático.
 * Implementa el patrón Abstract Factory para creación multi-canal.
 *
 * @author Clínica Veterinaria Team
 * @version 1.0
 * @since 2025-11-02
 */
@Entity
@Table(name = "notificaciones",
       indexes = {
           @Index(name = "idx_notif_destinatario", columnList = "destinatario_email"),
           @Index(name = "idx_notif_tipo", columnList = "tipo"),
           @Index(name = "idx_notif_canal", columnList = "canal"),
           @Index(name = "idx_notif_enviada", columnList = "enviada"),
           @Index(name = "idx_notif_fecha", columnList = "fecha_creacion")
       })
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = "usuario")
public class Notificacion {

    /**
     * Identificador único de la notificación.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idNotificacion;

    /**
     * Tipo de notificación.
     * Valores: CITA, PAGO, INVENTARIO, SISTEMA, RECORDATORIO
     */
    @NotBlank(message = "El tipo es obligatorio")
    @Size(max = 30, message = "El tipo no puede exceder 30 caracteres")
    @Column(nullable = false, length = 30)
    private String tipo;

    /**
     * Canal de envío de la notificación.
     * Valores: EMAIL, SMS, WHATSAPP, PUSH
     */
    @NotBlank(message = "El canal es obligatorio")
    @Size(max = 20, message = "El canal no puede exceder 20 caracteres")
    @Column(nullable = false, length = 20)
    private String canal;

    /**
     * Nombre del destinatario.
     */
    @NotBlank(message = "El nombre del destinatario es obligatorio")
    @Size(max = 200, message = "El nombre no puede exceder 200 caracteres")
    @Column(nullable = false, length = 200)
    private String destinatarioNombre;

    /**
     * Email del destinatario.
     */
    @Email(message = "Formato de email inválido")
    @Size(max = 100, message = "El email no puede exceder 100 caracteres")
    @Column(length = 100)
    private String destinatarioEmail;

    /**
     * Teléfono del destinatario (para SMS/WhatsApp).
     */
    @Pattern(regexp = "^[+]?[0-9]{7,15}$", message = "Formato de teléfono inválido")
    @Column(length = 15)
    private String destinatarioTelefono;

    /**
     * Asunto de la notificación.
     */
    @NotBlank(message = "El asunto es obligatorio")
    @Size(max = 200, message = "El asunto no puede exceder 200 caracteres")
    @Column(nullable = false, length = 200)
    private String asunto;

    /**
     * Mensaje o contenido de la notificación.
     */
    @NotBlank(message = "El mensaje es obligatorio")
    @Size(min = 10, max = 2000, message = "El mensaje debe tener entre 10 y 2000 caracteres")
    @Column(nullable = false, length = 2000)
    private String mensaje;

    /**
     * Prioridad de la notificación.
     * Valores: BAJA, NORMAL, ALTA, URGENTE
     */
    @NotBlank(message = "La prioridad es obligatoria")
    @Column(nullable = false, length = 20)
    @Builder.Default
    private String prioridad = "NORMAL";

    /**
     * ID de la entidad relacionada (idCita, idFactura, etc.).
     */
    @Column
    private Long idEntidadRelacionada;

    /**
     * Tipo de entidad relacionada.
     */
    @Size(max = 50, message = "El tipo de entidad no puede exceder 50 caracteres")
    @Column(length = 50)
    private String tipoEntidadRelacionada;

    /**
     * Indica si la notificación fue enviada exitosamente.
     */
    @Column(nullable = false)
    @Builder.Default
    private Boolean enviada = false;

    /**
     * Fecha y hora de envío.
     */
    @Column
    private LocalDateTime fechaEnvio;

    /**
     * Indica si la notificación fue leída.
     */
    @Column(nullable = false)
    @Builder.Default
    private Boolean leida = false;

    /**
     * Fecha y hora de lectura.
     */
    @Column
    private LocalDateTime fechaLectura;

    /**
     * Número de intentos de envío.
     */
    @Column(nullable = false)
    @Builder.Default
    private Integer intentosEnvio = 0;

    /**
     * Máximo número de intentos permitidos.
     */
    @Column(nullable = false)
    @Builder.Default
    private Integer maxIntentos = 3;

    /**
     * Mensaje de error (si falló el envío).
     */
    @Size(max = 1000, message = "El mensaje de error no puede exceder 1000 caracteres")
    @Column(length = 1000)
    private String mensajeError;

    /**
     * ID externo del proveedor (MessageID, TransactionID, etc.).
     */
    @Size(max = 100, message = "El ID externo no puede exceder 100 caracteres")
    @Column(length = 100)
    private String idExterno;

    /**
     * Usuario que generó la notificación (si aplica).
     * Relación Many-to-One con Usuario.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;

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
     * Marca la notificación como enviada.
     *
     * @param idExterno ID externo del proveedor
     */
    public void marcarComoEnviada(String idExterno) {
        this.enviada = true;
        this.fechaEnvio = LocalDateTime.now();
        this.idExterno = idExterno;
        this.mensajeError = null;
    }

    /**
     * Registra un fallo en el envío.
     *
     * @param error Mensaje de error
     */
    public void registrarFalloEnvio(String error) {
        this.intentosEnvio++;
        this.mensajeError = error;
        this.enviada = false;
    }

    /**
     * Verifica si se agotaron los intentos de envío.
     *
     * @return true si se agotaron los intentos
     */
    public boolean seAgotaronIntentos() {
        return intentosEnvio >= maxIntentos;
    }

    /**
     * Verifica si se puede reintentar el envío.
     *
     * @return true si se puede reintentar
     */
    public boolean puedeReintentar() {
        return !enviada && intentosEnvio < maxIntentos;
    }

    /**
     * Marca la notificación como leída.
     */
    public void marcarComoLeida() {
        if (!this.leida) {
            this.leida = true;
            this.fechaLectura = LocalDateTime.now();
        }
    }

    /**
     * Verifica si es una notificación de email.
     *
     * @return true si es email
     */
    public boolean esEmail() {
        return "EMAIL".equalsIgnoreCase(canal);
    }

    /**
     * Verifica si es una notificación de SMS.
     *
     * @return true si es SMS
     */
    public boolean esSMS() {
        return "SMS".equalsIgnoreCase(canal);
    }

    /**
     * Verifica si es una notificación de WhatsApp.
     *
     * @return true si es WhatsApp
     */
    public boolean esWhatsApp() {
        return "WHATSAPP".equalsIgnoreCase(canal);
    }

    /**
     * Verifica si es una notificación push.
     *
     * @return true si es push
     */
    public boolean esPush() {
        return "PUSH".equalsIgnoreCase(canal);
    }

    /**
     * Verifica si es urgente.
     *
     * @return true si es urgente
     */
    public boolean esUrgente() {
        return "URGENTE".equalsIgnoreCase(prioridad);
    }

    /**
     * Verifica si es de alta prioridad.
     *
     * @return true si es alta o urgente
     */
    public boolean esAltaPrioridad() {
        return "ALTA".equalsIgnoreCase(prioridad) || esUrgente();
    }

    /**
     * Obtiene el tiempo transcurrido desde la creación (en minutos).
     *
     * @return Minutos transcurridos
     */
    public long getMinutosDesdeCreacion() {
        if (fechaCreacion == null) {
            return 0;
        }
        return java.time.Duration.between(fechaCreacion, LocalDateTime.now()).toMinutes();
    }

    /**
     * Verifica si la notificación está pendiente de envío.
     *
     * @return true si está pendiente
     */
    public boolean estaPendiente() {
        return !enviada && puedeReintentar();
    }

    /**
     * Obtiene un resumen de la notificación.
     *
     * @return Resumen
     */
    public String getResumen() {
        return String.format("%s - %s - %s: %s",
            tipo,
            canal,
            destinatarioNombre,
            asunto);
    }

    // ===================================================================
    // EQUALS Y HASHCODE
    // ===================================================================

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Notificacion that)) return false;
        return idNotificacion != null && idNotificacion.equals(that.idNotificacion);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
