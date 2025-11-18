package com.veterinaria.clinica_veternica.patterns.structural.decorator;

import java.math.BigDecimal;

/**
 * Patrón Decorator: ServicioDomicilioDecorator
 *
 * Decorador que aplica un cargo adicional por servicio a domicilio.
 *
 * Justificación:
 * - Agrega funcionalidad (servicio a domicilio) sin modificar la clase Servicio
 * - Puede combinarse con otros decoradores
 * - Permite agregar/remover funcionalidades dinámicamente
 *
 * @author Clínica Veterinaria Team
 * @version 1.0
 * @since 2025-11-04
 */
public class ServicioDomicilioDecorator extends ServicioDecorator {

    private static final BigDecimal COSTO_DOMICILIO = new BigDecimal("30000");

    public ServicioDomicilioDecorator(com.veterinaria.clinica_veternica.domain.agenda.Servicio servicio) {
        super(servicio);
    }

    @Override
    public BigDecimal getPrecio() {
        BigDecimal precioBase = servicio.getPrecio();
        BigDecimal costoDomicilio = servicio.getCostoAdicionalDomicilio() != null
                ? servicio.getCostoAdicionalDomicilio()
                : COSTO_DOMICILIO;
        return precioBase.add(costoDomicilio);
    }

    @Override
    public String getDescripcion() {
        return servicio.getDescripcion() + " [Servicio a domicilio]";
    }
}

