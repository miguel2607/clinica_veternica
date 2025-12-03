package com.veterinaria.clinica_veternica.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.veterinaria.clinica_veternica.dto.request.inventario.TipoInsumoRequestDTO;
import com.veterinaria.clinica_veternica.dto.response.inventario.TipoInsumoResponseDTO;
import com.veterinaria.clinica_veternica.security.jwt.JwtAuthenticationFilter;
import com.veterinaria.clinica_veternica.security.jwt.JwtProperties;
import com.veterinaria.clinica_veternica.security.jwt.JwtUtils;
import com.veterinaria.clinica_veternica.security.service.UserDetailsServiceImpl;
import com.veterinaria.clinica_veternica.service.interfaces.ITipoInsumoService;
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

@WebMvcTest(controllers = TipoInsumoController.class, excludeAutoConfiguration = {
        org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class
})
@AutoConfigureMockMvc(addFilters = false)
@TestPropertySource(properties = {
    "spring.dotenv.enabled=false",
    "JWT_SECRET=404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970",
    "JWT_EXPIRATION=86400000"
})
class TipoInsumoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ITipoInsumoService tipoInsumoService;

    @MockitoBean
    private JwtUtils jwtUtils;

    @MockitoBean
    private UserDetailsServiceImpl userDetailsService;

    @MockitoBean
    private JwtProperties jwtProperties;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    private TipoInsumoRequestDTO requestDTO;
    private TipoInsumoResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        requestDTO = new TipoInsumoRequestDTO();
        requestDTO.setNombre("Material Médico");
        requestDTO.setDescripcion("Material médico descartable");

        responseDTO = new TipoInsumoResponseDTO();
        responseDTO.setIdTipoInsumo(1L);
        responseDTO.setNombre("Material Médico");
        responseDTO.setDescripcion("Material médico descartable");
        responseDTO.setActivo(true);
    }

    @Test
    @DisplayName("CREATE - Debe crear un tipo de insumo exitosamente")
    @WithMockUser(roles = {"VETERINARIO"})
    void debeCrearTipoInsumoExitosamente() throws Exception {
        when(tipoInsumoService.crear(any(TipoInsumoRequestDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(post("/api/inventario/tipos-insumo")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idTipoInsumo").value(1))
                .andExpect(jsonPath("$.nombre").value("Material Médico"));

        verify(tipoInsumoService).crear(any(TipoInsumoRequestDTO.class));
    }

    @Test
    @DisplayName("READ - Debe buscar tipo de insumo por ID")
    @WithMockUser
    void debeBuscarTipoInsumoPorId() throws Exception {
        when(tipoInsumoService.buscarPorId(1L)).thenReturn(responseDTO);

        mockMvc.perform(get("/api/inventario/tipos-insumo/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idTipoInsumo").value(1));

        verify(tipoInsumoService).buscarPorId(1L);
    }

    @Test
    @DisplayName("READ - Debe buscar tipo de insumo por nombre")
    @WithMockUser
    void debeBuscarTipoInsumoPorNombre() throws Exception {
        when(tipoInsumoService.buscarPorNombre("Material Médico")).thenReturn(responseDTO);

        mockMvc.perform(get("/api/inventario/tipos-insumo/nombre/Material Médico"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Material Médico"));

        verify(tipoInsumoService).buscarPorNombre("Material Médico");
    }

    @Test
    @DisplayName("READ - Debe listar todos los tipos de insumo")
    @WithMockUser
    void debeListarTodosLosTiposInsumo() throws Exception {
        List<TipoInsumoResponseDTO> tipos = Arrays.asList(responseDTO);
        when(tipoInsumoService.listarTodos()).thenReturn(tipos);

        mockMvc.perform(get("/api/inventario/tipos-insumo"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idTipoInsumo").value(1));

        verify(tipoInsumoService).listarTodos();
    }

    @Test
    @DisplayName("READ - Debe listar tipos de insumo activos")
    @WithMockUser
    void debeListarTiposInsumoActivos() throws Exception {
        List<TipoInsumoResponseDTO> tipos = Arrays.asList(responseDTO);
        when(tipoInsumoService.listarActivos()).thenReturn(tipos);

        mockMvc.perform(get("/api/inventario/tipos-insumo/activos"))
                .andExpect(status().isOk());

        verify(tipoInsumoService).listarActivos();
    }

    @Test
    @DisplayName("UPDATE - Debe actualizar un tipo de insumo")
    @WithMockUser(roles = {"VETERINARIO"})
    void debeActualizarTipoInsumo() throws Exception {
        when(tipoInsumoService.actualizar(eq(1L), any(TipoInsumoRequestDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(put("/api/inventario/tipos-insumo/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk());

        verify(tipoInsumoService).actualizar(eq(1L), any(TipoInsumoRequestDTO.class));
    }

    @Test
    @DisplayName("DELETE - Debe eliminar un tipo de insumo")
    @WithMockUser(roles = {"ADMIN"})
    void debeEliminarTipoInsumo() throws Exception {
        mockMvc.perform(delete("/api/inventario/tipos-insumo/1")
                        .with(csrf()))
                .andExpect(status().isNoContent());

        verify(tipoInsumoService).eliminar(1L);
    }

    @Test
    @DisplayName("PATCH - Debe activar un tipo de insumo")
    @WithMockUser(roles = {"VETERINARIO"})
    void debeActivarTipoInsumo() throws Exception {
        when(tipoInsumoService.activar(1L)).thenReturn(responseDTO);

        mockMvc.perform(patch("/api/inventario/tipos-insumo/1/activar")
                        .with(csrf()))
                .andExpect(status().isOk());

        verify(tipoInsumoService).activar(1L);
    }

    @Test
    @DisplayName("PATCH - Debe desactivar un tipo de insumo")
    @WithMockUser(roles = {"VETERINARIO"})
    void debeDesactivarTipoInsumo() throws Exception {
        when(tipoInsumoService.desactivar(1L)).thenReturn(responseDTO);

        mockMvc.perform(patch("/api/inventario/tipos-insumo/1/desactivar")
                        .with(csrf()))
                .andExpect(status().isOk());

        verify(tipoInsumoService).desactivar(1L);
    }
}

