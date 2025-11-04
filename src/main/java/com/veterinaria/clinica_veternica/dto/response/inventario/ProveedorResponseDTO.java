package com.veterinaria.clinica_veternica.dto.response.inventario;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO de Response para un Proveedor.
 *
 * @author Clínica Veterinaria Team
 * @version 1.0
 * @since 2025-11-04
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProveedorResponseDTO {

    private Long idProveedor;
    private String nombreEmpresa;
    private String documento;
    private String tipoDocumento;
    private String nombreContacto;
    private String telefono;
    private String telefonoSecundario;
    private String email;
    private String direccion;
    private String ciudad;
    private String pais;
    private String sitioWeb;
    private String categoriasProductos;
    private String condicionesPago;
    private Integer tiempoEntregaDias;
    private Integer calificacion;
    private String observaciones;
    private Boolean activo;
    private Integer cantidadInsumos;
    private Boolean esConfiable;
    private Boolean tieneEntregaRapida;
    private String infoContacto;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaModificacion;
}
