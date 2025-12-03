package com.veterinaria.clinica_veternica.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.veterinaria.clinica_veternica.dto.request.paciente.MascotaRequestDTO;
import com.veterinaria.clinica_veternica.dto.response.paciente.MascotaResponseDTO;
import com.veterinaria.clinica_veternica.security.jwt.JwtAuthenticationFilter;
import com.veterinaria.clinica_veternica.security.jwt.JwtProperties;
import com.veterinaria.clinica_veternica.security.jwt.JwtUtils;
import com.veterinaria.clinica_veternica.security.service.UserDetailsServiceImpl;
import com.veterinaria.clinica_veternica.service.interfaces.IMascotaService;
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

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests básicos CRUD para MascotaController
 */
@WebMvcTest(controllers = MascotaController.class, excludeAutoConfiguration = {
        org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class
})
@AutoConfigureMockMvc(addFilters = false)
@TestPropertySource(properties = {
    "spring.dotenv.enabled=false",
    "JWT_SECRET=404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970",
    "JWT_EXPIRATION=86400000"
})
class MascotaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private IMascotaService mascotaService;

    @MockitoBean
    private JwtUtils jwtUtils;

    @MockitoBean
    private UserDetailsServiceImpl userDetailsService;

    @MockitoBean
    private JwtProperties jwtProperties;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    private MascotaRequestDTO mascotaRequestDTO;
    private MascotaResponseDTO mascotaResponseDTO;

    @BeforeEach
    void setUp() {
        mascotaRequestDTO = new MascotaRequestDTO();
        mascotaRequestDTO.setNombre("Max");
        mascotaRequestDTO.setSexo("Macho");
        mascotaRequestDTO.setFechaNacimiento(LocalDate.of(2020, 1, 15));
        mascotaRequestDTO.setColor("Café");
        mascotaRequestDTO.setPeso(15.5);
        mascotaRequestDTO.setIdPropietario(1L);
        mascotaRequestDTO.setIdEspecie(1L);
        mascotaRequestDTO.setIdRaza(1L);

        mascotaResponseDTO = new MascotaResponseDTO();
        mascotaResponseDTO.setIdMascota(1L);
        mascotaResponseDTO.setNombre("Max");
        mascotaResponseDTO.setSexo("Macho");
        mascotaResponseDTO.setColor("Café");
        mascotaResponseDTO.setPeso(15.5);
        mascotaResponseDTO.setActivo(true);
    }

    @Test
    @DisplayName("CREATE - Debe crear una mascota exitosamente")
    @WithMockUser(roles = {"ADMIN"})
    void debeCrearMascotaExitosamente() throws Exception {
        when(mascotaService.crear(any(MascotaRequestDTO.class))).thenReturn(mascotaResponseDTO);

        mockMvc.perform(post("/api/mascotas")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(mascotaRequestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idMascota").value(1))
                .andExpect(jsonPath("$.nombre").value("Max"));

        verify(mascotaService).crear(any(MascotaRequestDTO.class));
    }

    @Test
    @DisplayName("READ - Debe buscar mascota por ID")
    @WithMockUser
    void debeBuscarMascotaPorId() throws Exception {
        when(mascotaService.buscarPorId(1L)).thenReturn(mascotaResponseDTO);

        mockMvc.perform(get("/api/mascotas/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idMascota").value(1))
                .andExpect(jsonPath("$.nombre").value("Max"));

        verify(mascotaService).buscarPorId(1L);
    }

    @Test
    @DisplayName("READ - Debe listar todas las mascotas")
    @WithMockUser
    void debeListarTodasLasMascotas() throws Exception {
        List<MascotaResponseDTO> mascotas = Arrays.asList(mascotaResponseDTO);
        when(mascotaService.listarTodas()).thenReturn(mascotas);

        mockMvc.perform(get("/api/mascotas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idMascota").value(1))
                .andExpect(jsonPath("$.length()").value(1));

        verify(mascotaService).listarTodas();
    }

    @Test
    @DisplayName("UPDATE - Debe actualizar una mascota")
    @WithMockUser
    void debeActualizarMascota() throws Exception {
        mascotaResponseDTO.setNombre("Max Actualizado");
        when(mascotaService.actualizar(eq(1L), any(MascotaRequestDTO.class))).thenReturn(mascotaResponseDTO);

        mockMvc.perform(put("/api/mascotas/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(mascotaRequestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idMascota").value(1));

        verify(mascotaService).actualizar(eq(1L), any(MascotaRequestDTO.class));
    }

    @Test
    @DisplayName("DELETE - Debe eliminar una mascota")
    @WithMockUser(roles = {"ADMIN"})
    void debeEliminarMascota() throws Exception {
        mockMvc.perform(delete("/api/mascotas/1")
                        .with(csrf()))
                .andExpect(status().isNoContent());

        verify(mascotaService).eliminar(1L);
    }
}
