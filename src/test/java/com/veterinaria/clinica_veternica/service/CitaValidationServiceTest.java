package com.veterinaria.clinica_veternica.service;

import com.veterinaria.clinica_veternica.domain.agenda.Cita;
import com.veterinaria.clinica_veternica.domain.agenda.EstadoCita;
import com.veterinaria.clinica_veternica.domain.paciente.Mascota;
import com.veterinaria.clinica_veternica.domain.usuario.Veterinario;
import com.veterinaria.clinica_veternica.domain.agenda.Servicio;
import com.veterinaria.clinica_veternica.exception.ValidationException;
import com.veterinaria.clinica_veternica.patterns.behavioral.chain.*;
import com.veterinaria.clinica_veternica.service.impl.CitaValidationService;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class CitaValidationServiceTest {

    @Mock
    private ValidacionDatosHandler validacionDatosHandler;

    @Mock
    private ValidacionDisponibilidadHandler validacionDisponibilidadHandler;

    @Mock
    private ValidacionPermisoHandler validacionPermisoHandler;

    @Mock
    private ValidacionStockHandler validacionStockHandler;

    @InjectMocks
    private CitaValidationService citaValidationService;

    private Cita cita;

    @BeforeEach
    void setUp() {
        cita = Cita.builder()
                .idCita(1L)
                .mascota(mock(Mascota.class))
                .veterinario(mock(Veterinario.class))
                .servicio(mock(Servicio.class))
                .fechaCita(LocalDate.now().plusDays(1))
                .horaCita(LocalTime.of(10, 0))
                .motivoConsulta("Consulta de rutina")
                .estado(EstadoCita.PROGRAMADA)
                .build();
    }

    @Test
    @DisplayName("Debe validar cita exitosamente")
    void debeValidarCitaExitosamente() {
        // Configurar la cadena de validaciones - setSiguiente retorna el handler mismo (fluent interface)
        doReturn(validacionDatosHandler).when(validacionDatosHandler).setSiguiente(any());
        doReturn(validacionDisponibilidadHandler).when(validacionDisponibilidadHandler).setSiguiente(any());
        doReturn(validacionPermisoHandler).when(validacionPermisoHandler).setSiguiente(any());
        
        // El método validar retorna true si pasa, lanza excepción si falla
        when(validacionDatosHandler.validar(cita)).thenReturn(true);

        assertDoesNotThrow(() -> citaValidationService.validarCita(cita));
        verify(validacionDatosHandler, times(1)).validar(cita);
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando validación falla")
    void debeLanzarExcepcionCuandoValidacionFalla() {
        // Configurar la cadena de validaciones - setSiguiente retorna el handler mismo (fluent interface)
        doReturn(validacionDatosHandler).when(validacionDatosHandler).setSiguiente(any());
        doReturn(validacionDisponibilidadHandler).when(validacionDisponibilidadHandler).setSiguiente(any());
        doReturn(validacionPermisoHandler).when(validacionPermisoHandler).setSiguiente(any());
        
        // El método validar lanza ValidationException cuando falla
        doThrow(new ValidationException("Error de validación")).when(validacionDatosHandler).validar(cita);

        assertThrows(ValidationException.class, () -> citaValidationService.validarCita(cita));
    }
}

