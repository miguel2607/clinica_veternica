package com.veterinaria.clinica_veternica.service.interfaces;

import com.veterinaria.clinica_veternica.dto.request.paciente.PropietarioRequestDTO;
import com.veterinaria.clinica_veternica.dto.response.paciente.PropietarioResponseDTO;

import java.util.List;

/**
 * Interfaz del servicio para gestión de Propietarios.
 *
 * @author Clínica Veterinaria Team
 * @version 1.0
 * @since 2025-11-03
 */
public interface IPropietarioService {

    PropietarioResponseDTO crear(PropietarioRequestDTO requestDTO);

    PropietarioResponseDTO actualizar(Long id, PropietarioRequestDTO requestDTO);

    PropietarioResponseDTO buscarPorId(Long id);

    List<PropietarioResponseDTO> listarTodos();

    List<PropietarioResponseDTO> listarActivos();

    List<PropietarioResponseDTO> buscarPorNombre(String nombre);

    PropietarioResponseDTO buscarPorDocumento(String tipoDocumento, String numeroDocumento);

    PropietarioResponseDTO buscarPorEmail(String email);

    List<PropietarioResponseDTO> buscarPorTelefono(String telefono);

    void eliminar(Long id);

    PropietarioResponseDTO activar(Long id);

    boolean existePorDocumento(String tipoDocumento, String numeroDocumento);

    boolean existePorEmail(String email);

    /**
     * Obtiene o crea automáticamente un propietario basado en el usuario autenticado.
     * Si el propietario no existe, lo crea con datos básicos del usuario.
     *
     * @param email Email del usuario autenticado
     * @return PropietarioResponseDTO del propietario encontrado o creado
     */
    PropietarioResponseDTO obtenerOCrearPropietarioPorEmail(String email);

    /**
     * Sincroniza usuarios con rol PROPIETARIO creando registros de propietarios
     * para aquellos que no tienen uno asociado.
     *
     * Útil para migración de datos y corrección de inconsistencias.
     *
     * @return Número de propietarios creados durante la sincronización
     */
    int sincronizarUsuariosPropietarios();
}
