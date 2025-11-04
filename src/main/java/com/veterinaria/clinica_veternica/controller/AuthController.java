package com.veterinaria.clinica_veternica.controller;

import com.veterinaria.clinica_veternica.dto.request.auth.LoginRequestDTO;
import com.veterinaria.clinica_veternica.dto.request.auth.RegisterRequestDTO;
import com.veterinaria.clinica_veternica.dto.response.auth.LoginResponseDTO;
import com.veterinaria.clinica_veternica.dto.response.usuario.UsuarioResponseDTO;
import com.veterinaria.clinica_veternica.service.interfaces.IAuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador REST para autenticación y registro de usuarios.
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticación", description = "API para autenticación y registro de usuarios")
public class AuthController {

    private final IAuthService authService;

    /**
     * Endpoint para inicio de sesión.
     *
     * @param loginRequest Datos de login (username y password)
     * @return LoginResponseDTO con el token JWT y datos del usuario
     */
    @Operation(
        summary = "Iniciar sesión",
        description = "Autentica un usuario y retorna un token JWT para acceder a los recursos protegidos"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Login exitoso"),
        @ApiResponse(responseCode = "401", description = "Credenciales inválidas"),
        @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos")
    })
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO loginRequest) {
        LoginResponseDTO response = authService.login(loginRequest);
        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint para registro de nuevos usuarios.
     *
     * @param registerRequest Datos del nuevo usuario
     * @return UsuarioResponseDTO con los datos del usuario creado
     */
    @Operation(
        summary = "Registrar nuevo usuario",
        description = "Crea una nueva cuenta de usuario en el sistema"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Usuario creado exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
        @ApiResponse(responseCode = "409", description = "El username o email ya existe")
    })
    @PostMapping("/register")
    public ResponseEntity<UsuarioResponseDTO> register(@Valid @RequestBody RegisterRequestDTO registerRequest) {
        UsuarioResponseDTO response = authService.register(registerRequest);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * Endpoint para verificar si el usuario está autenticado.
     *
     * @return Mensaje de confirmación
     */
    @Operation(
        summary = "Verificar autenticación",
        description = "Verifica si el token JWT es válido y el usuario está autenticado"
    )
    @GetMapping("/verify")
    public ResponseEntity<String> verify() {
        return ResponseEntity.ok("Token válido. Usuario autenticado correctamente.");
    }
}
