package com.veterinaria.clinica_veternica.service;

import com.veterinaria.clinica_veternica.domain.agenda.Horario;
import com.veterinaria.clinica_veternica.domain.usuario.Veterinario;
import com.veterinaria.clinica_veternica.dto.request.agenda.HorarioRequestDTO;
import com.veterinaria.clinica_veternica.dto.response.agenda.HorarioResponseDTO;
import com.veterinaria.clinica_veternica.exception.ResourceNotFoundException;
import com.veterinaria.clinica_veternica.mapper.agenda.HorarioMapper;
import com.veterinaria.clinica_veternica.repository.HorarioRepository;
import com.veterinaria.clinica_veternica.repository.VeterinarioRepository;
import com.veterinaria.clinica_veternica.service.impl.HorarioServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HorarioServiceTest {

    @Mock
    private HorarioRepository horarioRepository;

    @Mock
    private VeterinarioRepository veterinarioRepository;

    @Mock
    private HorarioMapper horarioMapper;

    @InjectMocks
    private HorarioServiceImpl horarioService;

    private HorarioRequestDTO requestDTO;
    private Horario horario;
    private HorarioResponseDTO responseDTO;
    private Veterinario veterinario;

    @BeforeEach
    void setUp() {
        veterinario = Veterinario.builder()
                .idPersonal(1L)
                .nombres("Juan")
                .apellidos("Pérez")
                .build();

        requestDTO = HorarioRequestDTO.builder()
                .idVeterinario(1L)
                .diaSemana("MONDAY")
                .horaInicio(LocalTime.of(9, 0))
                .horaFin(LocalTime.of(17, 0))
                .build();

        horario = Horario.builder()
                .idHorario(1L)
                .veterinario(veterinario)
                .diaSemana(DayOfWeek.MONDAY)
                .horaInicio(LocalTime.of(9, 0))
                .horaFin(LocalTime.of(17, 0))
                .activo(true)
                .build();

        responseDTO = HorarioResponseDTO.builder()
                .idHorario(1L)
                .diaSemana("MONDAY")
                .horaInicio(LocalTime.of(9, 0))
                .horaFin(LocalTime.of(17, 0))
                .activo(true)
                .build();
    }

    @Test
    @DisplayName("CREATE - Debe crear un horario exitosamente")
    void testCrearHorarioExitoso() {
        when(veterinarioRepository.findById(1L)).thenReturn(Optional.of(veterinario));
        when(horarioRepository.findByVeterinario(any(Veterinario.class))).thenReturn(Arrays.asList());
        when(horarioMapper.toEntity(requestDTO)).thenReturn(horario);
        when(horarioRepository.save(any(Horario.class))).thenReturn(horario);
        when(horarioMapper.toResponseDTO(horario)).thenReturn(responseDTO);

        HorarioResponseDTO resultado = horarioService.crear(requestDTO);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getIdHorario());
        verify(horarioRepository, times(1)).save(any(Horario.class));
    }

    @Test
    @DisplayName("CREATE - Debe lanzar excepción cuando veterinario no existe")
    void testCrearHorarioVeterinarioNoExiste() {
        when(veterinarioRepository.findById(999L)).thenReturn(Optional.empty());

        requestDTO.setIdVeterinario(999L);
        assertThrows(ResourceNotFoundException.class, () -> horarioService.crear(requestDTO));
    }

    @Test
    @DisplayName("READ - Debe buscar horario por ID exitosamente")
    void testBuscarHorarioPorIdExitoso() {
        when(horarioRepository.findById(1L)).thenReturn(Optional.of(horario));
        when(horarioMapper.toResponseDTO(horario)).thenReturn(responseDTO);

        HorarioResponseDTO resultado = horarioService.buscarPorId(1L);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getIdHorario());
    }

    @Test
    @DisplayName("READ - Debe lanzar excepción cuando horario no existe")
    void testBuscarHorarioPorIdNoEncontrado() {
        when(horarioRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> horarioService.buscarPorId(999L));
    }

    @Test
    @DisplayName("READ - Debe listar todos los horarios")
    void testListarTodosLosHorarios() {
        List<Horario> horarios = Arrays.asList(horario);
        List<HorarioResponseDTO> responseDTOs = Arrays.asList(responseDTO);
        when(horarioRepository.findAll()).thenReturn(horarios);
        when(horarioMapper.toResponseDTOList(horarios)).thenReturn(responseDTOs);

        List<HorarioResponseDTO> resultado = horarioService.listarTodos();

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
    }
}

