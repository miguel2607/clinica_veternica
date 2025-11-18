package com.veterinaria.clinica_veternica.service.interfaces;

import com.veterinaria.clinica_veternica.dto.request.usuario.AuxiliarVeterinarioRequestDTO;
import com.veterinaria.clinica_veternica.dto.response.usuario.AuxiliarVeterinarioResponseDTO;

import java.util.List;

/**
 * Interfaz del servicio para gestión de Auxiliares Veterinarios.
 *
 * @author Clínica Veterinaria Team
 * @version 1.0
 * @since 2025-11-12
 */
public interface IAuxiliarVeterinarioService {

    AuxiliarVeterinarioResponseDTO crear(AuxiliarVeterinarioRequestDTO requestDTO);

    AuxiliarVeterinarioResponseDTO actualizar(Long id, AuxiliarVeterinarioRequestDTO requestDTO);

    AuxiliarVeterinarioResponseDTO buscarPorId(Long id);

    List<AuxiliarVeterinarioResponseDTO> listarTodos();

    List<AuxiliarVeterinarioResponseDTO> listarActivos();

    List<AuxiliarVeterinarioResponseDTO> buscarPorNombre(String nombre);

    void eliminar(Long id);

    AuxiliarVeterinarioResponseDTO activar(Long id);
}

