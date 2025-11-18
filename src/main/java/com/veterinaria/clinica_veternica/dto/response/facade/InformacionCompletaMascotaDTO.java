package com.veterinaria.clinica_veternica.dto.response.facade;

import com.veterinaria.clinica_veternica.dto.response.agenda.CitaResponseDTO;
import com.veterinaria.clinica_veternica.dto.response.clinico.HistoriaClinicaResponseDTO;
import com.veterinaria.clinica_veternica.dto.response.paciente.MascotaResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO para información completa de una mascota.
 * Incluye datos de la mascota, su historia clínica y citas.
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
public class InformacionCompletaMascotaDTO {
    private MascotaResponseDTO mascota;
    private HistoriaClinicaResponseDTO historiaClinica;
    private List<CitaResponseDTO> citas;
    private Integer totalCitas;
}
