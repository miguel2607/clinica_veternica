package com.veterinaria.clinica_veternica.patterns.behavioral.observer;

import com.veterinaria.clinica_veternica.domain.agenda.Cita;
import com.veterinaria.clinica_veternica.domain.agenda.EstadoCita;
import com.veterinaria.clinica_veternica.domain.paciente.Mascota;
import com.veterinaria.clinica_veternica.domain.paciente.Propietario;
import com.veterinaria.clinica_veternica.domain.usuario.Veterinario;
import com.veterinaria.clinica_veternica.domain.agenda.Servicio;
import com.veterinaria.clinica_veternica.patterns.creational.abstractfactory.EmailNotificacionFactory;
import com.veterinaria.clinica_veternica.patterns.creational.abstractfactory.EnviadorNotificacion;
import com.veterinaria.clinica_veternica.patterns.creational.abstractfactory.MensajeNotificacion;
import com.veterinaria.clinica_veternica.repository.ComunicacionRepository;
import com.veterinaria.clinica_veternica.service.EmailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;


@ExtendWith(MockitoExtension.class)
class NotificacionObserverTest {

    @Mock
    private Mascota mascota;

    @Mock
    private Propietario propietario;

    @Mock
    private Veterinario veterinario;

    @Mock
    private Servicio servicio;

    @Mock
    private EmailNotificacionFactory emailFactory;

    @Mock
    private EmailService emailService;

    @Mock
    private ComunicacionRepository comunicacionRepository;

    @Mock
    private EnviadorNotificacion enviadorNotificacion;

    @Mock
    private MensajeNotificacion mensajeNotificacion;

    private NotificacionObserver observer;
    private Cita cita;

    @BeforeEach
    void setUp() {
        observer = new NotificacionObserver(emailFactory, emailService, comunicacionRepository);

        // Configurar mocks con lenient para evitar errores de stubbing innecesario
        lenient().when(mascota.getPropietario()).thenReturn(propietario);
        lenient().when(propietario.getEmail()).thenReturn("propietario@test.com");
        lenient().when(propietario.getNombreCompleto()).thenReturn("Juan Pérez");
        lenient().when(propietario.getTelefono()).thenReturn("1234567890");
        lenient().when(veterinario.getCorreo()).thenReturn("veterinario@test.com");
        lenient().when(veterinario.getNombreCompleto()).thenReturn("Dr. Veterinario");
        lenient().when(emailFactory.crearMensaje(anyString(), anyString(), anyString())).thenReturn(mensajeNotificacion);
        lenient().when(emailFactory.crearEnviador()).thenReturn(enviadorNotificacion);
        lenient().when(enviadorNotificacion.enviar(any(MensajeNotificacion.class))).thenReturn(true);
        lenient().when(enviadorNotificacion.getIdExterno()).thenReturn("test-id-123");
        lenient().when(emailService.generarTemplateHtml(anyString(), anyString(), anyString())).thenReturn("<html>Test</html>");

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
    }

    @Test
    @DisplayName("Observer - Debe notificar cambio de estado a CONFIRMADA")
    void debeNotificarCambioEstadoConfirmada() {
        assertDoesNotThrow(() -> {
            observer.onCitaStateChanged(cita, EstadoCita.PROGRAMADA.name(), EstadoCita.CONFIRMADA.name());
        });
    }

    @Test
    @DisplayName("Observer - Debe notificar cambio de estado a ATENDIDA")
    void debeNotificarCambioEstadoAtendida() {
        assertDoesNotThrow(() -> {
            observer.onCitaStateChanged(cita, EstadoCita.CONFIRMADA.name(), EstadoCita.ATENDIDA.name());
        });
    }

    @Test
    @DisplayName("Observer - Debe notificar creación de cita")
    void debeNotificarCreacionCita() {
        assertDoesNotThrow(() -> {
            observer.onCitaCreated(cita);
        });
    }

    @Test
    @DisplayName("Observer - Debe notificar cancelación de cita")
    void debeNotificarCancelacionCita() {
        assertDoesNotThrow(() -> {
            observer.onCitaCancelled(cita, "Motivo de cancelación");
        });
    }
}

