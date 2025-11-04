package com.veterinaria.clinica_veternica.domain.comunicacion;

import com.veterinaria.clinica_veternica.domain.agenda.Cita;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * Entidad que representa un Recordatorio de Cita.
 *
 * Recordatorios automáticos enviados a propietarios para confirmar asistencia a citas.
 * Se envían típicamente 24-48 horas antes de la cita programada.
 *
 * Implementa el patrón Observer para envío automático basado en eventos de cita.
 *
 * @author Clínica Veterinaria Team
 * @version 1.0
 * @since 2025-11-02
 */
@Entity
@Table(name = "recordatorios_cita",
       indexes = {
           @Index(name = "idx_recordatorio_cita", columnList = "id_cita"),
           @Index(name = "idx_recordatorio_programado", columnList = "fecha_programada_envio"),
           @Index(name = "idx_recordatorio_enviado", columnList = "enviado")
       })
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = "cita")
public class RecordatorioCita {

    /**
     * Identificador único del recordatorio.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idRecordatorio;

    /**
     * Cita a la que corresponde este recordatorio.
     * Relación Many-to-One con Cita.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_cita", nullable = false)
    @NotNull(message = "La cita es obligatoria")
    private Cita cita;

    /**
     * Tipo de recordatorio.
     * Valores: INICIAL (48h antes), FINAL (24h antes), CONFIRMACION
     */
    @NotBlank(message = "El tipo de recordatorio es obligatorio")
    @Size(max = 30, message = "El tipo no puede exceder 30 caracteres")
    @Column(nullable = false, length = 30)
    private String tipoRecordatorio;

    /**
     * Canal de envío del recordatorio.
     * Valores: EMAIL, SMS, WHATSAPP
     */
    @NotBlank(message = "El canal es obligatorio")
    @Size(max = 20, message = "El canal no puede exceder 20 caracteres")
    @Column(nullable = false, length = 20)
    private String canal;

    /**
     * Destinatario del recordatorio (nombre del propietario).
     */
    @NotBlank(message = "El destinatario es obligatorio")
    @Size(max = 200, message = "El destinatario no puede exceder 200 caracteres")
    @Column(nullable = false, length = 200)
    private String destinatario;

    /**
     * Email del destinatario.
     */
    @Email(message = "Formato de email inválido")
    @Size(max = 100, message = "El email no puede exceder 100 caracteres")
    @Column(length = 100)
    private String emailDestinatario;

    /**
     * Teléfono del destinatario.
     */
    @Pattern(regexp = "^[+]?[0-9]{7,15}$", message = "Formato de teléfono inválido")
    @Column(length = 15)
    private String telefonoDestinatario;

    /**
     * Mensaje del recordatorio.
     */
    @NotBlank(message = "El mensaje es obligatorio")
    @Size(min = 20, max = 1000, message = "El mensaje debe tener entre 20 y 1000 caracteres")
    @Column(nullable = false, length = 1000)
    private String mensaje;

    /**
     * Fecha y hora programada para el envío.
     */
    @NotNull(message = "La fecha programada es obligatoria")
    @Column(nullable = false)
    private LocalDateTime fechaProgramadaEnvio;

    /**
     * Indica si el recordatorio fue enviado.
     */
    @Column(nullable = false)
    @Builder.Default
    private Boolean enviado = false;

    /**
     * Fecha y hora real de envío.
     */
    @Column
    private LocalDateTime fechaEnvio;

    /**
     * Indica si el propietario confirmó la cita.
     */
    @Column(nullable = false)
    @Builder.Default
    private Boolean confirmado = false;

    /**
     * Fecha y hora de confirmación.
     */
    @Column
    private LocalDateTime fechaConfirmacion;

    /**
     * Token o link de confirmación (si aplica).
     */
    @Size(max = 200, message = "El token no puede exceder 200 caracteres")
    @Column(length = 200)
    private String tokenConfirmacion;

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
     * Mensaje de error (si falló el envío).
     */
    @Size(max = 500, message = "El mensaje de error no puede exceder 500 caracteres")
    @Column(length = 500)
    private String mensajeError;

    /**
     * ID externo del proveedor de notificaciones.
     */
    @Size(max = 100, message = "El ID externo no puede exceder 100 caracteres")
    @Column(length = 100)
    private String idExterno;

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
     * Marca el recordatorio como enviado.
     *
     * @param idExterno ID externo del proveedor
     */
    public void marcarComoEnviado(String idExterno) {
        this.enviado = true;
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
        this.enviado = false;
    }

    /**
     * Registra la confirmación del propietario.
     *
     * @param token Token de confirmación
     */
    public void registrarConfirmacion(String token) {
        if (token != null && token.equals(this.tokenConfirmacion)) {
            this.confirmado = true;
            this.fechaConfirmacion = LocalDateTime.now();
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
     * Verifica si está listo para enviar (llegó la fecha programada).
     *
     * @return true si está listo
     */
    public boolean estaListoParaEnviar() {
        return !enviado &&
               fechaProgramadaEnvio != null &&
               LocalDateTime.now().isAfter(fechaProgramadaEnvio);
    }

    /**
     * Genera un token de confirmación único.
     */
    public void generarTokenConfirmacion() {
        if (this.tokenConfirmacion == null) {
            this.tokenConfirmacion = java.util.UUID.randomUUID().toString();
        }
    }

    /**
     * Verifica si es recordatorio inicial (48h antes).
     *
     * @return true si es inicial
     */
    public boolean esRecordatorioInicial() {
        return "INICIAL".equalsIgnoreCase(tipoRecordatorio);
    }

    /**
     * Verifica si es recordatorio final (24h antes).
     *
     * @return true si es final
     */
    public boolean esRecordatorioFinal() {
        return "FINAL".equalsIgnoreCase(tipoRecordatorio);
    }

    /**
     * Verifica si requiere confirmación.
     *
     * @return true si requiere confirmación
     */
    public boolean requiereConfirmacion() {
        return "CONFIRMACION".equalsIgnoreCase(tipoRecordatorio);
    }

    /**
     * Obtiene las horas hasta el envío programado.
     *
     * @return Horas hasta el envío
     */
    public long getHorasHastaEnvio() {
        if (fechaProgramadaEnvio == null) {
            return 0;
        }
        return java.time.Duration.between(LocalDateTime.now(), fechaProgramadaEnvio).toHours();
    }

    /**
     * Verifica si el recordatorio está vencido (no se envió a tiempo).
     *
     * @return true si está vencido
     */
    public boolean estaVencido() {
        return !enviado &&
               fechaProgramadaEnvio != null &&
               LocalDateTime.now().isAfter(fechaProgramadaEnvio.plusHours(2));
    }

    // ===================================================================
    // EQUALS Y HASHCODE
    // ===================================================================

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RecordatorioCita that)) return false;
        return idRecordatorio != null && idRecordatorio.equals(that.idRecordatorio);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
