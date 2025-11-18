package com.veterinaria.clinica_veternica.service.impl;

import com.veterinaria.clinica_veternica.domain.usuario.Administrador;
import com.veterinaria.clinica_veternica.domain.usuario.RolUsuario;
import com.veterinaria.clinica_veternica.domain.usuario.Usuario;
import com.veterinaria.clinica_veternica.dto.request.usuario.AdministradorRequestDTO;
import com.veterinaria.clinica_veternica.dto.response.usuario.AdministradorResponseDTO;
import com.veterinaria.clinica_veternica.exception.BusinessException;
import com.veterinaria.clinica_veternica.exception.ResourceNotFoundException;
import com.veterinaria.clinica_veternica.exception.ValidationException;
import com.veterinaria.clinica_veternica.mapper.usuario.AdministradorMapper;
import com.veterinaria.clinica_veternica.mapper.usuario.UsuarioMapper;
import com.veterinaria.clinica_veternica.repository.AdministradorRepository;
import com.veterinaria.clinica_veternica.repository.PersonalRepository;
import com.veterinaria.clinica_veternica.repository.UsuarioRepository;
import com.veterinaria.clinica_veternica.service.interfaces.IAdministradorService;
import com.veterinaria.clinica_veternica.util.Constants;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class AdministradorServiceImpl implements IAdministradorService {

    private static final String MSG_YA_EXISTE_DOCUMENTO = "Ya existe un administrador con el documento: ";
    private static final String MSG_YA_EXISTE_USERNAME = "Ya existe un usuario con el username: ";
    private static final String MSG_YA_EXISTE_EMAIL = "Ya existe un usuario con el email: ";
    private static final String MSG_YA_ACTIVO = "El administrador ya está activo";
    private static final String ERROR_CODE_YA_ACTIVO = "ADMINISTRADOR_YA_ACTIVO";

    private final AdministradorRepository administradorRepository;
    private final PersonalRepository personalRepository;
    private final UsuarioRepository usuarioRepository;
    private final AdministradorMapper administradorMapper;
    private final UsuarioMapper usuarioMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public AdministradorResponseDTO crear(AdministradorRequestDTO requestDTO) {
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

        Administrador administrador = administradorMapper.toEntity(requestDTO);

        if (administrador.getActivo() == null) {
            administrador.setActivo(true);
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
            usuario.setRol(RolUsuario.ADMIN);
            if (usuario.getEstado() == null) {
                usuario.setEstado(true);
            }
            usuario.setBloqueado(false);
            usuario.setIntentosFallidos(0);

            Usuario usuarioGuardado = usuarioRepository.save(usuario);
            administrador.setUsuario(usuarioGuardado);
        }

        Administrador administradorGuardado = administradorRepository.save(administrador);
        return administradorMapper.toResponseDTO(administradorGuardado);
    }

    @Override
    public AdministradorResponseDTO actualizar(Long id, AdministradorRequestDTO requestDTO) {
        Administrador administrador = administradorRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(Constants.ENTIDAD_ADMINISTRADOR, "id", id));

        // Validar documento único (si cambió)
        if (!administrador.getDocumento().equals(requestDTO.getDocumento()) &&
            personalRepository.existsByDocumento(requestDTO.getDocumento())) {
            throw new ValidationException(
                "Ya existe otro personal con el documento: " + requestDTO.getDocumento(),
                "documento",
                "El documento ya está registrado"
            );
        }

        administradorMapper.updateEntityFromDTO(requestDTO, administrador);

        Administrador administradorActualizado = administradorRepository.save(administrador);
        return administradorMapper.toResponseDTO(administradorActualizado);
    }

    @Override
    @Transactional(readOnly = true)
    public AdministradorResponseDTO buscarPorId(Long id) {
        Administrador administrador = administradorRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(Constants.ENTIDAD_ADMINISTRADOR, "id", id));
        return administradorMapper.toResponseDTO(administrador);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AdministradorResponseDTO> listarTodos() {
        List<Administrador> administradores = administradorRepository.findAll();
        return administradorMapper.toResponseDTOList(administradores);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AdministradorResponseDTO> listarActivos() {
        List<Administrador> administradores = administradorRepository.findAdministradoresActivos();
        return administradorMapper.toResponseDTOList(administradores);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AdministradorResponseDTO> buscarPorNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new ValidationException("El nombre de búsqueda no puede estar vacío");
        }
        List<Administrador> administradores = administradorRepository.findAll().stream()
            .filter(a -> (a.getNombres() + " " + a.getApellidos()).toLowerCase()
                .contains(nombre.trim().toLowerCase()))
            .toList();
        return administradorMapper.toResponseDTOList(administradores);
    }

    @Override
    public void eliminar(Long id) {
        Administrador administrador = administradorRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(Constants.ENTIDAD_ADMINISTRADOR, "id", id));

        administrador.setActivo(false);
        administradorRepository.save(administrador);
    }

    @Override
    public AdministradorResponseDTO activar(Long id) {
        Administrador administrador = administradorRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(Constants.ENTIDAD_ADMINISTRADOR, "id", id));

        if (Constants.isTrue(administrador.getActivo())) {
            throw new BusinessException(MSG_YA_ACTIVO, ERROR_CODE_YA_ACTIVO);
        }

        administrador.setActivo(true);
        Administrador administradorActivado = administradorRepository.save(administrador);
        return administradorMapper.toResponseDTO(administradorActivado);
    }
}

