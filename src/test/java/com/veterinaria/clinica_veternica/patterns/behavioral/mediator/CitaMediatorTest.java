package com.veterinaria.clinica_veternica.patterns.behavioral.mediator;

import com.veterinaria.clinica_veternica.domain.agenda.Cita;
import com.veterinaria.clinica_veternica.domain.agenda.EstadoCita;
import com.veterinaria.clinica_veternica.domain.agenda.Servicio;
import com.veterinaria.clinica_veternica.domain.paciente.Mascota;
import com.veterinaria.clinica_veternica.domain.usuario.Veterinario;
import com.veterinaria.clinica_veternica.patterns.behavioral.observer.CitaSubject;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Tests para el patrón Mediator - CitaMediator
 */
@ExtendWith(MockitoExtension.class)
class CitaMediatorTest {

    @Mock
    private CitaRepository citaRepository;

    @Mock
    private CitaSubject citaSubject;

    @InjectMocks
    private CitaMediatorImpl citaMediator;

    private Cita cita;
    private Veterinario veterinario;
    private Mascota mascota;
    private Servicio servicio;

    @BeforeEach
    void setUp() {
        veterinario = Veterinario.builder()
                .idPersonal(1L)
                .nombres("Dr. Carlos")
                .apellidos("García")
                .activo(true)
                .build();

        mascota = Mascota.builder()
                .idMascota(1L)
                .nombre("Max")
                .activo(true)
                .build();

        servicio = Servicio.builder()
                .idServicio(1L)
                .nombre("Consulta General")
                .activo(true)
                .build();

        cita = Cita.builder()
                .idCita(1L)
                .veterinario(veterinario)
                .mascota(mascota)
                .servicio(servicio)
                .fechaCita(LocalDate.now())
                .horaCita(LocalTime.of(10, 0))
                .estado(EstadoCita.PROGRAMADA)
                .build();
    }

    @Test
    @DisplayName("Debe crear una cita coordinando todos los componentes")
    void debeCrearCitaCoordinandoComponentes() {
        when(citaRepository.save(any(Cita.class))).thenReturn(cita);

        Cita resultado = citaMediator.crearCita(cita);

        assertNotNull(resultado);
        assertEquals(cita.getIdCita(), resultado.getIdCita());
        verify(citaRepository, times(1)).save(any(Cita.class));
        verify(citaSubject, times(1)).notifyCitaCreated(any(Cita.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción si veterinario no está disponible")
    void debeLanzarExcepcionSiVeterinarioNoDisponible() {
        veterinario.setActivo(false);
        cita.setVeterinario(veterinario);

        assertThrows(IllegalStateException.class, () -> {
            citaMediator.crearCita(cita);
        }, "Debe lanzar excepción si el veterinario no está activo");

        verify(citaRepository, never()).save(any(Cita.class));
        verify(citaSubject, never()).notifyCitaCreated(any(Cita.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción si servicio no está disponible")
    void debeLanzarExcepcionSiServicioNoDisponible() {
        servicio.setActivo(false);
        cita.setServicio(servicio);

        assertThrows(IllegalStateException.class, () -> {
            citaMediator.crearCita(cita);
        }, "Debe lanzar excepción si el servicio no está activo");

        verify(citaRepository, never()).save(any(Cita.class));
    }

    @Test
    @DisplayName("Debe confirmar una cita coordinando notificaciones")
    void debeConfirmarCitaCoordinandoNotificaciones() {
        when(citaRepository.findById(1L)).thenReturn(Optional.of(cita));
        when(citaRepository.save(any(Cita.class))).thenReturn(cita);

        citaMediator.confirmarCita(1L);

        verify(citaRepository, times(1)).findById(1L);
        verify(citaRepository, times(1)).save(any(Cita.class));
        verify(citaSubject, times(1)).notifyStateChanged(cita, "PROGRAMADA", "CONFIRMADA");
    }

    @Test
    @DisplayName("Debe lanzar excepción al confirmar cita inexistente")
    void debeLanzarExcepcionAlConfirmarCitaInexistente() {
        when(citaRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> {
            citaMediator.confirmarCita(999L);
        }, "Debe lanzar excepción si la cita no existe");
    }

    @Test
    @DisplayName("Debe cancelar una cita coordinando liberación de recursos")
    void debeCancelarCitaCoordinandoLiberacionRecursos() {
        when(citaRepository.findById(1L)).thenReturn(Optional.of(cita));
        when(citaRepository.save(any(Cita.class))).thenReturn(cita);

        String motivo = "Cancelación por el cliente";
        citaMediator.cancelarCita(1L, motivo);

        verify(citaRepository, times(1)).findById(1L);
        verify(citaRepository, times(1)).save(any(Cita.class));
        verify(citaSubject, times(1)).notifyCitaCancelled(cita, motivo);
    }

    @Test
    @DisplayName("Debe notificar cambios en la cita")
    void debeNotificarCambiosEnCita() {
        when(citaRepository.findById(1L)).thenReturn(Optional.of(cita));

        citaMediator.notificarCambio(1L, "CITA_CREADA");

        verify(citaRepository, times(1)).findById(1L);
        verify(citaSubject, times(1)).notifyCitaCreated(cita);
    }

    @Test
    @DisplayName("Debe notificar cancelación cuando el evento es CITA_CANCELADA")
    void debeNotificarCancelacionCuandoEventoEsCitaCancelada() {
        when(citaRepository.findById(1L)).thenReturn(Optional.of(cita));

        citaMediator.notificarCambio(1L, "CITA_CANCELADA");

        verify(citaSubject, times(1)).notifyCitaCancelled(cita, "Sistema");
    }

    @Test
    @DisplayName("Debe lanzar excepción al notificar cambio en cita inexistente")
    void debeLanzarExcepcionAlNotificarCambioEnCitaInexistente() {
        when(citaRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> {
            citaMediator.notificarCambio(999L, "CITA_CREADA");
        }, "Debe lanzar excepción si la cita no existe");
    }
}

