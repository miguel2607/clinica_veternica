package com.veterinaria.clinica_veternica.controller;

import com.veterinaria.clinica_veternica.dto.request.usuario.VeterinarioRequestDTO;
import com.veterinaria.clinica_veternica.dto.response.usuario.VeterinarioResponseDTO;
import com.veterinaria.clinica_veternica.service.interfaces.IVeterinarioService;
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
@RequestMapping("/api/veterinarios")
@RequiredArgsConstructor
@Tag(name = "Veterinarios", description = "API para gestión de veterinarios")
public class VeterinarioController {

    private final IVeterinarioService veterinarioService;

    @Operation(summary = "Crear nuevo veterinario")
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<VeterinarioResponseDTO> crear(@Valid @RequestBody VeterinarioRequestDTO requestDTO) {
        return new ResponseEntity<>(veterinarioService.crear(requestDTO), HttpStatus.CREATED);
    }

    @Operation(summary = "Actualizar veterinario")
    @PreAuthorize("hasAnyRole('ADMIN', 'VETERINARIO')")
    @PutMapping("/{id}")
    public ResponseEntity<VeterinarioResponseDTO> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody VeterinarioRequestDTO requestDTO) {
        return ResponseEntity.ok(veterinarioService.actualizar(id, requestDTO));
    }

    @Operation(summary = "Obtener mi perfil de veterinario", 
               description = "Obtiene el perfil del veterinario asociado al usuario autenticado")
    @GetMapping("/mi-perfil")
    @PreAuthorize("hasRole('VETERINARIO')")
    public ResponseEntity<VeterinarioResponseDTO> obtenerMiPerfil() {
        return ResponseEntity.ok(veterinarioService.obtenerPorUsuarioAutenticado());
    }

    @Operation(summary = "Buscar veterinario por ID")
    @GetMapping("/{id}")
    public ResponseEntity<VeterinarioResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(veterinarioService.buscarPorId(id));
    }

    @Operation(summary = "Buscar veterinario por registro profesional")
    @GetMapping("/registro/{registroProfesional}")
    public ResponseEntity<VeterinarioResponseDTO> buscarPorRegistroProfesional(
            @PathVariable String registroProfesional) {
        return ResponseEntity.ok(veterinarioService.buscarPorRegistroProfesional(registroProfesional));
    }

    @Operation(summary = "Listar todos los veterinarios")
    @GetMapping
    public ResponseEntity<List<VeterinarioResponseDTO>> listarTodos() {
        return ResponseEntity.ok(veterinarioService.listarTodos());
    }

    @Operation(summary = "Listar veterinarios activos")
    @GetMapping("/activos")
    public ResponseEntity<List<VeterinarioResponseDTO>> listarActivos() {
        return ResponseEntity.ok(veterinarioService.listarActivos());
    }

    @Operation(summary = "Listar veterinarios disponibles")
    @GetMapping("/disponibles")
    public ResponseEntity<List<VeterinarioResponseDTO>> listarDisponibles() {
        return ResponseEntity.ok(veterinarioService.listarDisponibles());
    }

    @Operation(summary = "Listar veterinarios por especialidad")
    @GetMapping("/especialidad")
    public ResponseEntity<List<VeterinarioResponseDTO>> listarPorEspecialidad(@RequestParam String especialidad) {
        return ResponseEntity.ok(veterinarioService.listarPorEspecialidad(especialidad));
    }

    @Operation(summary = "Buscar veterinarios por nombre")
    @GetMapping("/buscar")
    public ResponseEntity<List<VeterinarioResponseDTO>> buscarPorNombre(@RequestParam String nombre) {
        return ResponseEntity.ok(veterinarioService.buscarPorNombre(nombre));
    }

    @Operation(summary = "Eliminar veterinario")
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        veterinarioService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Activar veterinario")
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/activar")
    public ResponseEntity<VeterinarioResponseDTO> activar(@PathVariable Long id) {
        return ResponseEntity.ok(veterinarioService.activar(id));
    }

    @Operation(summary = "Crear veterinario desde usuario existente",
               description = "Crea un veterinario para un usuario con rol VETERINARIO que no tiene veterinario asociado")
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/desde-usuario/{idUsuario}")
    public ResponseEntity<VeterinarioResponseDTO> crearDesdeUsuario(@PathVariable Long idUsuario) {
        return new ResponseEntity<>(veterinarioService.crearDesdeUsuario(idUsuario), HttpStatus.CREATED);
    }

    @Operation(summary = "Sincronizar usuarios veterinarios",
               description = "Crea veterinarios para todos los usuarios con rol VETERINARIO que no tienen veterinario asociado")
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/sincronizar")
    public ResponseEntity<Object> sincronizarUsuariosVeterinarios() {
        int creados = veterinarioService.sincronizarUsuariosVeterinarios();
        return ResponseEntity.ok(java.util.Map.of(
            "mensaje", "Sincronización completada",
            "veterinariosCreados", creados
        ));
    }
}
