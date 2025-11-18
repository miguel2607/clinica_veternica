package com.veterinaria.clinica_veternica.controller;

import com.veterinaria.clinica_veternica.dto.request.usuario.AdministradorRequestDTO;
import com.veterinaria.clinica_veternica.dto.response.usuario.AdministradorResponseDTO;
import com.veterinaria.clinica_veternica.service.interfaces.IAdministradorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/administradores")
@RequiredArgsConstructor
@Tag(name = "Administradores", description = "API para gestión de administradores")
public class AdministradorController {

    private final IAdministradorService administradorService;

    @Operation(summary = "Crear nuevo administrador")
    @PostMapping
    public ResponseEntity<AdministradorResponseDTO> crear(@Valid @RequestBody AdministradorRequestDTO requestDTO) {
        return new ResponseEntity<>(administradorService.crear(requestDTO), HttpStatus.CREATED);
    }

    @Operation(summary = "Actualizar administrador")
    @PutMapping("/{id}")
    public ResponseEntity<AdministradorResponseDTO> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody AdministradorRequestDTO requestDTO) {
        return ResponseEntity.ok(administradorService.actualizar(id, requestDTO));
    }

    @Operation(summary = "Buscar administrador por ID")
    @GetMapping("/{id}")
    public ResponseEntity<AdministradorResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(administradorService.buscarPorId(id));
    }

    @Operation(summary = "Listar todos los administradores")
    @GetMapping
    public ResponseEntity<List<AdministradorResponseDTO>> listarTodos() {
        return ResponseEntity.ok(administradorService.listarTodos());
    }

    @Operation(summary = "Listar administradores activos")
    @GetMapping("/activos")
    public ResponseEntity<List<AdministradorResponseDTO>> listarActivos() {
        return ResponseEntity.ok(administradorService.listarActivos());
    }

    @Operation(summary = "Buscar administradores por nombre")
    @GetMapping("/buscar")
    public ResponseEntity<List<AdministradorResponseDTO>> buscarPorNombre(@RequestParam String nombre) {
        return ResponseEntity.ok(administradorService.buscarPorNombre(nombre));
    }

    @Operation(summary = "Eliminar administrador")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        administradorService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Activar administrador")
    @PatchMapping("/{id}/activar")
    public ResponseEntity<AdministradorResponseDTO> activar(@PathVariable Long id) {
        return ResponseEntity.ok(administradorService.activar(id));
    }
}

