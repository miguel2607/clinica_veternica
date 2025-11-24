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
class CitaConfirmadaStateTest {

    @Mock
    private Mascota mascota;

    @Mock
    private Veterinario veterinario;

    @Mock
    private Servicio servicio;

    private CitaConfirmadaState estado;
    private Cita cita;

    @BeforeEach
    void setUp() {
        estado = new CitaConfirmadaState();

        cita = Cita.builder()
                .idCita(1L)
                .mascota(mascota)
                .veterinario(veterinario)
                .servicio(servicio)
                .fechaCita(LocalDate.now().plusDays(1))
                .horaCita(LocalTime.of(10, 0))
                .motivoConsulta("Consulta de rutina")
                .estado(EstadoCita.CONFIRMADA)
                .build();
    }

    @Test
    @DisplayName("State - Debe atender cita desde estado CONFIRMADA")
    void debeAtenderCita() {
        estado.atender(cita);

        assertEquals(EstadoCita.ATENDIDA, cita.getEstado());
        assertNotNull(cita.getFechaHoraInicioAtencion());
    }

    @Test
    @DisplayName("State - Debe cancelar cita desde estado CONFIRMADA")
    void debeCancelarCita() {
        String motivo = "Cliente canceló";
        estado.cancelar(cita, motivo);

        assertEquals(EstadoCita.CANCELADA, cita.getEstado());
        assertEquals(motivo, cita.getMotivoCancelacion());
        assertNotNull(cita.getFechaCancelacion());
    }

    @Test
    @DisplayName("State - No debe hacer nada al confirmar cita ya CONFIRMADA")
    void noDebeHacerNadaAlConfirmarCitaYaConfirmada() {
        estado.confirmar(cita);

        // Debe permanecer en el mismo estado
        assertEquals(EstadoCita.CONFIRMADA, cita.getEstado());
    }

    @Test
    @DisplayName("State - Debe retornar nombre de estado correcto")
    void debeRetornarNombreEstado() {
        assertEquals("CONFIRMADA", estado.getNombreEstado());
    }

    @Test
    @DisplayName("State - Debe establecer fecha de inicio de atención al atender")
    void debeEstablecerFechaInicioAtencionAlAtender() {
        assertNull(cita.getFechaHoraInicioAtencion());

        estado.atender(cita);

        assertNotNull(cita.getFechaHoraInicioAtencion());
    }

    @Test
    @DisplayName("State - Debe establecer fecha de cancelación al cancelar")
    void debeEstablecerFechaCancelacionAlCancelar() {
        assertNull(cita.getFechaCancelacion());

        estado.cancelar(cita, "Motivo de prueba");

        assertNotNull(cita.getFechaCancelacion());
    }

    @Test
    @DisplayName("State - Debe guardar motivo de cancelación")
    void debeGuardarMotivoCancelacion() {
        String motivo = "El propietario tuvo una emergencia";

        estado.cancelar(cita, motivo);

        assertEquals(motivo, cita.getMotivoCancelacion());
    }
}
