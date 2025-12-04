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

import java.util.List;
import java.util.Optional;

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
     * Busca el username real de un usuario basado en username, email o nombre del propietario.
     * 
     * @param identificador Puede ser username, email o nombre del propietario
     * @return El username real del usuario, o null si no se encuentra
     */
    private String obtenerUsernameReal(String identificador) {
        if (identificador == null || identificador.trim().isEmpty()) {
            return null;
        }
        
        String busqueda = identificador.trim();
        
        // 1. Intentar buscar por username
        Optional<Usuario> usuarioPorUsername = usuarioRepository.findByUsername(busqueda);
        if (usuarioPorUsername.isPresent()) {
            log.debug("Usuario encontrado por username: {}", busqueda);
            return usuarioPorUsername.get().getUsername();
        }
        
        // 2. Intentar buscar por email
        Optional<Usuario> usuarioPorEmail = usuarioRepository.findByEmail(busqueda);
        if (usuarioPorEmail.isPresent()) {
            log.debug("Usuario encontrado por email: {}", busqueda);
            return usuarioPorEmail.get().getUsername();
        }
        
        // 3. Intentar buscar por nombre del propietario
        List<Propietario> propietarios = propietarioRepository.buscarPropietarios(busqueda);
        if (!propietarios.isEmpty()) {
            // Buscar el propietario que coincida exactamente con el nombre completo
            Propietario propietarioEncontrado = null;
            String nombreCompletoBusqueda = busqueda.toLowerCase().trim();
            
            for (Propietario prop : propietarios) {
                String nombreCompleto = (prop.getNombres() + " " + prop.getApellidos()).toLowerCase().trim();
                if (nombreCompleto.equals(nombreCompletoBusqueda)) {
                    propietarioEncontrado = prop;
                    break;
                }
            }
            
            // Si no hay coincidencia exacta, usar el primero encontrado si solo hay uno
            if (propietarioEncontrado == null && propietarios.size() == 1) {
                propietarioEncontrado = propietarios.get(0);
            }
            
            if (propietarioEncontrado != null && propietarioEncontrado.getUsuario() != null) {
                log.debug("Usuario encontrado por nombre del propietario: {} -> {}", busqueda, propietarioEncontrado.getUsuario().getUsername());
                return propietarioEncontrado.getUsuario().getUsername();
            } else if (propietarioEncontrado != null) {
                log.warn("Propietario encontrado '{}' pero no tiene usuario asociado", busqueda);
            }
        }
        
        log.debug("No se encontró usuario con identificador: {}", busqueda);
        return null;
    }

    /**
     * Autentica un usuario y genera un token JWT.
     * Permite iniciar sesión con username, email o nombre del propietario.
     *
     * @param loginRequest Datos de inicio de sesión
     * @return LoginResponseDTO con el token y datos del usuario
     */
    @Override
    @Transactional(readOnly = true)
    public LoginResponseDTO login(LoginRequestDTO loginRequest) {
        // Obtener el username real basado en lo que el usuario ingresó
        String usernameReal = obtenerUsernameReal(loginRequest.getUsername());
        
        if (usernameReal == null) {
            String mensaje = String.format(
                "Usuario no encontrado con: '%s'. " +
                "Por favor verifica que estés usando tu nombre de usuario, email o nombre completo correcto.",
                loginRequest.getUsername()
            );
            log.warn("Intento de login fallido - usuario no encontrado: {}", loginRequest.getUsername());
            throw new UnauthorizedException(mensaje);
        }
        
        try {
            // Autenticar al usuario usando el username real
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    usernameReal,
                    loginRequest.getPassword()
                )
            );

            // Establecer la autenticación en el contexto de seguridad
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Generar el token JWT
            String jwt = jwtUtils.generateJwtToken(authentication);

            // Obtener los datos del usuario desde la base de datos
            Usuario usuario = usuarioRepository.findByUsername(usernameReal)
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

            log.info("Usuario autenticado exitosamente: {} (identificador usado: {})", usuario.getUsername(), loginRequest.getUsername());

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
            if (usernameReal != null) {
                usuarioRepository.findByUsername(usernameReal).ifPresent(usuario -> {
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
            }

            log.error("Error de autenticación para identificador: {} (username real: {})", loginRequest.getUsername(), usernameReal);
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
     * Resetea la contraseña de un usuario usando su nombre de usuario, email o nombre del propietario (público).
     * 
     * Busca el usuario en el siguiente orden:
     * 1. Por username
     * 2. Por email
     * 3. Por nombre del propietario (si es un propietario)
     *
     * @param requestDTO Datos con username/email/nombre y nueva contraseña
     */
    @Override
    @Transactional
    public void resetPasswordByUsername(ResetPasswordByUsernameRequestDTO requestDTO) {
        // Obtener el username real basado en lo que el usuario ingresó
        String usernameReal = obtenerUsernameReal(requestDTO.getUsername());
        
        if (usernameReal == null) {
            String mensaje = String.format(
                "Usuario no encontrado con: '%s'. " +
                "Por favor verifica que estés usando tu nombre de usuario, email o nombre completo correcto.",
                requestDTO.getUsername()
            );
            throw new ResourceNotFoundException(mensaje);
        }
        
        Usuario usuario = usuarioRepository.findByUsername(usernameReal)
            .orElseThrow(() -> new ResourceNotFoundException("Usuario", "username", usernameReal));

        // Validar que la nueva password sea diferente a la actual
        if (passwordEncoder.matches(requestDTO.getNuevaPassword(), usuario.getPassword())) {
            throw new ValidationException("La nueva contraseña debe ser diferente a la actual");
        }

        // Establecer nueva password encriptada
        usuario.setPassword(passwordEncoder.encode(requestDTO.getNuevaPassword()));
        usuario.setIntentosFallidos(0);
        usuario.setBloqueado(false);
        usuarioRepository.save(usuario);

        log.info("Contraseña reseteada para usuario: {} (identificador usado: {})", usuario.getUsername(), requestDTO.getUsername());
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
        // Establecer la relación con el usuario
        propietario.setUsuario(usuarioGuardado);
        Propietario propietarioGuardado = propietarioRepository.save(propietario);

        log.info("Propietario registrado: {} {} (usuario: {})", 
                propietarioGuardado.getNombres(), 
                propietarioGuardado.getApellidos(),
                usuarioGuardado.getUsername());

        return propietarioMapper.toResponseDTO(propietarioGuardado);
    }
}
