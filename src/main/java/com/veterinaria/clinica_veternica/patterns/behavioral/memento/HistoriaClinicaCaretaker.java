package com.veterinaria.clinica_veternica.patterns.behavioral.memento;

import com.veterinaria.clinica_veternica.domain.clinico.HistoriaClinica;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Patrón Memento: HistoriaClinicaCaretaker
 *
 * Gestiona y almacena los mementos de historias clínicas.
 * Mantiene un historial de snapshots y permite restaurar estados previos.
 *
 * Justificación:
 * - Gestiona el ciclo de vida de los mementos
 * - Mantiene un historial de cambios
 * - Permite restaurar a estados previos
 *
 * @author Clínica Veterinaria Team
 * @version 1.0
 * @since 2025-11-04
 */
@Slf4j
@Component
public class HistoriaClinicaCaretaker {

    private final Map<Long, List<HistoriaClinicaMemento>> historiales = new HashMap<>();
    private final HistoriaClinicaOriginator originator = new HistoriaClinicaOriginator();
    private static final int MAX_MEMENTOS_POR_HISTORIA = 10;

    /**
     * Guarda un memento de la historia clínica.
     *
     * @param historiaClinica Historia clínica de la cual guardar el memento
     */
    public void guardarMemento(HistoriaClinica historiaClinica) {
        Long id = historiaClinica.getIdHistoriaClinica();
        HistoriaClinicaMemento memento = originator.crearMemento(historiaClinica);

        historiales.computeIfAbsent(id, k -> new ArrayList<>()).add(memento);

        // Limitar el número de mementos por historia
        List<HistoriaClinicaMemento> mementos = historiales.get(id);
        if (mementos.size() > MAX_MEMENTOS_POR_HISTORIA) {
            mementos.remove(0); // Eliminar el más antiguo
        }

        log.info("Memento guardado para historia clínica ID: {}. Total mementos: {}", 
                id, mementos.size());
    }

    /**
     * Restaura la historia clínica al último memento guardado.
     *
     * @param historiaClinica Historia clínica a restaurar
     * @return true si se restauró exitosamente
     */
    public boolean restaurarUltimoMemento(HistoriaClinica historiaClinica) {
        Long id = historiaClinica.getIdHistoriaClinica();
        List<HistoriaClinicaMemento> mementos = historiales.get(id);

        if (mementos == null || mementos.isEmpty()) {
            log.warn("No hay mementos para restaurar en historia clínica ID: {}", id);
            return false;
        }

        HistoriaClinicaMemento ultimoMemento = mementos.get(mementos.size() - 1);
        originator.restaurarDesdeMemento(historiaClinica, ultimoMemento);
        return true;
    }

    /**
     * Restaura la historia clínica a un memento específico.
     *
     * @param historiaClinica Historia clínica a restaurar
     * @param indice Índice del memento en el historial
     * @return true si se restauró exitosamente
     */
    public boolean restaurarMemento(HistoriaClinica historiaClinica, int indice) {
        Long id = historiaClinica.getIdHistoriaClinica();
        List<HistoriaClinicaMemento> mementos = historiales.get(id);

        if (mementos == null || indice < 0 || indice >= mementos.size()) {
            log.warn("Índice de memento inválido: {} para historia clínica ID: {}", indice, id);
            return false;
        }

        HistoriaClinicaMemento memento = mementos.get(indice);
        originator.restaurarDesdeMemento(historiaClinica, memento);
        return true;
    }

    /**
     * Obtiene el historial de mementos de una historia clínica.
     *
     * @param idHistoriaClinica ID de la historia clínica
     * @return Lista de mementos (inmutable)
     */
    public List<HistoriaClinicaMemento> obtenerHistorial(Long idHistoriaClinica) {
        List<HistoriaClinicaMemento> mementos = historiales.get(idHistoriaClinica);
        return mementos != null ? Collections.unmodifiableList(mementos) : Collections.emptyList();
    }

    /**
     * Obtiene el número de mementos guardados para una historia clínica.
     *
     * @param idHistoriaClinica ID de la historia clínica
     * @return Número de mementos
     */
    public int obtenerCantidadMementos(Long idHistoriaClinica) {
        List<HistoriaClinicaMemento> mementos = historiales.get(idHistoriaClinica);
        return mementos != null ? mementos.size() : 0;
    }
}

