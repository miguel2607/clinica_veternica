package com.veterinaria.clinica_veternica.dto.response.facade;

import com.veterinaria.clinica_veternica.dto.response.paciente.MascotaResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO para alertas médicas de mascotas.
 * Incluye mascotas que requieren atención.
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
public class AlertasMedicasDTO {
    private List<MascotaResponseDTO> mascotasConAlertas;
    private Integer totalAlertas;
    private String mensaje;
}
