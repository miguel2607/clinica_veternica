package com.veterinaria.clinica_veternica.patterns.behavioral.state;

import com.veterinaria.clinica_veternica.domain.agenda.Cita;
import com.veterinaria.clinica_veternica.domain.agenda.EstadoCita;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests para el patrón State - CitaState
 */
@SpringBootTest
class CitaStateTest {

    @Autowired
    private CitaProgramadaState citaProgramadaState;

    @Autowired
    private CitaConfirmadaState citaConfirmadaState;

    @Autowired
    private CitaAtendidaState citaAtendidaState;

    @Autowired
    private CitaCanceladaState citaCanceladaState;

    private Cita cita;

    @BeforeEach
    void setUp() {
        cita = Cita.builder()
                .idCita(1L)
                .fechaCita(LocalDate.now().plusDays(1))
                .horaCita(LocalTime.of(10, 0))
                .motivoConsulta("Consulta de rutina")
                .estado(EstadoCita.PROGRAMADA)
                .build();
    }

    // ========== CitaProgramadaState Tests ==========

    @Test
    @DisplayName("State Programada - Debe confirmar cita exitosamente")
    void programadaDebeConfirmarCitaExitosamente() {
        citaProgramadaState.confirmar(cita);

        assertEquals(EstadoCita.CONFIRMADA, cita.getEstado());
        assertNotNull(cita.getFechaConfirmacion());
    }

    @Test
    @DisplayName("State Programada - Debe cancelar cita exitosamente")
    void programadaDebeCancelarCitaExitosamente() {
        String motivo = "Cambio de planes";
        citaProgramadaState.cancelar(cita, motivo);

        assertEquals(EstadoCita.CANCELADA, cita.getEstado());
        assertEquals(motivo, cita.getMotivoCancelacion());
        assertNotNull(cita.getFechaCancelacion());
    }

    @Test
    @DisplayName("State Programada - No debe permitir atender sin confirmar")
    void programadaNoDebePermitirAtenderSinConfirmar() {
        assertThrows(IllegalStateException.class, () -> {
            citaProgramadaState.atender(cita);
        });
    }

    @Test
    @DisplayName("State Programada - Debe obtener nombre del estado")
    void programadaDebeObtenerNombreEstado() {
        assertEquals("PROGRAMADA", citaProgramadaState.getNombreEstado());
    }

    // ========== CitaConfirmadaState Tests ==========

    @Test
    @DisplayName("State Confirmada - Debe atender cita exitosamente")
    void confirmadaDebeAtenderCitaExitosamente() {
        cita.setEstado(EstadoCita.CONFIRMADA);
        citaConfirmadaState.atender(cita);

        assertEquals(EstadoCita.ATENDIDA, cita.getEstado());
    }

    @Test
    @DisplayName("State Confirmada - Debe cancelar cita exitosamente")
    void confirmadaDebeCancelarCitaExitosamente() {
        cita.setEstado(EstadoCita.CONFIRMADA);
        String motivo = "Emergencia";
        citaConfirmadaState.cancelar(cita, motivo);

        assertEquals(EstadoCita.CANCELADA, cita.getEstado());
    }

    @Test
    @DisplayName("State Confirmada - Debe obtener nombre del estado")
    void confirmadaDebeObtenerNombreEstado() {
        assertEquals("CONFIRMADA", citaConfirmadaState.getNombreEstado());
    }

    // ========== CitaAtendidaState Tests ==========

    @Test
    @DisplayName("State Atendida - No debe permitir confirmar")
    void atendidaNoDebePermitirConfirmar() {
        cita.setEstado(EstadoCita.ATENDIDA);
        
        assertThrows(IllegalStateException.class, () -> {
            citaAtendidaState.confirmar(cita);
        });
    }

    @Test
    @DisplayName("State Atendida - No debe permitir cancelar")
    void atendidaNoDebePermitirCancelar() {
        cita.setEstado(EstadoCita.ATENDIDA);
        
        assertThrows(IllegalStateException.class, () -> {
            citaAtendidaState.cancelar(cita, "Motivo");
        });
    }

    @Test
    @DisplayName("State Atendida - Debe obtener nombre del estado")
    void atendidaDebeObtenerNombreEstado() {
        assertEquals("ATENDIDA", citaAtendidaState.getNombreEstado());
    }

    // ========== CitaCanceladaState Tests ==========

    @Test
    @DisplayName("State Cancelada - No debe permitir confirmar")
    void canceladaNoDebePermitirConfirmar() {
        cita.setEstado(EstadoCita.CANCELADA);
        
        assertThrows(IllegalStateException.class, () -> {
            citaCanceladaState.confirmar(cita);
        });
    }

    @Test
    @DisplayName("State Cancelada - No debe permitir atender")
    void canceladaNoDebePermitirAtender() {
        cita.setEstado(EstadoCita.CANCELADA);
        
        assertThrows(IllegalStateException.class, () -> {
            citaCanceladaState.atender(cita);
        });
    }

    @Test
    @DisplayName("State Cancelada - Debe obtener nombre del estado")
    void canceladaDebeObtenerNombreEstado() {
        assertEquals("CANCELADA", citaCanceladaState.getNombreEstado());
    }
}

