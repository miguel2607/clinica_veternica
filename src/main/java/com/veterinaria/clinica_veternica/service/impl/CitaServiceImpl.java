package com.veterinaria.clinica_veternica.service.impl;

import com.veterinaria.clinica_veternica.domain.agenda.Cita;
import com.veterinaria.clinica_veternica.domain.agenda.EstadoCita;
import com.veterinaria.clinica_veternica.domain.paciente.Mascota;
import com.veterinaria.clinica_veternica.domain.usuario.Usuario;
import com.veterinaria.clinica_veternica.domain.usuario.Veterinario;
import com.veterinaria.clinica_veternica.dto.request.agenda.CitaRequestDTO;
import com.veterinaria.clinica_veternica.dto.response.agenda.CitaResponseDTO;
import com.veterinaria.clinica_veternica.exception.ResourceNotFoundException;
import com.veterinaria.clinica_veternica.exception.UnauthorizedException;
import com.veterinaria.clinica_veternica.exception.ValidationException;
import com.veterinaria.clinica_veternica.mapper.agenda.CitaMapper;
import com.veterinaria.clinica_veternica.patterns.behavioral.mediator.CitaMediator;
import com.veterinaria.clinica_veternica.patterns.behavioral.template.AtencionTemplate;
import com.veterinaria.clinica_veternica.patterns.behavioral.template.AtencionConsultaGeneral;
import com.veterinaria.clinica_veternica.patterns.behavioral.template.AtencionCirugia;
import com.veterinaria.clinica_veternica.patterns.behavioral.template.AtencionEmergencia;
import com.veterinaria.clinica_veternica.patterns.creational.builder.CitaBuilder;
import com.veterinaria.clinica_veternica.repository.CitaRepository;
import com.veterinaria.clinica_veternica.repository.MascotaRepository;
import com.veterinaria.clinica_veternica.repository.ServicioRepository;
import com.veterinaria.clinica_veternica.repository.UsuarioRepository;
import com.veterinaria.clinica_veternica.repository.VeterinarioRepository;
import com.veterinaria.clinica_veternica.service.interfaces.ICitaService;
import com.veterinaria.clinica_veternica.util.Constants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Implementación del servicio para gestión de Citas.
 * Utiliza múltiples patrones de diseño:
 * - Mediator: Coordina creación y cambios de estado
 * - Chain of Responsibility: Validaciones en cadena (delegado a CitaValidationService)
 * - State: Gestión de estados de cita
 * - Template Method: Flujos de atención
 * 
 * Separado según SRP: delega validación a CitaValidationService y cálculo de precios a CitaPriceCalculationService.
 *
 * @author Clínica Veterinaria Team
 * @version 2.0
 * @since 2025-11-06
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class CitaServiceImpl implements ICitaService {

    private final CitaRepository citaRepository;
    private final MascotaRepository mascotaRepository;
    private final VeterinarioRepository veterinarioRepository;
    private final UsuarioRepository usuarioRepository;
    private final ServicioRepository servicioRepository;
    private final CitaMapper citaMapper;
    private final CitaMediator citaMediator;
    private final CitaValidationService citaValidationService;
    private final CitaPriceCalculationService citaPriceCalculationService;
    
    // Templates de atención
    private final AtencionConsultaGeneral atencionConsultaGeneral;
    private final AtencionCirugia atencionCirugia;
    private final AtencionEmergencia atencionEmergencia;

    @Override
    public CitaResponseDTO crear(CitaRequestDTO requestDTO) {
        log.info("Creando nueva cita para mascota ID: {}", requestDTO.getIdMascota());

        // Obtener entidades relacionadas
        Mascota mascota = mascotaRepository.findById(requestDTO.getIdMascota())
                .orElseThrow(() -> new ResourceNotFoundException(Constants.ENTIDAD_MASCOTA, "id", requestDTO.getIdMascota()));

        Veterinario veterinario = veterinarioRepository.findById(requestDTO.getIdVeterinario())
                .orElseThrow(() -> new ResourceNotFoundException(Constants.ENTIDAD_VETERINARIO, "id", requestDTO.getIdVeterinario()));

        var servicio = servicioRepository.findById(requestDTO.getIdServicio())
                .orElseThrow(() -> new ResourceNotFoundException(Constants.ENTIDAD_SERVICIO, "id", requestDTO.getIdServicio()));

        // Usar Builder para construir la cita
        CitaBuilder builder = new CitaBuilder()
                .conMascota(mascota)
                .conVeterinario(veterinario)
                .conServicio(servicio)
                .conFecha(requestDTO.getFechaCita())
                .conHora(requestDTO.getHoraCita())
                .conMotivoConsulta(requestDTO.getMotivo())
                .conObservaciones(requestDTO.getObservaciones());
        
        if (Constants.isTrue(requestDTO.getEsEmergencia())) {
            builder.comoEmergencia();
        }
        
        Cita cita = builder
                .conPrecioFinal(citaPriceCalculationService.calcularPrecioFinal(servicio, requestDTO))
                .build();

        // Validar que la fecha no sea en el pasado para citas nuevas
        if (cita.getFechaCita() != null && cita.getFechaCita().isBefore(LocalDate.now())) {
            throw new ValidationException("La fecha de la cita no puede ser en el pasado");
        }

        citaValidationService.validarCita(cita);

        // Usar Mediator para crear la cita (coordina todas las operaciones)
        Cita citaCreada = citaMediator.crearCita(cita);

        log.info("Cita creada exitosamente con ID: {}", citaCreada.getIdCita());
        return citaMapper.toResponseDTO(citaCreada);
    }

    @Override
    public CitaResponseDTO actualizar(Long id, CitaRequestDTO requestDTO) {
        log.info("Actualizando cita ID: {}", id);

        Cita cita = citaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(Constants.ENTIDAD_CITA, "id", id));

        // Validar que la cita pueda ser modificada
        if (!cita.puedeReprogramarse()) {
            throw new ValidationException(
                    "No se puede modificar una cita que ya fue atendida o cancelada",
                    "estado",
                    "Solo se pueden modificar citas programadas o confirmadas"
            );
        }

        // Guardar valores originales para validación
        LocalDate fechaOriginal = cita.getFechaCita();
        java.time.LocalTime horaOriginal = cita.getHoraCita();
        
        // Validar que la nueva fecha y hora sean diferentes a las actuales
        // Si se proporciona una nueva fecha, debe ser diferente a la actual
        if (requestDTO.getFechaCita() != null && fechaOriginal != null) {
            if (fechaOriginal.equals(requestDTO.getFechaCita())) {
                throw new ValidationException(
                        "La nueva fecha debe ser diferente a la fecha actual de la cita",
                        "fechaCita",
                        "Para actualizar la cita, debe cambiar la fecha"
                );
            }
        }
        
        // Si se proporciona una nueva hora, debe ser diferente a la actual
        if (requestDTO.getHoraCita() != null && horaOriginal != null) {
            if (horaOriginal.equals(requestDTO.getHoraCita())) {
                throw new ValidationException(
                        "La nueva hora debe ser diferente a la hora actual de la cita",
                        "horaCita",
                        "Para actualizar la cita, debe cambiar la hora"
                );
            }
        }

        // Actualizar campos permitidos
        if (requestDTO.getFechaCita() != null) {
            cita.setFechaCita(requestDTO.getFechaCita());
        }
        if (requestDTO.getHoraCita() != null) {
            cita.setHoraCita(requestDTO.getHoraCita());
        }
        if (requestDTO.getMotivo() != null) {
            cita.setMotivoConsulta(requestDTO.getMotivo());
        }
        if (requestDTO.getObservaciones() != null) {
            cita.setObservaciones(requestDTO.getObservaciones());
        }

        citaValidationService.validarCita(cita);

        Cita citaActualizada = citaRepository.save(cita);
        log.info("Cita actualizada exitosamente");
        return citaMapper.toResponseDTO(citaActualizada);
    }

    @Override
    @Transactional(readOnly = true)
    public CitaResponseDTO buscarPorId(Long id) {
        Cita cita = citaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(Constants.ENTIDAD_CITA, "id", id));
        return citaMapper.toResponseDTO(cita);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CitaResponseDTO> listarTodos() {
        List<Cita> citas = citaRepository.findAll();
        return citaMapper.toResponseDTOList(citas);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CitaResponseDTO> listarPorVeterinario(Long idVeterinario) {
        Veterinario veterinario = veterinarioRepository.findById(idVeterinario)
                .orElseThrow(() -> new ResourceNotFoundException(Constants.ENTIDAD_VETERINARIO, "id", idVeterinario));

        // Si el usuario autenticado es un veterinario, solo puede ver sus propias citas
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            boolean isVeterinario = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(auth -> auth.equals("ROLE_VETERINARIO"));
            
            if (isVeterinario) {
                // Obtener el usuario autenticado
                String username = authentication.getName();
                Usuario usuario = usuarioRepository.findByUsername(username)
                    .orElseThrow(() -> new UnauthorizedException("Usuario no encontrado"));
                
                // Buscar el veterinario asociado al usuario autenticado
                Veterinario veterinarioAutenticado = veterinarioRepository.findByUsuarioIdWithUsuario(usuario.getIdUsuario())
                    .orElseThrow(() -> new UnauthorizedException("No se encontró un perfil de veterinario asociado a tu usuario"));
                
                // Verificar que el veterinario solicitado sea el mismo que el autenticado
                if (!veterinario.getIdPersonal().equals(veterinarioAutenticado.getIdPersonal())) {
                    throw new UnauthorizedException("No tiene permisos para ver las citas de otro veterinario");
                }
            }
        }

        // Usar el método que carga las relaciones necesarias
        List<Cita> citas = citaRepository.findByVeterinarioWithRelations(veterinario);
        return citaMapper.toResponseDTOList(citas);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CitaResponseDTO> listarMisCitas() {
        // Obtener el usuario autenticado
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UnauthorizedException("Usuario no autenticado");
        }

        String username = authentication.getName();
        Usuario usuario = usuarioRepository.findByUsername(username)
            .orElseThrow(() -> new UnauthorizedException("Usuario no encontrado"));

        // Buscar el veterinario asociado al usuario autenticado
        Veterinario veterinario = veterinarioRepository.findByUsuarioIdWithUsuario(usuario.getIdUsuario())
            .orElseThrow(() -> new UnauthorizedException("No se encontró un perfil de veterinario asociado a tu usuario"));

        // Obtener las citas del veterinario autenticado
        List<Cita> citas = citaRepository.findByVeterinarioWithRelations(veterinario);
        return citaMapper.toResponseDTOList(citas);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CitaResponseDTO> listarPorMascota(Long idMascota) {
        Mascota mascota = mascotaRepository.findById(idMascota)
                .orElseThrow(() -> new ResourceNotFoundException(Constants.ENTIDAD_MASCOTA, "id", idMascota));

        List<Cita> citas = citaRepository.findCitasPorMascotaOrdenadas(mascota);
        return citaMapper.toResponseDTOList(citas);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CitaResponseDTO> listarProgramadas() {
        List<Cita> citas = citaRepository.findCitasProgramadas();
        return citaMapper.toResponseDTOList(citas);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CitaResponseDTO> listarPorRangoFechas(LocalDateTime inicio, LocalDateTime fin) {
        List<Cita> citas = citaRepository.findCitasEnRango(inicio, fin);
        return citaMapper.toResponseDTOList(citas);
    }

    @Override
    public CitaResponseDTO confirmar(Long id) {
        log.info("Confirmando cita ID: {}", id);
        citaMediator.confirmarCita(id);
        Cita cita = citaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(Constants.ENTIDAD_CITA, "id", id));
        return citaMapper.toResponseDTO(cita);
    }

    @Override
    public CitaResponseDTO cancelar(Long id, String motivo, String usuario) {
        log.info("Cancelando cita ID: {} por usuario: {}", id, usuario);
        citaMediator.cancelarCita(id, motivo);
        Cita cita = citaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(Constants.ENTIDAD_CITA, "id", id));
        return citaMapper.toResponseDTO(cita);
    }

    @Override
    public CitaResponseDTO marcarComoAtendida(Long id) {
        log.info("Marcando cita ID: {} como atendida", id);

        Cita cita = citaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(Constants.ENTIDAD_CITA, "id", id));

        // Usar Template Method según el tipo de servicio
        AtencionTemplate template = obtenerTemplateAtencion(cita);
        template.procesarAtencion(cita);

        Cita citaActualizada = citaRepository.save(cita);
        return citaMapper.toResponseDTO(citaActualizada);
    }

    @Override
    public CitaResponseDTO iniciarAtencion(Long id) {
        log.info("Iniciando atención de cita ID: {}", id);

        Cita cita = citaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(Constants.ENTIDAD_CITA, "id", id));

        cita.iniciarAtencion();
        Cita citaActualizada = citaRepository.save(cita);
        return citaMapper.toResponseDTO(citaActualizada);
    }

    @Override
    public CitaResponseDTO finalizarAtencion(Long id) {
        log.info("Finalizando atención de cita ID: {}", id);

        Cita cita = citaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(Constants.ENTIDAD_CITA, "id", id));

        cita.finalizarAtencion();
        Cita citaActualizada = citaRepository.save(cita);
        return citaMapper.toResponseDTO(citaActualizada);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CitaResponseDTO> listarParaRecordatorio(LocalDateTime ahora, LocalDateTime limite) {
        List<Cita> citas = citaRepository.findCitasParaRecordatorio(ahora, limite);
        return citaMapper.toResponseDTOList(citas);
    }

    /**
     * Obtiene el template de atención apropiado según el tipo de servicio.
     */
    private AtencionTemplate obtenerTemplateAtencion(Cita cita) {
        if (cita.getServicio() == null) {
            return atencionConsultaGeneral;
        }

        if (Boolean.TRUE.equals(cita.getEsEmergencia()) || cita.getServicio().esEmergencia()) {
            return atencionEmergencia;
        }

        if (cita.getServicio().esCirugia()) {
            return atencionCirugia;
        }

        return atencionConsultaGeneral;
    }

}

