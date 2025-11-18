package com.veterinaria.clinica_veternica.patterns.structural.facade;

import com.veterinaria.clinica_veternica.dto.request.agenda.CitaRequestDTO;
import com.veterinaria.clinica_veternica.dto.request.clinico.EvolucionClinicaRequestDTO;
import com.veterinaria.clinica_veternica.dto.response.agenda.CitaResponseDTO;
import com.veterinaria.clinica_veternica.dto.response.clinico.EvolucionClinicaResponseDTO;
import com.veterinaria.clinica_veternica.dto.response.clinico.HistoriaClinicaResponseDTO;
import com.veterinaria.clinica_veternica.dto.response.paciente.MascotaResponseDTO;
import com.veterinaria.clinica_veternica.service.interfaces.ICitaService;
import com.veterinaria.clinica_veternica.service.interfaces.IEvolucionClinicaService;
import com.veterinaria.clinica_veternica.service.interfaces.IHistoriaClinicaService;
import com.veterinaria.clinica_veternica.service.interfaces.IMascotaService;
import com.veterinaria.clinica_veternica.service.interfaces.IInventarioService;
import com.veterinaria.clinica_veternica.service.interfaces.INotificacionService;
import com.veterinaria.clinica_veternica.service.interfaces.IPropietarioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClinicaFacadeTest {

    @Mock
    private ICitaService citaService;

    @Mock
    private IMascotaService mascotaService;

    @Mock
    private IEvolucionClinicaService evolucionClinicaService;

    @Mock
    private IHistoriaClinicaService historiaClinicaService;

    @Mock
    private IInventarioService inventarioService;

    @Mock
    private INotificacionService notificacionService;

    @Mock
    private IPropietarioService propietarioService;

    @Mock
    private com.veterinaria.clinica_veternica.service.interfaces.IVeterinarioService veterinarioService;

    @Mock
    private com.veterinaria.clinica_veternica.service.interfaces.IHorarioService horarioService;

    @Mock
    private com.veterinaria.clinica_veternica.service.interfaces.IUsuarioService usuarioService;

    @InjectMocks
    private ClinicaFacade clinicaFacade;

    private CitaRequestDTO citaRequestDTO;
    private CitaResponseDTO citaResponseDTO;
    private MascotaResponseDTO mascotaResponseDTO;

    @BeforeEach
    void setUp() {
        citaRequestDTO = new CitaRequestDTO();
        citaRequestDTO.setIdMascota(1L);
        citaRequestDTO.setIdVeterinario(1L);
        citaRequestDTO.setIdServicio(1L);
        citaRequestDTO.setFechaCita(LocalDate.now().plusDays(1));
        citaRequestDTO.setHoraCita(LocalTime.of(10, 0));
        citaRequestDTO.setMotivo("Consulta de rutina");

        citaResponseDTO = new CitaResponseDTO();
        citaResponseDTO.setIdCita(1L);
        citaResponseDTO.setFechaCita(LocalDate.now().plusDays(1));
        citaResponseDTO.setHoraCita(LocalTime.of(10, 0));

        mascotaResponseDTO = new MascotaResponseDTO();
        mascotaResponseDTO.setIdMascota(1L);
        mascotaResponseDTO.setNombre("Max");
    }

    @Test
    @DisplayName("Facade - Debe crear cita con notificación exitosamente")
    void debeCrearCitaConNotificacion() {
        when(citaService.crear(any(CitaRequestDTO.class))).thenReturn(citaResponseDTO);

        CitaResponseDTO resultado = clinicaFacade.crearCitaConNotificacion(citaRequestDTO);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getIdCita());
        verify(citaService, times(1)).crear(any(CitaRequestDTO.class));
    }

    @Test
    @DisplayName("Facade - Debe procesar atención completa exitosamente")
    void debeProcesarAtencionCompleta() {
        EvolucionClinicaRequestDTO evolucionRequest = EvolucionClinicaRequestDTO.builder()
                .idHistoriaClinica(1L)
                .idVeterinario(1L)
                .tipoEvolucion("Consulta General")
                .motivoConsulta("Consulta de rutina para revisión general")
                .hallazgosExamen("Examen físico completo realizado, signos vitales normales")
                .diagnostico("Diagnóstico")
                .planTratamiento("Tratamiento")
                .observaciones("Observaciones")
                .build();

        // Crear MascotaSimpleDTO para el CitaResponseDTO
        CitaResponseDTO.MascotaSimpleDTO mascotaSimpleDTO = CitaResponseDTO.MascotaSimpleDTO.builder()
                .idMascota(1L)
                .nombre("Max")
                .build();
        citaResponseDTO.setMascota(mascotaSimpleDTO);
        
        HistoriaClinicaResponseDTO historiaClinica = new HistoriaClinicaResponseDTO();
        historiaClinica.setIdHistoriaClinica(1L);
        
        EvolucionClinicaResponseDTO evolucionResponse = new EvolucionClinicaResponseDTO();
        evolucionResponse.setIdEvolucion(1L);

        when(citaService.marcarComoAtendida(1L)).thenReturn(citaResponseDTO);
        when(historiaClinicaService.buscarPorMascota(1L)).thenReturn(historiaClinica);
        when(evolucionClinicaService.crear(eq(1L), any(EvolucionClinicaRequestDTO.class)))
                .thenReturn(evolucionResponse);

        Map<String, Object> resultado = clinicaFacade.procesarAtencionCompleta(1L, evolucionRequest);

        assertNotNull(resultado);
        verify(citaService, times(1)).marcarComoAtendida(1L);
        verify(historiaClinicaService, times(1)).buscarPorMascota(1L);
        verify(evolucionClinicaService, times(1)).crear(eq(1L), any(EvolucionClinicaRequestDTO.class));
    }

    @Test
    @DisplayName("Facade - Debe obtener información completa de mascota")
    void debeObtenerInformacionCompletaMascota() {
        HistoriaClinicaResponseDTO historiaClinica = new HistoriaClinicaResponseDTO();
        historiaClinica.setIdHistoriaClinica(1L);
        
        when(mascotaService.buscarPorId(1L)).thenReturn(mascotaResponseDTO);
        when(historiaClinicaService.buscarPorMascota(1L)).thenReturn(historiaClinica);
        when(citaService.listarPorMascota(1L)).thenReturn(java.util.Arrays.asList());

        Map<String, Object> resultado = clinicaFacade.obtenerInformacionCompletaMascota(1L);

        assertNotNull(resultado);
        verify(mascotaService, times(1)).buscarPorId(1L);
        verify(historiaClinicaService, times(1)).buscarPorMascota(1L);
        verify(citaService, times(1)).listarPorMascota(1L);
    }

    @Test
    @DisplayName("Facade - Debe obtener dashboard exitosamente")
    void debeObtenerDashboard() {
        when(citaService.listarPorRangoFechas(any(), any())).thenReturn(java.util.Arrays.asList());
        when(citaService.listarProgramadas()).thenReturn(java.util.Arrays.asList());
        when(inventarioService.listarConStockBajo()).thenReturn(java.util.Arrays.asList());
        when(inventarioService.listarAgotados()).thenReturn(java.util.Arrays.asList());
        when(notificacionService.listarTodas()).thenReturn(java.util.Arrays.asList());

        Map<String, Object> resultado = clinicaFacade.obtenerDashboard();

        assertNotNull(resultado);
        verify(citaService, times(1)).listarPorRangoFechas(any(), any());
        verify(citaService, times(1)).listarProgramadas();
        verify(inventarioService, times(1)).listarConStockBajo();
        verify(inventarioService, times(1)).listarAgotados();
        verify(notificacionService, times(1)).listarTodas();
    }

    @Test
    @DisplayName("Facade - Debe obtener estadísticas generales")
    void debeObtenerEstadisticasGenerales() {
        when(mascotaService.listarActivas()).thenReturn(java.util.Arrays.asList(mascotaResponseDTO));
        when(propietarioService.listarActivos()).thenReturn(java.util.Arrays.asList());
        when(veterinarioService.listarActivos()).thenReturn(java.util.Arrays.asList());
        when(citaService.listarPorRangoFechas(any(), any())).thenReturn(java.util.Arrays.asList());
        when(citaService.listarProgramadas()).thenReturn(java.util.Arrays.asList());

        Map<String, Object> resultado = clinicaFacade.obtenerEstadisticasGenerales();

        assertNotNull(resultado);
        verify(mascotaService, times(1)).listarActivas();
        verify(propietarioService, times(1)).listarActivos();
        verify(veterinarioService, times(1)).listarActivos();
    }

    @Test
    @DisplayName("Facade - Debe obtener resumen de inventario")
    void debeObtenerResumenInventario() {
        when(inventarioService.listarTodos()).thenReturn(java.util.Arrays.asList());
        when(inventarioService.listarConStockBajo()).thenReturn(java.util.Arrays.asList());
        when(inventarioService.listarAgotados()).thenReturn(java.util.Arrays.asList());
        when(inventarioService.listarOrdenadosPorValor()).thenReturn(java.util.Arrays.asList());

        Map<String, Object> resultado = clinicaFacade.obtenerResumenInventario();

        assertNotNull(resultado);
        verify(inventarioService, times(1)).listarTodos();
        verify(inventarioService, times(1)).listarConStockBajo();
        verify(inventarioService, times(1)).listarAgotados();
        verify(inventarioService, times(1)).listarOrdenadosPorValor();
    }
}

