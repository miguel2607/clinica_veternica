package com.veterinaria.clinica_veternica.service.impl;

import com.veterinaria.clinica_veternica.domain.usuario.Usuario;
import com.veterinaria.clinica_veternica.domain.usuario.Veterinario;
import com.veterinaria.clinica_veternica.dto.request.usuario.VeterinarioRequestDTO;
import com.veterinaria.clinica_veternica.dto.response.usuario.VeterinarioResponseDTO;
import com.veterinaria.clinica_veternica.exception.BusinessException;
import com.veterinaria.clinica_veternica.exception.ResourceNotFoundException;
import com.veterinaria.clinica_veternica.exception.ValidationException;
import com.veterinaria.clinica_veternica.mapper.usuario.VeterinarioMapper;
import com.veterinaria.clinica_veternica.repository.UsuarioRepository;
import com.veterinaria.clinica_veternica.repository.VeterinarioRepository;
import com.veterinaria.clinica_veternica.service.interfaces.IVeterinarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class VeterinarioServiceImpl implements IVeterinarioService {

    private final VeterinarioRepository veterinarioRepository;
    private final UsuarioRepository usuarioRepository;
    private final VeterinarioMapper veterinarioMapper;

    @Override
    public VeterinarioResponseDTO crear(VeterinarioRequestDTO requestDTO) {
        // Validar registro profesional único
        if (veterinarioRepository.existsByRegistroProfesional(requestDTO.getRegistroProfesional())) {
            throw new ValidationException(
                "Ya existe un veterinario con el registro profesional: " + requestDTO.getRegistroProfesional(),
                "registroProfesional",
                "El registro profesional ya está registrado"
            );
        }

        Veterinario veterinario = veterinarioMapper.toEntity(requestDTO);

        if (veterinario.getActivo() == null) {
            veterinario.setActivo(true);
        }

        Veterinario veterinarioGuardado = veterinarioRepository.save(veterinario);
        return veterinarioMapper.toResponseDTO(veterinarioGuardado);
    }

    @Override
    public VeterinarioResponseDTO actualizar(Long id, VeterinarioRequestDTO requestDTO) {
        Veterinario veterinario = veterinarioRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Veterinario", "id", id));

        // Validar registro profesional único (si cambió)
        if (!veterinario.getRegistroProfesional().equals(requestDTO.getRegistroProfesional()) &&
            veterinarioRepository.existsByRegistroProfesional(requestDTO.getRegistroProfesional())) {
            throw new ValidationException(
                "Ya existe otro veterinario con el registro profesional: " + requestDTO.getRegistroProfesional(),
                "registroProfesional",
                "El registro profesional ya está registrado"
            );
        }

        veterinarioMapper.updateEntityFromDTO(requestDTO, veterinario);

        Veterinario veterinarioActualizado = veterinarioRepository.save(veterinario);
        return veterinarioMapper.toResponseDTO(veterinarioActualizado);
    }

    @Override
    @Transactional(readOnly = true)
    public VeterinarioResponseDTO buscarPorId(Long id) {
        Veterinario veterinario = veterinarioRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Veterinario", "id", id));
        return veterinarioMapper.toResponseDTO(veterinario);
    }

    @Override
    @Transactional(readOnly = true)
    public VeterinarioResponseDTO buscarPorRegistroProfesional(String registroProfesional) {
        Veterinario veterinario = veterinarioRepository.findByRegistroProfesional(registroProfesional)
            .orElseThrow(() -> new ResourceNotFoundException("Veterinario", "registroProfesional", registroProfesional));
        return veterinarioMapper.toResponseDTO(veterinario);
    }

    @Override
    @Transactional(readOnly = true)
    public List<VeterinarioResponseDTO> listarTodos() {
        List<Veterinario> veterinarios = veterinarioRepository.findAll();
        return veterinarioMapper.toResponseDTOList(veterinarios);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<VeterinarioResponseDTO> listarTodos(Pageable pageable) {
        Page<Veterinario> veterinariosPage = veterinarioRepository.findAll(pageable);
        return veterinariosPage.map(veterinarioMapper::toResponseDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public List<VeterinarioResponseDTO> listarActivos() {
        List<Veterinario> veterinarios = veterinarioRepository.findByActivoTrue();
        return veterinarioMapper.toResponseDTOList(veterinarios);
    }

    @Override
    @Transactional(readOnly = true)
    public List<VeterinarioResponseDTO> listarDisponibles() {
        List<Veterinario> veterinarios = veterinarioRepository.findVeterinariosDisponibles();
        return veterinarioMapper.toResponseDTOList(veterinarios);
    }

    @Override
    @Transactional(readOnly = true)
    public List<VeterinarioResponseDTO> listarPorEspecialidad(String especialidad) {
        if (especialidad == null || especialidad.trim().isEmpty()) {
            throw new ValidationException("La especialidad de búsqueda no puede estar vacía");
        }
        List<Veterinario> veterinarios = veterinarioRepository.findByEspecialidadContainingIgnoreCase(especialidad.trim());
        return veterinarioMapper.toResponseDTOList(veterinarios);
    }

    @Override
    @Transactional(readOnly = true)
    public List<VeterinarioResponseDTO> buscarPorNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new ValidationException("El nombre de búsqueda no puede estar vacío");
        }
        List<Veterinario> veterinarios = veterinarioRepository.buscarPorNombre(nombre.trim());
        return veterinarioMapper.toResponseDTOList(veterinarios);
    }

    @Override
    public void eliminar(Long id) {
        Veterinario veterinario = veterinarioRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Veterinario", "id", id));

        // Verificar si tiene citas asociadas
        if (veterinario.getCitas() != null && !veterinario.getCitas().isEmpty()) {
            throw new BusinessException(
                "No se puede eliminar el veterinario porque tiene " +
                veterinario.getCitas().size() + " cita(s) asociada(s)",
                "VETERINARIO_CON_CITAS"
            );
        }

        // Verificar si tiene horarios asociados
        if (veterinario.getHorarios() != null && !veterinario.getHorarios().isEmpty()) {
            throw new BusinessException(
                "No se puede eliminar el veterinario porque tiene " +
                veterinario.getHorarios().size() + " horario(s) asociado(s)",
                "VETERINARIO_CON_HORARIOS"
            );
        }

        veterinario.setActivo(false);
        veterinarioRepository.save(veterinario);
    }

    @Override
    public VeterinarioResponseDTO activar(Long id) {
        Veterinario veterinario = veterinarioRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Veterinario", "id", id));

        if (Boolean.TRUE.equals(veterinario.getActivo())) {
            throw new BusinessException("El veterinario ya está activo", "VETERINARIO_YA_ACTIVO");
        }

        veterinario.setActivo(true);
        Veterinario veterinarioActivado = veterinarioRepository.save(veterinario);
        return veterinarioMapper.toResponseDTO(veterinarioActivado);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existePorRegistroProfesional(String registroProfesional) {
        if (registroProfesional == null || registroProfesional.trim().isEmpty()) {
            return false;
        }
        return veterinarioRepository.existsByRegistroProfesional(registroProfesional.trim());
    }
}
