package com.veterinaria.clinica_veternica.service.interfaces;

import com.veterinaria.clinica_veternica.dto.request.auth.LoginRequestDTO;
import com.veterinaria.clinica_veternica.dto.request.auth.RegisterRequestDTO;
import com.veterinaria.clinica_veternica.dto.response.auth.LoginResponseDTO;
import com.veterinaria.clinica_veternica.dto.response.usuario.UsuarioResponseDTO;

/**
 * Interfaz de servicio para autenticación y registro de usuarios.
 */
public interface IAuthService {

    /**
     * Autentica un usuario y genera un token JWT.
     *
     * @param loginRequest Datos de inicio de sesión
     * @return LoginResponseDTO con el token y datos del usuario
     */
    LoginResponseDTO login(LoginRequestDTO loginRequest);

    /**
     * Registra un nuevo usuario en el sistema.
     *
     * @param registerRequest Datos del nuevo usuario
     * @return UsuarioResponseDTO con los datos del usuario creado
     */
    UsuarioResponseDTO register(RegisterRequestDTO registerRequest);
}
