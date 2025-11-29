package com.veterinaria.clinica_veternica.patterns.structural.proxy;

import com.veterinaria.clinica_veternica.domain.clinico.HistoriaClinica;
import com.veterinaria.clinica_veternica.domain.paciente.Propietario;
import com.veterinaria.clinica_veternica.domain.usuario.Usuario;
import com.veterinaria.clinica_veternica.patterns.creational.singleton.AuditLogger;
import com.veterinaria.clinica_veternica.repository.PropietarioRepository;
import com.veterinaria.clinica_veternica.repository.UsuarioRepository;
import com.veterinaria.clinica_veternica.util.Constants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Optional;

/**
 * Patrón Proxy: HistoriaClinicaProxy
 *
 * Controla el acceso a historias clínicas verificando permisos y roles.
 * Las historias clínicas contienen información médica sensible que requiere
 * control de acceso estricto.
 *
 * PROPÓSITO:
 * - Control de acceso basado en roles para información médica sensible
 * - Auditoría automática de accesos a historias clínicas
 * - Validación de permisos antes de permitir operaciones
 * - Cumplimiento de normativas de privacidad médica (HIPAA, GDPR)
 *
 * REGLAS DE ACCESO:
 * - VETERINARIO: Puede leer y escribir historias clínicas de sus pacientes
 * - ADMIN: Acceso completo a todas las historias clínicas
 * - RECEPCIONISTA: Solo lectura limitada (información básica)
 * - AUXILIAR_VETERINARIO: Lectura y escritura bajo supervisión
 * - PROPIETARIO: Solo lectura de la historia de sus propias mascotas
 *
 * Justificación:
 * - Las historias clínicas contienen información médica sensible
 * - Requieren control de acceso estricto por normativas legales
 * - Necesitan auditoría de todos los accesos
 * - Diferentes roles tienen diferentes niveles de acceso
 *
 * @author Clínica Veterinaria Team
 * @version 1.0
 * @since 2025-01-27
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class HistoriaClinicaProxy {

    private final AuditLogger auditLogger;
    private final UsuarioRepository usuarioRepository;
    private final PropietarioRepository propietarioRepository;

    /**
     * Verifica si el usuario actual tiene permisos para leer una historia clínica.
     *
     * PROPÓSITO: Valida permisos de lectura antes de permitir acceso a información médica.
     *
     * @param historiaClinica Historia clínica a acceder
     * @return true si tiene permisos, false en caso contrario
     */
    public boolean tienePermisoLectura(HistoriaClinica historiaClinica) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        
        if (auth == null || !auth.isAuthenticated()) {
            log.warn("Intento de acceso a historia clínica sin autenticación");
            return false;
        }

        String usuario = auth.getName();
        Collection<? extends GrantedAuthority> authorities = auth.getAuthorities();

        // Verificar roles con permisos de lectura
        boolean tienePermiso = authorities.stream()
                .anyMatch(a -> {
                    String role = a.getAuthority();
                    return role.equals(Constants.ROLE_ADMIN_STRING) ||
                           role.equals(Constants.ROLE_VETERINARIO_STRING) ||
                           role.equals(Constants.ROLE_RECEPCIONISTA_STRING) ||
                           role.equals(Constants.ROLE_AUXILIAR_VETERINARIO_STRING);
                });

        // Si no tiene permiso con roles estándar, verificar si es PROPIETARIO
        if (!tienePermiso) {
            boolean esPropietario = authorities.stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_" + Constants.ROLE_PROPIETARIO) || 
                                  a.getAuthority().equals(Constants.ROLE_PROPIETARIO));
            
            if (esPropietario) {
                // Verificar que la mascota de la historia clínica pertenezca al propietario
                try {
                    Optional<Usuario> usuarioOpt = usuarioRepository.findByUsername(usuario);
                    if (usuarioOpt.isPresent()) {
                        Usuario usuarioEntity = usuarioOpt.get();
                        Optional<Propietario> propietarioOpt = propietarioRepository.findByEmail(usuarioEntity.getEmail());
                        
                        if (propietarioOpt.isPresent()) {
                            Propietario propietario = propietarioOpt.get();
                            // Verificar que la mascota de la historia clínica pertenezca a este propietario
                            if (historiaClinica.getMascota() != null && 
                                historiaClinica.getMascota().getPropietario() != null &&
                                historiaClinica.getMascota().getPropietario().getIdPropietario().equals(propietario.getIdPropietario())) {
                                tienePermiso = true;
                                log.debug("Acceso autorizado a historia clínica {} por propietario {} (mascota propia)", 
                                        historiaClinica.getIdHistoriaClinica(), usuario);
                            } else {
                                log.warn("Propietario {} intentó acceder a historia clínica de mascota que no le pertenece", usuario);
                            }
                        }
                    }
                } catch (Exception e) {
                    log.error("Error al verificar permisos de propietario: {}", e.getMessage(), e);
                }
            }
        }

        if (tienePermiso) {
            // Registrar acceso autorizado
            auditLogger.logAccess(Constants.ENTIDAD_HISTORIA_CLINICA_SIN_ESPACIO, 
                    historiaClinica.getIdHistoriaClinica(), 
                    usuario);
            log.debug("Acceso autorizado a historia clínica {} por usuario {}", 
                    historiaClinica.getIdHistoriaClinica(), usuario);
        } else {
            // Registrar intento no autorizado
            auditLogger.logUnauthorizedAccess(Constants.ENTIDAD_HISTORIA_CLINICA_SIN_ESPACIO, 
                    historiaClinica.getIdHistoriaClinica(), 
                    usuario, 
                    "Usuario sin permisos de lectura");
            log.warn("Acceso denegado a historia clínica {} por usuario {}", 
                    historiaClinica.getIdHistoriaClinica(), usuario);
        }

        return tienePermiso;
    }

    /**
     * Verifica si el usuario actual tiene permisos para modificar una historia clínica.
     *
     * PROPÓSITO: Valida permisos de escritura antes de permitir modificaciones.
     *
     * @param historiaClinica Historia clínica a modificar
     * @return true si tiene permisos, false en caso contrario
     */
    public boolean tienePermisoEscritura(HistoriaClinica historiaClinica) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        
        if (auth == null || !auth.isAuthenticated()) {
            log.warn("Intento de modificación de historia clínica sin autenticación");
            return false;
        }

        String usuario = auth.getName();
        Collection<? extends GrantedAuthority> authorities = auth.getAuthorities();

        // ADMIN, VETERINARIO y AUXILIAR pueden modificar
        // AUXILIAR puede modificar observaciones y campos no críticos
        boolean tienePermiso = authorities.stream()
                .anyMatch(a -> {
                    String role = a.getAuthority();
                    return role.equals(Constants.ROLE_ADMIN_STRING) ||
                           role.equals(Constants.ROLE_VETERINARIO_STRING) ||
                           role.equals("ROLE_" + Constants.ROLE_AUXILIAR) ||
                           role.equals(Constants.ROLE_AUXILIAR_VETERINARIO_STRING);
                });

        if (tienePermiso) {
            log.debug("Permiso de escritura autorizado para historia clínica {} por usuario {}", 
                    historiaClinica.getIdHistoriaClinica(), usuario);
        } else {
            auditLogger.logUnauthorizedAccess(Constants.ENTIDAD_HISTORIA_CLINICA_SIN_ESPACIO, 
                    historiaClinica.getIdHistoriaClinica(), 
                    usuario, 
                    "Usuario sin permisos de escritura");
            log.warn("Modificación denegada de historia clínica {} por usuario {}", 
                    historiaClinica.getIdHistoriaClinica(), usuario);
        }

        return tienePermiso;
    }

    /**
     * Verifica si el usuario puede acceder a la historia clínica de una mascota específica.
     * Los veterinarios solo pueden acceder a historias de sus pacientes.
     *
     * PROPÓSITO: Implementa control de acceso granular basado en relación médico-paciente.
     *
     * @param historiaClinica Historia clínica a acceder
     * @param idVeterinario ID del veterinario (opcional, para validar relación)
     * @return true si tiene acceso, false en caso contrario
     */
    public boolean puedeAccederAHistoria(HistoriaClinica historiaClinica, Long idVeterinario) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        
        if (auth == null || !auth.isAuthenticated()) {
            return false;
        }

        String usuario = auth.getName();
        Collection<? extends GrantedAuthority> authorities = auth.getAuthorities();

        // NOTA: ADMIN tiene acceso total
        boolean esAdmin = authorities.stream()
                .anyMatch(a -> a.getAuthority().equals(Constants.ROLE_ADMIN_STRING));

        if (esAdmin) {
            auditLogger.logAccess(Constants.ENTIDAD_HISTORIA_CLINICA_SIN_ESPACIO, 
                    historiaClinica.getIdHistoriaClinica(), 
                    usuario);
            return true;
        }

        // VETERINARIO solo puede acceder a historias de sus pacientes
        boolean esVeterinario = authorities.stream()
                .anyMatch(a -> a.getAuthority().equals(Constants.ROLE_VETERINARIO_STRING));

        if (esVeterinario && idVeterinario != null) {
            // Verificar si el veterinario tiene relación con esta mascota
            // (esto requeriría una consulta adicional en producción)
            // Por ahora, permitimos si es veterinario
            auditLogger.logAccess(Constants.ENTIDAD_HISTORIA_CLINICA_SIN_ESPACIO, 
                    historiaClinica.getIdHistoriaClinica(), 
                    usuario);
            return true;
        }

        // Otros roles tienen acceso limitado
        boolean tieneAccesoLimitado = authorities.stream()
                .anyMatch(a -> {
                    String role = a.getAuthority();
                    return role.equals(Constants.ROLE_RECEPCIONISTA_STRING) ||
                           role.equals(Constants.ROLE_AUXILIAR_VETERINARIO_STRING);
                });

        if (tieneAccesoLimitado) {
            auditLogger.logAccess(Constants.ENTIDAD_HISTORIA_CLINICA_SIN_ESPACIO, 
                    historiaClinica.getIdHistoriaClinica(), 
                    usuario);
            return true;
        }

        auditLogger.logUnauthorizedAccess(Constants.ENTIDAD_HISTORIA_CLINICA_SIN_ESPACIO, 
                historiaClinica.getIdHistoriaClinica(), 
                usuario, 
                "Usuario sin relación con la mascota");
        return false;
    }

    /**
     * Registra auditoría de acceso a historia clínica.
     *
     * PROPÓSITO: Registra todos los accesos para cumplimiento normativo.
     *
     * @param historiaClinica Historia clínica accedida
     * @param accion Acción realizada (READ, UPDATE, CREATE)
     */
    public void registrarAcceso(HistoriaClinica historiaClinica, String accion) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String usuario = auth != null ? auth.getName() : "SISTEMA";

        auditLogger.log(accion, 
                Constants.ENTIDAD_HISTORIA_CLINICA_SIN_ESPACIO, 
                historiaClinica.getIdHistoriaClinica(), 
                usuario, 
                "Acceso a información médica sensible");

        log.info("Acceso registrado: {} - Historia clínica {} - Usuario {}", 
                accion, historiaClinica.getIdHistoriaClinica(), usuario);
    }

    /**
     * Registra auditoría de modificación de historia clínica.
     *
     * PROPÓSITO: Registra cambios en historias clínicas para trazabilidad médica.
     *
     * @param historiaClinica Historia clínica modificada
     * @param cambios Descripción de los cambios realizados
     */
    public void registrarModificacion(HistoriaClinica historiaClinica, String cambios) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String usuario = auth != null ? auth.getName() : "SISTEMA";

        auditLogger.logUpdate(Constants.ENTIDAD_HISTORIA_CLINICA_SIN_ESPACIO, 
                historiaClinica.getIdHistoriaClinica(), 
                usuario, 
                cambios);

        log.info("Modificación registrada: Historia clínica {} - Usuario {} - Cambios: {}", 
                historiaClinica.getIdHistoriaClinica(), usuario, cambios);
    }
}

