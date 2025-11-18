package com.veterinaria.clinica_veternica.patterns.behavioral.observer;

import com.veterinaria.clinica_veternica.domain.agenda.Cita;
import com.veterinaria.clinica_veternica.domain.agenda.EstadoCita;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.*;

/**
 * Tests para el patrón Observer - CitaObserver
 */
@ExtendWith(MockitoExtension.class)
class CitaObserverTest {

    @Mock
    private CitaObserver notificacionObserver;

    @Mock
    private CitaObserver auditoriaObserver;

    private CitaSubject citaSubject;
    private Cita cita;

    @BeforeEach
    void setUp() {
        citaSubject = new CitaSubject();
        
        cita = Cita.builder()
                .idCita(1L)
                .fechaCita(LocalDate.now().plusDays(1))
                .horaCita(LocalTime.of(10, 0))
                .motivoConsulta("Consulta de rutina")
                .estado(EstadoCita.PROGRAMADA)
                .build();
    }

    @Test
    @DisplayName("Observer - Debe agregar observador")
    void debeAgregarObservador() {
        citaSubject.addObserver(notificacionObserver);
        
        // Verificar que el observador fue agregado (no hay método directo, 
        // pero podemos verificar que se notifica)
        citaSubject.notifyCitaCreated(cita);
        verify(notificacionObserver, times(1)).onCitaCreated(cita);
    }

    @Test
    @DisplayName("Observer - Debe eliminar observador")
    void debeEliminarObservador() {
        citaSubject.addObserver(notificacionObserver);
        citaSubject.removeObserver(notificacionObserver);
        
        citaSubject.notifyCitaCreated(cita);
        verify(notificacionObserver, never()).onCitaCreated(cita);
    }

    @Test
    @DisplayName("Observer - Debe notificar cambio de estado a todos los observadores")
    void debeNotificarCambioDeEstadoATodosLosObservadores() {
        citaSubject.addObserver(notificacionObserver);
        citaSubject.addObserver(auditoriaObserver);

        citaSubject.notifyStateChanged(cita, EstadoCita.PROGRAMADA.name(), EstadoCita.CONFIRMADA.name());

        verify(notificacionObserver, times(1)).onCitaStateChanged(
                cita, EstadoCita.PROGRAMADA.name(), EstadoCita.CONFIRMADA.name());
        verify(auditoriaObserver, times(1)).onCitaStateChanged(
                cita, EstadoCita.PROGRAMADA.name(), EstadoCita.CONFIRMADA.name());
    }

    @Test
    @DisplayName("Observer - Debe notificar creación de cita a todos los observadores")
    void debeNotificarCreacionDeCitaATodosLosObservadores() {
        citaSubject.addObserver(notificacionObserver);
        citaSubject.addObserver(auditoriaObserver);

        citaSubject.notifyCitaCreated(cita);

        verify(notificacionObserver, times(1)).onCitaCreated(cita);
        verify(auditoriaObserver, times(1)).onCitaCreated(cita);
    }

    @Test
    @DisplayName("Observer - Debe notificar cancelación de cita a todos los observadores")
    void debeNotificarCancelacionDeCitaATodosLosObservadores() {
        citaSubject.addObserver(notificacionObserver);
        citaSubject.addObserver(auditoriaObserver);

        String motivo = "Cambio de planes del propietario";
        citaSubject.notifyCitaCancelled(cita, motivo);

        verify(notificacionObserver, times(1)).onCitaCancelled(cita, motivo);
        verify(auditoriaObserver, times(1)).onCitaCancelled(cita, motivo);
    }

    @Test
    @DisplayName("Observer - No debe agregar observador nulo")
    void noDebeAgregarObservadorNulo() {
        // Agregar un observador válido primero
        citaSubject.addObserver(notificacionObserver);
        
        // No debe lanzar excepción al agregar null
        try {
            citaSubject.addObserver(null);
            citaSubject.notifyCitaCreated(cita);
        } catch (Exception e) {
            fail("No debería lanzar excepción al agregar observador nulo");
        }
        
        // Verificar que los observadores válidos siguen funcionando
        verify(notificacionObserver, times(1)).onCitaCreated(cita);
    }

    @Test
    @DisplayName("Observer - No debe agregar el mismo observador dos veces")
    void noDebeAgregarMismoObservadorDosVeces() {
        citaSubject.addObserver(notificacionObserver);
        citaSubject.addObserver(notificacionObserver); // Intentar agregar de nuevo

        citaSubject.notifyCitaCreated(cita);
        
        // Debe notificar solo una vez
        verify(notificacionObserver, times(1)).onCitaCreated(cita);
    }

    @Test
    @DisplayName("Observer - Debe manejar errores en observadores sin detener la cadena")
    void debeManejarErroresEnObservadoresSinDetenerCadena() {
        CitaObserver observerConError = mock(CitaObserver.class);
        doThrow(new RuntimeException("Error en observador")).when(observerConError).onCitaCreated(any());

        citaSubject.addObserver(observerConError);
        citaSubject.addObserver(notificacionObserver);

        // No debe lanzar excepción, debe continuar con el siguiente observador
        // Ejecutar sin lanzar excepción
        citaSubject.notifyCitaCreated(cita);

        verify(notificacionObserver, times(1)).onCitaCreated(cita);
    }
}

