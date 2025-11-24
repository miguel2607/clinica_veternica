package com.veterinaria.clinica_veternica.dto.request.inventario;

import com.veterinaria.clinica_veternica.domain.inventario.EstadoInsumo;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO de Request para crear/actualizar un Insumo.
 *
 * @author Clínica Veterinaria Team
 * @version 1.0
 * @since 2025-11-04
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InsumoRequestDTO {

    /**
     * Código único del insumo (SKU, código de barras, etc.).
     */
    @NotBlank(message = "El código es obligatorio")
    @Size(min = 2, max = 50, message = "El código debe tener entre 2 y 50 caracteres")
    private String codigo;

    /**
     * Nombre del insumo.
     */
    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 2, max = 200, message = "El nombre debe tener entre 2 y 200 caracteres")
    private String nombre;

    /**
     * Descripción del insumo.
     */
    @Size(max = 1000, message = "La descripción no puede exceder 1000 caracteres")
    private String descripcion;

    /**
     * ID del tipo de insumo.
     */
    @NotNull(message = "El tipo de insumo es obligatorio")
    private Long idTipoInsumo;

    /**
     * Unidad de medida (Unidad, Caja, Kg, Litro, etc.).
     */
    @NotBlank(message = "La unidad de medida es obligatoria")
    @Size(max = 50, message = "La unidad de medida no puede exceder 50 caracteres")
    private String unidadMedida;

    /**
     * Cantidad actual en stock.
     */
    @NotNull(message = "La cantidad en stock es obligatoria")
    @Min(value = 0, message = "La cantidad no puede ser negativa")
    private Integer cantidadStock;

    /**
     * Stock mínimo (punto de reorden).
     */
    @NotNull(message = "El stock mínimo es obligatorio")
    @Min(value = 0, message = "El stock mínimo no puede ser negativo")
    private Integer stockMinimo;

    /**
     * Stock máximo permitido.
     */
    @Min(value = 0, message = "El stock máximo no puede ser negativo")
    private Integer stockMaximo;

    /**
     * Precio unitario de compra.
     */
    @NotNull(message = "El precio de compra es obligatorio")
    @DecimalMin(value = "0.0", message = "El precio no puede ser negativo")
    private BigDecimal precioCompra;

    /**
     * Precio unitario de venta (si aplica).
     */
    @DecimalMin(value = "0.0", message = "El precio no puede ser negativo")
    private BigDecimal precioVenta;

    /**
     * Lote del insumo.
     */
    @Size(max = 50, message = "El lote no puede exceder 50 caracteres")
    private String lote;

    /**
     * Fecha de vencimiento.
     */
    private LocalDate fechaVencimiento;

    /**
     * Ubicación física en el almacén.
     */
    @Size(max = 100, message = "La ubicación no puede exceder 100 caracteres")
    private String ubicacion;

    /**
     * Estado del insumo.
     */
    @NotNull(message = "El estado es obligatorio")
    private EstadoInsumo estado;

    /**
     * Indica si requiere refrigeración.
     */
    private Boolean requiereRefrigeracion;

    /**
     * Indica si requiere receta médica para su uso.
     */
    private Boolean requiereReceta;

    /**
     * Observaciones sobre el insumo.
     */
    @Size(max = 1000, message = "Las observaciones no pueden exceder 1000 caracteres")
    private String observaciones;

    /**
     * Indica si el insumo está activo.
     */
    private Boolean activo;
}
