package com.veterinaria.clinica_veternica.controller;

import com.veterinaria.clinica_veternica.patterns.structural.facade.ClinicaFacade;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Facade Controller especializado para Dashboard y Estadísticas.
 * Implementa el patrón Facade para simplificar operaciones que requieren
 * coordinación de múltiples servicios para dashboards y resúmenes.
 *
 * @author Clínica Veterinaria Team
 * @version 2.0
 * @since 2025-11-17
 */
@RestController
@RequestMapping("/api/facade/dashboard")
@RequiredArgsConstructor
@Tag(name = "Facade - Dashboard", description = "Dashboard y estadísticas generales (Facade Pattern)")
public class DashboardFacadeController {

    private final ClinicaFacade clinicaFacade;

    @Operation(summary = "Obtener dashboard completo",
               description = "Obtiene un resumen completo: citas del día, stock bajo, notificaciones. Ideal para la pantalla principal del frontend.")
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'VETERINARIO', 'RECEPCIONISTA')")
    public ResponseEntity<Map<String, Object>> obtenerDashboard() {
        return ResponseEntity.ok(clinicaFacade.obtenerDashboard());
    }

    @Operation(summary = "Obtener estadísticas generales",
               description = "Obtiene estadísticas completas de la clínica: total de mascotas, propietarios, citas del mes, ingresos, etc.")
    @GetMapping("/estadisticas")
    @PreAuthorize("hasAnyRole('ADMIN', 'VETERINARIO')")
    public ResponseEntity<Map<String, Object>> obtenerEstadisticas() {
        return ResponseEntity.ok(clinicaFacade.obtenerEstadisticasGenerales());
    }

    @Operation(summary = "Obtener resumen de inventario",
               description = "Obtiene resumen completo de inventario con alertas de stock bajo y agotado.")
    @GetMapping("/inventario")
    @PreAuthorize("hasAnyRole('ADMIN', 'VETERINARIO', 'AUXILIAR')")
    public ResponseEntity<Map<String, Object>> obtenerResumenInventario() {
        return ResponseEntity.ok(clinicaFacade.obtenerResumenInventario());
    }
}
