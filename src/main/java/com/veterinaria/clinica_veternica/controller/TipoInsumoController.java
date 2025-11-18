package com.veterinaria.clinica_veternica.controller;

import com.veterinaria.clinica_veternica.dto.request.inventario.TipoInsumoRequestDTO;
import com.veterinaria.clinica_veternica.dto.response.inventario.TipoInsumoResponseDTO;
import com.veterinaria.clinica_veternica.service.interfaces.ITipoInsumoService;
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
 * Controller REST para gestión de Tipos de Insumo.
 *
 * @author Clínica Veterinaria Team
 * @version 1.0
 * @since 2025-01-XX
 */
@RestController
@RequestMapping("/api/inventario/tipos-insumo")
@RequiredArgsConstructor
@Tag(name = "Tipos de Insumo", description = "API para gestión de tipos de insumo")
public class TipoInsumoController {

    private final ITipoInsumoService tipoInsumoService;

    @Operation(summary = "Crear nuevo tipo de insumo")
    @PreAuthorize("hasAnyRole('ADMIN', 'VETERINARIO', 'AUXILIAR')")
    @PostMapping
    public ResponseEntity<TipoInsumoResponseDTO> crear(@Valid @RequestBody TipoInsumoRequestDTO requestDTO) {
        return new ResponseEntity<>(tipoInsumoService.crear(requestDTO), HttpStatus.CREATED);
    }

    @Operation(summary = "Actualizar tipo de insumo")
    @PreAuthorize("hasAnyRole('ADMIN', 'VETERINARIO', 'AUXILIAR')")
    @PutMapping("/{id}")
    public ResponseEntity<TipoInsumoResponseDTO> actualizar(
            @Parameter(description = "ID del tipo de insumo") @PathVariable Long id,
            @Valid @RequestBody TipoInsumoRequestDTO requestDTO) {
        return ResponseEntity.ok(tipoInsumoService.actualizar(id, requestDTO));
    }

    @Operation(summary = "Buscar tipo de insumo por ID")
    @GetMapping("/{id}")
    public ResponseEntity<TipoInsumoResponseDTO> buscarPorId(
            @Parameter(description = "ID del tipo de insumo") @PathVariable Long id) {
        return ResponseEntity.ok(tipoInsumoService.buscarPorId(id));
    }

    @Operation(summary = "Buscar tipo de insumo por nombre")
    @GetMapping("/nombre/{nombre}")
    public ResponseEntity<TipoInsumoResponseDTO> buscarPorNombre(
            @Parameter(description = "Nombre del tipo de insumo") @PathVariable String nombre) {
        return ResponseEntity.ok(tipoInsumoService.buscarPorNombre(nombre));
    }

    @Operation(summary = "Listar todos los tipos de insumo")
    @GetMapping
    public ResponseEntity<List<TipoInsumoResponseDTO>> listarTodos() {
        return ResponseEntity.ok(tipoInsumoService.listarTodos());
    }

    @Operation(summary = "Listar tipos de insumo activos")
    @GetMapping("/activos")
    public ResponseEntity<List<TipoInsumoResponseDTO>> listarActivos() {
        return ResponseEntity.ok(tipoInsumoService.listarActivos());
    }

    @Operation(summary = "Eliminar tipo de insumo")
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(
            @Parameter(description = "ID del tipo de insumo") @PathVariable Long id) {
        tipoInsumoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Activar tipo de insumo")
    @PreAuthorize("hasAnyRole('ADMIN', 'VETERINARIO', 'AUXILIAR')")
    @PatchMapping("/{id}/activar")
    public ResponseEntity<TipoInsumoResponseDTO> activar(
            @Parameter(description = "ID del tipo de insumo") @PathVariable Long id) {
        return ResponseEntity.ok(tipoInsumoService.activar(id));
    }

    @Operation(summary = "Desactivar tipo de insumo")
    @PreAuthorize("hasAnyRole('ADMIN', 'VETERINARIO', 'AUXILIAR')")
    @PatchMapping("/{id}/desactivar")
    public ResponseEntity<TipoInsumoResponseDTO> desactivar(
            @Parameter(description = "ID del tipo de insumo") @PathVariable Long id) {
        return ResponseEntity.ok(tipoInsumoService.desactivar(id));
    }
}

