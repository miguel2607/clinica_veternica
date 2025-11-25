package com.veterinaria.clinica_veternica.service.impl;

import com.veterinaria.clinica_veternica.domain.clinico.HistoriaClinica;
import com.veterinaria.clinica_veternica.domain.paciente.Especie;
import com.veterinaria.clinica_veternica.domain.paciente.Mascota;
import com.veterinaria.clinica_veternica.domain.paciente.Propietario;
import com.veterinaria.clinica_veternica.domain.paciente.Raza;
import com.veterinaria.clinica_veternica.domain.usuario.Usuario;
import com.veterinaria.clinica_veternica.dto.request.paciente.MascotaRequestDTO;
import com.veterinaria.clinica_veternica.dto.response.paciente.MascotaResponseDTO;
import com.veterinaria.clinica_veternica.exception.BusinessException;
import com.veterinaria.clinica_veternica.exception.ResourceNotFoundException;
import com.veterinaria.clinica_veternica.exception.UnauthorizedException;
import org.springframework.dao.DataAccessException;

import com.veterinaria.clinica_veternica.mapper.paciente.MascotaMapper;
import com.veterinaria.clinica_veternica.patterns.creational.abstractfactory.EmailNotificacionFactory;
import com.veterinaria.clinica_veternica.patterns.creational.builder.HistoriaClinicaBuilder;
import com.veterinaria.clinica_veternica.patterns.structural.proxy.CachedServiceProxy;
import com.veterinaria.clinica_veternica.repository.EspecieRepository;
import com.veterinaria.clinica_veternica.repository.HistoriaClinicaRepository;
import com.veterinaria.clinica_veternica.repository.MascotaRepository;
import com.veterinaria.clinica_veternica.repository.PropietarioRepository;
import com.veterinaria.clinica_veternica.repository.RazaRepository;
import com.veterinaria.clinica_veternica.repository.UsuarioRepository;
import com.veterinaria.clinica_veternica.service.interfaces.IMascotaService;
import com.veterinaria.clinica_veternica.util.Constants;
import com.veterinaria.clinica_veternica.util.ValidationHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class MascotaServiceImpl implements IMascotaService {

    private static final String CACHE_PATTERN_MASCOTAS = "mascotas:*";

    private final MascotaRepository mascotaRepository;
    private final PropietarioRepository propietarioRepository;
    private final EspecieRepository especieRepository;
    private final RazaRepository razaRepository;
    private final HistoriaClinicaRepository historiaClinicaRepository;
    private final UsuarioRepository usuarioRepository;
    private final MascotaMapper mascotaMapper;
    private final ValidationHelper validationHelper;
    private final CachedServiceProxy cachedServiceProxy;
    private final EmailNotificacionFactory emailFactory;

    @Override
    public MascotaResponseDTO crear(MascotaRequestDTO requestDTO) {
        // Obtener el usuario autenticado
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Propietario propietario;
        
        // Verificar si el usuario es propietario
        if (authentication != null && authentication.isAuthenticated()) {
            Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
            boolean esPropietario = authorities.stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_PROPIETARIO"));
            
            if (esPropietario) {
                // Si es propietario, obtener su propio perfil y usar su ID
                String username = authentication.getName();
                Usuario usuario = usuarioRepository.findByUsername(username)
                    .orElseThrow(() -> new UnauthorizedException("Usuario no encontrado"));
                
                propietario = propietarioRepository.findByEmail(usuario.getEmail())
                    .orElseThrow(() -> new ResourceNotFoundException(
                        "No se encontró un perfil de propietario asociado a tu usuario. Por favor, completa tu perfil primero."));
                
                // Asegurar que el propietario solo pueda crear mascotas para sí mismo
                if (requestDTO.getIdPropietario() != null && 
                    !requestDTO.getIdPropietario().equals(propietario.getIdPropietario())) {
                    throw new UnauthorizedException("No puedes crear mascotas para otros propietarios");
                }
                
                // Asignar automáticamente el ID del propietario autenticado
                requestDTO.setIdPropietario(propietario.getIdPropietario());
                log.info("Propietario autenticado creando mascota para su propio perfil (ID: {})", 
                        propietario.getIdPropietario());
            } else {
                // Para otros roles (ADMIN, RECEPCIONISTA, VETERINARIO), el ID del propietario es obligatorio
                if (requestDTO.getIdPropietario() == null) {
                    throw new BusinessException("El ID del propietario es obligatorio");
                }
                propietario = propietarioRepository.findById(requestDTO.getIdPropietario())
                    .orElseThrow(() -> new ResourceNotFoundException(Constants.ENTIDAD_PROPIETARIO, "id", requestDTO.getIdPropietario()));
            }
        } else {
            // Si no hay autenticación, el ID del propietario es obligatorio (aunque esto no debería pasar por @PreAuthorize)
            if (requestDTO.getIdPropietario() == null) {
                throw new BusinessException("El ID del propietario es obligatorio");
            }
            propietario = propietarioRepository.findById(requestDTO.getIdPropietario())
                .orElseThrow(() -> new ResourceNotFoundException(Constants.ENTIDAD_PROPIETARIO, "id", requestDTO.getIdPropietario()));
        }

        // Validar que la especie existe
        Especie especie = especieRepository.findById(requestDTO.getIdEspecie())
            .orElseThrow(() -> new ResourceNotFoundException(Constants.ENTIDAD_ESPECIE, "id", requestDTO.getIdEspecie()));

        // Validar raza si se proporciona
        Raza raza = null;
        if (requestDTO.getIdRaza() != null) {
            raza = razaRepository.findById(requestDTO.getIdRaza())
                .orElseThrow(() -> new ResourceNotFoundException("Raza", "id", requestDTO.getIdRaza()));

            // Validar que la raza pertenezca a la especie
            validationHelper.validateRazaBelongsToSpecies(
                raza.getEspecie().getIdEspecie(),
                especie.getIdEspecie(),
                raza.getNombre(),
                especie.getNombre()
            );
        }

        // Establecer valores por defecto si no se proporcionan
        if (requestDTO.getEsterilizado() == null) {
            requestDTO.setEsterilizado(false);
        }
        if (requestDTO.getActivo() == null) {
            requestDTO.setActivo(true);
        }
        
        Mascota mascota = mascotaMapper.toEntity(requestDTO);
        mascota.setPropietario(propietario);
        mascota.setEspecie(especie);
        mascota.setRaza(raza);

        if (mascota.getActivo() == null) {
            mascota.setActivo(true);
        }

        Mascota mascotaGuardada = mascotaRepository.save(mascota);

        // Crear automáticamente una historia clínica para la mascota
        try {
            HistoriaClinica historiaClinica = new HistoriaClinicaBuilder()
                    .conMascota(mascotaGuardada)
                    .build();
            
            HistoriaClinica historiaGuardada = historiaClinicaRepository.save(historiaClinica);
            log.info("Historia clínica creada automáticamente para mascota ID: {} con historia ID: {}", 
                    mascotaGuardada.getIdMascota(), historiaGuardada.getIdHistoriaClinica());
        } catch (DataAccessException e) {
            log.error("Error de acceso a datos al crear historia clínica automática para mascota ID: {}", 
                    mascotaGuardada.getIdMascota(), e);
            // No lanzamos excepción para no interrumpir la creación de la mascota
            // La historia clínica se puede crear manualmente después si es necesario
        } catch (IllegalArgumentException e) {
            log.error("Error de validación al crear historia clínica automática para mascota ID: {}", 
                    mascotaGuardada.getIdMascota(), e);
            // No lanzamos excepción para no interrumpir la creación de la mascota
        }

        // Enviar notificación al propietario sobre la nueva mascota registrada
        try {
            enviarNotificacionMascotaCreada(mascotaGuardada);
        } catch (Exception e) {
            log.error("Error al enviar notificación de creación de mascota ID: {}", 
                    mascotaGuardada.getIdMascota(), e);
            // No lanzamos excepción para no interrumpir la creación de la mascota
        }

        // Invalidar caché después de crear
        cachedServiceProxy.evictPattern(CACHE_PATTERN_MASCOTAS);

        return mascotaMapper.toResponseDTO(mascotaGuardada);
    }

    @Override
    public MascotaResponseDTO actualizar(Long id, MascotaRequestDTO requestDTO) {
        Mascota mascota = mascotaRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(Constants.ENTIDAD_MASCOTA, "id", id));

        // Validar propietario
        Propietario propietario = propietarioRepository.findById(requestDTO.getIdPropietario())
            .orElseThrow(() -> new ResourceNotFoundException(Constants.ENTIDAD_PROPIETARIO, "id", requestDTO.getIdPropietario()));

        // Validar especie
        Especie especie = especieRepository.findById(requestDTO.getIdEspecie())
            .orElseThrow(() -> new ResourceNotFoundException(Constants.ENTIDAD_ESPECIE, "id", requestDTO.getIdEspecie()));

        // Validar raza
        Raza raza = null;
        if (requestDTO.getIdRaza() != null) {
            raza = razaRepository.findById(requestDTO.getIdRaza())
                .orElseThrow(() -> new ResourceNotFoundException("Raza", "id", requestDTO.getIdRaza()));

            validationHelper.validateRazaBelongsToSpecies(
                raza.getEspecie().getIdEspecie(),
                especie.getIdEspecie(),
                raza.getNombre(),
                especie.getNombre()
            );
        }

        mascotaMapper.updateEntityFromDTO(requestDTO, mascota);
        mascota.setPropietario(propietario);
        mascota.setEspecie(especie);
        mascota.setRaza(raza);

        Mascota mascotaActualizada = mascotaRepository.save(mascota);

        // Invalidar caché después de actualizar
        cachedServiceProxy.evictPattern(CACHE_PATTERN_MASCOTAS);

        return mascotaMapper.toResponseDTO(mascotaActualizada);
    }

    @Override
    @Transactional(readOnly = true)
    public MascotaResponseDTO buscarPorId(Long id) {
        Mascota mascota = mascotaRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(Constants.ENTIDAD_MASCOTA, "id", id));
        
        // Validar que si el usuario es propietario, solo pueda ver sus propias mascotas
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
            boolean esPropietario = authorities.stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_PROPIETARIO"));
            
            if (esPropietario) {
                // Obtener el usuario autenticado
                String username = authentication.getName();
                Usuario usuario = usuarioRepository.findByUsername(username)
                    .orElseThrow(() -> new UnauthorizedException("Usuario no encontrado"));
                
                // Buscar el propietario asociado al usuario
                Optional<Propietario> propietarioOpt = propietarioRepository.findByEmail(usuario.getEmail());
                
                if (propietarioOpt.isPresent()) {
                    Propietario propietario = propietarioOpt.get();
                    // Verificar que la mascota pertenezca al propietario
                    if (mascota.getPropietario() == null || 
                        !mascota.getPropietario().getIdPropietario().equals(propietario.getIdPropietario())) {
                        throw new UnauthorizedException("No tienes permiso para ver esta mascota");
                    }
                } else {
                    throw new UnauthorizedException("No se encontró un perfil de propietario asociado a tu usuario");
                }
            }
        }
        
        return mascotaMapper.toResponseDTO(mascota);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MascotaResponseDTO> listarTodas() {
        // Usar CachedServiceProxy para mejorar rendimiento
        return cachedServiceProxy.executeWithCache(
            "mascotas:todas",
            () -> {
                List<Mascota> mascotas = mascotaRepository.findAll();
                return mascotaMapper.toResponseDTOList(mascotas);
            },
            Constants.CACHE_TTL_DEFAULT_MS
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<MascotaResponseDTO> listarActivas() {
        // Usar CachedServiceProxy para mejorar rendimiento
        return cachedServiceProxy.executeWithCache(
            "mascotas:activas",
            () -> {
                List<Mascota> mascotas = mascotaRepository.findByActivoTrue();
                return mascotaMapper.toResponseDTOList(mascotas);
            },
            Constants.CACHE_TTL_DEFAULT_MS
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<MascotaResponseDTO> listarPorPropietario(Long idPropietario) {
        if (!propietarioRepository.existsById(idPropietario)) {
            throw new ResourceNotFoundException(Constants.ENTIDAD_PROPIETARIO, "id", idPropietario);
        }
        List<Mascota> mascotas = mascotaRepository.findByPropietarioId(idPropietario);
        return mascotaMapper.toResponseDTOList(mascotas);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MascotaResponseDTO> listarPorEspecie(Long idEspecie) {
        if (!especieRepository.existsById(idEspecie)) {
            throw new ResourceNotFoundException(Constants.ENTIDAD_ESPECIE, "id", idEspecie);
        }
        List<Mascota> mascotas = mascotaRepository.findByEspecieId(idEspecie);
        return mascotaMapper.toResponseDTOList(mascotas);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MascotaResponseDTO> listarPorRaza(Long idRaza) {
        if (!razaRepository.existsById(idRaza)) {
            throw new ResourceNotFoundException("Raza", "id", idRaza);
        }
        List<Mascota> mascotas = mascotaRepository.findByRazaId(idRaza);
        return mascotaMapper.toResponseDTOList(mascotas);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MascotaResponseDTO> buscarPorNombre(String nombre) {
        String nombreSanitizado = validationHelper.validateAndSanitizeSearchTerm(nombre, 100);
        List<Mascota> mascotas = mascotaRepository.findByNombreContainingIgnoreCase(nombreSanitizado);
        return mascotaMapper.toResponseDTOList(mascotas);
    }

    @Override
    public void eliminar(Long id) {
        Mascota mascota = mascotaRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(Constants.ENTIDAD_MASCOTA, "id", id));

        // Verificar si tiene historia clínica
        if (mascota.getHistoriaClinica() != null) {
            throw new BusinessException(
                "No se puede eliminar la mascota porque tiene una historia clínica asociada",
                "MASCOTA_CON_HISTORIA_CLINICA"
            );
        }

        // Verificar si tiene citas
        if (mascota.getCitas() != null && !mascota.getCitas().isEmpty()) {
            throw new BusinessException(
                "No se puede eliminar la mascota porque tiene " + mascota.getCitas().size() + " cita(s) asociada(s)",
                "MASCOTA_CON_CITAS"
            );
        }

        mascota.setActivo(false);
        mascotaRepository.save(mascota);

        // Invalidar caché después de eliminar
        cachedServiceProxy.evictPattern(CACHE_PATTERN_MASCOTAS);
    }

    @Override
    public MascotaResponseDTO activar(Long id) {
        Mascota mascota = mascotaRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(Constants.ENTIDAD_MASCOTA, "id", id));

        if (Constants.isTrue(mascota.getActivo())) {
            throw new BusinessException("La mascota ya está activa", "MASCOTA_YA_ACTIVA");
        }

        mascota.setActivo(true);
        Mascota mascotaActivada = mascotaRepository.save(mascota);

        // Invalidar caché después de activar
        cachedServiceProxy.evictPattern(CACHE_PATTERN_MASCOTAS);

        return mascotaMapper.toResponseDTO(mascotaActivada);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existePorNombreYPropietario(String nombre, Long idPropietario) {
        if (nombre == null || nombre.trim().isEmpty() || idPropietario == null) {
            return false;
        }
        return mascotaRepository.existsByNombreAndPropietarioId(nombre.trim(), idPropietario);
    }

    /**
     * Envía una notificación al propietario cuando se crea una nueva mascota.
     */
    private void enviarNotificacionMascotaCreada(Mascota mascota) {
        try {
            Propietario propietario = mascota.getPropietario();
            if (propietario == null || propietario.getEmail() == null || propietario.getEmail().isBlank()) {
                log.warn("No se puede enviar notificación de creación de mascota {}: el propietario no tiene email", 
                        mascota.getIdMascota());
                return;
            }

            String asunto = "Mascota Registrada - " + mascota.getNombre();
            String mensaje = String.format("""
                    <p>Estimado/a <strong>%s</strong>,</p>
                    
                    <p>Le informamos que su mascota <strong>%s</strong> ha sido registrada exitosamente en nuestra clínica veterinaria.</p>
                    
                    <div style="background-color: #f0f9ff; padding: 20px; border-radius: 8px; border-left: 4px solid #3b82f6; margin: 20px 0;">
                        <h3 style="margin-top: 0; color: #1e40af;">Detalles de la mascota:</h3>
                        <table style="width: 100%%; border-collapse: collapse;">
                            <tr>
                                <td style="padding: 8px 0; color: #4b5563; font-weight: 600;">Nombre:</td>
                                <td style="padding: 8px 0; color: #1f2937;">%s</td>
                            </tr>
                            <tr>
                                <td style="padding: 8px 0; color: #4b5563; font-weight: 600;">Especie:</td>
                                <td style="padding: 8px 0; color: #1f2937;">%s</td>
                            </tr>
                            <tr>
                                <td style="padding: 8px 0; color: #4b5563; font-weight: 600;">Raza:</td>
                                <td style="padding: 8px 0; color: #1f2937;">%s</td>
                            </tr>
                            <tr>
                                <td style="padding: 8px 0; color: #4b5563; font-weight: 600;">Fecha de nacimiento:</td>
                                <td style="padding: 8px 0; color: #1f2937;">%s</td>
                            </tr>
                        </table>
                    </div>
                    
                    <p>Su mascota ya cuenta con una historia clínica creada y está lista para recibir atención veterinaria.</p>
                    
                    <p>Si tiene alguna pregunta o necesita programar una cita, no dude en contactarnos.</p>
                    
                    <p>Atentamente,<br><strong>Clínica Veterinaria</strong></p>
                    """,
                    propietario.getNombreCompleto(),
                    mascota.getNombre(),
                    mascota.getNombre(),
                    mascota.getEspecie() != null ? mascota.getEspecie().getNombre() : "No especificada",
                    mascota.getRaza() != null ? mascota.getRaza().getNombre() : "No especificada",
                    mascota.getFechaNacimiento() != null ? mascota.getFechaNacimiento().toString() : "No especificada"
            );

            var mensajeNotificacion = emailFactory.crearMensaje(propietario.getEmail(), asunto, mensaje);
            var enviador = emailFactory.crearEnviador();
            boolean enviado = enviador.enviar(mensajeNotificacion);

            if (enviado) {
                log.info("Notificación de creación de mascota {} enviada a: {}", 
                        mascota.getIdMascota(), propietario.getEmail());
            } else {
                log.warn("Error al enviar notificación de creación de mascota {} a: {}", 
                        mascota.getIdMascota(), propietario.getEmail());
            }
        } catch (Exception e) {
            log.error("Error al enviar notificación de creación de mascota {}: {}", 
                    mascota.getIdMascota(), e.getMessage(), e);
        }
    }
}
