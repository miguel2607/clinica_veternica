package com.veterinaria.clinica_veternica.controller;

import com.veterinaria.clinica_veternica.dto.request.clinico.HistoriaClinicaRequestDTO;
import com.veterinaria.clinica_veternica.dto.request.paciente.MascotaRequestDTO;
import com.veterinaria.clinica_veternica.dto.request.paciente.PropietarioRequestDTO;
import com.veterinaria.clinica_veternica.dto.response.facade.AlertasMedicasDTO;
import com.veterinaria.clinica_veternica.dto.response.facade.InformacionCompletaMascotaDTO;
import com.veterinaria.clinica_veternica.dto.response.facade.ResultadoRegistroCompletoDTO;
import com.veterinaria.clinica_veternica.patterns.structural.facade.BusquedaFacadeService;
import com.veterinaria.clinica_veternica.patterns.structural.facade.OperacionesFacadeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Facade Controller especializado para operaciones complejas de Mascotas.
 * Implementa el patrón Facade para simplificar operaciones que requieren
 * coordinación de múltiples servicios relacionados con mascotas.
 *
 * @author Clínica Veterinaria Team
 * @version 3.0 - Refactorizado para usar servicios especializados
 * @since 2025-11-17
 */
@RestController
@RequestMapping("/api/facade/mascotas")
@RequiredArgsConstructor
@Tag(name = "Facade - Mascotas", description = "Operaciones complejas de mascotas (Facade Pattern)")
public class MascotaFacadeController {

    private final OperacionesFacadeService operacionesFacadeService;
    private final BusquedaFacadeService busquedaFacadeService;

    @Operation(summary = "Obtener información completa de mascota",
               description = "Obtiene mascota, historia clínica y citas en una sola llamada.")
    @GetMapping("/{idMascota}/completa")
    @PreAuthorize("hasAnyRole('ADMIN', 'VETERINARIO', 'RECEPCIONISTA')")
    public ResponseEntity<InformacionCompletaMascotaDTO> obtenerInformacionCompletaMascota(
            @Parameter(description = "ID de la mascota") @PathVariable Long idMascota) {
        return ResponseEntity.ok(operacionesFacadeService.obtenerInformacionCompletaMascota(idMascota));
    }

    @Operation(summary = "Registrar mascota completa",
               description = "Crea propietario, mascota e historia clínica inicial en una sola operación. Ideal para formularios de registro.")
    @PostMapping("/registro-completo")
    @PreAuthorize("hasAnyRole('ADMIN', 'VETERINARIO', 'RECEPCIONISTA')")
    public ResponseEntity<ResultadoRegistroCompletoDTO> registrarMascotaCompleta(
            @Valid @RequestBody RegistroMascotaCompletoRequest request) {
        return new ResponseEntity<>(
                operacionesFacadeService.registrarMascotaCompleta(
                        request.getPropietario(),
                        request.getMascota(),
                        request.getHistoriaClinica()
                ),
                HttpStatus.CREATED
        );
    }

    @Operation(summary = "Buscar mascotas con alertas médicas",
               description = "Busca mascotas que requieren seguimiento médico, vacunas pendientes o tratamientos activos.")
    @GetMapping("/alertas-medicas")
    @PreAuthorize("hasAnyRole('ADMIN', 'VETERINARIO', 'AUXILIAR')")
    public ResponseEntity<AlertasMedicasDTO> buscarMascotasConAlertas() {
        return ResponseEntity.ok(busquedaFacadeService.obtenerMascotasConAlertasMedicas());
    }

    /**
     * DTO interno para el registro completo de mascota.
     */
    public static class RegistroMascotaCompletoRequest {
        @Valid
        private PropietarioRequestDTO propietario;

        @Valid
        private MascotaRequestDTO mascota;

        private HistoriaClinicaRequestDTO historiaClinica;

        // Getters y setters
        public PropietarioRequestDTO getPropietario() {
            return propietario;
        }

        public void setPropietario(PropietarioRequestDTO propietario) {
            this.propietario = propietario;
        }

        public MascotaRequestDTO getMascota() {
            return mascota;
        }

        public void setMascota(MascotaRequestDTO mascota) {
            this.mascota = mascota;
        }

        public HistoriaClinicaRequestDTO getHistoriaClinica() {
            return historiaClinica;
        }

        public void setHistoriaClinica(HistoriaClinicaRequestDTO historiaClinica) {
            this.historiaClinica = historiaClinica;
        }
    }
}
