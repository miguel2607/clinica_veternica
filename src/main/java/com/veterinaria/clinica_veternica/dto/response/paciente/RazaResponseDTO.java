package com.veterinaria.clinica_veternica.dto.response.paciente;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO de Response para una Raza.
 *
 * @author Clínica Veterinaria Team
 * @version 1.0
 * @since 2025-11-03
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RazaResponseDTO {

    /**
     * Identificador único de la raza.
     */
    private Long idRaza;

    /**
     * Nombre de la raza.
     */
    private String nombre;

    /**
     * Descripción de la raza.
     */
    private String descripcion;

    /**
     * Características típicas de la raza.
     */
    private String caracteristicas;

    /**
     * Tamaño típico de la raza (Pequeño, Mediano, Grande, Extra Grande).
     */
    private String tamanio;

    /**
     * Peso promedio en kg.
     */
    private Double pesoPromedio;

    /**
     * Indica si la raza está activa.
     */
    private Boolean activo;

    /**
     * Información básica de la especie a la que pertenece.
     */
    private EspecieSimpleDTO especie;

    /**
     * Cantidad de mascotas de esta raza.
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

    /**
     * DTO simplificado de Especie para evitar referencias circulares.
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class EspecieSimpleDTO {
        private Long idEspecie;
        private String nombre;
    }
}
