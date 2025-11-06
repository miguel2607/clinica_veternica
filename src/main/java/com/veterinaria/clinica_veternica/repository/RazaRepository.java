package com.veterinaria.clinica_veternica.repository;

import com.veterinaria.clinica_veternica.domain.paciente.Especie;
import com.veterinaria.clinica_veternica.domain.paciente.Raza;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para la entidad Raza.
 *
 * @author Clínica Veterinaria Team
 * @version 1.0
 * @since 2025-11-02
 */
@Repository
public interface RazaRepository extends JpaRepository<Raza, Long> {

    /**
     * Busca una raza por su nombre.
     *
     * @param nombre Nombre de la raza
     * @return Optional con la raza si existe
     */
    Optional<Raza> findByNombre(String nombre);

    /**
     * Busca razas por especie.
     *
     * @param especie Especie de la raza
     * @return Lista de razas
     */
    List<Raza> findByEspecie(Especie especie);

    /**
     * Busca razas activas.
     *
     * @param activo Estado activo
     * @return Lista de razas activas
     */
    List<Raza> findByActivo(Boolean activo);

    /**
     * Busca razas por especie y estado.
     *
     * @param especie Especie
     * @param activo Estado
     * @return Lista de razas
     */
    List<Raza> findByEspecieAndActivo(Especie especie, Boolean activo);

    /**
     * Verifica si existe una raza con el nombre dado.
     *
     * @param nombre Nombre a verificar
     * @return true si existe
     */
    boolean existsByNombre(String nombre);

    /**
     * Verifica si existe una raza con el nombre y especie dados.
     *
     * @param nombre Nombre a verificar
     * @param idEspecie ID de la especie
     * @return true si existe
     */
    @Query("SELECT COUNT(r) > 0 FROM Raza r WHERE r.nombre = :nombre AND r.especie.idEspecie = :idEspecie")
    boolean existsByNombreAndEspecieId(@Param("nombre") String nombre, @Param("idEspecie") Long idEspecie);

    /**
     * Verifica si existe otra raza con el mismo nombre y especie (excluyendo el ID dado).
     *
     * @param nombre Nombre a verificar
     * @param idEspecie ID de la especie
     * @param id ID de la raza a excluir
     * @return true si existe otra raza
     */
    @Query("SELECT COUNT(r) > 0 FROM Raza r WHERE r.nombre = :nombre AND r.especie.idEspecie = :idEspecie AND r.idRaza <> :id")
    boolean existsByNombreAndEspecieIdAndIdNot(@Param("nombre") String nombre, @Param("idEspecie") Long idEspecie, @Param("id") Long id);

    /**
     * Busca razas por nombre (búsqueda parcial).
     *
     * @param nombre Término de búsqueda
     * @return Lista de razas
     */
    @Query("SELECT r FROM Raza r WHERE LOWER(r.nombre) LIKE LOWER(CONCAT('%', :nombre, '%'))")
    List<Raza> buscarPorNombre(@Param("nombre") String nombre);

    /**
     * Busca razas por nombre que contenga el texto (case insensitive).
     *
     * @param nombre Nombre a buscar
     * @return Lista de razas que coinciden
     */
    List<Raza> findByNombreContainingIgnoreCase(String nombre);

    /**
     * Busca todas las razas activas.
     *
     * @return Lista de razas activas
     */
    @Query("SELECT r FROM Raza r WHERE r.activo = true ORDER BY r.nombre")
    List<Raza> findRazasActivas();

    /**
     * Busca todas las razas activas (query derivado).
     *
     * @return Lista de razas activas
     */
    List<Raza> findByActivoTrue();

    /**
     * Busca razas activas por especie.
     *
     * @param especie Especie
     * @return Lista de razas activas
     */
    @Query("SELECT r FROM Raza r WHERE r.especie = :especie AND r.activo = true ORDER BY r.nombre")
    List<Raza> findRazasActivasPorEspecie(@Param("especie") Especie especie);

    /**
     * Busca razas por especie ID.
     *
     * @param idEspecie ID de la especie
     * @return Lista de razas activas
     */
    @Query("SELECT r FROM Raza r WHERE r.especie.idEspecie = :idEspecie AND r.activo = true")
    List<Raza> findByEspecieId(@Param("idEspecie") Long idEspecie);

    /**
     * Busca razas activas por especie ID.
     *
     * @param idEspecie ID de la especie
     * @return Lista de razas activas
     */
    @Query("SELECT r FROM Raza r WHERE r.especie.idEspecie = :idEspecie AND r.activo = true")
    List<Raza> findByEspecieIdAndActivoTrue(@Param("idEspecie") Long idEspecie);

    /**
     * Cuenta razas por especie.
     *
     * @param especie Especie
     * @return Número de razas
     */
    @Query("SELECT COUNT(r) FROM Raza r WHERE r.especie = :especie AND r.activo = true")
    long countByEspecie(@Param("especie") Especie especie);

    /**
     * Cuenta razas activas.
     *
     * @return Número de razas activas
     */
    @Query("SELECT COUNT(r) FROM Raza r WHERE r.activo = true")
    long countRazasActivas();
}
