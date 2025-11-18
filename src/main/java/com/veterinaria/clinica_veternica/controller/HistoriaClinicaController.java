package com.veterinaria.clinica_veternica.controller;

import com.veterinaria.clinica_veternica.dto.request.clinico.HistoriaClinicaRequestDTO;
import com.veterinaria.clinica_veternica.dto.response.clinico.HistoriaClinicaResponseDTO;
import com.veterinaria.clinica_veternica.service.interfaces.IHistoriaClinicaService;
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
 * Controller REST para gestión de Historias Clínicas.
 * Maneja exclusivamente operaciones CRUD de historias clínicas.
 *
 * <p><b>Responsabilidad Única:</b></p>
 * <ul>
 *   <li>✅ <b>Una sola responsabilidad</b>: Gestión de historias clínicas</li>
 *   <li>✅ Cumple estrictamente <b>SRP (Single Responsibility Principle)</b></li>
 *   <li>✅ Controlador independiente, desacoplado</li>
 *   <li>✅ No maneja sub-recursos (evita antipatrón)</li>
 * </ul>
 *
 * <p><b>Principios SOLID:</b></p>
 * <ul>
 *   <li>✅ <b>SRP</b>: Una única responsabilidad</li>
 *   <li>✅ <b>DIP</b>: Depende de interfaces (abstracciones)</li>
 *   <li>✅ <b>ISP</b>: Interfaz específica y segregada</li>
 *   <li>✅ <b>OCP</b>: Extensible sin modificar código existente</li>
 *   <li>✅ <b>LSP</b>: Sustituible por implementaciones alternativas</li>
 * </ul>
 *
 * <p><b>Patrones de Diseño:</b></p>
 * <ul>
 *   <li>Builder: Para construcción de historias clínicas</li>
 *   <li>Memento: Para guardar/restaurar estados de historias clínicas</li>
 * </ul>
 *
 * @author Clínica Veterinaria Team
 * @version 2.0
 * @since 2025-11-17
 */
@RestController
@RequestMapping("/api/historias-clinicas")
@RequiredArgsConstructor
@Tag(name = "Historias Clínicas", description = "API para gestión de historias clínicas de mascotas")
public class HistoriaClinicaController {

    private final IHistoriaClinicaService historiaClinicaService;

    @Operation(summary = "Crear nueva historia clínica")
    @PreAuthorize("hasAnyRole('ADMIN', 'VETERINARIO')")
    @PostMapping
    public ResponseEntity<HistoriaClinicaResponseDTO> crear(@Valid @RequestBody HistoriaClinicaRequestDTO requestDTO) {
        return new ResponseEntity<>(historiaClinicaService.crear(requestDTO), HttpStatus.CREATED);
    }

    @Operation(summary = "Crear historia clínica usando Builder", description = "Crea una historia clínica usando Builder pattern")
    @PostMapping("/builder/{idMascota}")
    public ResponseEntity<HistoriaClinicaResponseDTO> crearConBuilder(
            @Parameter(description = "ID de la mascota") @PathVariable Long idMascota,
            @Valid @RequestBody HistoriaClinicaRequestDTO requestDTO) {
        return new ResponseEntity<>(historiaClinicaService.crearConBuilder(idMascota, requestDTO), HttpStatus.CREATED);
    }

    @Operation(summary = "Actualizar historia clínica", description = "Guarda un memento antes de actualizar (Memento pattern)")
    @PreAuthorize("hasAnyRole('ADMIN', 'VETERINARIO', 'AUXILIAR')")
    @PutMapping("/{id}")
    public ResponseEntity<HistoriaClinicaResponseDTO> actualizar(
            @Parameter(description = "ID de la historia clínica") @PathVariable Long id,
            @Valid @RequestBody HistoriaClinicaRequestDTO requestDTO) {
        return ResponseEntity.ok(historiaClinicaService.actualizar(id, requestDTO));
    }

    @Operation(summary = "Buscar historia clínica por ID")
    @GetMapping("/{id}")
    public ResponseEntity<HistoriaClinicaResponseDTO> buscarPorId(
            @Parameter(description = "ID de la historia clínica") @PathVariable Long id) {
        return ResponseEntity.ok(historiaClinicaService.buscarPorId(id));
    }

    @Operation(summary = "Buscar historia clínica por mascota")
    @GetMapping("/mascota/{idMascota}")
    public ResponseEntity<HistoriaClinicaResponseDTO> buscarPorMascota(
            @Parameter(description = "ID de la mascota") @PathVariable Long idMascota) {
        return ResponseEntity.ok(historiaClinicaService.buscarPorMascota(idMascota));
    }

    @Operation(summary = "Listar todas las historias clínicas")
    @GetMapping
    public ResponseEntity<List<HistoriaClinicaResponseDTO>> listarTodos() {
        return ResponseEntity.ok(historiaClinicaService.listarTodos());
    }

    @Operation(summary = "Listar historias clínicas activas")
    @GetMapping("/activas")
    public ResponseEntity<List<HistoriaClinicaResponseDTO>> listarActivas() {
        return ResponseEntity.ok(historiaClinicaService.listarActivas());
    }

    @Operation(summary = "Guardar memento", description = "Guarda un snapshot del estado actual (Memento pattern)")
    @PostMapping("/{id}/memento")
    public ResponseEntity<Void> guardarMemento(
            @Parameter(description = "ID de la historia clínica") @PathVariable Long id) {
        historiaClinicaService.guardarMemento(id);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Restaurar último memento", description = "Restaura el estado al último memento guardado (Memento pattern)")
    @PutMapping("/{id}/restaurar-ultimo")
    public ResponseEntity<Boolean> restaurarUltimoMemento(
            @Parameter(description = "ID de la historia clínica") @PathVariable Long id) {
        return ResponseEntity.ok(historiaClinicaService.restaurarUltimoMemento(id));
    }

    @Operation(summary = "Restaurar memento específico", description = "Restaura el estado a un memento específico por índice (Memento pattern)")
    @PutMapping("/{id}/restaurar/{indice}")
    public ResponseEntity<Boolean> restaurarMemento(
            @Parameter(description = "ID de la historia clínica") @PathVariable Long id,
            @Parameter(description = "Índice del memento") @PathVariable int indice) {
        return ResponseEntity.ok(historiaClinicaService.restaurarMemento(id, indice));
    }

    @Operation(summary = "Obtener cantidad de mementos", description = "Obtiene el número de mementos guardados para una historia clínica")
    @GetMapping("/{id}/mementos/cantidad")
    public ResponseEntity<Integer> obtenerCantidadMementos(
            @Parameter(description = "ID de la historia clínica") @PathVariable Long id) {
        return ResponseEntity.ok(historiaClinicaService.obtenerCantidadMementos(id));
    }

    @Operation(summary = "Archivar historia clínica")
    @PreAuthorize("hasAnyRole('ADMIN', 'VETERINARIO')")
    @PutMapping("/{id}/archivar")
    public ResponseEntity<Void> archivar(
            @Parameter(description = "ID de la historia clínica") @PathVariable Long id,
            @Parameter(description = "Motivo de archivo") @RequestParam String motivo) {
        historiaClinicaService.archivar(id, motivo);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Reactivar historia clínica")
    @PreAuthorize("hasAnyRole('ADMIN', 'VETERINARIO')")
    @PutMapping("/{id}/reactivar")
    public ResponseEntity<Void> reactivar(
            @Parameter(description = "ID de la historia clínica") @PathVariable Long id) {
        historiaClinicaService.reactivar(id);
        return ResponseEntity.ok().build();
    }
}

