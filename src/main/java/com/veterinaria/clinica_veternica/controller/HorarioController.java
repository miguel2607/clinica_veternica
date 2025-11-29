package com.veterinaria.clinica_veternica.controller;

import com.veterinaria.clinica_veternica.dto.request.agenda.HorarioRequestDTO;
import com.veterinaria.clinica_veternica.dto.response.agenda.DisponibilidadVeterinarioDTO;
import com.veterinaria.clinica_veternica.dto.response.agenda.HorarioResponseDTO;
import com.veterinaria.clinica_veternica.service.interfaces.IHorarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

/**
 * Controller REST para gestión de Horarios de Veterinarios.
 *
 * @author Clínica Veterinaria Team
 * @version 1.0
 * @since 2025-11-06
 */
@RestController
@RequestMapping("/api/horarios")
@RequiredArgsConstructor
@Tag(name = "Horarios", description = "API para gestión de horarios de disponibilidad de veterinarios")
public class HorarioController {

    private final IHorarioService horarioService;

    @Operation(summary = "Crear nuevo horario")
    @PostMapping
    public ResponseEntity<HorarioResponseDTO> crear(@Valid @RequestBody HorarioRequestDTO requestDTO) {
        return new ResponseEntity<>(horarioService.crear(requestDTO), HttpStatus.CREATED);
    }

    @Operation(summary = "Actualizar horario")
    @PutMapping("/{id}")
    public ResponseEntity<HorarioResponseDTO> actualizar(
            @Parameter(description = "ID del horario") @PathVariable Long id,
            @Valid @RequestBody HorarioRequestDTO requestDTO) {
        return ResponseEntity.ok(horarioService.actualizar(id, requestDTO));
    }

    @Operation(summary = "Buscar horario por ID")
    @GetMapping("/{id}")
    public ResponseEntity<HorarioResponseDTO> buscarPorId(
            @Parameter(description = "ID del horario") @PathVariable Long id) {
        return ResponseEntity.ok(horarioService.buscarPorId(id));
    }

    @Operation(summary = "Listar todos los horarios")
    @GetMapping
    public ResponseEntity<List<HorarioResponseDTO>> listarTodos() {
        return ResponseEntity.ok(horarioService.listarTodos());
    }

    @Operation(summary = "Listar horarios activos")
    @GetMapping("/activos")
    public ResponseEntity<List<HorarioResponseDTO>> listarActivos() {
        return ResponseEntity.ok(horarioService.listarActivos());
    }

    @Operation(summary = "Listar horarios por veterinario")
    @GetMapping("/veterinario/{idVeterinario}")
    public ResponseEntity<List<HorarioResponseDTO>> listarPorVeterinario(
            @Parameter(description = "ID del veterinario") @PathVariable Long idVeterinario) {
        return ResponseEntity.ok(horarioService.listarPorVeterinario(idVeterinario));
    }

    @Operation(summary = "Listar horarios por día de la semana")
    @GetMapping("/dia/{diaSemana}")
    public ResponseEntity<List<HorarioResponseDTO>> listarPorDiaSemana(
            @Parameter(description = "Día de la semana (MONDAY, TUESDAY, etc.)") @PathVariable DayOfWeek diaSemana) {
        return ResponseEntity.ok(horarioService.listarPorDiaSemana(diaSemana));
    }

    @Operation(summary = "Eliminar horario (desactivar)")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(
            @Parameter(description = "ID del horario") @PathVariable Long id) {
        horarioService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Activar horario")
    @PutMapping("/{id}/activar")
    public ResponseEntity<HorarioResponseDTO> activar(
            @Parameter(description = "ID del horario") @PathVariable Long id) {
        return ResponseEntity.ok(horarioService.activar(id));
    }

    @Operation(summary = "Desactivar horario")
    @PutMapping("/{id}/desactivar")
    public ResponseEntity<HorarioResponseDTO> desactivar(
            @Parameter(description = "ID del horario") @PathVariable Long id) {
        return ResponseEntity.ok(horarioService.desactivar(id));
    }

    @Operation(summary = "Obtener disponibilidad de un veterinario para una fecha específica")
    @GetMapping("/veterinario/{idVeterinario}/disponibilidad")
    public ResponseEntity<DisponibilidadVeterinarioDTO> obtenerDisponibilidad(
            @Parameter(description = "ID del veterinario") @PathVariable Long idVeterinario,
            @Parameter(description = "Fecha a consultar (formato: yyyy-MM-dd)") 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        return ResponseEntity.ok(horarioService.obtenerDisponibilidad(idVeterinario, fecha));
    }
}

