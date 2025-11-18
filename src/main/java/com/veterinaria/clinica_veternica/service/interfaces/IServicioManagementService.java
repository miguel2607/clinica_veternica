package com.veterinaria.clinica_veternica.service.interfaces;

import com.veterinaria.clinica_veternica.dto.response.agenda.ServicioResponseDTO;

/**
 * Interfaz para operaciones de gestión de estado de servicios.
 * Segregada según Interface Segregation Principle (ISP).
 *
 * @author Clínica Veterinaria Team
 * @version 2.0
 * @since 2025-11-13
 */
public interface IServicioManagementService {

    void eliminar(Long id);

    ServicioResponseDTO activar(Long id);

    ServicioResponseDTO desactivar(Long id);
}

