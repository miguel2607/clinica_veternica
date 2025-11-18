package com.veterinaria.clinica_veternica.controller;

import com.veterinaria.clinica_veternica.patterns.structural.facade.ClinicaFacade;
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

import java.util.Map;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = DashboardFacadeController.class, excludeAutoConfiguration = {
        org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class
})
@AutoConfigureMockMvc(addFilters = false)
class DashboardFacadeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ClinicaFacade clinicaFacade;

    @MockitoBean
    private JwtUtils jwtUtils;

    @MockitoBean
    private UserDetailsServiceImpl userDetailsService;

    @MockitoBean
    private JwtProperties jwtProperties;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    private Map<String, Object> dashboardData;
    private Map<String, Object> estadisticasData;
    private Map<String, Object> inventarioData;

    @BeforeEach
    void setUp() {
        dashboardData = Map.of(
                "citasHoy", 5,
                "stockBajo", 3,
                "notificacionesPendientes", 2
        );

        estadisticasData = Map.of(
                "totalMascotas", 100,
                "totalPropietarios", 80,
                "citasMes", 150
        );

        inventarioData = Map.of(
                "totalInsumos", 50,
                "stockBajo", 5,
                "stockAgotado", 2
        );
    }

    @Test
    @DisplayName("GET - Debe obtener dashboard completo")
    @WithMockUser(roles = {"VETERINARIO"})
    void debeObtenerDashboard() throws Exception {
        when(clinicaFacade.obtenerDashboard()).thenReturn(dashboardData);

        mockMvc.perform(get("/api/facade/dashboard"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.citasHoy").value(5));

        verify(clinicaFacade).obtenerDashboard();
    }

    @Test
    @DisplayName("GET - Debe obtener estadísticas generales")
    @WithMockUser(roles = {"ADMIN"})
    void debeObtenerEstadisticas() throws Exception {
        when(clinicaFacade.obtenerEstadisticasGenerales()).thenReturn(estadisticasData);

        mockMvc.perform(get("/api/facade/dashboard/estadisticas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalMascotas").value(100));

        verify(clinicaFacade).obtenerEstadisticasGenerales();
    }

    @Test
    @DisplayName("GET - Debe obtener resumen de inventario")
    @WithMockUser(roles = {"VETERINARIO"})
    void debeObtenerResumenInventario() throws Exception {
        when(clinicaFacade.obtenerResumenInventario()).thenReturn(inventarioData);

        mockMvc.perform(get("/api/facade/dashboard/inventario"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalInsumos").value(50));

        verify(clinicaFacade).obtenerResumenInventario();
    }
}

