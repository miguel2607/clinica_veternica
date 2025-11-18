package com.veterinaria.clinica_veternica.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.veterinaria.clinica_veternica.dto.request.agenda.CitaRequestDTO;
import com.veterinaria.clinica_veternica.dto.request.clinico.EvolucionClinicaRequestDTO;
import com.veterinaria.clinica_veternica.dto.response.agenda.CitaResponseDTO;
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
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = CitaFacadeController.class, excludeAutoConfiguration = {
        org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class
})
@AutoConfigureMockMvc(addFilters = false)
class CitaFacadeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

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

    private CitaRequestDTO citaRequestDTO;
    private CitaResponseDTO citaResponseDTO;
    private EvolucionClinicaRequestDTO evolucionRequestDTO;

    @BeforeEach
    void setUp() {
        citaRequestDTO = new CitaRequestDTO();
        citaRequestDTO.setIdMascota(1L);
        citaRequestDTO.setIdVeterinario(1L);
        citaRequestDTO.setIdServicio(1L);
        citaRequestDTO.setFechaCita(LocalDate.now().plusDays(1));
        citaRequestDTO.setHoraCita(LocalTime.of(10, 0));
        citaRequestDTO.setMotivo("Consulta de rutina");

        citaResponseDTO = new CitaResponseDTO();
        citaResponseDTO.setIdCita(1L);
        citaResponseDTO.setFechaCita(LocalDate.now().plusDays(1));
        citaResponseDTO.setHoraCita(LocalTime.of(10, 0));

        evolucionRequestDTO = EvolucionClinicaRequestDTO.builder()
                .idHistoriaClinica(1L)
                .idVeterinario(1L)
                .tipoEvolucion("Consulta General")
                .motivoConsulta("Consulta de rutina para revisión general")
                .hallazgosExamen("Examen físico completo realizado, signos vitales normales")
                .diagnostico("Diagnóstico de prueba")
                .planTratamiento("Tratamiento de prueba")
                .observaciones("Observaciones de prueba")
                .build();
    }

    @Test
    @DisplayName("POST - Debe crear cita con notificación automática")
    @WithMockUser(roles = {"RECEPCIONISTA"})
    void debeCrearCitaConNotificacion() throws Exception {
        when(clinicaFacade.crearCitaConNotificacion(any(CitaRequestDTO.class))).thenReturn(citaResponseDTO);

        mockMvc.perform(post("/api/facade/citas/crear-con-notificacion")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(citaRequestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.cita").exists())
                .andExpect(jsonPath("$.notificacionEnviada").value(true));

        verify(clinicaFacade).crearCitaConNotificacion(any(CitaRequestDTO.class));
    }

    @Test
    @DisplayName("POST - Debe procesar atención completa")
    @WithMockUser(roles = {"VETERINARIO"})
    void debeProcesarAtencionCompleta() throws Exception {
        Map<String, Object> resultado = Map.of(
                "cita", citaResponseDTO,
                "evolucion", Map.of("idEvolucion", 1L)
        );
        when(clinicaFacade.procesarAtencionCompleta(eq(1L), any(EvolucionClinicaRequestDTO.class)))
                .thenReturn(resultado);

        mockMvc.perform(post("/api/facade/citas/1/atencion-completa")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(evolucionRequestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cita.idCita").value(1));

        verify(clinicaFacade).procesarAtencionCompleta(eq(1L), any(EvolucionClinicaRequestDTO.class));
    }
}

