package com.veterinaria.clinica_veternica.service.interfaces;

import com.veterinaria.clinica_veternica.dto.request.inventario.InsumoRequestDTO;
import com.veterinaria.clinica_veternica.dto.response.inventario.InsumoResponseDTO;

import java.util.List;

/**
 * Interfaz del servicio para gestión de Insumos.
 *
 * @author Clínica Veterinaria Team
 * @version 1.0
 * @since 2025-01-XX
 */
public interface IInsumoService {

    InsumoResponseDTO crear(InsumoRequestDTO requestDTO);

    InsumoResponseDTO actualizar(Long id, InsumoRequestDTO requestDTO);

    InsumoResponseDTO buscarPorId(Long id);

    InsumoResponseDTO buscarPorCodigo(String codigo);

    List<InsumoResponseDTO> listarTodos();

    List<InsumoResponseDTO> listarActivos();

    List<InsumoResponseDTO> listarConStockBajo();

    List<InsumoResponseDTO> listarAgotados();

    List<InsumoResponseDTO> listarPorTipoInsumo(Long idTipoInsumo);

    List<InsumoResponseDTO> buscarPorNombre(String nombre);

    void eliminar(Long id);

    InsumoResponseDTO activar(Long id);

    InsumoResponseDTO desactivar(Long id);

    boolean existePorCodigo(String codigo);
}

