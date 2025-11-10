package com.veterinaria.clinica_veternica.controller;

import com.veterinaria.clinica_veternica.dto.request.paciente.RazaRequestDTO;
import com.veterinaria.clinica_veternica.dto.response.paciente.RazaResponseDTO;
import com.veterinaria.clinica_veternica.service.interfaces.IRazaService;
import io.swagger.v3.oas.annotations.Operation;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/razas")
@RequiredArgsConstructor
@Tag(name = "Razas", description = "API para gestión de razas de mascotas")
public class RazaController {

    private final IRazaService razaService;

    @Operation(summary = "Crear nueva raza")
    @PostMapping
    public ResponseEntity<RazaResponseDTO> crear(@Valid @RequestBody RazaRequestDTO requestDTO) {
        return new ResponseEntity<>(razaService.crear(requestDTO), HttpStatus.CREATED);
    }

    @Operation(summary = "Actualizar raza")
    @PutMapping("/{id}")
    public ResponseEntity<RazaResponseDTO> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody RazaRequestDTO requestDTO) {
        return ResponseEntity.ok(razaService.actualizar(id, requestDTO));
    }

    @Operation(summary = "Buscar raza por ID")
    @GetMapping("/{id}")
    public ResponseEntity<RazaResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(razaService.buscarPorId(id));
    }

    @Operation(summary = "Listar todas las razas")
    @GetMapping
    public ResponseEntity<List<RazaResponseDTO>> listarTodas() {
        return ResponseEntity.ok(razaService.listarTodas());
    }

    @Operation(summary = "Listar razas con paginación")
    @GetMapping("/paginadas")
    public ResponseEntity<Page<RazaResponseDTO>> listarTodasPaginadas(
            @PageableDefault(size = 10, sort = "nombre") Pageable pageable) {
        return ResponseEntity.ok(razaService.listarTodas(pageable));
    }

    @Operation(summary = "Listar razas por especie")
    @GetMapping("/especie/{idEspecie}")
    public ResponseEntity<List<RazaResponseDTO>> listarPorEspecie(@PathVariable Long idEspecie) {
        return ResponseEntity.ok(razaService.listarPorEspecie(idEspecie));
    }

    @Operation(summary = "Listar razas activas")
    @GetMapping("/activas")
    public ResponseEntity<List<RazaResponseDTO>> listarActivas() {
        return ResponseEntity.ok(razaService.listarActivas());
    }

    @Operation(summary = "Listar razas activas por especie")
    @GetMapping("/activas/especie/{idEspecie}")
    public ResponseEntity<List<RazaResponseDTO>> listarActivasPorEspecie(@PathVariable Long idEspecie) {
        return ResponseEntity.ok(razaService.listarActivasPorEspecie(idEspecie));
    }

    @Operation(summary = "Buscar razas por nombre")
    @GetMapping("/buscar")
    public ResponseEntity<List<RazaResponseDTO>> buscarPorNombre(@RequestParam String nombre) {
        return ResponseEntity.ok(razaService.buscarPorNombre(nombre));
    }

    @Operation(summary = "Eliminar raza")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        razaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Activar raza")
    @PatchMapping("/{id}/activar")
    public ResponseEntity<RazaResponseDTO> activar(@PathVariable Long id) {
        return ResponseEntity.ok(razaService.activar(id));
    }
}
