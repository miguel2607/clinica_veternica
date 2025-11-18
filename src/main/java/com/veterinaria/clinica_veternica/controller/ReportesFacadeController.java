package com.veterinaria.clinica_veternica.controller;

import com.veterinaria.clinica_veternica.dto.response.facade.ReporteCitasDTO;
import com.veterinaria.clinica_veternica.dto.response.facade.ReporteInventarioDTO;
import com.veterinaria.clinica_veternica.dto.response.facade.ReporteVeterinariosDTO;
import com.veterinaria.clinica_veternica.patterns.structural.facade.ReporteFacadeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

/**
 * Facade Controller especializado para Reportes.
 * Implementa el patrón Facade para simplificar la generación de reportes
 * que requieren coordinación de múltiples servicios.
 *
 * @author Clínica Veterinaria Team
 * @version 3.0 - Refactorizado para usar ReporteFacadeService especializado
 * @since 2025-11-17
 */
@RestController
@RequestMapping("/api/facade/reportes")
@RequiredArgsConstructor
@Tag(name = "Facade - Reportes", description = "Generación de reportes complejos (Facade Pattern)")
public class ReportesFacadeController {

    private final ReporteFacadeService reporteFacadeService;

    @Operation(summary = "Obtener reporte de citas",
               description = "Genera reporte de citas en un rango de fechas con estadísticas.")
    @GetMapping("/citas")
    @PreAuthorize("hasAnyRole('ADMIN', 'VETERINARIO')")
    public ResponseEntity<ReporteCitasDTO> obtenerReporteCitas(
            @Parameter(description = "Fecha de inicio") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @Parameter(description = "Fecha de fin") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {
        return ResponseEntity.ok(reporteFacadeService.generarReporteCitas(fechaInicio, fechaFin));
    }

    @Operation(summary = "Obtener reporte de inventario",
               description = "Genera reporte completo de inventario con valorización y movimientos.")
    @GetMapping("/inventario")
    @PreAuthorize("hasAnyRole('ADMIN', 'AUXILIAR')")
    public ResponseEntity<ReporteInventarioDTO> obtenerReporteInventario() {
        return ResponseEntity.ok(reporteFacadeService.generarReporteInventario());
    }

    @Operation(summary = "Obtener reporte de atenciones por veterinario",
               description = "Genera reporte de atenciones realizadas por cada veterinario en un período.")
    @GetMapping("/veterinarios")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ReporteVeterinariosDTO> obtenerReporteVeterinarios(
            @Parameter(description = "Fecha de inicio") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @Parameter(description = "Fecha de fin") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {
        return ResponseEntity.ok(reporteFacadeService.generarReporteVeterinarios(fechaInicio, fechaFin));
    }
}
