package com.veterinaria.clinica_veternica.service.interfaces;

import com.veterinaria.clinica_veternica.dto.request.usuario.RecepcionistaRequestDTO;
import com.veterinaria.clinica_veternica.dto.response.usuario.RecepcionistaResponseDTO;

import java.util.List;

/**
 * Interfaz del servicio para gestión de Recepcionistas.
 *
 * @author Clínica Veterinaria Team
 * @version 1.0
 * @since 2025-11-12
 */
public interface IRecepcionistaService {

    RecepcionistaResponseDTO crear(RecepcionistaRequestDTO requestDTO);

    RecepcionistaResponseDTO actualizar(Long id, RecepcionistaRequestDTO requestDTO);

    RecepcionistaResponseDTO buscarPorId(Long id);

    List<RecepcionistaResponseDTO> listarTodos();

    List<RecepcionistaResponseDTO> listarActivos();

    List<RecepcionistaResponseDTO> buscarPorNombre(String nombre);

    void eliminar(Long id);

    RecepcionistaResponseDTO activar(Long id);
}

