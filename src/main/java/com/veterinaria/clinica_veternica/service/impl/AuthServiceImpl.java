package com.veterinaria.clinica_veternica.service.impl;

import com.veterinaria.clinica_veternica.domain.paciente.Propietario;
import com.veterinaria.clinica_veternica.domain.usuario.RolUsuario;
import com.veterinaria.clinica_veternica.domain.usuario.Usuario;
import com.veterinaria.clinica_veternica.dto.request.auth.LoginRequestDTO;
import com.veterinaria.clinica_veternica.dto.request.auth.RegisterPropietarioRequestDTO;
import com.veterinaria.clinica_veternica.dto.request.auth.RegisterRequestDTO;
import com.veterinaria.clinica_veternica.dto.request.auth.ResetPasswordByUsernameRequestDTO;
import com.veterinaria.clinica_veternica.dto.request.paciente.PropietarioRequestDTO;
import com.veterinaria.clinica_veternica.dto.response.auth.LoginResponseDTO;
import com.veterinaria.clinica_veternica.dto.response.paciente.PropietarioResponseDTO;
import com.veterinaria.clinica_veternica.dto.response.usuario.UsuarioResponseDTO;
import com.veterinaria.clinica_veternica.exception.ResourceNotFoundException;
import com.veterinaria.clinica_veternica.exception.UnauthorizedException;
import com.veterinaria.clinica_veternica.exception.ValidationException;
import com.veterinaria.clinica_veternica.mapper.paciente.PropietarioMapper;
import com.veterinaria.clinica_veternica.mapper.usuario.UsuarioMapper;
import com.veterinaria.clinica_veternica.repository.PropietarioRepository;
import com.veterinaria.clinica_veternica.repository.UsuarioRepository;
import com.veterinaria.clinica_veternica.security.jwt.JwtProperties;
import com.veterinaria.clinica_veternica.security.jwt.JwtUtils;
import com.veterinaria.clinica_veternica.service.interfaces.IAuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementación del servicio de autenticación.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements IAuthService {

    private final AuthenticationManager authenticationManager;
    private final UsuarioRepository usuarioRepository;
    private final PropietarioRepository propietarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final JwtProperties jwtProperties;
    private final UsuarioMapper usuarioMapper;
    private final PropietarioMapper propietarioMapper;

    /**
     * Autentica un usuario y genera un token JWT.
     *
     * @param loginRequest Datos de inicio de sesión
     * @return LoginResponseDTO con el token y datos del usuario
     */
    @Override
    @Transactional(readOnly = true)
    public LoginResponseDTO login(LoginRequestDTO loginRequest) {
        try {
            // Autenticar al usuario
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    loginRequest.getUsername(),
                    loginRequest.getPassword()
                )
            );

            // Establecer la autenticación en el contexto de seguridad
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Generar el token JWT
            String jwt = jwtUtils.generateJwtToken(authentication);

            // Obtener los datos del usuario desde la base de datos
            Usuario usuario = usuarioRepository.findByUsername(loginRequest.getUsername())
                .orElseThrow(() -> new UnauthorizedException("Usuario no encontrado"));

            // Verificar si el usuario está activo y no bloqueado
            if (usuario.getEstado() == null || !usuario.getEstado()) {
                throw new UnauthorizedException("Usuario inactivo");
            }

            if (usuario.getBloqueado() != null && usuario.getBloqueado()) {
                throw new UnauthorizedException("Usuario bloqueado: " + usuario.getMotivoBloqueo());
            }

            // Resetear intentos fallidos en caso de login exitoso
            if (usuario.getIntentosFallidos() > 0) {
                usuario.setIntentosFallidos(0);
                usuarioRepository.save(usuario);
            }

            log.info("Usuario autenticado exitosamente: {}", usuario.getUsername());

            // Construir la respuesta
            return LoginResponseDTO.builder()
                .token(jwt)
                .type("Bearer")
                .idUsuario(usuario.getIdUsuario())
                .username(usuario.getUsername())
                .email(usuario.getEmail())
                .rol(usuario.getRol().name())
                .expiresIn(jwtProperties.getExpiration())
                .build();

        } catch (AuthenticationException e) {
            // Manejar intentos fallidos de login
            usuarioRepository.findByUsername(loginRequest.getUsername()).ifPresent(usuario -> {
                int intentos = usuario.getIntentosFallidos() + 1;
                usuario.setIntentosFallidos(intentos);

                // Bloquear usuario después de 5 intentos fallidos
                if (intentos >= 5) {
                    usuario.setBloqueado(true);
                    usuario.setMotivoBloqueo("Bloqueado automáticamente por múltiples intentos fallidos de inicio de sesión");
                    log.warn("Usuario bloqueado por intentos fallidos: {}", usuario.getUsername());
                }

                usuarioRepository.save(usuario);
            });

            log.error("Error de autenticación para usuario: {}", loginRequest.getUsername());
            throw new UnauthorizedException("Credenciales inválidas");
        }
    }

    /**
     * Registra un nuevo usuario en el sistema.
     *
     * @param registerRequest Datos del nuevo usuario
     * @return UsuarioResponseDTO con los datos del usuario creado
     */
    @Override
    @Transactional
    public UsuarioResponseDTO register(RegisterRequestDTO registerRequest) {
        // Validar que el username no exista
        if (usuarioRepository.existsByUsername(registerRequest.getUsername())) {
            throw new ValidationException(
                "El username ya está registrado",
                "username",
                "Este nombre de usuario ya está en uso"
            );
        }

        // Validar que el email no exista
        if (usuarioRepository.existsByEmail(registerRequest.getEmail())) {
            throw new ValidationException(
                "El email ya está registrado",
                "email",
                "Este correo electrónico ya está en uso"
            );
        }

        // Determinar el rol (por defecto RECEPCIONISTA)
        RolUsuario rol = RolUsuario.RECEPCIONISTA;
        if (registerRequest.getRol() != null) {
            try {
                rol = RolUsuario.valueOf(registerRequest.getRol().toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new ValidationException(
                    "Rol inválido: " + registerRequest.getRol(),
                    "rol",
                    "El rol debe ser uno de: ADMIN, VETERINARIO, AUXILIAR, RECEPCIONISTA, PROPIETARIO, ESTUDIANTE"
                );
            }
        }

        // Crear el nuevo usuario
        Usuario usuario = Usuario.builder()
            .username(registerRequest.getUsername())
            .email(registerRequest.getEmail())
            .password(passwordEncoder.encode(registerRequest.getPassword()))
            .rol(rol)
            .estado(true)
            .bloqueado(false)
            .intentosFallidos(0)
            .build();

        // Guardar el usuario
        Usuario usuarioGuardado = usuarioRepository.save(usuario);

        log.info("Nuevo usuario registrado: {}", usuarioGuardado.getUsername());

        // Retornar el DTO de respuesta
        return usuarioMapper.toResponseDTO(usuarioGuardado);
    }

    /**
     * Resetea la contraseña de un usuario usando su nombre de usuario (público).
     *
     * @param requestDTO Datos con username y nueva contraseña
     */
    @Override
    @Transactional
    public void resetPasswordByUsername(ResetPasswordByUsernameRequestDTO requestDTO) {
        Usuario usuario = usuarioRepository.findByUsername(requestDTO.getUsername())
            .orElseThrow(() -> new ResourceNotFoundException("Usuario", "username", requestDTO.getUsername()));

        // Validar que la nueva password sea diferente a la actual
        if (passwordEncoder.matches(requestDTO.getNuevaPassword(), usuario.getPassword())) {
            throw new ValidationException("La nueva contraseña debe ser diferente a la actual");
        }

        // Establecer nueva password encriptada
        usuario.setPassword(passwordEncoder.encode(requestDTO.getNuevaPassword()));
        usuario.setIntentosFallidos(0);
        usuario.setBloqueado(false);
        usuarioRepository.save(usuario);

        log.info("Contraseña reseteada para usuario: {}", usuario.getUsername());
    }

    /**
     * Registra un nuevo propietario (crea usuario + propietario).
     *
     * @param requestDTO Datos del propietario y usuario
     * @return PropietarioResponseDTO con los datos del propietario creado
     */
    @Override
    @Transactional
    public PropietarioResponseDTO registerPropietario(RegisterPropietarioRequestDTO requestDTO) {
        // Validar que el username no exista
        if (usuarioRepository.existsByUsername(requestDTO.getUsername())) {
            throw new ValidationException(
                "El username ya está registrado",
                "username",
                "Este nombre de usuario ya está en uso"
            );
        }

        // Validar que el email no exista
        if (usuarioRepository.existsByEmail(requestDTO.getEmail())) {
            throw new ValidationException(
                "El email ya está registrado",
                "email",
                "Este correo electrónico ya está en uso"
            );
        }

        // Validar que no exista un propietario con el mismo documento
        if (propietarioRepository.existsByTipoDocumentoAndNumeroDocumento(
                requestDTO.getTipoDocumento(), requestDTO.getDocumento())) {
            throw new ValidationException(
                "Ya existe un propietario con este documento",
                "documento",
                "El documento " + requestDTO.getTipoDocumento() + " " + requestDTO.getDocumento() + " ya está registrado"
            );
        }

        // Validar que no exista un propietario con el mismo email
        if (propietarioRepository.existsByEmail(requestDTO.getEmail())) {
            throw new ValidationException(
                "Ya existe un propietario con este email",
                "email",
                "Este correo electrónico ya está registrado como propietario"
            );
        }

        // Crear el usuario con rol PROPIETARIO
        Usuario usuario = Usuario.builder()
            .username(requestDTO.getUsername())
            .email(requestDTO.getEmail())
            .password(passwordEncoder.encode(requestDTO.getPassword()))
            .rol(RolUsuario.PROPIETARIO)
            .estado(true)
            .bloqueado(false)
            .intentosFallidos(0)
            .build();

        Usuario usuarioGuardado = usuarioRepository.save(usuario);
        log.info("Usuario creado para propietario: {}", usuarioGuardado.getUsername());

        // Crear el propietario
        PropietarioRequestDTO propietarioRequestDTO = PropietarioRequestDTO.builder()
            .documento(requestDTO.getDocumento())
            .tipoDocumento(requestDTO.getTipoDocumento())
            .nombres(requestDTO.getNombres())
            .apellidos(requestDTO.getApellidos())
            .telefono(requestDTO.getTelefono())
            .email(requestDTO.getEmail())
            .direccion(requestDTO.getDireccion())
            .activo(true)
            .build();

        Propietario propietario = propietarioMapper.toEntity(propietarioRequestDTO);
        Propietario propietarioGuardado = propietarioRepository.save(propietario);

        log.info("Propietario registrado: {} {}", propietarioGuardado.getNombres(), propietarioGuardado.getApellidos());

        return propietarioMapper.toResponseDTO(propietarioGuardado);
    }
}
