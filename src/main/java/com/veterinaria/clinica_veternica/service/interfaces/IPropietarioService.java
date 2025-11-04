package com.veterinaria.clinica_veternica.service.interfaces;

import com.veterinaria.clinica_veternica.dto.request.paciente.PropietarioRequestDTO;
import com.veterinaria.clinica_veternica.dto.response.paciente.PropietarioResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

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

    Page<PropietarioResponseDTO> listarTodos(Pageable pageable);

    List<PropietarioResponseDTO> listarActivos();

    List<PropietarioResponseDTO> buscarPorNombre(String nombre);

    PropietarioResponseDTO buscarPorDocumento(String tipoDocumento, String numeroDocumento);

    PropietarioResponseDTO buscarPorEmail(String email);

    List<PropietarioResponseDTO> buscarPorTelefono(String telefono);

    void eliminar(Long id);

    PropietarioResponseDTO activar(Long id);

    boolean existePorDocumento(String tipoDocumento, String numeroDocumento);

    boolean existePorEmail(String email);
}
