package com.veterinaria.clinica_veternica.service;

import com.veterinaria.clinica_veternica.domain.agenda.Cita;
import com.veterinaria.clinica_veternica.domain.agenda.EstadoCita;
import com.veterinaria.clinica_veternica.domain.agenda.Servicio;
import com.veterinaria.clinica_veternica.domain.paciente.Mascota;
import com.veterinaria.clinica_veternica.domain.usuario.Veterinario;
import com.veterinaria.clinica_veternica.dto.request.agenda.CitaRequestDTO;
import com.veterinaria.clinica_veternica.dto.response.agenda.CitaResponseDTO;
import com.veterinaria.clinica_veternica.exception.ResourceNotFoundException;
import com.veterinaria.clinica_veternica.mapper.agenda.CitaMapper;
import com.veterinaria.clinica_veternica.patterns.behavioral.chain.*;
import com.veterinaria.clinica_veternica.patterns.behavioral.mediator.CitaMediator;
import com.veterinaria.clinica_veternica.patterns.behavioral.template.AtencionCirugia;
import com.veterinaria.clinica_veternica.patterns.behavioral.template.AtencionConsultaGeneral;
import com.veterinaria.clinica_veternica.patterns.behavioral.template.AtencionEmergencia;
import com.veterinaria.clinica_veternica.repository.*;
import com.veterinaria.clinica_veternica.service.impl.CitaServiceImpl;
import com.veterinaria.clinica_veternica.service.impl.CitaPriceCalculationService;
import com.veterinaria.clinica_veternica.service.impl.CitaValidationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Tests básicos CRUD para CitaService
 */
@ExtendWith(MockitoExtension.class)
class CitaServiceTest {

    @Mock
    private CitaRepository citaRepository;

    @Mock
    private MascotaRepository mascotaRepository;

    @Mock
    private VeterinarioRepository veterinarioRepository;

    @Mock
    private ServicioRepository servicioRepository;

    @Mock
    private CitaMapper citaMapper;

    @Mock
    private CitaMediator citaMediator;

    @Mock
    private ValidacionDatosHandler validacionDatosHandler;

    @Mock
    private ValidacionDisponibilidadHandler validacionDisponibilidadHandler;

    @Mock
    private ValidacionPermisoHandler validacionPermisoHandler;

    @Mock
    private ValidacionStockHandler validacionStockHandler;

    @Mock
    private CitaValidationService citaValidationService;

    @Mock
    private CitaPriceCalculationService citaPriceCalculationService;

    @Mock
    private AtencionConsultaGeneral atencionConsultaGeneral;

    @Mock
    private AtencionCirugia atencionCirugia;

    @Mock
    private AtencionEmergencia atencionEmergencia;

    @InjectMocks
    private CitaServiceImpl citaService;

    private CitaRequestDTO requestDTO;
    private Mascota mascota;
    private Veterinario veterinario;
    private Servicio servicio;
    private Cita cita;
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
                .observaciones("Primera visita")
                .esEmergencia(false)
                .build();

        mascota = Mascota.builder()
                .idMascota(1L)
                .nombre("Max")
                .build();

        veterinario = Veterinario.builder()
                .idPersonal(1L)
                .nombres("Dr. Carlos")
                .apellidos("García")
                .build();

        servicio = Servicio.builder()
                .idServicio(1L)
                .nombre("Consulta General")
                .precio(new BigDecimal("50000"))
                .duracionEstimadaMinutos(30)
                .build();

        cita = Cita.builder()
                .idCita(1L)
                .mascota(mascota)
                .veterinario(veterinario)
                .servicio(servicio)
                .fechaCita(requestDTO.getFechaCita())
                .horaCita(requestDTO.getHoraCita())
                .estado(EstadoCita.PROGRAMADA)
                .build();

        responseDTO = CitaResponseDTO.builder()
                .idCita(1L)
                .estado("PROGRAMADA")
                .build();
    }

    @Test
    @DisplayName("CREATE - Debe crear una cita exitosamente")
    void testCrearCitaExitoso() {
        when(mascotaRepository.findById(1L)).thenReturn(Optional.of(mascota));
        when(veterinarioRepository.findById(1L)).thenReturn(Optional.of(veterinario));
        when(servicioRepository.findById(1L)).thenReturn(Optional.of(servicio));
        
        // Mock del cálculo de precio
        when(citaPriceCalculationService.calcularPrecioFinal(any(Servicio.class), any(CitaRequestDTO.class)))
                .thenReturn(new BigDecimal("50000"));
        
        // Mock de la validación (no lanza excepción)
        doNothing().when(citaValidationService).validarCita(any(Cita.class));
        
        when(citaMediator.crearCita(any(Cita.class))).thenReturn(cita);
        when(citaMapper.toResponseDTO(cita)).thenReturn(responseDTO);

        CitaResponseDTO resultado = citaService.crear(requestDTO);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getIdCita());
        verify(citaMediator, times(1)).crearCita(any(Cita.class));
        verify(citaValidationService, times(1)).validarCita(any(Cita.class));
        verify(citaPriceCalculationService, times(1)).calcularPrecioFinal(any(Servicio.class), any(CitaRequestDTO.class));
    }

    @Test
    @DisplayName("READ - Debe buscar cita por ID exitosamente")
    void testBuscarCitaPorIdExitoso() {
        when(citaRepository.findById(1L)).thenReturn(Optional.of(cita));
        when(citaMapper.toResponseDTO(cita)).thenReturn(responseDTO);

        CitaResponseDTO resultado = citaService.buscarPorId(1L);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getIdCita());
    }

    @Test
    @DisplayName("READ - Debe lanzar excepción cuando cita no existe")
    void testBuscarCitaPorIdNoEncontrado() {
        when(citaRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> citaService.buscarPorId(999L));
    }

    @Test
    @DisplayName("UPDATE - Debe actualizar una cita exitosamente")
    void testActualizarCitaExitoso() {
        when(citaRepository.findById(1L)).thenReturn(Optional.of(cita));
        
        // Mock de la validación (no lanza excepción)
        doNothing().when(citaValidationService).validarCita(any(Cita.class));
        
        when(citaRepository.save(any(Cita.class))).thenReturn(cita);
        when(citaMapper.toResponseDTO(cita)).thenReturn(responseDTO);

        CitaResponseDTO resultado = citaService.actualizar(1L, requestDTO);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getIdCita());
        verify(citaRepository, times(1)).save(any(Cita.class));
        verify(citaValidationService, times(1)).validarCita(any(Cita.class));
    }

    @Test
    @DisplayName("DELETE - Debe cancelar una cita exitosamente")
    void testCancelarCitaExitoso() {
        // El mediator maneja el save internamente
        doNothing().when(citaMediator).cancelarCita(anyLong(), anyString());
        when(citaRepository.findById(1L)).thenReturn(Optional.of(cita));
        when(citaMapper.toResponseDTO(cita)).thenReturn(responseDTO);

        CitaResponseDTO resultado = citaService.cancelar(1L, "Cliente no puede asistir", "Admin");

        assertNotNull(resultado);
        verify(citaMediator, times(1)).cancelarCita(1L, "Cliente no puede asistir");
        verify(citaRepository, times(1)).findById(1L);
    }
}
