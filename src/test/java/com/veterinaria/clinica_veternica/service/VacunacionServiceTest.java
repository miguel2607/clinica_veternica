package com.veterinaria.clinica_veternica.service;

import com.veterinaria.clinica_veternica.domain.clinico.HistoriaClinica;
import com.veterinaria.clinica_veternica.domain.clinico.Vacunacion;
import com.veterinaria.clinica_veternica.domain.paciente.Mascota;
import com.veterinaria.clinica_veternica.domain.usuario.Veterinario;
import com.veterinaria.clinica_veternica.dto.request.clinico.VacunacionRequestDTO;
import com.veterinaria.clinica_veternica.dto.response.clinico.VacunacionResponseDTO;
import com.veterinaria.clinica_veternica.exception.ResourceNotFoundException;
import com.veterinaria.clinica_veternica.mapper.clinico.VacunacionMapper;
import com.veterinaria.clinica_veternica.repository.HistoriaClinicaRepository;
import com.veterinaria.clinica_veternica.repository.VacunacionRepository;
import com.veterinaria.clinica_veternica.repository.VeterinarioRepository;
import com.veterinaria.clinica_veternica.service.impl.VacunacionServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VacunacionServiceTest {

    @Mock
    private VacunacionRepository vacunacionRepository;

    @Mock
    private HistoriaClinicaRepository historiaClinicaRepository;

    @Mock
    private VeterinarioRepository veterinarioRepository;

    @Mock
    private VacunacionMapper vacunacionMapper;

    @InjectMocks
    private VacunacionServiceImpl vacunacionService;

    private VacunacionRequestDTO requestDTO;
    private Vacunacion vacunacion;
    private VacunacionResponseDTO responseDTO;
    private HistoriaClinica historiaClinica;
    private Veterinario veterinario;
    private Mascota mascota;

    @BeforeEach
    void setUp() {
        mascota = Mascota.builder()
                .idMascota(1L)
                .nombre("Max")
                .build();

        veterinario = Veterinario.builder()
                .idPersonal(1L)
                .nombres("Juan")
                .apellidos("Pérez")
                .build();

        historiaClinica = HistoriaClinica.builder()
                .idHistoriaClinica(1L)
                .mascota(mascota)
                .build();

        requestDTO = new VacunacionRequestDTO();
        requestDTO.setNombreVacuna("Rabia");
        requestDTO.setFechaAplicacion(LocalDate.now());
        requestDTO.setFechaProximaDosis(LocalDate.now().plusMonths(12));
        requestDTO.setLote("LOTE123");
        requestDTO.setIdVeterinario(1L);

        vacunacion = Vacunacion.builder()
                .idVacunacion(1L)
                .nombreVacuna("Rabia")
                .fechaAplicacion(LocalDate.now())
                .fechaProximaDosis(LocalDate.now().plusMonths(12))
                .lote("LOTE123")
                .historiaClinica(historiaClinica)
                .mascota(mascota)
                .veterinario(veterinario)
                .build();

        responseDTO = new VacunacionResponseDTO();
        responseDTO.setIdVacunacion(1L);
        responseDTO.setNombreVacuna("Rabia");
        responseDTO.setFechaAplicacion(LocalDate.now());
    }

    @Test
    @DisplayName("CREATE - Debe crear vacunación exitosamente")
    void testCrearVacunacionExitoso() {
        when(historiaClinicaRepository.findById(1L)).thenReturn(Optional.of(historiaClinica));
        when(veterinarioRepository.findById(1L)).thenReturn(Optional.of(veterinario));
        when(vacunacionMapper.toEntity(requestDTO)).thenReturn(vacunacion);
        when(vacunacionRepository.save(any(Vacunacion.class))).thenReturn(vacunacion);
        when(vacunacionMapper.toResponseDTO(vacunacion)).thenReturn(responseDTO);

        VacunacionResponseDTO resultado = vacunacionService.crear(1L, requestDTO);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getIdVacunacion());
        assertEquals("Rabia", resultado.getNombreVacuna());
        verify(vacunacionRepository, times(1)).save(any(Vacunacion.class));
    }

    @Test
    @DisplayName("CREATE - Debe lanzar excepción cuando historia clínica no existe")
    void testCrearVacunacionHistoriaNoExiste() {
        when(historiaClinicaRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> vacunacionService.crear(999L, requestDTO));
    }

    @Test
    @DisplayName("CREATE - Debe lanzar excepción cuando veterinario no existe")
    void testCrearVacunacionVeterinarioNoExiste() {
        when(historiaClinicaRepository.findById(1L)).thenReturn(Optional.of(historiaClinica));
        when(veterinarioRepository.findById(999L)).thenReturn(Optional.empty());

        requestDTO.setIdVeterinario(999L);
        assertThrows(ResourceNotFoundException.class, () -> vacunacionService.crear(1L, requestDTO));
    }

    @Test
    @DisplayName("READ - Debe listar vacunaciones por historia clínica")
    void testListarVacunacionesPorHistoriaClinica() {
        List<Vacunacion> vacunaciones = Arrays.asList(vacunacion);
        List<VacunacionResponseDTO> responseDTOs = Arrays.asList(responseDTO);
        when(historiaClinicaRepository.findById(1L)).thenReturn(Optional.of(historiaClinica));
        when(vacunacionRepository.findByHistoriaClinica(historiaClinica)).thenReturn(vacunaciones);
        when(vacunacionMapper.toResponseDTOList(vacunaciones)).thenReturn(responseDTOs);

        List<VacunacionResponseDTO> resultado = vacunacionService.listarPorHistoriaClinica(1L);

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
    }
}

