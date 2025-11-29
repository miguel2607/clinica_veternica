package com.veterinaria.clinica_veternica.controller;

import com.veterinaria.clinica_veternica.domain.usuario.Usuario;
import com.veterinaria.clinica_veternica.dto.request.paciente.PropietarioRequestDTO;
import com.veterinaria.clinica_veternica.dto.response.paciente.PropietarioResponseDTO;
import com.veterinaria.clinica_veternica.repository.UsuarioRepository;
import com.veterinaria.clinica_veternica.service.interfaces.IPropietarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/propietarios")
@RequiredArgsConstructor
@Tag(name = "Propietarios", description = "API para gestión de propietarios de mascotas")
public class PropietarioController {

    private final IPropietarioService propietarioService;
    private final UsuarioRepository usuarioRepository;

    @Operation(summary = "Crear nuevo propietario")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPCIONISTA')")
    @PostMapping
    public ResponseEntity<PropietarioResponseDTO> crear(@Valid @RequestBody PropietarioRequestDTO requestDTO) {
        return new ResponseEntity<>(propietarioService.crear(requestDTO), HttpStatus.CREATED);
    }

    @Operation(summary = "Actualizar propietario")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPCIONISTA')")
    @PutMapping("/{id}")
    public ResponseEntity<PropietarioResponseDTO> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody PropietarioRequestDTO requestDTO) {
        return ResponseEntity.ok(propietarioService.actualizar(id, requestDTO));
    }

    @Operation(summary = "Buscar propietario por ID")
    @GetMapping("/{id}")
    public ResponseEntity<PropietarioResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(propietarioService.buscarPorId(id));
    }

    @Operation(summary = "Listar todos los propietarios")
    @GetMapping
    public ResponseEntity<List<PropietarioResponseDTO>> listarTodos() {
        return ResponseEntity.ok(propietarioService.listarTodos());
    }

    @Operation(summary = "Listar propietarios activos")
    @GetMapping("/activos")
    public ResponseEntity<List<PropietarioResponseDTO>> listarActivos() {
        return ResponseEntity.ok(propietarioService.listarActivos());
    }

    @Operation(summary = "Buscar propietarios por nombre")
    @GetMapping("/buscar")
    public ResponseEntity<List<PropietarioResponseDTO>> buscarPorNombre(@RequestParam String nombre) {
        return ResponseEntity.ok(propietarioService.buscarPorNombre(nombre));
    }

    @Operation(summary = "Buscar propietario por documento")
    @GetMapping("/documento")
    public ResponseEntity<PropietarioResponseDTO> buscarPorDocumento(
            @RequestParam String tipoDocumento,
            @RequestParam String numeroDocumento) {
        return ResponseEntity.ok(propietarioService.buscarPorDocumento(tipoDocumento, numeroDocumento));
    }

    @Operation(summary = "Buscar propietario por email")
    @GetMapping("/email")
    public ResponseEntity<PropietarioResponseDTO> buscarPorEmail(@RequestParam String email) {
        return ResponseEntity.ok(propietarioService.buscarPorEmail(email));
    }

    @Operation(summary = "Obtener o crear propietario por email", 
               description = "Obtiene el propietario por email. Si no existe, lo crea automáticamente con datos básicos del usuario autenticado.")
    @GetMapping("/mi-perfil")
    @PreAuthorize("hasRole('PROPIETARIO')")
    public ResponseEntity<PropietarioResponseDTO> obtenerOCrearMiPerfil() {
        // Obtener el username del usuario autenticado
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        
        // Buscar el usuario en la base de datos
        Usuario usuario = usuarioRepository.findByUsername(username)
            .orElseThrow(() -> new com.veterinaria.clinica_veternica.exception.ResourceNotFoundException("Usuario no encontrado"));
        
        return ResponseEntity.ok(propietarioService.obtenerOCrearPropietarioPorEmail(usuario.getEmail()));
    }

    @Operation(summary = "Buscar propietarios por teléfono")
    @GetMapping("/telefono")
    public ResponseEntity<List<PropietarioResponseDTO>> buscarPorTelefono(@RequestParam String telefono) {
        return ResponseEntity.ok(propietarioService.buscarPorTelefono(telefono));
    }

    @Operation(summary = "Eliminar propietario")
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        propietarioService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Activar propietario")
    @PatchMapping("/{id}/activar")
    public ResponseEntity<PropietarioResponseDTO> activar(@PathVariable Long id) {
        return ResponseEntity.ok(propietarioService.activar(id));
    }

    @Operation(summary = "Sincronizar usuarios con rol PROPIETARIO",
               description = "Crea registros de propietarios para usuarios con rol PROPIETARIO que no tienen uno asociado. " +
                             "Útil para migración de datos y corrección de inconsistencias.")
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/sincronizar")
    public ResponseEntity<SincronizacionResponse> sincronizarUsuariosPropietarios() {
        int propietariosCreados = propietarioService.sincronizarUsuariosPropietarios();

        SincronizacionResponse response = new SincronizacionResponse(
            "Sincronización completada exitosamente",
            propietariosCreados,
            propietariosCreados > 0
                ? "Se crearon " + propietariosCreados + " registros de propietarios"
                : "No se encontraron usuarios sin propietario asociado"
        );

        return ResponseEntity.ok(response);
    }

    /**
     * DTO para la respuesta de sincronización.
     */
    private record SincronizacionResponse(
        String mensaje,
        int propietariosCreados,
        String detalle
    ) {}
}
