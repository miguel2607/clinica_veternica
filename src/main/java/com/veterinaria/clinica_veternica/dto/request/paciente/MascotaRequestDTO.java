package com.veterinaria.clinica_veternica.dto.request.paciente;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * DTO de Request para crear/actualizar una Mascota.
 *
 * @author Clínica Veterinaria Team
 * @version 1.0
 * @since 2025-11-03
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MascotaRequestDTO {

    /**
     * Nombre de la mascota.
     */
    @NotBlank(message = "El nombre de la mascota es obligatorio")
    @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres")
    private String nombre;

    /**
     * Sexo de la mascota (Macho, Hembra).
     */
    @NotBlank(message = "El sexo es obligatorio")
    @Pattern(regexp = "^(Macho|Hembra)$", message = "El sexo debe ser 'Macho' o 'Hembra'")
    private String sexo;

    /**
     * Fecha de nacimiento de la mascota.
     */
    @Past(message = "La fecha de nacimiento debe ser en el pasado")
    private LocalDate fechaNacimiento;

    /**
     * Color o colores del pelaje/plumaje/escamas.
     */
    @Size(max = 100, message = "El color no puede exceder 100 caracteres")
    private String color;

    /**
     * Peso actual en kilogramos.
     */
    @Positive(message = "El peso debe ser un valor positivo")
    private Double peso;

    /**
     * Número de microchip (identificación única internacional).
     */
    @Size(max = 20, message = "El número de microchip no puede exceder 20 caracteres")
    private String numeroMicrochip;

    /**
     * Indica si la mascota está esterilizada/castrada.
     */
    private Boolean esterilizado;

    /**
     * Observaciones generales sobre el comportamiento o características especiales.
     * NOTA: Las alergias y enfermedades crónicas se registran en HistoriaClinica.
     * Si la mascota falleció, incluir esa información aquí.
     */
    @Size(max = 2000, message = "Las observaciones no pueden exceder 2000 caracteres")
    private String observaciones;

    /**
     * URL de la foto de la mascota.
     */
    @Size(max = 255, message = "La URL de la foto no puede exceder 255 caracteres")
    private String fotoUrl;

    /**
     * ID del propietario de la mascota.
     * Opcional para propietarios autenticados (se asigna automáticamente).
     * Obligatorio para ADMIN, RECEPCIONISTA y VETERINARIO.
     */
    @Positive(message = "El ID del propietario debe ser positivo")
    private Long idPropietario;

    /**
     * ID de la especie de la mascota.
     */
    @NotNull(message = "El ID de la especie es obligatorio")
    @Positive(message = "El ID de la especie debe ser positivo")
    private Long idEspecie;

    /**
     * ID de la raza de la mascota (opcional).
     */
    @Positive(message = "El ID de la raza debe ser positivo")
    private Long idRaza;

    /**
     * Indica si la mascota está activa.
     */
    private Boolean activo;
}
