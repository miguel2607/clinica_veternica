package com.veterinaria.clinica_veternica.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.veterinaria.clinica_veternica.dto.request.auth.LoginRequestDTO;
import com.veterinaria.clinica_veternica.dto.request.auth.RegisterRequestDTO;
import com.veterinaria.clinica_veternica.dto.response.auth.LoginResponseDTO;
import com.veterinaria.clinica_veternica.security.jwt.JwtAuthenticationFilter;
import com.veterinaria.clinica_veternica.security.jwt.JwtProperties;
import com.veterinaria.clinica_veternica.security.jwt.JwtUtils;
import com.veterinaria.clinica_veternica.security.service.UserDetailsServiceImpl;
import com.veterinaria.clinica_veternica.service.interfaces.IAuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = AuthController.class, excludeAutoConfiguration = {
        org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class
})
@AutoConfigureMockMvc(addFilters = false)
@TestPropertySource(properties = {
    "spring.dotenv.enabled=false",
    "JWT_SECRET=404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970",
    "JWT_EXPIRATION=86400000"
})
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private IAuthService authService;

    @MockitoBean
    private JwtUtils jwtUtils;

    @MockitoBean
    private UserDetailsServiceImpl userDetailsService;

    @MockitoBean
    private JwtProperties jwtProperties;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    private LoginRequestDTO loginRequestDTO;
    private RegisterRequestDTO registerRequestDTO;
    private LoginResponseDTO loginResponseDTO;

    @BeforeEach
    void setUp() {
        loginRequestDTO = new LoginRequestDTO();
        loginRequestDTO.setUsername("admin");
        loginRequestDTO.setPassword("Admin123!");

        registerRequestDTO = new RegisterRequestDTO();
        registerRequestDTO.setUsername("nuevoUsuario");
        registerRequestDTO.setEmail("nuevo@email.com");
        registerRequestDTO.setPassword("Password123!");
        registerRequestDTO.setRol("VETERINARIO");

        loginResponseDTO = new LoginResponseDTO();
        loginResponseDTO.setToken("jwt-token-test");
        loginResponseDTO.setType("Bearer");
        loginResponseDTO.setIdUsuario(1L);
        loginResponseDTO.setUsername("admin");
        loginResponseDTO.setEmail("admin@veterinaria.com");
        loginResponseDTO.setRol("ADMIN");
    }

    @Test
    @DisplayName("Debe hacer login exitosamente")
    void debeHacerLoginExitosamente() throws Exception {
        when(authService.login(any(LoginRequestDTO.class))).thenReturn(loginResponseDTO);

        mockMvc.perform(post("/api/auth/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt-token-test"))
                .andExpect(jsonPath("$.username").value("admin"))
                .andExpect(jsonPath("$.rol").value("ADMIN"));

        verify(authService).login(any(LoginRequestDTO.class));
    }

    @Test
    @DisplayName("Debe registrar un nuevo usuario")
    void debeRegistrarNuevoUsuario() throws Exception {
        com.veterinaria.clinica_veternica.dto.response.usuario.UsuarioResponseDTO usuarioResponse = 
            new com.veterinaria.clinica_veternica.dto.response.usuario.UsuarioResponseDTO();
        usuarioResponse.setIdUsuario(1L);
        usuarioResponse.setUsername("nuevoUsuario");
        usuarioResponse.setEmail("nuevo@email.com");
        usuarioResponse.setRol("VETERINARIO");
        
        when(authService.register(any(RegisterRequestDTO.class))).thenReturn(usuarioResponse);

        mockMvc.perform(post("/api/auth/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("nuevoUsuario"));

        verify(authService).register(any(RegisterRequestDTO.class));
    }

    @Test
    @DisplayName("Debe validar campos requeridos en login")
    void debeValidarCamposRequeridosEnLogin() throws Exception {
        LoginRequestDTO loginInvalido = new LoginRequestDTO();

        mockMvc.perform(post("/api/auth/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginInvalido)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Debe validar campos requeridos en registro")
    void debeValidarCamposRequeridosEnRegistro() throws Exception {
        RegisterRequestDTO registroInvalido = new RegisterRequestDTO();

        mockMvc.perform(post("/api/auth/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registroInvalido)))
                .andExpect(status().isBadRequest());
    }
}

