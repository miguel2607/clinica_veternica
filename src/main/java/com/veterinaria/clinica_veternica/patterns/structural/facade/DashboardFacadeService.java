package com.veterinaria.clinica_veternica.patterns.structural.facade;

import com.veterinaria.clinica_veternica.dto.response.agenda.CitaResponseDTO;
import com.veterinaria.clinica_veternica.dto.response.comunicacion.NotificacionResponseDTO;
import com.veterinaria.clinica_veternica.dto.response.facade.DashboardResponseDTO;
import com.veterinaria.clinica_veternica.dto.response.facade.EstadisticasGeneralesDTO;
import com.veterinaria.clinica_veternica.dto.response.inventario.InventarioResponseDTO;
import com.veterinaria.clinica_veternica.dto.response.paciente.MascotaResponseDTO;
import com.veterinaria.clinica_veternica.dto.response.paciente.PropietarioResponseDTO;
import com.veterinaria.clinica_veternica.dto.response.usuario.VeterinarioResponseDTO;
import com.veterinaria.clinica_veternica.service.interfaces.ICitaService;
import com.veterinaria.clinica_veternica.service.interfaces.IInventarioService;
import com.veterinaria.clinica_veternica.service.interfaces.IMascotaService;
import com.veterinaria.clinica_veternica.service.interfaces.INotificacionService;
import com.veterinaria.clinica_veternica.service.interfaces.IPropietarioService;
import com.veterinaria.clinica_veternica.service.interfaces.IVeterinarioService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Servicio especializado para operaciones de Dashboard.
 * Parte de la división del antipatrón God Object (ClinicaFacade).
 *
 * <p>Responsabilidades:</p>
 * <ul>
 *   <li>Generar dashboard principal con citas, stock y notificaciones</li>
 *   <li>Calcular estadísticas generales del sistema</li>
 *   <li>Obtener resumen de inventario</li>
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
public class DashboardFacadeService {

    private final ICitaService citaService;
    private final IInventarioService inventarioService;
    private final INotificacionService notificacionService;
    private final IMascotaService mascotaService;
    private final IPropietarioService propietarioService;
    private final IVeterinarioService veterinarioService;

    private static final int LIMITE_NOTIFICACIONES = 10;

    /**
     * Obtiene el dashboard completo con información resumida.
     *
     * @return DashboardResponseDTO con datos del dashboard
     */
    @Transactional(readOnly = true)
    public DashboardResponseDTO obtenerDashboard() {
        log.info("DashboardFacadeService: Obteniendo dashboard completo");

        List<CitaResponseDTO> citasHoy = obtenerCitasDelDia();
        List<CitaResponseDTO> citasProgramadas = citaService.listarProgramadas();
        List<InventarioResponseDTO> stockBajo = inventarioService.listarConStockBajo();
        List<NotificacionResponseDTO> notificacionesRecientes = obtenerNotificacionesRecientes();

        log.info("Dashboard generado: {} citas hoy, {} stock bajo", citasHoy.size(), stockBajo.size());

        return DashboardResponseDTO.builder()
                .citasHoy(citasHoy)
                .totalCitasHoy(citasHoy.size())
                .citasProgramadas(citasProgramadas)
                .totalCitasProgramadas(citasProgramadas.size())
                .stockBajo(stockBajo)
                .totalStockBajo(stockBajo.size())
                .notificacionesRecientes(notificacionesRecientes)
                .totalNotificacionesRecientes(notificacionesRecientes.size())
                .build();
    }

    /**
     * Obtiene estadísticas generales del sistema.
     *
     * @return EstadisticasGeneralesDTO con estadísticas
     */
    @Transactional(readOnly = true)
    public EstadisticasGeneralesDTO obtenerEstadisticasGenerales() {
        log.info("DashboardFacadeService: Calculando estadísticas generales");

        List<MascotaResponseDTO> mascotas = mascotaService.listarActivas();
        List<PropietarioResponseDTO> propietarios = propietarioService.listarActivos();
        List<VeterinarioResponseDTO> veterinarios = veterinarioService.listarActivos();
        List<CitaResponseDTO> citasProgramadas = citaService.listarProgramadas();
        List<CitaResponseDTO> citasHoy = obtenerCitasDelDia();
        List<InventarioResponseDTO> stockBajo = inventarioService.listarConStockBajo();

        return EstadisticasGeneralesDTO.builder()
                .totalPropietarios((long) propietarios.size())
                .totalMascotas((long) mascotas.size())
                .totalVeterinarios((long) veterinarios.size())
                .totalCitasProgramadas((long) citasProgramadas.size())
                .totalCitasHoy((long) citasHoy.size())
                .insumosStockBajo(stockBajo.size())
                .build();
    }

    /**
     * Obtiene las citas del día actual.
     *
     * @return Lista de citas del día
     */
    private List<CitaResponseDTO> obtenerCitasDelDia() {
        LocalDateTime inicioDia = LocalDate.now().atStartOfDay();
        LocalDateTime finDia = LocalDate.now().atTime(23, 59, 59);
        return citaService.listarPorRangoFechas(inicioDia, finDia);
    }

    /**
     * Obtiene las notificaciones más recientes limitadas.
     *
     * @return Lista de notificaciones recientes
     */
    private List<NotificacionResponseDTO> obtenerNotificacionesRecientes() {
        List<NotificacionResponseDTO> notificaciones = notificacionService.listarTodas();
        return notificaciones.stream()
                .limit(LIMITE_NOTIFICACIONES)
                .toList();
    }
}
