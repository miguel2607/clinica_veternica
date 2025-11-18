package com.veterinaria.clinica_veternica.service.interfaces;

import com.veterinaria.clinica_veternica.dto.request.usuario.AdministradorRequestDTO;
import com.veterinaria.clinica_veternica.dto.response.usuario.AdministradorResponseDTO;

import java.util.List;

/**
 * Interfaz del servicio para gestión de Administradores.
 *
 * @author Clínica Veterinaria Team
 * @version 1.0
 * @since 2025-11-12
 */
public interface IAdministradorService {

    AdministradorResponseDTO crear(AdministradorRequestDTO requestDTO);

    AdministradorResponseDTO actualizar(Long id, AdministradorRequestDTO requestDTO);

    AdministradorResponseDTO buscarPorId(Long id);

    List<AdministradorResponseDTO> listarTodos();

    List<AdministradorResponseDTO> listarActivos();

    List<AdministradorResponseDTO> buscarPorNombre(String nombre);

    void eliminar(Long id);

    AdministradorResponseDTO activar(Long id);
}

