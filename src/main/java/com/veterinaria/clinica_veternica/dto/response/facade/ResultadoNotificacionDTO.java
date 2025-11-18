package com.veterinaria.clinica_veternica.dto.response.facade;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para resultado de envío de notificaciones.
 * Incluye contadores de notificaciones enviadas y errores.
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
public class ResultadoNotificacionDTO {
    private String tipoOperacion;
    private Integer notificacionesEnviadas;
    private Integer errores;
    private String mensaje;
    private Boolean exitoso;
}
