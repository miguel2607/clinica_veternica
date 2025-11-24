package com.veterinaria.clinica_veternica.patterns.structural.facade;

import com.veterinaria.clinica_veternica.dto.request.agenda.CitaRequestDTO;
import com.veterinaria.clinica_veternica.dto.response.agenda.CitaResponseDTO;
import com.veterinaria.clinica_veternica.dto.response.facade.CalendarioCitasDTO;
import com.veterinaria.clinica_veternica.dto.response.facade.ResultadoCitaConNotificacionDTO;
import com.veterinaria.clinica_veternica.service.interfaces.ICitaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Servicio especializado para operaciones de Citas con Notificaciones.
 * Parte de la división del antipatrón God Object (ClinicaFacade).
 *
 * <p>Responsabilidades:</p>
 * <ul>
 *   <li>Crear citas con notificación automática</li>
 *   <li>Cancelar citas con notificación</li>
 *   <li>Reprogramar citas con notificación</li>
 *   <li>Obtener calendario de citas</li>
 * </ul>
 *
 * @author Clínica Veterinaria Team
 * @version 1.0
 * @since 2025-11-18
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class CitaFacadeService {

    private final ICitaService citaService;

    /**
     * Crea una cita y envía notificación automática al propietario.
     * La notificación se envía automáticamente mediante el patrón Mediator.
     *
     * @param requestDTO Datos de la cita
     * @return CitaResponseDTO con la cita creada
     */
    public CitaResponseDTO crearCitaConNotificacion(CitaRequestDTO requestDTO) {
        log.info("CitaFacadeService: Creando cita con notificación automática");

        // Crear la cita (ya incluye validaciones y notificaciones vía Mediator)
        CitaResponseDTO cita = citaService.crear(requestDTO);

        if (cita != null) {
            log.info("Cita {} creada exitosamente con notificación enviada", cita.getIdCita());
        }
        return cita;
    }

    /**
     * Cancela una cita y envía notificación al propietario.
     *
     * @param idCita ID de la cita
     * @param motivo Motivo de cancelación
     * @param usuario Usuario que cancela
     * @return ResultadoCitaConNotificacionDTO con resultado de la operación
     */
    public ResultadoCitaConNotificacionDTO cancelarCitaConNotificacion(Long idCita, String motivo, String usuario) {
        if (idCita == null) {
            throw new IllegalArgumentException("ID de cita no puede ser nulo");
        }
        log.info("CitaFacadeService: Cancelando cita ID {} con notificación", idCita);

        // Cancelar la cita (ya incluye notificación vía Mediator)
        CitaResponseDTO citaCancelada = citaService.cancelar(idCita, motivo, usuario);

        log.info("Cita {} cancelada exitosamente con notificación", idCita);
        return ResultadoCitaConNotificacionDTO.builder()
                .cita(citaCancelada)
                .notificacion(null) // La notificación se envía automáticamente vía Mediator
                .mensaje("Cita cancelada exitosamente. Notificación enviada al propietario.")
                .build();
    }

    /**
     * Reprograma una cita y envía notificación al propietario.
     *
     * @param idCita ID de la cita
     * @param nuevaCitaDTO Datos de la nueva cita
     * @return ResultadoCitaConNotificacionDTO con la nueva cita
     */
    public ResultadoCitaConNotificacionDTO reprogramarCitaConNotificacion(Long idCita, CitaRequestDTO nuevaCitaDTO) {
        log.info("CitaFacadeService: Reprogramando cita ID {} con notificación", idCita);

        // Cancelar la cita anterior
        citaService.cancelar(idCita, "Reprogramación de cita", "Sistema");

        // Crear nueva cita (ya incluye notificación vía Mediator)
        CitaResponseDTO nuevaCita = citaService.crear(nuevaCitaDTO);

        log.info("Cita {} reprogramada exitosamente a nueva cita {}", idCita, nuevaCita.getIdCita());
        return ResultadoCitaConNotificacionDTO.builder()
                .cita(nuevaCita)
                .notificacion(null) // La notificación se envía automáticamente vía Mediator
                .mensaje("Cita reprogramada exitosamente. Cita anterior ID: " + idCita + ". Nueva cita ID: " + nuevaCita.getIdCita())
                .build();
    }

    /**
     * Obtiene información completa para el calendario de citas.
     * Incluye: citas del día con estadísticas por estado.
     *
     * @param fecha Fecha para la cual se quiere el calendario
     * @return CalendarioCitasDTO con información del calendario
     */
    @Transactional(readOnly = true)
    public CalendarioCitasDTO obtenerCalendarioCitas(LocalDate fecha) {
        log.info("CitaFacadeService: Obteniendo calendario para fecha: {}", fecha);

        // Citas del día
        LocalDateTime inicioDia = fecha.atStartOfDay();
        LocalDateTime finDia = fecha.atTime(23, 59, 59);
        List<CitaResponseDTO> citas = citaService.listarPorRangoFechas(inicioDia, finDia);

        // Contar citas por estado
        long citasAtendidas = citas.stream().filter(c -> com.veterinaria.clinica_veternica.util.Constants.ESTADO_CITA_ATENDIDA.equals(c.getEstado())).count();
        long citasProgramadas = citas.stream().filter(c -> com.veterinaria.clinica_veternica.util.Constants.ESTADO_CITA_PROGRAMADA.equals(c.getEstado())).count();
        long citasCanceladas = citas.stream().filter(c -> com.veterinaria.clinica_veternica.util.Constants.ESTADO_CITA_CANCELADA.equals(c.getEstado())).count();

        log.info("Calendario generado: {} citas totales ({} atendidas, {} programadas, {} canceladas)",
                citas.size(), citasAtendidas, citasProgramadas, citasCanceladas);

        return CalendarioCitasDTO.builder()
                .fecha(fecha)
                .citas(citas)
                .totalCitas(citas.size())
                .citasAtendidas(citasAtendidas)
                .citasProgramadas(citasProgramadas)
                .citasCanceladas(citasCanceladas)
                .build();
    }
}
