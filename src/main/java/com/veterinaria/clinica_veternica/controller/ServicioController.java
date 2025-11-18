package com.veterinaria.clinica_veternica.controller;

import com.veterinaria.clinica_veternica.dto.request.agenda.ServicioRequestDTO;
import com.veterinaria.clinica_veternica.dto.response.agenda.ServicioResponseDTO;
import com.veterinaria.clinica_veternica.service.interfaces.IServicioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * Controller REST para gestión de Servicios.
 * Utiliza el patrón Factory Method para crear servicios según su categoría.
 *
 * @author Clínica Veterinaria Team
 * @version 1.0
 * @since 2025-11-06
 */
@RestController
@RequestMapping("/api/servicios")
@RequiredArgsConstructor
@Tag(name = "Servicios", description = "API para gestión de servicios veterinarios")
public class ServicioController {

    private final IServicioService servicioService;

    @Operation(summary = "Crear nuevo servicio")
    @PostMapping
    public ResponseEntity<ServicioResponseDTO> crear(@Valid @RequestBody ServicioRequestDTO requestDTO) {
        return new ResponseEntity<>(servicioService.crear(requestDTO), HttpStatus.CREATED);
    }

    @Operation(summary = "Crear servicio usando Factory", description = "Crea un servicio usando Factory Method pattern según la categoría")
    @PostMapping("/factory")
    public ResponseEntity<ServicioResponseDTO> crearConFactory(
            @Parameter(description = "Nombre del servicio") @RequestParam String nombre,
            @Parameter(description = "Descripción del servicio") @RequestParam String descripcion,
            @Parameter(description = "Precio del servicio") @RequestParam BigDecimal precio,
            @Parameter(description = "Categoría (CLINICO, QUIRURGICO, ESTETICO, EMERGENCIA)") @RequestParam String categoria) {
        return new ResponseEntity<>(servicioService.crearConFactory(nombre, descripcion, precio, categoria), HttpStatus.CREATED);
    }

    @Operation(summary = "Actualizar servicio")
    @PutMapping("/{id}")
    public ResponseEntity<ServicioResponseDTO> actualizar(
            @Parameter(description = "ID del servicio") @PathVariable Long id,
            @Valid @RequestBody ServicioRequestDTO requestDTO) {
        return ResponseEntity.ok(servicioService.actualizar(id, requestDTO));
    }

    @Operation(summary = "Buscar servicio por ID")
    @GetMapping("/{id}")
    public ResponseEntity<ServicioResponseDTO> buscarPorId(
            @Parameter(description = "ID del servicio") @PathVariable Long id) {
        return ResponseEntity.ok(servicioService.buscarPorId(id));
    }

    @Operation(summary = "Listar todos los servicios")
    @GetMapping
    public ResponseEntity<List<ServicioResponseDTO>> listarTodos() {
        return ResponseEntity.ok(servicioService.listarTodos());
    }

    @Operation(summary = "Listar servicios activos")
    @GetMapping("/activos")
    public ResponseEntity<List<ServicioResponseDTO>> listarActivos() {
        return ResponseEntity.ok(servicioService.listarActivos());
    }

    @Operation(summary = "Listar servicios por tipo")
    @GetMapping("/tipo/{tipoServicio}")
    public ResponseEntity<List<ServicioResponseDTO>> listarPorTipo(
            @Parameter(description = "Tipo de servicio") @PathVariable String tipoServicio) {
        return ResponseEntity.ok(servicioService.listarPorTipo(tipoServicio));
    }

    @Operation(summary = "Listar servicios por categoría")
    @GetMapping("/categoria/{categoria}")
    public ResponseEntity<List<ServicioResponseDTO>> listarPorCategoria(
            @Parameter(description = "Categoría del servicio") @PathVariable String categoria) {
        return ResponseEntity.ok(servicioService.listarPorCategoria(categoria));
    }

    @Operation(summary = "Listar servicios por rango de precio")
    @GetMapping("/precio")
    public ResponseEntity<List<ServicioResponseDTO>> listarPorRangoPrecio(
            @Parameter(description = "Precio mínimo") @RequestParam BigDecimal min,
            @Parameter(description = "Precio máximo") @RequestParam BigDecimal max) {
        return ResponseEntity.ok(servicioService.listarPorRangoPrecio(min, max));
    }

    @Operation(summary = "Eliminar servicio (desactivar)")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(
            @Parameter(description = "ID del servicio") @PathVariable Long id) {
        servicioService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Activar servicio")
    @PutMapping("/{id}/activar")
    public ResponseEntity<ServicioResponseDTO> activar(
            @Parameter(description = "ID del servicio") @PathVariable Long id) {
        return ResponseEntity.ok(servicioService.activar(id));
    }

    @Operation(summary = "Desactivar servicio")
    @PutMapping("/{id}/desactivar")
    public ResponseEntity<ServicioResponseDTO> desactivar(
            @Parameter(description = "ID del servicio") @PathVariable Long id) {
        return ResponseEntity.ok(servicioService.desactivar(id));
    }
}

