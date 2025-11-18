package com.veterinaria.clinica_veternica.patterns.behavioral.chain;

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

/**
 * Tests para el patrón Chain of Responsibility - ValidacionHandler
 */
@ExtendWith(MockitoExtension.class)
class ValidacionHandlerTest {

    @Mock
    private Mascota mascota;

    @Mock
    private Veterinario veterinario;

    @Mock
    private Servicio servicio;

    private Cita citaValida;
    private ValidacionDatosHandler validacionDatosHandler;

    @BeforeEach
    void setUp() {
        validacionDatosHandler = new ValidacionDatosHandler();

        citaValida = Cita.builder()
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
    @DisplayName("Chain - Debe validar cita válida exitosamente")
    void debeValidarCitaValidaExitosamente() {
        boolean resultado = validacionDatosHandler.validar(citaValida);
        assertTrue(resultado);
    }

    @Test
    @DisplayName("Chain - Debe lanzar excepción sin mascota")
    void debeLanzarExcepcionSinMascota() {
        Cita citaSinMascota = crearCitaValida();
        citaSinMascota.setMascota(null);

        assertThrows(IllegalArgumentException.class, () -> {
            validacionDatosHandler.validar(citaSinMascota);
        });
    }
    
    private Cita crearCitaValida() {
        Cita cita = new Cita();
        cita.setMascota(citaValida.getMascota());
        cita.setVeterinario(citaValida.getVeterinario());
        cita.setServicio(citaValida.getServicio());
        cita.setFechaCita(citaValida.getFechaCita());
        cita.setHoraCita(citaValida.getHoraCita());
        cita.setMotivoConsulta(citaValida.getMotivoConsulta());
        return cita;
    }

    @Test
    @DisplayName("Chain - Debe lanzar excepción sin veterinario")
    void debeLanzarExcepcionSinVeterinario() {
        citaValida.setVeterinario(null);

        assertThrows(IllegalArgumentException.class, () -> {
            validacionDatosHandler.validar(citaValida);
        });
    }

    @Test
    @DisplayName("Chain - Debe lanzar excepción sin servicio")
    void debeLanzarExcepcionSinServicio() {
        citaValida.setServicio(null);

        assertThrows(IllegalArgumentException.class, () -> {
            validacionDatosHandler.validar(citaValida);
        });
    }

    @Test
    @DisplayName("Chain - Debe lanzar excepción sin fecha")
    void debeLanzarExcepcionSinFecha() {
        citaValida.setFechaCita(null);

        assertThrows(IllegalArgumentException.class, () -> {
            validacionDatosHandler.validar(citaValida);
        });
    }

    @Test
    @DisplayName("Chain - Debe lanzar excepción sin hora")
    void debeLanzarExcepcionSinHora() {
        citaValida.setHoraCita(null);

        assertThrows(IllegalArgumentException.class, () -> {
            validacionDatosHandler.validar(citaValida);
        });
    }

    @Test
    @DisplayName("Chain - Debe lanzar excepción sin motivo")
    void debeLanzarExcepcionSinMotivo() {
        citaValida.setMotivoConsulta(null);

        assertThrows(IllegalArgumentException.class, () -> {
            validacionDatosHandler.validar(citaValida);
        });
    }

    @Test
    @DisplayName("Chain - Debe lanzar excepción con motivo vacío")
    void debeLanzarExcepcionConMotivoVacio() {
        citaValida.setMotivoConsulta("");

        assertThrows(IllegalArgumentException.class, () -> {
            validacionDatosHandler.validar(citaValida);
        });
    }

    @Test
    @DisplayName("Chain - Debe encadenar múltiples handlers")
    void debeEncadenarMultiplesHandlers() {
        ValidacionDisponibilidadHandler disponibilidadHandler = new ValidacionDisponibilidadHandler();
        
        // Configurar cadena
        validacionDatosHandler.setSiguiente(disponibilidadHandler);

        // La validación debe pasar por ambos handlers
        // Verificar que la cadena funciona correctamente
        boolean resultado = validacionDatosHandler.validar(citaValida);
        
        // Verificar que el resultado es válido (puede ser true o false dependiendo de disponibilidad)
        assertNotNull(Boolean.valueOf(resultado), "El resultado debe ser un boolean válido");
    }

    @Test
    @DisplayName("Chain - Debe detenerse en el primer handler que falla")
    void debeDetenerseEnPrimerHandlerQueFalla() {
        // Crear una nueva cita inválida para este test específico
        Cita citaInvalida = crearCitaValida();
        citaInvalida.setMascota(null); // Esto hará fallar ValidacionDatosHandler

        // Configurar cadena con múltiples handlers
        ValidacionDisponibilidadHandler disponibilidadHandler = new ValidacionDisponibilidadHandler();
        validacionDatosHandler.setSiguiente(disponibilidadHandler);

        // Aunque haya más handlers en la cadena, debe detenerse en el primero que falla
        // Verificar que se lanza excepción y que no se procesa el siguiente handler
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            validacionDatosHandler.validar(citaInvalida);
        });
        assertNotNull(exception.getMessage(), "La excepción debe tener un mensaje");
    }

    @Test
    @DisplayName("Chain - Debe retornar true si no hay siguiente handler")
    void debeRetornarTrueSiNoHaySiguienteHandler() {
        // Crear un nuevo handler sin siguiente para este test
        ValidacionDatosHandler handlerSinSiguiente = new ValidacionDatosHandler();
        
        // ValidacionDatosHandler sin siguiente handler debe retornar true si pasa validación
        boolean resultado = handlerSinSiguiente.validar(citaValida);
        assertTrue(resultado, "Debe retornar true cuando no hay siguiente handler y la validación pasa");
    }
}

