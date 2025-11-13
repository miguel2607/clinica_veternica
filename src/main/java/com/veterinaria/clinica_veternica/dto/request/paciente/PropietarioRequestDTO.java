package com.veterinaria.clinica_veternica.dto.request.paciente;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;



/**
 * DTO de Request para crear/actualizar un Propietario.
 *
 * @author Clínica Veterinaria Team
 * @version 1.0
 * @since 2025-11-03
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PropietarioRequestDTO {

    /**
     * Número de documento de identificación.
     */
    @NotBlank(message = "El documento es obligatorio")
    @Size(min = 6, max = 20, message = "El documento debe tener entre 6 y 20 caracteres")
    private String documento;

    /**
     * Tipo de documento (DNI, Cédula, Pasaporte, etc.).
     */
    @NotBlank(message = "El tipo de documento es obligatorio")
    @Size(max = 30, message = "El tipo de documento no puede exceder 30 caracteres")
    private String tipoDocumento;

    /**
     * Nombres del propietario.
     */
    @NotBlank(message = "Los nombres son obligatorios")
    @Size(min = 2, max = 100, message = "Los nombres deben tener entre 2 y 100 caracteres")
    private String nombres;

    /**
     * Apellidos del propietario.
     */
    @NotBlank(message = "Los apellidos son obligatorios")
    @Size(min = 2, max = 100, message = "Los apellidos deben tener entre 2 y 100 caracteres")
    private String apellidos;

    /**
     * Teléfono principal de contacto.
     */
    @NotBlank(message = "El teléfono es obligatorio")
    @Pattern(regexp = "^[+]?\\d{7,15}$", message = "Formato de teléfono inválido")
    private String telefono;

    /**
     * Correo electrónico del propietario.
     */
    @NotBlank(message = "El email es obligatorio")
    @Email(message = "Formato de email inválido")
    @Size(max = 100, message = "El email no puede exceder 100 caracteres")
    private String email;

    /**
     * Dirección de residencia completa.
     * NOTA: Incluir ciudad y código postal aquí si es necesario.
     * Ejemplo: "Calle Principal 123, Lima, 15001"
     */
    @Size(max = 300, message = "La dirección no puede exceder 300 caracteres")
    private String direccion;

    /**
     * Observaciones o notas adicionales sobre el propietario.
     */
    @Size(max = 500, message = "Las observaciones no pueden exceder 500 caracteres")
    private String observaciones;

    /**
     * Indica si el propietario está activo.
     */
    private Boolean activo;
}
