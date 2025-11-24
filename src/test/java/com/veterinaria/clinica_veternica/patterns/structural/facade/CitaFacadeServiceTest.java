package com.veterinaria.clinica_veternica.patterns.structural.facade;

import com.veterinaria.clinica_veternica.dto.request.agenda.CitaRequestDTO;
import com.veterinaria.clinica_veternica.dto.response.agenda.CitaResponseDTO;
import com.veterinaria.clinica_veternica.dto.response.facade.CalendarioCitasDTO;
import com.veterinaria.clinica_veternica.dto.response.facade.ResultadoCitaConNotificacionDTO;
import com.veterinaria.clinica_veternica.service.interfaces.ICitaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import org.mockito.InOrder;

/**
 * Pruebas unitarias para el servicio CitaFacadeService.
 *
 * Verifica que el servicio facade de citas:
 * - Cree citas con notificación automática
 * - Cancele citas con notificación
 * - Reprograme citas con notificación
 * - Obtenga calendario de citas
 *
 * @author Clínica Veterinaria Team
 * @version 1.0
 * @since 2025-11-18
 */
@ExtendWith(MockitoExtension.class)
class CitaFacadeServiceTest {

    @Mock
    private ICitaService citaService;

    @InjectMocks
    private CitaFacadeService citaFacadeService;

    private CitaRequestDTO requestDTO;
    private CitaResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        requestDTO = CitaRequestDTO.builder()
                .idMascota(1L)
                .idVeterinario(1L)
                .idServicio(1L)
                .fechaCita(LocalDate.now().plusDays(1))
                .horaCita(LocalTime.of(10, 0))
                .motivo("Consulta general")
                .build();

        responseDTO = CitaResponseDTO.builder()
                .idCita(1L)
                .fechaCita(requestDTO.getFechaCita())
                .horaCita(requestDTO.getHoraCita())
                .motivo(requestDTO.getMotivo())
                .estado("PROGRAMADA")
                .build();
    }

    @Test
    @DisplayName("Debe crear cita con notificación automática")
    void debeCrearCitaConNotificacion() {
        when(citaService.crear(any(CitaRequestDTO.class))).thenReturn(responseDTO);

        CitaResponseDTO result = citaFacadeService.crearCitaConNotificacion(requestDTO);

        assertNotNull(result, "El resultado no debe ser nulo");
        assertEquals(1L, result.getIdCita(), "El ID de la cita debe ser 1");
        assertEquals("PROGRAMADA", result.getEstado(), "El estado debe ser PROGRAMADA");
        assertEquals("Consulta general", result.getMotivo(), "El motivo debe ser Consulta general");

        verify(citaService, times(1)).crear(any(CitaRequestDTO.class));
        verifyNoMoreInteractions(citaService);
    }

    @Test
    @DisplayName("Debe retornar null cuando el servicio retorna null")
    void debeRetornarNullCuandoServicioRetornaNull() {
        when(citaService.crear(any(CitaRequestDTO.class))).thenReturn(null);

        CitaResponseDTO result = citaFacadeService.crearCitaConNotificacion(requestDTO);

        assertNull(result, "El resultado debe ser nulo");
        verify(citaService).crear(any(CitaRequestDTO.class));
    }

    @Test
    @DisplayName("Debe cancelar cita con notificación")
    void debeCancelarCitaConNotificacion() {
        CitaResponseDTO citaCancelada = CitaResponseDTO.builder()
                .idCita(1L)
                .fechaCita(responseDTO.getFechaCita())
                .horaCita(responseDTO.getHoraCita())
                .motivo(responseDTO.getMotivo())
                .estado("CANCELADA")
                .build();

        when(citaService.cancelar(anyLong(), anyString(), anyString())).thenReturn(citaCancelada);

        ResultadoCitaConNotificacionDTO result = citaFacadeService.cancelarCitaConNotificacion(1L, "Motivo test", "admin");

        assertNotNull(result, "El resultado no debe ser nulo");
        assertNotNull(result.getCita(), "La cita no debe ser nula");
        assertEquals("CANCELADA", result.getCita().getEstado(), "El estado debe ser CANCELADA");
        assertEquals(1L, result.getCita().getIdCita(), "El ID de la cita debe ser 1");
        assertNotNull(result.getMensaje(), "El mensaje no debe ser nulo");
        assertTrue(result.getMensaje().contains("cancelada"), "El mensaje debe contener 'cancelada'");

        verify(citaService, times(1)).cancelar(1L, "Motivo test", "admin");
        verifyNoMoreInteractions(citaService);
    }

    @Test
    @DisplayName("Debe lanzar excepción al cancelar cita con ID nulo")
    void debeValidarIdCitaAlCancelar() {
        assertThrows(IllegalArgumentException.class,
            () -> citaFacadeService.cancelarCitaConNotificacion(null, "Motivo", "admin"),
            "Debe lanzar excepción para ID nulo");
    }

    @Test
    @DisplayName("Debe reprogramar cita con notificación")
    void debeReprogramarCitaConNotificacion() {
        CitaResponseDTO nuevaCita = CitaResponseDTO.builder()
                .idCita(2L)
                .fechaCita(LocalDate.now().plusDays(2))
                .horaCita(LocalTime.of(14, 0))
                .motivo("Consulta general")
                .estado("PROGRAMADA")
                .build();

        when(citaService.cancelar(anyLong(), anyString(), anyString())).thenReturn(responseDTO);
        when(citaService.crear(any(CitaRequestDTO.class))).thenReturn(nuevaCita);

        ResultadoCitaConNotificacionDTO result = citaFacadeService.reprogramarCitaConNotificacion(1L, requestDTO);

        assertNotNull(result, "El resultado no debe ser nulo");
        assertNotNull(result.getCita(), "La cita no debe ser nula");
        assertEquals(2L, result.getCita().getIdCita(), "El ID de la nueva cita debe ser 2");
        assertEquals("PROGRAMADA", result.getCita().getEstado(), "El estado debe ser PROGRAMADA");
        assertNotNull(result.getMensaje(), "El mensaje no debe ser nulo");
        assertTrue(result.getMensaje().contains("reprogramada"), "El mensaje debe contener 'reprogramada'");

        verify(citaService, times(1)).cancelar(1L, "Reprogramación de cita", "Sistema");
        verify(citaService, times(1)).crear(any(CitaRequestDTO.class));
        verifyNoMoreInteractions(citaService);
    }

    @Test
    @DisplayName("Debe reprogramar cita con datos diferentes")
    void debeReprogramarCitaConDatos() {
        CitaRequestDTO nuevoCitaRequest = CitaRequestDTO.builder()
                .idMascota(1L)
                .idVeterinario(2L)
                .idServicio(2L)
                .fechaCita(LocalDate.now().plusDays(3))
                .horaCita(LocalTime.of(15, 30))
                .motivo("Consulta urgente")
                .build();

        CitaResponseDTO nuevaCita = CitaResponseDTO.builder()
                .idCita(3L)
                .fechaCita(nuevoCitaRequest.getFechaCita())
                .horaCita(nuevoCitaRequest.getHoraCita())
                .motivo(nuevoCitaRequest.getMotivo())
                .estado("PROGRAMADA")
                .build();

        when(citaService.cancelar(anyLong(), anyString(), anyString())).thenReturn(responseDTO);
        when(citaService.crear(any(CitaRequestDTO.class))).thenReturn(nuevaCita);

        ResultadoCitaConNotificacionDTO result = citaFacadeService.reprogramarCitaConNotificacion(1L, nuevoCitaRequest);

        assertNotNull(result.getCita());
        assertEquals(3L, result.getCita().getIdCita());
        assertEquals("Consulta urgente", result.getCita().getMotivo());
    }

    @Test
    @DisplayName("Debe obtener calendario de citas para un día")
    void debeObtenerCalendarioCitas() {
        LocalDate fecha = LocalDate.now();
        List<CitaResponseDTO> citas = List.of(responseDTO);

        when(citaService.listarPorRangoFechas(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(citas);

        CalendarioCitasDTO result = citaFacadeService.obtenerCalendarioCitas(fecha);

        assertNotNull(result, "El resultado no debe ser nulo");
        assertEquals(fecha, result.getFecha(), "La fecha debe coincidir");
        assertEquals(1, result.getTotalCitas(), "Debe haber 1 cita");
        assertNotNull(result.getCitas(), "La lista de citas no debe ser nula");
        assertEquals(1, result.getCitas().size(), "Debe haber 1 cita en la lista");

        verify(citaService, times(1)).listarPorRangoFechas(any(LocalDateTime.class), any(LocalDateTime.class));
        verifyNoMoreInteractions(citaService);
    }

    @Test
    @DisplayName("Debe obtener calendario vacío cuando no hay citas")
    void debeObtenerCalendarioVacio() {
        LocalDate fecha = LocalDate.now();
        List<CitaResponseDTO> citas = Collections.emptyList();

        when(citaService.listarPorRangoFechas(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(citas);

        CalendarioCitasDTO result = citaFacadeService.obtenerCalendarioCitas(fecha);

        assertNotNull(result, "El resultado no debe ser nulo");
        assertEquals(fecha, result.getFecha(), "La fecha debe coincidir");
        assertEquals(0, result.getTotalCitas(), "No debe haber citas");
        assertEquals(0, result.getCitasAtendidas(), "No debe haber citas atendidas");
        assertEquals(0, result.getCitasProgramadas(), "No debe haber citas programadas");
        assertEquals(0, result.getCitasCanceladas(), "No debe haber citas canceladas");
        assertNotNull(result.getCitas(), "La lista de citas no debe ser nula");
        assertTrue(result.getCitas().isEmpty(), "La lista de citas debe estar vacía");
    }

    @Test
    @DisplayName("Debe contar citas por estado en el calendario")
    void debeContarCitasPorEstado() {
        LocalDate fecha = LocalDate.now();

        CitaResponseDTO citaProgramada = CitaResponseDTO.builder()
                .idCita(1L)
                .estado("PROGRAMADA")
                .build();

        CitaResponseDTO citaAtendida = CitaResponseDTO.builder()
                .idCita(2L)
                .estado("ATENDIDA")
                .build();

        CitaResponseDTO citaCancelada = CitaResponseDTO.builder()
                .idCita(3L)
                .estado("CANCELADA")
                .build();

        List<CitaResponseDTO> citas = List.of(citaProgramada, citaAtendida, citaCancelada);

        when(citaService.listarPorRangoFechas(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(citas);

        CalendarioCitasDTO result = citaFacadeService.obtenerCalendarioCitas(fecha);

        assertEquals(3, result.getTotalCitas(), "Debe haber 3 citas");
        assertEquals(1, result.getCitasAtendidas(), "Debe haber 1 cita atendida");
        assertEquals(1, result.getCitasProgramadas(), "Debe haber 1 cita programada");
        assertEquals(1, result.getCitasCanceladas(), "Debe haber 1 cita cancelada");
    }

    @Test
    @DisplayName("Debe incluir todos los DTOs necesarios en la respuesta de cancelación")
    void debeIncluirDatosCompletosEnCancelacion() {
        CitaResponseDTO citaCancelada = CitaResponseDTO.builder()
                .idCita(1L)
                .fechaCita(LocalDate.now())
                .horaCita(LocalTime.of(10, 0))
                .motivo("Consulta")
                .estado("CANCELADA")
                .build();

        when(citaService.cancelar(anyLong(), anyString(), anyString())).thenReturn(citaCancelada);

        ResultadoCitaConNotificacionDTO result = citaFacadeService.cancelarCitaConNotificacion(1L, "No disponible", "admin");

        assertNotNull(result);
        assertNotNull(result.getCita());
        assertNotNull(result.getMensaje());
        assertEquals(1L, result.getCita().getIdCita());
        assertEquals("CANCELADA", result.getCita().getEstado());
    }

    @Test
    @DisplayName("Debe manejar múltiples citas en el calendario")
    void debeManejartMultiplesCitasEnCalendario() {
        LocalDate fecha = LocalDate.now();

        List<CitaResponseDTO> citas = List.of(
                CitaResponseDTO.builder().idCita(1L).horaCita(LocalTime.of(9, 0)).estado("PROGRAMADA").build(),
                CitaResponseDTO.builder().idCita(2L).horaCita(LocalTime.of(10, 0)).estado("PROGRAMADA").build(),
                CitaResponseDTO.builder().idCita(3L).horaCita(LocalTime.of(11, 0)).estado("ATENDIDA").build(),
                CitaResponseDTO.builder().idCita(4L).horaCita(LocalTime.of(12, 0)).estado("PROGRAMADA").build(),
                CitaResponseDTO.builder().idCita(5L).horaCita(LocalTime.of(14, 0)).estado("CANCELADA").build()
        );

        when(citaService.listarPorRangoFechas(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(citas);

        CalendarioCitasDTO result = citaFacadeService.obtenerCalendarioCitas(fecha);

        assertEquals(5, result.getTotalCitas());
        assertEquals(3, result.getCitasProgramadas());
        assertEquals(1, result.getCitasAtendidas());
        assertEquals(1, result.getCitasCanceladas());
        assertEquals(5, result.getCitas().size());
    }

    @Test
    @DisplayName("Debe verificar que se llama a cancelar antes de crear en reprogramación")
    void debeVerificarOrdenOperacionesEnReprogramacion() {
        CitaResponseDTO nuevaCita = CitaResponseDTO.builder()
                .idCita(2L)
                .estado("PROGRAMADA")
                .build();

        when(citaService.cancelar(anyLong(), anyString(), anyString())).thenReturn(responseDTO);
        when(citaService.crear(any(CitaRequestDTO.class))).thenReturn(nuevaCita);

        citaFacadeService.reprogramarCitaConNotificacion(1L, requestDTO);

        InOrder inOrder = org.mockito.Mockito.inOrder(citaService);
        inOrder.verify(citaService).cancelar(1L, "Reprogramación de cita", "Sistema");
        inOrder.verify(citaService).crear(any(CitaRequestDTO.class));
    }
}
