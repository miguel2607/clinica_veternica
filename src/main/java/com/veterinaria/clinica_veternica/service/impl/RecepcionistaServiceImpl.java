package com.veterinaria.clinica_veternica.service.impl;

import com.veterinaria.clinica_veternica.domain.usuario.Recepcionista;
import com.veterinaria.clinica_veternica.domain.usuario.RolUsuario;
import com.veterinaria.clinica_veternica.domain.usuario.Usuario;
import com.veterinaria.clinica_veternica.dto.request.usuario.RecepcionistaRequestDTO;
import com.veterinaria.clinica_veternica.dto.response.usuario.RecepcionistaResponseDTO;
import com.veterinaria.clinica_veternica.exception.BusinessException;
import com.veterinaria.clinica_veternica.exception.ResourceNotFoundException;
import com.veterinaria.clinica_veternica.exception.ValidationException;
import com.veterinaria.clinica_veternica.mapper.usuario.RecepcionistaMapper;
import com.veterinaria.clinica_veternica.mapper.usuario.UsuarioMapper;
import com.veterinaria.clinica_veternica.repository.PersonalRepository;
import com.veterinaria.clinica_veternica.repository.RecepcionistaRepository;
import com.veterinaria.clinica_veternica.repository.UsuarioRepository;
import com.veterinaria.clinica_veternica.service.interfaces.IRecepcionistaService;
import com.veterinaria.clinica_veternica.util.Constants;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class RecepcionistaServiceImpl implements IRecepcionistaService {

    private static final String MSG_YA_EXISTE_DOCUMENTO = "Ya existe un recepcionista con el documento: ";
    private static final String MSG_YA_EXISTE_USERNAME = "Ya existe un usuario con el username: ";
    private static final String MSG_YA_EXISTE_EMAIL = "Ya existe un usuario con el email: ";
    private static final String MSG_YA_ACTIVO = "El recepcionista ya está activo";
    private static final String ERROR_CODE_YA_ACTIVO = "RECEPCIONISTA_YA_ACTIVO";

    private final RecepcionistaRepository recepcionistaRepository;
    private final PersonalRepository personalRepository;
    private final UsuarioRepository usuarioRepository;
    private final RecepcionistaMapper recepcionistaMapper;
    private final UsuarioMapper usuarioMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public RecepcionistaResponseDTO crear(RecepcionistaRequestDTO requestDTO) {
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

        Recepcionista recepcionista = recepcionistaMapper.toEntity(requestDTO);

        if (recepcionista.getActivo() == null) {
            recepcionista.setActivo(true);
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
            usuario.setRol(RolUsuario.RECEPCIONISTA);
            if (usuario.getEstado() == null) {
                usuario.setEstado(true);
            }
            usuario.setBloqueado(false);
            usuario.setIntentosFallidos(0);

            Usuario usuarioGuardado = usuarioRepository.save(usuario);
            recepcionista.setUsuario(usuarioGuardado);
        }

        Recepcionista recepcionistaGuardado = recepcionistaRepository.save(recepcionista);
        return recepcionistaMapper.toResponseDTO(recepcionistaGuardado);
    }

    @Override
    public RecepcionistaResponseDTO actualizar(Long id, RecepcionistaRequestDTO requestDTO) {
        Recepcionista recepcionista = recepcionistaRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(Constants.ENTIDAD_RECEPCIONISTA, "id", id));

        // Validar documento único (si cambió)
        if (!recepcionista.getDocumento().equals(requestDTO.getDocumento()) &&
            personalRepository.existsByDocumento(requestDTO.getDocumento())) {
            throw new ValidationException(
                "Ya existe otro personal con el documento: " + requestDTO.getDocumento(),
                "documento",
                "El documento ya está registrado"
            );
        }

        recepcionistaMapper.updateEntityFromDTO(requestDTO, recepcionista);

        Recepcionista recepcionistaActualizado = recepcionistaRepository.save(recepcionista);
        return recepcionistaMapper.toResponseDTO(recepcionistaActualizado);
    }

    @Override
    @Transactional(readOnly = true)
    public RecepcionistaResponseDTO buscarPorId(Long id) {
        Recepcionista recepcionista = recepcionistaRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(Constants.ENTIDAD_RECEPCIONISTA, "id", id));
        return recepcionistaMapper.toResponseDTO(recepcionista);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RecepcionistaResponseDTO> listarTodos() {
        List<Recepcionista> recepcionistas = recepcionistaRepository.findAll();
        return recepcionistaMapper.toResponseDTOList(recepcionistas);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RecepcionistaResponseDTO> listarActivos() {
        List<Recepcionista> recepcionistas = recepcionistaRepository.findRecepcionistasActivos();
        return recepcionistaMapper.toResponseDTOList(recepcionistas);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RecepcionistaResponseDTO> buscarPorNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new ValidationException("El nombre de búsqueda no puede estar vacío");
        }
        List<Recepcionista> recepcionistas = recepcionistaRepository.findAll().stream()
            .filter(r -> (r.getNombres() + " " + r.getApellidos()).toLowerCase()
                .contains(nombre.trim().toLowerCase()))
            .toList();
        return recepcionistaMapper.toResponseDTOList(recepcionistas);
    }

    @Override
    public void eliminar(Long id) {
        Recepcionista recepcionista = recepcionistaRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(Constants.ENTIDAD_RECEPCIONISTA, "id", id));

        recepcionista.setActivo(false);
        recepcionistaRepository.save(recepcionista);
    }

    @Override
    public RecepcionistaResponseDTO activar(Long id) {
        Recepcionista recepcionista = recepcionistaRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(Constants.ENTIDAD_RECEPCIONISTA, "id", id));

        if (Constants.isTrue(recepcionista.getActivo())) {
            throw new BusinessException(MSG_YA_ACTIVO, ERROR_CODE_YA_ACTIVO);
        }

        recepcionista.setActivo(true);
        Recepcionista recepcionistaActivado = recepcionistaRepository.save(recepcionista);
        return recepcionistaMapper.toResponseDTO(recepcionistaActivado);
    }
}

