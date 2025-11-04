package com.veterinaria.clinica_veternica.repository;

import com.veterinaria.clinica_veternica.domain.paciente.Especie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para la entidad Especie.
 *
 * @author Clínica Veterinaria Team
 * @version 1.0
 * @since 2025-11-02
 */
@Repository
public interface EspecieRepository extends JpaRepository<Especie, Long> {

    /**
     * Busca una especie por su nombre.
     *
     * @param nombre Nombre de la especie
     * @return Optional con la especie si existe
     */
    Optional<Especie> findByNombre(String nombre);

    /**
     * Busca especies activas.
     *
     * @param activo Estado activo
     * @return Lista de especies activas
     */
    List<Especie> findByActivo(Boolean activo);

    /**
     * Verifica si existe una especie con el nombre dado.
     *
     * @param nombre Nombre a verificar
     * @return true si existe
     */
    boolean existsByNombre(String nombre);

    /**
     * Verifica si existe una especie con el nombre dado (case insensitive).
     *
     * @param nombre Nombre a verificar
     * @return true si existe
     */
    boolean existsByNombreIgnoreCase(String nombre);

    /**
     * Busca especies por nombre (búsqueda parcial).
     *
     * @param nombre Término de búsqueda
     * @return Lista de especies
     */
    @Query("SELECT e FROM Especie e WHERE LOWER(e.nombre) LIKE LOWER(CONCAT('%', :nombre, '%'))")
    List<Especie> buscarPorNombre(@Param("nombre") String nombre);

    /**
     * Busca especies por nombre que contenga el texto (case insensitive).
     *
     * @param nombre Nombre a buscar
     * @return Lista de especies que coinciden
     */
    List<Especie> findByNombreContainingIgnoreCase(String nombre);

    /**
     * Busca todas las especies activas.
     *
     * @return Lista de especies activas
     */
    @Query("SELECT e FROM Especie e WHERE e.activo = true ORDER BY e.nombre")
    List<Especie> findEspeciesActivas();

    /**
     * Busca todas las especies activas (query derivado).
     *
     * @return Lista de especies activas
     */
    List<Especie> findByActivoTrue();

    /**
     * Cuenta especies activas.
     *
     * @return Número de especies activas
     */
    @Query("SELECT COUNT(e) FROM Especie e WHERE e.activo = true")
    long countEspeciesActivas();
}
