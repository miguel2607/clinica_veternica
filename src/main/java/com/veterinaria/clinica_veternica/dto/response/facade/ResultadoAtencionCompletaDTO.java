package com.veterinaria.clinica_veternica.dto.response.facade;

import com.veterinaria.clinica_veternica.dto.response.agenda.CitaResponseDTO;
import com.veterinaria.clinica_veternica.dto.response.clinico.EvolucionClinicaResponseDTO;
import com.veterinaria.clinica_veternica.dto.response.clinico.HistoriaClinicaResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para resultado de procesar atención completa.
 * Incluye la cita, historia clínica y evolución creada.
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
public class ResultadoAtencionCompletaDTO {
    private CitaResponseDTO cita;
    private HistoriaClinicaResponseDTO historiaClinica;
    private EvolucionClinicaResponseDTO evolucionClinica;
    private String mensaje;
}
