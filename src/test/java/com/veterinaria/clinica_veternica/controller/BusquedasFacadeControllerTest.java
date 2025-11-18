package com.veterinaria.clinica_veternica.controller;

import com.veterinaria.clinica_veternica.dto.response.facade.BusquedaGlobalDTO;
import com.veterinaria.clinica_veternica.patterns.structural.facade.BusquedaFacadeService;
import com.veterinaria.clinica_veternica.security.jwt.JwtAuthenticationFilter;
import com.veterinaria.clinica_veternica.security.jwt.JwtProperties;
import com.veterinaria.clinica_veternica.security.jwt.JwtUtils;
import com.veterinaria.clinica_veternica.security.service.UserDetailsServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = BusquedasFacadeController.class, excludeAutoConfiguration = {
        org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class
})
@AutoConfigureMockMvc(addFilters = false)
class BusquedasFacadeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BusquedaFacadeService busquedaFacadeService;

    @MockitoBean
    private JwtUtils jwtUtils;

    @MockitoBean
    private UserDetailsServiceImpl userDetailsService;

    @MockitoBean
    private JwtProperties jwtProperties;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    private BusquedaGlobalDTO responseData;

    @BeforeEach
    void setUp() {
        responseData = BusquedaGlobalDTO.builder()
                .terminoBusqueda("test")
                .mascotas(Collections.emptyList())
                .totalMascotas(0)
                .propietarios(Collections.emptyList())
                .totalPropietarios(0)
                .veterinarios(Collections.emptyList())
                .totalVeterinarios(0)
                .totalResultados(0)
                .build();
    }

    @Test
    @DisplayName("GET - Debe realizar búsqueda global")
    @WithMockUser(roles = {"VETERINARIO"})
    void debeRealizarBusquedaGlobal() throws Exception {
        when(busquedaFacadeService.busquedaGlobal("test")).thenReturn(responseData);

        mockMvc.perform(get("/api/facade/busquedas/global")
                        .param("termino", "test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalResultados").value(0));

        verify(busquedaFacadeService).busquedaGlobal("test");
    }
}

