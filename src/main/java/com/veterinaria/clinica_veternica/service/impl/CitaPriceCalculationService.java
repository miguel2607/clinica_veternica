package com.veterinaria.clinica_veternica.service.impl;

import com.veterinaria.clinica_veternica.domain.agenda.Servicio;
import com.veterinaria.clinica_veternica.dto.request.agenda.CitaRequestDTO;
import com.veterinaria.clinica_veternica.patterns.structural.decorator.ServicioUrgenciaDecorator;
import com.veterinaria.clinica_veternica.util.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * Servicio dedicado al cálculo de precios de citas.
 * Separado según Single Responsibility Principle (SRP).
 *
 * @author Clínica Veterinaria Team
 * @version 2.0
 * @since 2025-11-13
 */
@Slf4j
@Service
public class CitaPriceCalculationService {

    public BigDecimal calcularPrecioFinal(Servicio servicio, CitaRequestDTO requestDTO) {
        BigDecimal precioBase = servicio.getPrecio();

        if (Constants.isTrue(requestDTO.getEsEmergencia())) {
            ServicioUrgenciaDecorator decorator = new ServicioUrgenciaDecorator(servicio);
            precioBase = decorator.getPrecio();
        }

        return precioBase;
    }
}

