package com.veterinaria.clinica_veternica.domain.usuario;

/**
 * Enum que representa los diferentes roles de usuario en el sistema.
 *
 * Define los niveles de acceso y permisos para cada tipo de usuario.
 * Se utiliza junto con Spring Security para control de acceso basado en roles.
 *
 * Roles disponibles:
 * - ADMIN: Administrador con acceso total al sistema
 * - VETERINARIO: Veterinario profesional que atiende pacientes
 * - AUXILIAR: Auxiliar veterinario que asiste en procedimientos
 * - RECEPCIONISTA: Personal de recepción que gestiona citas y facturación
 * - PROPIETARIO: Dueño de mascota con acceso limitado a su información
 *
 * @author Clínica Veterinaria Team
 * @version 1.0
 * @since 2025-11-02
 */
public enum RolUsuario {
    /**
     * Administrador del sistema.
     * Permisos: Acceso total, configuración del sistema, gestión de usuarios.
     */
    ADMIN("Administrador"),

    /**
     * Veterinario profesional.
     * Permisos: Gestión de citas, historias clínicas, diagnósticos, recetas.
     */
    VETERINARIO("Veterinario"),

    /**
     * Auxiliar veterinario.
     * Permisos: Asistencia en procedimientos, gestión de inventario, registro de evoluciones.
     */
    AUXILIAR("Auxiliar Veterinario"),

    /**
     * Personal de recepción.
     * Permisos: Gestión de citas, registro de mascotas y propietarios, facturación.
     */
    RECEPCIONISTA("Recepcionista"),

    /**
     * Propietario de mascota.
     * Permisos: Consulta de información de sus mascotas, solicitud de citas, visualización de facturas.
     */
    PROPIETARIO("Propietario");

    private final String displayName;

    /**
     * Constructor del enum.
     *
     * @param displayName Nombre descriptivo del rol para mostrar en UI
     */
    RolUsuario(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Obtiene el nombre descriptivo del rol.
     *
     * @return Nombre del rol en formato amigable
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Convierte el rol a formato Spring Security.
     * Spring Security requiere que los roles tengan el prefijo "ROLE_".
     *
     * @return Nombre del rol con prefijo "ROLE_"
     */
    public String getAuthority() {
        return "ROLE_" + this.name();
    }
}
