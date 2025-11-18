package com.veterinaria.clinica_veternica.patterns.structural.decorator;

import java.math.BigDecimal;

/**
 * Patrón Decorator: ServicioConSeguroDecorator
 *
 * Decorador que agrega seguro al servicio.
 * Aplica un cargo adicional por seguro médico.
 *
 * Justificación:
 * - Agrega funcionalidad (seguro) sin modificar la clase Servicio
 * - Puede combinarse con otros decoradores
 * - Permite agregar/remover funcionalidades dinámicamente
 *
 * @author Clínica Veterinaria Team
 * @version 1.0
 * @since 2025-11-04
 */
public class ServicioConSeguroDecorator extends ServicioDecorator {

    private static final BigDecimal COSTO_SEGURO = new BigDecimal("50000");

    public ServicioConSeguroDecorator(com.veterinaria.clinica_veternica.domain.agenda.Servicio servicio) {
        super(servicio);
    }

    @Override
    public BigDecimal getPrecio() {
        BigDecimal precioBase = servicio.getPrecio();
        return precioBase.add(COSTO_SEGURO);
    }

    @Override
    public String getDescripcion() {
        return servicio.getDescripcion() + " [Incluye seguro médico]";
    }
}

