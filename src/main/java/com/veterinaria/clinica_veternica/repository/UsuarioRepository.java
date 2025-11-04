package com.veterinaria.clinica_veternica.repository;

import com.veterinaria.clinica_veternica.domain.usuario.RolUsuario;
import com.veterinaria.clinica_veternica.domain.usuario.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio para la entidad Usuario.
 *
 * Proporciona operaciones CRUD y consultas personalizadas para
 * la gestión de usuarios del sistema.
 *
 * @author Clínica Veterinaria Team
 * @version 1.0
 * @since 2025-11-02
 */
@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    // ===================================================================
    // CONSULTAS DERIVADAS (QUERY METHODS)
    // ===================================================================

    /**
     * Busca un usuario por su username.
     *
     * @param username Username del usuario
     * @return Optional con el usuario si existe
     */
    Optional<Usuario> findByUsername(String username);

    /**
     * Busca un usuario por su email.
     *
     * @param email Email del usuario
     * @return Optional con el usuario si existe
     */
    Optional<Usuario> findByEmail(String email);

    /**
     * Busca usuarios por rol.
     *
     * @param rol Rol del usuario
     * @return Lista de usuarios con ese rol
     */
    List<Usuario> findByRol(RolUsuario rol);

    /**
     * Busca usuarios activos.
     *
     * @param estado Estado del usuario (true = activo)
     * @return Lista de usuarios activos
     */
    List<Usuario> findByEstado(Boolean estado);

    /**
     * Busca usuarios por rol y estado.
     *
     * @param rol Rol del usuario
     * @param estado Estado del usuario
     * @return Lista de usuarios
     */
    List<Usuario> findByRolAndEstado(RolUsuario rol, Boolean estado);

    /**
     * Busca usuarios bloqueados.
     *
     * @param bloqueado Estado de bloqueo
     * @return Lista de usuarios bloqueados
     */
    List<Usuario> findByBloqueado(Boolean bloqueado);

    /**
     * Verifica si existe un usuario con el username dado.
     *
     * @param username Username a verificar
     * @return true si existe
     */
    boolean existsByUsername(String username);

    /**
     * Verifica si existe un usuario con el email dado.
     *
     * @param email Email a verificar
     * @return true si existe
     */
    boolean existsByEmail(String email);

    // ===================================================================
    // CONSULTAS PERSONALIZADAS CON @Query
    // ===================================================================

    /**
     * Busca usuarios activos y no bloqueados.
     *
     * @return Lista de usuarios disponibles
     */
    @Query("SELECT u FROM Usuario u WHERE u.estado = true AND u.bloqueado = false")
    List<Usuario> findUsuariosDisponibles();

    /**
     * Busca usuarios por rol que estén activos.
     *
     * @param rol Rol del usuario
     * @return Lista de usuarios
     */
    @Query("SELECT u FROM Usuario u WHERE u.rol = :rol AND u.estado = true")
    List<Usuario> findActiveUsersByRole(@Param("rol") RolUsuario rol);

    /**
     * Busca usuarios con intentos fallidos de login mayores a un valor.
     *
     * @param intentos Número de intentos
     * @return Lista de usuarios
     */
    @Query("SELECT u FROM Usuario u WHERE u.intentosFallidos >= :intentos AND u.estado = true")
    List<Usuario> findUsuariosConIntentosAltos(@Param("intentos") Integer intentos);

    /**
     * Busca usuarios bloqueados temporalmente (fecha de bloqueo no expirada).
     *
     * @param ahora Fecha y hora actual
     * @return Lista de usuarios bloqueados temporalmente
     */
    @Query("SELECT u FROM Usuario u WHERE u.bloqueado = true AND u.fechaBloqueo > :ahora")
    List<Usuario> findUsuariosBloqueadosTemporalmente(@Param("ahora") LocalDateTime ahora);

    /**
     * Busca usuarios cuyo bloqueo ha expirado.
     *
     * @param ahora Fecha y hora actual
     * @return Lista de usuarios con bloqueo expirado
     */
    @Query("SELECT u FROM Usuario u WHERE u.bloqueado = true AND u.fechaBloqueo < :ahora")
    List<Usuario> findUsuariosConBloqueoExpirado(@Param("ahora") LocalDateTime ahora);

    /**
     * Busca usuarios que no han iniciado sesión desde una fecha determinada.
     *
     * @param fecha Fecha límite
     * @return Lista de usuarios inactivos
     */
    @Query("SELECT u FROM Usuario u WHERE u.ultimoLogin < :fecha OR u.ultimoLogin IS NULL")
    List<Usuario> findUsuariosInactivosDesde(@Param("fecha") LocalDateTime fecha);

    /**
     * Busca usuarios creados en un rango de fechas.
     *
     * @param inicio Fecha de inicio
     * @param fin Fecha de fin
     * @return Lista de usuarios
     */
    @Query("SELECT u FROM Usuario u WHERE u.fechaCreacion BETWEEN :inicio AND :fin")
    List<Usuario> findUsuariosCreadosEnRango(@Param("inicio") LocalDateTime inicio,
                                               @Param("fin") LocalDateTime fin);

    /**
     * Cuenta usuarios por rol.
     *
     * @param rol Rol a contar
     * @return Número de usuarios con ese rol
     */
    @Query("SELECT COUNT(u) FROM Usuario u WHERE u.rol = :rol")
    long countByRol(@Param("rol") RolUsuario rol);

    /**
     * Cuenta usuarios activos.
     *
     * @return Número de usuarios activos
     */
    @Query("SELECT COUNT(u) FROM Usuario u WHERE u.estado = true")
    long countUsuariosActivos();

    /**
     * Busca usuarios por username o email (búsqueda parcial).
     *
     * @param busqueda Término de búsqueda
     * @return Lista de usuarios
     */
    @Query("SELECT u FROM Usuario u WHERE " +
           "LOWER(u.username) LIKE LOWER(CONCAT('%', :busqueda, '%')) OR " +
           "LOWER(u.email) LIKE LOWER(CONCAT('%', :busqueda, '%'))")
    List<Usuario> buscarUsuarios(@Param("busqueda") String busqueda);

    /**
     * Obtiene usuarios ordenados por último login (más recientes primero).
     *
     * @return Lista de usuarios ordenada
     */
    @Query("SELECT u FROM Usuario u WHERE u.ultimoLogin IS NOT NULL " +
           "ORDER BY u.ultimoLogin DESC")
    List<Usuario> findUsuariosOrdenadosPorUltimoLogin();

    /**
     * Busca usuarios que iniciaron sesión hoy.
     *
     * @param inicio Inicio del día
     * @param fin Fin del día
     * @return Lista de usuarios
     */
    @Query("SELECT u FROM Usuario u WHERE u.ultimoLogin BETWEEN :inicio AND :fin")
    List<Usuario> findUsuariosLogueadosHoy(@Param("inicio") LocalDateTime inicio,
                                            @Param("fin") LocalDateTime fin);
}
