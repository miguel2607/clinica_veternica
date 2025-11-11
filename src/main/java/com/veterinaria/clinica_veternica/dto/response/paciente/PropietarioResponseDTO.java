package com.veterinaria.clinica_veternica.dto.response.paciente;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * DTO de Response para un Propietario.
 *
 * @author Clínica Veterinaria Team
 * @version 1.0
 * @since 2025-11-03
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PropietarioResponseDTO {

    /**
     * Identificador único del propietario.
     */
    private Long idPropietario;

    /**
     * Número de documento de identificación.
     */
    private String documento;

    /**
     * Tipo de documento.
     */
    private String tipoDocumento;

    /**
     * Nombres del propietario.
     */
    private String nombres;

    /**
     * Apellidos del propietario.
     */
    private String apellidos;

    /**
     * Nombre completo (nombres + apellidos).
     */
    private String nombreCompleto;

    /**
     * Fecha de nacimiento del propietario.
     */
    private LocalDate fechaNacimiento;

    /**
     * Edad en años.
     */
    private Integer edad;

    /**
     * Teléfono principal de contacto.
     */
    private String telefono;

    /**
     * Teléfono secundario de contacto.
     */
    private String telefonoSecundario;

    /**
     * Correo electrónico del propietario.
     */
    private String email;

    /**
     * Dirección de residencia.
     */
    private String direccion;

    /**
     * Ciudad de residencia.
     */
    private String ciudad;

    /**
     * Código postal.
     */
    private String codigoPostal;

    /**
     * Dirección completa (dirección, ciudad, código postal).
     */
    private String direccionCompleta;

    /**
     * Observaciones o notas adicionales.
     */
    private String observaciones;

    /**
     * Indica si el propietario está activo.
     */
    private Boolean activo;

    /**
     * Cantidad de mascotas registradas.
     */
    private Integer cantidadMascotas;

    /**
     * Fecha y hora de creación del registro.
     */
    private LocalDateTime fechaCreacion;

    /**
     * Fecha y hora de última modificación.
     */
    private LocalDateTime fechaModificacion;
}
