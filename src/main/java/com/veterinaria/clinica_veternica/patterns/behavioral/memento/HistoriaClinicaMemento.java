package com.veterinaria.clinica_veternica.patterns.behavioral.memento;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * Patrón Memento: HistoriaClinicaMemento
 *
 * Representa un snapshot del estado de una historia clínica en un momento dado.
 * Permite guardar y restaurar estados previos sin violar el encapsulamiento.
 *
 * Justificación:
 * - Permite deshacer cambios en historias clínicas
 * - Facilita auditoría y trazabilidad
 * - Mantiene el encapsulamiento de la historia clínica
 *
 * @author Clínica Veterinaria Team
 * @version 1.0
 * @since 2025-11-04
 */
@Getter
@Builder
public class HistoriaClinicaMemento {

    private final Long idHistoriaClinica;
    private final String numeroHistoria;
    private final String antecedentesMedicos;
    private final String antecedentesQuirurgicos;
    private final String alergias;
    private final String enfermedadesCronicas;
    private final String medicamentosActuales;
    private final String observacionesGenerales;
    private final LocalDateTime fechaCreacion;
    @Builder.Default
    private final LocalDateTime fechaSnapshot = LocalDateTime.now();
}

