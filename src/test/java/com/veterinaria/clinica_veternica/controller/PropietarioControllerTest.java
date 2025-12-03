package com.veterinaria.clinica_veternica.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.veterinaria.clinica_veternica.dto.request.paciente.PropietarioRequestDTO;
import com.veterinaria.clinica_veternica.dto.response.paciente.PropietarioResponseDTO;
import com.veterinaria.clinica_veternica.repository.UsuarioRepository;
import com.veterinaria.clinica_veternica.security.jwt.JwtAuthenticationFilter;
import com.veterinaria.clinica_veternica.security.jwt.JwtProperties;
import com.veterinaria.clinica_veternica.security.jwt.JwtUtils;
import com.veterinaria.clinica_veternica.security.service.UserDetailsServiceImpl;
import com.veterinaria.clinica_veternica.service.interfaces.IPropietarioService;
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
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests básicos CRUD para PropietarioController
 */
@WebMvcTest(controllers = PropietarioController.class, excludeAutoConfiguration = {
        org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class
})
@AutoConfigureMockMvc(addFilters = false)
@TestPropertySource(properties = {
    "spring.dotenv.enabled=false",
    "JWT_SECRET=404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970",
    "JWT_EXPIRATION=86400000"
})
class PropietarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private IPropietarioService propietarioService;

    @MockitoBean
    private JwtUtils jwtUtils;

    @MockitoBean
    private UserDetailsServiceImpl userDetailsService;

    @MockitoBean
    private JwtProperties jwtProperties;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockitoBean
    private UsuarioRepository usuarioRepository;

    private PropietarioRequestDTO propietarioRequestDTO;
    private PropietarioResponseDTO propietarioResponseDTO;

    @BeforeEach
    void setUp() {
        propietarioRequestDTO = new PropietarioRequestDTO();
        propietarioRequestDTO.setNombres("Juan");
        propietarioRequestDTO.setApellidos("Pérez");
        propietarioRequestDTO.setTipoDocumento("DNI");
        propietarioRequestDTO.setDocumento("12345678");
        propietarioRequestDTO.setEmail("juan.perez@email.com");
        propietarioRequestDTO.setTelefono("987654321");
        propietarioRequestDTO.setDireccion("Av. Principal 123");

        propietarioResponseDTO = new PropietarioResponseDTO();
        propietarioResponseDTO.setIdPropietario(1L);
        propietarioResponseDTO.setNombres("Juan");
        propietarioResponseDTO.setApellidos("Pérez");
        propietarioResponseDTO.setTipoDocumento("DNI");
        propietarioResponseDTO.setDocumento("12345678");
        propietarioResponseDTO.setEmail("juan.perez@email.com");
        propietarioResponseDTO.setTelefono("987654321");
        propietarioResponseDTO.setActivo(true);
    }

    @Test
    @DisplayName("CREATE - Debe crear un propietario exitosamente")
    @WithMockUser(roles = {"ADMIN"})
    void debeCrearPropietarioExitosamente() throws Exception {
        when(propietarioService.crear(any(PropietarioRequestDTO.class))).thenReturn(propietarioResponseDTO);

        mockMvc.perform(post("/api/propietarios")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(propietarioRequestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idPropietario").value(1))
                .andExpect(jsonPath("$.nombres").value("Juan"));

        verify(propietarioService).crear(any(PropietarioRequestDTO.class));
    }

    @Test
    @DisplayName("READ - Debe buscar propietario por ID")
    @WithMockUser
    void debeBuscarPropietarioPorId() throws Exception {
        when(propietarioService.buscarPorId(1L)).thenReturn(propietarioResponseDTO);

        mockMvc.perform(get("/api/propietarios/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idPropietario").value(1))
                .andExpect(jsonPath("$.nombres").value("Juan"));

        verify(propietarioService).buscarPorId(1L);
    }

    @Test
    @DisplayName("READ - Debe listar todos los propietarios")
    @WithMockUser
    void debeListarTodosLosPropietarios() throws Exception {
        List<PropietarioResponseDTO> propietarios = Arrays.asList(propietarioResponseDTO);
        when(propietarioService.listarTodos()).thenReturn(propietarios);

        mockMvc.perform(get("/api/propietarios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idPropietario").value(1))
                .andExpect(jsonPath("$.length()").value(1));

        verify(propietarioService).listarTodos();
    }

    @Test
    @DisplayName("UPDATE - Debe actualizar un propietario")
    @WithMockUser
    void debeActualizarPropietario() throws Exception {
        propietarioResponseDTO.setTelefono("999888777");
        when(propietarioService.actualizar(eq(1L), any(PropietarioRequestDTO.class)))
                .thenReturn(propietarioResponseDTO);

        mockMvc.perform(put("/api/propietarios/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(propietarioRequestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idPropietario").value(1));

        verify(propietarioService).actualizar(eq(1L), any(PropietarioRequestDTO.class));
    }

    @Test
    @DisplayName("DELETE - Debe eliminar un propietario")
    @WithMockUser(roles = {"ADMIN"})
    void debeEliminarPropietario() throws Exception {
        mockMvc.perform(delete("/api/propietarios/1")
                        .with(csrf()))
                .andExpect(status().isNoContent());

        verify(propietarioService).eliminar(1L);
    }
}
