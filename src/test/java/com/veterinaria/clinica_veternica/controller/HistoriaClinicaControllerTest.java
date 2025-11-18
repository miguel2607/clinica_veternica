package com.veterinaria.clinica_veternica.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.veterinaria.clinica_veternica.dto.request.clinico.HistoriaClinicaRequestDTO;
import com.veterinaria.clinica_veternica.dto.response.clinico.HistoriaClinicaResponseDTO;
import com.veterinaria.clinica_veternica.security.jwt.JwtAuthenticationFilter;
import com.veterinaria.clinica_veternica.security.jwt.JwtProperties;
import com.veterinaria.clinica_veternica.security.jwt.JwtUtils;
import com.veterinaria.clinica_veternica.security.service.UserDetailsServiceImpl;
import com.veterinaria.clinica_veternica.service.interfaces.IHistoriaClinicaService;
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


import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests básicos CRUD para HistoriaClinicaController
 */
@WebMvcTest(controllers = HistoriaClinicaController.class, excludeAutoConfiguration = {
        org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class
})
@AutoConfigureMockMvc(addFilters = false)
class HistoriaClinicaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private IHistoriaClinicaService historiaClinicaService;

    @MockitoBean
    private JwtUtils jwtUtils;

    @MockitoBean
    private UserDetailsServiceImpl userDetailsService;

    @MockitoBean
    private JwtProperties jwtProperties;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    private HistoriaClinicaRequestDTO historiaClinicaRequestDTO;
    private HistoriaClinicaResponseDTO historiaClinicaResponseDTO;

    @BeforeEach
    void setUp() {
        historiaClinicaRequestDTO = new HistoriaClinicaRequestDTO();
        historiaClinicaRequestDTO.setIdMascota(1L);
        historiaClinicaRequestDTO.setNumeroHistoria("HC-001");
        historiaClinicaRequestDTO.setAlergias("Ninguna");
        historiaClinicaRequestDTO.setEnfermedadesCronicas("Ninguna");
        historiaClinicaRequestDTO.setObservaciones("Mascota saludable");

        historiaClinicaResponseDTO = new HistoriaClinicaResponseDTO();
        historiaClinicaResponseDTO.setIdHistoriaClinica(1L);
        historiaClinicaResponseDTO.setNumeroHistoria("HC-001");
        historiaClinicaResponseDTO.setAlergias("Ninguna");
        historiaClinicaResponseDTO.setEnfermedadesCronicas("Ninguna");
        historiaClinicaResponseDTO.setObservaciones("Mascota saludable");
        historiaClinicaResponseDTO.setActivo(true);
    }

    @Test
    @DisplayName("CREATE - Debe crear una historia clínica exitosamente")
    @WithMockUser(roles = {"VETERINARIO"})
    void debeCrearHistoriaClinicaExitosamente() throws Exception {
        when(historiaClinicaService.crear(any(HistoriaClinicaRequestDTO.class))).thenReturn(historiaClinicaResponseDTO);

        mockMvc.perform(post("/api/historias-clinicas")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(historiaClinicaRequestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idHistoriaClinica").value(1))
                .andExpect(jsonPath("$.numeroHistoria").value("HC-001"));

        verify(historiaClinicaService).crear(any(HistoriaClinicaRequestDTO.class));
    }

    @Test
    @DisplayName("READ - Debe buscar historia clínica por ID")
    @WithMockUser
    void debeBuscarHistoriaClinicaPorId() throws Exception {
        when(historiaClinicaService.buscarPorId(1L)).thenReturn(historiaClinicaResponseDTO);

        mockMvc.perform(get("/api/historias-clinicas/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idHistoriaClinica").value(1))
                .andExpect(jsonPath("$.numeroHistoria").value("HC-001"));

        verify(historiaClinicaService).buscarPorId(1L);
    }

    @Test
    @DisplayName("READ - Debe listar todas las historias clínicas")
    @WithMockUser
    void debeListarTodasLasHistoriasClinicas() throws Exception {
        List<HistoriaClinicaResponseDTO> historias = Arrays.asList(historiaClinicaResponseDTO);
        when(historiaClinicaService.listarTodos()).thenReturn(historias);

        mockMvc.perform(get("/api/historias-clinicas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idHistoriaClinica").value(1))
                .andExpect(jsonPath("$.length()").value(1));

        verify(historiaClinicaService).listarTodos();
    }

    @Test
    @DisplayName("UPDATE - Debe actualizar una historia clínica")
    @WithMockUser(roles = {"VETERINARIO"})
    void debeActualizarHistoriaClinica() throws Exception {
        historiaClinicaResponseDTO.setAlergias("Alergia a penicilina");
        when(historiaClinicaService.actualizar(eq(1L), any(HistoriaClinicaRequestDTO.class)))
                .thenReturn(historiaClinicaResponseDTO);

        mockMvc.perform(put("/api/historias-clinicas/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(historiaClinicaRequestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idHistoriaClinica").value(1));

        verify(historiaClinicaService).actualizar(eq(1L), any(HistoriaClinicaRequestDTO.class));
    }

    @Test
    @DisplayName("DELETE - Debe archivar una historia clínica")
    @WithMockUser(roles = {"ADMIN"})
    void debeArchivarHistoriaClinica() throws Exception {
        mockMvc.perform(put("/api/historias-clinicas/1/archivar")
                        .with(csrf())
                        .param("motivo", "Archivado por administración"))
                .andExpect(status().isOk());

        verify(historiaClinicaService).archivar(eq(1L), anyString());
    }
}

