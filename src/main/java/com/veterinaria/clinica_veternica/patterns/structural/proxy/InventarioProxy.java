package com.veterinaria.clinica_veternica.patterns.structural.proxy;

import com.veterinaria.clinica_veternica.domain.inventario.Inventario;
import com.veterinaria.clinica_veternica.patterns.creational.singleton.AuditLogger;
import com.veterinaria.clinica_veternica.util.Constants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * Patrón Proxy: InventarioProxy
 *
 * Controla el acceso a operaciones de inventario, verificando permisos
 * y registrando auditoría antes de permitir modificaciones.
 *
 * Justificación:
 * - Añade control de acceso sin modificar las clases reales
 * - Implementa auditoría automática
 * - Puede implementar caché para consultas frecuentes
 * - Facilita el control de seguridad
 *
 * @author Clínica Veterinaria Team
 * @version 1.0
 * @since 2025-11-04
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class InventarioProxy {

    private final AuditLogger auditLogger;

    /**
     * Verifica permisos antes de modificar inventario.
     */
    public boolean tienePermisoModificar() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            log.warn("Intento de modificar inventario sin autenticación");
            return false;
        }

        // Verificar roles (simplificado)
        boolean tienePermiso = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals(Constants.ROLE_ADMIN_STRING) || 
                             a.getAuthority().equals(Constants.ROLE_VETERINARIO_STRING));

        if (!tienePermiso) {
            log.warn("Usuario {} no tiene permisos para modificar inventario", auth.getName());
            auditLogger.logUnauthorizedAccess("Inventario", null, auth.getName(), 
                    "Intento de modificación sin permisos");
        }

        return tienePermiso;
    }

    /**
     * Registra auditoría al modificar inventario.
     */
    public void registrarModificacion(Inventario inventario, String accion, String detalles) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String usuario = auth != null ? auth.getName() : "SISTEMA";

        auditLogger.log(accion, "Inventario", inventario.getIdInventario(), usuario, detalles);
        log.info("Modificación de inventario registrada: {} por usuario {}", accion, usuario);
    }
}

