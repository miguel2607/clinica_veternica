package com.veterinaria.clinica_veternica.patterns.structural.facade;

import com.veterinaria.clinica_veternica.domain.agenda.EstadoCita;
import com.veterinaria.clinica_veternica.dto.response.agenda.CitaResponseDTO;
import com.veterinaria.clinica_veternica.dto.response.facade.ReporteCitasDTO;
import com.veterinaria.clinica_veternica.dto.response.facade.ReporteInventarioDTO;
import com.veterinaria.clinica_veternica.dto.response.facade.ReporteVeterinariosDTO;
import com.veterinaria.clinica_veternica.dto.response.inventario.InventarioResponseDTO;
import com.veterinaria.clinica_veternica.patterns.creational.builder.ReporteBuilder;
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

        // Debug: Log de estados encontrados
        if (citas != null && !citas.isEmpty()) {
            log.debug("Estados encontrados en citas: {}", 
                    citas.stream()
                            .map(c -> c.getEstado())
                            .distinct()
                            .collect(java.util.stream.Collectors.toList()));
        }

        // Contar citas por estado
        long citasAtendidas = contarCitasPorEstado(citas, com.veterinaria.clinica_veternica.util.Constants.ESTADO_CITA_ATENDIDA);
        long citasProgramadas = contarCitasPorEstado(citas, com.veterinaria.clinica_veternica.util.Constants.ESTADO_CITA_PROGRAMADA);
        long citasCanceladas = contarCitasPorEstado(citas, com.veterinaria.clinica_veternica.util.Constants.ESTADO_CITA_CANCELADA);
        long citasEnAtencion = contarCitasPorEstado(citas, "EN_ATENCION");
        long citasFinalizadas = contarCitasPorEstado(citas, "FINALIZADA");
        long citasConfirmadas = contarCitasPorEstado(citas, "CONFIRMADA");
        
        // Las citas en atención, finalizadas y confirmadas también se cuentan como atendidas/programadas para el reporte
        long totalCitasAtendidas = citasAtendidas + citasEnAtencion + citasFinalizadas;
        long totalCitasProgramadas = citasProgramadas + citasConfirmadas;

        log.info("Reporte generado: {} citas totales ({} atendidas, {} en atención, {} programadas, {} canceladas)",
                citas != null ? citas.size() : 0, citasAtendidas, citasEnAtencion, citasProgramadas, citasCanceladas);

        return ReporteCitasDTO.builder()
                .fechaInicio(fechaInicio)
                .fechaFin(fechaFin)
                .citas(citas)
                .totalCitas(citas != null ? citas.size() : 0)
                .citasAtendidas(totalCitasAtendidas)
                .citasProgramadas(totalCitasProgramadas)
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

        if (todoInventario == null) {
            log.warn("ReporteFacadeService: La lista de inventario es null, inicializando lista vacía");
            todoInventario = new ArrayList<>();
        }
        if (stockBajo == null) {
            log.warn("ReporteFacadeService: La lista de stock bajo es null, inicializando lista vacía");
            stockBajo = new ArrayList<>();
        }

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

        if (citas == null) {
            log.warn("ReporteFacadeService: La lista de citas es null, inicializando lista vacía");
            citas = new ArrayList<>();
        }

        log.debug("ReporteFacadeService: {} citas encontradas en el rango de fechas", citas.size());

        // Agrupar citas por veterinario
        Map<Long, List<CitaResponseDTO>> citasPorVeterinario = citas.stream()
                .filter(c -> c != null && c.getVeterinario() != null && c.getVeterinario().getIdPersonal() != null)
                .collect(Collectors.groupingBy(c -> c.getVeterinario().getIdPersonal()));

        // Generar estadísticas por veterinario
        List<ReporteVeterinariosDTO.EstadisticaVeterinarioDTO> estadisticas = new ArrayList<>();

        citasPorVeterinario.forEach((idVet, citasVet) -> {
            // Contar citas atendidas: incluye ATENDIDA, EN_ATENCION, y FINALIZADA
            long atendidas = citasVet.stream()
                    .filter(c -> c != null && c.getEstado() != null && 
                            (com.veterinaria.clinica_veternica.util.Constants.ESTADO_CITA_ATENDIDA.equalsIgnoreCase(c.getEstado()) ||
                             "EN_ATENCION".equalsIgnoreCase(c.getEstado()) ||
                             "FINALIZADA".equalsIgnoreCase(c.getEstado())))
                    .count();
            long programadas = citasVet.stream()
                    .filter(c -> c != null && c.getEstado() != null && 
                            (com.veterinaria.clinica_veternica.util.Constants.ESTADO_CITA_PROGRAMADA.equalsIgnoreCase(c.getEstado()) ||
                             "CONFIRMADA".equalsIgnoreCase(c.getEstado())))
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
        if (citas == null || estado == null) {
            return 0;
        }
        return citas.stream()
                .filter(c -> c != null && c.getEstado() != null && estado.equalsIgnoreCase(c.getEstado()))
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

    // ===================================================================
    // MÉTODOS CON REPORTE BUILDER (PATRÓN BUILDER + BRIDGE)
    // ===================================================================

    /**
     * Genera reporte de citas usando ReporteBuilder con opciones avanzadas.
     * Este método usa el patrón Builder para construcción flexible y el patrón Bridge
     * para soportar diferentes formatos de salida (PDF, Excel, JSON).
     *
     * @param fechaInicio Fecha de inicio
     * @param fechaFin Fecha de fin
     * @param formato Formato del reporte
     * @param incluirGraficos Si debe incluir gráficos
     * @return Reporte construido con Builder
     */
    public ReporteBuilder.Reporte generarReporteCitasConBuilder(
            LocalDate fechaInicio,
            LocalDate fechaFin,
            ReporteBuilder.FormatoReporte formato,
            boolean incluirGraficos) {

        log.info("ReporteFacadeService: Generando reporte de citas con Builder - Formato: {}", formato);

        // Usar ReporteBuilder para construcción flexible
        ReporteBuilder.Reporte reporte = new ReporteBuilder()
                .tipoReporte(ReporteBuilder.TipoReporte.CITAS)
                .conRangoFechas(fechaInicio, fechaFin)
                .conFormato(formato)
                .conTitulo(String.format("Reporte de Citas (%s - %s)", fechaInicio, fechaFin))
                .incluirGraficos(incluirGraficos)
                .incluirResumen(true)
                .incluirDetalles(true)
                .agregarColumna("Fecha")
                .agregarColumna("Hora")
                .agregarColumna("Mascota")
                .agregarColumna("Veterinario")
                .agregarColumna("Estado")
                .conOrdenamiento("fecha", true)
                .build();

        log.info("Reporte de citas generado con Builder exitosamente - Formato: {}", formato);
        return reporte;
    }

    /**
     * Genera reporte de inventario usando ReporteBuilder.
     *
     * @param formato Formato del reporte
     * @param incluirGraficos Si debe incluir gráficos
     * @return Reporte construido con Builder
     */
    public ReporteBuilder.Reporte generarReporteInventarioConBuilder(
            ReporteBuilder.FormatoReporte formato,
            boolean incluirGraficos) {

        log.info("ReporteFacadeService: Generando reporte de inventario con Builder - Formato: {}", formato);

        ReporteBuilder.Reporte reporte = new ReporteBuilder()
                .tipoReporte(ReporteBuilder.TipoReporte.INVENTARIO)
                .conFormato(formato)
                .conTitulo("Reporte de Inventario")
                .incluirGraficos(incluirGraficos)
                .incluirResumen(true)
                .agregarColumna("Insumo")
                .agregarColumna("Cantidad")
                .agregarColumna("Stock Mínimo")
                .agregarColumna("Valor Total")
                .conOrdenamiento("cantidad", false)
                .build();

        log.info("Reporte de inventario generado con Builder exitosamente - Formato: {}", formato);
        return reporte;
    }

    /**
     * Genera reporte consolidado de actividad de la clínica usando ReporteBuilder.
     * Este reporte incluye citas, inventario y estadísticas generales.
     *
     * @param fechaInicio Fecha de inicio
     * @param fechaFin Fecha de fin
     * @param formato Formato del reporte
     * @return Reporte consolidado construido con Builder
     */
    public ReporteBuilder.Reporte generarReporteConsolidadoConBuilder(
            LocalDate fechaInicio,
            LocalDate fechaFin,
            ReporteBuilder.FormatoReporte formato) {

        log.info("ReporteFacadeService: Generando reporte consolidado con Builder");

        ReporteBuilder.Reporte reporte = new ReporteBuilder()
                .tipoReporte(ReporteBuilder.TipoReporte.CONSOLIDADO)
                .conRangoFechas(fechaInicio, fechaFin)
                .conFormato(formato)
                .conTitulo(String.format("Reporte Consolidado de Actividad (%s - %s)", fechaInicio, fechaFin))
                .incluirGraficos(true)
                .incluirResumen(true)
                .incluirDetalles(true)
                .agregarColumna("Sección")
                .agregarColumna("Métrica")
                .agregarColumna("Valor")
                .build();

        log.info("Reporte consolidado generado con Builder exitosamente");
        return reporte;
    }
}
