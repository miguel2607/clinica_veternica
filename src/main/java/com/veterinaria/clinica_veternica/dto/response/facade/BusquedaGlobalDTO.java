package com.veterinaria.clinica_veternica.dto.response.facade;

import com.veterinaria.clinica_veternica.dto.response.paciente.MascotaResponseDTO;
import com.veterinaria.clinica_veternica.dto.response.paciente.PropietarioResponseDTO;
import com.veterinaria.clinica_veternica.dto.response.usuario.VeterinarioResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO para resultados de búsqueda global.
 * Incluye resultados de propietarios, mascotas y veterinarios.
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
public class BusquedaGlobalDTO {
    private String terminoBusqueda;
    private List<PropietarioResponseDTO> propietarios;
    private Integer totalPropietarios;
    private List<MascotaResponseDTO> mascotas;
    private Integer totalMascotas;
    private List<VeterinarioResponseDTO> veterinarios;
    private Integer totalVeterinarios;
    private Integer totalResultados;
}
