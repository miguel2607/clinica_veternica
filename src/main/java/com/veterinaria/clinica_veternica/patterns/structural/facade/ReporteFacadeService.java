package com.veterinaria.clinica_veternica.patterns.structural.facade;

import com.veterinaria.clinica_veternica.domain.agenda.EstadoCita;
import com.veterinaria.clinica_veternica.dto.response.agenda.CitaResponseDTO;
import com.veterinaria.clinica_veternica.dto.response.facade.ReporteCitasDTO;
import com.veterinaria.clinica_veternica.dto.response.facade.ReporteInventarioDTO;
import com.veterinaria.clinica_veternica.dto.response.facade.ReporteVeterinariosDTO;
import com.veterinaria.clinica_veternica.dto.response.inventario.InventarioResponseDTO;
import com.veterinaria.clinica_veternica.service.interfaces.ICitaService;
import com.veterinaria.clinica_veternica.service.interfaces.IInventarioService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Servicio especializado para generación de Reportes.
 * Parte de la división del antipatrón God Object (ClinicaFacade).
 *
 * <p>Responsabilidades:</p>
 * <ul>
 *   <li>Generar reportes de citas por rango de fechas</li>
 *   <li>Generar reportes de inventario con valorización</li>
 *   <li>Generar reportes de atenciones por veterinario</li>
 * </ul>
 *
 * @author Clínica Veterinaria Team
 * @version 2.0
 * @since 2025-11-17
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReporteFacadeService {

    private final ICitaService citaService;
    private final IInventarioService inventarioService;

    /**
     * Genera reporte de citas en un rango de fechas con estadísticas.
     *
     * @param fechaInicio Fecha de inicio
     * @param fechaFin Fecha de fin
     * @return ReporteCitasDTO con el reporte completo
     */
    public ReporteCitasDTO generarReporteCitas(LocalDate fechaInicio, LocalDate fechaFin) {
        log.info("ReporteFacadeService: Generando reporte de citas desde {} hasta {}", fechaInicio, fechaFin);

        LocalDateTime inicio = fechaInicio.atStartOfDay();
        LocalDateTime fin = fechaFin.atTime(23, 59, 59);

        List<CitaResponseDTO> citas = citaService.listarPorRangoFechas(inicio, fin);

        // Contar citas por estado
        long citasAtendidas = contarCitasPorEstado(citas, com.veterinaria.clinica_veternica.util.Constants.ESTADO_CITA_ATENDIDA);
        long citasProgramadas = contarCitasPorEstado(citas, com.veterinaria.clinica_veternica.util.Constants.ESTADO_CITA_PROGRAMADA);
        long citasCanceladas = contarCitasPorEstado(citas, com.veterinaria.clinica_veternica.util.Constants.ESTADO_CITA_CANCELADA);

        log.info("Reporte generado: {} citas totales ({} atendidas, {} programadas, {} canceladas)",
                citas.size(), citasAtendidas, citasProgramadas, citasCanceladas);

        return ReporteCitasDTO.builder()
                .fechaInicio(fechaInicio)
                .fechaFin(fechaFin)
                .citas(citas)
                .totalCitas(citas.size())
                .citasAtendidas(citasAtendidas)
                .citasProgramadas(citasProgramadas)
                .citasCanceladas(citasCanceladas)
                .build();
    }

    /**
     * Genera reporte completo de inventario con valorización.
     *
     * @return ReporteInventarioDTO con el reporte completo
     */
    public ReporteInventarioDTO generarReporteInventario() {
        log.info("ReporteFacadeService: Generando reporte de inventario");

        List<InventarioResponseDTO> todoInventario = inventarioService.listarTodos();
        List<InventarioResponseDTO> stockBajo = inventarioService.listarConStockBajo();

        // Calcular valor total del inventario
        BigDecimal valorTotal = calcularValorTotalInventario(todoInventario);

        log.info("Reporte inventario generado: {} items totales, {} con stock bajo, valor total: {}",
                todoInventario.size(), stockBajo.size(), valorTotal);

        return ReporteInventarioDTO.builder()
                .inventarios(todoInventario)
                .totalItems(todoInventario.size())
                .stockBajo(stockBajo)
                .totalStockBajo(stockBajo.size())
                .valorTotalInventario(valorTotal)
                .build();
    }

    /**
     * Genera reporte de atenciones por veterinario en un período.
     *
     * @param fechaInicio Fecha de inicio
     * @param fechaFin Fecha de fin
     * @return ReporteVeterinariosDTO con estadísticas por veterinario
     */
    public ReporteVeterinariosDTO generarReporteVeterinarios(LocalDate fechaInicio, LocalDate fechaFin) {
        log.info("ReporteFacadeService: Generando reporte de veterinarios desde {} hasta {}", fechaInicio, fechaFin);

        LocalDateTime inicio = fechaInicio.atStartOfDay();
        LocalDateTime fin = fechaFin.atTime(23, 59, 59);

        List<CitaResponseDTO> citas = citaService.listarPorRangoFechas(inicio, fin);

        // Agrupar citas por veterinario
        Map<Long, List<CitaResponseDTO>> citasPorVeterinario = citas.stream()
                .filter(c -> c.getVeterinario() != null)
                .collect(Collectors.groupingBy(c -> c.getVeterinario().getIdPersonal()));

        // Generar estadísticas por veterinario
        List<ReporteVeterinariosDTO.EstadisticaVeterinarioDTO> estadisticas = new ArrayList<>();

        citasPorVeterinario.forEach((idVet, citasVet) -> {
            long atendidas = citasVet.stream()
                    .filter(c -> com.veterinaria.clinica_veternica.util.Constants.ESTADO_CITA_ATENDIDA.equals(c.getEstado()))
                    .count();
            long programadas = citasVet.stream()
                    .filter(c -> com.veterinaria.clinica_veternica.util.Constants.ESTADO_CITA_PROGRAMADA.equals(c.getEstado()))
                    .count();

            // Obtener nombre del veterinario de la primera cita
            CitaResponseDTO primeraCita = citasVet.get(0);
            String nombreVet = primeraCita.getVeterinario().getNombreCompleto();
            String especialidad = primeraCita.getVeterinario().getEspecialidad();

            estadisticas.add(ReporteVeterinariosDTO.EstadisticaVeterinarioDTO.builder()
                    .idVeterinario(idVet)
                    .nombreVeterinario(nombreVet)
                    .especialidad(especialidad)
                    .totalCitasAtendidas(atendidas)
                    .totalCitasProgramadas(programadas)
                    .build());
        });

        long totalAtenciones = estadisticas.stream()
                .mapToLong(ReporteVeterinariosDTO.EstadisticaVeterinarioDTO::getTotalCitasAtendidas)
                .sum();

        log.info("Reporte veterinarios generado: {} veterinarios, {} atenciones totales",
                estadisticas.size(), totalAtenciones);

        return ReporteVeterinariosDTO.builder()
                .fechaInicio(fechaInicio)
                .fechaFin(fechaFin)
                .estadisticasPorVeterinario(estadisticas)
                .totalAtenciones(totalAtenciones)
                .build();
    }

    /**
     * Cuenta citas por estado específico.
     *
     * @param citas Lista de citas
     * @param estado Estado a contar
     * @return Cantidad de citas con ese estado
     */
    private long contarCitasPorEstado(List<CitaResponseDTO> citas, String estado) {
        return citas.stream()
                .filter(c -> estado.equals(c.getEstado()))
                .count();
    }

    /**
     * Calcula el valor total del inventario.
     *
     * @param inventarios Lista de inventarios
     * @return Valor total
     */
    private BigDecimal calcularValorTotalInventario(List<InventarioResponseDTO> inventarios) {
        return inventarios.stream()
                .map(inv -> inv.getValorTotal() != null ? inv.getValorTotal() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
