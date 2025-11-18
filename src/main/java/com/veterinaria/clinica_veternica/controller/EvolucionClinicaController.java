package com.veterinaria.clinica_veternica.controller;

import com.veterinaria.clinica_veternica.dto.request.clinico.EvolucionClinicaRequestDTO;
import com.veterinaria.clinica_veternica.dto.response.clinico.EvolucionClinicaResponseDTO;
import com.veterinaria.clinica_veternica.service.interfaces.IEvolucionClinicaService;
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
 * Controller REST para gestión de Evoluciones Clínicas.
 * Maneja exclusivamente operaciones CRUD de evoluciones clínicas.
 *
 * <p><b>Responsabilidad Única:</b></p>
 * <ul>
 *   <li>✅ <b>Una sola responsabilidad</b>: Gestión de evoluciones clínicas</li>
 *   <li>✅ Cumple estrictamente <b>SRP (Single Responsibility Principle)</b></li>
 *   <li>✅ Controlador independiente, desacoplado</li>
 * </ul>
 *
 * @author Clínica Veterinaria Team
 * @version 2.0
 * @since 2025-11-17
 */
@RestController
@RequestMapping("/api/evoluciones-clinicas")
@RequiredArgsConstructor
@Tag(name = "Evoluciones Clínicas", description = "API para gestión de evoluciones clínicas")
public class EvolucionClinicaController {

    private final IEvolucionClinicaService evolucionClinicaService;

    @Operation(summary = "Crear evolución clínica",
               description = "Crea una nueva evolución clínica para una historia clínica específica")
    @PreAuthorize("hasAnyRole('ADMIN', 'VETERINARIO', 'AUXILIAR')")
    @PostMapping
    public ResponseEntity<EvolucionClinicaResponseDTO> crear(
            @Parameter(description = "ID de la historia clínica") @RequestParam Long idHistoriaClinica,
            @Valid @RequestBody EvolucionClinicaRequestDTO requestDTO) {
        return new ResponseEntity<>(evolucionClinicaService.crear(idHistoriaClinica, requestDTO), HttpStatus.CREATED);
    }

    @Operation(summary = "Listar evoluciones por historia clínica",
               description = "Lista todas las evoluciones clínicas de una historia clínica específica")
    @GetMapping("/historia-clinica/{idHistoriaClinica}")
    public ResponseEntity<List<EvolucionClinicaResponseDTO>> listarPorHistoriaClinica(
            @Parameter(description = "ID de la historia clínica") @PathVariable Long idHistoriaClinica) {
        return ResponseEntity.ok(evolucionClinicaService.listarPorHistoriaClinica(idHistoriaClinica));
    }
}
