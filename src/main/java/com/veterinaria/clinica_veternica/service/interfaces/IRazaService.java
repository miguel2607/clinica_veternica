package com.veterinaria.clinica_veternica.service.interfaces;

import com.veterinaria.clinica_veternica.dto.request.paciente.RazaRequestDTO;
import com.veterinaria.clinica_veternica.dto.response.paciente.RazaResponseDTO;

import java.util.List;

/**
 * Interfaz del servicio para gestión de Razas.
 *
 * @author Clínica Veterinaria Team
 * @version 1.0
 * @since 2025-11-03
 */
public interface IRazaService {

    /**
     * Crea una nueva raza.
     *
     * @param requestDTO Datos de la raza a crear
     * @return Raza creada
     */
    RazaResponseDTO crear(RazaRequestDTO requestDTO);

    /**
     * Actualiza una raza existente.
     *
     * @param id ID de la raza
     * @param requestDTO Datos actualizados
     * @return Raza actualizada
     */
    RazaResponseDTO actualizar(Long id, RazaRequestDTO requestDTO);

    /**
     * Busca una raza por ID.
     *
     * @param id ID de la raza
     * @return Raza encontrada
     */
    RazaResponseDTO buscarPorId(Long id);

    /**
     * Lista todas las razas.
     *
     * @return Lista de razas
     */
    List<RazaResponseDTO> listarTodas();

    /**
     * Lista razas por especie.
     *
     * @param idEspecie ID de la especie
     * @return Lista de razas de la especie
     */
    List<RazaResponseDTO> listarPorEspecie(Long idEspecie);

    /**
     * Lista solo las razas activas.
     *
     * @return Lista de razas activas
     */
    List<RazaResponseDTO> listarActivas();

    /**
     * Lista razas activas por especie.
     *
     * @param idEspecie ID de la especie
     * @return Lista de razas activas de la especie
     */
    List<RazaResponseDTO> listarActivasPorEspecie(Long idEspecie);

    /**
     * Busca razas por nombre (búsqueda parcial).
     *
     * @param nombre Nombre a buscar
     * @return Lista de razas que coinciden
     */
    List<RazaResponseDTO> buscarPorNombre(String nombre);

    /**
     * Elimina una raza (soft delete - marca como inactiva).
     *
     * @param id ID de la raza
     */
    void eliminar(Long id);

    /**
     * Activa una raza previamente desactivada.
     *
     * @param id ID de la raza
     * @return Raza activada
     */
    RazaResponseDTO activar(Long id);

    /**
     * Verifica si una raza existe por su nombre y especie.
     *
     * @param nombre Nombre de la raza
     * @param idEspecie ID de la especie
     * @return true si existe, false si no
     */
    boolean existePorNombreYEspecie(String nombre, Long idEspecie);
}
