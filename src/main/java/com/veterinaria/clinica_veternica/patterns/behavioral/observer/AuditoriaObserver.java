package com.veterinaria.clinica_veternica.patterns.behavioral.observer;

import com.veterinaria.clinica_veternica.domain.agenda.Cita;
import com.veterinaria.clinica_veternica.patterns.creational.singleton.AuditLogger;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * Patrón Observer: AuditoriaObserver
 *
 * Observador que registra automáticamente todas las operaciones importantes
 * del sistema para auditoría y cumplimiento normativo.
 *
 * PROPÓSITO:
 * - Registra automáticamente todas las operaciones críticas
 * - Proporciona trazabilidad completa de cambios
 * - Facilita el cumplimiento de normativas (GDPR, HIPAA veterinaria)
 * - Permite análisis forense de incidentes
 * - Soporta auditorías internas y externas
 *
 * OPERACIONES AUDITADAS:
 * - Creación, modificación y eliminación de entidades críticas
 * - Cambios de estado (citas, facturas, usuarios)
 * - Accesos a información sensible (historias clínicas)
 * - Operaciones de seguridad (login, logout, cambios de contraseña)
 * - Operaciones financieras (pagos, facturas, reembolsos)
 *
 * Justificación:
 * - Requisito legal para clínicas veterinarias
 * - Necesario para cumplimiento normativo
 * - Facilita la resolución de problemas
 * - Proporciona trazabilidad completa
 * - Soporta análisis de seguridad
 *
 * @author Clínica Veterinaria Team
 * @version 1.0
 * @since 2025-01-27
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AuditoriaObserver implements CitaObserver {

    private final AuditLogger auditLogger;

    /**
     * Se invoca cuando cambia el estado de una cita.
     * Registra el cambio para auditoría.
     *
     * PROPÓSITO: Registra todos los cambios de estado de citas para trazabilidad.
     */
    @Override
    public void onCitaStateChanged(Cita cita, String estadoAnterior, String estadoNuevo) {
        String usuario = obtenerUsuarioActual();
        
        auditLogger.logStateChange(
                "Cita",
                cita.getIdCita(),
                usuario,
                estadoAnterior,
                estadoNuevo
        );

        log.debug("Auditoría: Cambio de estado de cita {} registrado: {} -> {} por usuario {}", 
                cita.getIdCita(), estadoAnterior, estadoNuevo, usuario);
    }

    /**
     * Se invoca cuando se crea una nueva cita.
     * Registra la creación para auditoría.
     *
     * PROPÓSITO: Registra la creación de nuevas citas para trazabilidad.
     */
    @Override
    public void onCitaCreated(Cita cita) {
        String usuario = obtenerUsuarioActual();
        
        auditLogger.logCreate(
                "Cita",
                cita.getIdCita(),
                usuario
        );

        String detalles = String.format(
                "Cita creada - Mascota: %s, Veterinario: %s, Fecha: %s, Hora: %s",
                cita.getMascota().getNombre(),
                cita.getVeterinario().getNombreCompleto(),
                cita.getFechaCita(),
                cita.getHoraCita()
        );

        auditLogger.log("CREATE", "Cita", cita.getIdCita(), usuario, detalles);

        log.debug("Auditoría: Creación de cita {} registrada por usuario {}", 
                cita.getIdCita(), usuario);
    }

    /**
     * Se invoca cuando se cancela una cita.
     * Registra la cancelación para auditoría.
     *
     * PROPÓSITO: Registra cancelaciones de citas con motivo para trazabilidad.
     */
    @Override
    public void onCitaCancelled(Cita cita, String motivo) {
        String usuario = obtenerUsuarioActual();
        
        String detalles = String.format(
                "Cita cancelada - Motivo: %s, Mascota: %s, Fecha original: %s",
                motivo,
                cita.getMascota().getNombre(),
                cita.getFechaCita()
        );

        auditLogger.log("CANCEL", "Cita", cita.getIdCita(), usuario, detalles);

        log.debug("Auditoría: Cancelación de cita {} registrada por usuario {} - Motivo: {}", 
                cita.getIdCita(), usuario, motivo);
    }

    /**
     * Registra una operación de creación de entidad.
     *
     * PROPÓSITO: Método genérico para registrar creaciones de cualquier entidad.
     *
     * @param entidad Nombre de la entidad
     * @param entidadId ID de la entidad
     * @param detalles Detalles adicionales
     */
    public void registrarCreacion(String entidad, Long entidadId, String detalles) {
        String usuario = obtenerUsuarioActual();
        auditLogger.logCreate(entidad, entidadId, usuario);
        
        if (detalles != null && !detalles.isBlank()) {
            auditLogger.log("CREATE", entidad, entidadId, usuario, detalles);
        }

        log.debug("Auditoría: Creación de {} {} registrada por usuario {}", 
                entidad, entidadId, usuario);
    }

    /**
     * Registra una operación de actualización de entidad.
     *
     * PROPÓSITO: Método genérico para registrar actualizaciones de cualquier entidad.
     *
     * @param entidad Nombre de la entidad
     * @param entidadId ID de la entidad
     * @param cambios Descripción de los cambios realizados
     */
    public void registrarActualizacion(String entidad, Long entidadId, String cambios) {
        String usuario = obtenerUsuarioActual();
        auditLogger.logUpdate(entidad, entidadId, usuario, cambios);

        log.debug("Auditoría: Actualización de {} {} registrada por usuario {} - Cambios: {}", 
                entidad, entidadId, usuario, cambios);
    }

    /**
     * Registra una operación de eliminación de entidad.
     *
     * PROPÓSITO: Método genérico para registrar eliminaciones de cualquier entidad.
     *
     * @param entidad Nombre de la entidad
     * @param entidadId ID de la entidad
     * @param motivo Motivo de la eliminación
     */
    public void registrarEliminacion(String entidad, Long entidadId, String motivo) {
        String usuario = obtenerUsuarioActual();
        auditLogger.logDelete(entidad, entidadId, usuario);

        if (motivo != null && !motivo.isBlank()) {
            auditLogger.log("DELETE", entidad, entidadId, usuario, "Motivo: " + motivo);
        }

        log.debug("Auditoría: Eliminación de {} {} registrada por usuario {} - Motivo: {}", 
                entidad, entidadId, usuario, motivo);
    }

    /**
     * Registra un acceso a información sensible.
     *
     * PROPÓSITO: Registra accesos a información médica o sensible para cumplimiento normativo.
     *
     * @param entidad Nombre de la entidad
     * @param entidadId ID de la entidad
     * @param tipoAcceso Tipo de acceso (READ, EXPORT, PRINT)
     */
    public void registrarAccesoSensible(String entidad, Long entidadId, String tipoAcceso) {
        String usuario = obtenerUsuarioActual();
        auditLogger.logAccess(entidad, entidadId, usuario);

        String detalles = String.format("Acceso %s a información sensible", tipoAcceso);
        auditLogger.log("ACCESS", entidad, entidadId, usuario, detalles);

        log.debug("Auditoría: Acceso {} a {} {} registrado por usuario {}", 
                tipoAcceso, entidad, entidadId, usuario);
    }

    /**
     * Registra un intento de acceso no autorizado.
     *
     * PROPÓSITO: Registra intentos de acceso no autorizados para análisis de seguridad.
     *
     * @param entidad Nombre de la entidad
     * @param entidadId ID de la entidad
     * @param motivo Motivo del rechazo
     */
    public void registrarAccesoNoAutorizado(String entidad, Long entidadId, String motivo) {
        String usuario = obtenerUsuarioActual();
        auditLogger.logUnauthorizedAccess(entidad, entidadId, usuario, motivo);

        log.warn("Auditoría: Intento de acceso no autorizado a {} {} por usuario {} - Motivo: {}", 
                entidad, entidadId, usuario, motivo);
    }

    /**
     * Registra una operación financiera.
     *
     * PROPÓSITO: Registra operaciones financieras para auditoría contable.
     *
     * @param tipoOperacion Tipo de operación (PAGO, REEMBOLSO, AJUSTE)
     * @param entidadId ID de la entidad relacionada
     * @param monto Monto de la operación
     * @param detalles Detalles adicionales
     */
    public void registrarOperacionFinanciera(String tipoOperacion, Long entidadId, 
                                              java.math.BigDecimal monto, String detalles) {
        String usuario = obtenerUsuarioActual();
        
        String detallesCompletos = String.format(
                "%s - Monto: %s. %s",
                tipoOperacion,
                monto != null ? monto.toString() : "N/A",
                detalles != null ? detalles : ""
        );

        auditLogger.log(tipoOperacion, "OperacionFinanciera", entidadId, usuario, detallesCompletos);

        log.info("Auditoría: Operación financiera {} registrada - Monto: {} - Usuario: {}", 
                tipoOperacion, monto, usuario);
    }

    /**
     * Obtiene el usuario actual del contexto de seguridad.
     *
     * PROPÓSITO: Extrae el usuario del contexto para auditoría.
     *
     * @return Nombre del usuario o "SISTEMA" si no hay usuario autenticado
     */
    private String obtenerUsuarioActual() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getName())) {
            return auth.getName();
        }
        return "SISTEMA";
    }
}

