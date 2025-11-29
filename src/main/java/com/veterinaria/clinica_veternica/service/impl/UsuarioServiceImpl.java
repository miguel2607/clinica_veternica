package com.veterinaria.clinica_veternica.service.impl;

import com.veterinaria.clinica_veternica.domain.paciente.Propietario;
import com.veterinaria.clinica_veternica.domain.usuario.RolUsuario;
import com.veterinaria.clinica_veternica.domain.usuario.Usuario;
import com.veterinaria.clinica_veternica.domain.usuario.Veterinario;
import com.veterinaria.clinica_veternica.dto.request.usuario.UsuarioRequestDTO;
import com.veterinaria.clinica_veternica.dto.response.usuario.UsuarioResponseDTO;
import com.veterinaria.clinica_veternica.exception.BusinessException;
import com.veterinaria.clinica_veternica.exception.ResourceNotFoundException;
import com.veterinaria.clinica_veternica.exception.UnauthorizedException;
import com.veterinaria.clinica_veternica.exception.ValidationException;
import com.veterinaria.clinica_veternica.mapper.usuario.UsuarioMapper;
import com.veterinaria.clinica_veternica.patterns.creational.abstractfactory.EmailNotificacionFactory;
import com.veterinaria.clinica_veternica.repository.PropietarioRepository;
import com.veterinaria.clinica_veternica.repository.UsuarioRepository;
import com.veterinaria.clinica_veternica.repository.VeterinarioRepository;
import com.veterinaria.clinica_veternica.service.interfaces.IUsuarioService;
import com.veterinaria.clinica_veternica.util.Constants;
import com.veterinaria.clinica_veternica.util.NameParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class UsuarioServiceImpl implements IUsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final VeterinarioRepository veterinarioRepository;
    private final PropietarioRepository propietarioRepository;
    private final UsuarioMapper usuarioMapper;
    private final PasswordEncoder passwordEncoder;
    private final EmailNotificacionFactory emailFactory;

    @Override
    public UsuarioResponseDTO crear(UsuarioRequestDTO requestDTO) {
        // Validar que el rol sea válido
        if (requestDTO.getRol() == null || requestDTO.getRol().trim().isEmpty()) {
            throw new ValidationException(
                "El rol es obligatorio",
                "rol",
                "Debe especificar un rol válido: ADMIN, VETERINARIO, RECEPCIONISTA, AUXILIAR, PROPIETARIO"
            );
        }

        try {
            RolUsuario.valueOf(requestDTO.getRol().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ValidationException(
                "Rol inválido: " + requestDTO.getRol(),
                "rol",
                "El rol debe ser uno de: ADMIN, VETERINARIO, RECEPCIONISTA, AUXILIAR, PROPIETARIO"
            );
        }

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

        // Si el rol es VETERINARIO, crear automáticamente el registro de Veterinario
        if (usuarioGuardado.getRol() == RolUsuario.VETERINARIO) {
            try {
                // Verificar que no exista ya un veterinario asociado a este usuario
                if (!veterinarioRepository.existsByUsuarioId(usuarioGuardado.getIdUsuario())) {
                    // Extraer nombres y apellidos del username
                    NameParser.NameParts nombreParts = NameParser.extractNamesAndLastNames(usuarioGuardado.getUsername());

                    // Crear un veterinario básico con valores por defecto
                    // El administrador podrá completar la información desde la gestión de veterinarios
                    Veterinario veterinario = Veterinario.builder()
                        .nombres(nombreParts.nombres())
                        .apellidos(nombreParts.apellidos().isEmpty() ? "Pendiente" : nombreParts.apellidos())
                        .documento("TEMP_" + usuarioGuardado.getIdUsuario()) // Documento temporal único
                        .correo(usuarioGuardado.getEmail())
                        .telefono("0000000000") // Teléfono temporal
                        .especialidad("Medicina General") // Especialidad por defecto
                        .registroProfesional("REG_" + usuarioGuardado.getIdUsuario()) // Registro temporal único
                        .aniosExperiencia(0)
                        .activo(usuarioGuardado.getEstado()) // Usar el mismo estado que el usuario
                        .usuario(usuarioGuardado)
                        .build();

                    veterinarioRepository.save(veterinario);
                    log.info("✅ Veterinario creado automáticamente para usuario ID: {} (username: {}, nombres: {}, apellidos: {})",
                            usuarioGuardado.getIdUsuario(), usuarioGuardado.getUsername(), nombreParts.nombres(), nombreParts.apellidos());
                } else {
                    log.info("ℹ️ Usuario ID: {} ya tiene un veterinario asociado", usuarioGuardado.getIdUsuario());
                }
            } catch (Exception e) {
                log.error("❌ Error al crear veterinario automáticamente para usuario ID: {} - {}",
                        usuarioGuardado.getIdUsuario(), e.getMessage(), e);
                // No lanzamos excepción para no interrumpir la creación del usuario
                // El administrador podrá crear el veterinario manualmente después
            }
        }

        // Si el rol es PROPIETARIO, crear automáticamente el registro de Propietario
        if (usuarioGuardado.getRol() == RolUsuario.PROPIETARIO) {
            try {
                // Verificar que no exista ya un propietario asociado a este usuario
                if (!propietarioRepository.existsByUsuarioId(usuarioGuardado.getIdUsuario())) {
                    // Extraer nombres y apellidos del username
                    NameParser.NameParts nombreParts = NameParser.extractNamesAndLastNames(usuarioGuardado.getUsername());

                    // Crear un propietario básico con valores por defecto
                    // El administrador o el propio propietario podrá completar la información después
                    Propietario propietario = Propietario.builder()
                        .documento("TEMP_" + usuarioGuardado.getIdUsuario()) // Documento temporal único
                        .tipoDocumento("CC") // Tipo de documento por defecto (Cédula de Ciudadanía)
                        .nombres(nombreParts.nombres())
                        .apellidos(nombreParts.apellidos().isEmpty() ? "Pendiente" : nombreParts.apellidos())
                        .email(usuarioGuardado.getEmail())
                        .telefono("0000000000") // Teléfono temporal
                        .activo(usuarioGuardado.getEstado()) // Usar el mismo estado que el usuario
                        .observaciones("Propietario creado automáticamente. Complete su información desde la gestión de propietarios.")
                        .usuario(usuarioGuardado)
                        .build();

                    propietarioRepository.save(propietario);
                    log.info("✅ Propietario creado automáticamente para usuario ID: {} (username: {}, nombres: {}, apellidos: {})",
                            usuarioGuardado.getIdUsuario(), usuarioGuardado.getUsername(), nombreParts.nombres(), nombreParts.apellidos());
                } else {
                    log.info("ℹ️ Usuario ID: {} ya tiene un propietario asociado", usuarioGuardado.getIdUsuario());
                }
            } catch (Exception e) {
                log.error("❌ Error al crear propietario automáticamente para usuario ID: {} - {}",
                        usuarioGuardado.getIdUsuario(), e.getMessage(), e);
                // No lanzamos excepción para no interrumpir la creación del usuario
                // El administrador podrá crear el propietario manualmente después
            }
        }

        // Enviar notificación al usuario sobre su cuenta creada
        try {
            enviarNotificacionUsuarioCreado(usuarioGuardado);
        } catch (Exception e) {
            log.error("Error al enviar notificación de creación de usuario ID: {}", 
                    usuarioGuardado.getIdUsuario(), e);
            // No lanzamos excepción para no interrumpir la creación del usuario
        }
        
        return usuarioMapper.toResponseDTO(usuarioGuardado);
    }

    @Override
    public UsuarioResponseDTO actualizar(Long id, UsuarioRequestDTO requestDTO) {
        Usuario usuario = usuarioRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(Constants.ENTIDAD_USUARIO, "id", id));

        // Validar username único (si cambió)
        if (!Objects.equals(usuario.getUsername(), requestDTO.getUsername()) &&
            usuarioRepository.existsByUsername(requestDTO.getUsername())) {
            throw new ValidationException(
                "Ya existe otro usuario con el username: " + requestDTO.getUsername(),
                "username",
                "El nombre de usuario ya está registrado"
            );
        }

        // Validar email único (si cambió)
        if (!Objects.equals(usuario.getEmail(), requestDTO.getEmail()) &&
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
    public void eliminar(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(Constants.ENTIDAD_USUARIO, "id", id));

        // Validar que no se elimine a sí mismo (si se implementa en el futuro)
        // Por ahora, solo desactivamos en lugar de eliminar físicamente por seguridad
        log.warn("Eliminación de usuario solicitada para ID: {}. Se desactivará en lugar de eliminar.", id);
        usuario.setEstado(false);
        usuarioRepository.save(usuario);
        
        // Si realmente se quiere eliminar físicamente, descomentar:
        // usuarioRepository.delete(usuario);
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

    /**
     * Envía una notificación al usuario cuando se crea su cuenta.
     */
    private void enviarNotificacionUsuarioCreado(Usuario usuario) {
        try {
            if (usuario.getEmail() == null || usuario.getEmail().isBlank()) {
                log.warn("No se puede enviar notificación de creación de usuario {}: no tiene email", 
                        usuario.getIdUsuario());
                return;
            }

            String asunto = "Bienvenido a Clínica Veterinaria - Cuenta Creada";
            String mensaje = String.format("""
                    <p>Estimado/a <strong>%s</strong>,</p>
                    
                    <p>¡Bienvenido/a! Le informamos que su cuenta ha sido creada exitosamente en nuestro sistema.</p>
                    
                    <div style="background-color: #f0fdf4; padding: 20px; border-radius: 8px; border-left: 4px solid #10b981; margin: 20px 0;">
                        <h3 style="margin-top: 0; color: #065f46;">Detalles de su cuenta:</h3>
                        <table style="width: 100%%; border-collapse: collapse;">
                            <tr>
                                <td style="padding: 8px 0; color: #4b5563; font-weight: 600;">Usuario:</td>
                                <td style="padding: 8px 0; color: #1f2937;">%s</td>
                            </tr>
                            <tr>
                                <td style="padding: 8px 0; color: #4b5563; font-weight: 600;">Email:</td>
                                <td style="padding: 8px 0; color: #1f2937;">%s</td>
                            </tr>
                            <tr>
                                <td style="padding: 8px 0; color: #4b5563; font-weight: 600;">Rol:</td>
                                <td style="padding: 8px 0; color: #1f2937;">%s</td>
                            </tr>
                        </table>
                    </div>
                    
                    <p><strong>Ya puede iniciar sesión en el sistema con sus credenciales.</strong></p>
                    
                    <p>Si tiene alguna pregunta o necesita asistencia, no dude en contactarnos.</p>
                    
                    <p>Atentamente,<br><strong>Clínica Veterinaria</strong></p>
                    """,
                    usuario.getUsername(),
                    usuario.getUsername(),
                    usuario.getEmail(),
                    usuario.getRol() != null ? usuario.getRol().name() : "No especificado"
            );

            var mensajeNotificacion = emailFactory.crearMensaje(usuario.getEmail(), asunto, mensaje);
            var enviador = emailFactory.crearEnviador();
            boolean enviado = enviador.enviar(mensajeNotificacion);

            if (enviado) {
                log.info("Notificación de creación de usuario {} enviada a: {}", 
                        usuario.getIdUsuario(), usuario.getEmail());
            } else {
                log.warn("Error al enviar notificación de creación de usuario {} a: {}", 
                        usuario.getIdUsuario(), usuario.getEmail());
            }
        } catch (Exception e) {
            log.error("Error al enviar notificación de creación de usuario {}: {}", 
                    usuario.getIdUsuario(), e.getMessage(), e);
        }
    }

}
