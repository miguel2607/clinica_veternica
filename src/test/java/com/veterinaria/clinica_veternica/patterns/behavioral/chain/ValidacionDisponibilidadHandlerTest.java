package com.veterinaria.clinica_veternica.patterns.behavioral.chain;

import com.veterinaria.clinica_veternica.domain.agenda.Cita;
import com.veterinaria.clinica_veternica.domain.agenda.EstadoCita;
import com.veterinaria.clinica_veternica.domain.paciente.Mascota;
import com.veterinaria.clinica_veternica.domain.usuario.Veterinario;
import com.veterinaria.clinica_veternica.domain.agenda.Servicio;
import com.veterinaria.clinica_veternica.exception.ValidationException;
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
class ValidacionDisponibilidadHandlerTest {

    @Mock
    private Mascota mascota;

    @Mock
    private Veterinario veterinario;

    @Mock
    private Servicio servicio;

    private ValidacionDisponibilidadHandler handler;
    private Cita citaValida;

    @BeforeEach
    void setUp() {
        handler = new ValidacionDisponibilidadHandler();

        citaValida = Cita.builder()
                .idCita(1L)
                .mascota(mascota)
                .veterinario(veterinario)
                .servicio(servicio)
                .fechaCita(LocalDate.now().plusDays(1))
                .horaCita(LocalTime.of(10, 0))
                .motivoConsulta("Consulta de rutina")
                .estado(EstadoCita.PROGRAMADA)
                .esEmergencia(false)
                .build();
    }

    @Test
    @DisplayName("Chain - Debe validar disponibilidad exitosamente para fecha futura")
    void debeValidarDisponibilidadFechaFutura() {
        boolean resultado = handler.validar(citaValida);
        assertTrue(resultado);
    }

    @Test
    @DisplayName("Chain - Debe lanzar excepción para fecha en el pasado (no emergencia)")
    void debeLanzarExcepcionFechaPasado() {
        Cita citaPasado = Cita.builder()
                .idCita(1L)
                .mascota(mascota)
                .veterinario(veterinario)
                .servicio(servicio)
                .fechaCita(LocalDate.now().minusDays(1))
                .horaCita(LocalTime.of(10, 0))
                .motivoConsulta("Consulta")
                .estado(EstadoCita.PROGRAMADA)
                .esEmergencia(false)
                .build();

        assertThrows(ValidationException.class, () -> handler.validar(citaPasado));
    }

    @Test
    @DisplayName("Chain - Debe permitir fecha en el pasado para emergencias")
    void debePermitirFechaPasadoEmergencia() {
        Cita citaEmergencia = Cita.builder()
                .idCita(1L)
                .mascota(mascota)
                .veterinario(veterinario)
                .servicio(servicio)
                .fechaCita(LocalDate.now().minusDays(1))
                .horaCita(LocalTime.of(10, 0))
                .motivoConsulta("Emergencia")
                .estado(EstadoCita.PROGRAMADA)
                .esEmergencia(true)
                .build();

        boolean resultado = handler.validar(citaEmergencia);
        assertTrue(resultado);
    }
}

