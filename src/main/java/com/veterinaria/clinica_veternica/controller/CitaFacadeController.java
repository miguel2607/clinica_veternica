package com.veterinaria.clinica_veternica.controller;

import com.veterinaria.clinica_veternica.dto.request.agenda.CitaRequestDTO;
import com.veterinaria.clinica_veternica.dto.request.clinico.EvolucionClinicaRequestDTO;
import com.veterinaria.clinica_veternica.dto.response.facade.CalendarioCitasDTO;
import com.veterinaria.clinica_veternica.dto.response.facade.ResultadoAtencionCompletaDTO;
import com.veterinaria.clinica_veternica.dto.response.facade.ResultadoCitaConNotificacionDTO;
import com.veterinaria.clinica_veternica.patterns.structural.facade.CitaFacadeService;
import com.veterinaria.clinica_veternica.patterns.structural.facade.OperacionesFacadeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

/**
 * Facade Controller especializado para operaciones complejas de Citas.
 * Implementa el patrón Facade para simplificar operaciones que requieren
 * coordinación de múltiples servicios relacionados con citas.
 *
 * @author Clínica Veterinaria Team
 * @version 4.0 - Refactorizado para eliminar God Object ClinicaFacade
 * @since 2025-11-18
 */
@RestController
@RequestMapping("/api/facade/citas")
@RequiredArgsConstructor
@Tag(name = "Facade - Citas", description = "Operaciones complejas de citas (Facade Pattern)")
public class CitaFacadeController {

    private final CitaFacadeService citaFacadeService;
    private final OperacionesFacadeService operacionesFacadeService;

    @Operation(summary = "Crear cita con notificación automática",
               description = "Crea una cita y envía notificación automáticamente al propietario.")
    @PostMapping("/crear-con-notificacion")
    @PreAuthorize("hasAnyRole('ADMIN', 'VETERINARIO', 'RECEPCIONISTA', 'PROPIETARIO')")
    public ResponseEntity<ResultadoCitaConNotificacionDTO> crearCitaConNotificacion(
            @Valid @RequestBody CitaRequestDTO requestDTO) {
        var cita = citaFacadeService.crearCitaConNotificacion(requestDTO);
        var resultado = ResultadoCitaConNotificacionDTO.builder()
                .cita(cita)
                .notificacion(null)
                .mensaje("Cita creada exitosamente con notificación")
                .build();
        return new ResponseEntity<>(resultado, HttpStatus.CREATED);
    }

    @Operation(summary = "Procesar atención completa",
               description = "Marca cita como atendida y crea evolución clínica en una sola operación.")
    @PostMapping("/{idCita}/atencion-completa")
    @PreAuthorize("hasAnyRole('ADMIN', 'VETERINARIO')")
    public ResponseEntity<ResultadoAtencionCompletaDTO> procesarAtencionCompleta(
            @Parameter(description = "ID de la cita") @PathVariable Long idCita,
            @Valid @RequestBody EvolucionClinicaRequestDTO evolucionRequestDTO) {
        return ResponseEntity.ok(operacionesFacadeService.procesarAtencionCompleta(idCita, evolucionRequestDTO));
    }

    @Operation(summary = "Cancelar cita con notificación",
               description = "Cancela una cita y envía notificación automática al propietario.")
    @PutMapping("/{idCita}/cancelar-con-notificacion")
    @PreAuthorize("hasAnyRole('ADMIN', 'VETERINARIO', 'RECEPCIONISTA')")
    public ResponseEntity<ResultadoCitaConNotificacionDTO> cancelarCitaConNotificacion(
            @Parameter(description = "ID de la cita") @PathVariable Long idCita,
            @Parameter(description = "Motivo de cancelación") @RequestParam String motivo,
            @Parameter(description = "Usuario que cancela") @RequestParam(required = false, defaultValue = "Sistema") String usuario) {
        return ResponseEntity.ok(citaFacadeService.cancelarCitaConNotificacion(idCita, motivo, usuario));
    }

    @Operation(summary = "Reprogramar cita con notificación",
               description = "Reprograma una cita y envía notificación automática al propietario.")
    @PutMapping("/{idCita}/reprogramar")
    @PreAuthorize("hasAnyRole('ADMIN', 'VETERINARIO', 'RECEPCIONISTA')")
    public ResponseEntity<ResultadoCitaConNotificacionDTO> reprogramarCitaConNotificacion(
            @Parameter(description = "ID de la cita") @PathVariable Long idCita,
            @Valid @RequestBody CitaRequestDTO nuevaCitaDTO) {
        return ResponseEntity.ok(citaFacadeService.reprogramarCitaConNotificacion(idCita, nuevaCitaDTO));
    }

    @Operation(summary = "Obtener calendario de citas",
               description = "Obtiene citas de un día específico con información agrupada por estado.")
    @GetMapping("/calendario")
    @PreAuthorize("hasAnyRole('ADMIN', 'VETERINARIO', 'RECEPCIONISTA')")
    public ResponseEntity<CalendarioCitasDTO> obtenerCalendarioCitas(
            @Parameter(description = "Fecha para el calendario")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        return ResponseEntity.ok(citaFacadeService.obtenerCalendarioCitas(fecha));
    }
}
