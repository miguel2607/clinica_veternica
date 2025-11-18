package com.veterinaria.clinica_veternica.patterns.structural.decorator;

import com.veterinaria.clinica_veternica.domain.agenda.Servicio;
import com.veterinaria.clinica_veternica.util.Constants;

import java.math.BigDecimal;

/**
 * Decorador concreto que aplica un descuento al precio del servicio.
 *
 * @author Clínica Veterinaria Team
 * @version 1.0
 * @since 2025-11-04
 */
public class ServicioConDescuentoDecorator extends ServicioDecorator {

    private final BigDecimal porcentajeDescuento;

    public ServicioConDescuentoDecorator(Servicio servicio, BigDecimal porcentajeDescuento) {
        super(servicio);
        if (porcentajeDescuento.compareTo(BigDecimal.ZERO) < 0 ||
            porcentajeDescuento.compareTo(Constants.PORCENTAJE_DIVISOR) > 0) {
            throw new IllegalArgumentException("El descuento debe estar entre 0 y 100");
        }
        this.porcentajeDescuento = porcentajeDescuento;
    }

    @Override
    public BigDecimal getPrecio() {
        BigDecimal precioBase = servicio.getPrecio();
        BigDecimal descuento = precioBase.multiply(porcentajeDescuento)
                .divide(Constants.PORCENTAJE_DIVISOR, 2, java.math.RoundingMode.HALF_UP);
        return precioBase.subtract(descuento);
    }

    @Override
    public String getDescripcion() {
        return servicio.getDescripcion() + 
               String.format(" (Descuento del %.0f%%)", porcentajeDescuento);
    }
}

