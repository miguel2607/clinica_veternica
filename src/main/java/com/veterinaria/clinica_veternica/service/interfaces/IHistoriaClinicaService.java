package com.veterinaria.clinica_veternica.service.interfaces;

import com.veterinaria.clinica_veternica.dto.request.clinico.HistoriaClinicaRequestDTO;
import com.veterinaria.clinica_veternica.dto.response.clinico.HistoriaClinicaResponseDTO;

import java.util.List;

/**
 * Interfaz del servicio para gestión de Historias Clínicas.
 * Utiliza los patrones Memento y Builder.
 *
 * @author Clínica Veterinaria Team
 * @version 1.0
 * @since 2025-11-06
 */
public interface IHistoriaClinicaService {

    HistoriaClinicaResponseDTO crear(HistoriaClinicaRequestDTO requestDTO);

    HistoriaClinicaResponseDTO crearConBuilder(Long idMascota, HistoriaClinicaRequestDTO requestDTO);

    HistoriaClinicaResponseDTO actualizar(Long id, HistoriaClinicaRequestDTO requestDTO);

    HistoriaClinicaResponseDTO buscarPorId(Long id);

    HistoriaClinicaResponseDTO buscarPorMascota(Long idMascota);

    List<HistoriaClinicaResponseDTO> listarTodos();

    List<HistoriaClinicaResponseDTO> listarActivas();

    void guardarMemento(Long id);

    boolean restaurarUltimoMemento(Long id);

    boolean restaurarMemento(Long id, int indice);

    int obtenerCantidadMementos(Long id);

    void archivar(Long id, String motivo);

    void reactivar(Long id);
}

