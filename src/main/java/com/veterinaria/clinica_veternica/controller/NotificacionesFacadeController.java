package com.veterinaria.clinica_veternica.controller;

import com.veterinaria.clinica_veternica.dto.response.facade.ResultadoNotificacionDTO;
import com.veterinaria.clinica_veternica.patterns.structural.facade.NotificacionesFacadeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Facade Controller especializado para Notificaciones Masivas.
 * Implementa el patrón Facade para simplificar envío masivo de notificaciones
 * que requieren coordinación de múltiples servicios.
 *
 * @author Clínica Veterinaria Team
 * @version 3.0 - Refactorizado para eliminar God Object ClinicaFacade
 * @since 2025-11-18
 */
@RestController
@RequestMapping("/api/facade/notificaciones")
@RequiredArgsConstructor
@Tag(name = "Facade - Notificaciones", description = "Notificaciones masivas y automatizadas (Facade Pattern)")
public class NotificacionesFacadeController {

    private final NotificacionesFacadeService notificacionesFacadeService;

    @Operation(summary = "Enviar recordatorios de citas",
               description = "Envía recordatorios automáticos a propietarios con citas próximas.")
    @PostMapping("/recordatorios-citas")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPCIONISTA')")
    public ResponseEntity<ResultadoNotificacionDTO> enviarRecordatoriosCitas(
            @Parameter(description = "Horas de anticipación") @RequestParam(defaultValue = "24") int horasAnticipacion) {
        return ResponseEntity.ok(notificacionesFacadeService.enviarRecordatoriosCitasProximas(horasAnticipacion));
    }

    @Operation(summary = "Notificar stock bajo",
               description = "Envía notificaciones sobre insumos con stock bajo a administradores.")
    @PostMapping("/stock-bajo")
    @PreAuthorize("hasAnyRole('ADMIN', 'AUXILIAR')")
    public ResponseEntity<ResultadoNotificacionDTO> notificarStockBajo() {
        return ResponseEntity.ok(notificacionesFacadeService.notificarStockBajo());
    }
}
