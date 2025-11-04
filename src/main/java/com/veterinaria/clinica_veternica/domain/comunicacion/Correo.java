package com.veterinaria.clinica_veternica.domain.comunicacion;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * Entidad que representa un Correo Electrónico enviado.
 *
 * Log de todos los correos enviados desde el sistema para auditoría y seguimiento.
 * Almacena el historial completo de comunicaciones por email.
 *
 * @author Clínica Veterinaria Team
 * @version 1.0
 * @since 2025-11-02
 */
@Entity
@Table(name = "correos",
       indexes = {
           @Index(name = "idx_correo_destinatario", columnList = "destinatario"),
           @Index(name = "idx_correo_fecha", columnList = "fecha_envio"),
           @Index(name = "idx_correo_enviado", columnList = "enviado"),
           @Index(name = "idx_correo_tipo", columnList = "tipo_correo")
       })
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class Correo {

    /**
     * Identificador único del correo.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idCorreo;

    /**
     * Tipo de correo.
     * Valores: RECORDATORIO, CONFIRMACION, FACTURA, NOTIFICACION, MARKETING, SISTEMA
     */
    @NotBlank(message = "El tipo es obligatorio")
    @Size(max = 30, message = "El tipo no puede exceder 30 caracteres")
    @Column(name = "tipo_correo", nullable = false, length = 30)
    private String tipoCorreo;

    /**
     * Email del remitente.
     */
    @NotBlank(message = "El remitente es obligatorio")
    @Email(message = "Formato de email inválido")
    @Size(max = 100, message = "El email no puede exceder 100 caracteres")
    @Column(nullable = false, length = 100)
    private String remitente;

    /**
     * Nombre del remitente.
     */
    @Size(max = 150, message = "El nombre del remitente no puede exceder 150 caracteres")
    @Column(length = 150)
    private String nombreRemitente;

    /**
     * Email del destinatario.
     */
    @NotBlank(message = "El destinatario es obligatorio")
    @Email(message = "Formato de email inválido")
    @Size(max = 100, message = "El email no puede exceder 100 caracteres")
    @Column(nullable = false, length = 100)
    private String destinatario;

    /**
     * Nombre del destinatario.
     */
    @Size(max = 150, message = "El nombre del destinatario no puede exceder 150 caracteres")
    @Column(length = 150)
    private String nombreDestinatario;

    /**
     * Emails en copia (CC) - separados por coma.
     */
    @Size(max = 500, message = "Los destinatarios CC no pueden exceder 500 caracteres")
    @Column(length = 500)
    private String destinatariosCC;

    /**
     * Emails en copia oculta (BCC) - separados por coma.
     */
    @Size(max = 500, message = "Los destinatarios BCC no pueden exceder 500 caracteres")
    @Column(length = 500)
    private String destinatariosBCC;

    /**
     * Asunto del correo.
     */
    @NotBlank(message = "El asunto es obligatorio")
    @Size(min = 3, max = 200, message = "El asunto debe tener entre 3 y 200 caracteres")
    @Column(nullable = false, length = 200)
    private String asunto;

    /**
     * Cuerpo del correo en texto plano.
     */
    @Size(max = 5000, message = "El cuerpo no puede exceder 5000 caracteres")
    @Column(length = 5000)
    private String cuerpoTexto;

    /**
     * Cuerpo del correo en HTML.
     */
    @Size(max = 10000, message = "El cuerpo HTML no puede exceder 10000 caracteres")
    @Column(length = 10000)
    private String cuerpoHTML;

    /**
     * Archivos adjuntos (nombres separados por coma).
     */
    @Size(max = 500, message = "Los adjuntos no pueden exceder 500 caracteres")
    @Column(length = 500)
    private String adjuntos;

    /**
     * Tamaño total de adjuntos en bytes.
     */
    @Column
    private Long tamanioAdjuntos;

    /**
     * Prioridad del correo.
     * Valores: BAJA, NORMAL, ALTA
     */
    @NotBlank(message = "La prioridad es obligatoria")
    @Column(nullable = false, length = 20)
    @Builder.Default
    private String prioridad = "NORMAL";

    /**
     * Indica si el correo fue enviado exitosamente.
     */
    @Column(nullable = false)
    @Builder.Default
    private Boolean enviado = false;

    /**
     * Fecha y hora de envío.
     */
    @Column
    private LocalDateTime fechaEnvio;

    /**
     * Indica si el correo fue abierto/leído.
     */
    @Column(nullable = false)
    @Builder.Default
    private Boolean abierto = false;

    /**
     * Fecha y hora de apertura.
     */
    @Column
    private LocalDateTime fechaApertura;

    /**
     * Número de veces que se abrió el correo.
     */
    @Column(nullable = false)
    @Builder.Default
    private Integer vecesAbierto = 0;

    /**
     * Indica si hubo clicks en enlaces del correo.
     */
    @Column(nullable = false)
    @Builder.Default
    private Boolean huboClic = false;

    /**
     * Fecha del primer clic.
     */
    @Column
    private LocalDateTime fechaPrimerClic;

    /**
     * Número de intentos de envío.
     */
    @Column(nullable = false)
    @Builder.Default
    private Integer intentosEnvio = 0;

    /**
     * Máximo número de intentos.
     */
    @Column(nullable = false)
    @Builder.Default
    private Integer maxIntentos = 3;

    /**
     * Mensaje de error (si falló).
     */
    @Size(max = 1000, message = "El mensaje de error no puede exceder 1000 caracteres")
    @Column(length = 1000)
    private String mensajeError;

    /**
     * ID del mensaje en el servidor SMTP o proveedor.
     */
    @Size(max = 200, message = "El ID del mensaje no puede exceder 200 caracteres")
    @Column(length = 200)
    private String idMensaje;

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
     * Proveedor de envío (SMTP, SendGrid, AWS SES, etc.).
     */
    @Size(max = 50, message = "El proveedor no puede exceder 50 caracteres")
    @Column(length = 50)
    private String proveedor;

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
     * Marca el correo como enviado.
     *
     * @param idMensaje ID del mensaje del proveedor
     */
    public void marcarComoEnviado(String idMensaje) {
        this.enviado = true;
        this.fechaEnvio = LocalDateTime.now();
        this.idMensaje = idMensaje;
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
        this.enviado = false;
    }

    /**
     * Registra la apertura del correo.
     */
    public void registrarApertura() {
        if (!this.abierto) {
            this.abierto = true;
            this.fechaApertura = LocalDateTime.now();
        }
        this.vecesAbierto++;
    }

    /**
     * Registra un clic en el correo.
     */
    public void registrarClic() {
        if (!this.huboClic) {
            this.huboClic = true;
            this.fechaPrimerClic = LocalDateTime.now();
        }
    }

    /**
     * Verifica si se agotaron los intentos.
     *
     * @return true si se agotaron
     */
    public boolean seAgotaronIntentos() {
        return intentosEnvio >= maxIntentos;
    }

    /**
     * Verifica si se puede reintentar.
     *
     * @return true si se puede reintentar
     */
    public boolean puedeReintentar() {
        return !enviado && intentosEnvio < maxIntentos;
    }

    /**
     * Verifica si tiene adjuntos.
     *
     * @return true si tiene adjuntos
     */
    public boolean tieneAdjuntos() {
        return adjuntos != null && !adjuntos.isBlank();
    }

    /**
     * Verifica si es correo HTML.
     *
     * @return true si tiene cuerpo HTML
     */
    public boolean esHTML() {
        return cuerpoHTML != null && !cuerpoHTML.isBlank();
    }

    /**
     * Verifica si el correo fue exitoso (enviado y abierto).
     *
     * @return true si fue exitoso
     */
    public boolean fueExitoso() {
        return enviado && abierto;
    }

    /**
     * Calcula la tasa de apertura (0-100).
     *
     * @return Tasa de apertura
     */
    public Double getTasaApertura() {
        if (!enviado) {
            return 0.0;
        }
        return abierto ? 100.0 : 0.0;
    }

    /**
     * Calcula el tiempo hasta la apertura (en minutos).
     *
     * @return Minutos hasta la apertura, o null si no se abrió
     */
    public Long getMinutosHastaApertura() {
        if (fechaEnvio == null || fechaApertura == null) {
            return null;
        }
        return java.time.Duration.between(fechaEnvio, fechaApertura).toMinutes();
    }

    /**
     * Verifica si es de alta prioridad.
     *
     * @return true si es alta
     */
    public boolean esAltaPrioridad() {
        return "ALTA".equalsIgnoreCase(prioridad);
    }

    /**
     * Obtiene un resumen del correo.
     *
     * @return Resumen
     */
    public String getResumen() {
        String estado = enviado ? (abierto ? "Leído" : "Enviado") : "Pendiente";
        return String.format("%s - %s - %s: %s",
            tipoCorreo,
            destinatario,
            estado,
            asunto);
    }

    // ===================================================================
    // EQUALS Y HASHCODE
    // ===================================================================

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Correo correo)) return false;
        return idCorreo != null && idCorreo.equals(correo.idCorreo);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
