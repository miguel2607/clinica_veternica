package com.veterinaria.clinica_veternica.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.veterinaria.clinica_veternica.dto.request.agenda.CitaRequestDTO;
import com.veterinaria.clinica_veternica.dto.response.agenda.CitaResponseDTO;
import com.veterinaria.clinica_veternica.security.jwt.JwtAuthenticationFilter;
import com.veterinaria.clinica_veternica.security.jwt.JwtProperties;
import com.veterinaria.clinica_veternica.security.jwt.JwtUtils;
import com.veterinaria.clinica_veternica.security.service.UserDetailsServiceImpl;
import com.veterinaria.clinica_veternica.service.interfaces.ICitaService;
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
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDate;
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
 * Tests básicos CRUD para CitaController
 */
@WebMvcTest(controllers = CitaController.class, excludeAutoConfiguration = {
        org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class
})
@AutoConfigureMockMvc(addFilters = false)
@TestPropertySource(properties = {
    "spring.dotenv.enabled=false",
    "JWT_SECRET=404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970",
    "JWT_EXPIRATION=86400000"
})
class CitaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ICitaService citaService;

    @MockitoBean
    private JwtUtils jwtUtils;

    @MockitoBean
    private UserDetailsServiceImpl userDetailsService;

    @MockitoBean
    private JwtProperties jwtProperties;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    private CitaRequestDTO citaRequestDTO;
    private CitaResponseDTO citaResponseDTO;

    @BeforeEach
    void setUp() {
        citaRequestDTO = new CitaRequestDTO();
        citaRequestDTO.setIdMascota(1L);
        citaRequestDTO.setIdVeterinario(1L);
        citaRequestDTO.setIdServicio(1L);
        citaRequestDTO.setFechaCita(LocalDate.now().plusDays(1));
        citaRequestDTO.setHoraCita(LocalTime.of(10, 0));
        citaRequestDTO.setMotivo("Consulta de rutina");
        citaRequestDTO.setEsEmergencia(false);

        citaResponseDTO = new CitaResponseDTO();
        citaResponseDTO.setIdCita(1L);
        citaResponseDTO.setFechaCita(LocalDate.now().plusDays(1));
        citaResponseDTO.setHoraCita(LocalTime.of(10, 0));
        citaResponseDTO.setEstado("PROGRAMADA");
        citaResponseDTO.setMotivo("Consulta de rutina");
    }

    @Test
    @DisplayName("CREATE - Debe crear una cita exitosamente")
    @WithMockUser(roles = {"ADMIN"})
    void debeCrearCitaExitosamente() throws Exception {
        when(citaService.crear(any(CitaRequestDTO.class))).thenReturn(citaResponseDTO);

        mockMvc.perform(post("/api/citas")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(citaRequestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idCita").value(1))
                .andExpect(jsonPath("$.estado").value("PROGRAMADA"));

        verify(citaService).crear(any(CitaRequestDTO.class));
    }

    @Test
    @DisplayName("READ - Debe buscar cita por ID")
    @WithMockUser
    void debeBuscarCitaPorId() throws Exception {
        when(citaService.buscarPorId(1L)).thenReturn(citaResponseDTO);

        mockMvc.perform(get("/api/citas/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idCita").value(1))
                .andExpect(jsonPath("$.estado").value("PROGRAMADA"));

        verify(citaService).buscarPorId(1L);
    }

    @Test
    @DisplayName("READ - Debe listar todas las citas")
    @WithMockUser
    void debeListarTodasLasCitas() throws Exception {
        List<CitaResponseDTO> citas = Arrays.asList(citaResponseDTO);
        when(citaService.listarTodos()).thenReturn(citas);

        mockMvc.perform(get("/api/citas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idCita").value(1))
                .andExpect(jsonPath("$.length()").value(1));

        verify(citaService).listarTodos();
    }

    @Test
    @DisplayName("UPDATE - Debe actualizar una cita")
    @WithMockUser(roles = {"ADMIN"})
    void debeActualizarCita() throws Exception {
        citaResponseDTO.setMotivo("Motivo actualizado");
        when(citaService.actualizar(eq(1L), any(CitaRequestDTO.class))).thenReturn(citaResponseDTO);

        mockMvc.perform(put("/api/citas/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(citaRequestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idCita").value(1));

        verify(citaService).actualizar(eq(1L), any(CitaRequestDTO.class));
    }

    @Test
    @DisplayName("DELETE - Debe cancelar una cita")
    @WithMockUser(roles = {"ADMIN"})
    void debeCancelarCita() throws Exception {
        citaResponseDTO.setEstado("CANCELADA");
        when(citaService.cancelar(eq(1L), anyString(), anyString())).thenReturn(citaResponseDTO);

        mockMvc.perform(put("/api/citas/1/cancelar")
                        .with(csrf())
                        .param("motivo", "Cliente no puede asistir")
                        .param("usuario", "Admin"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("CANCELADA"));

        verify(citaService).cancelar(eq(1L), anyString(), anyString());
    }
}
