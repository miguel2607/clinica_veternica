package com.veterinaria.clinica_veternica.patterns.behavioral.command;

import com.veterinaria.clinica_veternica.domain.agenda.Cita;
import com.veterinaria.clinica_veternica.domain.agenda.EstadoCita;
import com.veterinaria.clinica_veternica.domain.paciente.Mascota;
import com.veterinaria.clinica_veternica.exception.BusinessException;
import com.veterinaria.clinica_veternica.exception.ValidationException;
import com.veterinaria.clinica_veternica.repository.CitaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Tests para el patrón Command - Command
 */
@ExtendWith(MockitoExtension.class)
class CommandTest {

    @Mock
    private CitaRepository citaRepository;

    @InjectMocks
    private CrearCitaCommand crearCitaCommand;

    private Cita cita;
    private Cita citaGuardada;
    private Mascota mascota;

    @BeforeEach
    void setUp() {
        mascota = Mascota.builder()
                .idMascota(1L)
                .nombre("Max")
                .sexo("Macho")
                .activo(true)
                .build();

        cita = Cita.builder()
                .idCita(1L)
                .mascota(mascota)
                .fechaCita(LocalDate.now().plusDays(1))
                .horaCita(LocalTime.of(10, 0))
                .motivoConsulta("Consulta de rutina")
                .estado(EstadoCita.PROGRAMADA)
                .build();

        citaGuardada = Cita.builder()
                .idCita(1L)
                .mascota(mascota)
                .fechaCita(cita.getFechaCita())
                .horaCita(cita.getHoraCita())
                .motivoConsulta(cita.getMotivoConsulta())
                .estado(cita.getEstado())
                .build();

        crearCitaCommand = new CrearCitaCommand(citaRepository);
    }

    @Test
    @DisplayName("Command - Debe ejecutar comando exitosamente")
    void debeEjecutarComandoExitosamente() throws ValidationException, BusinessException {
        crearCitaCommand.setCita(cita);
        when(citaRepository.save(any(Cita.class))).thenReturn(citaGuardada);

        assertDoesNotThrow(() -> crearCitaCommand.ejecutar());
        verify(citaRepository, times(1)).save(any(Cita.class));
    }

    @Test
    @DisplayName("Command - Debe lanzar excepción si cita es nula")
    void debeLanzarExcepcionSiCitaEsNula() {
        assertThrows(IllegalStateException.class, () -> {
            crearCitaCommand.ejecutar();
        });
    }

    @Test
    @DisplayName("Command - Debe deshacer comando exitosamente")
    void debeDeshacerComandoExitosamente() throws ValidationException, BusinessException {
        crearCitaCommand.setCita(cita);
        when(citaRepository.save(any(Cita.class))).thenReturn(citaGuardada);
        doNothing().when(citaRepository).delete(any(Cita.class));

        crearCitaCommand.ejecutar();
        assertDoesNotThrow(() -> crearCitaCommand.deshacer());
        
        verify(citaRepository, times(1)).save(any(Cita.class));
        verify(citaRepository, times(1)).delete(any(Cita.class));
    }

    @Test
    @DisplayName("Command - Debe lanzar excepción al deshacer sin ejecutar")
    void debeLanzarExcepcionAlDeshacerSinEjecutar() {
        crearCitaCommand.setCita(cita);

        assertThrows(IllegalStateException.class, () -> {
            crearCitaCommand.deshacer();
        });
    }

    @Test
    @DisplayName("Command - Debe obtener descripción del comando")
    void debeObtenerDescripcionDelComando() {
        crearCitaCommand.setCita(cita);
        
        String descripcion = crearCitaCommand.getDescripcion();
        assertNotNull(descripcion);
        assertTrue(descripcion.contains("Crear cita"));
    }

    @Test
    @DisplayName("Command - Debe obtener descripción incluso sin cita")
    void debeObtenerDescripcionSinCita() {
        String descripcion = crearCitaCommand.getDescripcion();
        assertNotNull(descripcion);
        assertTrue(descripcion.contains("N/A"));
    }
}

