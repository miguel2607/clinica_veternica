package com.veterinaria.clinica_veternica.patterns.behavioral.memento;

import com.veterinaria.clinica_veternica.domain.clinico.HistoriaClinica;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests para el patrón Memento - HistoriaClinicaMemento
 */
class HistoriaClinicaMementoTest {

    private HistoriaClinica historiaClinica;
    private HistoriaClinicaOriginator originator;
    private HistoriaClinicaCaretaker caretaker;

    @BeforeEach
    void setUp() {
        historiaClinica = HistoriaClinica.builder()
                .idHistoriaClinica(1L)
                .numeroHistoria("HC-001")
                .antecedentesMedicos("Ninguno")
                .antecedentesQuirurgicos("Ninguno")
                .alergias("Ninguna")
                .enfermedadesCronicas("Ninguna")
                .medicamentosActuales("Ninguno")
                .observacionesGenerales("Mascota saludable")
                .fechaCreacion(LocalDateTime.now())
                .build();

        originator = new HistoriaClinicaOriginator();
        caretaker = new HistoriaClinicaCaretaker();
    }

    @Test
    @DisplayName("Debe crear un memento exitosamente")
    void debeCrearMementoExitosamente() {
        HistoriaClinicaMemento memento = originator.crearMemento(historiaClinica);

        assertNotNull(memento);
        assertEquals(historiaClinica.getIdHistoriaClinica(), memento.getIdHistoriaClinica());
        assertEquals(historiaClinica.getNumeroHistoria(), memento.getNumeroHistoria());
        assertEquals(historiaClinica.getAntecedentesMedicos(), memento.getAntecedentesMedicos());
        assertEquals(historiaClinica.getAlergias(), memento.getAlergias());
        assertNotNull(memento.getFechaSnapshot());
    }

    @Test
    @DisplayName("Debe restaurar historia clínica desde memento")
    void debeRestaurarHistoriaClinicaDesdeMemento() {
        // Guardar estado original
        HistoriaClinicaMemento memento = originator.crearMemento(historiaClinica);

        // Modificar la historia clínica
        historiaClinica.setAntecedentesMedicos("Diabetes");
        historiaClinica.setAlergias("Penicilina");
        historiaClinica.setObservacionesGenerales("Mascota con condiciones especiales");

        // Verificar que cambió
        assertEquals("Diabetes", historiaClinica.getAntecedentesMedicos());
        assertEquals("Penicilina", historiaClinica.getAlergias());

        // Restaurar desde memento
        originator.restaurarDesdeMemento(historiaClinica, memento);

        // Verificar que se restauró
        assertEquals("Ninguno", historiaClinica.getAntecedentesMedicos());
        assertEquals("Ninguna", historiaClinica.getAlergias());
        assertEquals("Mascota saludable", historiaClinica.getObservacionesGenerales());
    }

    @Test
    @DisplayName("Debe lanzar excepción si el memento no corresponde a la historia clínica")
    void debeLanzarExcepcionSiMementoNoCorresponde() {
        HistoriaClinica otraHistoria = HistoriaClinica.builder()
                .idHistoriaClinica(2L)
                .numeroHistoria("HC-002")
                .build();

        HistoriaClinicaMemento memento = originator.crearMemento(historiaClinica);

        assertThrows(IllegalArgumentException.class, () -> {
            originator.restaurarDesdeMemento(otraHistoria, memento);
        }, "Debe lanzar excepción si el ID no coincide");
    }

    @Test
    @DisplayName("Caretaker - Debe guardar memento")
    void caretakerDebeGuardarMemento() {
        int cantidadInicial = caretaker.obtenerCantidadMementos(historiaClinica.getIdHistoriaClinica());

        caretaker.guardarMemento(historiaClinica);

        int cantidadFinal = caretaker.obtenerCantidadMementos(historiaClinica.getIdHistoriaClinica());
        assertEquals(cantidadInicial + 1, cantidadFinal);
    }

    @Test
    @DisplayName("Caretaker - Debe restaurar último memento")
    void caretakerDebeRestaurarUltimoMemento() {
        // Guardar estado inicial
        caretaker.guardarMemento(historiaClinica);

        // Modificar
        historiaClinica.setAntecedentesMedicos("Modificado");

        // Restaurar
        boolean restaurado = caretaker.restaurarUltimoMemento(historiaClinica);

        assertTrue(restaurado);
        assertEquals("Ninguno", historiaClinica.getAntecedentesMedicos());
    }

    @Test
    @DisplayName("Caretaker - Debe restaurar memento específico por índice")
    void caretakerDebeRestaurarMementoEspecifico() {
        // Guardar varios estados
        caretaker.guardarMemento(historiaClinica);
        historiaClinica.setAntecedentesMedicos("Estado 1");
        caretaker.guardarMemento(historiaClinica);
        historiaClinica.setAntecedentesMedicos("Estado 2");
        caretaker.guardarMemento(historiaClinica);

        // Restaurar al primer estado (índice 0)
        boolean restaurado = caretaker.restaurarMemento(historiaClinica, 0);

        assertTrue(restaurado);
        assertEquals("Ninguno", historiaClinica.getAntecedentesMedicos());
    }

    @Test
    @DisplayName("Caretaker - Debe retornar false si no hay mementos")
    void caretakerDebeRetornarFalseSiNoHayMementos() {
        boolean restaurado = caretaker.restaurarUltimoMemento(historiaClinica);

        assertFalse(restaurado);
    }

    @Test
    @DisplayName("Caretaker - Debe limitar número de mementos")
    void caretakerDebeLimitarNumeroDeMementos() {
        // Guardar más mementos que el límite (10)
        for (int i = 0; i < 15; i++) {
            historiaClinica.setAntecedentesMedicos("Estado " + i);
            caretaker.guardarMemento(historiaClinica);
        }

        int cantidad = caretaker.obtenerCantidadMementos(historiaClinica.getIdHistoriaClinica());
        assertTrue(cantidad <= 10, "No debe exceder el límite de 10 mementos");
    }

    @Test
    @DisplayName("Caretaker - Debe obtener historial de mementos")
    void caretakerDebeObtenerHistorialDeMementos() {
        caretaker.guardarMemento(historiaClinica);
        historiaClinica.setAntecedentesMedicos("Modificado");
        caretaker.guardarMemento(historiaClinica);

        var historial = caretaker.obtenerHistorial(historiaClinica.getIdHistoriaClinica());

        assertNotNull(historial);
        assertEquals(2, historial.size());
    }
}

