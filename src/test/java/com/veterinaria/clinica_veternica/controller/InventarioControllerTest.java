package com.veterinaria.clinica_veternica.controller;

import com.veterinaria.clinica_veternica.dto.response.inventario.InventarioResponseDTO;
import com.veterinaria.clinica_veternica.security.jwt.JwtAuthenticationFilter;
import com.veterinaria.clinica_veternica.security.jwt.JwtProperties;
import com.veterinaria.clinica_veternica.security.jwt.JwtUtils;
import com.veterinaria.clinica_veternica.security.service.UserDetailsServiceImpl;
import com.veterinaria.clinica_veternica.service.interfaces.IInventarioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests básicos CRUD para InventarioController
 */
@WebMvcTest(controllers = InventarioController.class, excludeAutoConfiguration = {
        org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class
})
@AutoConfigureMockMvc(addFilters = false)
class InventarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private IInventarioService inventarioService;

    @MockitoBean
    private JwtUtils jwtUtils;

    @MockitoBean
    private UserDetailsServiceImpl userDetailsService;

    @MockitoBean
    private JwtProperties jwtProperties;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    private InventarioResponseDTO inventarioResponseDTO;

    @BeforeEach
    void setUp() {
        inventarioResponseDTO = new InventarioResponseDTO();
        inventarioResponseDTO.setIdInventario(1L);
        inventarioResponseDTO.setCantidadActual(50);
        inventarioResponseDTO.setStockMinimo(10);
        inventarioResponseDTO.setValorTotal(new BigDecimal("500.00"));
    }

    @Test
    @DisplayName("READ - Debe buscar inventario por ID")
    @WithMockUser
    void debeBuscarInventarioPorId() throws Exception {
        when(inventarioService.buscarPorId(1L)).thenReturn(inventarioResponseDTO);

        mockMvc.perform(get("/api/inventario/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idInventario").value(1))
                .andExpect(jsonPath("$.cantidadActual").value(50));

        verify(inventarioService).buscarPorId(1L);
    }

    @Test
    @DisplayName("READ - Debe buscar inventario por insumo")
    @WithMockUser
    void debeBuscarInventarioPorInsumo() throws Exception {
        when(inventarioService.buscarPorInsumo(1L)).thenReturn(inventarioResponseDTO);

        mockMvc.perform(get("/api/inventario/insumo/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idInventario").value(1));

        verify(inventarioService).buscarPorInsumo(1L);
    }

    @Test
    @DisplayName("READ - Debe listar todo el inventario")
    @WithMockUser
    void debeListarTodoElInventario() throws Exception {
        List<InventarioResponseDTO> inventarios = Arrays.asList(inventarioResponseDTO);
        when(inventarioService.listarTodos()).thenReturn(inventarios);

        mockMvc.perform(get("/api/inventario"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idInventario").value(1))
                .andExpect(jsonPath("$.length()").value(1));

        verify(inventarioService).listarTodos();
    }

    @Test
    @DisplayName("READ - Debe listar inventario con stock bajo")
    @WithMockUser
    void debeListarInventarioConStockBajo() throws Exception {
        List<InventarioResponseDTO> inventarios = Arrays.asList(inventarioResponseDTO);
        when(inventarioService.listarConStockBajo()).thenReturn(inventarios);

        mockMvc.perform(get("/api/inventario/stock-bajo"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idInventario").value(1));

        verify(inventarioService).listarConStockBajo();
    }

}

