package com.veterinaria.clinica_veternica.controller;

import com.veterinaria.clinica_veternica.domain.inventario.EstadoInsumo;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.veterinaria.clinica_veternica.dto.request.inventario.InsumoRequestDTO;
import com.veterinaria.clinica_veternica.dto.response.inventario.InsumoResponseDTO;
import com.veterinaria.clinica_veternica.security.jwt.JwtAuthenticationFilter;
import com.veterinaria.clinica_veternica.security.jwt.JwtProperties;
import com.veterinaria.clinica_veternica.security.jwt.JwtUtils;
import com.veterinaria.clinica_veternica.security.service.UserDetailsServiceImpl;
import com.veterinaria.clinica_veternica.service.interfaces.IInsumoService;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = InsumoController.class, excludeAutoConfiguration = {
        org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class
})
@AutoConfigureMockMvc(addFilters = false)
@TestPropertySource(properties = {
    "spring.dotenv.enabled=false",
    "JWT_SECRET=404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970",
    "JWT_EXPIRATION=86400000"
})
class InsumoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private IInsumoService insumoService;

    @MockitoBean
    private JwtUtils jwtUtils;

    @MockitoBean
    private UserDetailsServiceImpl userDetailsService;

    @MockitoBean
    private JwtProperties jwtProperties;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    private InsumoRequestDTO requestDTO;
    private InsumoResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        requestDTO = InsumoRequestDTO.builder()
                .nombre("Jeringas")
                .codigo("INS001")
                .cantidadStock(100)
                .stockMinimo(10)
                .precioCompra(BigDecimal.valueOf(5.50))
                .idTipoInsumo(1L)
                .unidadMedida("Unidad")
                .estado(EstadoInsumo.DISPONIBLE)
                .build();

        responseDTO = InsumoResponseDTO.builder()
                .idInsumo(1L)
                .nombre("Jeringas")
                .codigo("INS001")
                .cantidadStock(100)
                .stockMinimo(10)
                .precioCompra(BigDecimal.valueOf(5.50))
                .activo(true)
                .build();
    }

    @Test
    @DisplayName("CREATE - Debe crear un insumo exitosamente")
    @WithMockUser(roles = {"VETERINARIO"})
    void debeCrearInsumoExitosamente() throws Exception {
        when(insumoService.crear(any(InsumoRequestDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(post("/api/inventario/insumos")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idInsumo").value(1))
                .andExpect(jsonPath("$.nombre").value("Jeringas"));

        verify(insumoService).crear(any(InsumoRequestDTO.class));
    }

    @Test
    @DisplayName("READ - Debe buscar insumo por ID")
    @WithMockUser
    void debeBuscarInsumoPorId() throws Exception {
        when(insumoService.buscarPorId(1L)).thenReturn(responseDTO);

        mockMvc.perform(get("/api/inventario/insumos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idInsumo").value(1));

        verify(insumoService).buscarPorId(1L);
    }

    @Test
    @DisplayName("READ - Debe buscar insumo por código")
    @WithMockUser
    void debeBuscarInsumoPorCodigo() throws Exception {
        when(insumoService.buscarPorCodigo("INS001")).thenReturn(responseDTO);

        mockMvc.perform(get("/api/inventario/insumos/codigo/INS001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.codigo").value("INS001"));

        verify(insumoService).buscarPorCodigo("INS001");
    }

    @Test
    @DisplayName("READ - Debe listar todos los insumos")
    @WithMockUser
    void debeListarTodosLosInsumos() throws Exception {
        List<InsumoResponseDTO> insumos = Arrays.asList(responseDTO);
        when(insumoService.listarTodos()).thenReturn(insumos);

        mockMvc.perform(get("/api/inventario/insumos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idInsumo").value(1));

        verify(insumoService).listarTodos();
    }

    @Test
    @DisplayName("READ - Debe listar insumos activos")
    @WithMockUser
    void debeListarInsumosActivos() throws Exception {
        List<InsumoResponseDTO> insumos = Arrays.asList(responseDTO);
        when(insumoService.listarActivos()).thenReturn(insumos);

        mockMvc.perform(get("/api/inventario/insumos/activos"))
                .andExpect(status().isOk());

        verify(insumoService).listarActivos();
    }

    @Test
    @DisplayName("READ - Debe listar insumos con stock bajo")
    @WithMockUser
    void debeListarInsumosConStockBajo() throws Exception {
        List<InsumoResponseDTO> insumos = Arrays.asList(responseDTO);
        when(insumoService.listarConStockBajo()).thenReturn(insumos);

        mockMvc.perform(get("/api/inventario/insumos/stock-bajo"))
                .andExpect(status().isOk());

        verify(insumoService).listarConStockBajo();
    }

    @Test
    @DisplayName("READ - Debe listar insumos agotados")
    @WithMockUser
    void debeListarInsumosAgotados() throws Exception {
        List<InsumoResponseDTO> insumos = Arrays.asList(responseDTO);
        when(insumoService.listarAgotados()).thenReturn(insumos);

        mockMvc.perform(get("/api/inventario/insumos/agotados"))
                .andExpect(status().isOk());

        verify(insumoService).listarAgotados();
    }

    @Test
    @DisplayName("READ - Debe listar insumos por tipo")
    @WithMockUser
    void debeListarInsumosPorTipo() throws Exception {
        List<InsumoResponseDTO> insumos = Arrays.asList(responseDTO);
        when(insumoService.listarPorTipoInsumo(1L)).thenReturn(insumos);

        mockMvc.perform(get("/api/inventario/insumos/tipo/1"))
                .andExpect(status().isOk());

        verify(insumoService).listarPorTipoInsumo(1L);
    }

    @Test
    @DisplayName("READ - Debe buscar insumos por nombre")
    @WithMockUser
    void debeBuscarInsumosPorNombre() throws Exception {
        List<InsumoResponseDTO> insumos = Arrays.asList(responseDTO);
        when(insumoService.buscarPorNombre("Jeringas")).thenReturn(insumos);

        mockMvc.perform(get("/api/inventario/insumos/buscar")
                        .param("nombre", "Jeringas"))
                .andExpect(status().isOk());

        verify(insumoService).buscarPorNombre("Jeringas");
    }

    @Test
    @DisplayName("UPDATE - Debe actualizar un insumo")
    @WithMockUser(roles = {"VETERINARIO"})
    void debeActualizarInsumo() throws Exception {
        when(insumoService.actualizar(eq(1L), any(InsumoRequestDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(put("/api/inventario/insumos/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk());

        verify(insumoService).actualizar(eq(1L), any(InsumoRequestDTO.class));
    }

    @Test
    @DisplayName("DELETE - Debe eliminar un insumo")
    @WithMockUser(roles = {"ADMIN"})
    void debeEliminarInsumo() throws Exception {
        mockMvc.perform(delete("/api/inventario/insumos/1")
                        .with(csrf()))
                .andExpect(status().isNoContent());

        verify(insumoService).eliminar(1L);
    }

    @Test
    @DisplayName("PATCH - Debe activar un insumo")
    @WithMockUser(roles = {"VETERINARIO"})
    void debeActivarInsumo() throws Exception {
        when(insumoService.activar(1L)).thenReturn(responseDTO);

        mockMvc.perform(patch("/api/inventario/insumos/1/activar")
                        .with(csrf()))
                .andExpect(status().isOk());

        verify(insumoService).activar(1L);
    }

    @Test
    @DisplayName("PATCH - Debe desactivar un insumo")
    @WithMockUser(roles = {"VETERINARIO"})
    void debeDesactivarInsumo() throws Exception {
        when(insumoService.desactivar(1L)).thenReturn(responseDTO);

        mockMvc.perform(patch("/api/inventario/insumos/1/desactivar")
                        .with(csrf()))
                .andExpect(status().isOk());

        verify(insumoService).desactivar(1L);
    }
}

