package com.veterinaria.clinica_veternica.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.veterinaria.clinica_veternica.dto.request.paciente.EspecieRequestDTO;
import com.veterinaria.clinica_veternica.dto.response.paciente.EspecieResponseDTO;
import com.veterinaria.clinica_veternica.security.jwt.JwtAuthenticationFilter;
import com.veterinaria.clinica_veternica.security.jwt.JwtProperties;
import com.veterinaria.clinica_veternica.security.jwt.JwtUtils;
import com.veterinaria.clinica_veternica.security.service.UserDetailsServiceImpl;
import com.veterinaria.clinica_veternica.service.interfaces.IEspecieService;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = EspecieController.class, excludeAutoConfiguration = {
        org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class
})
@AutoConfigureMockMvc(addFilters = false)
class EspecieControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private IEspecieService especieService;

    @MockitoBean
    private JwtUtils jwtUtils;

    @MockitoBean
    private UserDetailsServiceImpl userDetailsService;

    @MockitoBean
    private JwtProperties jwtProperties;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    private EspecieRequestDTO especieRequestDTO;
    private EspecieResponseDTO especieResponseDTO;

    @BeforeEach
    void setUp() {
        especieRequestDTO = new EspecieRequestDTO();
        especieRequestDTO.setNombre("Canino");
        especieRequestDTO.setDescripcion("Especie canina");

        especieResponseDTO = new EspecieResponseDTO();
        especieResponseDTO.setIdEspecie(1L);
        especieResponseDTO.setNombre("Canino");
        especieResponseDTO.setDescripcion("Especie canina");
        especieResponseDTO.setActivo(true);
    }

    @Test
    @DisplayName("CREATE - Debe crear una especie exitosamente")
    @WithMockUser(roles = {"ADMIN"})
    void debeCrearEspecieExitosamente() throws Exception {
        when(especieService.crear(any(EspecieRequestDTO.class))).thenReturn(especieResponseDTO);

        mockMvc.perform(post("/api/especies")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(especieRequestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idEspecie").value(1))
                .andExpect(jsonPath("$.nombre").value("Canino"));

        verify(especieService).crear(any(EspecieRequestDTO.class));
    }

    @Test
    @DisplayName("READ - Debe buscar especie por ID")
    @WithMockUser
    void debeBuscarEspeciePorId() throws Exception {
        when(especieService.buscarPorId(1L)).thenReturn(especieResponseDTO);

        mockMvc.perform(get("/api/especies/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idEspecie").value(1))
                .andExpect(jsonPath("$.nombre").value("Canino"));

        verify(especieService).buscarPorId(1L);
    }

    @Test
    @DisplayName("READ - Debe listar todas las especies")
    @WithMockUser
    void debeListarTodasLasEspecies() throws Exception {
        List<EspecieResponseDTO> especies = Arrays.asList(especieResponseDTO);
        when(especieService.listarTodas()).thenReturn(especies);

        mockMvc.perform(get("/api/especies"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idEspecie").value(1))
                .andExpect(jsonPath("$.length()").value(1));

        verify(especieService).listarTodas();
    }

    @Test
    @DisplayName("READ - Debe listar especies activas")
    @WithMockUser
    void debeListarEspeciesActivas() throws Exception {
        List<EspecieResponseDTO> especies = Arrays.asList(especieResponseDTO);
        when(especieService.listarActivas()).thenReturn(especies);

        mockMvc.perform(get("/api/especies/activas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idEspecie").value(1));

        verify(especieService).listarActivas();
    }

    @Test
    @DisplayName("READ - Debe buscar especies por nombre")
    @WithMockUser
    void debeBuscarEspeciesPorNombre() throws Exception {
        List<EspecieResponseDTO> especies = Arrays.asList(especieResponseDTO);
        when(especieService.buscarPorNombre("Canino")).thenReturn(especies);

        mockMvc.perform(get("/api/especies/buscar")
                        .param("nombre", "Canino"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombre").value("Canino"));

        verify(especieService).buscarPorNombre("Canino");
    }

    @Test
    @DisplayName("UPDATE - Debe actualizar una especie")
    @WithMockUser
    void debeActualizarEspecie() throws Exception {
        especieResponseDTO.setNombre("Canino Actualizado");
        when(especieService.actualizar(eq(1L), any(EspecieRequestDTO.class))).thenReturn(especieResponseDTO);

        mockMvc.perform(put("/api/especies/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(especieRequestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idEspecie").value(1));

        verify(especieService).actualizar(eq(1L), any(EspecieRequestDTO.class));
    }

    @Test
    @DisplayName("DELETE - Debe eliminar una especie")
    @WithMockUser(roles = {"ADMIN"})
    void debeEliminarEspecie() throws Exception {
        mockMvc.perform(delete("/api/especies/1")
                        .with(csrf()))
                .andExpect(status().isNoContent());

        verify(especieService).eliminar(1L);
    }

    @Test
    @DisplayName("PATCH - Debe activar una especie")
    @WithMockUser
    void debeActivarEspecie() throws Exception {
        when(especieService.activar(1L)).thenReturn(especieResponseDTO);

        mockMvc.perform(patch("/api/especies/1/activar")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idEspecie").value(1));

        verify(especieService).activar(1L);
    }

    @Test
    @DisplayName("GET - Debe verificar existencia por nombre")
    @WithMockUser
    void debeVerificarExistenciaPorNombre() throws Exception {
        when(especieService.existePorNombre("Canino")).thenReturn(true);

        mockMvc.perform(get("/api/especies/existe")
                        .param("nombre", "Canino"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));

        verify(especieService).existePorNombre("Canino");
    }
}

