package com.veterinaria.clinica_veternica.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.veterinaria.clinica_veternica.dto.request.clinico.EvolucionClinicaRequestDTO;
import com.veterinaria.clinica_veternica.dto.response.clinico.EvolucionClinicaResponseDTO;
import com.veterinaria.clinica_veternica.security.jwt.JwtAuthenticationFilter;
import com.veterinaria.clinica_veternica.security.jwt.JwtProperties;
import com.veterinaria.clinica_veternica.security.jwt.JwtUtils;
import com.veterinaria.clinica_veternica.security.service.UserDetailsServiceImpl;
import com.veterinaria.clinica_veternica.service.interfaces.IEvolucionClinicaService;
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

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = EvolucionClinicaController.class, excludeAutoConfiguration = {
        org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class
})
@AutoConfigureMockMvc(addFilters = false)
class EvolucionClinicaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private IEvolucionClinicaService evolucionClinicaService;

    @MockitoBean
    private JwtUtils jwtUtils;

    @MockitoBean
    private UserDetailsServiceImpl userDetailsService;

    @MockitoBean
    private JwtProperties jwtProperties;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    private EvolucionClinicaRequestDTO requestDTO;
    private EvolucionClinicaResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        requestDTO = EvolucionClinicaRequestDTO.builder()
                .idHistoriaClinica(1L)
                .idVeterinario(1L)
                .tipoEvolucion("Consulta General")
                .motivoConsulta("Consulta de rutina para revisión general")
                .hallazgosExamen("Examen físico completo realizado, signos vitales normales")
                .diagnostico("Diagnóstico de prueba")
                .planTratamiento("Tratamiento de prueba")
                .observaciones("Observaciones de prueba")
                .build();

        responseDTO = new EvolucionClinicaResponseDTO();
        responseDTO.setIdEvolucion(1L);
        responseDTO.setObservaciones("Observaciones de prueba");
        responseDTO.setDiagnostico("Diagnóstico de prueba");
        responseDTO.setPlanTratamiento("Tratamiento de prueba");
        responseDTO.setFechaEvolucion(LocalDateTime.now());
    }

    @Test
    @DisplayName("CREATE - Debe crear una evolución clínica exitosamente")
    @WithMockUser(roles = {"VETERINARIO"})
    void debeCrearEvolucionClinicaExitosamente() throws Exception {
        when(evolucionClinicaService.crear(eq(1L), any(EvolucionClinicaRequestDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(post("/api/evoluciones-clinicas")
                        .with(csrf())
                        .param("idHistoriaClinica", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idEvolucion").value(1))
                .andExpect(jsonPath("$.observaciones").value("Observaciones de prueba"));

        verify(evolucionClinicaService).crear(eq(1L), any(EvolucionClinicaRequestDTO.class));
    }

    @Test
    @DisplayName("READ - Debe listar evoluciones por historia clínica")
    @WithMockUser
    void debeListarEvolucionesPorHistoriaClinica() throws Exception {
        List<EvolucionClinicaResponseDTO> evoluciones = Arrays.asList(responseDTO);
        when(evolucionClinicaService.listarPorHistoriaClinica(1L)).thenReturn(evoluciones);

        mockMvc.perform(get("/api/evoluciones-clinicas/historia-clinica/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idEvolucion").value(1))
                .andExpect(jsonPath("$.length()").value(1));

        verify(evolucionClinicaService).listarPorHistoriaClinica(1L);
    }
}

