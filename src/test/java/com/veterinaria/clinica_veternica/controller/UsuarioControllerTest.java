package com.veterinaria.clinica_veternica.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.veterinaria.clinica_veternica.dto.request.usuario.UsuarioRequestDTO;
import com.veterinaria.clinica_veternica.dto.response.usuario.UsuarioResponseDTO;
import com.veterinaria.clinica_veternica.security.jwt.JwtAuthenticationFilter;
import com.veterinaria.clinica_veternica.security.jwt.JwtProperties;
import com.veterinaria.clinica_veternica.security.jwt.JwtUtils;
import com.veterinaria.clinica_veternica.security.service.UserDetailsServiceImpl;
import com.veterinaria.clinica_veternica.service.interfaces.IUsuarioService;
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
 * Tests básicos CRUD para UsuarioController
 */
@WebMvcTest(controllers = UsuarioController.class, excludeAutoConfiguration = {
        org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class
})
@AutoConfigureMockMvc(addFilters = false)
class UsuarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private IUsuarioService usuarioService;

    @MockitoBean
    private JwtUtils jwtUtils;

    @MockitoBean
    private UserDetailsServiceImpl userDetailsService;

    @MockitoBean
    private JwtProperties jwtProperties;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    private UsuarioRequestDTO usuarioRequestDTO;
    private UsuarioResponseDTO usuarioResponseDTO;

    @BeforeEach
    void setUp() {
        usuarioRequestDTO = new UsuarioRequestDTO();
        usuarioRequestDTO.setUsername("testuser");
        usuarioRequestDTO.setPassword("Password123!");
        usuarioRequestDTO.setEmail("test@example.com");
        usuarioRequestDTO.setRol("VETERINARIO");
        usuarioRequestDTO.setEstado(true);

        usuarioResponseDTO = new UsuarioResponseDTO();
        usuarioResponseDTO.setIdUsuario(1L);
        usuarioResponseDTO.setUsername("testuser");
        usuarioResponseDTO.setEmail("test@example.com");
        usuarioResponseDTO.setRol("VETERINARIO");
        usuarioResponseDTO.setEstado(true);
    }

    @Test
    @DisplayName("CREATE - Debe crear un usuario exitosamente")
    @WithMockUser(roles = {"ADMIN"})
    void debeCrearUsuarioExitosamente() throws Exception {
        when(usuarioService.crear(any(UsuarioRequestDTO.class))).thenReturn(usuarioResponseDTO);

        mockMvc.perform(post("/api/usuarios")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(usuarioRequestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idUsuario").value(1))
                .andExpect(jsonPath("$.username").value("testuser"));

        verify(usuarioService).crear(any(UsuarioRequestDTO.class));
    }

    @Test
    @DisplayName("READ - Debe buscar usuario por ID")
    @WithMockUser
    void debeBuscarUsuarioPorId() throws Exception {
        when(usuarioService.buscarPorId(1L)).thenReturn(usuarioResponseDTO);

        mockMvc.perform(get("/api/usuarios/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idUsuario").value(1))
                .andExpect(jsonPath("$.username").value("testuser"));

        verify(usuarioService).buscarPorId(1L);
    }

    @Test
    @DisplayName("READ - Debe listar todos los usuarios")
    @WithMockUser
    void debeListarTodosLosUsuarios() throws Exception {
        List<UsuarioResponseDTO> usuarios = Arrays.asList(usuarioResponseDTO);
        when(usuarioService.listarTodos()).thenReturn(usuarios);

        mockMvc.perform(get("/api/usuarios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idUsuario").value(1))
                .andExpect(jsonPath("$.length()").value(1));

        verify(usuarioService).listarTodos();
    }

    @Test
    @DisplayName("READ - Debe listar usuarios paginados")
    @WithMockUser
    void debeListarUsuariosPaginados() throws Exception {
        when(usuarioService.listarTodos()).thenReturn(Arrays.asList(usuarioResponseDTO));

        mockMvc.perform(get("/api/usuarios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].idUsuario").value(1));

        verify(usuarioService).listarTodos();
    }

    @Test
    @DisplayName("READ - Debe listar usuarios por rol")
    @WithMockUser
    void debeListarUsuariosPorRol() throws Exception {
        List<UsuarioResponseDTO> usuarios = Arrays.asList(usuarioResponseDTO);
        when(usuarioService.listarPorRol("VETERINARIO")).thenReturn(usuarios);

        mockMvc.perform(get("/api/usuarios/rol/VETERINARIO"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].idUsuario").value(1))
                .andExpect(jsonPath("$[0].rol").value("VETERINARIO"));

        verify(usuarioService).listarPorRol("VETERINARIO");
    }

    @Test
    @DisplayName("UPDATE - Debe actualizar un usuario")
    @WithMockUser(roles = {"ADMIN"})
    void debeActualizarUsuario() throws Exception {
        usuarioResponseDTO.setEmail("updated@example.com");
        when(usuarioService.actualizar(eq(1L), any(UsuarioRequestDTO.class))).thenReturn(usuarioResponseDTO);

        mockMvc.perform(put("/api/usuarios/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(usuarioRequestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idUsuario").value(1));

        verify(usuarioService).actualizar(eq(1L), any(UsuarioRequestDTO.class));
    }
}

