package com.veterinaria.clinica_veternica.dto.response.facade;

import com.veterinaria.clinica_veternica.dto.response.agenda.CitaResponseDTO;
import com.veterinaria.clinica_veternica.dto.response.agenda.HorarioResponseDTO;
import com.veterinaria.clinica_veternica.dto.response.usuario.VeterinarioResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO para información completa de un veterinario.
 * Incluye datos del veterinario, sus horarios y citas.
 * Reemplaza el uso de Map<String, Object> para type-safety.
 *
 * @author Clínica Veterinaria Team
 * @version 2.0
 * @since 2025-11-17
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InformacionCompletaVeterinarioDTO {
    private VeterinarioResponseDTO veterinario;
    private List<HorarioResponseDTO> horarios;
    private Integer totalHorarios;
    private List<CitaResponseDTO> citasProgramadas;
    private Integer totalCitasProgramadas;
}
