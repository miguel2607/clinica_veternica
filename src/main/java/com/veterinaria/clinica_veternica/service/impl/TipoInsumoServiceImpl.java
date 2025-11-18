package com.veterinaria.clinica_veternica.service.impl;

import com.veterinaria.clinica_veternica.domain.inventario.TipoInsumo;
import com.veterinaria.clinica_veternica.dto.request.inventario.TipoInsumoRequestDTO;
import com.veterinaria.clinica_veternica.dto.response.inventario.TipoInsumoResponseDTO;
import com.veterinaria.clinica_veternica.exception.BusinessException;
import com.veterinaria.clinica_veternica.exception.ResourceNotFoundException;
import com.veterinaria.clinica_veternica.exception.ValidationException;
import com.veterinaria.clinica_veternica.mapper.inventario.TipoInsumoMapper;
import com.veterinaria.clinica_veternica.repository.TipoInsumoRepository;
import com.veterinaria.clinica_veternica.service.interfaces.ITipoInsumoService;
import com.veterinaria.clinica_veternica.util.Constants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Implementación del servicio para gestión de Tipos de Insumo.
 *
 * @author Clínica Veterinaria Team
 * @version 1.0
 * @since 2025-01-XX
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class TipoInsumoServiceImpl implements ITipoInsumoService {

    private final TipoInsumoRepository tipoInsumoRepository;
    private final TipoInsumoMapper tipoInsumoMapper;

    @Override
    public TipoInsumoResponseDTO crear(TipoInsumoRequestDTO requestDTO) {
        // Validar que no exista el nombre
        if (tipoInsumoRepository.existsByNombre(requestDTO.getNombre())) {
            throw new ValidationException(
                "Ya existe un tipo de insumo con el nombre " + requestDTO.getNombre(),
                "nombre",
                "El nombre ya está registrado"
            );
        }

        TipoInsumo tipoInsumo = tipoInsumoMapper.toEntity(requestDTO);

        if (tipoInsumo.getActivo() == null) {
            tipoInsumo.setActivo(true);
        }

        TipoInsumo tipoInsumoGuardado = tipoInsumoRepository.save(tipoInsumo);
        log.info("Tipo de insumo creado: {}", tipoInsumoGuardado.getNombre());
        return tipoInsumoMapper.toResponseDTO(tipoInsumoGuardado);
    }

    @Override
    public TipoInsumoResponseDTO actualizar(Long id, TipoInsumoRequestDTO requestDTO) {
        TipoInsumo tipoInsumo = tipoInsumoRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(Constants.ENTIDAD_TIPO_INSUMO, "id", id));

        // Validar nombre único (si cambió)
        if (!tipoInsumo.getNombre().equals(requestDTO.getNombre()) &&
            tipoInsumoRepository.existsByNombre(requestDTO.getNombre())) {
            throw new ValidationException(
                "Ya existe otro tipo de insumo con el nombre " + requestDTO.getNombre(),
                "nombre",
                "El nombre ya está registrado"
            );
        }

        tipoInsumoMapper.updateEntityFromDTO(requestDTO, tipoInsumo);
        TipoInsumo tipoInsumoActualizado = tipoInsumoRepository.save(tipoInsumo);
        log.info("Tipo de insumo actualizado: {}", tipoInsumoActualizado.getNombre());
        return tipoInsumoMapper.toResponseDTO(tipoInsumoActualizado);
    }

    @Override
    @Transactional(readOnly = true)
    public TipoInsumoResponseDTO buscarPorId(Long id) {
        TipoInsumo tipoInsumo = tipoInsumoRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(Constants.ENTIDAD_TIPO_INSUMO, "id", id));
        return tipoInsumoMapper.toResponseDTO(tipoInsumo);
    }

    @Override
    @Transactional(readOnly = true)
    public TipoInsumoResponseDTO buscarPorNombre(String nombre) {
        TipoInsumo tipoInsumo = tipoInsumoRepository.findByNombre(nombre)
            .orElseThrow(() -> new ResourceNotFoundException("Tipo de Insumo", "nombre", nombre));
        return tipoInsumoMapper.toResponseDTO(tipoInsumo);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TipoInsumoResponseDTO> listarTodos() {
        return tipoInsumoMapper.toResponseDTOList(tipoInsumoRepository.findAll());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TipoInsumoResponseDTO> listarActivos() {
        return tipoInsumoMapper.toResponseDTOList(tipoInsumoRepository.findTiposActivos());
    }

    @Override
    public void eliminar(Long id) {
        TipoInsumo tipoInsumo = tipoInsumoRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(Constants.ENTIDAD_TIPO_INSUMO, "id", id));

        // Validar que no tenga insumos asociados
        if (tipoInsumo.getCantidadInsumos() > 0) {
            throw new BusinessException(
                "No se puede eliminar el tipo de insumo porque tiene " + tipoInsumo.getCantidadInsumos() + " insumos asociados"
            );
        }

        tipoInsumoRepository.delete(tipoInsumo);
        log.info("Tipo de insumo eliminado: {}", tipoInsumo.getNombre());
    }

    @Override
    public TipoInsumoResponseDTO activar(Long id) {
        TipoInsumo tipoInsumo = tipoInsumoRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(Constants.ENTIDAD_TIPO_INSUMO, "id", id));
        tipoInsumo.activar();
        TipoInsumo tipoInsumoActualizado = tipoInsumoRepository.save(tipoInsumo);
        log.info("Tipo de insumo activado: {}", tipoInsumoActualizado.getNombre());
        return tipoInsumoMapper.toResponseDTO(tipoInsumoActualizado);
    }

    @Override
    public TipoInsumoResponseDTO desactivar(Long id) {
        TipoInsumo tipoInsumo = tipoInsumoRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(Constants.ENTIDAD_TIPO_INSUMO, "id", id));
        tipoInsumo.desactivar();
        TipoInsumo tipoInsumoActualizado = tipoInsumoRepository.save(tipoInsumo);
        log.info("Tipo de insumo desactivado: {}", tipoInsumoActualizado.getNombre());
        return tipoInsumoMapper.toResponseDTO(tipoInsumoActualizado);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existePorNombre(String nombre) {
        return tipoInsumoRepository.existsByNombre(nombre);
    }
}

