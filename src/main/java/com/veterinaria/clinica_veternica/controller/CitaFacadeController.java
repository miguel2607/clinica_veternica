package com.veterinaria.clinica_veternica.controller;

import com.veterinaria.clinica_veternica.dto.request.agenda.CitaRequestDTO;
import com.veterinaria.clinica_veternica.dto.request.clinico.EvolucionClinicaRequestDTO;
import com.veterinaria.clinica_veternica.patterns.structural.facade.ClinicaFacade;
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
import java.util.Map;

/**
 * Facade Controller especializado para operaciones complejas de Citas.
 * Implementa el patrón Facade para simplificar operaciones que requieren
 * coordinación de múltiples servicios relacionados con citas.
 *
 * @author Clínica Veterinaria Team
 * @version 2.0
 * @since 2025-11-17
 */
@RestController
@RequestMapping("/api/facade/citas")
@RequiredArgsConstructor
@Tag(name = "Facade - Citas", description = "Operaciones complejas de citas (Facade Pattern)")
public class CitaFacadeController {

    private final ClinicaFacade clinicaFacade;

    @Operation(summary = "Crear cita con notificación automática",
               description = "Crea una cita y envía notificación automáticamente al propietario.")
    @PostMapping("/crear-con-notificacion")
    @PreAuthorize("hasAnyRole('ADMIN', 'VETERINARIO', 'RECEPCIONISTA', 'PROPIETARIO')")
    public ResponseEntity<Map<String, Object>> crearCitaConNotificacion(
            @Valid @RequestBody CitaRequestDTO requestDTO) {
        var cita = clinicaFacade.crearCitaConNotificacion(requestDTO);
        return new ResponseEntity<>(Map.of("cita", cita, "notificacionEnviada", true), HttpStatus.CREATED);
    }

    @Operation(summary = "Procesar atención completa",
               description = "Marca cita como atendida y crea evolución clínica en una sola operación.")
    @PostMapping("/{idCita}/atencion-completa")
    @PreAuthorize("hasAnyRole('ADMIN', 'VETERINARIO')")
    public ResponseEntity<Map<String, Object>> procesarAtencionCompleta(
            @Parameter(description = "ID de la cita") @PathVariable Long idCita,
            @Valid @RequestBody EvolucionClinicaRequestDTO evolucionRequestDTO) {
        return ResponseEntity.ok(clinicaFacade.procesarAtencionCompleta(idCita, evolucionRequestDTO));
    }

    @Operation(summary = "Cancelar cita con notificación",
               description = "Cancela una cita y envía notificación automática al propietario.")
    @PutMapping("/{idCita}/cancelar-con-notificacion")
    @PreAuthorize("hasAnyRole('ADMIN', 'VETERINARIO', 'RECEPCIONISTA')")
    public ResponseEntity<Map<String, Object>> cancelarCitaConNotificacion(
            @Parameter(description = "ID de la cita") @PathVariable Long idCita,
            @Parameter(description = "Motivo de cancelación") @RequestParam String motivo,
            @Parameter(description = "Usuario que cancela") @RequestParam(required = false, defaultValue = "Sistema") String usuario) {
        return ResponseEntity.ok(clinicaFacade.cancelarCitaConNotificacion(idCita, motivo, usuario));
    }

    @Operation(summary = "Reprogramar cita con notificación",
               description = "Reprograma una cita y envía notificación automática al propietario.")
    @PutMapping("/{idCita}/reprogramar")
    @PreAuthorize("hasAnyRole('ADMIN', 'VETERINARIO', 'RECEPCIONISTA')")
    public ResponseEntity<Map<String, Object>> reprogramarCitaConNotificacion(
            @Parameter(description = "ID de la cita") @PathVariable Long idCita,
            @Valid @RequestBody CitaRequestDTO nuevaCitaDTO) {
        return ResponseEntity.ok(clinicaFacade.reprogramarCitaConNotificacion(idCita, nuevaCitaDTO));
    }

    @Operation(summary = "Obtener calendario de citas",
               description = "Obtiene citas de un día específico con información agrupada por estado.")
    @GetMapping("/calendario")
    @PreAuthorize("hasAnyRole('ADMIN', 'VETERINARIO', 'RECEPCIONISTA')")
    public ResponseEntity<Map<String, Object>> obtenerCalendarioCitas(
            @Parameter(description = "Fecha para el calendario")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        return ResponseEntity.ok(clinicaFacade.obtenerCalendarioCitas(fecha));
    }
}
