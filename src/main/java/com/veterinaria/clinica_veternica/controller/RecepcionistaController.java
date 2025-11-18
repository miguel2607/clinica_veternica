package com.veterinaria.clinica_veternica.controller;

import com.veterinaria.clinica_veternica.dto.request.usuario.RecepcionistaRequestDTO;
import com.veterinaria.clinica_veternica.dto.response.usuario.RecepcionistaResponseDTO;
import com.veterinaria.clinica_veternica.service.interfaces.IRecepcionistaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recepcionistas")
@RequiredArgsConstructor
@Tag(name = "Recepcionistas", description = "API para gestión de recepcionistas")
public class RecepcionistaController {

    private final IRecepcionistaService recepcionistaService;

    @Operation(summary = "Crear nuevo recepcionista")
    @PostMapping
    public ResponseEntity<RecepcionistaResponseDTO> crear(@Valid @RequestBody RecepcionistaRequestDTO requestDTO) {
        return new ResponseEntity<>(recepcionistaService.crear(requestDTO), HttpStatus.CREATED);
    }

    @Operation(summary = "Actualizar recepcionista")
    @PutMapping("/{id}")
    public ResponseEntity<RecepcionistaResponseDTO> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody RecepcionistaRequestDTO requestDTO) {
        return ResponseEntity.ok(recepcionistaService.actualizar(id, requestDTO));
    }

    @Operation(summary = "Buscar recepcionista por ID")
    @GetMapping("/{id}")
    public ResponseEntity<RecepcionistaResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(recepcionistaService.buscarPorId(id));
    }

    @Operation(summary = "Listar todos los recepcionistas")
    @GetMapping
    public ResponseEntity<List<RecepcionistaResponseDTO>> listarTodos() {
        return ResponseEntity.ok(recepcionistaService.listarTodos());
    }

    @Operation(summary = "Listar recepcionistas activos")
    @GetMapping("/activos")
    public ResponseEntity<List<RecepcionistaResponseDTO>> listarActivos() {
        return ResponseEntity.ok(recepcionistaService.listarActivos());
    }

    @Operation(summary = "Buscar recepcionistas por nombre")
    @GetMapping("/buscar")
    public ResponseEntity<List<RecepcionistaResponseDTO>> buscarPorNombre(@RequestParam String nombre) {
        return ResponseEntity.ok(recepcionistaService.buscarPorNombre(nombre));
    }

    @Operation(summary = "Eliminar recepcionista")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        recepcionistaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Activar recepcionista")
    @PatchMapping("/{id}/activar")
    public ResponseEntity<RecepcionistaResponseDTO> activar(@PathVariable Long id) {
        return ResponseEntity.ok(recepcionistaService.activar(id));
    }
}

