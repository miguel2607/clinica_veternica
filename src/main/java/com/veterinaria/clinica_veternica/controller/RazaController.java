package com.veterinaria.clinica_veternica.controller;

import com.veterinaria.clinica_veternica.dto.request.paciente.RazaRequestDTO;
import com.veterinaria.clinica_veternica.dto.response.paciente.RazaResponseDTO;
import com.veterinaria.clinica_veternica.service.interfaces.IRazaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/razas")
@RequiredArgsConstructor
@Tag(name = "Razas", description = "API para gestión de razas de mascotas")
public class RazaController {

    private final IRazaService razaService;

    @Operation(summary = "Crear nueva raza")
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<RazaResponseDTO> crear(@Valid @RequestBody RazaRequestDTO requestDTO) {
        return new ResponseEntity<>(razaService.crear(requestDTO), HttpStatus.CREATED);
    }

    @Operation(summary = "Actualizar raza")
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<RazaResponseDTO> actualizar(
            @io.swagger.v3.oas.annotations.Parameter(description = "ID de la raza") @PathVariable Long id,
            @Valid @RequestBody RazaRequestDTO requestDTO) {
        return ResponseEntity.ok(razaService.actualizar(id, requestDTO));
    }

    @Operation(summary = "Buscar raza por ID")
    @GetMapping("/{id}")
    public ResponseEntity<RazaResponseDTO> buscarPorId(
            @io.swagger.v3.oas.annotations.Parameter(description = "ID de la raza") @PathVariable Long id) {
        return ResponseEntity.ok(razaService.buscarPorId(id));
    }

    @Operation(summary = "Listar todas las razas")
    @GetMapping
    public ResponseEntity<List<RazaResponseDTO>> listarTodas() {
        return ResponseEntity.ok(razaService.listarTodas());
    }

    @Operation(summary = "Listar razas por especie")
    @GetMapping("/especie/{idEspecie}")
    public ResponseEntity<List<RazaResponseDTO>> listarPorEspecie(
            @io.swagger.v3.oas.annotations.Parameter(description = "ID de la especie") @PathVariable Long idEspecie) {
        return ResponseEntity.ok(razaService.listarPorEspecie(idEspecie));
    }

    @Operation(summary = "Listar razas activas")
    @GetMapping("/activas")
    public ResponseEntity<List<RazaResponseDTO>> listarActivas() {
        return ResponseEntity.ok(razaService.listarActivas());
    }

    @Operation(summary = "Listar razas activas por especie")
    @GetMapping("/activas/especie/{idEspecie}")
    public ResponseEntity<List<RazaResponseDTO>> listarActivasPorEspecie(
            @io.swagger.v3.oas.annotations.Parameter(description = "ID de la especie") @PathVariable Long idEspecie) {
        return ResponseEntity.ok(razaService.listarActivasPorEspecie(idEspecie));
    }

    @Operation(summary = "Buscar razas por nombre")
    @GetMapping("/buscar")
    public ResponseEntity<List<RazaResponseDTO>> buscarPorNombre(
            @io.swagger.v3.oas.annotations.Parameter(description = "Nombre a buscar") @RequestParam String nombre) {
        return ResponseEntity.ok(razaService.buscarPorNombre(nombre));
    }

    @Operation(summary = "Eliminar raza")
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(
            @io.swagger.v3.oas.annotations.Parameter(description = "ID de la raza") @PathVariable Long id) {
        razaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Activar raza")
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/activar")
    public ResponseEntity<RazaResponseDTO> activar(
            @io.swagger.v3.oas.annotations.Parameter(description = "ID de la raza") @PathVariable Long id) {
        return ResponseEntity.ok(razaService.activar(id));
    }
}
