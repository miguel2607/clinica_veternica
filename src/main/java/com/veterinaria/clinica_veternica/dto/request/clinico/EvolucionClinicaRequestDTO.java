package com.veterinaria.clinica_veternica.dto.request.clinico;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de Request para crear/actualizar una Evolución Clínica.
 *
 * @author Clínica Veterinaria Team
 * @version 1.0
 * @since 2025-11-03
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EvolucionClinicaRequestDTO {

    @NotNull @Positive
    private Long idHistoriaClinica;

    @NotNull @Positive
    private Long idVeterinario;

    @NotBlank @Size(max = 50)
    private String tipoEvolucion;

    @NotBlank @Size(min = 10, max = 2000)
    private String motivoConsulta;

    @NotBlank @Size(min = 10, max = 3000)
    private String hallazgosExamen;

    @Size(max = 2000)
    private String diagnostico;

    @Size(max = 3000)
    private String planTratamiento;

    @Size(max = 1000)
    private String observaciones;

    @DecimalMin(value = "0.0")
    private Double peso;

    @DecimalMin(value = "0.0")
    private Double temperatura;

    @Min(0)
    private Integer frecuenciaCardiaca;

    @Min(0)
    private Integer frecuenciaRespiratoria;
}
