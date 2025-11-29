package com.veterinaria.clinica_veternica.service.impl;

import com.veterinaria.clinica_veternica.domain.paciente.Propietario;
import com.veterinaria.clinica_veternica.domain.usuario.RolUsuario;
import com.veterinaria.clinica_veternica.domain.usuario.Usuario;
import com.veterinaria.clinica_veternica.dto.request.paciente.PropietarioRequestDTO;
import com.veterinaria.clinica_veternica.dto.response.paciente.PropietarioResponseDTO;
import com.veterinaria.clinica_veternica.exception.BusinessException;
import com.veterinaria.clinica_veternica.exception.ResourceNotFoundException;
import com.veterinaria.clinica_veternica.mapper.paciente.PropietarioMapper;
import com.veterinaria.clinica_veternica.patterns.creational.abstractfactory.EmailNotificacionFactory;
import com.veterinaria.clinica_veternica.patterns.structural.proxy.CachedServiceProxy;
import com.veterinaria.clinica_veternica.repository.PropietarioRepository;
import com.veterinaria.clinica_veternica.repository.UsuarioRepository;
import com.veterinaria.clinica_veternica.service.interfaces.IPropietarioService;
import com.veterinaria.clinica_veternica.util.Constants;
import com.veterinaria.clinica_veternica.util.NameParser;
import com.veterinaria.clinica_veternica.util.ValidationHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class PropietarioServiceImpl implements IPropietarioService {

    private static final String CACHE_PATTERN_PROPIETARIOS = "propietarios:*";

    private final PropietarioRepository propietarioRepository;
    private final PropietarioMapper propietarioMapper;
    private final ValidationHelper validationHelper;
    private final CachedServiceProxy cachedServiceProxy;
    private final EmailNotificacionFactory emailFactory;
    private final UsuarioRepository usuarioRepository;

    @Override
    public PropietarioResponseDTO crear(PropietarioRequestDTO requestDTO) {
        // Validar que no exista el documento
        validationHelper.validateDocumentUnique(
            () -> propietarioRepository.existsByTipoDocumentoAndNumeroDocumento(
                requestDTO.getTipoDocumento(), requestDTO.getDocumento()),
            requestDTO.getTipoDocumento(),
            requestDTO.getDocumento()
        );

        // Validar email único
        validationHelper.validateEmailUnique(
            requestDTO.getEmail(),
            null,
            () -> propietarioRepository.existsByEmail(requestDTO.getEmail())
        );

        Propietario propietario = propietarioMapper.toEntity(requestDTO);

        if (propietario.getActivo() == null) {
            propietario.setActivo(true);
        }

        Propietario propietarioGuardado = propietarioRepository.save(propietario);

        // Enviar notificación al propietario sobre su registro
        try {
            enviarNotificacionPropietarioCreado(propietarioGuardado);
        } catch (Exception e) {
            log.error("Error al enviar notificación de creación de propietario ID: {}", 
                    propietarioGuardado.getIdPropietario(), e);
            // No lanzamos excepción para no interrumpir la creación del propietario
        }

        // Invalidar caché después de crear
        cachedServiceProxy.evictPattern(CACHE_PATTERN_PROPIETARIOS);

        return propietarioMapper.toResponseDTO(propietarioGuardado);
    }

    @Override
    public PropietarioResponseDTO actualizar(Long id, PropietarioRequestDTO requestDTO) {
        Propietario propietario = propietarioRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(Constants.ENTIDAD_PROPIETARIO, "id", id));

        // Validar documento único (si cambió)
        if (!Objects.equals(propietario.getDocumento(), requestDTO.getDocumento())) {
            validationHelper.validateDocumentUnique(
                () -> propietarioRepository.existsByTipoDocumentoAndNumeroDocumento(
                    requestDTO.getTipoDocumento(), requestDTO.getDocumento()),
                requestDTO.getTipoDocumento(),
                requestDTO.getDocumento()
            );
        }

        // Validar email único (si cambió)
        validationHelper.validateEmailUnique(
            requestDTO.getEmail(),
            propietario.getEmail(),
            () -> propietarioRepository.existsByEmail(requestDTO.getEmail())
        );

        propietarioMapper.updateEntityFromDTO(requestDTO, propietario);
        Propietario propietarioActualizado = propietarioRepository.save(propietario);

        // Invalidar caché después de actualizar
        cachedServiceProxy.evictPattern(CACHE_PATTERN_PROPIETARIOS);

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
        // Usar CachedServiceProxy para mejorar rendimiento
        return cachedServiceProxy.executeWithCache(
            "propietarios:todos",
            () -> {
                List<Propietario> propietarios = propietarioRepository.findAll();
                return propietarioMapper.toResponseDTOList(propietarios);
            },
            Constants.CACHE_TTL_DEFAULT_MS
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<PropietarioResponseDTO> listarActivos() {
        // Usar CachedServiceProxy para mejorar rendimiento
        return cachedServiceProxy.executeWithCache(
            "propietarios:activos",
            () -> {
                List<Propietario> propietarios = propietarioRepository.findByActivoTrue();
                return propietarioMapper.toResponseDTOList(propietarios);
            },
            Constants.CACHE_TTL_DEFAULT_MS
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<PropietarioResponseDTO> buscarPorNombre(String nombre) {
        String nombreSanitizado = validationHelper.validateAndSanitizeSearchTerm(nombre, 200);
        List<Propietario> propietarios = propietarioRepository.buscarPorNombreCompleto(nombreSanitizado);
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

        // Invalidar caché después de eliminar
        cachedServiceProxy.evictPattern(CACHE_PATTERN_PROPIETARIOS);
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

        // Invalidar caché después de activar
        cachedServiceProxy.evictPattern(CACHE_PATTERN_PROPIETARIOS);

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

    @Override
    @Transactional
    public PropietarioResponseDTO obtenerOCrearPropietarioPorEmail(String email) {
        log.info("Obteniendo o creando propietario para email: {}", email);
        
        // Buscar propietario existente
        Optional<Propietario> propietarioExistente = propietarioRepository.findByEmail(email);
        if (propietarioExistente.isPresent()) {
            log.info("Propietario encontrado para email: {}", email);
            return propietarioMapper.toResponseDTO(propietarioExistente.get());
        }
        
        // Si no existe, buscar el usuario para obtener datos básicos
        Usuario usuario = usuarioRepository.findByEmail(email)
            .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con email: " + email));
        
        log.info("Propietario no encontrado. Creando nuevo propietario para usuario: {}", usuario.getUsername());
        
        // Crear propietario con datos básicos del usuario
        // Usar el username como documento temporal si no hay documento
        String documento = usuario.getUsername() + "_TEMP";
        String tipoDocumento = "CC"; // Por defecto
        
        // Extraer nombres y apellidos del username si es posible
        String[] partesNombre = usuario.getUsername().split("[._-]");
        String nombres = partesNombre.length > 0 ? capitalizar(partesNombre[0]) : usuario.getUsername();
        String apellidos = partesNombre.length > 1 ? capitalizar(partesNombre[1]) : "Usuario";
        
        // Crear el propietario
        Propietario nuevoPropietario = Propietario.builder()
            .documento(documento)
            .tipoDocumento(tipoDocumento)
            .nombres(nombres)
            .apellidos(apellidos)
            .email(email)
            .telefono("0000000000") // Teléfono temporal, debe ser actualizado
            .activo(true)
            .observaciones("Propietario creado automáticamente desde usuario. Por favor, complete su información.")
            .build();
        
        Propietario propietarioGuardado = propietarioRepository.save(nuevoPropietario);
        
        log.info("Propietario creado automáticamente con ID: {} para email: {}", 
                propietarioGuardado.getIdPropietario(), email);
        
        // Invalidar caché
        cachedServiceProxy.evictPattern(CACHE_PATTERN_PROPIETARIOS);
        
        // No enviar notificación en este caso ya que es una creación automática
        // El usuario deberá completar su información después
        
        return propietarioMapper.toResponseDTO(propietarioGuardado);
    }
    
    /**
     * Capitaliza la primera letra de una cadena.
     */
    private String capitalizar(String texto) {
        if (texto == null || texto.isEmpty()) {
            return texto;
        }
        return texto.substring(0, 1).toUpperCase() + texto.substring(1).toLowerCase();
    }

    /**
     * Envía una notificación al propietario cuando se crea su registro.
     */
    private void enviarNotificacionPropietarioCreado(Propietario propietario) {
        try {
            if (propietario.getEmail() == null || propietario.getEmail().isBlank()) {
                log.warn("No se puede enviar notificación de creación de propietario {}: no tiene email", 
                        propietario.getIdPropietario());
                return;
            }

            String asunto = "Bienvenido a Clínica Veterinaria - Registro Exitoso";
            String mensaje = String.format("""
                    <p>Estimado/a <strong>%s</strong>,</p>
                    
                    <p>¡Bienvenido/a a nuestra clínica veterinaria! Le informamos que su registro ha sido creado exitosamente.</p>
                    
                    <div style="background-color: #f0fdf4; padding: 20px; border-radius: 8px; border-left: 4px solid #10b981; margin: 20px 0;">
                        <h3 style="margin-top: 0; color: #065f46;">Detalles de su registro:</h3>
                        <table style="width: 100%%; border-collapse: collapse;">
                            <tr>
                                <td style="padding: 8px 0; color: #4b5563; font-weight: 600;">Nombre:</td>
                                <td style="padding: 8px 0; color: #1f2937;">%s</td>
                            </tr>
                            <tr>
                                <td style="padding: 8px 0; color: #4b5563; font-weight: 600;">Documento:</td>
                                <td style="padding: 8px 0; color: #1f2937;">%s %s</td>
                            </tr>
                            <tr>
                                <td style="padding: 8px 0; color: #4b5563; font-weight: 600;">Email:</td>
                                <td style="padding: 8px 0; color: #1f2937;">%s</td>
                            </tr>
                            <tr>
                                <td style="padding: 8px 0; color: #4b5563; font-weight: 600;">Teléfono:</td>
                                <td style="padding: 8px 0; color: #1f2937;">%s</td>
                            </tr>
                        </table>
                    </div>
                    
                    <p><strong>Ya puede registrar sus mascotas y programar citas en nuestra clínica.</strong></p>
                    
                    <p>Si tiene alguna pregunta o necesita asistencia, no dude en contactarnos.</p>
                    
                    <p>Atentamente,<br><strong>Clínica Veterinaria</strong></p>
                    """,
                    propietario.getNombreCompleto(),
                    propietario.getNombreCompleto(),
                    propietario.getTipoDocumento() != null ? propietario.getTipoDocumento() : "N/A",
                    propietario.getDocumento(),
                    propietario.getEmail(),
                    propietario.getTelefono() != null ? propietario.getTelefono() : "No especificado"
            );

            var mensajeNotificacion = emailFactory.crearMensaje(propietario.getEmail(), asunto, mensaje);
            var enviador = emailFactory.crearEnviador();
            boolean enviado = enviador.enviar(mensajeNotificacion);

            if (enviado) {
                log.info("Notificación de creación de propietario {} enviada a: {}", 
                        propietario.getIdPropietario(), propietario.getEmail());
            } else {
                log.warn("Error al enviar notificación de creación de propietario {} a: {}", 
                        propietario.getIdPropietario(), propietario.getEmail());
            }
        } catch (Exception e) {
            log.error("Error al enviar notificación de creación de propietario {}: {}",
                    propietario.getIdPropietario(), e.getMessage(), e);
        }
    }

    /**
     * Sincroniza usuarios con rol PROPIETARIO creando automáticamente registros de propietarios
     * para aquellos usuarios que no tienen uno asociado.
     *
     * Útil para:
     * - Migración de datos
     * - Corrección de inconsistencias
     * - Usuarios creados antes de implementar la creación automática
     *
     * @return Número de propietarios creados en la sincronización
     */
    @Override
    @Transactional
    public int sincronizarUsuariosPropietarios() {
        log.info("Iniciando sincronización de usuarios con rol PROPIETARIO");

        // Obtener todos los usuarios con rol PROPIETARIO
        List<Usuario> usuariosPropietarios = usuarioRepository.findByRol(RolUsuario.PROPIETARIO);
        int propietariosCreados = 0;

        for (Usuario usuario : usuariosPropietarios) {
            try {
                // Verificar si ya tiene un propietario asociado
                if (!propietarioRepository.existsByUsuarioId(usuario.getIdUsuario())) {
                    // Extraer nombres y apellidos del username
                    NameParser.NameParts nombreParts = NameParser.extractNamesAndLastNames(usuario.getUsername());

                    // Crear propietario básico
                    Propietario propietario = Propietario.builder()
                        .documento("TEMP_" + usuario.getIdUsuario()) // Documento temporal único
                        .tipoDocumento("CC") // Tipo de documento por defecto (Cédula de Ciudadanía)
                        .nombres(nombreParts.nombres())
                        .apellidos(nombreParts.apellidos().isEmpty() ? "Pendiente" : nombreParts.apellidos())
                        .email(usuario.getEmail())
                        .telefono("0000000000") // Teléfono temporal
                        .activo(usuario.getEstado()) // Usar el mismo estado que el usuario
                        .observaciones("Propietario creado automáticamente mediante sincronización. Complete su información desde la gestión de propietarios.")
                        .usuario(usuario)
                        .build();

                    propietarioRepository.save(propietario);
                    propietariosCreados++;

                    log.info("✅ Propietario creado en sincronización para usuario ID: {} (username: {})",
                            usuario.getIdUsuario(), usuario.getUsername());
                }
            } catch (Exception e) {
                log.error("❌ Error al sincronizar propietario para usuario ID: {} - {}",
                        usuario.getIdUsuario(), e.getMessage(), e);
                // Continuar con el siguiente usuario
            }
        }

        // Invalidar caché después de la sincronización
        if (propietariosCreados > 0) {
            cachedServiceProxy.evictPattern(CACHE_PATTERN_PROPIETARIOS);
        }

        log.info("Sincronización completada: {} propietarios creados de {} usuarios PROPIETARIO",
                propietariosCreados, usuariosPropietarios.size());

        return propietariosCreados;
    }

}
