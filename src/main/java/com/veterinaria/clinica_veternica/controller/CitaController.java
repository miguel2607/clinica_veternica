package com.veterinaria.clinica_veternica.controller;

import com.veterinaria.clinica_veternica.dto.request.agenda.CitaRequestDTO;
import com.veterinaria.clinica_veternica.dto.response.agenda.CitaResponseDTO;
import com.veterinaria.clinica_veternica.service.interfaces.ICitaService;
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

import java.time.LocalDateTime;
import java.util.List;

/**
 * Controller REST para gestión de Citas.
 * Utiliza múltiples patrones de diseño: Mediator, Chain, Command, State, Template Method.
 *
 * @author Clínica Veterinaria Team
 * @version 1.0
 * @since 2025-11-06
 */
@RestController
@RequestMapping("/api/citas")
@RequiredArgsConstructor
@Tag(name = "Citas", description = "API para gestión de citas veterinarias")
public class CitaController {

    private final ICitaService citaService;

    @Operation(summary = "Crear nueva cita", description = "Crea una nueva cita usando Builder pattern y valida con Chain of Responsibility")
    @PreAuthorize("hasAnyRole('ADMIN', 'VETERINARIO', 'RECEPCIONISTA', 'PROPIETARIO')")
    @PostMapping
    public ResponseEntity<CitaResponseDTO> crear(@Valid @RequestBody CitaRequestDTO requestDTO) {
        return new ResponseEntity<>(citaService.crear(requestDTO), HttpStatus.CREATED);
    }

    @Operation(summary = "Actualizar cita", description = "Actualiza una cita existente si está en estado modificable")
    @PreAuthorize("hasAnyRole('ADMIN', 'VETERINARIO', 'RECEPCIONISTA', 'PROPIETARIO')")
    @PutMapping("/{id}")
    public ResponseEntity<CitaResponseDTO> actualizar(
            @Parameter(description = "ID de la cita") @PathVariable Long id,
            @Valid @RequestBody CitaRequestDTO requestDTO) {
        return ResponseEntity.ok(citaService.actualizar(id, requestDTO));
    }

    @Operation(summary = "Buscar cita por ID")
    @GetMapping("/{id}")
    public ResponseEntity<CitaResponseDTO> buscarPorId(
            @Parameter(description = "ID de la cita") @PathVariable Long id) {
        return ResponseEntity.ok(citaService.buscarPorId(id));
    }

    @Operation(summary = "Listar todas las citas")
    @GetMapping
    public ResponseEntity<List<CitaResponseDTO>> listarTodos() {
        return ResponseEntity.ok(citaService.listarTodos());
    }

    @Operation(summary = "Listar citas por veterinario")
    @GetMapping("/veterinario/{idVeterinario}")
    public ResponseEntity<List<CitaResponseDTO>> listarPorVeterinario(
            @Parameter(description = "ID del veterinario") @PathVariable Long idVeterinario) {
        return ResponseEntity.ok(citaService.listarPorVeterinario(idVeterinario));
    }

    @Operation(summary = "Listar mis citas (veterinario autenticado)")
    @PreAuthorize("hasRole('VETERINARIO')")
    @GetMapping("/mis-citas")
    public ResponseEntity<List<CitaResponseDTO>> listarMisCitas() {
        return ResponseEntity.ok(citaService.listarMisCitas());
    }

    @Operation(summary = "Listar citas por mascota")
    @GetMapping("/mascota/{idMascota}")
    public ResponseEntity<List<CitaResponseDTO>> listarPorMascota(
            @Parameter(description = "ID de la mascota") @PathVariable Long idMascota) {
        return ResponseEntity.ok(citaService.listarPorMascota(idMascota));
    }

    @Operation(summary = "Listar citas programadas")
    @GetMapping("/programadas")
    public ResponseEntity<List<CitaResponseDTO>> listarProgramadas() {
        return ResponseEntity.ok(citaService.listarProgramadas());
    }

    @Operation(summary = "Listar citas en rango de fechas")
    @GetMapping("/rango")
    public ResponseEntity<List<CitaResponseDTO>> listarPorRangoFechas(
            @Parameter(description = "Fecha de inicio") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @Parameter(description = "Fecha de fin") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fin) {
        return ResponseEntity.ok(citaService.listarPorRangoFechas(inicio, fin));
    }

    @Operation(summary = "Confirmar cita", description = "Confirma una cita usando Mediator pattern")
    @PreAuthorize("hasAnyRole('ADMIN', 'VETERINARIO', 'RECEPCIONISTA', 'PROPIETARIO')")
    @PutMapping("/{id}/confirmar")
    public ResponseEntity<CitaResponseDTO> confirmar(
            @Parameter(description = "ID de la cita") @PathVariable Long id) {
        return ResponseEntity.ok(citaService.confirmar(id));
    }

    @Operation(summary = "Cancelar cita", description = "Cancela una cita usando Command pattern")
    @PreAuthorize("hasAnyRole('ADMIN', 'VETERINARIO', 'RECEPCIONISTA', 'PROPIETARIO')")
    @PutMapping("/{id}/cancelar")
    public ResponseEntity<CitaResponseDTO> cancelar(
            @Parameter(description = "ID de la cita") @PathVariable Long id,
            @Parameter(description = "Motivo de cancelación") @RequestParam String motivo,
            @Parameter(description = "Usuario que cancela") @RequestParam(required = false, defaultValue = "Sistema") String usuario) {
        return ResponseEntity.ok(citaService.cancelar(id, motivo, usuario));
    }

    @Operation(summary = "Marcar cita como atendida", description = "Marca una cita como atendida usando Template Method pattern")
    @PreAuthorize("hasAnyRole('ADMIN', 'VETERINARIO')")
    @PutMapping("/{id}/atender")
    public ResponseEntity<CitaResponseDTO> marcarComoAtendida(
            @Parameter(description = "ID de la cita") @PathVariable Long id) {
        return ResponseEntity.ok(citaService.marcarComoAtendida(id));
    }

    @Operation(summary = "Iniciar atención de cita", description = "Inicia el proceso de atención usando State pattern")
    @PutMapping("/{id}/iniciar-atencion")
    public ResponseEntity<CitaResponseDTO> iniciarAtencion(
            @Parameter(description = "ID de la cita") @PathVariable Long id) {
        return ResponseEntity.ok(citaService.iniciarAtencion(id));
    }

    @Operation(summary = "Finalizar atención de cita", description = "Finaliza el proceso de atención usando State pattern")
    @PutMapping("/{id}/finalizar-atencion")
    public ResponseEntity<CitaResponseDTO> finalizarAtencion(
            @Parameter(description = "ID de la cita") @PathVariable Long id) {
        return ResponseEntity.ok(citaService.finalizarAtencion(id));
    }

    @Operation(summary = "Listar citas para recordatorio", description = "Obtiene citas que requieren recordatorio")
    @GetMapping("/recordatorios")
    public ResponseEntity<List<CitaResponseDTO>> listarParaRecordatorio(
            @Parameter(description = "Fecha/hora actual") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime ahora,
            @Parameter(description = "Fecha/hora límite") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime limite) {
        return ResponseEntity.ok(citaService.listarParaRecordatorio(ahora, limite));
    }
}

