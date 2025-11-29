package com.veterinaria.clinica_veternica.service.interfaces;

import com.veterinaria.clinica_veternica.dto.request.agenda.HorarioRequestDTO;
import com.veterinaria.clinica_veternica.dto.response.agenda.DisponibilidadVeterinarioDTO;
import com.veterinaria.clinica_veternica.dto.response.agenda.HorarioResponseDTO;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

/**
 * Interfaz del servicio para gestión de Horarios.
 *
 * @author Clínica Veterinaria Team
 * @version 1.0
 * @since 2025-11-06
 */
public interface IHorarioService {

    HorarioResponseDTO crear(HorarioRequestDTO requestDTO);

    HorarioResponseDTO actualizar(Long id, HorarioRequestDTO requestDTO);

    HorarioResponseDTO buscarPorId(Long id);

    List<HorarioResponseDTO> listarTodos();

    List<HorarioResponseDTO> listarActivos();

    List<HorarioResponseDTO> listarPorVeterinario(Long idVeterinario);

    List<HorarioResponseDTO> listarPorDiaSemana(DayOfWeek diaSemana);

    void eliminar(Long id);

    HorarioResponseDTO activar(Long id);

    HorarioResponseDTO desactivar(Long id);

    /**
     * Obtiene la disponibilidad de un veterinario para una fecha específica.
     *
     * @param idVeterinario ID del veterinario
     * @param fecha Fecha a consultar
     * @return Disponibilidad del veterinario con horarios y slots disponibles
     */
    DisponibilidadVeterinarioDTO obtenerDisponibilidad(Long idVeterinario, LocalDate fecha);
}

