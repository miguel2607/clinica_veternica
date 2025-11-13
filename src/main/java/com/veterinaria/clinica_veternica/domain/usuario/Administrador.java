package com.veterinaria.clinica_veternica.domain.usuario;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * Entidad que representa un Administrador de la clínica.
 *
 * El administrador tiene acceso total al sistema y puede realizar todas las operaciones.
 * Es responsable de la gestión general de la clínica.
 *
 * Responsabilidades:
 * - Gestión completa del sistema
 * - Administración de usuarios y permisos
 * - Configuración del sistema
 * - Generación de reportes
 * - Supervisión de todas las operaciones
 * - Gestión de inventario y proveedores
 *
 * @author Clínica Veterinaria Team
 * @version 1.0
 * @since 2025-11-02
 */
@Entity
@Table(name = "administradores")
@DiscriminatorValue("ADMINISTRADOR")
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
@ToString(callSuper = true)
public class Administrador extends Personal {

    /**
     * Obtiene el tipo de personal.
     *
     * @return "Administrador"
     */
    @Override
    public String getTipoPersonal() {
        return "Administrador";
    }
}
