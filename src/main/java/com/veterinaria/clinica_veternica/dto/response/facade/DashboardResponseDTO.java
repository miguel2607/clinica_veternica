package com.veterinaria.clinica_veternica.dto.response.facade;

import com.veterinaria.clinica_veternica.dto.response.agenda.CitaResponseDTO;
import com.veterinaria.clinica_veternica.dto.response.comunicacion.NotificacionResponseDTO;
import com.veterinaria.clinica_veternica.dto.response.inventario.InventarioResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO para respuesta del dashboard principal.
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
public class DashboardResponseDTO {
    private List<CitaResponseDTO> citasHoy;
    private Integer totalCitasHoy;
    private List<CitaResponseDTO> citasProgramadas;
    private Integer totalCitasProgramadas;
    private List<InventarioResponseDTO> stockBajo;
    private Integer totalStockBajo;
    private List<NotificacionResponseDTO> notificacionesRecientes;
    private Integer totalNotificacionesRecientes;
}
