package com.veterinaria.clinica_veternica.service.interfaces;

import com.veterinaria.clinica_veternica.dto.request.clinico.VacunacionRequestDTO;
import com.veterinaria.clinica_veternica.dto.response.clinico.VacunacionResponseDTO;

import java.util.List;

/**
 * Interfaz del servicio para gestión de Vacunaciones.
 * Sigue el principio de Interface Segregation (ISP).
 *
 * @author Clínica Veterinaria Team
 * @version 1.0
 * @since 2025-11-15
 */
public interface IVacunacionService {

    /**
     * Crea una nueva vacunación para una historia clínica.
     *
     * @param idHistoriaClinica ID de la historia clínica
     * @param requestDTO DTO con los datos de la vacunación
     * @return Vacunación creada
     */
    VacunacionResponseDTO crear(Long idHistoriaClinica, VacunacionRequestDTO requestDTO);

    /**
     * Lista todas las vacunaciones de una historia clínica.
     *
     * @param idHistoriaClinica ID de la historia clínica
     * @return Lista de vacunaciones
     */
    List<VacunacionResponseDTO> listarPorHistoriaClinica(Long idHistoriaClinica);

    /**
     * Lista todas las vacunaciones.
     *
     * @return Lista de todas las vacunaciones
     */
    List<VacunacionResponseDTO> listarTodas();
}

