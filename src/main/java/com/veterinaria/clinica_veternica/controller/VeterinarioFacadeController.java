package com.veterinaria.clinica_veternica.controller;

import com.veterinaria.clinica_veternica.dto.response.facade.InformacionCompletaVeterinarioDTO;
import com.veterinaria.clinica_veternica.patterns.structural.facade.OperacionesFacadeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Facade Controller especializado para operaciones complejas de Veterinarios.
 * Implementa el patrón Facade para simplificar operaciones que requieren
 * coordinación de múltiples servicios relacionados con veterinarios.
 *
 * @author Clínica Veterinaria Team
 * @version 3.0 - Refactorizado para usar servicios especializados
 * @since 2025-11-17
 */
@RestController
@RequestMapping("/api/facade/veterinarios")
@RequiredArgsConstructor
@Tag(name = "Facade - Veterinarios", description = "Operaciones complejas de veterinarios (Facade Pattern)")
public class VeterinarioFacadeController {

    private final OperacionesFacadeService operacionesFacadeService;

    @Operation(summary = "Obtener información completa de veterinario",
               description = "Obtiene veterinario con sus citas, horarios y estadísticas.")
    @GetMapping("/{idVeterinario}/completo")
    @PreAuthorize("hasAnyRole('ADMIN', 'VETERINARIO')")
    public ResponseEntity<InformacionCompletaVeterinarioDTO> obtenerInformacionCompletaVeterinario(
            @Parameter(description = "ID del veterinario") @PathVariable Long idVeterinario) {
        return ResponseEntity.ok(operacionesFacadeService.obtenerInformacionCompletaVeterinario(idVeterinario));
    }
}
