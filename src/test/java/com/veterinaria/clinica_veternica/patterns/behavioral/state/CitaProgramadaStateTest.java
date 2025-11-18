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
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class CitaProgramadaStateTest {

    @Mock
    private Mascota mascota;

    @Mock
    private Veterinario veterinario;

    @Mock
    private Servicio servicio;

    private CitaProgramadaState estado;
    private Cita cita;

    @BeforeEach
    void setUp() {
        estado = new CitaProgramadaState();

        cita = Cita.builder()
                .idCita(1L)
                .mascota(mascota)
                .veterinario(veterinario)
                .servicio(servicio)
                .fechaCita(LocalDate.now().plusDays(1))
                .horaCita(LocalTime.of(10, 0))
                .motivoConsulta("Consulta de rutina")
                .estado(EstadoCita.PROGRAMADA)
                .build();
    }

    @Test
    @DisplayName("State - Debe confirmar cita desde estado PROGRAMADA")
    void debeConfirmarCita() {
        estado.confirmar(cita);

        assertEquals(EstadoCita.CONFIRMADA, cita.getEstado());
        assertNotNull(cita.getFechaConfirmacion());
    }

    @Test
    @DisplayName("State - Debe cancelar cita desde estado PROGRAMADA")
    void debeCancelarCita() {
        String motivo = "Cambio de planes";
        estado.cancelar(cita, motivo);

        assertEquals(EstadoCita.CANCELADA, cita.getEstado());
        assertEquals(motivo, cita.getMotivoCancelacion());
        assertNotNull(cita.getFechaCancelacion());
    }

    @Test
    @DisplayName("State - Debe lanzar excepción al intentar atender cita PROGRAMADA")
    void debeLanzarExcepcionAlAtender() {
        assertThrows(IllegalStateException.class, () -> estado.atender(cita));
    }

    @Test
    @DisplayName("State - Debe retornar nombre de estado correcto")
    void debeRetornarNombreEstado() {
        assertEquals("PROGRAMADA", estado.getNombreEstado());
    }
}

