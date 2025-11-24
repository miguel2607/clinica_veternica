package com.veterinaria.clinica_veternica.dto.response.inventario;

import com.veterinaria.clinica_veternica.domain.inventario.EstadoInsumo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * DTO de Response para un Insumo.
 *
 * @author Clínica Veterinaria Team
 * @version 1.0
 * @since 2025-11-04
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InsumoResponseDTO {

    private Long idInsumo;
    private String codigo;
    private String nombre;
    private String descripcion;
    private Long idTipoInsumo;
    private String nombreTipoInsumo;
    private String unidadMedida;
    private Integer cantidadStock;
    private Integer stockMinimo;
    private Integer stockMaximo;
    private BigDecimal precioCompra;
    private BigDecimal precioVenta;
    private String lote;
    private LocalDate fechaVencimiento;
    private LocalDate fechaUltimaCompra;
    private String ubicacion;
    private EstadoInsumo estado;
    private Boolean requiereRefrigeracion;
    private Boolean requiereReceta;
    private String observaciones;
    private Boolean activo;
    private Boolean esStockBajo;
    private Boolean estaSinStock;
    private Boolean estaProximoAVencer;
    private Boolean estaVencido;
    private BigDecimal margenGanancia;
    private Double porcentajeMargen;
    private BigDecimal valorTotalInventario;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaModificacion;
}
