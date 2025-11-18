package com.veterinaria.clinica_veternica.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.veterinaria.clinica_veternica.dto.request.agenda.HorarioRequestDTO;
import com.veterinaria.clinica_veternica.dto.response.agenda.HorarioResponseDTO;
import com.veterinaria.clinica_veternica.security.jwt.JwtAuthenticationFilter;
import com.veterinaria.clinica_veternica.security.jwt.JwtProperties;
import com.veterinaria.clinica_veternica.security.jwt.JwtUtils;
import com.veterinaria.clinica_veternica.security.service.UserDetailsServiceImpl;
import com.veterinaria.clinica_veternica.service.interfaces.IHorarioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests básicos CRUD para HorarioController
 */
@WebMvcTest(controllers = HorarioController.class, excludeAutoConfiguration = {
        org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class
})
@AutoConfigureMockMvc(addFilters = false)
class HorarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private IHorarioService horarioService;

    @MockitoBean
    private JwtUtils jwtUtils;

    @MockitoBean
    private UserDetailsServiceImpl userDetailsService;

    @MockitoBean
    private JwtProperties jwtProperties;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    private HorarioRequestDTO horarioRequestDTO;
    private HorarioResponseDTO horarioResponseDTO;

    @BeforeEach
    void setUp() {
        horarioRequestDTO = new HorarioRequestDTO();
        horarioRequestDTO.setIdVeterinario(1L);
        horarioRequestDTO.setDiaSemana(DayOfWeek.MONDAY.name());
        horarioRequestDTO.setHoraInicio(LocalTime.of(9, 0));
        horarioRequestDTO.setHoraFin(LocalTime.of(17, 0));
        horarioRequestDTO.setActivo(true);

        horarioResponseDTO = new HorarioResponseDTO();
        horarioResponseDTO.setIdHorario(1L);
        horarioResponseDTO.setDiaSemana(DayOfWeek.MONDAY.name());
        horarioResponseDTO.setHoraInicio(LocalTime.of(9, 0));
        horarioResponseDTO.setHoraFin(LocalTime.of(17, 0));
        horarioResponseDTO.setActivo(true);
    }

    @Test
    @DisplayName("CREATE - Debe crear un horario exitosamente")
    @WithMockUser
    void debeCrearHorarioExitosamente() throws Exception {
        when(horarioService.crear(any(HorarioRequestDTO.class))).thenReturn(horarioResponseDTO);

        mockMvc.perform(post("/api/horarios")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(horarioRequestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idHorario").value(1))
                .andExpect(jsonPath("$.diaSemana").value("MONDAY"));

        verify(horarioService).crear(any(HorarioRequestDTO.class));
    }

    @Test
    @DisplayName("READ - Debe buscar horario por ID")
    @WithMockUser
    void debeBuscarHorarioPorId() throws Exception {
        when(horarioService.buscarPorId(1L)).thenReturn(horarioResponseDTO);

        mockMvc.perform(get("/api/horarios/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idHorario").value(1))
                .andExpect(jsonPath("$.diaSemana").value("MONDAY"));

        verify(horarioService).buscarPorId(1L);
    }

    @Test
    @DisplayName("READ - Debe listar todos los horarios")
    @WithMockUser
    void debeListarTodosLosHorarios() throws Exception {
        List<HorarioResponseDTO> horarios = Arrays.asList(horarioResponseDTO);
        when(horarioService.listarTodos()).thenReturn(horarios);

        mockMvc.perform(get("/api/horarios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idHorario").value(1))
                .andExpect(jsonPath("$.length()").value(1));

        verify(horarioService).listarTodos();
    }

    @Test
    @DisplayName("READ - Debe listar horarios activos")
    @WithMockUser
    void debeListarHorariosActivos() throws Exception {
        List<HorarioResponseDTO> horarios = Arrays.asList(horarioResponseDTO);
        when(horarioService.listarActivos()).thenReturn(horarios);

        mockMvc.perform(get("/api/horarios/activos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idHorario").value(1));

        verify(horarioService).listarActivos();
    }

    @Test
    @DisplayName("READ - Debe listar horarios por veterinario")
    @WithMockUser
    void debeListarHorariosPorVeterinario() throws Exception {
        List<HorarioResponseDTO> horarios = Arrays.asList(horarioResponseDTO);
        when(horarioService.listarPorVeterinario(1L)).thenReturn(horarios);

        mockMvc.perform(get("/api/horarios/veterinario/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idHorario").value(1));

        verify(horarioService).listarPorVeterinario(1L);
    }

    @Test
    @DisplayName("UPDATE - Debe actualizar un horario")
    @WithMockUser
    void debeActualizarHorario() throws Exception {
        when(horarioService.actualizar(eq(1L), any(HorarioRequestDTO.class))).thenReturn(horarioResponseDTO);

        mockMvc.perform(put("/api/horarios/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(horarioRequestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idHorario").value(1));

        verify(horarioService).actualizar(eq(1L), any(HorarioRequestDTO.class));
    }

    @Test
    @DisplayName("DELETE - Debe eliminar un horario")
    @WithMockUser
    void debeEliminarHorario() throws Exception {
        mockMvc.perform(delete("/api/horarios/1")
                        .with(csrf()))
                .andExpect(status().isNoContent());

        verify(horarioService).eliminar(1L);
    }
}

