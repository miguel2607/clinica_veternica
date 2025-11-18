package com.veterinaria.clinica_veternica.controller;

import com.veterinaria.clinica_veternica.patterns.structural.facade.ClinicaFacade;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Facade Controller especializado para Búsquedas Avanzadas.
 * Implementa el patrón Facade para simplificar búsquedas complejas que requieren
 * coordinación de múltiples servicios.
 *
 * @author Clínica Veterinaria Team
 * @version 2.0
 * @since 2025-11-17
 */
@RestController
@RequestMapping("/api/facade/busquedas")
@RequiredArgsConstructor
@Tag(name = "Facade - Búsquedas", description = "Búsquedas avanzadas y complejas (Facade Pattern)")
public class BusquedasFacadeController {

    private final ClinicaFacade clinicaFacade;

    @Operation(summary = "Búsqueda global",
               description = "Busca en mascotas, propietarios y veterinarios por término de búsqueda.")
    @GetMapping("/global")
    @PreAuthorize("hasAnyRole('ADMIN', 'VETERINARIO', 'RECEPCIONISTA')")
    public ResponseEntity<Map<String, Object>> busquedaGlobal(
            @Parameter(description = "Término de búsqueda") @RequestParam String termino) {
        return ResponseEntity.ok(clinicaFacade.busquedaGlobal(termino));
    }
}
