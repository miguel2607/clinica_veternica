package com.veterinaria.clinica_veternica.service.impl;

import com.veterinaria.clinica_veternica.domain.agenda.Cita;
import com.veterinaria.clinica_veternica.exception.ValidationException;
import com.veterinaria.clinica_veternica.patterns.behavioral.chain.*;
import jakarta.annotation.PostConstruct;
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

    /**
     * Inicializa la cadena de validaciones al construir el bean.
     * Elimina el antipatrón Sequential Coupling asegurando que la cadena
     * esté siempre construida antes de usar el servicio.
     */
    @PostConstruct
    private void construirCadenaValidaciones() {
        validacionDatosHandler
                .setSiguiente(validacionDisponibilidadHandler)
                .setSiguiente(validacionPermisoHandler)
                .setSiguiente(validacionStockHandler);
        log.debug("Cadena de validaciones de citas inicializada correctamente");
    }

    public void validarCita(Cita cita) {
        try {
            validacionDatosHandler.validar(cita);
        } catch (ValidationException e) {
            throw e;
        } catch (RuntimeException e) {
            log.error("Error inesperado durante la validación de cita: {}", e.getMessage(), e);
            throw new ValidationException("Error al validar la cita: " + e.getMessage());
        }
    }
}

