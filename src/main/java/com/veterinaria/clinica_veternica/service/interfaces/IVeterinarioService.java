package com.veterinaria.clinica_veternica.service.interfaces;

import com.veterinaria.clinica_veternica.dto.request.usuario.VeterinarioRequestDTO;
import com.veterinaria.clinica_veternica.dto.response.usuario.VeterinarioResponseDTO;

import java.util.List;

/**
 * Interfaz del servicio para gestión de Veterinarios.
 *
 * @author Clínica Veterinaria Team
 * @version 1.0
 * @since 2025-11-03
 */
public interface IVeterinarioService {

    VeterinarioResponseDTO crear(VeterinarioRequestDTO requestDTO);

    VeterinarioResponseDTO actualizar(Long id, VeterinarioRequestDTO requestDTO);

    VeterinarioResponseDTO buscarPorId(Long id);

    VeterinarioResponseDTO buscarPorRegistroProfesional(String registroProfesional);

    List<VeterinarioResponseDTO> listarTodos();

    List<VeterinarioResponseDTO> listarActivos();

    List<VeterinarioResponseDTO> listarDisponibles();

    List<VeterinarioResponseDTO> listarPorEspecialidad(String especialidad);

    List<VeterinarioResponseDTO> buscarPorNombre(String nombre);

    void eliminar(Long id);

    VeterinarioResponseDTO activar(Long id);

    boolean existePorRegistroProfesional(String registroProfesional);

    VeterinarioResponseDTO obtenerPorUsuarioAutenticado();

    VeterinarioResponseDTO crearDesdeUsuario(Long idUsuario);

    int sincronizarUsuariosVeterinarios();
}
