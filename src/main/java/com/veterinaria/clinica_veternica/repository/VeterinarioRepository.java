package com.veterinaria.clinica_veternica.repository;

import com.veterinaria.clinica_veternica.domain.usuario.Veterinario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para la entidad Veterinario.
 *
 * Proporciona operaciones CRUD y consultas personalizadas para
 * la gestión de veterinarios.
 *
 * @author Clínica Veterinaria Team
 * @version 1.0
 * @since 2025-11-02
 */
@Repository
public interface VeterinarioRepository extends JpaRepository<Veterinario, Long> {

    // ===================================================================
    // CONSULTAS DERIVADAS
    // ===================================================================

    /**
     * Busca un veterinario por su tarjeta profesional.
     *
     * @param tarjetaProfesional Número de tarjeta profesional
     * @return Optional con el veterinario si existe
     */
    Optional<Veterinario> findByTarjetaProfesional(String tarjetaProfesional);

    /**
     * Busca un veterinario por su registro profesional.
     *
     * @param registroProfesional Número de registro profesional
     * @return Optional con el veterinario si existe
     */
    Optional<Veterinario> findByRegistroProfesional(String registroProfesional);

    /**
     * Busca veterinarios por especialidad.
     *
     * @param especialidad Especialidad del veterinario
     * @return Lista de veterinarios con esa especialidad
     */
    List<Veterinario> findByEspecialidad(String especialidad);

    /**
     * Busca veterinarios por especialidad (case insensitive y búsqueda parcial).
     *
     * @param especialidad Especialidad a buscar
     * @return Lista de veterinarios
     */
    List<Veterinario> findByEspecialidadContainingIgnoreCase(String especialidad);

    /**
     * Busca veterinarios activos.
     *
     * @param estado Estado del veterinario
     * @return Lista de veterinarios activos
     */
    List<Veterinario> findByEstado(Boolean estado);

    /**
     * Busca veterinarios activos (query derivado).
     *
     * @return Lista de veterinarios activos
     */
    List<Veterinario> findByActivoTrue();

    /**
     * Busca veterinarios por especialidad y estado.
     *
     * @param especialidad Especialidad
     * @param estado Estado
     * @return Lista de veterinarios
     */
    List<Veterinario> findByEspecialidadAndEstado(String especialidad, Boolean estado);

    /**
     * Verifica si existe un veterinario con la tarjeta profesional dada.
     *
     * @param tarjetaProfesional Tarjeta profesional a verificar
     * @return true si existe
     */
    boolean existsByTarjetaProfesional(String tarjetaProfesional);

    /**
     * Verifica si existe un veterinario con el registro profesional dado.
     *
     * @param registroProfesional Registro profesional a verificar
     * @return true si existe
     */
    boolean existsByRegistroProfesional(String registroProfesional);

    /**
     * Verifica si existe un veterinario asociado al usuario dado.
     *
     * @param idUsuario ID del usuario
     * @return true si existe
     */
    boolean existsByUsuarioId(Long idUsuario);

    // ===================================================================
    // CONSULTAS PERSONALIZADAS CON @Query
    // ===================================================================

    /**
     * Busca veterinarios activos y disponibles.
     *
     * @return Lista de veterinarios disponibles
     */
    @Query("SELECT v FROM Veterinario v WHERE v.estado = true AND v.disponible = true")
    List<Veterinario> findVeterinariosDisponibles();

    /**
     * Busca veterinarios por especialidad que estén activos y disponibles.
     *
     * @param especialidad Especialidad buscada
     * @return Lista de veterinarios
     */
    @Query("SELECT v FROM Veterinario v WHERE v.especialidad = :especialidad " +
           "AND v.estado = true AND v.disponible = true")
    List<Veterinario> findVeterinariosDisponiblesPorEspecialidad(@Param("especialidad") String especialidad);

    /**
     * Busca veterinarios por nombre o apellido (búsqueda parcial).
     *
     * @param busqueda Término de búsqueda
     * @return Lista de veterinarios
     */
    @Query("SELECT v FROM Veterinario v WHERE " +
           "LOWER(v.nombres) LIKE LOWER(CONCAT('%', :busqueda, '%')) OR " +
           "LOWER(v.apellidos) LIKE LOWER(CONCAT('%', :busqueda, '%'))")
    List<Veterinario> buscarVeterinarios(@Param("busqueda") String busqueda);

    /**
     * Busca veterinarios por nombre (alias para buscarVeterinarios).
     *
     * @param nombre Término de búsqueda
     * @return Lista de veterinarios
     */
    @Query("SELECT v FROM Veterinario v WHERE " +
           "LOWER(v.nombres) LIKE LOWER(CONCAT('%', :nombre, '%')) OR " +
           "LOWER(v.apellidos) LIKE LOWER(CONCAT('%', :nombre, '%'))")
    List<Veterinario> buscarPorNombre(@Param("nombre") String nombre);

    /**
     * Cuenta veterinarios por especialidad.
     *
     * @param especialidad Especialidad
     * @return Número de veterinarios
     */
    @Query("SELECT COUNT(v) FROM Veterinario v WHERE v.especialidad = :especialidad")
    long countByEspecialidad(@Param("especialidad") String especialidad);

    /**
     * Cuenta veterinarios activos.
     *
     * @return Número de veterinarios activos
     */
    @Query("SELECT COUNT(v) FROM Veterinario v WHERE v.estado = true")
    long countVeterinariosActivos();

    /**
     * Obtiene todas las especialidades distintas.
     *
     * @return Lista de especialidades
     */
    @Query("SELECT DISTINCT v.especialidad FROM Veterinario v WHERE v.especialidad IS NOT NULL")
    List<String> findAllEspecialidades();

    /**
     * Busca veterinarios con años de experiencia mayores o iguales a un valor.
     *
     * @param anios Años de experiencia mínimos
     * @return Lista de veterinarios
     */
    @Query("SELECT v FROM Veterinario v WHERE v.aniosExperiencia >= :anios AND v.estado = true")
    List<Veterinario> findByExperienciaMinima(@Param("anios") Integer anios);

    /**
     * Busca veterinarios ordenados por años de experiencia (descendente).
     *
     * @return Lista de veterinarios ordenada
     */
    @Query("SELECT v FROM Veterinario v WHERE v.estado = true ORDER BY v.aniosExperiencia DESC")
    List<Veterinario> findVeterinariosOrdenadosPorExperiencia();
}
