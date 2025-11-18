package com.veterinaria.clinica_veternica.controller;

import com.veterinaria.clinica_veternica.patterns.structural.facade.ClinicaFacade;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Facade Controller especializado para Notificaciones Masivas.
 * Implementa el patrón Facade para simplificar envío masivo de notificaciones
 * que requieren coordinación de múltiples servicios.
 *
 * @author Clínica Veterinaria Team
 * @version 2.0
 * @since 2025-11-17
 */
@RestController
@RequestMapping("/api/facade/notificaciones")
@RequiredArgsConstructor
@Tag(name = "Facade - Notificaciones", description = "Notificaciones masivas y automatizadas (Facade Pattern)")
public class NotificacionesFacadeController {

    private final ClinicaFacade clinicaFacade;

    @Operation(summary = "Enviar recordatorios de citas",
               description = "Envía recordatorios automáticos a propietarios con citas próximas.")
    @PostMapping("/recordatorios-citas")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPCIONISTA')")
    public ResponseEntity<Map<String, Object>> enviarRecordatoriosCitas(
            @Parameter(description = "Horas de anticipación") @RequestParam(defaultValue = "24") int horasAnticipacion) {
        return ResponseEntity.ok(clinicaFacade.enviarRecordatoriosCitasProximas(horasAnticipacion));
    }

    @Operation(summary = "Notificar stock bajo",
               description = "Envía notificaciones sobre insumos con stock bajo a administradores.")
    @PostMapping("/stock-bajo")
    @PreAuthorize("hasAnyRole('ADMIN', 'AUXILIAR')")
    public ResponseEntity<Map<String, Object>> notificarStockBajo() {
        return ResponseEntity.ok(clinicaFacade.notificarStockBajo());
    }
}
