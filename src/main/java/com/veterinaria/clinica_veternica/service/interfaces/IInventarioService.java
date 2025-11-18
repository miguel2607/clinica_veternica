package com.veterinaria.clinica_veternica.service.interfaces;

import com.veterinaria.clinica_veternica.dto.response.inventario.InventarioResponseDTO;

import java.util.List;

/**
 * Interfaz del servicio para gestión de Inventario.
 * Utiliza el patrón Proxy para control de acceso.
 *
 * @author Clínica Veterinaria Team
 * @version 1.0
 * @since 2025-11-06
 */
public interface IInventarioService {

    InventarioResponseDTO buscarPorId(Long id);

    InventarioResponseDTO buscarPorInsumo(Long idInsumo);

    List<InventarioResponseDTO> listarTodos();

    List<InventarioResponseDTO> listarConStockBajo();

    List<InventarioResponseDTO> listarAgotados();

    List<InventarioResponseDTO> listarOrdenadosPorValor();
}

