package com.veterinaria.clinica_veternica.dto.request.clinico;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * DTO de Request para crear/actualizar una Vacunación.
 *
 * @author Clínica Veterinaria Team
 * @version 1.0
 * @since 2025-11-03
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VacunacionRequestDTO {

    @NotNull @Positive
    private Long idHistoriaClinica;

    @NotNull @Positive
    private Long idVeterinario;

    @NotBlank @Size(max = 200)
    private String nombreVacuna;

    @NotBlank @Size(max = 100)
    private String laboratorio;

    @NotBlank @Size(max = 50)
    private String lote;

    @NotNull
    private LocalDate fechaAplicacion;

    @NotNull @Future
    private LocalDate fechaProximaDosis;

    @Size(max = 100)
    private String viaAdministracion;

    @Size(max = 500)
    private String observaciones;

    private Boolean esquemaCompleto;
}
