package com.veterinaria.clinica_veternica.service.interfaces;

import com.veterinaria.clinica_veternica.dto.request.clinico.EvolucionClinicaRequestDTO;
import com.veterinaria.clinica_veternica.dto.response.clinico.EvolucionClinicaResponseDTO;

import java.util.List;

/**
 * Interfaz del servicio para gestión de Evoluciones Clínicas.
 * Sigue el principio de Interface Segregation (ISP).
 *
 * @author Clínica Veterinaria Team
 * @version 1.0
 * @since 2025-11-15
 */
public interface IEvolucionClinicaService {

    /**
     * Crea una nueva evolución clínica para una historia clínica.
     *
     * @param idHistoriaClinica ID de la historia clínica
     * @param requestDTO DTO con los datos de la evolución
     * @return Evolución clínica creada
     */
    EvolucionClinicaResponseDTO crear(Long idHistoriaClinica, EvolucionClinicaRequestDTO requestDTO);

    /**
     * Lista todas las evoluciones de una historia clínica.
     *
     * @param idHistoriaClinica ID de la historia clínica
     * @return Lista de evoluciones
     */
    List<EvolucionClinicaResponseDTO> listarPorHistoriaClinica(Long idHistoriaClinica);
}

