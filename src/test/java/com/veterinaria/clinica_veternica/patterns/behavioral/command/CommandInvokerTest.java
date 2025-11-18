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
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Tests para el patrón Command - CommandInvoker
 */
@ExtendWith(MockitoExtension.class)
class CommandInvokerTest {

    @Mock
    private CitaRepository citaRepository;

    private CommandInvoker commandInvoker;
    private CrearCitaCommand crearCitaCommand;
    private Cita cita;
    private Mascota mascota;

    @BeforeEach
    void setUp() {
        commandInvoker = new CommandInvoker();
        crearCitaCommand = new CrearCitaCommand(citaRepository);

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

        crearCitaCommand.setCita(cita);
    }

    @Test
    @DisplayName("CommandInvoker - Debe ejecutar comando exitosamente")
    void debeEjecutarComandoExitosamente() {
        when(citaRepository.save(any(Cita.class))).thenReturn(cita);
        
        assertDoesNotThrow(() -> {
            commandInvoker.ejecutarComando(crearCitaCommand);
        });

        verify(citaRepository, times(1)).save(any(Cita.class));
        assertEquals(1, commandInvoker.getTamanioHistorial());
    }

    @Test
    @DisplayName("CommandInvoker - Debe deshacer último comando")
    void debeDeshacerUltimoComando() throws ValidationException, BusinessException {
        when(citaRepository.save(any(Cita.class))).thenReturn(cita);
        doNothing().when(citaRepository).delete(any(Cita.class));
        
        commandInvoker.ejecutarComando(crearCitaCommand);
        assertEquals(1, commandInvoker.getTamanioHistorial());

        assertDoesNotThrow(() -> {
            commandInvoker.deshacerUltimoComando();
        });

        verify(citaRepository, times(1)).delete(any(Cita.class));
        assertEquals(0, commandInvoker.getTamanioHistorial());
    }

    @Test
    @DisplayName("CommandInvoker - Debe mantener historial de múltiples comandos")
    void debeMantenerHistorialDeMultiplesComandos() {
        when(citaRepository.save(any(Cita.class))).thenReturn(cita);
        
        CrearCitaCommand comando1 = new CrearCitaCommand(citaRepository);
        comando1.setCita(cita);

        CrearCitaCommand comando2 = new CrearCitaCommand(citaRepository);
        Cita cita2 = Cita.builder()
                .idCita(2L)
                .mascota(mascota)
                .fechaCita(LocalDate.now().plusDays(2))
                .horaCita(LocalTime.of(11, 0))
                .motivoConsulta("Segunda consulta")
                .estado(EstadoCita.PROGRAMADA)
                .build();
        comando2.setCita(cita2);

        commandInvoker.ejecutarComando(comando1);
        commandInvoker.ejecutarComando(comando2);

        assertEquals(2, commandInvoker.getTamanioHistorial());
    }

    @Test
    @DisplayName("CommandInvoker - Debe deshacer comandos en orden LIFO")
    void debeDeshacerComandosEnOrdenLIFO() {
        when(citaRepository.save(any(Cita.class))).thenReturn(cita);
        doNothing().when(citaRepository).delete(any(Cita.class));
        
        CrearCitaCommand comando1 = new CrearCitaCommand(citaRepository);
        comando1.setCita(cita);
        CrearCitaCommand comando2 = new CrearCitaCommand(citaRepository);
        comando2.setCita(cita);

        commandInvoker.ejecutarComando(comando1);
        commandInvoker.ejecutarComando(comando2);

        // Deshacer debe eliminar primero comando2 (último en entrar)
        commandInvoker.deshacerUltimoComando();
        verify(citaRepository, times(1)).delete(any(Cita.class));
        assertEquals(1, commandInvoker.getTamanioHistorial());
    }

    @Test
    @DisplayName("CommandInvoker - No debe lanzar excepción al deshacer con historial vacío")
    void noDebeLanzarExcepcionAlDeshacerConHistorialVacio() {
        assertDoesNotThrow(() -> {
            commandInvoker.deshacerUltimoComando();
        });
    }

    @Test
    @DisplayName("CommandInvoker - Debe limpiar historial")
    void debeLimpiarHistorial() {
        when(citaRepository.save(any(Cita.class))).thenReturn(cita);
        
        commandInvoker.ejecutarComando(crearCitaCommand);
        assertEquals(1, commandInvoker.getTamanioHistorial());

        commandInvoker.limpiarHistorial();
        assertEquals(0, commandInvoker.getTamanioHistorial());
    }

    @Test
    @DisplayName("CommandInvoker - Debe obtener tamaño del historial")
    void debeObtenerTamanioHistorial() {
        when(citaRepository.save(any(Cita.class))).thenReturn(cita);
        
        assertEquals(0, commandInvoker.getTamanioHistorial());

        commandInvoker.ejecutarComando(crearCitaCommand);
        assertEquals(1, commandInvoker.getTamanioHistorial());

        commandInvoker.ejecutarComando(crearCitaCommand);
        assertEquals(2, commandInvoker.getTamanioHistorial());
    }
}

