package com.veterinaria.clinica_veternica.service.interfaces;

import com.veterinaria.clinica_veternica.dto.request.paciente.MascotaRequestDTO;
import com.veterinaria.clinica_veternica.dto.response.paciente.MascotaResponseDTO;

import java.util.List;

/**
 * Interfaz del servicio para gestión de Mascotas.
 *
 * @author Clínica Veterinaria Team
 * @version 1.0
 * @since 2025-11-03
 */
public interface IMascotaService {

    MascotaResponseDTO crear(MascotaRequestDTO requestDTO);

    MascotaResponseDTO actualizar(Long id, MascotaRequestDTO requestDTO);

    MascotaResponseDTO buscarPorId(Long id);

    List<MascotaResponseDTO> listarTodas();

    List<MascotaResponseDTO> listarActivas();

    List<MascotaResponseDTO> listarPorPropietario(Long idPropietario);

    List<MascotaResponseDTO> listarPorEspecie(Long idEspecie);

    List<MascotaResponseDTO> listarPorRaza(Long idRaza);

    List<MascotaResponseDTO> buscarPorNombre(String nombre);

    void eliminar(Long id);

    MascotaResponseDTO activar(Long id);

    boolean existePorNombreYPropietario(String nombre, Long idPropietario);
}
