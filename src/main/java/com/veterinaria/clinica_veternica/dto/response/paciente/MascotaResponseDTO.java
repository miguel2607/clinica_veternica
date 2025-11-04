package com.veterinaria.clinica_veternica.dto.response.paciente;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * DTO de Response para una Mascota.
 *
 * @author Clínica Veterinaria Team
 * @version 1.0
 * @since 2025-11-03
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MascotaResponseDTO {

    /**
     * Identificador único de la mascota.
     */
    private Long idMascota;

    /**
     * Nombre de la mascota.
     */
    private String nombre;

    /**
     * Sexo de la mascota.
     */
    private String sexo;

    /**
     * Fecha de nacimiento de la mascota.
     */
    private LocalDate fechaNacimiento;

    /**
     * Edad en años (calculada).
     */
    private Integer edad;

    /**
     * Edad en formato legible (ej: "3 años y 2 meses").
     */
    private String edadFormateada;

    /**
     * Color o colores del pelaje/plumaje/escamas.
     */
    private String color;

    /**
     * Peso actual en kilogramos.
     */
    private Double peso;

    /**
     * Talla o tamaño.
     */
    private String talla;

    /**
     * Número de microchip.
     */
    private String numeroMicrochip;

    /**
     * Indica si está esterilizada/castrada.
     */
    private Boolean esterilizado;

    /**
     * Alergias conocidas.
     */
    private String alergias;

    /**
     * Enfermedades crónicas.
     */
    private String enfermedadesCronicas;

    /**
     * Observaciones generales.
     */
    private String observaciones;

    /**
     * URL de la foto de la mascota.
     */
    private String fotoUrl;

    /**
     * Indica si está activa.
     */
    private Boolean activo;

    /**
     * Fecha de fallecimiento (si aplica).
     */
    private LocalDate fechaFallecimiento;

    /**
     * Causa de fallecimiento (si aplica).
     */
    private String causaFallecimiento;

    /**
     * Información del propietario.
     */
    private PropietarioSimpleDTO propietario;

    /**
     * Información de la especie.
     */
    private EspecieSimpleDTO especie;

    /**
     * Información de la raza.
     */
    private RazaSimpleDTO raza;

    /**
     * Indica si tiene historia clínica creada.
     */
    private Boolean tieneHistoriaClinica;

    /**
     * Cantidad de citas registradas.
     */
    private Integer cantidadCitas;

    /**
     * Fecha y hora de creación del registro.
     */
    private LocalDateTime fechaCreacion;

    /**
     * Fecha y hora de última modificación.
     */
    private LocalDateTime fechaModificacion;

    /**
     * DTO simplificado de Propietario.
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PropietarioSimpleDTO {
        private Long idPropietario;
        private String nombreCompleto;
        private String telefono;
        private String email;
    }

    /**
     * DTO simplificado de Especie.
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class EspecieSimpleDTO {
        private Long idEspecie;
        private String nombre;
    }

    /**
     * DTO simplificado de Raza.
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class RazaSimpleDTO {
        private Long idRaza;
        private String nombre;
    }
}
