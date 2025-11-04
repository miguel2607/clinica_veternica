package com.veterinaria.clinica_veternica.patterns.creational.singleton;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Patrón Singleton: AuditLogger
 *
 * Sistema único de auditoría para registrar todas las operaciones críticas
 * del sistema de manera centralizada y thread-safe.
 *
 * Justificación:
 * - Garantiza un único punto de auditoría en todo el sistema
 * - Evita inconsistencias en logs de auditoría
 * - Thread-safe para operaciones concurrentes
 * - Almacenamiento en memoria con posibilidad de persistencia
 *
 * Uso:
 * - Registrar operaciones CRUD de entidades críticas
 * - Auditar cambios de estado (citas, facturas, usuarios)
 * - Tracking de accesos a información sensible
 * - Cumplimiento de normativas (GDPR, HIPAA veterinaria)
 *
 * @author Clínica Veterinaria Team
 * @version 1.0
 * @since 2025-11-04
 */
@Slf4j
@Component
public class AuditLogger {

    // Cola thread-safe para almacenar logs de auditoría
    private final ConcurrentLinkedQueue<AuditLog> auditLogs = new ConcurrentLinkedQueue<>();

    // Límite de logs en memoria (se puede configurar)
    private static final int MAX_LOGS_IN_MEMORY = 10000;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * Registra una operación de auditoría.
     *
     * @param accion Acción realizada (CREATE, UPDATE, DELETE, READ)
     * @param entidad Nombre de la entidad afectada
     * @param entidadId ID de la entidad
     * @param usuario Usuario que realizó la acción
     * @param detalles Detalles adicionales
     */
    public void log(String accion, String entidad, Long entidadId, String usuario, String detalles) {
        AuditLog auditLog = AuditLog.builder()
                .timestamp(LocalDateTime.now())
                .accion(accion)
                .entidad(entidad)
                .entidadId(entidadId)
                .usuario(usuario)
                .detalles(detalles)
                .build();

        auditLogs.offer(auditLog);

        // Log adicional con SLF4J para persistencia
        log.info("AUDIT: {} | {} | {} | ID:{} | Usuario:{} | {}",
                auditLog.getTimestamp().format(formatter),
                auditLog.getAccion(),
                auditLog.getEntidad(),
                auditLog.getEntidadId(),
                auditLog.getUsuario(),
                auditLog.getDetalles());

        // Limitar tamaño en memoria
        if (auditLogs.size() > MAX_LOGS_IN_MEMORY) {
            auditLogs.poll(); // Eliminar el más antiguo
        }
    }

    /**
     * Registra una creación de entidad.
     */
    public void logCreate(String entidad, Long entidadId, String usuario) {
        log("CREATE", entidad, entidadId, usuario, "Entidad creada");
    }

    /**
     * Registra una actualización de entidad.
     */
    public void logUpdate(String entidad, Long entidadId, String usuario, String cambios) {
        log("UPDATE", entidad, entidadId, usuario, "Cambios: " + cambios);
    }

    /**
     * Registra una eliminación de entidad.
     */
    public void logDelete(String entidad, Long entidadId, String usuario) {
        log("DELETE", entidad, entidadId, usuario, "Entidad eliminada");
    }

    /**
     * Registra un acceso a información sensible.
     */
    public void logAccess(String entidad, Long entidadId, String usuario) {
        log("ACCESS", entidad, entidadId, usuario, "Acceso a información sensible");
    }

    /**
     * Registra un cambio de estado.
     */
    public void logStateChange(String entidad, Long entidadId, String usuario, String estadoAnterior, String estadoNuevo) {
        String detalles = String.format("Cambio de estado: %s -> %s", estadoAnterior, estadoNuevo);
        log("STATE_CHANGE", entidad, entidadId, usuario, detalles);
    }

    /**
     * Registra un intento de acceso no autorizado.
     */
    public void logUnauthorizedAccess(String entidad, Long entidadId, String usuario, String motivo) {
        log("UNAUTHORIZED", entidad, entidadId, usuario, "Acceso denegado: " + motivo);
    }

    /**
     * Registra un error de operación.
     */
    public void logError(String entidad, Long entidadId, String usuario, String error) {
        log("ERROR", entidad, entidadId, usuario, "Error: " + error);
    }

    /**
     * Obtiene todos los logs de auditoría en memoria.
     */
    public List<AuditLog> getAllLogs() {
        return new ArrayList<>(auditLogs);
    }

    /**
     * Obtiene logs filtrados por entidad.
     */
    public List<AuditLog> getLogsByEntidad(String entidad) {
        return auditLogs.stream()
                .filter(log -> entidad.equals(log.getEntidad()))
                .toList();
    }

    /**
     * Obtiene logs filtrados por usuario.
     */
    public List<AuditLog> getLogsByUsuario(String usuario) {
        return auditLogs.stream()
                .filter(log -> usuario.equals(log.getUsuario()))
                .toList();
    }

    /**
     * Obtiene logs filtrados por acción.
     */
    public List<AuditLog> getLogsByAccion(String accion) {
        return auditLogs.stream()
                .filter(log -> accion.equals(log.getAccion()))
                .toList();
    }

    /**
     * Obtiene logs en un rango de fechas.
     */
    public List<AuditLog> getLogsByDateRange(LocalDateTime inicio, LocalDateTime fin) {
        return auditLogs.stream()
                .filter(log -> !log.getTimestamp().isBefore(inicio) &&
                              !log.getTimestamp().isAfter(fin))
                .toList();
    }

    /**
     * Limpia todos los logs de auditoría.
     */
    public void clearLogs() {
        log.warn("Limpiando todos los logs de auditoría");
        auditLogs.clear();
    }

    /**
     * Obtiene el número de logs almacenados.
     */
    public int getLogCount() {
        return auditLogs.size();
    }

    /**
     * Clase interna para representar un log de auditoría.
     */
    @lombok.Data
    @lombok.Builder
    public static class AuditLog {
        private LocalDateTime timestamp;
        private String accion;
        private String entidad;
        private Long entidadId;
        private String usuario;
        private String detalles;
    }
}
