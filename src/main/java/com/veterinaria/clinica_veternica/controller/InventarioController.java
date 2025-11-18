package com.veterinaria.clinica_veternica.controller;

import com.veterinaria.clinica_veternica.dto.response.inventario.InventarioResponseDTO;
import com.veterinaria.clinica_veternica.service.interfaces.IInventarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller REST para gestión de Inventario.
 * Utiliza el patrón Proxy para control de acceso y auditoría.
 *
 * @author Clínica Veterinaria Team
 * @version 1.0
 * @since 2025-11-06
 */
@RestController
@RequestMapping("/api/inventario")
@RequiredArgsConstructor
@Tag(name = "Inventario", description = "API para gestión de inventario (usa Proxy pattern para control de acceso)")
public class InventarioController {

    private final IInventarioService inventarioService;

    @Operation(summary = "Buscar inventario por ID")
    @GetMapping("/{id}")
    public ResponseEntity<InventarioResponseDTO> buscarPorId(
            @Parameter(description = "ID del inventario") @PathVariable Long id) {
        return ResponseEntity.ok(inventarioService.buscarPorId(id));
    }

    @Operation(summary = "Buscar inventario por insumo")
    @GetMapping("/insumo/{idInsumo}")
    public ResponseEntity<InventarioResponseDTO> buscarPorInsumo(
            @Parameter(description = "ID del insumo") @PathVariable Long idInsumo) {
        return ResponseEntity.ok(inventarioService.buscarPorInsumo(idInsumo));
    }

    @Operation(summary = "Listar todo el inventario")
    @GetMapping
    public ResponseEntity<List<InventarioResponseDTO>> listarTodos() {
        return ResponseEntity.ok(inventarioService.listarTodos());
    }

    @Operation(summary = "Listar inventario con stock bajo", description = "Obtiene insumos que requieren reorden")
    @GetMapping("/stock-bajo")
    public ResponseEntity<List<InventarioResponseDTO>> listarConStockBajo() {
        return ResponseEntity.ok(inventarioService.listarConStockBajo());
    }

    @Operation(summary = "Listar inventario agotado")
    @GetMapping("/agotados")
    public ResponseEntity<List<InventarioResponseDTO>> listarAgotados() {
        return ResponseEntity.ok(inventarioService.listarAgotados());
    }

    @Operation(summary = "Listar inventario ordenado por valor")
    @GetMapping("/ordenados-por-valor")
    public ResponseEntity<List<InventarioResponseDTO>> listarOrdenadosPorValor() {
        return ResponseEntity.ok(inventarioService.listarOrdenadosPorValor());
    }

}

