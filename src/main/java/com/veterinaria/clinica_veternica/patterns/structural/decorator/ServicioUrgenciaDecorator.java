package com.veterinaria.clinica_veternica.patterns.structural.decorator;

import java.math.BigDecimal;

/**
 * Patrón Decorator: ServicioUrgenciaDecorator
 *
 * Decorador que aplica un recargo por servicio de urgencia.
 * Aplica un 50% adicional al precio base.
 *
 * Justificación:
 * - Agrega funcionalidad (recargo de urgencia) sin modificar la clase Servicio
 * - Puede combinarse con otros decoradores
 * - Permite agregar/remover funcionalidades dinámicamente
 *
 * @author Clínica Veterinaria Team
 * @version 1.0
 * @since 2025-11-04
 */
public class ServicioUrgenciaDecorator extends ServicioDecorator {

    private static final BigDecimal PORCENTAJE_RECARGO = new BigDecimal("0.50");

    public ServicioUrgenciaDecorator(com.veterinaria.clinica_veternica.domain.agenda.Servicio servicio) {
        super(servicio);
    }

    @Override
    public BigDecimal getPrecio() {
        BigDecimal precioBase = servicio.getPrecio();
        BigDecimal recargo = precioBase.multiply(PORCENTAJE_RECARGO);
        return precioBase.add(recargo);
    }

    @Override
    public String getDescripcion() {
        return servicio.getDescripcion() + " [Servicio de urgencia - Recargo 50%]";
    }
}

