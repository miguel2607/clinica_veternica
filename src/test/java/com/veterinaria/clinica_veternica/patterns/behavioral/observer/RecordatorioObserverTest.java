package com.veterinaria.clinica_veternica.patterns.behavioral.observer;

import com.veterinaria.clinica_veternica.domain.agenda.Cita;
import com.veterinaria.clinica_veternica.domain.agenda.EstadoCita;
import com.veterinaria.clinica_veternica.domain.comunicacion.Comunicacion;
import com.veterinaria.clinica_veternica.domain.paciente.Mascota;
import com.veterinaria.clinica_veternica.domain.paciente.Propietario;
import com.veterinaria.clinica_veternica.domain.usuario.Veterinario;
import com.veterinaria.clinica_veternica.domain.agenda.Servicio;
import com.veterinaria.clinica_veternica.patterns.creational.abstractfactory.EmailNotificacionFactory;
import com.veterinaria.clinica_veternica.patterns.creational.abstractfactory.EnviadorNotificacion;
import com.veterinaria.clinica_veternica.patterns.creational.abstractfactory.MensajeNotificacion;
import com.veterinaria.clinica_veternica.patterns.creational.singleton.ConfigurationManager;
import com.veterinaria.clinica_veternica.repository.ComunicacionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Tests para el patrón Observer - RecordatorioObserver
 */
@ExtendWith(MockitoExtension.class)
class RecordatorioObserverTest {

    @Mock
    private ComunicacionRepository comunicacionRepository;

    @Mock
    private EmailNotificacionFactory emailFactory;

    @Mock
    private ConfigurationManager configurationManager;

    @Mock
    private RecordatorioObserver self;

    @InjectMocks
    private RecordatorioObserver recordatorioObserver;

    private Cita cita;
    private Mascota mascota;
    private Propietario propietario;
    private Veterinario veterinario;
    private Servicio servicio;

    @BeforeEach
    void setUp() {
        propietario = Propietario.builder()
                .idPropietario(1L)
                .nombres("Juan")
                .apellidos("Pérez")
                .email("juan@example.com")
                .telefono("+5730108543096")
                .documento("12345678")
                .tipoDocumento("CC")
                .build();

        mascota = Mascota.builder()
                .idMascota(1L)
                .nombre("Max")
                .propietario(propietario)
                .build();

        veterinario = Veterinario.builder()
                .idPersonal(1L)
                .nombres("Dr.")
                .apellidos("García")
                .documento("12345678")
                .correo("garcia@clinic.com")
                .telefono("123456789")
                .especialidad("Medicina General")
                .registroProfesional("REG-001")
                .build();

        servicio = Servicio.builder()
                .idServicio(1L)
                .nombre("Consulta General")
                .build();

        cita = Cita.builder()
                .idCita(1L)
                .mascota(mascota)
                .veterinario(veterinario)
                .servicio(servicio)
                .fechaCita(LocalDate.now().plusDays(2))
                .horaCita(LocalTime.of(10, 0))
                .estado(EstadoCita.PROGRAMADA)
                .build();
    }

    @Test
    @DisplayName("Debe programar recordatorios cuando cita cambia a CONFIRMADA")
    void debeProgramarRecordatoriosCuandoCitaCambiaAConfirmada() {
        // Arrange
        String estadoAnterior = "PROGRAMADA";
        String estadoNuevo = "CONFIRMADA";
        doNothing().when(self).programarRecordatorios(any(Cita.class));

        // Act
        recordatorioObserver.onCitaStateChanged(cita, estadoAnterior, estadoNuevo);

        // Assert
        verify(self, times(1)).programarRecordatorios(cita);
    }

    @Test
    @DisplayName("Debe cancelar recordatorios cuando cita cambia a CANCELADA")
    void debeCancelarRecordatoriosCuandoCitaCambiaACancelada() {
        // Arrange
        String estadoAnterior = "CONFIRMADA";
        String estadoNuevo = "CANCELADA";
        when(comunicacionRepository.findByTipo("RECORDATORIO"))
                .thenReturn(Collections.emptyList());

        // Act
        recordatorioObserver.onCitaStateChanged(cita, estadoAnterior, estadoNuevo);

        // Assert
        verify(comunicacionRepository, times(1)).findByTipo("RECORDATORIO");
    }

    @Test
    @DisplayName("Debe cancelar recordatorios cuando cita cambia a ATENDIDA")
    void debeCancelarRecordatoriosCuandoCitaCambiaAAtendida() {
        // Arrange
        String estadoAnterior = "CONFIRMADA";
        String estadoNuevo = "ATENDIDA";
        when(comunicacionRepository.findByTipo("RECORDATORIO"))
                .thenReturn(Collections.emptyList());

        // Act
        recordatorioObserver.onCitaStateChanged(cita, estadoAnterior, estadoNuevo);

        // Assert
        verify(comunicacionRepository, times(1)).findByTipo("RECORDATORIO");
    }

    @Test
    @DisplayName("Debe programar recordatorios cuando se crea cita confirmada")
    void debeProgramarRecordatoriosCuandoSeCreaCitaConfirmada() {
        // Arrange
        cita.setEstado(EstadoCita.CONFIRMADA);
        doNothing().when(self).programarRecordatorios(any(Cita.class));

        // Act
        recordatorioObserver.onCitaCreated(cita);

        // Assert
        verify(self, times(1)).programarRecordatorios(cita);
    }

    @Test
    @DisplayName("No debe programar recordatorios cuando se crea cita no confirmada")
    void noDebeProgramarRecordatoriosCuandoSeCreaCitaNoConfirmada() {
        // Arrange
        cita.setEstado(EstadoCita.PROGRAMADA);

        // Act
        recordatorioObserver.onCitaCreated(cita);

        // Assert
        verify(self, never()).programarRecordatorios(any(Cita.class));
    }

    @Test
    @DisplayName("Debe cancelar recordatorios cuando se cancela cita")
    void debeCancelarRecordatoriosCuandoSeCancelaCita() {
        // Arrange
        String motivo = "Cliente canceló";
        when(comunicacionRepository.findByTipo("RECORDATORIO"))
                .thenReturn(Collections.emptyList());

        // Act
        recordatorioObserver.onCitaCancelled(cita, motivo);

        // Assert
        verify(comunicacionRepository, times(1)).findByTipo("RECORDATORIO");
    }

    @Test
    @DisplayName("Debe crear recordatorios cuando los recordatorios automáticos están habilitados")
    void debeCrearRecordatoriosCuandoRecordatoriosAutomaticosEstanHabilitados() {
        // Arrange
        when(configurationManager.getRecordatoriosAutomaticos()).thenReturn(true);
        when(configurationManager.getHorasAnticipacionRecordatorio()).thenReturn(24);

        // Configurar cita para dentro de 3 días
        cita.setFechaCita(LocalDate.now().plusDays(3));
        cita.setHoraCita(LocalTime.of(10, 0));

        // Act
        recordatorioObserver.programarRecordatorios(cita);

        // Assert
        // Debe crear al menos 2 recordatorios (24h antes, 2h antes, 1h antes)
        verify(comunicacionRepository, atLeast(2)).save(any(Comunicacion.class));
    }

    @Test
    @DisplayName("No debe crear recordatorios cuando los recordatorios automáticos están deshabilitados")
    void noDebeCrearRecordatoriosCuandoRecordatoriosAutomaticosEstanDeshabilitados() {
        // Arrange
        when(configurationManager.getRecordatoriosAutomaticos()).thenReturn(false);

        // Act
        recordatorioObserver.programarRecordatorios(cita);

        // Assert
        verify(comunicacionRepository, never()).save(any(Comunicacion.class));
    }

    @Test
    @DisplayName("Debe crear recordatorios con información correcta de la cita")
    void debeCrearRecordatoriosConInformacionCorrectaDeLaCita() {
        // Arrange
        when(configurationManager.getRecordatoriosAutomaticos()).thenReturn(true);
        when(configurationManager.getHorasAnticipacionRecordatorio()).thenReturn(24);

        cita.setFechaCita(LocalDate.now().plusDays(3));
        ArgumentCaptor<Comunicacion> comunicacionCaptor = ArgumentCaptor.forClass(Comunicacion.class);

        // Act
        recordatorioObserver.programarRecordatorios(cita);

        // Assert
        verify(comunicacionRepository, atLeast(1)).save(comunicacionCaptor.capture());
        Comunicacion recordatorio = comunicacionCaptor.getValue();

        assertEquals("RECORDATORIO", recordatorio.getTipo());
        assertEquals("EMAIL", recordatorio.getCanal());
        assertEquals("Recordatorio de Cita", recordatorio.getAsunto());
        assertEquals(propietario.getNombreCompleto(), recordatorio.getDestinatarioNombre());
        assertEquals(propietario.getEmail(), recordatorio.getDestinatarioEmail());
        assertEquals(cita, recordatorio.getCita());
        assertFalse(com.veterinaria.clinica_veternica.util.Constants.isTrue(recordatorio.getEnviada()));
    }

    @Test
    @DisplayName("Debe cancelar solo recordatorios pendientes de la cita específica")
    void debeCancelarSoloRecordatoriosPendientesDeLaCitaEspecifica() {
        // Arrange
        Comunicacion recordatorio1 = Comunicacion.builder()
                .idComunicacion(1L)
                .tipo("RECORDATORIO")
                .cita(cita)
                .enviada(false)
                .build();

        Comunicacion recordatorio2 = Comunicacion.builder()
                .idComunicacion(2L)
                .tipo("RECORDATORIO")
                .cita(cita)
                .enviada(true)
                .build();

        Cita otraCita = Cita.builder().idCita(2L).build();
        Comunicacion recordatorio3 = Comunicacion.builder()
                .idComunicacion(3L)
                .tipo("RECORDATORIO")
                .cita(otraCita)
                .enviada(false)
                .build();

        when(comunicacionRepository.findByTipo("RECORDATORIO"))
                .thenReturn(Arrays.asList(recordatorio1, recordatorio2, recordatorio3));

        // Act
        recordatorioObserver.onCitaCancelled(cita, "Prueba");

        // Assert
        verify(comunicacionRepository, times(1)).save(recordatorio1);
        verify(comunicacionRepository, never()).save(recordatorio2);
        verify(comunicacionRepository, never()).save(recordatorio3);
        assertTrue(com.veterinaria.clinica_veternica.util.Constants.isTrue(recordatorio1.getEnviada()));
    }

    @Test
    @DisplayName("Debe enviar recordatorio con formato correcto")
    void debeEnviarRecordatorioConFormatoCorrecto() {
        // Arrange
        Comunicacion comunicacion = Comunicacion.builder()
                .idComunicacion(1L)
                .tipo("RECORDATORIO")
                .cita(cita)
                .enviada(false)
                .build();

        MensajeNotificacion mensaje = mock(MensajeNotificacion.class);
        EnviadorNotificacion enviador = mock(EnviadorNotificacion.class);

        when(emailFactory.crearMensaje(anyString(), anyString(), anyString()))
                .thenReturn(mensaje);
        when(emailFactory.crearEnviador()).thenReturn(enviador);
        when(enviador.enviar(any(MensajeNotificacion.class))).thenReturn(true);

        // Act
        recordatorioObserver.enviarRecordatorio(comunicacion);

        // Assert
        verify(emailFactory, times(1)).crearMensaje(
                eq(propietario.getEmail()),
                eq("Recordatorio de Cita"),
                anyString()
        );
        verify(enviador, times(1)).enviar(mensaje);
        verify(comunicacionRepository, times(1)).save(comunicacion);
    }

    @Test
    @DisplayName("No debe enviar recordatorio si ya fue enviado")
    void noDebeEnviarRecordatorioSiYaFueEnviado() {
        // Arrange
        Comunicacion comunicacion = Comunicacion.builder()
                .idComunicacion(1L)
                .tipo("RECORDATORIO")
                .cita(cita)
                .enviada(true)
                .build();

        // Act
        recordatorioObserver.enviarRecordatorio(comunicacion);

        // Assert
        verify(emailFactory, never()).crearMensaje(anyString(), anyString(), anyString());
        verify(emailFactory, never()).crearEnviador();
    }

    @Test
    @DisplayName("Debe manejar error al enviar recordatorio")
    void debeManejarErrorAlEnviarRecordatorio() {
        // Arrange
        Comunicacion comunicacion = Comunicacion.builder()
                .idComunicacion(1L)
                .tipo("RECORDATORIO")
                .cita(cita)
                .enviada(false)
                .build();

        when(emailFactory.crearMensaje(anyString(), anyString(), anyString()))
                .thenThrow(new IllegalArgumentException("Email inválido"));

        // Act
        recordatorioObserver.enviarRecordatorio(comunicacion);

        // Assert
        verify(comunicacionRepository, times(1)).save(comunicacion);
        assertNotNull(comunicacion.getMensajeError());
        assertTrue(comunicacion.getMensajeError().contains("Error de validación"));
    }

    @Test
    @DisplayName("Debe crear recordatorio 24h antes cuando cita es en más de 24 horas")
    void debeCrearRecordatorio24hAntesCuandoCitaEsEnMasDe24Horas() {
        // Arrange
        when(configurationManager.getRecordatoriosAutomaticos()).thenReturn(true);
        when(configurationManager.getHorasAnticipacionRecordatorio()).thenReturn(24);

        cita.setFechaCita(LocalDate.now().plusDays(5));
        cita.setHoraCita(LocalTime.of(10, 0));

        // Act
        recordatorioObserver.programarRecordatorios(cita);

        // Assert
        verify(comunicacionRepository, atLeast(3)).save(any(Comunicacion.class));
    }

    @Test
    @DisplayName("No debe crear recordatorio si comunicación no tiene cita asociada")
    void noDebeEnviarRecordatorioSiComunicacionNoTieneCitaAsociada() {
        // Arrange
        Comunicacion comunicacion = Comunicacion.builder()
                .idComunicacion(1L)
                .tipo("RECORDATORIO")
                .cita(null)
                .enviada(false)
                .build();

        // Act
        recordatorioObserver.enviarRecordatorio(comunicacion);

        // Assert
        verify(emailFactory, never()).crearMensaje(anyString(), anyString(), anyString());
        verify(emailFactory, never()).crearEnviador();
    }
}
