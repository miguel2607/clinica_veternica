package com.veterinaria.clinica_veternica.controller;

import com.veterinaria.clinica_veternica.dto.response.facade.BusquedaGlobalDTO;
import com.veterinaria.clinica_veternica.patterns.structural.facade.BusquedaFacadeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Facade Controller especializado para Búsquedas Avanzadas.
 * Implementa el patrón Facade para simplificar búsquedas complejas que requieren
 * coordinación de múltiples servicios.
 *
 * @author Clínica Veterinaria Team
 * @version 3.0 - Refactorizado para usar BusquedaFacadeService especializado
 * @since 2025-11-17
 */
@RestController
@RequestMapping("/api/facade/busquedas")
@RequiredArgsConstructor
@Tag(name = "Facade - Búsquedas", description = "Búsquedas avanzadas y complejas (Facade Pattern)")
public class BusquedasFacadeController {

    private final BusquedaFacadeService busquedaFacadeService;

    @Operation(summary = "Búsqueda global",
               description = "Busca en mascotas, propietarios y veterinarios por término de búsqueda.")
    @GetMapping("/global")
    @PreAuthorize("hasAnyRole('ADMIN', 'VETERINARIO', 'RECEPCIONISTA')")
    public ResponseEntity<BusquedaGlobalDTO> busquedaGlobal(
            @Parameter(description = "Término de búsqueda") @RequestParam String termino) {
        return ResponseEntity.ok(busquedaFacadeService.busquedaGlobal(termino));
    }
}
