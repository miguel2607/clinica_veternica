package com.veterinaria.clinica_veternica.service.impl;

import com.veterinaria.clinica_veternica.domain.usuario.RolUsuario;
import com.veterinaria.clinica_veternica.domain.usuario.Usuario;
import com.veterinaria.clinica_veternica.dto.request.usuario.UsuarioRequestDTO;
import com.veterinaria.clinica_veternica.dto.response.usuario.UsuarioResponseDTO;
import com.veterinaria.clinica_veternica.exception.BusinessException;
import com.veterinaria.clinica_veternica.exception.ResourceNotFoundException;
import com.veterinaria.clinica_veternica.exception.UnauthorizedException;
import com.veterinaria.clinica_veternica.exception.ValidationException;
import com.veterinaria.clinica_veternica.mapper.usuario.UsuarioMapper;
import com.veterinaria.clinica_veternica.repository.UsuarioRepository;
import com.veterinaria.clinica_veternica.service.interfaces.IUsuarioService;
import com.veterinaria.clinica_veternica.util.Constants;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class UsuarioServiceImpl implements IUsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioMapper usuarioMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UsuarioResponseDTO crear(UsuarioRequestDTO requestDTO) {
        // Validar username único
        if (usuarioRepository.existsByUsername(requestDTO.getUsername())) {
            throw new ValidationException(
                "Ya existe un usuario con el username: " + requestDTO.getUsername(),
                "username",
                "El nombre de usuario ya está registrado"
            );
        }

        // Validar email único
        if (usuarioRepository.existsByEmail(requestDTO.getEmail())) {
            throw new ValidationException(
                "Ya existe un usuario con el email: " + requestDTO.getEmail(),
                "email",
                "El email ya está registrado"
            );
        }

        Usuario usuario = usuarioMapper.toEntity(requestDTO);

        // Encriptar password
        usuario.setPassword(passwordEncoder.encode(requestDTO.getPassword()));

        // Establecer valores por defecto
        if (usuario.getEstado() == null) {
            usuario.setEstado(true);
        }
        usuario.setBloqueado(false);
        usuario.setIntentosFallidos(0);

        Usuario usuarioGuardado = usuarioRepository.save(usuario);
        return usuarioMapper.toResponseDTO(usuarioGuardado);
    }

    @Override
    public UsuarioResponseDTO actualizar(Long id, UsuarioRequestDTO requestDTO) {
        Usuario usuario = usuarioRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(Constants.ENTIDAD_USUARIO, "id", id));

        // Validar username único (si cambió)
        if (!usuario.getUsername().equals(requestDTO.getUsername()) &&
            usuarioRepository.existsByUsername(requestDTO.getUsername())) {
            throw new ValidationException(
                "Ya existe otro usuario con el username: " + requestDTO.getUsername(),
                "username",
                "El nombre de usuario ya está registrado"
            );
        }

        // Validar email único (si cambió)
        if (!usuario.getEmail().equals(requestDTO.getEmail()) &&
            usuarioRepository.existsByEmail(requestDTO.getEmail())) {
            throw new ValidationException(
                "Ya existe otro usuario con el email: " + requestDTO.getEmail(),
                "email",
                "El email ya está registrado"
            );
        }

        // Guardar password anterior si no se proporciona nueva
        String passwordAnterior = usuario.getPassword();

        usuarioMapper.updateEntityFromDTO(requestDTO, usuario);

        // Si se proporcionó nueva password, encriptarla; sino mantener la anterior
        if (requestDTO.getPassword() != null && !requestDTO.getPassword().isEmpty()) {
            usuario.setPassword(passwordEncoder.encode(requestDTO.getPassword()));
        } else {
            usuario.setPassword(passwordAnterior);
        }

        Usuario usuarioActualizado = usuarioRepository.save(usuario);
        return usuarioMapper.toResponseDTO(usuarioActualizado);
    }

    @Override
    @Transactional(readOnly = true)
    public UsuarioResponseDTO buscarPorId(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(Constants.ENTIDAD_USUARIO, "id", id));
        return usuarioMapper.toResponseDTO(usuario);
    }

    @Override
    @Transactional(readOnly = true)
    public UsuarioResponseDTO buscarPorUsername(String username) {
        Usuario usuario = usuarioRepository.findByUsername(username)
            .orElseThrow(() -> new ResourceNotFoundException(Constants.ENTIDAD_USUARIO, "username", username));
        return usuarioMapper.toResponseDTO(usuario);
    }

    @Override
    @Transactional(readOnly = true)
    public UsuarioResponseDTO buscarPorEmail(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email)
            .orElseThrow(() -> new ResourceNotFoundException(Constants.ENTIDAD_USUARIO, "email", email));
        return usuarioMapper.toResponseDTO(usuario);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UsuarioResponseDTO> listarTodos() {
        List<Usuario> usuarios = usuarioRepository.findAll();
        return usuarioMapper.toResponseDTOList(usuarios);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UsuarioResponseDTO> listarTodos(Pageable pageable) {
        Page<Usuario> usuariosPage = usuarioRepository.findAll(pageable);
        return usuariosPage.map(usuarioMapper::toResponseDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UsuarioResponseDTO> listarPorRol(String rol) {
        try {
            RolUsuario rolUsuario = RolUsuario.valueOf(rol.toUpperCase());
            List<Usuario> usuarios = usuarioRepository.findByRol(rolUsuario);
            return usuarioMapper.toResponseDTOList(usuarios);
        } catch (IllegalArgumentException e) {
            throw new ValidationException("Rol inválido: " + rol);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<UsuarioResponseDTO> listarPorEstado(Boolean estado) {
        List<Usuario> usuarios = usuarioRepository.findByEstado(estado);
        return usuarioMapper.toResponseDTOList(usuarios);
    }

    @Override
    public void cambiarPassword(Long id, String passwordActual, String passwordNueva) {
        Usuario usuario = usuarioRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(Constants.ENTIDAD_USUARIO, "id", id));

        // Verificar password actual
        if (!passwordEncoder.matches(passwordActual, usuario.getPassword())) {
            throw new UnauthorizedException("La contraseña actual es incorrecta");
        }

        // Validar que la nueva password sea diferente
        if (passwordEncoder.matches(passwordNueva, usuario.getPassword())) {
            throw new ValidationException("La nueva contraseña debe ser diferente a la actual");
        }

        // Establecer nueva password encriptada
        usuario.setPassword(passwordEncoder.encode(passwordNueva));
        usuarioRepository.save(usuario);
    }

    @Override
    public void resetearPassword(Long id, String nuevaPassword) {
        Usuario usuario = usuarioRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(Constants.ENTIDAD_USUARIO, "id", id));

        usuario.setPassword(passwordEncoder.encode(nuevaPassword));
        usuario.setIntentosFallidos(0);
        usuario.setBloqueado(false);
        usuarioRepository.save(usuario);
    }

    @Override
    public void bloquearUsuario(Long id, String motivo) {
        Usuario usuario = usuarioRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(Constants.ENTIDAD_USUARIO, "id", id));

        if (Constants.isTrue(usuario.getBloqueado())) {
            throw new BusinessException("El usuario ya está bloqueado", "USUARIO_YA_BLOQUEADO");
        }

        usuario.setBloqueado(true);
        usuario.setFechaBloqueo(LocalDateTime.now());
        usuarioRepository.save(usuario);
    }

    @Override
    public void desbloquearUsuario(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(Constants.ENTIDAD_USUARIO, "id", id));

        if (Constants.isFalse(usuario.getBloqueado())) {
            throw new BusinessException("El usuario no está bloqueado", "USUARIO_NO_BLOQUEADO");
        }

        usuario.setBloqueado(false);
        usuario.setIntentosFallidos(0);
        usuario.setFechaBloqueo(null);
        usuarioRepository.save(usuario);
    }

    @Override
    public void activarUsuario(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(Constants.ENTIDAD_USUARIO, "id", id));

        if (Constants.isTrue(usuario.getEstado())) {
            throw new BusinessException("El usuario ya está activo", "USUARIO_YA_ACTIVO");
        }

        usuario.setEstado(true);
        usuarioRepository.save(usuario);
    }

    @Override
    public void desactivarUsuario(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(Constants.ENTIDAD_USUARIO, "id", id));

        if (Constants.isFalse(usuario.getEstado())) {
            throw new BusinessException("El usuario ya está inactivo", "USUARIO_YA_INACTIVO");
        }

        usuario.setEstado(false);
        usuarioRepository.save(usuario);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existePorUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            return false;
        }
        return usuarioRepository.existsByUsername(username.trim());
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existePorEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        return usuarioRepository.existsByEmail(email.trim());
    }

    @Override
    @Transactional(readOnly = true)
    public boolean verificarPassword(Long id, String password) {
        Usuario usuario = usuarioRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(Constants.ENTIDAD_USUARIO, "id", id));
        return passwordEncoder.matches(password, usuario.getPassword());
    }
}
