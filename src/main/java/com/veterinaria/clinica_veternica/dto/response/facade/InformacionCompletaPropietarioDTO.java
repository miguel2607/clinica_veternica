package com.veterinaria.clinica_veternica.dto.response.facade;

import com.veterinaria.clinica_veternica.dto.response.clinico.HistoriaClinicaResponseDTO;
import com.veterinaria.clinica_veternica.dto.response.paciente.MascotaResponseDTO;
import com.veterinaria.clinica_veternica.dto.response.paciente.PropietarioResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO para información completa de un propietario.
 * Incluye datos del propietario, sus mascotas e historias clínicas.
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
public class InformacionCompletaPropietarioDTO {
    private PropietarioResponseDTO propietario;
    private List<MascotaResponseDTO> mascotas;
    private Integer totalMascotas;
    private List<HistoriaClinicaResponseDTO> historiasClinicas;
}
