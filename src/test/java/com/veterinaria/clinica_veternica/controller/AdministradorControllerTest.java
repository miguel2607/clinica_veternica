package com.veterinaria.clinica_veternica.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.veterinaria.clinica_veternica.dto.request.usuario.AdministradorRequestDTO;
import com.veterinaria.clinica_veternica.dto.response.usuario.AdministradorResponseDTO;
import com.veterinaria.clinica_veternica.security.jwt.JwtAuthenticationFilter;
import com.veterinaria.clinica_veternica.security.jwt.JwtProperties;
import com.veterinaria.clinica_veternica.security.jwt.JwtUtils;
import com.veterinaria.clinica_veternica.security.service.UserDetailsServiceImpl;
import com.veterinaria.clinica_veternica.service.interfaces.IAdministradorService;
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
import org.springframework.test.context.TestPropertySource;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests básicos CRUD para AdministradorController
 */
@WebMvcTest(controllers = AdministradorController.class, excludeAutoConfiguration = {
        org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class
})
@AutoConfigureMockMvc(addFilters = false)
@TestPropertySource(properties = {
    "spring.dotenv.enabled=false",
    "JWT_SECRET=404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970",
    "JWT_EXPIRATION=86400000"
})
class AdministradorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private IAdministradorService administradorService;

    @MockitoBean
    private JwtUtils jwtUtils;

    @MockitoBean
    private UserDetailsServiceImpl userDetailsService;

    @MockitoBean
    private JwtProperties jwtProperties;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    private AdministradorRequestDTO administradorRequestDTO;
    private AdministradorResponseDTO administradorResponseDTO;

    @BeforeEach
    void setUp() {
        administradorRequestDTO = new AdministradorRequestDTO();
        administradorRequestDTO.setNombres("Juan");
        administradorRequestDTO.setApellidos("Pérez");
        administradorRequestDTO.setDocumento("12345678");
        administradorRequestDTO.setCorreo("juan.perez@clinica.com");
        administradorRequestDTO.setTelefono("+51987654321");
        administradorRequestDTO.setActivo(true);

        administradorResponseDTO = new AdministradorResponseDTO();
        administradorResponseDTO.setIdPersonal(1L);
        administradorResponseDTO.setNombres("Juan");
        administradorResponseDTO.setApellidos("Pérez");
        administradorResponseDTO.setDocumento("12345678");
        administradorResponseDTO.setActivo(true);
    }

    @Test
    @DisplayName("CREATE - Debe crear un administrador exitosamente")
    @WithMockUser
    void debeCrearAdministradorExitosamente() throws Exception {
        when(administradorService.crear(any(AdministradorRequestDTO.class))).thenReturn(administradorResponseDTO);

        mockMvc.perform(post("/api/administradores")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(administradorRequestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idPersonal").value(1))
                .andExpect(jsonPath("$.nombres").value("Juan"));

        verify(administradorService).crear(any(AdministradorRequestDTO.class));
    }

    @Test
    @DisplayName("READ - Debe buscar administrador por ID")
    @WithMockUser
    void debeBuscarAdministradorPorId() throws Exception {
        when(administradorService.buscarPorId(1L)).thenReturn(administradorResponseDTO);

        mockMvc.perform(get("/api/administradores/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idPersonal").value(1))
                .andExpect(jsonPath("$.nombres").value("Juan"));

        verify(administradorService).buscarPorId(1L);
    }

    @Test
    @DisplayName("READ - Debe listar todos los administradores")
    @WithMockUser
    void debeListarTodosLosAdministradores() throws Exception {
        List<AdministradorResponseDTO> administradores = Arrays.asList(administradorResponseDTO);
        when(administradorService.listarTodos()).thenReturn(administradores);

        mockMvc.perform(get("/api/administradores"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idPersonal").value(1))
                .andExpect(jsonPath("$.length()").value(1));

        verify(administradorService).listarTodos();
    }

    @Test
    @DisplayName("UPDATE - Debe actualizar un administrador")
    @WithMockUser
    void debeActualizarAdministrador() throws Exception {
        administradorResponseDTO.setNombres("Juan Carlos");
        when(administradorService.actualizar(eq(1L), any(AdministradorRequestDTO.class))).thenReturn(administradorResponseDTO);

        mockMvc.perform(put("/api/administradores/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(administradorRequestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idPersonal").value(1));

        verify(administradorService).actualizar(eq(1L), any(AdministradorRequestDTO.class));
    }
}

