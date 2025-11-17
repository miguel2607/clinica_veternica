package com.veterinaria.clinica_veternica.service.impl;

import com.veterinaria.clinica_veternica.domain.paciente.Especie;
import com.veterinaria.clinica_veternica.domain.paciente.Raza;
import com.veterinaria.clinica_veternica.dto.request.paciente.RazaRequestDTO;
import com.veterinaria.clinica_veternica.dto.response.paciente.RazaResponseDTO;
import com.veterinaria.clinica_veternica.exception.BusinessException;
import com.veterinaria.clinica_veternica.exception.ResourceNotFoundException;
import com.veterinaria.clinica_veternica.exception.ValidationException;
import com.veterinaria.clinica_veternica.mapper.paciente.RazaMapper;
import com.veterinaria.clinica_veternica.repository.EspecieRepository;
import com.veterinaria.clinica_veternica.repository.RazaRepository;
import com.veterinaria.clinica_veternica.service.interfaces.IRazaService;
import com.veterinaria.clinica_veternica.util.Constants;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Implementación del servicio de Razas.
 *
 * @author Clínica Veterinaria Team
 * @version 1.0
 * @since 2025-11-03
 */
@Service
@RequiredArgsConstructor
@Transactional
public class RazaServiceImpl implements IRazaService {

    private final RazaRepository razaRepository;
    private final EspecieRepository especieRepository;
    private final RazaMapper razaMapper;

    @Override
    public RazaResponseDTO crear(RazaRequestDTO requestDTO) {
        // Validar que la especie existe
        Especie especie = especieRepository.findById(requestDTO.getIdEspecie())
            .orElseThrow(() -> new ResourceNotFoundException("Especie", "id", requestDTO.getIdEspecie()));

        // Validar que no exista una raza con el mismo nombre para esta especie
        if (razaRepository.existsByNombreAndEspecieId(requestDTO.getNombre(), requestDTO.getIdEspecie())) {
            throw new ValidationException(
                "Ya existe una raza con el nombre '" + requestDTO.getNombre() +
                "' para la especie '" + especie.getNombre() + "'",
                "nombre",
                "El nombre de la raza ya está registrado para esta especie"
            );
        }

        // Convertir DTO a entidad
        Raza raza = razaMapper.toEntity(requestDTO);
        raza.setEspecie(especie);

        // Establecer activo por defecto si no se especifica
        if (raza.getActivo() == null) {
            raza.setActivo(true);
        }

        // Guardar
        Raza razaGuardada = razaRepository.save(raza);

        // Retornar DTO de respuesta
        return razaMapper.toResponseDTO(razaGuardada);
    }

    @Override
    public RazaResponseDTO actualizar(Long id, RazaRequestDTO requestDTO) {
        // Buscar raza existente
        Raza raza = razaRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Raza", "id", id));

        // Validar que la nueva especie existe (si se está cambiando)
        Especie nuevaEspecie = especieRepository.findById(requestDTO.getIdEspecie())
            .orElseThrow(() -> new ResourceNotFoundException("Especie", "id", requestDTO.getIdEspecie()));

        // Validar que no exista otra raza con el mismo nombre para la especie
        if (razaRepository.existsByNombreAndEspecieIdAndIdNot(
                requestDTO.getNombre(),
                requestDTO.getIdEspecie(),
                id)) {
            throw new ValidationException(
                "Ya existe otra raza con el nombre '" + requestDTO.getNombre() +
                "' para la especie '" + nuevaEspecie.getNombre() + "'",
                "nombre",
                "El nombre de la raza ya está registrado para esta especie"
            );
        }

        // Actualizar campos
        razaMapper.updateEntityFromDTO(requestDTO, raza);
        raza.setEspecie(nuevaEspecie);

        // Guardar cambios
        Raza razaActualizada = razaRepository.save(raza);

        return razaMapper.toResponseDTO(razaActualizada);
    }

    @Override
    @Transactional(readOnly = true)
    public RazaResponseDTO buscarPorId(Long id) {
        Raza raza = razaRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Raza", "id", id));

        return razaMapper.toResponseDTO(raza);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RazaResponseDTO> listarTodas() {
        List<Raza> razas = razaRepository.findAll();
        return razaMapper.toResponseDTOList(razas);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RazaResponseDTO> listarPorEspecie(Long idEspecie) {
        // Validar que la especie existe
        if (!especieRepository.existsById(idEspecie)) {
            throw new ResourceNotFoundException("Especie", "id", idEspecie);
        }

        List<Raza> razas = razaRepository.findByEspecieId(idEspecie);
        return razaMapper.toResponseDTOList(razas);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RazaResponseDTO> listarActivas() {
        List<Raza> razas = razaRepository.findByActivoTrue();
        return razaMapper.toResponseDTOList(razas);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RazaResponseDTO> listarActivasPorEspecie(Long idEspecie) {
        // Validar que la especie existe
        if (!especieRepository.existsById(idEspecie)) {
            throw new ResourceNotFoundException("Especie", "id", idEspecie);
        }

        List<Raza> razas = razaRepository.findByEspecieIdAndActivoTrue(idEspecie);
        return razaMapper.toResponseDTOList(razas);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RazaResponseDTO> buscarPorNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new ValidationException("El nombre de búsqueda no puede estar vacío");
        }

        List<Raza> razas = razaRepository.findByNombreContainingIgnoreCase(nombre.trim());
        return razaMapper.toResponseDTOList(razas);
    }

    @Override
    public void eliminar(Long id) {
        Raza raza = razaRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Raza", "id", id));

        // Verificar si tiene mascotas asociadas
        if (raza.getMascotas() != null && !raza.getMascotas().isEmpty()) {
            throw new BusinessException(
                "No se puede eliminar la raza porque tiene " +
                raza.getMascotas().size() + " mascota(s) asociada(s)",
                "RAZA_CON_MASCOTAS"
            );
        }

        // Soft delete - marcar como inactiva
        raza.setActivo(false);
        razaRepository.save(raza);
    }

    @Override
    public RazaResponseDTO activar(Long id) {
        Raza raza = razaRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Raza", "id", id));

        if (Constants.isTrue(raza.getActivo())) {
            throw new BusinessException("La raza ya está activa", "RAZA_YA_ACTIVA");
        }

        raza.setActivo(true);
        Raza razaActivada = razaRepository.save(raza);

        return razaMapper.toResponseDTO(razaActivada);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existePorNombreYEspecie(String nombre, Long idEspecie) {
        if (nombre == null || nombre.trim().isEmpty() || idEspecie == null) {
            return false;
        }
        return razaRepository.existsByNombreAndEspecieId(nombre.trim(), idEspecie);
    }
}
