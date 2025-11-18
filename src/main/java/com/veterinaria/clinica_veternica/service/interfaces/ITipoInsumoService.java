package com.veterinaria.clinica_veternica.service.interfaces;

import com.veterinaria.clinica_veternica.dto.request.inventario.TipoInsumoRequestDTO;
import com.veterinaria.clinica_veternica.dto.response.inventario.TipoInsumoResponseDTO;

import java.util.List;

/**
 * Interfaz del servicio para gestión de Tipos de Insumo.
 *
 * @author Clínica Veterinaria Team
 * @version 1.0
 * @since 2025-01-XX
 */
public interface ITipoInsumoService {

    TipoInsumoResponseDTO crear(TipoInsumoRequestDTO requestDTO);

    TipoInsumoResponseDTO actualizar(Long id, TipoInsumoRequestDTO requestDTO);

    TipoInsumoResponseDTO buscarPorId(Long id);

    TipoInsumoResponseDTO buscarPorNombre(String nombre);

    List<TipoInsumoResponseDTO> listarTodos();

    List<TipoInsumoResponseDTO> listarActivos();

    void eliminar(Long id);

    TipoInsumoResponseDTO activar(Long id);

    TipoInsumoResponseDTO desactivar(Long id);

    boolean existePorNombre(String nombre);
}

