package com.veterinaria.clinica_veternica.controller;

import com.veterinaria.clinica_veternica.dto.response.facade.InformacionCompletaPropietarioDTO;
import com.veterinaria.clinica_veternica.patterns.structural.facade.OperacionesFacadeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Facade Controller especializado para operaciones complejas de Propietarios.
 * Implementa el patrón Facade para simplificar operaciones que requieren
 * coordinación de múltiples servicios relacionados con propietarios.
 *
 * @author Clínica Veterinaria Team
 * @version 3.0 - Refactorizado para usar servicios especializados
 * @since 2025-11-17
 */
@RestController
@RequestMapping("/api/facade/propietarios")
@RequiredArgsConstructor
@Tag(name = "Facade - Propietarios", description = "Operaciones complejas de propietarios (Facade Pattern)")
public class PropietarioFacadeController {

    private final OperacionesFacadeService operacionesFacadeService;

    @Operation(summary = "Obtener información completa de propietario",
               description = "Obtiene propietario con todas sus mascotas e historias clínicas en una sola llamada.")
    @GetMapping("/{idPropietario}/completo")
    @PreAuthorize("hasAnyRole('ADMIN', 'VETERINARIO', 'RECEPCIONISTA')")
    public ResponseEntity<InformacionCompletaPropietarioDTO> obtenerInformacionCompletaPropietario(
            @Parameter(description = "ID del propietario") @PathVariable Long idPropietario) {
        return ResponseEntity.ok(operacionesFacadeService.obtenerInformacionCompletaPropietario(idPropietario));
    }
}
