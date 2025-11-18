package com.veterinaria.clinica_veternica.patterns.structural.bridge;

import com.veterinaria.clinica_veternica.repository.CitaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

/**
 * Servicio Factory para crear instancias de ReporteCitasAbstraction
 * con el implementor específico según el formato requerido.
 *
 * Este servicio resuelve el problema de inyección de dependencias
 * cuando hay múltiples implementaciones de ReporteImplementor.
 *
 * @author Clínica Veterinaria Team
 * @version 1.0
 * @since 2025-11-06
 */
@Service
@RequiredArgsConstructor
public class ReporteService {

    private final ReportePDFImpl reportePDF;
    private final ReporteExcelImpl reporteExcel;
    private final ReporteJSONImpl reporteJSON;
    private final CitaRepository citaRepository;

    /**
     * Crea un reporte de citas en formato PDF.
     *
     * @param fechaInicio Fecha de inicio del rango
     * @param fechaFin Fecha de fin del rango
     * @return ReporteCitasAbstraction configurado para PDF
     */
    public ReporteCitasAbstraction crearReportePDF(LocalDate fechaInicio, LocalDate fechaFin) {
        ReporteCitasAbstraction reporte = new ReporteCitasAbstraction(reportePDF, citaRepository);
        return reporte.setRangoFechas(fechaInicio, fechaFin);
    }

    /**
     * Crea un reporte de citas en formato Excel.
     *
     * @param fechaInicio Fecha de inicio del rango
     * @param fechaFin Fecha de fin del rango
     * @return ReporteCitasAbstraction configurado para Excel
     */
    public ReporteCitasAbstraction crearReporteExcel(LocalDate fechaInicio, LocalDate fechaFin) {
        ReporteCitasAbstraction reporte = new ReporteCitasAbstraction(reporteExcel, citaRepository);
        return reporte.setRangoFechas(fechaInicio, fechaFin);
    }

    /**
     * Crea un reporte de citas en formato JSON.
     *
     * @param fechaInicio Fecha de inicio del rango
     * @param fechaFin Fecha de fin del rango
     * @return ReporteCitasAbstraction configurado para JSON
     */
    public ReporteCitasAbstraction crearReporteJSON(LocalDate fechaInicio, LocalDate fechaFin) {
        ReporteCitasAbstraction reporte = new ReporteCitasAbstraction(reporteJSON, citaRepository);
        return reporte.setRangoFechas(fechaInicio, fechaFin);
    }

    /**
     * Crea un reporte de citas según el formato especificado.
     *
     * @param formato Formato del reporte: "pdf", "excel", "json"
     * @param fechaInicio Fecha de inicio del rango
     * @param fechaFin Fecha de fin del rango
     * @return ReporteCitasAbstraction configurado
     * @throws IllegalArgumentException si el formato no es válido
     */
    public ReporteCitasAbstraction crearReporte(String formato, LocalDate fechaInicio, LocalDate fechaFin) {
        return switch (formato.toLowerCase()) {
            case "pdf" -> crearReportePDF(fechaInicio, fechaFin);
            case "excel", "xlsx" -> crearReporteExcel(fechaInicio, fechaFin);
            case "json" -> crearReporteJSON(fechaInicio, fechaFin);
            default -> throw new IllegalArgumentException("Formato no soportado: " + formato);
        };
    }
}

