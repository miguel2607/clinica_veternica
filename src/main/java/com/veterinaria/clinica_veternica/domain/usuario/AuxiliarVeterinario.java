package com.veterinaria.clinica_veternica.domain.usuario;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * Entidad que representa un Auxiliar Veterinario de la clínica.
 *
 * El auxiliar veterinario asiste al veterinario en procedimientos
 * y gestiona el inventario de insumos médicos.
 *
 * Responsabilidades:
 * - Asistir en procedimientos veterinarios
 * - Gestión de inventario de insumos
 * - Registro de uso de insumos
 * - Preparación de instrumental y equipos
 * - Registro de evoluciones clínicas (supervisadas)
 * - Cuidado post-operatorio de pacientes
 * - Actualización de stock
 *
 * @author Clínica Veterinaria Team
 * @version 1.0
 * @since 2025-11-02
 */
@Entity
@Table(name = "auxiliares_veterinarios")
@DiscriminatorValue("AUXILIAR")
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
@ToString(callSuper = true)
public class AuxiliarVeterinario extends Personal {

    /**
     * Constante para nivel de certificación avanzada.
     */
    private static final String NIVEL_AVANZADO = "Avanzado";

    /**
     * Nivel de certificación del auxiliar.
     * Ejemplos: Básico, Intermedio, Avanzado
     */
    @Column(length = 50)
    private String nivelCertificacion;

    /**
     * Áreas de especialización del auxiliar.
     * Ejemplos: Cirugía, Laboratorio, Cuidados Intensivos
     */
    @Column(length = 200)
    private String areasEspecializacion;

    /**
     * Obtiene el tipo de personal.
     *
     * @return "Auxiliar Veterinario"
     */
    @Override
    public String getTipoPersonal() {
        return "Auxiliar Veterinario";
    }

    /**
     * Verifica si puede gestionar inventario.
     *
     * @return true si está activo
     */
    public boolean puedeGestionarInventario() {
        return estaActivo();
    }

    /**
     * Verifica si puede asistir en procedimientos.
     *
     * @return true si está activo y tiene certificación
     */
    public boolean puedeAsistirProcedimientos() {
        return estaActivo() && nivelCertificacion != null;
    }

    /**
     * Verifica si tiene certificación avanzada.
     *
     * @return true si el nivel es Avanzado
     */
    public boolean tieneCertificacionAvanzada() {
        return nivelCertificacion != null &&
               nivelCertificacion.equalsIgnoreCase(NIVEL_AVANZADO);
    }
}
