package com.veterinaria.clinica_veternica.service.interfaces;

import com.veterinaria.clinica_veternica.dto.response.agenda.CitaResponseDTO;

/**
 * Interfaz para operaciones de cambio de estado de citas.
 * Segregada según Interface Segregation Principle (ISP).
 *
 * @author Clínica Veterinaria Team
 * @version 2.0
 * @since 2025-11-13
 */
public interface ICitaStateService {

    CitaResponseDTO confirmar(Long id);

    CitaResponseDTO cancelar(Long id, String motivo, String usuario);

    CitaResponseDTO marcarComoAtendida(Long id);

    CitaResponseDTO iniciarAtencion(Long id);

    CitaResponseDTO finalizarAtencion(Long id);
}

