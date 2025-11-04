package com.veterinaria.clinica_veternica.service.impl;

import com.veterinaria.clinica_veternica.domain.usuario.RolUsuario;
import com.veterinaria.clinica_veternica.domain.usuario.Usuario;
import com.veterinaria.clinica_veternica.dto.request.auth.LoginRequestDTO;
import com.veterinaria.clinica_veternica.dto.request.auth.RegisterRequestDTO;
import com.veterinaria.clinica_veternica.dto.response.auth.LoginResponseDTO;
import com.veterinaria.clinica_veternica.dto.response.usuario.UsuarioResponseDTO;
import com.veterinaria.clinica_veternica.exception.UnauthorizedException;
import com.veterinaria.clinica_veternica.exception.ValidationException;
import com.veterinaria.clinica_veternica.mapper.usuario.UsuarioMapper;
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
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final JwtProperties jwtProperties;
    private final UsuarioMapper usuarioMapper;

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
            if (!usuario.getEstado()) {
                throw new UnauthorizedException("Usuario inactivo");
            }

            if (usuario.getBloqueado()) {
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
}
