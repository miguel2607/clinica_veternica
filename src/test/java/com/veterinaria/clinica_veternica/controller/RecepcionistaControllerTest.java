package com.veterinaria.clinica_veternica.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.veterinaria.clinica_veternica.dto.request.usuario.RecepcionistaRequestDTO;
import com.veterinaria.clinica_veternica.dto.response.usuario.RecepcionistaResponseDTO;
import com.veterinaria.clinica_veternica.security.jwt.JwtAuthenticationFilter;
import com.veterinaria.clinica_veternica.security.jwt.JwtProperties;
import com.veterinaria.clinica_veternica.security.jwt.JwtUtils;
import com.veterinaria.clinica_veternica.security.service.UserDetailsServiceImpl;
import com.veterinaria.clinica_veternica.service.interfaces.IRecepcionistaService;
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

@WebMvcTest(controllers = RecepcionistaController.class, excludeAutoConfiguration = {
        org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class
})
@AutoConfigureMockMvc(addFilters = false)
class RecepcionistaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private IRecepcionistaService recepcionistaService;

    @MockitoBean
    private JwtUtils jwtUtils;

    @MockitoBean
    private UserDetailsServiceImpl userDetailsService;

    @MockitoBean
    private JwtProperties jwtProperties;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    private RecepcionistaRequestDTO requestDTO;
    private RecepcionistaResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        requestDTO = RecepcionistaRequestDTO.builder()
                .nombres("Carlos")
                .apellidos("Rodríguez")
                .correo("carlos.rodriguez@clinica.com")
                .telefono("5555555550")
                .documento("11223344")
                .direccion("Calle Principal 789")
                .build();

        responseDTO = RecepcionistaResponseDTO.builder()
                .idPersonal(1L)
                .nombres("Carlos")
                .apellidos("Rodríguez")
                .correo("carlos.rodriguez@clinica.com")
                .activo(true)
                .build();
    }

    @Test
    @DisplayName("CREATE - Debe crear un recepcionista exitosamente")
    @WithMockUser
    void debeCrearRecepcionistaExitosamente() throws Exception {
        when(recepcionistaService.crear(any(RecepcionistaRequestDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(post("/api/recepcionistas")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idPersonal").value(1))
                .andExpect(jsonPath("$.nombres").value("Carlos"));

        verify(recepcionistaService).crear(any(RecepcionistaRequestDTO.class));
    }

    @Test
    @DisplayName("READ - Debe buscar recepcionista por ID")
    @WithMockUser
    void debeBuscarRecepcionistaPorId() throws Exception {
        when(recepcionistaService.buscarPorId(1L)).thenReturn(responseDTO);

        mockMvc.perform(get("/api/recepcionistas/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idPersonal").value(1));

        verify(recepcionistaService).buscarPorId(1L);
    }

    @Test
    @DisplayName("READ - Debe listar todos los recepcionistas")
    @WithMockUser
    void debeListarTodosLosRecepcionistas() throws Exception {
        List<RecepcionistaResponseDTO> recepcionistas = Arrays.asList(responseDTO);
        when(recepcionistaService.listarTodos()).thenReturn(recepcionistas);

        mockMvc.perform(get("/api/recepcionistas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idPersonal").value(1));

        verify(recepcionistaService).listarTodos();
    }

    @Test
    @DisplayName("READ - Debe listar recepcionistas activos")
    @WithMockUser
    void debeListarRecepcionistasActivos() throws Exception {
        List<RecepcionistaResponseDTO> recepcionistas = Arrays.asList(responseDTO);
        when(recepcionistaService.listarActivos()).thenReturn(recepcionistas);

        mockMvc.perform(get("/api/recepcionistas/activos"))
                .andExpect(status().isOk());

        verify(recepcionistaService).listarActivos();
    }

    @Test
    @DisplayName("READ - Debe buscar recepcionistas por nombre")
    @WithMockUser
    void debeBuscarRecepcionistasPorNombre() throws Exception {
        List<RecepcionistaResponseDTO> recepcionistas = Arrays.asList(responseDTO);
        when(recepcionistaService.buscarPorNombre("Carlos")).thenReturn(recepcionistas);

        mockMvc.perform(get("/api/recepcionistas/buscar")
                        .param("nombre", "Carlos"))
                .andExpect(status().isOk());

        verify(recepcionistaService).buscarPorNombre("Carlos");
    }

    @Test
    @DisplayName("UPDATE - Debe actualizar un recepcionista")
    @WithMockUser
    void debeActualizarRecepcionista() throws Exception {
        when(recepcionistaService.actualizar(eq(1L), any(RecepcionistaRequestDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(put("/api/recepcionistas/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk());

        verify(recepcionistaService).actualizar(eq(1L), any(RecepcionistaRequestDTO.class));
    }

    @Test
    @DisplayName("DELETE - Debe eliminar un recepcionista")
    @WithMockUser
    void debeEliminarRecepcionista() throws Exception {
        mockMvc.perform(delete("/api/recepcionistas/1")
                        .with(csrf()))
                .andExpect(status().isNoContent());

        verify(recepcionistaService).eliminar(1L);
    }

    @Test
    @DisplayName("PATCH - Debe activar un recepcionista")
    @WithMockUser
    void debeActivarRecepcionista() throws Exception {
        when(recepcionistaService.activar(1L)).thenReturn(responseDTO);

        mockMvc.perform(patch("/api/recepcionistas/1/activar")
                        .with(csrf()))
                .andExpect(status().isOk());

        verify(recepcionistaService).activar(1L);
    }
}

