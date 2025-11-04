package com.veterinaria.clinica_veternica.dto.response.inventario;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO de Response para un Tipo de Insumo.
 *
 * @author Clínica Veterinaria Team
 * @version 1.0
 * @since 2025-11-04
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TipoInsumoResponseDTO {

    private Long idTipoInsumo;
    private String nombre;
    private String descripcion;
    private String categoria;
    private Boolean activo;
    private Integer cantidadInsumos;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaModificacion;
}
