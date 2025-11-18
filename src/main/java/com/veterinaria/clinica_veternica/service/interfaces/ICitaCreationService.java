package com.veterinaria.clinica_veternica.service.interfaces;

import com.veterinaria.clinica_veternica.dto.request.agenda.CitaRequestDTO;
import com.veterinaria.clinica_veternica.dto.response.agenda.CitaResponseDTO;

/**
 * Interfaz para operaciones de creación y actualización de citas.
 * Segregada según Interface Segregation Principle (ISP).
 *
 * @author Clínica Veterinaria Team
 * @version 2.0
 * @since 2025-11-13
 */
public interface ICitaCreationService {

    CitaResponseDTO crear(CitaRequestDTO requestDTO);

    CitaResponseDTO actualizar(Long id, CitaRequestDTO requestDTO);
}

