package com.veterinaria.clinica_veternica.service.interfaces;

import com.veterinaria.clinica_veternica.dto.response.agenda.ServicioResponseDTO;

import java.math.BigDecimal;
import java.util.List;

/**
 * Interfaz para operaciones de consulta de servicios.
 * Segregada según Interface Segregation Principle (ISP).
 *
 * @author Clínica Veterinaria Team
 * @version 2.0
 * @since 2025-11-13
 */
public interface IServicioQueryService {

    ServicioResponseDTO buscarPorId(Long id);

    List<ServicioResponseDTO> listarTodos();

    List<ServicioResponseDTO> listarActivos();

    List<ServicioResponseDTO> listarPorTipo(String tipoServicio);

    List<ServicioResponseDTO> listarPorCategoria(String categoria);

    List<ServicioResponseDTO> listarPorRangoPrecio(BigDecimal min, BigDecimal max);
}

