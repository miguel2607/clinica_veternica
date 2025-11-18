package com.veterinaria.clinica_veternica.service.impl;

import com.veterinaria.clinica_veternica.domain.inventario.Insumo;
import com.veterinaria.clinica_veternica.domain.inventario.TipoInsumo;
import com.veterinaria.clinica_veternica.dto.request.inventario.InsumoRequestDTO;
import com.veterinaria.clinica_veternica.dto.response.inventario.InsumoResponseDTO;
import com.veterinaria.clinica_veternica.exception.BusinessException;
import com.veterinaria.clinica_veternica.exception.ResourceNotFoundException;
import com.veterinaria.clinica_veternica.exception.ValidationException;
import com.veterinaria.clinica_veternica.mapper.inventario.InsumoMapper;
import com.veterinaria.clinica_veternica.repository.InsumoRepository;
import com.veterinaria.clinica_veternica.repository.TipoInsumoRepository;
import com.veterinaria.clinica_veternica.service.interfaces.IInsumoService;
import com.veterinaria.clinica_veternica.util.Constants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Implementación del servicio para gestión de Insumos.
 *
 * @author Clínica Veterinaria Team
 * @version 1.0
 * @since 2025-01-XX
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class InsumoServiceImpl implements IInsumoService {

    private final InsumoRepository insumoRepository;
    private final TipoInsumoRepository tipoInsumoRepository;
    private final InsumoMapper insumoMapper;

    @Override
    public InsumoResponseDTO crear(InsumoRequestDTO requestDTO) {
        // Validar que no exista el código
        if (insumoRepository.existsByCodigo(requestDTO.getCodigo())) {
            throw new ValidationException(
                "Ya existe un insumo con el código " + requestDTO.getCodigo(),
                "codigo",
                "El código ya está registrado"
            );
        }

        // Validar que exista el tipo de insumo
        TipoInsumo tipoInsumo = tipoInsumoRepository.findById(requestDTO.getIdTipoInsumo())
            .orElseThrow(() -> new ResourceNotFoundException(Constants.ENTIDAD_TIPO_INSUMO, "id", requestDTO.getIdTipoInsumo()));

        Insumo insumo = insumoMapper.toEntity(requestDTO);
        insumo.setTipoInsumo(tipoInsumo);

        if (insumo.getActivo() == null) {
            insumo.setActivo(true);
        }

        // Actualizar estado según stock
        if (insumo.getCantidadStock() == 0) {
            insumo.marcarComoAgotado();
        } else if (insumo.esStockBajo()) {
            insumo.marcarComoDisponible();
        }

        Insumo insumoGuardado = insumoRepository.save(insumo);
        log.info("Insumo creado: {} ({})", insumoGuardado.getNombre(), insumoGuardado.getCodigo());
        return insumoMapper.toResponseDTO(insumoGuardado);
    }

    @Override
    public InsumoResponseDTO actualizar(Long id, InsumoRequestDTO requestDTO) {
        Insumo insumo = insumoRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(Constants.ENTIDAD_INSUMO, "id", id));

        // Validar código único (si cambió)
        if (!insumo.getCodigo().equals(requestDTO.getCodigo()) &&
            insumoRepository.existsByCodigo(requestDTO.getCodigo())) {
            throw new ValidationException(
                "Ya existe otro insumo con el código " + requestDTO.getCodigo(),
                "codigo",
                "El código ya está registrado"
            );
        }

        // Validar que exista el tipo de insumo
        TipoInsumo tipoInsumo = tipoInsumoRepository.findById(requestDTO.getIdTipoInsumo())
            .orElseThrow(() -> new ResourceNotFoundException(Constants.ENTIDAD_TIPO_INSUMO, "id", requestDTO.getIdTipoInsumo()));

        insumoMapper.updateEntityFromDTO(requestDTO, insumo);
        insumo.setTipoInsumo(tipoInsumo);

        // Actualizar estado según stock
        if (insumo.getCantidadStock() == 0) {
            insumo.marcarComoAgotado();
        } else if (insumo.esStockBajo()) {
            insumo.marcarComoDisponible();
        }

        Insumo insumoActualizado = insumoRepository.save(insumo);
        log.info("Insumo actualizado: {} ({})", insumoActualizado.getNombre(), insumoActualizado.getCodigo());
        return insumoMapper.toResponseDTO(insumoActualizado);
    }

    @Override
    @Transactional(readOnly = true)
    public InsumoResponseDTO buscarPorId(Long id) {
        Insumo insumo = insumoRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(Constants.ENTIDAD_INSUMO, "id", id));
        return insumoMapper.toResponseDTO(insumo);
    }

    @Override
    @Transactional(readOnly = true)
    public InsumoResponseDTO buscarPorCodigo(String codigo) {
        Insumo insumo = insumoRepository.findByCodigo(codigo)
            .orElseThrow(() -> new ResourceNotFoundException(Constants.ENTIDAD_INSUMO, "codigo", codigo));
        return insumoMapper.toResponseDTO(insumo);
    }

    @Override
    @Transactional(readOnly = true)
    public List<InsumoResponseDTO> listarTodos() {
        return insumoMapper.toResponseDTOList(insumoRepository.findAll());
    }

    @Override
    @Transactional(readOnly = true)
    public List<InsumoResponseDTO> listarActivos() {
        return insumoMapper.toResponseDTOList(insumoRepository.findInsumosActivos());
    }

    @Override
    @Transactional(readOnly = true)
    public List<InsumoResponseDTO> listarConStockBajo() {
        return insumoMapper.toResponseDTOList(insumoRepository.findInsumosConStockBajo());
    }

    @Override
    @Transactional(readOnly = true)
    public List<InsumoResponseDTO> listarAgotados() {
        return insumoMapper.toResponseDTOList(insumoRepository.findInsumosAgotados());
    }

    @Override
    @Transactional(readOnly = true)
    public List<InsumoResponseDTO> listarPorTipoInsumo(Long idTipoInsumo) {
        TipoInsumo tipoInsumo = tipoInsumoRepository.findById(idTipoInsumo)
            .orElseThrow(() -> new ResourceNotFoundException(Constants.ENTIDAD_TIPO_INSUMO, "id", idTipoInsumo));
        return insumoMapper.toResponseDTOList(insumoRepository.findInsumosActivosPorTipo(tipoInsumo));
    }

    @Override
    @Transactional(readOnly = true)
    public List<InsumoResponseDTO> buscarPorNombre(String nombre) {
        return insumoMapper.toResponseDTOList(insumoRepository.buscarInsumos(nombre));
    }

    @Override
    public void eliminar(Long id) {
        Insumo insumo = insumoRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(Constants.ENTIDAD_INSUMO, "id", id));

        insumoRepository.delete(insumo);
        log.info("Insumo eliminado: {} ({})", insumo.getNombre(), insumo.getCodigo());
    }

    @Override
    public InsumoResponseDTO activar(Long id) {
        Insumo insumo = insumoRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(Constants.ENTIDAD_INSUMO, "id", id));
        insumo.activar();
        Insumo insumoActualizado = insumoRepository.save(insumo);
        log.info("Insumo activado: {} ({})", insumoActualizado.getNombre(), insumoActualizado.getCodigo());
        return insumoMapper.toResponseDTO(insumoActualizado);
    }

    @Override
    public InsumoResponseDTO desactivar(Long id) {
        Insumo insumo = insumoRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(Constants.ENTIDAD_INSUMO, "id", id));
        insumo.desactivar();
        Insumo insumoActualizado = insumoRepository.save(insumo);
        log.info("Insumo desactivado: {} ({})", insumoActualizado.getNombre(), insumoActualizado.getCodigo());
        return insumoMapper.toResponseDTO(insumoActualizado);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existePorCodigo(String codigo) {
        return insumoRepository.existsByCodigo(codigo);
    }
}

