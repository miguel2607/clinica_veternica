package com.veterinaria.clinica_veternica.controller;

import com.veterinaria.clinica_veternica.dto.request.clinico.VacunacionRequestDTO;
import com.veterinaria.clinica_veternica.dto.response.clinico.VacunacionResponseDTO;
import com.veterinaria.clinica_veternica.service.interfaces.IVacunacionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller REST para gestión de Vacunaciones.
 * Maneja exclusivamente operaciones CRUD de vacunaciones.
 *
 * <p><b>Responsabilidad Única:</b></p>
 * <ul>
 *   <li>✅ <b>Una sola responsabilidad</b>: Gestión de vacunaciones</li>
 *   <li>✅ Cumple estrictamente <b>SRP (Single Responsibility Principle)</b></li>
 *   <li>✅ Controlador independiente, desacoplado</li>
 * </ul>
 *
 * @author Clínica Veterinaria Team
 * @version 2.0
 * @since 2025-11-17
 */
@RestController
@RequestMapping("/api/vacunaciones")
@RequiredArgsConstructor
@Tag(name = "Vacunaciones", description = "API para gestión de vacunaciones")
public class VacunacionController {

    private final IVacunacionService vacunacionService;

    @Operation(summary = "Crear vacunación",
               description = "Crea una nueva vacunación para una historia clínica específica")
    @PreAuthorize("hasAnyRole('ADMIN', 'VETERINARIO', 'AUXILIAR')")
    @PostMapping
    public ResponseEntity<VacunacionResponseDTO> crear(
            @Parameter(description = "ID de la historia clínica") @RequestParam Long idHistoriaClinica,
            @Valid @RequestBody VacunacionRequestDTO requestDTO) {
        return new ResponseEntity<>(vacunacionService.crear(idHistoriaClinica, requestDTO), HttpStatus.CREATED);
    }

    @Operation(summary = "Listar todas las vacunaciones",
               description = "Lista todas las vacunaciones del sistema")
    @PreAuthorize("hasAnyRole('ADMIN', 'VETERINARIO')")
    @GetMapping
    public ResponseEntity<List<VacunacionResponseDTO>> listarTodas() {
        return ResponseEntity.ok(vacunacionService.listarTodas());
    }

    @Operation(summary = "Listar vacunaciones por historia clínica",
               description = "Lista todas las vacunaciones de una historia clínica específica")
    @PreAuthorize("hasAnyRole('ADMIN', 'VETERINARIO', 'PROPIETARIO', 'AUXILIAR')")
    @GetMapping("/historia-clinica/{idHistoriaClinica}")
    public ResponseEntity<List<VacunacionResponseDTO>> listarPorHistoriaClinica(
            @Parameter(description = "ID de la historia clínica") @PathVariable Long idHistoriaClinica) {
        return ResponseEntity.ok(vacunacionService.listarPorHistoriaClinica(idHistoriaClinica));
    }
}
