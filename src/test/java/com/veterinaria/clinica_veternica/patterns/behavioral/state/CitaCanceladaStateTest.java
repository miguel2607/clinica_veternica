package com.veterinaria.clinica_veternica.patterns.behavioral.state;

import com.veterinaria.clinica_veternica.domain.agenda.Cita;
import com.veterinaria.clinica_veternica.domain.agenda.EstadoCita;
import com.veterinaria.clinica_veternica.domain.paciente.Mascota;
import com.veterinaria.clinica_veternica.domain.usuario.Veterinario;
import com.veterinaria.clinica_veternica.domain.agenda.Servicio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class CitaCanceladaStateTest {

    @Mock
    private Mascota mascota;

    @Mock
    private Veterinario veterinario;

    @Mock
    private Servicio servicio;

    private CitaCanceladaState estado;
    private Cita cita;

    @BeforeEach
    void setUp() {
        estado = new CitaCanceladaState();

        cita = Cita.builder()
                .idCita(1L)
                .mascota(mascota)
                .veterinario(veterinario)
                .servicio(servicio)
                .fechaCita(LocalDate.now().plusDays(1))
                .horaCita(LocalTime.of(10, 0))
                .motivoConsulta("Consulta de rutina")
                .estado(EstadoCita.CANCELADA)
                .fechaCancelacion(LocalDateTime.now())
                .motivoCancelacion("Cliente canceló")
                .build();
    }

    @Test
    @DisplayName("State - Debe lanzar excepción al intentar confirmar cita CANCELADA")
    void debeLanzarExcepcionAlConfirmar() {
        IllegalStateException exception = assertThrows(
            IllegalStateException.class,
            () -> estado.confirmar(cita)
        );

        assertTrue(exception.getMessage().contains("fue cancelada"));
    }

    @Test
    @DisplayName("State - Debe lanzar excepción al intentar atender cita CANCELADA")
    void debeLanzarExcepcionAlAtender() {
        IllegalStateException exception = assertThrows(
            IllegalStateException.class,
            () -> estado.atender(cita)
        );

        assertTrue(exception.getMessage().contains("fue cancelada"));
    }

    @Test
    @DisplayName("State - No debe hacer nada al cancelar cita ya CANCELADA")
    void noDebeHacerNadaAlCancelarCitaYaCancelada() {
        String motivoOriginal = cita.getMotivoCancelacion();
        LocalDateTime fechaOriginal = cita.getFechaCancelacion();

        estado.cancelar(cita, "Nuevo motivo");

        // Debe mantener los valores originales
        assertEquals(EstadoCita.CANCELADA, cita.getEstado());
        assertEquals(motivoOriginal, cita.getMotivoCancelacion());
        assertEquals(fechaOriginal, cita.getFechaCancelacion());
    }

    @Test
    @DisplayName("State - Debe retornar nombre de estado correcto")
    void debeRetornarNombreEstado() {
        assertEquals("CANCELADA", estado.getNombreEstado());
    }

    @Test
    @DisplayName("State - Confirmar debe incluir mensaje apropiado en excepción")
    void confirmarDebeIncluirMensajeApropiadoEnExcepcion() {
        IllegalStateException exception = assertThrows(
            IllegalStateException.class,
            () -> estado.confirmar(cita)
        );

        assertTrue(exception.getMessage().toLowerCase().contains("confirmar"));
    }

    @Test
    @DisplayName("State - Atender debe incluir mensaje apropiado en excepción")
    void atenderDebeIncluirMensajeApropiadoEnExcepcion() {
        IllegalStateException exception = assertThrows(
            IllegalStateException.class,
            () -> estado.atender(cita)
        );

        assertTrue(exception.getMessage().toLowerCase().contains("atender"));
    }

    @Test
    @DisplayName("State - Estado debe permanecer cancelado después de intentar cancelar de nuevo")
    void estadoDebePermanecerCanceladoDespuesDeIntentarCancelarDeNuevo() {
        estado.cancelar(cita, "Otro motivo");

        assertEquals(EstadoCita.CANCELADA, cita.getEstado());
    }
}
