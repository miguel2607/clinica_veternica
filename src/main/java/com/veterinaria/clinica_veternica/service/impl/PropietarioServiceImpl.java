package com.veterinaria.clinica_veternica.service.impl;

import com.veterinaria.clinica_veternica.domain.paciente.Propietario;
import com.veterinaria.clinica_veternica.dto.request.paciente.PropietarioRequestDTO;
import com.veterinaria.clinica_veternica.dto.response.paciente.PropietarioResponseDTO;
import com.veterinaria.clinica_veternica.exception.BusinessException;
import com.veterinaria.clinica_veternica.exception.ResourceNotFoundException;
import com.veterinaria.clinica_veternica.exception.ValidationException;
import com.veterinaria.clinica_veternica.mapper.paciente.PropietarioMapper;
import com.veterinaria.clinica_veternica.repository.PropietarioRepository;
import com.veterinaria.clinica_veternica.service.interfaces.IPropietarioService;
import com.veterinaria.clinica_veternica.util.Constants;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class PropietarioServiceImpl implements IPropietarioService {

    private final PropietarioRepository propietarioRepository;
    private final PropietarioMapper propietarioMapper;

    @Override
    public PropietarioResponseDTO crear(PropietarioRequestDTO requestDTO) {
        // Validar que no exista el documento
        if (propietarioRepository.existsByTipoDocumentoAndNumeroDocumento(
                requestDTO.getTipoDocumento(), requestDTO.getDocumento())) {
            throw new ValidationException(
                "Ya existe un propietario con el documento " + requestDTO.getTipoDocumento() +
                " " + requestDTO.getDocumento(),
                "documento",
                "El documento ya está registrado"
            );
        }

        // Validar email único
        if (requestDTO.getEmail() != null && propietarioRepository.existsByEmail(requestDTO.getEmail())) {
            throw new ValidationException(
                "Ya existe un propietario con el email " + requestDTO.getEmail(),
                "email",
                "El email ya está registrado"
            );
        }

        Propietario propietario = propietarioMapper.toEntity(requestDTO);

        if (propietario.getActivo() == null) {
            propietario.setActivo(true);
        }

        Propietario propietarioGuardado = propietarioRepository.save(propietario);
        return propietarioMapper.toResponseDTO(propietarioGuardado);
    }

    @Override
    public PropietarioResponseDTO actualizar(Long id, PropietarioRequestDTO requestDTO) {
        Propietario propietario = propietarioRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(Constants.ENTIDAD_PROPIETARIO, "id", id));

        // Validar documento único (si cambió)
        if (!propietario.getDocumento().equals(requestDTO.getDocumento()) &&
            propietarioRepository.existsByTipoDocumentoAndNumeroDocumento(
                requestDTO.getTipoDocumento(), requestDTO.getDocumento())) {
            throw new ValidationException(
                "Ya existe otro propietario con el documento " + requestDTO.getTipoDocumento() +
                " " + requestDTO.getDocumento(),
                "documento",
                "El documento ya está registrado"
            );
        }

        // Validar email único (si cambió)
        if (requestDTO.getEmail() != null &&
            !requestDTO.getEmail().equals(propietario.getEmail()) &&
            propietarioRepository.existsByEmail(requestDTO.getEmail())) {
            throw new ValidationException(
                "Ya existe otro propietario con el email " + requestDTO.getEmail(),
                "email",
                "El email ya está registrado"
            );
        }

        propietarioMapper.updateEntityFromDTO(requestDTO, propietario);
        Propietario propietarioActualizado = propietarioRepository.save(propietario);
        return propietarioMapper.toResponseDTO(propietarioActualizado);
    }

    @Override
    @Transactional(readOnly = true)
    public PropietarioResponseDTO buscarPorId(Long id) {
        Propietario propietario = propietarioRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(Constants.ENTIDAD_PROPIETARIO, "id", id));
        return propietarioMapper.toResponseDTO(propietario);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PropietarioResponseDTO> listarTodos() {
        List<Propietario> propietarios = propietarioRepository.findAll();
        return propietarioMapper.toResponseDTOList(propietarios);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PropietarioResponseDTO> listarActivos() {
        List<Propietario> propietarios = propietarioRepository.findByActivoTrue();
        return propietarioMapper.toResponseDTOList(propietarios);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PropietarioResponseDTO> buscarPorNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new ValidationException("El nombre de búsqueda no puede estar vacío");
        }
        List<Propietario> propietarios = propietarioRepository.buscarPorNombreCompleto(nombre.trim());
        return propietarioMapper.toResponseDTOList(propietarios);
    }

    @Override
    @Transactional(readOnly = true)
    public PropietarioResponseDTO buscarPorDocumento(String tipoDocumento, String numeroDocumento) {
        Propietario propietario = propietarioRepository.findByTipoDocumentoAndNumeroDocumento(tipoDocumento, numeroDocumento)
            .orElseThrow(() -> new ResourceNotFoundException(
                "Propietario no encontrado con documento " + tipoDocumento + " " + numeroDocumento));
        return propietarioMapper.toResponseDTO(propietario);
    }

    @Override
    @Transactional(readOnly = true)
    public PropietarioResponseDTO buscarPorEmail(String email) {
        Propietario propietario = propietarioRepository.findByEmail(email)
            .orElseThrow(() -> new ResourceNotFoundException(Constants.ENTIDAD_PROPIETARIO, "email", email));
        return propietarioMapper.toResponseDTO(propietario);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PropietarioResponseDTO> buscarPorTelefono(String telefono) {
        List<Propietario> propietarios = propietarioRepository.findByTelefonoContaining(telefono);
        return propietarioMapper.toResponseDTOList(propietarios);
    }

    @Override
    public void eliminar(Long id) {
        Propietario propietario = propietarioRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(Constants.ENTIDAD_PROPIETARIO, "id", id));

        if (propietario.getMascotas() != null && !propietario.getMascotas().isEmpty()) {
            throw new BusinessException(
                "No se puede eliminar el propietario porque tiene " +
                propietario.getMascotas().size() + " mascota(s) asociada(s)",
                "PROPIETARIO_CON_MASCOTAS"
            );
        }

        propietario.setActivo(false);
        propietarioRepository.save(propietario);
    }

    @Override
    public PropietarioResponseDTO activar(Long id) {
        Propietario propietario = propietarioRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(Constants.ENTIDAD_PROPIETARIO, "id", id));

        if (Constants.isTrue(propietario.getActivo())) {
            throw new BusinessException("El propietario ya está activo", "PROPIETARIO_YA_ACTIVO");
        }

        propietario.setActivo(true);
        Propietario propietarioActivado = propietarioRepository.save(propietario);
        return propietarioMapper.toResponseDTO(propietarioActivado);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existePorDocumento(String tipoDocumento, String numeroDocumento) {
        if (tipoDocumento == null || numeroDocumento == null) {
            return false;
        }
        return propietarioRepository.existsByTipoDocumentoAndNumeroDocumento(tipoDocumento, numeroDocumento);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existePorEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        return propietarioRepository.existsByEmail(email.trim());
    }
}
