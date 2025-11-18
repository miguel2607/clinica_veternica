package com.veterinaria.clinica_veternica.patterns.behavioral.command;

import com.veterinaria.clinica_veternica.domain.agenda.Cita;
import com.veterinaria.clinica_veternica.domain.agenda.EstadoCita;
import com.veterinaria.clinica_veternica.domain.paciente.Mascota;
import com.veterinaria.clinica_veternica.domain.usuario.Veterinario;
import com.veterinaria.clinica_veternica.domain.agenda.Servicio;
import com.veterinaria.clinica_veternica.repository.CitaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class CrearCitaCommandTest {

    @Mock
    private CitaRepository citaRepository;

    @Mock
    private Mascota mascota;

    @Mock
    private Veterinario veterinario;

    @Mock
    private Servicio servicio;

    @InjectMocks
    private CrearCitaCommand crearCitaCommand;

    private Cita cita;

    @BeforeEach
    void setUp() {
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

        when(mascota.getNombre()).thenReturn("Max");
    }

    @Test
    @DisplayName("Command - Debe ejecutar comando de crear cita exitosamente")
    void debeEjecutarComandoCrearCita() throws Exception {
        Cita citaGuardada = Cita.builder()
                .idCita(1L)
                .mascota(mascota)
                .veterinario(veterinario)
                .servicio(servicio)
                .fechaCita(LocalDate.now().plusDays(1))
                .horaCita(LocalTime.of(10, 0))
                .motivoConsulta("Consulta de rutina")
                .estado(EstadoCita.PROGRAMADA)
                .build();

        when(citaRepository.save(any(Cita.class))).thenReturn(citaGuardada);

        crearCitaCommand.setCita(cita);
        crearCitaCommand.ejecutar();

        verify(citaRepository, times(1)).save(any(Cita.class));
    }

    @Test
    @DisplayName("Command - Debe lanzar excepción cuando cita es nula")
    void debeLanzarExcepcionCitaNula() {
        // Crear una nueva instancia sin cita establecida
        CrearCitaCommand comandoSinCita = new CrearCitaCommand(citaRepository);
        assertThrows(IllegalStateException.class, comandoSinCita::ejecutar);
    }

    @Test
    @DisplayName("Command - Debe deshacer comando exitosamente")
    void debeDeshacerComando() throws Exception {
        Cita citaGuardada = Cita.builder()
                .idCita(1L)
                .mascota(mascota)
                .veterinario(veterinario)
                .servicio(servicio)
                .fechaCita(LocalDate.now().plusDays(1))
                .horaCita(LocalTime.of(10, 0))
                .motivoConsulta("Consulta de rutina")
                .estado(EstadoCita.PROGRAMADA)
                .build();

        when(citaRepository.save(any(Cita.class))).thenReturn(citaGuardada);
        doNothing().when(citaRepository).delete(any(Cita.class));

        crearCitaCommand.setCita(cita);
        crearCitaCommand.ejecutar();
        crearCitaCommand.deshacer();

        verify(citaRepository, times(1)).delete(any(Cita.class));
    }

    @Test
    @DisplayName("Command - Debe lanzar excepción al deshacer sin ejecutar")
    void debeLanzarExcepcionDeshacerSinEjecutar() {
        // Crear una nueva instancia sin cita creada
        CrearCitaCommand comandoSinEjecutar = new CrearCitaCommand(citaRepository);
        assertThrows(IllegalStateException.class, comandoSinEjecutar::deshacer);
    }

    @Test
    @DisplayName("Command - Debe retornar descripción correcta")
    void debeRetornarDescripcionCorrecta() {
        crearCitaCommand.setCita(cita);
        String descripcion = crearCitaCommand.getDescripcion();

        assertNotNull(descripcion);
        assertTrue(descripcion.contains("Crear cita"));
    }
}

