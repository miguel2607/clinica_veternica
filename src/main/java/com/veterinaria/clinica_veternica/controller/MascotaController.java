package com.veterinaria.clinica_veternica.controller;

import com.veterinaria.clinica_veternica.dto.request.paciente.MascotaRequestDTO;
import com.veterinaria.clinica_veternica.dto.response.paciente.MascotaResponseDTO;
import com.veterinaria.clinica_veternica.service.interfaces.IMascotaService;
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
@RequestMapping("/api/mascotas")
@RequiredArgsConstructor
@Tag(name = "Mascotas", description = "API para gestión de mascotas (pacientes)")
public class MascotaController {

    private final IMascotaService mascotaService;

    @Operation(summary = "Crear nueva mascota", 
               description = "Los propietarios solo pueden crear mascotas para sí mismos. " +
                           "ADMIN, RECEPCIONISTA y VETERINARIO pueden crear mascotas para cualquier propietario.")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPCIONISTA', 'VETERINARIO', 'PROPIETARIO')")
    @PostMapping
    public ResponseEntity<MascotaResponseDTO> crear(@Valid @RequestBody MascotaRequestDTO requestDTO) {
        return new ResponseEntity<>(mascotaService.crear(requestDTO), HttpStatus.CREATED);
    }

    @Operation(summary = "Actualizar mascota")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPCIONISTA', 'VETERINARIO')")
    @PutMapping("/{id}")
    public ResponseEntity<MascotaResponseDTO> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody MascotaRequestDTO requestDTO) {
        return ResponseEntity.ok(mascotaService.actualizar(id, requestDTO));
    }

    @Operation(summary = "Buscar mascota por ID")
    @GetMapping("/{id}")
    public ResponseEntity<MascotaResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(mascotaService.buscarPorId(id));
    }

    @Operation(summary = "Listar todas las mascotas")
    @GetMapping
    public ResponseEntity<List<MascotaResponseDTO>> listarTodas() {
        return ResponseEntity.ok(mascotaService.listarTodas());
    }

    @Operation(summary = "Listar mascotas activas")
    @GetMapping("/activas")
    public ResponseEntity<List<MascotaResponseDTO>> listarActivas() {
        return ResponseEntity.ok(mascotaService.listarActivas());
    }

    @Operation(summary = "Listar mascotas por propietario")
    @GetMapping("/propietario/{idPropietario}")
    public ResponseEntity<List<MascotaResponseDTO>> listarPorPropietario(@PathVariable Long idPropietario) {
        return ResponseEntity.ok(mascotaService.listarPorPropietario(idPropietario));
    }

    @Operation(summary = "Listar mascotas por especie")
    @GetMapping("/especie/{idEspecie}")
    public ResponseEntity<List<MascotaResponseDTO>> listarPorEspecie(@PathVariable Long idEspecie) {
        return ResponseEntity.ok(mascotaService.listarPorEspecie(idEspecie));
    }

    @Operation(summary = "Listar mascotas por raza")
    @GetMapping("/raza/{idRaza}")
    public ResponseEntity<List<MascotaResponseDTO>> listarPorRaza(@PathVariable Long idRaza) {
        return ResponseEntity.ok(mascotaService.listarPorRaza(idRaza));
    }

    @Operation(summary = "Buscar mascotas por nombre")
    @GetMapping("/buscar")
    public ResponseEntity<List<MascotaResponseDTO>> buscarPorNombre(@RequestParam String nombre) {
        return ResponseEntity.ok(mascotaService.buscarPorNombre(nombre));
    }

    @Operation(summary = "Eliminar mascota")
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        mascotaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Activar mascota")
    @PatchMapping("/{id}/activar")
    public ResponseEntity<MascotaResponseDTO> activar(@PathVariable Long id) {
        return ResponseEntity.ok(mascotaService.activar(id));
    }
}
