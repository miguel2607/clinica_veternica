package com.veterinaria.clinica_veternica.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.veterinaria.clinica_veternica.dto.request.paciente.RazaRequestDTO;
import com.veterinaria.clinica_veternica.dto.response.paciente.RazaResponseDTO;
import com.veterinaria.clinica_veternica.security.jwt.JwtAuthenticationFilter;
import com.veterinaria.clinica_veternica.security.jwt.JwtProperties;
import com.veterinaria.clinica_veternica.security.jwt.JwtUtils;
import com.veterinaria.clinica_veternica.security.service.UserDetailsServiceImpl;
import com.veterinaria.clinica_veternica.service.interfaces.IRazaService;
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
 * Tests básicos CRUD para RazaController
 */
@WebMvcTest(controllers = RazaController.class, excludeAutoConfiguration = {
        org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class
})
@AutoConfigureMockMvc(addFilters = false)
class RazaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private IRazaService razaService;

    @MockitoBean
    private JwtUtils jwtUtils;

    @MockitoBean
    private UserDetailsServiceImpl userDetailsService;

    @MockitoBean
    private JwtProperties jwtProperties;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    private RazaRequestDTO razaRequestDTO;
    private RazaResponseDTO razaResponseDTO;

    @BeforeEach
    void setUp() {
        razaRequestDTO = new RazaRequestDTO();
        razaRequestDTO.setNombre("Labrador Retriever");
        razaRequestDTO.setDescripcion("Raza de perro amigable");
        razaRequestDTO.setIdEspecie(1L);
        razaRequestDTO.setActivo(true);

        razaResponseDTO = new RazaResponseDTO();
        razaResponseDTO.setIdRaza(1L);
        razaResponseDTO.setNombre("Labrador Retriever");
        razaResponseDTO.setActivo(true);
    }

    @Test
    @DisplayName("CREATE - Debe crear una raza exitosamente")
    @WithMockUser
    void debeCrearRazaExitosamente() throws Exception {
        when(razaService.crear(any(RazaRequestDTO.class))).thenReturn(razaResponseDTO);

        mockMvc.perform(post("/api/razas")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(razaRequestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idRaza").value(1))
                .andExpect(jsonPath("$.nombre").value("Labrador Retriever"));

        verify(razaService).crear(any(RazaRequestDTO.class));
    }

    @Test
    @DisplayName("READ - Debe buscar raza por ID")
    @WithMockUser
    void debeBuscarRazaPorId() throws Exception {
        when(razaService.buscarPorId(1L)).thenReturn(razaResponseDTO);

        mockMvc.perform(get("/api/razas/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idRaza").value(1))
                .andExpect(jsonPath("$.nombre").value("Labrador Retriever"));

        verify(razaService).buscarPorId(1L);
    }

    @Test
    @DisplayName("READ - Debe listar todas las razas")
    @WithMockUser
    void debeListarTodasLasRazas() throws Exception {
        List<RazaResponseDTO> razas = Arrays.asList(razaResponseDTO);
        when(razaService.listarTodas()).thenReturn(razas);

        mockMvc.perform(get("/api/razas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idRaza").value(1))
                .andExpect(jsonPath("$.length()").value(1));

        verify(razaService).listarTodas();
    }

    @Test
    @DisplayName("READ - Debe listar razas por especie")
    @WithMockUser
    void debeListarRazasPorEspecie() throws Exception {
        List<RazaResponseDTO> razas = Arrays.asList(razaResponseDTO);
        when(razaService.listarPorEspecie(1L)).thenReturn(razas);

        mockMvc.perform(get("/api/razas/especie/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idRaza").value(1));

        verify(razaService).listarPorEspecie(1L);
    }

    @Test
    @DisplayName("UPDATE - Debe actualizar una raza")
    @WithMockUser
    void debeActualizarRaza() throws Exception {
        razaResponseDTO.setNombre("Labrador Retriever Actualizado");
        when(razaService.actualizar(eq(1L), any(RazaRequestDTO.class))).thenReturn(razaResponseDTO);

        mockMvc.perform(put("/api/razas/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(razaRequestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idRaza").value(1));

        verify(razaService).actualizar(eq(1L), any(RazaRequestDTO.class));
    }

    @Test
    @DisplayName("DELETE - Debe eliminar una raza")
    @WithMockUser
    void debeEliminarRaza() throws Exception {
        mockMvc.perform(delete("/api/razas/1")
                        .with(csrf()))
                .andExpect(status().isNoContent());

        verify(razaService).eliminar(1L);
    }
}

