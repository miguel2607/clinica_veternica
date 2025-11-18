package com.veterinaria.clinica_veternica.patterns.structural.decorator;

import com.veterinaria.clinica_veternica.domain.agenda.Servicio;

import java.math.BigDecimal;

/**
 * Patrón Decorator: ServicioDecorator (Abstract)
 *
 * Clase base abstracta para decoradores de servicios.
 * Permite agregar funcionalidades adicionales a servicios dinámicamente
 * sin modificar la clase base.
 *
 * Justificación:
 * - Permite agregar responsabilidades sin modificar la clase base
 * - Facilita combinar múltiples funcionalidades
 * - Evita la explosión de subclases
 * - Permite agregar/remover funcionalidades en tiempo de ejecución
 *
 * Decoradores implementados:
 * - ServicioConDescuentoDecorator: Aplica descuentos
 * - ServicioConSeguroDecorator: Incluye seguro
 * - ServicioUrgenciaDecorator: Cargo adicional por urgencia
 * - ServicioDomicilioDecorator: Servicio a domicilio
 *
 * @author Clínica Veterinaria Team
 * @version 1.0
 * @since 2025-11-04
 */
public abstract class ServicioDecorator {

    protected Servicio servicio;

    protected ServicioDecorator(Servicio servicio) {
        this.servicio = servicio;
    }

    /**
     * Obtiene el precio del servicio con las modificaciones del decorador.
     *
     * @return Precio modificado
     */
    public abstract BigDecimal getPrecio();

    /**
     * Obtiene la descripción del servicio con las modificaciones.
     *
     * @return Descripción modificada
     */
    public abstract String getDescripcion();

    /**
     * Obtiene el servicio base.
     *
     * @return Servicio base
     */
    public Servicio getServicio() {
        return servicio;
    }
}

