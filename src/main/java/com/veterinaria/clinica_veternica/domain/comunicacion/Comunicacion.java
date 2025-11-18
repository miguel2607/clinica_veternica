package com.veterinaria.clinica_veternica.domain.comunicacion;

import com.veterinaria.clinica_veternica.domain.agenda.Cita;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * Entidad unificada que representa todas las comunicaciones del sistema.
 *
 * Reemplaza: Notificacion, RecordatorioCita, Correo
 * Tipos: NOTIFICACION, RECORDATORIO, CORREO
 *
 * @author Clínica Veterinaria Team
 * @version 2.0 - Unificada
 * @since 2025-11-04
 */
@Entity
@Table(name = "comunicaciones",
       indexes = {
           @Index(name = "idx_comunicacion_tipo", columnList = "tipo"),
           @Index(name = "idx_comunicacion_canal", columnList = "canal"),
           @Index(name = "idx_comunicacion_destinatario", columnList = "destinatario_email"),
           @Index(name = "idx_comunicacion_enviada", columnList = "enviada"),
           @Index(name = "idx_comunicacion_cita", columnList = "id_cita")
       })
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = "cita")
public class Comunicacion {

    /**
     * Constantes para tipos de comunicación.
     */
    private static final String TIPO_RECORDATORIO = "RECORDATORIO";
    private static final String TIPO_NOTIFICACION = "NOTIFICACION";
    private static final String TIPO_CORREO = "CORREO";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idComunicacion;

    /**
     * Tipo de comunicación.
     * Valores: NOTIFICACION, RECORDATORIO, CORREO
     */
    @NotBlank(message = "El tipo es obligatorio")
    @Size(max = 30, message = "El tipo no puede exceder 30 caracteres")
    @Column(nullable = false, length = 30)
    private String tipo;

    /**
     * Canal de envío.
     * Valores: EMAIL, SMS, WHATSAPP, PUSH
     */
    @NotBlank(message = "El canal es obligatorio")
    @Size(max = 20, message = "El canal no puede exceder 20 caracteres")
    @Column(nullable = false, length = 20)
    private String canal;

    /**
     * Nombre del destinatario.
     */
    @NotBlank(message = "El destinatario es obligatorio")
    @Size(max = 200, message = "El destinatario no puede exceder 200 caracteres")
    @Column(nullable = false, length = 200)
    private String destinatarioNombre;

    /**
     * Email del destinatario.
     */
    @Email(message = "Formato de email inválido")
    @Size(max = 100, message = "El email no puede exceder 100 caracteres")
    @Column(name = "destinatario_email", length = 100)
    private String destinatarioEmail;

    /**
     * Teléfono del destinatario.
     */
    @Pattern(regexp = "^[+]?\\d{7,15}$", message = "Formato de teléfono inválido")
    @Column(name = "destinatario_telefono", length = 15)
    private String destinatarioTelefono;

    /**
     * Asunto de la comunicación.
     */
    @NotBlank(message = "El asunto es obligatorio")
    @Size(max = 200, message = "El asunto no puede exceder 200 caracteres")
    @Column(nullable = false, length = 200)
    private String asunto;

    /**
     * Mensaje o contenido.
     */
    @NotBlank(message = "El mensaje es obligatorio")
    @Size(max = 2000, message = "El mensaje no puede exceder 2000 caracteres")
    @Column(nullable = false, length = 2000)
    private String mensaje;

    /**
     * Cita relacionada (solo para RECORDATORIO).
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_cita")
    private Cita cita;

    /**
     * Fecha programada para envío (solo para RECORDATORIO).
     */
    @Column(name = "fecha_programada_envio")
    private LocalDateTime fechaProgramadaEnvio;

    /**
     * Indica si fue enviada.
     */
    @Column(nullable = false)
    @Builder.Default
    private Boolean enviada = false;

    /**
     * Fecha de envío.
     */
    @Column(name = "fecha_envio")
    private LocalDateTime fechaEnvio;

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
     * Mensaje de error si falló.
     */
    @Size(max = 500, message = "El mensaje de error no puede exceder 500 caracteres")
    @Column(name = "mensaje_error", length = 500)
    private String mensajeError;

    /**
     * ID externo del proveedor.
     */
    @Size(max = 100, message = "El ID externo no puede exceder 100 caracteres")
    @Column(name = "id_externo", length = 100)
    private String idExterno;

    /**
     * Fecha de creación.
     */
    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    // ===================================================================
    // MÉTODOS DE NEGOCIO
    // ===================================================================

    public void marcarComoEnviada(String idExterno) {
        this.enviada = true;
        this.fechaEnvio = LocalDateTime.now();
        this.idExterno = idExterno;
        this.mensajeError = null;
    }

    public void registrarFalloEnvio(String error) {
        this.intentosEnvio++;
        this.mensajeError = error;
        this.enviada = false;
    }

    public boolean puedeReintentar() {
        return !enviada && intentosEnvio < maxIntentos;
    }

    public boolean esRecordatorio() {
        return TIPO_RECORDATORIO.equalsIgnoreCase(tipo);
    }

    public boolean esNotificacion() {
        return TIPO_NOTIFICACION.equalsIgnoreCase(tipo);
    }

    public boolean esCorreo() {
        return TIPO_CORREO.equalsIgnoreCase(tipo);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Comunicacion that)) return false;
        return idComunicacion != null && idComunicacion.equals(that.idComunicacion);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}

