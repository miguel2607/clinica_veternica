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

    // idHistoriaClinica se pasa como @RequestParam en el controlador, no en el body

    @NotNull @Positive
    private Long idVeterinario;

    @NotBlank @Size(max = 200)
    private String nombreVacuna;

    @Size(max = 30)
    private String tipoVacuna;

    @NotBlank @Size(max = 100)
    private String laboratorio;

    @NotBlank @Size(max = 50)
    private String lote;

    @NotNull
    private LocalDate fechaAplicacion;

    @NotNull
    private LocalDate fechaProximaDosis;

    @Size(max = 30)
    private String viaAdministracion;

    @Size(max = 500)
    private String enfermedadesPrevenidas;

    @Size(max = 500)
    private String observaciones;

    private Boolean esquemaCompleto;

    /**
     * ID del insumo (vacuna) utilizado del inventario.
     * Se asociará el insumo y se decrementará su stock.
     */
    @NotNull(message = "El ID del insumo es obligatorio")
    @Positive(message = "El ID del insumo debe ser un número positivo")
    private Long idInsumo;

    /**
     * Cantidad de insumo utilizada.
     */
    @NotNull(message = "La cantidad utilizada es obligatoria")
    @Min(value = 1, message = "La cantidad utilizada debe ser al menos 1")
    private Integer cantidadUsada;
}
