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
class CitaAtendidaStateTest {

    @Mock
    private Mascota mascota;

    @Mock
    private Veterinario veterinario;

    @Mock
    private Servicio servicio;

    private CitaAtendidaState estado;
    private Cita cita;

    @BeforeEach
    void setUp() {
        estado = new CitaAtendidaState();

        cita = Cita.builder()
                .idCita(1L)
                .mascota(mascota)
                .veterinario(veterinario)
                .servicio(servicio)
                .fechaCita(LocalDate.now())
                .horaCita(LocalTime.of(10, 0))
                .motivoConsulta("Consulta de rutina")
                .estado(EstadoCita.ATENDIDA)
                .fechaHoraInicioAtencion(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("State - Debe lanzar excepción al intentar confirmar cita ATENDIDA")
    void debeLanzarExcepcionAlConfirmar() {
        IllegalStateException exception = assertThrows(
            IllegalStateException.class,
            () -> estado.confirmar(cita)
        );

        assertTrue(exception.getMessage().contains("ya fue atendida"));
    }

    @Test
    @DisplayName("State - Debe lanzar excepción al intentar cancelar cita ATENDIDA")
    void debeLanzarExcepcionAlCancelar() {
        IllegalStateException exception = assertThrows(
            IllegalStateException.class,
            () -> estado.cancelar(cita, "Motivo")
        );

        assertTrue(exception.getMessage().contains("ya fue atendida"));
    }

    @Test
    @DisplayName("State - Debe finalizar atención si ya está en curso")
    void debeFinalizarAtencionSiYaEstaEnCurso() {
        // Configurar cita con inicio de atención pero sin fin
        cita.setFechaHoraInicioAtencion(LocalDateTime.now().minusMinutes(30));
        cita.setFechaHoraFinAtencion(null);

        estado.atender(cita);

        assertNotNull(cita.getFechaHoraFinAtencion());
    }

    @Test
    @DisplayName("State - No debe hacer nada si atención ya fue completada")
    void noDebeHacerNadaSiAtencionYaFueCompletada() {
        // Configurar cita completamente atendida
        LocalDateTime inicio = LocalDateTime.now().minusMinutes(30);
        LocalDateTime fin = LocalDateTime.now();
        cita.setFechaHoraInicioAtencion(inicio);
        cita.setFechaHoraFinAtencion(fin);

        estado.atender(cita);

        // No debe cambiar las fechas
        assertEquals(fin, cita.getFechaHoraFinAtencion());
    }

    @Test
    @DisplayName("State - Debe retornar nombre de estado correcto")
    void debeRetornarNombreEstado() {
        assertEquals("ATENDIDA", estado.getNombreEstado());
    }

    @Test
    @DisplayName("State - Debe establecer fecha fin cuando se finaliza atención")
    void debeEstablecerFechaFinCuandoSeFinalizaAtencion() {
        cita.setFechaHoraInicioAtencion(LocalDateTime.now().minusMinutes(30));
        cita.setFechaHoraFinAtencion(null);

        assertNull(cita.getFechaHoraFinAtencion());

        estado.atender(cita);

        assertNotNull(cita.getFechaHoraFinAtencion());
    }

    @Test
    @DisplayName("State - Fecha fin debe ser posterior a fecha inicio")
    void fechaFinDebeSerPosteriorAFechaInicio() {
        LocalDateTime inicio = LocalDateTime.now().minusMinutes(30);
        cita.setFechaHoraInicioAtencion(inicio);
        cita.setFechaHoraFinAtencion(null);

        estado.atender(cita);

        assertTrue(cita.getFechaHoraFinAtencion().isAfter(inicio));
    }
}
