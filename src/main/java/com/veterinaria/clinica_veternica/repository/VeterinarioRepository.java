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
     * @param activo Estado activo del veterinario
     * @return Lista de veterinarios activos
     */
    List<Veterinario> findByActivo(Boolean activo);

    /**
     * Busca veterinarios activos (query derivado).
     *
     * @return Lista de veterinarios activos
     */
    List<Veterinario> findByActivoTrue();

    /**
     * Busca veterinarios por especialidad y estado activo.
     *
     * @param especialidad Especialidad
     * @param activo Estado activo
     * @return Lista de veterinarios
     */
    @Query("SELECT v FROM Veterinario v WHERE v.especialidad = :especialidad AND v.activo = :activo")
    List<Veterinario> findByEspecialidadAndEstado(@Param("especialidad") String especialidad, @Param("activo") Boolean activo);

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
    @Query("SELECT COUNT(v) > 0 FROM Veterinario v WHERE v.usuario.idUsuario = :idUsuario")
    boolean existsByUsuarioId(@Param("idUsuario") Long idUsuario);

    /**
     * Busca un veterinario por su usuario asociado.
     *
     * @param idUsuario ID del usuario
     * @return Optional con el veterinario si existe
     */
    @Query("SELECT v FROM Veterinario v WHERE v.usuario.idUsuario = :idUsuario")
    Optional<Veterinario> findByUsuarioId(@Param("idUsuario") Long idUsuario);

    /**
     * Busca un veterinario por su usuario asociado, cargando la relación usuario.
     *
     * @param idUsuario ID del usuario
     * @return Optional con el veterinario si existe
     */
    @Query("SELECT v FROM Veterinario v JOIN FETCH v.usuario WHERE v.usuario.idUsuario = :idUsuario")
    Optional<Veterinario> findByUsuarioIdWithUsuario(@Param("idUsuario") Long idUsuario);

    /**
     * Lista todos los veterinarios cargando la relación usuario.
     *
     * @return Lista de veterinarios con usuario cargado
     */
    @Query("SELECT DISTINCT v FROM Veterinario v LEFT JOIN FETCH v.usuario")
    List<Veterinario> findAllWithUsuario();

    // ===================================================================
    // CONSULTAS PERSONALIZADAS CON @Query
    // ===================================================================

    /**
     * Busca veterinarios activos.
     *
     * @return Lista de veterinarios activos
     */
    @Query("SELECT v FROM Veterinario v WHERE v.activo = true")
    List<Veterinario> findVeterinariosDisponibles();

    /**
     * Busca veterinarios por especialidad que estén activos.
     *
     * @param especialidad Especialidad buscada
     * @return Lista de veterinarios
     */
    @Query("SELECT v FROM Veterinario v WHERE v.especialidad = :especialidad AND v.activo = true")
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
    @Query("SELECT COUNT(v) FROM Veterinario v WHERE v.activo = true")
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
    @Query("SELECT v FROM Veterinario v WHERE v.aniosExperiencia >= :anios AND v.activo = true")
    List<Veterinario> findByExperienciaMinima(@Param("anios") Integer anios);

    /**
     * Busca veterinarios ordenados por años de experiencia (descendente).
     *
     * @return Lista de veterinarios ordenada
     */
    @Query("SELECT v FROM Veterinario v WHERE v.activo = true ORDER BY v.aniosExperiencia DESC")
    List<Veterinario> findVeterinariosOrdenadosPorExperiencia();
}
