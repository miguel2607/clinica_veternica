package com.veterinaria.clinica_veternica.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.veterinaria.clinica_veternica.dto.request.agenda.ServicioRequestDTO;
import com.veterinaria.clinica_veternica.dto.response.agenda.ServicioResponseDTO;
import com.veterinaria.clinica_veternica.security.jwt.JwtAuthenticationFilter;
import com.veterinaria.clinica_veternica.security.jwt.JwtProperties;
import com.veterinaria.clinica_veternica.security.jwt.JwtUtils;
import com.veterinaria.clinica_veternica.security.service.UserDetailsServiceImpl;
import com.veterinaria.clinica_veternica.service.interfaces.IServicioService;
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

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests básicos CRUD para ServicioController
 */
@WebMvcTest(controllers = ServicioController.class, excludeAutoConfiguration = {
        org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class
})
@AutoConfigureMockMvc(addFilters = false)
@TestPropertySource(properties = {
    "spring.dotenv.enabled=false",
    "JWT_SECRET=404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970",
    "JWT_EXPIRATION=86400000"
})
class ServicioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private IServicioService servicioService;

    @MockitoBean
    private JwtUtils jwtUtils;

    @MockitoBean
    private UserDetailsServiceImpl userDetailsService;

    @MockitoBean
    private JwtProperties jwtProperties;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    private ServicioRequestDTO servicioRequestDTO;
    private ServicioResponseDTO servicioResponseDTO;

    @BeforeEach
    void setUp() {
        servicioRequestDTO = new ServicioRequestDTO();
        servicioRequestDTO.setNombre("Consulta General");
        servicioRequestDTO.setDescripcion("Consulta veterinaria general");
        servicioRequestDTO.setTipoServicio("CONSULTA");
        servicioRequestDTO.setCategoria("CLINICO");
        servicioRequestDTO.setPrecio(new BigDecimal("50.00"));
        servicioRequestDTO.setDuracionMinutos(30);
        servicioRequestDTO.setActivo(true);

        servicioResponseDTO = new ServicioResponseDTO();
        servicioResponseDTO.setIdServicio(1L);
        servicioResponseDTO.setNombre("Consulta General");
        servicioResponseDTO.setPrecio(new BigDecimal("50.00"));
        servicioResponseDTO.setActivo(true);
    }

    @Test
    @DisplayName("CREATE - Debe crear un servicio exitosamente")
    @WithMockUser
    void debeCrearServicioExitosamente() throws Exception {
        when(servicioService.crear(any(ServicioRequestDTO.class))).thenReturn(servicioResponseDTO);

        mockMvc.perform(post("/api/servicios")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(servicioRequestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idServicio").value(1))
                .andExpect(jsonPath("$.nombre").value("Consulta General"));

        verify(servicioService).crear(any(ServicioRequestDTO.class));
    }

    @Test
    @DisplayName("CREATE - Debe crear servicio usando Factory Method")
    @WithMockUser
    void debeCrearServicioConFactory() throws Exception {
        when(servicioService.crearConFactory(anyString(), anyString(), any(BigDecimal.class), anyString()))
                .thenReturn(servicioResponseDTO);

        mockMvc.perform(post("/api/servicios/factory")
                        .with(csrf())
                        .param("nombre", "Cirugía")
                        .param("descripcion", "Procedimiento quirúrgico")
                        .param("precio", "200.00")
                        .param("categoria", "QUIRURGICO"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idServicio").value(1));

        verify(servicioService).crearConFactory(anyString(), anyString(), any(BigDecimal.class), anyString());
    }

    @Test
    @DisplayName("READ - Debe buscar servicio por ID")
    @WithMockUser
    void debeBuscarServicioPorId() throws Exception {
        when(servicioService.buscarPorId(1L)).thenReturn(servicioResponseDTO);

        mockMvc.perform(get("/api/servicios/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idServicio").value(1))
                .andExpect(jsonPath("$.nombre").value("Consulta General"));

        verify(servicioService).buscarPorId(1L);
    }

    @Test
    @DisplayName("READ - Debe listar todos los servicios")
    @WithMockUser
    void debeListarTodosLosServicios() throws Exception {
        List<ServicioResponseDTO> servicios = Arrays.asList(servicioResponseDTO);
        when(servicioService.listarTodos()).thenReturn(servicios);

        mockMvc.perform(get("/api/servicios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idServicio").value(1))
                .andExpect(jsonPath("$.length()").value(1));

        verify(servicioService).listarTodos();
    }

    @Test
    @DisplayName("UPDATE - Debe actualizar un servicio")
    @WithMockUser
    void debeActualizarServicio() throws Exception {
        servicioResponseDTO.setNombre("Consulta Actualizada");
        when(servicioService.actualizar(eq(1L), any(ServicioRequestDTO.class))).thenReturn(servicioResponseDTO);

        mockMvc.perform(put("/api/servicios/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(servicioRequestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idServicio").value(1));

        verify(servicioService).actualizar(eq(1L), any(ServicioRequestDTO.class));
    }
}

