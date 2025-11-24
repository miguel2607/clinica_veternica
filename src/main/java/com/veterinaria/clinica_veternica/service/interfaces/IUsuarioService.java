package com.veterinaria.clinica_veternica.service.interfaces;

import com.veterinaria.clinica_veternica.dto.request.usuario.UsuarioRequestDTO;
import com.veterinaria.clinica_veternica.dto.response.usuario.UsuarioResponseDTO;

import java.util.List;

/**
 * Interfaz del servicio para gestión de Usuarios.
 *
 * @author Clínica Veterinaria Team
 * @version 1.0
 * @since 2025-11-03
 */
public interface IUsuarioService {

    UsuarioResponseDTO crear(UsuarioRequestDTO requestDTO);

    UsuarioResponseDTO actualizar(Long id, UsuarioRequestDTO requestDTO);

    UsuarioResponseDTO buscarPorId(Long id);

    UsuarioResponseDTO buscarPorUsername(String username);

    UsuarioResponseDTO buscarPorEmail(String email);

    List<UsuarioResponseDTO> listarTodos();

    List<UsuarioResponseDTO> listarPorRol(String rol);

    List<UsuarioResponseDTO> listarPorEstado(Boolean estado);

    void cambiarPassword(Long id, String passwordActual, String passwordNueva);

    void resetearPassword(Long id, String nuevaPassword);

    void bloquearUsuario(Long id, String motivo);

    void desbloquearUsuario(Long id);

    void activarUsuario(Long id);

    void desactivarUsuario(Long id);

    void eliminar(Long id);

    boolean existePorUsername(String username);

    boolean existePorEmail(String email);

    boolean verificarPassword(Long id, String password);
}
