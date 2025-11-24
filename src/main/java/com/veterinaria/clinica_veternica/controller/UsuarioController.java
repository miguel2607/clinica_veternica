package com.veterinaria.clinica_veternica.controller;

import com.veterinaria.clinica_veternica.dto.request.usuario.BloquearUsuarioRequestDTO;
import com.veterinaria.clinica_veternica.dto.request.usuario.CambiarPasswordRequestDTO;
import com.veterinaria.clinica_veternica.dto.request.usuario.ResetearPasswordRequestDTO;
import com.veterinaria.clinica_veternica.dto.request.usuario.UsuarioRequestDTO;
import com.veterinaria.clinica_veternica.dto.response.usuario.UsuarioResponseDTO;
import com.veterinaria.clinica_veternica.service.interfaces.IUsuarioService;
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
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
@Tag(name = "Usuarios", description = "API para gestión de usuarios del sistema")
public class UsuarioController {

    private final IUsuarioService usuarioService;

    @Operation(summary = "Crear nuevo usuario", description = "Solo administradores pueden crear usuarios")
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<UsuarioResponseDTO> crear(@Valid @RequestBody UsuarioRequestDTO requestDTO) {
        return new ResponseEntity<>(usuarioService.crear(requestDTO), HttpStatus.CREATED);
    }

    @Operation(summary = "Actualizar usuario", description = "Solo administradores pueden actualizar usuarios")
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody UsuarioRequestDTO requestDTO) {
        return ResponseEntity.ok(usuarioService.actualizar(id, requestDTO));
    }

    @Operation(summary = "Buscar usuario por ID")
    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(usuarioService.buscarPorId(id));
    }

    @Operation(summary = "Buscar usuario por username")
    @GetMapping("/username/{username}")
    public ResponseEntity<UsuarioResponseDTO> buscarPorUsername(@PathVariable String username) {
        return ResponseEntity.ok(usuarioService.buscarPorUsername(username));
    }

    @Operation(summary = "Buscar usuario por email")
    @GetMapping("/email")
    public ResponseEntity<UsuarioResponseDTO> buscarPorEmail(@RequestParam String email) {
        return ResponseEntity.ok(usuarioService.buscarPorEmail(email));
    }

    @Operation(summary = "Listar todos los usuarios")
    @GetMapping
    public ResponseEntity<List<UsuarioResponseDTO>> listarTodos() {
        return ResponseEntity.ok(usuarioService.listarTodos());
    }

    @Operation(summary = "Listar usuarios por rol")
    @GetMapping("/rol/{rol}")
    public ResponseEntity<List<UsuarioResponseDTO>> listarPorRol(@PathVariable String rol) {
        return ResponseEntity.ok(usuarioService.listarPorRol(rol));
    }

    @Operation(summary = "Listar usuarios por estado")
    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<UsuarioResponseDTO>> listarPorEstado(@PathVariable Boolean estado) {
        return ResponseEntity.ok(usuarioService.listarPorEstado(estado));
    }

    @Operation(summary = "Cambiar contraseña")
    @PatchMapping("/{id}/cambiar-password")
    public ResponseEntity<Void> cambiarPassword(
            @PathVariable Long id,
            @Valid @RequestBody CambiarPasswordRequestDTO requestDTO) {
        usuarioService.cambiarPassword(id, requestDTO.getPasswordActual(), requestDTO.getPasswordNueva());
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Resetear contraseña (admin)", description = "Solo administradores pueden resetear contraseñas")
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/resetear-password")
    public ResponseEntity<Void> resetearPassword(
            @PathVariable Long id,
            @Valid @RequestBody ResetearPasswordRequestDTO requestDTO) {
        usuarioService.resetearPassword(id, requestDTO.getNuevaPassword());
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Bloquear usuario", description = "Solo administradores pueden bloquear usuarios")
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/bloquear")
    public ResponseEntity<Void> bloquearUsuario(
            @PathVariable Long id,
            @RequestBody(required = false) BloquearUsuarioRequestDTO requestDTO) {
        String motivoBloqueo = requestDTO != null && requestDTO.getMotivo() != null 
                ? requestDTO.getMotivo() 
                : "Bloqueado por administrador";
        usuarioService.bloquearUsuario(id, motivoBloqueo);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Desbloquear usuario", description = "Solo administradores pueden desbloquear usuarios")
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/desbloquear")
    public ResponseEntity<Void> desbloquearUsuario(@PathVariable Long id) {
        usuarioService.desbloquearUsuario(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Activar usuario", description = "Solo administradores pueden activar usuarios")
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/activar")
    public ResponseEntity<Void> activarUsuario(@PathVariable Long id) {
        usuarioService.activarUsuario(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Desactivar usuario", description = "Solo administradores pueden desactivar usuarios")
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/desactivar")
    public ResponseEntity<Void> desactivarUsuario(@PathVariable Long id) {
        usuarioService.desactivarUsuario(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Eliminar usuario", description = "Solo administradores pueden eliminar usuarios. Nota: Se desactiva en lugar de eliminar físicamente por seguridad.")
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        usuarioService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
