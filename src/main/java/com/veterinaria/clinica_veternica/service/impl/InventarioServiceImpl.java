package com.veterinaria.clinica_veternica.service.impl;

import com.veterinaria.clinica_veternica.domain.inventario.Inventario;
import com.veterinaria.clinica_veternica.domain.inventario.Insumo;
import com.veterinaria.clinica_veternica.dto.response.inventario.InventarioResponseDTO;
import com.veterinaria.clinica_veternica.exception.ResourceNotFoundException;

import com.veterinaria.clinica_veternica.mapper.inventario.InventarioMapper;
import com.veterinaria.clinica_veternica.patterns.structural.proxy.InventarioProxy;
import com.veterinaria.clinica_veternica.repository.InventarioRepository;
import com.veterinaria.clinica_veternica.repository.InsumoRepository;
import com.veterinaria.clinica_veternica.service.interfaces.IInventarioService;
import com.veterinaria.clinica_veternica.util.Constants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Implementación del servicio para gestión de Inventario.
 * Utiliza el patrón Proxy para control de acceso y auditoría.
 *
 * @author Clínica Veterinaria Team
 * @version 1.0
 * @since 2025-11-06
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class InventarioServiceImpl implements IInventarioService {

    private final InventarioRepository inventarioRepository;
    private final InsumoRepository insumoRepository;
    private final InventarioMapper inventarioMapper;
    private final InventarioProxy inventarioProxy;

    @Override
    @Transactional(readOnly = true)
    public InventarioResponseDTO buscarPorId(Long id) {
        Inventario inventario = inventarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(Constants.ENTIDAD_INVENTARIO, "id", id));
        return inventarioMapper.toResponseDTO(inventario);
    }

    @Override
    @Transactional(readOnly = true)
    public InventarioResponseDTO buscarPorInsumo(Long idInsumo) {
        Insumo insumo = insumoRepository.findById(idInsumo)
                .orElseThrow(() -> new ResourceNotFoundException(Constants.ENTIDAD_INSUMO, "id", idInsumo));

        Inventario inventario = inventarioRepository.findByInsumo(insumo)
                .orElseThrow(() -> new ResourceNotFoundException(Constants.ENTIDAD_INVENTARIO, "insumo", idInsumo));

        return inventarioMapper.toResponseDTO(inventario);
    }

    @Override
    @Transactional(readOnly = true)
    public List<InventarioResponseDTO> listarTodos() {
        List<Inventario> inventarios = inventarioRepository.findAll();
        return inventarioMapper.toResponseDTOList(inventarios);
    }

    @Override
    @Transactional(readOnly = true)
    public List<InventarioResponseDTO> listarConStockBajo() {
        List<Inventario> inventarios = inventarioRepository.findInventariosConStockBajo();
        return inventarioMapper.toResponseDTOList(inventarios);
    }

    @Override
    @Transactional(readOnly = true)
    public List<InventarioResponseDTO> listarAgotados() {
        List<Inventario> inventarios = inventarioRepository.findInventariosAgotados();
        return inventarioMapper.toResponseDTOList(inventarios);
    }

    @Override
    @Transactional(readOnly = true)
    public List<InventarioResponseDTO> listarOrdenadosPorValor() {
        List<Inventario> inventarios = inventarioRepository.findInventariosOrdenadosPorValor();
        return inventarioMapper.toResponseDTOList(inventarios);
    }
}

