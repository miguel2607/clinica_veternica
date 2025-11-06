package com.veterinaria.clinica_veternica.domain.usuario;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * Entidad que representa un Recepcionista de la clínica.
 *
 * El recepcionista es el primer punto de contacto con los propietarios.
 * Gestiona citas, registros y facturación.
 *
 * Responsabilidades:
 * - Gestión de citas (crear, modificar, cancelar)
 * - Registro de mascotas y propietarios
 * - Confirmación de asistencia a citas
 * - Generación de facturas
 * - Registro de pagos
 * - Atención al cliente
 * - Gestión de agenda de veterinarios
 *
 * @author Clínica Veterinaria Team
 * @version 1.0
 * @since 2025-11-02
 */
@Entity
@Table(name = "recepcionistas")
@DiscriminatorValue("RECEPCIONISTA")
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
@ToString(callSuper = true)
public class Recepcionista extends Personal {

    /**
     * Obtiene el tipo de personal.
     *
     * @return "Recepcionista"
     */
    @Override
    public String getTipoPersonal() {
        return "Recepcionista";
    }

    /**
     * Verifica si puede gestionar citas.
     *
     * @return true si está activo
     */
    public boolean puedeGestionarCitas() {
        return estaActivo();
    }

    /**
     * Verifica si puede generar facturas.
     *
     * @return true si está activo
     */
    public boolean puedeGenerarFacturas() {
        return estaActivo();
    }
}
