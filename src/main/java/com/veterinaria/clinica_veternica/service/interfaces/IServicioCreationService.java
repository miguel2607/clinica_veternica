package com.veterinaria.clinica_veternica.service.interfaces;

import com.veterinaria.clinica_veternica.dto.request.agenda.ServicioRequestDTO;
import com.veterinaria.clinica_veternica.dto.response.agenda.ServicioResponseDTO;

import java.math.BigDecimal;

/**
 * Interfaz para operaciones de creación y actualización de servicios.
 * Segregada según Interface Segregation Principle (ISP).
 *
 * @author Clínica Veterinaria Team
 * @version 2.0
 * @since 2025-11-13
 */
public interface IServicioCreationService {

    ServicioResponseDTO crear(ServicioRequestDTO requestDTO);

    ServicioResponseDTO crearConFactory(String nombre, String descripcion, BigDecimal precio, String categoria);

    ServicioResponseDTO actualizar(Long id, ServicioRequestDTO requestDTO);
}

