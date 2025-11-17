package com.veterinaria.clinica_veternica.controller;

import com.veterinaria.clinica_veternica.dto.request.paciente.EspecieRequestDTO;
import com.veterinaria.clinica_veternica.dto.response.paciente.EspecieResponseDTO;
import com.veterinaria.clinica_veternica.service.interfaces.IEspecieService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller REST para gestión de Especies.
 *
 * @author Clínica Veterinaria Team
 * @version 1.0
 * @since 2025-11-03
 */
@RestController
@RequestMapping("/api/especies")
@RequiredArgsConstructor
@Tag(name = "Especies", description = "API para gestión de especies de mascotas")
public class EspecieController {

    private final IEspecieService especieService;

    @Operation(summary = "Crear nueva especie", description = "Crea una nueva especie en el sistema")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Especie creada exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
        @ApiResponse(responseCode = "409", description = "La especie ya existe")
    })
    @PostMapping
    public ResponseEntity<EspecieResponseDTO> crear(
            @Valid @RequestBody EspecieRequestDTO requestDTO) {
        EspecieResponseDTO especie = especieService.crear(requestDTO);
        return new ResponseEntity<>(especie, HttpStatus.CREATED);
    }

    @Operation(summary = "Actualizar especie", description = "Actualiza una especie existente")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Especie actualizada exitosamente"),
        @ApiResponse(responseCode = "404", description = "Especie no encontrada"),
        @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos")
    })
    @PutMapping("/{id}")
    public ResponseEntity<EspecieResponseDTO> actualizar(
            @Parameter(description = "ID de la especie") @PathVariable Long id,
            @Valid @RequestBody EspecieRequestDTO requestDTO) {
        EspecieResponseDTO especie = especieService.actualizar(id, requestDTO);
        return ResponseEntity.ok(especie);
    }

    @Operation(summary = "Buscar especie por ID", description = "Obtiene una especie por su ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Especie encontrada"),
        @ApiResponse(responseCode = "404", description = "Especie no encontrada")
    })
    @GetMapping("/{id}")
    public ResponseEntity<EspecieResponseDTO> buscarPorId(
            @Parameter(description = "ID de la especie") @PathVariable Long id) {
        EspecieResponseDTO especie = especieService.buscarPorId(id);
        return ResponseEntity.ok(especie);
    }

    @Operation(summary = "Listar todas las especies", description = "Obtiene todas las especies registradas")
    @GetMapping
    public ResponseEntity<List<EspecieResponseDTO>> listarTodas() {
        List<EspecieResponseDTO> especies = especieService.listarTodas();
        return ResponseEntity.ok(especies);
    }

    @Operation(summary = "Listar especies activas", description = "Obtiene solo las especies activas")
    @GetMapping("/activas")
    public ResponseEntity<List<EspecieResponseDTO>> listarActivas() {
        List<EspecieResponseDTO> especies = especieService.listarActivas();
        return ResponseEntity.ok(especies);
    }

    @Operation(summary = "Buscar especies por nombre", description = "Busca especies por nombre (búsqueda parcial)")
    @GetMapping("/buscar")
    public ResponseEntity<List<EspecieResponseDTO>> buscarPorNombre(
            @Parameter(description = "Nombre a buscar") @RequestParam String nombre) {
        List<EspecieResponseDTO> especies = especieService.buscarPorNombre(nombre);
        return ResponseEntity.ok(especies);
    }

    @Operation(summary = "Eliminar especie", description = "Elimina una especie (soft delete)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Especie eliminada exitosamente"),
        @ApiResponse(responseCode = "404", description = "Especie no encontrada"),
        @ApiResponse(responseCode = "422", description = "No se puede eliminar: tiene razas asociadas")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(
            @Parameter(description = "ID de la especie") @PathVariable Long id) {
        especieService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Activar especie", description = "Activa una especie previamente desactivada")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Especie activada exitosamente"),
        @ApiResponse(responseCode = "404", description = "Especie no encontrada"),
        @ApiResponse(responseCode = "422", description = "La especie ya está activa")
    })
    @PatchMapping("/{id}/activar")
    public ResponseEntity<EspecieResponseDTO> activar(
            @Parameter(description = "ID de la especie") @PathVariable Long id) {
        EspecieResponseDTO especie = especieService.activar(id);
        return ResponseEntity.ok(especie);
    }

    @Operation(summary = "Verificar existencia por nombre", description = "Verifica si existe una especie con el nombre dado")
    @GetMapping("/existe")
    public ResponseEntity<Boolean> existePorNombre(
            @Parameter(description = "Nombre a verificar") @RequestParam String nombre) {
        boolean existe = especieService.existePorNombre(nombre);
        return ResponseEntity.ok(existe);
    }
}
