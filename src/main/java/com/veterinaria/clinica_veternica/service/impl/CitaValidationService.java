package com.veterinaria.clinica_veternica.service.impl;

import com.veterinaria.clinica_veternica.domain.agenda.Cita;
import com.veterinaria.clinica_veternica.exception.ValidationException;
import com.veterinaria.clinica_veternica.patterns.behavioral.chain.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Servicio dedicado a la validación de citas.
 * Separado según Single Responsibility Principle (SRP).
 *
 * @author Clínica Veterinaria Team
 * @version 2.0
 * @since 2025-11-13
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CitaValidationService {

    private final ValidacionDatosHandler validacionDatosHandler;
    private final ValidacionDisponibilidadHandler validacionDisponibilidadHandler;
    private final ValidacionPermisoHandler validacionPermisoHandler;
    private final ValidacionStockHandler validacionStockHandler;

    public void validarCita(Cita cita) {
        construirCadenaValidaciones();
        try {
            validacionDatosHandler.validar(cita);
        } catch (ValidationException e) {
            throw e;
        } catch (RuntimeException e) {
            log.error("Error inesperado durante la validación de cita: {}", e.getMessage(), e);
            throw new ValidationException("Error al validar la cita: " + e.getMessage());
        }
    }

    private void construirCadenaValidaciones() {
        validacionDatosHandler
                .setSiguiente(validacionDisponibilidadHandler)
                .setSiguiente(validacionPermisoHandler)
                .setSiguiente(validacionStockHandler);
    }
}

