package com.veterinaria.clinica_veternica.patterns.behavioral.memento;

import com.veterinaria.clinica_veternica.domain.clinico.HistoriaClinica;
import lombok.extern.slf4j.Slf4j;

/**
 * Patrón Memento: HistoriaClinicaOriginator
 *
 * Crea y restaura mementos de historias clínicas.
 * Es responsable de crear snapshots y restaurar desde mementos.
 *
 * Justificación:
 * - Encapsula la lógica de creación y restauración de mementos
 * - Mantiene el encapsulamiento de HistoriaClinica
 * - Facilita la implementación del patrón Memento
 *
 * @author Clínica Veterinaria Team
 * @version 1.0
 * @since 2025-11-04
 */
@Slf4j
public class HistoriaClinicaOriginator {

    /**
     * Crea un memento del estado actual de la historia clínica.
     *
     * @param historiaClinica Historia clínica de la cual crear el memento
     * @return Memento con el estado actual
     */
    public HistoriaClinicaMemento crearMemento(HistoriaClinica historiaClinica) {
        log.debug("Creando memento de historia clínica ID: {}", historiaClinica.getIdHistoriaClinica());

        return HistoriaClinicaMemento.builder()
                .idHistoriaClinica(historiaClinica.getIdHistoriaClinica())
                .numeroHistoria(historiaClinica.getNumeroHistoria())
                .antecedentesMedicos(historiaClinica.getAntecedentesMedicos())
                .antecedentesQuirurgicos(historiaClinica.getAntecedentesQuirurgicos())
                .alergias(historiaClinica.getAlergias())
                .enfermedadesCronicas(historiaClinica.getEnfermedadesCronicas())
                .medicamentosActuales(historiaClinica.getMedicamentosActuales())
                .observacionesGenerales(historiaClinica.getObservacionesGenerales())
                .fechaCreacion(historiaClinica.getFechaCreacion())
                .build();
    }

    /**
     * Restaura el estado de una historia clínica desde un memento.
     *
     * @param historiaClinica Historia clínica a restaurar
     * @param memento Memento con el estado a restaurar
     */
    public void restaurarDesdeMemento(HistoriaClinica historiaClinica, HistoriaClinicaMemento memento) {
        log.info("Restaurando historia clínica ID: {} desde memento del {}", 
                historiaClinica.getIdHistoriaClinica(), memento.getFechaSnapshot());

        if (!historiaClinica.getIdHistoriaClinica().equals(memento.getIdHistoriaClinica())) {
            throw new IllegalArgumentException("El memento no corresponde a esta historia clínica");
        }

        historiaClinica.setAntecedentesMedicos(memento.getAntecedentesMedicos());
        historiaClinica.setAntecedentesQuirurgicos(memento.getAntecedentesQuirurgicos());
        historiaClinica.setAlergias(memento.getAlergias());
        historiaClinica.setEnfermedadesCronicas(memento.getEnfermedadesCronicas());
        historiaClinica.setMedicamentosActuales(memento.getMedicamentosActuales());
        historiaClinica.setObservacionesGenerales(memento.getObservacionesGenerales());

        log.info("Historia clínica restaurada exitosamente");
    }
}

