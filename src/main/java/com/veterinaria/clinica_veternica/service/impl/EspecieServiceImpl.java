package com.veterinaria.clinica_veternica.service.impl;

import com.veterinaria.clinica_veternica.domain.paciente.Especie;
import com.veterinaria.clinica_veternica.dto.request.paciente.EspecieRequestDTO;
import com.veterinaria.clinica_veternica.dto.response.paciente.EspecieResponseDTO;
import com.veterinaria.clinica_veternica.exception.BusinessException;
import com.veterinaria.clinica_veternica.exception.ResourceNotFoundException;
import com.veterinaria.clinica_veternica.exception.ValidationException;
import com.veterinaria.clinica_veternica.mapper.paciente.EspecieMapper;
import com.veterinaria.clinica_veternica.repository.EspecieRepository;
import com.veterinaria.clinica_veternica.service.interfaces.IEspecieService;
import com.veterinaria.clinica_veternica.util.Constants;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Implementación del servicio de Especies.
 *
 * @author Clínica Veterinaria Team
 * @version 1.0
 * @since 2025-11-03
 */
@Service
@RequiredArgsConstructor
@Transactional
public class EspecieServiceImpl implements IEspecieService {

    private final EspecieRepository especieRepository;
    private final EspecieMapper especieMapper;

    @Override
    public EspecieResponseDTO crear(EspecieRequestDTO requestDTO) {
        // Validar que no exista una especie con el mismo nombre
        if (especieRepository.existsByNombreIgnoreCase(requestDTO.getNombre())) {
            throw new ValidationException(
                "Ya existe una especie con el nombre: " + requestDTO.getNombre(),
                "nombre",
                "El nombre de la especie ya está registrado"
            );
        }

        // Convertir DTO a entidad
        Especie especie = especieMapper.toEntity(requestDTO);

        // Establecer activo por defecto si no se especifica
        if (especie.getActivo() == null) {
            especie.setActivo(true);
        }

        // Guardar
        Especie especieGuardada = especieRepository.save(especie);

        // Retornar DTO de respuesta
        return especieMapper.toResponseDTO(especieGuardada);
    }

    @Override
    public EspecieResponseDTO actualizar(Long id, EspecieRequestDTO requestDTO) {
        // Buscar especie existente
        Especie especie = especieRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(Constants.ENTIDAD_ESPECIE, "id", id));

        // Validar que no exista otra especie con el mismo nombre
        if (!especie.getNombre().equalsIgnoreCase(requestDTO.getNombre()) &&
            especieRepository.existsByNombreIgnoreCase(requestDTO.getNombre())) {
            throw new ValidationException(
                "Ya existe otra especie con el nombre: " + requestDTO.getNombre(),
                "nombre",
                "El nombre de la especie ya está registrado"
            );
        }

        // Actualizar campos
        especieMapper.updateEntityFromDTO(requestDTO, especie);

        // Guardar cambios
        Especie especieActualizada = especieRepository.save(especie);

        return especieMapper.toResponseDTO(especieActualizada);
    }

    @Override
    @Transactional(readOnly = true)
    public EspecieResponseDTO buscarPorId(Long id) {
        Especie especie = especieRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(Constants.ENTIDAD_ESPECIE, "id", id));

        return especieMapper.toResponseDTO(especie);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EspecieResponseDTO> listarTodas() {
        List<Especie> especies = especieRepository.findAll();
        return especieMapper.toResponseDTOList(especies);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EspecieResponseDTO> listarActivas() {
        List<Especie> especies = especieRepository.findByActivoTrue();
        return especieMapper.toResponseDTOList(especies);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EspecieResponseDTO> buscarPorNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new ValidationException("El nombre de búsqueda no puede estar vacío");
        }

        List<Especie> especies = especieRepository.findByNombreContainingIgnoreCase(nombre.trim());
        return especieMapper.toResponseDTOList(especies);
    }

    @Override
    public void eliminar(Long id) {
        Especie especie = especieRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(Constants.ENTIDAD_ESPECIE, "id", id));

        // Verificar si tiene razas asociadas
        if (especie.getRazas() != null && !especie.getRazas().isEmpty()) {
            throw new BusinessException(
                "No se puede eliminar la especie porque tiene " +
                especie.getRazas().size() + " raza(s) asociada(s)",
                "ESPECIE_CON_RAZAS"
            );
        }

        // Soft delete - marcar como inactiva
        especie.setActivo(false);
        especieRepository.save(especie);
    }

    @Override
    public EspecieResponseDTO activar(Long id) {
        Especie especie = especieRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(Constants.ENTIDAD_ESPECIE, "id", id));

        if (Constants.isTrue(especie.getActivo())) {
            throw new BusinessException("La especie ya está activa", "ESPECIE_YA_ACTIVA");
        }

        especie.setActivo(true);
        Especie especieActivada = especieRepository.save(especie);

        return especieMapper.toResponseDTO(especieActivada);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existePorNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            return false;
        }
        return especieRepository.existsByNombreIgnoreCase(nombre.trim());
    }
}
