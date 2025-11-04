package com.veterinaria.clinica_veternica.dto.request.clinico;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * DTO de Request para crear/actualizar una Receta Médica.
 *
 * @author Clínica Veterinaria Team
 * @version 1.0
 * @since 2025-11-03
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecetaMedicaRequestDTO {

    @NotNull @Positive
    private Long idHistoriaClinica;

    @NotNull @Positive
    private Long idVeterinario;

    @NotBlank @Size(max = 200)
    private String nombreMedicamento;

    @NotBlank @Size(max = 100)
    private String concentracion;

    @NotBlank @Size(max = 100)
    private String dosis;

    @NotBlank @Size(max = 100)
    private String frecuencia;

    @NotNull @Positive
    private Integer duracionDias;

    @Size(max = 50)
    private String viaAdministracion;

    @Size(max = 1000)
    private String indicaciones;

    @NotNull
    private LocalDate fechaEmision;

    @Min(1)
    private Integer numeroRepeticiones;
}
