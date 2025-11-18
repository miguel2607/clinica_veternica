package com.veterinaria.clinica_veternica.service;

import com.veterinaria.clinica_veternica.domain.clinico.EvolucionClinica;
import com.veterinaria.clinica_veternica.domain.clinico.HistoriaClinica;
import com.veterinaria.clinica_veternica.domain.usuario.Veterinario;
import com.veterinaria.clinica_veternica.dto.request.clinico.EvolucionClinicaRequestDTO;
import com.veterinaria.clinica_veternica.dto.response.clinico.EvolucionClinicaResponseDTO;
import com.veterinaria.clinica_veternica.exception.ResourceNotFoundException;
import com.veterinaria.clinica_veternica.mapper.clinico.EvolucionClinicaMapper;
import com.veterinaria.clinica_veternica.repository.EvolucionClinicaRepository;
import com.veterinaria.clinica_veternica.repository.HistoriaClinicaRepository;
import com.veterinaria.clinica_veternica.repository.VeterinarioRepository;
import com.veterinaria.clinica_veternica.service.impl.EvolucionClinicaServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Tests básicos CRUD para EvolucionClinicaService
 */
@ExtendWith(MockitoExtension.class)
class EvolucionClinicaServiceTest {

    @Mock
    private EvolucionClinicaRepository evolucionClinicaRepository;

    @Mock
    private HistoriaClinicaRepository historiaClinicaRepository;

    @Mock
    private VeterinarioRepository veterinarioRepository;

    @Mock
    private EvolucionClinicaMapper evolucionClinicaMapper;

    @InjectMocks
    private EvolucionClinicaServiceImpl evolucionClinicaService;

    private EvolucionClinicaRequestDTO requestDTO;
    private EvolucionClinica evolucion;
    private EvolucionClinicaResponseDTO responseDTO;
    private HistoriaClinica historiaClinica;
    private Veterinario veterinario;

    @BeforeEach
    void setUp() {
        historiaClinica = HistoriaClinica.builder()
                .idHistoriaClinica(1L)
                .numeroHistoria("HC-001")
                .build();

        veterinario = Veterinario.builder()
                .idPersonal(1L)
                .nombres("Dr. Carlos")
                .apellidos("García")
                .build();

        requestDTO = EvolucionClinicaRequestDTO.builder()
                .idHistoriaClinica(1L)
                .idVeterinario(1L)
                .tipoEvolucion("SEGUIMIENTO")
                .motivoConsulta("Consulta de rutina")
                .hallazgosExamen("Estado general bueno")
                .planTratamiento("Continuar con dieta actual")
                .build();

        evolucion = EvolucionClinica.builder()
                .idEvolucion(1L)
                .historiaClinica(historiaClinica)
                .veterinario(veterinario)
                .tipoEvolucion("SEGUIMIENTO")
                .descripcion("Motivo de consulta: Consulta de rutina. Hallazgos del examen: Estado general bueno")
                .plan("Continuar con dieta actual")
                .fechaEvolucion(LocalDateTime.now())
                .estadoPaciente("ESTABLE")
                .build();

        responseDTO = EvolucionClinicaResponseDTO.builder()
                .idEvolucion(1L)
                .motivoConsulta("Consulta de rutina")
                .hallazgosExamen("Estado general bueno")
                .planTratamiento("Continuar con dieta actual")
                .tipoEvolucion("SEGUIMIENTO")
                .build();
    }

    @Test
    @DisplayName("CREATE - Debe crear una evolución clínica exitosamente")
    void testCrearEvolucionClinicaExitoso() {
        when(historiaClinicaRepository.findById(1L)).thenReturn(Optional.of(historiaClinica));
        when(veterinarioRepository.findById(1L)).thenReturn(Optional.of(veterinario));
        when(evolucionClinicaMapper.toEntity(requestDTO)).thenReturn(evolucion);
        when(evolucionClinicaRepository.save(any(EvolucionClinica.class))).thenReturn(evolucion);
        when(evolucionClinicaMapper.toResponseDTO(evolucion)).thenReturn(responseDTO);

        EvolucionClinicaResponseDTO resultado = evolucionClinicaService.crear(1L, requestDTO);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getIdEvolucion());
        verify(evolucionClinicaRepository, times(1)).save(any(EvolucionClinica.class));
    }

    @Test
    @DisplayName("CREATE - Debe lanzar excepción si historia clínica no existe")
    void testCrearEvolucionClinicaHistoriaNoExiste() {
        when(historiaClinicaRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> evolucionClinicaService.crear(999L, requestDTO));
    }

    @Test
    @DisplayName("CREATE - Debe lanzar excepción si veterinario no existe")
    void testCrearEvolucionClinicaVeterinarioNoExiste() {
        when(historiaClinicaRepository.findById(1L)).thenReturn(Optional.of(historiaClinica));
        when(veterinarioRepository.findById(999L)).thenReturn(Optional.empty());

        requestDTO.setIdVeterinario(999L);
        assertThrows(ResourceNotFoundException.class, () -> evolucionClinicaService.crear(1L, requestDTO));
    }

    @Test
    @DisplayName("READ - Debe listar evoluciones clínicas por historia clínica exitosamente")
    void testListarEvolucionesPorHistoriaClinicaExitoso() {
        List<EvolucionClinica> evoluciones = Arrays.asList(evolucion);
        List<EvolucionClinicaResponseDTO> responseDTOs = Arrays.asList(responseDTO);

        when(historiaClinicaRepository.findById(1L)).thenReturn(Optional.of(historiaClinica));
        when(evolucionClinicaRepository.findByHistoriaOrdenadas(historiaClinica)).thenReturn(evoluciones);
        when(evolucionClinicaMapper.toResponseDTOList(evoluciones)).thenReturn(responseDTOs);

        List<EvolucionClinicaResponseDTO> resultado = evolucionClinicaService.listarPorHistoriaClinica(1L);

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals(1L, resultado.get(0).getIdEvolucion());
    }

    @Test
    @DisplayName("READ - Debe lanzar excepción cuando historia clínica no existe al listar")
    void testListarEvolucionesHistoriaNoExiste() {
        when(historiaClinicaRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> evolucionClinicaService.listarPorHistoriaClinica(999L));
    }
}


