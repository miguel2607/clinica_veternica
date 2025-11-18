package com.veterinaria.clinica_veternica.controller;

import com.veterinaria.clinica_veternica.dto.request.usuario.AuxiliarVeterinarioRequestDTO;
import com.veterinaria.clinica_veternica.dto.response.usuario.AuxiliarVeterinarioResponseDTO;
import com.veterinaria.clinica_veternica.service.interfaces.IAuxiliarVeterinarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/auxiliares-veterinarios")
@RequiredArgsConstructor
@Tag(name = "Auxiliares Veterinarios", description = "API para gestión de auxiliares veterinarios")
public class AuxiliarVeterinarioController {

    private final IAuxiliarVeterinarioService auxiliarVeterinarioService;

    @Operation(summary = "Crear nuevo auxiliar veterinario")
    @PostMapping
    public ResponseEntity<AuxiliarVeterinarioResponseDTO> crear(@Valid @RequestBody AuxiliarVeterinarioRequestDTO requestDTO) {
        return new ResponseEntity<>(auxiliarVeterinarioService.crear(requestDTO), HttpStatus.CREATED);
    }

    @Operation(summary = "Actualizar auxiliar veterinario")
    @PutMapping("/{id}")
    public ResponseEntity<AuxiliarVeterinarioResponseDTO> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody AuxiliarVeterinarioRequestDTO requestDTO) {
        return ResponseEntity.ok(auxiliarVeterinarioService.actualizar(id, requestDTO));
    }

    @Operation(summary = "Buscar auxiliar veterinario por ID")
    @GetMapping("/{id}")
    public ResponseEntity<AuxiliarVeterinarioResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(auxiliarVeterinarioService.buscarPorId(id));
    }

    @Operation(summary = "Listar todos los auxiliares veterinarios")
    @GetMapping
    public ResponseEntity<List<AuxiliarVeterinarioResponseDTO>> listarTodos() {
        return ResponseEntity.ok(auxiliarVeterinarioService.listarTodos());
    }

    @Operation(summary = "Listar auxiliares veterinarios activos")
    @GetMapping("/activos")
    public ResponseEntity<List<AuxiliarVeterinarioResponseDTO>> listarActivos() {
        return ResponseEntity.ok(auxiliarVeterinarioService.listarActivos());
    }

    @Operation(summary = "Buscar auxiliares veterinarios por nombre")
    @GetMapping("/buscar")
    public ResponseEntity<List<AuxiliarVeterinarioResponseDTO>> buscarPorNombre(@RequestParam String nombre) {
        return ResponseEntity.ok(auxiliarVeterinarioService.buscarPorNombre(nombre));
    }

    @Operation(summary = "Eliminar auxiliar veterinario")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        auxiliarVeterinarioService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Activar auxiliar veterinario")
    @PatchMapping("/{id}/activar")
    public ResponseEntity<AuxiliarVeterinarioResponseDTO> activar(@PathVariable Long id) {
        return ResponseEntity.ok(auxiliarVeterinarioService.activar(id));
    }
}

