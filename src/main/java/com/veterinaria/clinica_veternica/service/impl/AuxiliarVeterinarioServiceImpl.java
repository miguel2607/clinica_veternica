package com.veterinaria.clinica_veternica.service.impl;

import com.veterinaria.clinica_veternica.domain.usuario.AuxiliarVeterinario;
import com.veterinaria.clinica_veternica.domain.usuario.RolUsuario;
import com.veterinaria.clinica_veternica.domain.usuario.Usuario;
import com.veterinaria.clinica_veternica.dto.request.usuario.AuxiliarVeterinarioRequestDTO;
import com.veterinaria.clinica_veternica.dto.response.usuario.AuxiliarVeterinarioResponseDTO;
import com.veterinaria.clinica_veternica.exception.BusinessException;
import com.veterinaria.clinica_veternica.exception.ResourceNotFoundException;
import com.veterinaria.clinica_veternica.exception.ValidationException;
import com.veterinaria.clinica_veternica.mapper.usuario.AuxiliarVeterinarioMapper;
import com.veterinaria.clinica_veternica.mapper.usuario.UsuarioMapper;
import com.veterinaria.clinica_veternica.repository.AuxiliarVeterinarioRepository;
import com.veterinaria.clinica_veternica.repository.PersonalRepository;
import com.veterinaria.clinica_veternica.repository.UsuarioRepository;
import com.veterinaria.clinica_veternica.service.interfaces.IAuxiliarVeterinarioService;
import com.veterinaria.clinica_veternica.util.Constants;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class AuxiliarVeterinarioServiceImpl implements IAuxiliarVeterinarioService {

    private static final String MSG_YA_EXISTE_DOCUMENTO = "Ya existe un auxiliar veterinario con el documento: ";
    private static final String MSG_YA_EXISTE_USERNAME = "Ya existe un usuario con el username: ";
    private static final String MSG_YA_EXISTE_EMAIL = "Ya existe un usuario con el email: ";
    private static final String MSG_YA_ACTIVO = "El auxiliar veterinario ya está activo";
    private static final String ERROR_CODE_YA_ACTIVO = "AUXILIAR_YA_ACTIVO";

    private final AuxiliarVeterinarioRepository auxiliarVeterinarioRepository;
    private final PersonalRepository personalRepository;
    private final UsuarioRepository usuarioRepository;
    private final AuxiliarVeterinarioMapper auxiliarVeterinarioMapper;
    private final UsuarioMapper usuarioMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public AuxiliarVeterinarioResponseDTO crear(AuxiliarVeterinarioRequestDTO requestDTO) {
        // Validar documento único
        if (personalRepository.existsByDocumento(requestDTO.getDocumento())) {
            throw new ValidationException(
                MSG_YA_EXISTE_DOCUMENTO + requestDTO.getDocumento(),
                "documento",
                "El documento ya está registrado"
            );
        }

        // Validar correo único
        if (personalRepository.existsByCorreo(requestDTO.getCorreo())) {
            throw new ValidationException(
                "Ya existe un personal con el correo: " + requestDTO.getCorreo(),
                "correo",
                "El correo ya está registrado"
            );
        }

        AuxiliarVeterinario auxiliar = auxiliarVeterinarioMapper.toEntity(requestDTO);

        if (auxiliar.getActivo() == null) {
            auxiliar.setActivo(true);
        }

        // Crear Usuario si se proporciona en el DTO
        if (requestDTO.getUsuario() != null) {
            // Validar username único
            if (usuarioRepository.existsByUsername(requestDTO.getUsuario().getUsername())) {
                throw new ValidationException(
                    MSG_YA_EXISTE_USERNAME + requestDTO.getUsuario().getUsername(),
                    "usuario.username",
                    "El nombre de usuario ya está registrado"
                );
            }

            // Validar email único
            if (usuarioRepository.existsByEmail(requestDTO.getUsuario().getEmail())) {
                throw new ValidationException(
                    MSG_YA_EXISTE_EMAIL + requestDTO.getUsuario().getEmail(),
                    "usuario.email",
                    "El email ya está registrado"
                );
            }

            Usuario usuario = usuarioMapper.toEntity(requestDTO.getUsuario());
            usuario.setPassword(passwordEncoder.encode(requestDTO.getUsuario().getPassword()));
            usuario.setRol(RolUsuario.AUXILIAR);
            if (usuario.getEstado() == null) {
                usuario.setEstado(true);
            }
            usuario.setBloqueado(false);
            usuario.setIntentosFallidos(0);

            Usuario usuarioGuardado = usuarioRepository.save(usuario);
            auxiliar.setUsuario(usuarioGuardado);
        }

        AuxiliarVeterinario auxiliarGuardado = auxiliarVeterinarioRepository.save(auxiliar);
        return auxiliarVeterinarioMapper.toResponseDTO(auxiliarGuardado);
    }

    @Override
    public AuxiliarVeterinarioResponseDTO actualizar(Long id, AuxiliarVeterinarioRequestDTO requestDTO) {
        AuxiliarVeterinario auxiliar = auxiliarVeterinarioRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(Constants.ENTIDAD_AUXILIAR_VETERINARIO, "id", id));

        // Validar documento único (si cambió)
        if (!auxiliar.getDocumento().equals(requestDTO.getDocumento()) &&
            personalRepository.existsByDocumento(requestDTO.getDocumento())) {
            throw new ValidationException(
                "Ya existe otro personal con el documento: " + requestDTO.getDocumento(),
                "documento",
                "El documento ya está registrado"
            );
        }

        auxiliarVeterinarioMapper.updateEntityFromDTO(requestDTO, auxiliar);

        AuxiliarVeterinario auxiliarActualizado = auxiliarVeterinarioRepository.save(auxiliar);
        return auxiliarVeterinarioMapper.toResponseDTO(auxiliarActualizado);
    }

    @Override
    @Transactional(readOnly = true)
    public AuxiliarVeterinarioResponseDTO buscarPorId(Long id) {
        AuxiliarVeterinario auxiliar = auxiliarVeterinarioRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(Constants.ENTIDAD_AUXILIAR_VETERINARIO, "id", id));
        return auxiliarVeterinarioMapper.toResponseDTO(auxiliar);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AuxiliarVeterinarioResponseDTO> listarTodos() {
        List<AuxiliarVeterinario> auxiliares = auxiliarVeterinarioRepository.findAll();
        return auxiliarVeterinarioMapper.toResponseDTOList(auxiliares);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AuxiliarVeterinarioResponseDTO> listarActivos() {
        List<AuxiliarVeterinario> auxiliares = auxiliarVeterinarioRepository.findAuxiliaresActivos();
        return auxiliarVeterinarioMapper.toResponseDTOList(auxiliares);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AuxiliarVeterinarioResponseDTO> buscarPorNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new ValidationException("El nombre de búsqueda no puede estar vacío");
        }
        List<AuxiliarVeterinario> auxiliares = auxiliarVeterinarioRepository.findAll().stream()
            .filter(a -> (a.getNombres() + " " + a.getApellidos()).toLowerCase()
                .contains(nombre.trim().toLowerCase()))
            .toList();
        return auxiliarVeterinarioMapper.toResponseDTOList(auxiliares);
    }

    @Override
    public void eliminar(Long id) {
        AuxiliarVeterinario auxiliar = auxiliarVeterinarioRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(Constants.ENTIDAD_AUXILIAR_VETERINARIO, "id", id));

        auxiliar.setActivo(false);
        auxiliarVeterinarioRepository.save(auxiliar);
    }

    @Override
    public AuxiliarVeterinarioResponseDTO activar(Long id) {
        AuxiliarVeterinario auxiliar = auxiliarVeterinarioRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(Constants.ENTIDAD_AUXILIAR_VETERINARIO, "id", id));

        if (Constants.isTrue(auxiliar.getActivo())) {
            throw new BusinessException(MSG_YA_ACTIVO, ERROR_CODE_YA_ACTIVO);
        }

        auxiliar.setActivo(true);
        AuxiliarVeterinario auxiliarActivado = auxiliarVeterinarioRepository.save(auxiliar);
        return auxiliarVeterinarioMapper.toResponseDTO(auxiliarActivado);
    }
}

