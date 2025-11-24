package com.veterinaria.clinica_veternica.patterns.structural.facade;

import com.veterinaria.clinica_veternica.dto.request.clinico.EvolucionClinicaRequestDTO;
import com.veterinaria.clinica_veternica.dto.request.clinico.HistoriaClinicaRequestDTO;
import com.veterinaria.clinica_veternica.dto.request.paciente.MascotaRequestDTO;
import com.veterinaria.clinica_veternica.dto.request.paciente.PropietarioRequestDTO;
import com.veterinaria.clinica_veternica.dto.response.agenda.CitaResponseDTO;
import com.veterinaria.clinica_veternica.dto.response.clinico.HistoriaClinicaResponseDTO;
import com.veterinaria.clinica_veternica.dto.response.facade.InformacionCompletaMascotaDTO;
import com.veterinaria.clinica_veternica.dto.response.facade.InformacionCompletaPropietarioDTO;
import com.veterinaria.clinica_veternica.dto.response.facade.InformacionCompletaVeterinarioDTO;
import com.veterinaria.clinica_veternica.dto.response.facade.ResultadoAtencionCompletaDTO;
import com.veterinaria.clinica_veternica.dto.response.facade.ResultadoRegistroCompletoDTO;
import com.veterinaria.clinica_veternica.dto.response.paciente.MascotaResponseDTO;
import com.veterinaria.clinica_veternica.exception.ResourceNotFoundException;
import com.veterinaria.clinica_veternica.service.interfaces.ICitaService;
import com.veterinaria.clinica_veternica.service.interfaces.IEvolucionClinicaService;
import com.veterinaria.clinica_veternica.service.interfaces.IHistoriaClinicaService;
import com.veterinaria.clinica_veternica.service.interfaces.IMascotaService;
import com.veterinaria.clinica_veternica.service.interfaces.IPropietarioService;
import com.veterinaria.clinica_veternica.service.interfaces.IVeterinarioService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Servicio especializado para Operaciones Complejas.
 * Parte de la división del antipatrón God Object (ClinicaFacade).
 *
 * <p>Responsabilidades:</p>
 * <ul>
 *   <li>Procesar atención completa (cita + historia + evolución)</li>
 *   <li>Registro completo (propietario + mascota + historia)</li>
 *   <li>Obtener información completa de entidades relacionadas</li>
 * </ul>
 *
 * @author Clínica Veterinaria Team
 * @version 2.0
 * @since 2025-11-17
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class OperacionesFacadeService {

    private final ICitaService citaService;
    private final IHistoriaClinicaService historiaClinicaService;
    private final IEvolucionClinicaService evolucionClinicaService;
    private final IMascotaService mascotaService;
    private final IPropietarioService propietarioService;
    private final IVeterinarioService veterinarioService;

    /**
     * Procesa la atención completa de una cita.
     * Marca la cita como atendida y crea la evolución clínica.
     *
     * @param idCita ID de la cita
     * @param evolucionRequestDTO Datos de la evolución clínica
     * @return ResultadoAtencionCompletaDTO con la información procesada
     */
    public ResultadoAtencionCompletaDTO procesarAtencionCompleta(
            Long idCita,
            EvolucionClinicaRequestDTO evolucionRequestDTO) {

        log.info("OperacionesFacadeService: Procesando atención completa para cita ID: {}", idCita);

        // 1. Marcar cita como atendida
        CitaResponseDTO citaAtendida = citaService.marcarComoAtendida(idCita);
        log.debug("Cita {} marcada como atendida", idCita);

        // 2. Obtener historia clínica de la mascota
        Long idMascota = citaAtendida.getMascota().getIdMascota();
        HistoriaClinicaResponseDTO historiaClinica = historiaClinicaService.buscarPorMascota(idMascota);
        log.debug("Historia clínica {} obtenida para mascota {}", historiaClinica.getIdHistoriaClinica(), idMascota);

        // 3. Crear evolución clínica
        var evolucion = evolucionClinicaService.crear(
                historiaClinica.getIdHistoriaClinica(),
                evolucionRequestDTO
        );
        log.debug("Evolución clínica creada con ID: {}", evolucion.getIdEvolucion());

        log.info("Atención completa procesada exitosamente: cita {}, evolución {}",
                idCita, evolucion.getIdEvolucion());

        return ResultadoAtencionCompletaDTO.builder()
                .cita(citaAtendida)
                .historiaClinica(historiaClinica)
                .evolucionClinica(evolucion)
                .mensaje("Atención completa procesada exitosamente")
                .build();
    }

    /**
     * Registro completo de nueva mascota: propietario + mascota + historia clínica.
     *
     * @param propietarioRequestDTO Datos del propietario
     * @param mascotaRequestDTO Datos de la mascota
     * @param historiaClinicaRequestDTO Datos iniciales de la historia clínica (opcional)
     * @return ResultadoRegistroCompletoDTO con todas las entidades creadas
     */
    public ResultadoRegistroCompletoDTO registrarMascotaCompleta(
            PropietarioRequestDTO propietarioRequestDTO,
            MascotaRequestDTO mascotaRequestDTO,
            HistoriaClinicaRequestDTO historiaClinicaRequestDTO) {

        log.info("OperacionesFacadeService: Registrando mascota completa con propietario e historia clínica");

        // 1. Crear propietario
        var propietario = propietarioService.crear(propietarioRequestDTO);
        log.debug("Propietario creado con ID: {}", propietario.getIdPropietario());

        // 2. Asociar mascota al propietario y crearla
        mascotaRequestDTO.setIdPropietario(propietario.getIdPropietario());
        var mascota = mascotaService.crear(mascotaRequestDTO);
        log.debug("Mascota creada con ID: {}", mascota.getIdMascota());

        // 3. Manejar historia clínica inicial (si se proporciona)
        // Nota: La mascota ya tiene una historia clínica creada automáticamente,
        // por lo que si se proporciona información adicional, se actualiza la existente
        HistoriaClinicaResponseDTO historiaClinica = null;
        if (historiaClinicaRequestDTO != null) {
            try {
                // Intentar obtener la historia clínica existente (creada automáticamente)
                historiaClinica = historiaClinicaService.buscarPorMascota(mascota.getIdMascota());
                log.debug("Historia clínica existente encontrada con ID: {}", historiaClinica.getIdHistoriaClinica());
                
                // Actualizar la historia clínica con los datos proporcionados
                historiaClinicaRequestDTO.setIdMascota(mascota.getIdMascota());
                historiaClinica = historiaClinicaService.actualizar(historiaClinica.getIdHistoriaClinica(), historiaClinicaRequestDTO);
                log.debug("Historia clínica actualizada con ID: {}", historiaClinica.getIdHistoriaClinica());
            } catch (ResourceNotFoundException e) {
                // Si no existe (caso poco probable), crearla
                log.debug("Historia clínica no encontrada, creando nueva");
                historiaClinicaRequestDTO.setIdMascota(mascota.getIdMascota());
                historiaClinica = historiaClinicaService.crear(historiaClinicaRequestDTO);
                log.debug("Historia clínica inicial creada con ID: {}", historiaClinica.getIdHistoriaClinica());
            }
        } else {
            // Si no se proporciona información adicional, obtener la historia clínica creada automáticamente
            try {
                historiaClinica = historiaClinicaService.buscarPorMascota(mascota.getIdMascota());
                log.debug("Historia clínica automática obtenida con ID: {}", historiaClinica.getIdHistoriaClinica());
            } catch (ResourceNotFoundException e) {
                log.debug("No se encontró historia clínica para mascota ID: {}", mascota.getIdMascota());
            }
        }

        log.info("Registro completo exitoso: propietario {}, mascota {}, historia {}",
                propietario.getIdPropietario(),
                mascota.getIdMascota(),
                historiaClinica != null ? historiaClinica.getIdHistoriaClinica() : "N/A");

        return ResultadoRegistroCompletoDTO.builder()
                .propietario(propietario)
                .mascota(mascota)
                .historiaClinica(historiaClinica)
                .mensaje("Registro completo exitoso")
                .build();
    }

    /**
     * Obtiene información completa de una mascota con su historia y citas.
     *
     * @param idMascota ID de la mascota
     * @return InformacionCompletaMascotaDTO con información completa
     */
    @Transactional(readOnly = true)
    public InformacionCompletaMascotaDTO obtenerInformacionCompletaMascota(Long idMascota) {
        log.info("OperacionesFacadeService: Obteniendo información completa de mascota ID: {}", idMascota);

        MascotaResponseDTO mascota = mascotaService.buscarPorId(idMascota);

        HistoriaClinicaResponseDTO historiaClinica = null;
        try {
            historiaClinica = historiaClinicaService.buscarPorMascota(idMascota);
        } catch (ResourceNotFoundException e) {
            log.debug("Mascota {} no tiene historia clínica aún: {}", idMascota, e.getMessage());
        }

        List<CitaResponseDTO> citas = citaService.listarPorMascota(idMascota);

        return InformacionCompletaMascotaDTO.builder()
                .mascota(mascota)
                .historiaClinica(historiaClinica)
                .citas(citas)
                .totalCitas(citas.size())
                .build();
    }

    /**
     * Obtiene información completa de un propietario con sus mascotas e historias.
     *
     * @param idPropietario ID del propietario
     * @return InformacionCompletaPropietarioDTO con información completa
     */
    @Transactional(readOnly = true)
    public InformacionCompletaPropietarioDTO obtenerInformacionCompletaPropietario(Long idPropietario) {
        log.info("OperacionesFacadeService: Obteniendo información completa de propietario ID: {}", idPropietario);

        var propietario = propietarioService.buscarPorId(idPropietario);
        List<MascotaResponseDTO> mascotas = mascotaService.listarPorPropietario(idPropietario);

        List<HistoriaClinicaResponseDTO> historiasClinicas = new ArrayList<>();
        for (var mascota : mascotas) {
            try {
                HistoriaClinicaResponseDTO historia = historiaClinicaService.buscarPorMascota(
                        mascota.getIdMascota()
                );
                historiasClinicas.add(historia);
            } catch (ResourceNotFoundException e) {
                log.debug("Mascota {} no tiene historia clínica: {}", mascota.getIdMascota(), e.getMessage());
            }
        }

        return InformacionCompletaPropietarioDTO.builder()
                .propietario(propietario)
                .mascotas(mascotas)
                .totalMascotas(mascotas.size())
                .historiasClinicas(historiasClinicas)
                .build();
    }

    /**
     * Obtiene información completa de un veterinario con sus horarios y citas.
     *
     * @param idVeterinario ID del veterinario
     * @return InformacionCompletaVeterinarioDTO con información completa
     */
    @Transactional(readOnly = true)
    public InformacionCompletaVeterinarioDTO obtenerInformacionCompletaVeterinario(Long idVeterinario) {
        log.info("OperacionesFacadeService: Obteniendo información completa de veterinario ID: {}", idVeterinario);

        var veterinario = veterinarioService.buscarPorId(idVeterinario);
        List<CitaResponseDTO> citasProgramadas = citaService.listarPorVeterinario(idVeterinario);

        return InformacionCompletaVeterinarioDTO.builder()
                .veterinario(veterinario)
                .horarios(List.of()) //  Implementar cuando exista servicio de horarios
                .totalHorarios(0)
                .citasProgramadas(citasProgramadas)
                .totalCitasProgramadas(citasProgramadas.size())
                .build();
    }
}
