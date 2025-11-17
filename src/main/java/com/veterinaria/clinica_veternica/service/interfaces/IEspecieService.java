package com.veterinaria.clinica_veternica.service.interfaces;

import com.veterinaria.clinica_veternica.dto.request.paciente.EspecieRequestDTO;
import com.veterinaria.clinica_veternica.dto.response.paciente.EspecieResponseDTO;

import java.util.List;

/**
 * Interfaz del servicio para gestión de Especies.
 *
 * @author Clínica Veterinaria Team
 * @version 1.0
 * @since 2025-11-03
 */
public interface IEspecieService {

    /**
     * Crea una nueva especie.
     *
     * @param requestDTO Datos de la especie a crear
     * @return Especie creada
     */
    EspecieResponseDTO crear(EspecieRequestDTO requestDTO);

    /**
     * Actualiza una especie existente.
     *
     * @param id ID de la especie
     * @param requestDTO Datos actualizados
     * @return Especie actualizada
     */
    EspecieResponseDTO actualizar(Long id, EspecieRequestDTO requestDTO);

    /**
     * Busca una especie por ID.
     *
     * @param id ID de la especie
     * @return Especie encontrada
     */
    EspecieResponseDTO buscarPorId(Long id);

    /**
     * Lista todas las especies.
     *
     * @return Lista de especies
     */
    List<EspecieResponseDTO> listarTodas();

    /**
     * Lista solo las especies activas.
     *
     * @return Lista de especies activas
     */
    List<EspecieResponseDTO> listarActivas();

    /**
     * Busca especies por nombre (búsqueda parcial).
     *
     * @param nombre Nombre a buscar
     * @return Lista de especies que coinciden
     */
    List<EspecieResponseDTO> buscarPorNombre(String nombre);

    /**
     * Elimina una especie (soft delete - marca como inactiva).
     *
     * @param id ID de la especie
     */
    void eliminar(Long id);

    /**
     * Activa una especie previamente desactivada.
     *
     * @param id ID de la especie
     * @return Especie activada
     */
    EspecieResponseDTO activar(Long id);

    /**
     * Verifica si una especie existe por su nombre.
     *
     * @param nombre Nombre de la especie
     * @return true si existe, false si no
     */
    boolean existePorNombre(String nombre);
}
