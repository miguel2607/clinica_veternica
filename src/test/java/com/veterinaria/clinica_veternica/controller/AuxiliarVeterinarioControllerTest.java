package com.veterinaria.clinica_veternica.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.veterinaria.clinica_veternica.dto.request.usuario.AuxiliarVeterinarioRequestDTO;
import com.veterinaria.clinica_veternica.dto.response.usuario.AuxiliarVeterinarioResponseDTO;
import com.veterinaria.clinica_veternica.security.jwt.JwtAuthenticationFilter;
import com.veterinaria.clinica_veternica.security.jwt.JwtProperties;
import com.veterinaria.clinica_veternica.security.jwt.JwtUtils;
import com.veterinaria.clinica_veternica.security.service.UserDetailsServiceImpl;
import com.veterinaria.clinica_veternica.service.interfaces.IAuxiliarVeterinarioService;
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

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = AuxiliarVeterinarioController.class, excludeAutoConfiguration = {
        org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class
})
@AutoConfigureMockMvc(addFilters = false)
@TestPropertySource(properties = {
    "spring.dotenv.enabled=false",
    "JWT_SECRET=404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970",
    "JWT_EXPIRATION=86400000"
})
class AuxiliarVeterinarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private IAuxiliarVeterinarioService auxiliarVeterinarioService;

    @MockitoBean
    private JwtUtils jwtUtils;

    @MockitoBean
    private UserDetailsServiceImpl userDetailsService;

    @MockitoBean
    private JwtProperties jwtProperties;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    private AuxiliarVeterinarioRequestDTO requestDTO;
    private AuxiliarVeterinarioResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        requestDTO = AuxiliarVeterinarioRequestDTO.builder()
                .nombres("María")
                .apellidos("González")
                .correo("maria.gonzalez@clinica.com")
                .telefono("9876543210")
                .documento("87654321")
                .direccion("Avenida 456")
                .build();

        responseDTO = AuxiliarVeterinarioResponseDTO.builder()
                .idPersonal(1L)
                .nombres("María")
                .apellidos("González")
                .correo("maria.gonzalez@clinica.com")
                .activo(true)
                .build();
    }

    @Test
    @DisplayName("CREATE - Debe crear un auxiliar veterinario exitosamente")
    @WithMockUser
    void debeCrearAuxiliarVeterinarioExitosamente() throws Exception {
        when(auxiliarVeterinarioService.crear(any(AuxiliarVeterinarioRequestDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(post("/api/auxiliares-veterinarios")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idPersonal").value(1))
                .andExpect(jsonPath("$.nombres").value("María"));

        verify(auxiliarVeterinarioService).crear(any(AuxiliarVeterinarioRequestDTO.class));
    }

    @Test
    @DisplayName("READ - Debe buscar auxiliar veterinario por ID")
    @WithMockUser
    void debeBuscarAuxiliarVeterinarioPorId() throws Exception {
        when(auxiliarVeterinarioService.buscarPorId(1L)).thenReturn(responseDTO);

        mockMvc.perform(get("/api/auxiliares-veterinarios/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idPersonal").value(1));

        verify(auxiliarVeterinarioService).buscarPorId(1L);
    }

    @Test
    @DisplayName("READ - Debe listar todos los auxiliares veterinarios")
    @WithMockUser
    void debeListarTodosLosAuxiliaresVeterinarios() throws Exception {
        List<AuxiliarVeterinarioResponseDTO> auxiliares = Arrays.asList(responseDTO);
        when(auxiliarVeterinarioService.listarTodos()).thenReturn(auxiliares);

        mockMvc.perform(get("/api/auxiliares-veterinarios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idPersonal").value(1));

        verify(auxiliarVeterinarioService).listarTodos();
    }

    @Test
    @DisplayName("READ - Debe listar auxiliares veterinarios activos")
    @WithMockUser
    void debeListarAuxiliaresVeterinariosActivos() throws Exception {
        List<AuxiliarVeterinarioResponseDTO> auxiliares = Arrays.asList(responseDTO);
        when(auxiliarVeterinarioService.listarActivos()).thenReturn(auxiliares);

        mockMvc.perform(get("/api/auxiliares-veterinarios/activos"))
                .andExpect(status().isOk());

        verify(auxiliarVeterinarioService).listarActivos();
    }

    @Test
    @DisplayName("READ - Debe buscar auxiliares veterinarios por nombre")
    @WithMockUser
    void debeBuscarAuxiliaresVeterinariosPorNombre() throws Exception {
        List<AuxiliarVeterinarioResponseDTO> auxiliares = Arrays.asList(responseDTO);
        when(auxiliarVeterinarioService.buscarPorNombre("María")).thenReturn(auxiliares);

        mockMvc.perform(get("/api/auxiliares-veterinarios/buscar")
                        .param("nombre", "María"))
                .andExpect(status().isOk());

        verify(auxiliarVeterinarioService).buscarPorNombre("María");
    }

    @Test
    @DisplayName("UPDATE - Debe actualizar un auxiliar veterinario")
    @WithMockUser
    void debeActualizarAuxiliarVeterinario() throws Exception {
        when(auxiliarVeterinarioService.actualizar(eq(1L), any(AuxiliarVeterinarioRequestDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(put("/api/auxiliares-veterinarios/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk());

        verify(auxiliarVeterinarioService).actualizar(eq(1L), any(AuxiliarVeterinarioRequestDTO.class));
    }

    @Test
    @DisplayName("DELETE - Debe eliminar un auxiliar veterinario")
    @WithMockUser
    void debeEliminarAuxiliarVeterinario() throws Exception {
        mockMvc.perform(delete("/api/auxiliares-veterinarios/1")
                        .with(csrf()))
                .andExpect(status().isNoContent());

        verify(auxiliarVeterinarioService).eliminar(1L);
    }

    @Test
    @DisplayName("PATCH - Debe activar un auxiliar veterinario")
    @WithMockUser
    void debeActivarAuxiliarVeterinario() throws Exception {
        when(auxiliarVeterinarioService.activar(1L)).thenReturn(responseDTO);

        mockMvc.perform(patch("/api/auxiliares-veterinarios/1/activar")
                        .with(csrf()))
                .andExpect(status().isOk());

        verify(auxiliarVeterinarioService).activar(1L);
    }
}

