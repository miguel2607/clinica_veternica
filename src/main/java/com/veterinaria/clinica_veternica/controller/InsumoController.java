package com.veterinaria.clinica_veternica.controller;

import com.veterinaria.clinica_veternica.dto.request.inventario.InsumoRequestDTO;
import com.veterinaria.clinica_veternica.dto.response.inventario.InsumoResponseDTO;
import com.veterinaria.clinica_veternica.service.interfaces.IInsumoService;
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
 * Controller REST para gestión de Insumos.
 *
 * @author Clínica Veterinaria Team
 * @version 1.0
 * @since 2025-01-XX
 */
@RestController
@RequestMapping("/api/inventario/insumos")
@RequiredArgsConstructor
@Tag(name = "Insumos", description = "API para gestión de insumos del inventario")
public class InsumoController {

    private final IInsumoService insumoService;

    @Operation(summary = "Crear nuevo insumo")
    @PreAuthorize("hasAnyRole('ADMIN', 'VETERINARIO', 'AUXILIAR')")
    @PostMapping
    public ResponseEntity<InsumoResponseDTO> crear(@Valid @RequestBody InsumoRequestDTO requestDTO) {
        return new ResponseEntity<>(insumoService.crear(requestDTO), HttpStatus.CREATED);
    }

    @Operation(summary = "Actualizar insumo")
    @PreAuthorize("hasAnyRole('ADMIN', 'VETERINARIO', 'AUXILIAR')")
    @PutMapping("/{id}")
    public ResponseEntity<InsumoResponseDTO> actualizar(
            @Parameter(description = "ID del insumo") @PathVariable Long id,
            @Valid @RequestBody InsumoRequestDTO requestDTO) {
        return ResponseEntity.ok(insumoService.actualizar(id, requestDTO));
    }

    @Operation(summary = "Buscar insumo por ID")
    @GetMapping("/{id}")
    public ResponseEntity<InsumoResponseDTO> buscarPorId(
            @Parameter(description = "ID del insumo") @PathVariable Long id) {
        return ResponseEntity.ok(insumoService.buscarPorId(id));
    }

    @Operation(summary = "Buscar insumo por código")
    @GetMapping("/codigo/{codigo}")
    public ResponseEntity<InsumoResponseDTO> buscarPorCodigo(
            @Parameter(description = "Código del insumo") @PathVariable String codigo) {
        return ResponseEntity.ok(insumoService.buscarPorCodigo(codigo));
    }

    @Operation(summary = "Listar todos los insumos")
    @GetMapping
    public ResponseEntity<List<InsumoResponseDTO>> listarTodos() {
        return ResponseEntity.ok(insumoService.listarTodos());
    }

    @Operation(summary = "Listar insumos activos")
    @GetMapping("/activos")
    public ResponseEntity<List<InsumoResponseDTO>> listarActivos() {
        return ResponseEntity.ok(insumoService.listarActivos());
    }

    @Operation(summary = "Listar insumos con stock bajo")
    @GetMapping("/stock-bajo")
    public ResponseEntity<List<InsumoResponseDTO>> listarConStockBajo() {
        return ResponseEntity.ok(insumoService.listarConStockBajo());
    }

    @Operation(summary = "Listar insumos agotados")
    @GetMapping("/agotados")
    public ResponseEntity<List<InsumoResponseDTO>> listarAgotados() {
        return ResponseEntity.ok(insumoService.listarAgotados());
    }

    @Operation(summary = "Listar insumos por tipo")
    @GetMapping("/tipo/{idTipoInsumo}")
    public ResponseEntity<List<InsumoResponseDTO>> listarPorTipoInsumo(
            @Parameter(description = "ID del tipo de insumo") @PathVariable Long idTipoInsumo) {
        return ResponseEntity.ok(insumoService.listarPorTipoInsumo(idTipoInsumo));
    }

    @Operation(summary = "Buscar insumos por nombre")
    @GetMapping("/buscar")
    public ResponseEntity<List<InsumoResponseDTO>> buscarPorNombre(
            @Parameter(description = "Nombre a buscar") @RequestParam String nombre) {
        return ResponseEntity.ok(insumoService.buscarPorNombre(nombre));
    }

    @Operation(summary = "Eliminar insumo")
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(
            @Parameter(description = "ID del insumo") @PathVariable Long id) {
        insumoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Activar insumo")
    @PreAuthorize("hasAnyRole('ADMIN', 'VETERINARIO', 'AUXILIAR')")
    @PatchMapping("/{id}/activar")
    public ResponseEntity<InsumoResponseDTO> activar(
            @Parameter(description = "ID del insumo") @PathVariable Long id) {
        return ResponseEntity.ok(insumoService.activar(id));
    }

    @Operation(summary = "Desactivar insumo")
    @PreAuthorize("hasAnyRole('ADMIN', 'VETERINARIO', 'AUXILIAR')")
    @PatchMapping("/{id}/desactivar")
    public ResponseEntity<InsumoResponseDTO> desactivar(
            @Parameter(description = "ID del insumo") @PathVariable Long id) {
        return ResponseEntity.ok(insumoService.desactivar(id));
    }
}

