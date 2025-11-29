package com.veterinaria.clinica_veternica.service.interfaces;

import com.veterinaria.clinica_veternica.dto.response.agenda.CitaResponseDTO;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Interfaz para operaciones de consulta de citas.
 * Segregada según Interface Segregation Principle (ISP).
 *
 * @author Clínica Veterinaria Team
 * @version 2.0
 * @since 2025-11-13
 */
public interface ICitaQueryService {

    CitaResponseDTO buscarPorId(Long id);

    List<CitaResponseDTO> listarTodos();

    List<CitaResponseDTO> listarPorVeterinario(Long idVeterinario);

    List<CitaResponseDTO> listarMisCitas();

    List<CitaResponseDTO> listarPorMascota(Long idMascota);

    List<CitaResponseDTO> listarProgramadas();

    List<CitaResponseDTO> listarPorRangoFechas(LocalDateTime inicio, LocalDateTime fin);

    List<CitaResponseDTO> listarParaRecordatorio(LocalDateTime ahora, LocalDateTime limite);
}

