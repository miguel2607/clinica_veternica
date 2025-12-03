package com.veterinaria.clinica_veternica.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.veterinaria.clinica_veternica.dto.request.usuario.VeterinarioRequestDTO;
import com.veterinaria.clinica_veternica.dto.response.usuario.VeterinarioResponseDTO;
import com.veterinaria.clinica_veternica.security.jwt.JwtAuthenticationFilter;
import com.veterinaria.clinica_veternica.security.jwt.JwtProperties;
import com.veterinaria.clinica_veternica.security.jwt.JwtUtils;
import com.veterinaria.clinica_veternica.security.service.UserDetailsServiceImpl;
import com.veterinaria.clinica_veternica.service.interfaces.IVeterinarioService;
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

@WebMvcTest(controllers = VeterinarioController.class, excludeAutoConfiguration = {
        org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class
})
@AutoConfigureMockMvc(addFilters = false)
@TestPropertySource(properties = {
    "spring.dotenv.enabled=false",
    "JWT_SECRET=404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970",
    "JWT_EXPIRATION=86400000"
})
class VeterinarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private IVeterinarioService veterinarioService;

    @MockitoBean
    private JwtUtils jwtUtils;

    @MockitoBean
    private UserDetailsServiceImpl userDetailsService;

    @MockitoBean
    private JwtProperties jwtProperties;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    private VeterinarioRequestDTO requestDTO;
    private VeterinarioResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        requestDTO = VeterinarioRequestDTO.builder()
                .nombres("Juan")
                .apellidos("Pérez")
                .correo("juan.perez@clinica.com")
                .telefono("1234567890")
                .documento("12345678")
                .direccion("Calle 123")
                .registroProfesional("VET001")
                .especialidad("Cirugía")
                .build();

        responseDTO = VeterinarioResponseDTO.builder()
                .idPersonal(1L)
                .nombres("Juan")
                .apellidos("Pérez")
                .correo("juan.perez@clinica.com")
                .registroProfesional("VET001")
                .especialidad("Cirugía")
                .activo(true)
                .build();
    }

    @Test
    @DisplayName("CREATE - Debe crear un veterinario exitosamente")
    @WithMockUser(roles = {"ADMIN"})
    void debeCrearVeterinarioExitosamente() throws Exception {
        when(veterinarioService.crear(any(VeterinarioRequestDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(post("/api/veterinarios")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idPersonal").value(1))
                .andExpect(jsonPath("$.nombres").value("Juan"));

        verify(veterinarioService).crear(any(VeterinarioRequestDTO.class));
    }

    @Test
    @DisplayName("READ - Debe buscar veterinario por ID")
    @WithMockUser
    void debeBuscarVeterinarioPorId() throws Exception {
        when(veterinarioService.buscarPorId(1L)).thenReturn(responseDTO);

        mockMvc.perform(get("/api/veterinarios/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idPersonal").value(1));

        verify(veterinarioService).buscarPorId(1L);
    }

    @Test
    @DisplayName("READ - Debe buscar veterinario por registro profesional")
    @WithMockUser
    void debeBuscarVeterinarioPorRegistroProfesional() throws Exception {
        when(veterinarioService.buscarPorRegistroProfesional("VET001")).thenReturn(responseDTO);

        mockMvc.perform(get("/api/veterinarios/registro/VET001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.registroProfesional").value("VET001"));

        verify(veterinarioService).buscarPorRegistroProfesional("VET001");
    }

    @Test
    @DisplayName("READ - Debe listar todos los veterinarios")
    @WithMockUser
    void debeListarTodosLosVeterinarios() throws Exception {
        List<VeterinarioResponseDTO> veterinarios = Arrays.asList(responseDTO);
        when(veterinarioService.listarTodos()).thenReturn(veterinarios);

        mockMvc.perform(get("/api/veterinarios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idPersonal").value(1));

        verify(veterinarioService).listarTodos();
    }

    @Test
    @DisplayName("READ - Debe listar veterinarios activos")
    @WithMockUser
    void debeListarVeterinariosActivos() throws Exception {
        List<VeterinarioResponseDTO> veterinarios = Arrays.asList(responseDTO);
        when(veterinarioService.listarActivos()).thenReturn(veterinarios);

        mockMvc.perform(get("/api/veterinarios/activos"))
                .andExpect(status().isOk());

        verify(veterinarioService).listarActivos();
    }

    @Test
    @DisplayName("READ - Debe listar veterinarios disponibles")
    @WithMockUser
    void debeListarVeterinariosDisponibles() throws Exception {
        List<VeterinarioResponseDTO> veterinarios = Arrays.asList(responseDTO);
        when(veterinarioService.listarDisponibles()).thenReturn(veterinarios);

        mockMvc.perform(get("/api/veterinarios/disponibles"))
                .andExpect(status().isOk());

        verify(veterinarioService).listarDisponibles();
    }

    @Test
    @DisplayName("READ - Debe listar veterinarios por especialidad")
    @WithMockUser
    void debeListarVeterinariosPorEspecialidad() throws Exception {
        List<VeterinarioResponseDTO> veterinarios = Arrays.asList(responseDTO);
        when(veterinarioService.listarPorEspecialidad("Cirugía")).thenReturn(veterinarios);

        mockMvc.perform(get("/api/veterinarios/especialidad")
                        .param("especialidad", "Cirugía"))
                .andExpect(status().isOk());

        verify(veterinarioService).listarPorEspecialidad("Cirugía");
    }

    @Test
    @DisplayName("READ - Debe buscar veterinarios por nombre")
    @WithMockUser
    void debeBuscarVeterinariosPorNombre() throws Exception {
        List<VeterinarioResponseDTO> veterinarios = Arrays.asList(responseDTO);
        when(veterinarioService.buscarPorNombre("Juan")).thenReturn(veterinarios);

        mockMvc.perform(get("/api/veterinarios/buscar")
                        .param("nombre", "Juan"))
                .andExpect(status().isOk());

        verify(veterinarioService).buscarPorNombre("Juan");
    }

    @Test
    @DisplayName("UPDATE - Debe actualizar un veterinario")
    @WithMockUser(roles = {"VETERINARIO"})
    void debeActualizarVeterinario() throws Exception {
        when(veterinarioService.actualizar(eq(1L), any(VeterinarioRequestDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(put("/api/veterinarios/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk());

        verify(veterinarioService).actualizar(eq(1L), any(VeterinarioRequestDTO.class));
    }

    @Test
    @DisplayName("DELETE - Debe eliminar un veterinario")
    @WithMockUser(roles = {"ADMIN"})
    void debeEliminarVeterinario() throws Exception {
        mockMvc.perform(delete("/api/veterinarios/1")
                        .with(csrf()))
                .andExpect(status().isNoContent());

        verify(veterinarioService).eliminar(1L);
    }

    @Test
    @DisplayName("PATCH - Debe activar un veterinario")
    @WithMockUser(roles = {"ADMIN"})
    void debeActivarVeterinario() throws Exception {
        when(veterinarioService.activar(1L)).thenReturn(responseDTO);

        mockMvc.perform(patch("/api/veterinarios/1/activar")
                        .with(csrf()))
                .andExpect(status().isOk());

        verify(veterinarioService).activar(1L);
    }
}

