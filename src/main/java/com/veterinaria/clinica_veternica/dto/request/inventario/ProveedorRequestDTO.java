package com.veterinaria.clinica_veternica.dto.request.inventario;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de Request para crear/actualizar un Proveedor.
 *
 * @author Clínica Veterinaria Team
 * @version 1.0
 * @since 2025-11-04
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProveedorRequestDTO {

    /**
     * Nombre de la empresa proveedora.
     */
    @NotBlank(message = "El nombre de la empresa es obligatorio")
    @Size(min = 2, max = 200, message = "El nombre debe tener entre 2 y 200 caracteres")
    private String nombreEmpresa;

    /**
     * Documento de identificación (RUC, NIT, etc.).
     */
    @NotBlank(message = "El documento es obligatorio")
    @Size(min = 6, max = 20, message = "El documento debe tener entre 6 y 20 caracteres")
    private String documento;

    /**
     * Tipo de documento (RUC, NIT, DNI, etc.).
     */
    @Size(max = 30, message = "El tipo de documento no puede exceder 30 caracteres")
    private String tipoDocumento;

    /**
     * Nombre de la persona de contacto.
     */
    @Size(max = 150, message = "El nombre de contacto no puede exceder 150 caracteres")
    private String nombreContacto;

    /**
     * Teléfono principal.
     */
    @NotBlank(message = "El teléfono es obligatorio")
    @Pattern(regexp = "^[+]?[0-9]{7,15}$", message = "Formato de teléfono inválido")
    private String telefono;

    /**
     * Teléfono secundario.
     */
    @Pattern(regexp = "^[+]?[0-9]{7,15}$", message = "Formato de teléfono inválido")
    private String telefonoSecundario;

    /**
     * Correo electrónico.
     */
    @NotBlank(message = "El email es obligatorio")
    @Email(message = "Formato de email inválido")
    @Size(max = 100, message = "El email no puede exceder 100 caracteres")
    private String email;

    /**
     * Dirección física.
     */
    @Size(max = 300, message = "La dirección no puede exceder 300 caracteres")
    private String direccion;

    /**
     * Ciudad.
     */
    @Size(max = 100, message = "La ciudad no puede exceder 100 caracteres")
    private String ciudad;

    /**
     * País.
     */
    @Size(max = 100, message = "El país no puede exceder 100 caracteres")
    private String pais;

    /**
     * Sitio web del proveedor.
     */
    @Size(max = 200, message = "El sitio web no puede exceder 200 caracteres")
    private String sitioWeb;

    /**
     * Categorías de productos que ofrece.
     */
    @Size(max = 500, message = "Las categorías no pueden exceder 500 caracteres")
    private String categoriasProductos;

    /**
     * Condiciones de pago acordadas.
     */
    @Size(max = 200, message = "Las condiciones de pago no pueden exceder 200 caracteres")
    private String condicionesPago;

    /**
     * Tiempo de entrega estimado en días.
     */
    @Min(value = 0, message = "El tiempo de entrega no puede ser negativo")
    private Integer tiempoEntregaDias;

    /**
     * Calificación del proveedor (1-5 estrellas).
     */
    @Min(value = 1, message = "La calificación mínima es 1")
    @Max(value = 5, message = "La calificación máxima es 5")
    private Integer calificacion;

    /**
     * Observaciones sobre el proveedor.
     */
    @Size(max = 1000, message = "Las observaciones no pueden exceder 1000 caracteres")
    private String observaciones;

    /**
     * Indica si el proveedor está activo.
     */
    private Boolean activo;
}
