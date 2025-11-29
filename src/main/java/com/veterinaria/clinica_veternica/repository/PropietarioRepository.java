package com.veterinaria.clinica_veternica.repository;

import com.veterinaria.clinica_veternica.domain.paciente.Propietario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para la entidad Propietario.
 *
 * Proporciona operaciones CRUD y consultas personalizadas para
 * la gestión de propietarios de mascotas.
 *
 * @author Clínica Veterinaria Team
 * @version 1.0
 * @since 2025-11-02
 */
@Repository
public interface PropietarioRepository extends JpaRepository<Propietario, Long> {

    // ===================================================================
    // CONSULTAS DERIVADAS
    // ===================================================================

    /**
     * Busca un propietario por documento de identidad.
     *
     * @param documento Documento del propietario
     * @return Optional con el propietario si existe
     */
    Optional<Propietario> findByDocumento(String documento);

    /**
     * Busca un propietario por email (case-insensitive).
     *
     * @param email Email del propietario
     * @return Optional con el propietario si existe
     */
    @Query("SELECT p FROM Propietario p WHERE LOWER(p.email) = LOWER(:email)")
    Optional<Propietario> findByEmail(@Param("email") String email);

    /**
     * Busca propietarios por teléfono.
     *
     * @param telefono Teléfono del propietario
     * @return Lista de propietarios
     */
    List<Propietario> findByTelefono(String telefono);

    /**
     * Busca propietarios activos.
     *
     * @param activo Estado activo
     * @return Lista de propietarios activos
     */
    List<Propietario> findByActivo(Boolean activo);

    /**
     * Busca propietarios activos (query derivado).
     *
     * @return Lista de propietarios activos
     */
    List<Propietario> findByActivoTrue();

    /**
     * Busca un propietario por tipo y número de documento.
     *
     * @param tipoDocumento Tipo de documento
     * @param documento Número de documento
     * @return Optional con el propietario si existe
     */
    @Query("SELECT p FROM Propietario p WHERE p.tipoDocumento = :tipoDocumento AND p.documento = :documento")
    Optional<Propietario> findByTipoDocumentoAndNumeroDocumento(@Param("tipoDocumento") String tipoDocumento, @Param("documento") String documento);

    /**
     * Busca propietarios por teléfono que contenga el texto.
     *
     * @param telefono Teléfono a buscar
     * @return Lista de propietarios
     */
    List<Propietario> findByTelefonoContaining(String telefono);

    /**
     * Verifica si existe un propietario con el documento dado.
     *
     * @param documento Documento a verificar
     * @return true si existe
     */
    boolean existsByDocumento(String documento);

    /**
     * Verifica si existe un propietario con tipo y número de documento.
     *
     * @param tipoDocumento Tipo de documento
     * @param documento Número de documento
     * @return true si existe
     */
    @Query("SELECT COUNT(p) > 0 FROM Propietario p WHERE p.tipoDocumento = :tipoDocumento AND p.documento = :documento")
    boolean existsByTipoDocumentoAndNumeroDocumento(@Param("tipoDocumento") String tipoDocumento, @Param("documento") String documento);

    /**
     * Verifica si existe un propietario con el email dado.
     *
     * @param email Email a verificar
     * @return true si existe
     */
    boolean existsByEmail(String email);

    /**
     * Verifica si existe un propietario asociado a un usuario específico.
     *
     * @param idUsuario ID del usuario a verificar
     * @return true si existe
     */
    @Query("SELECT COUNT(p) > 0 FROM Propietario p WHERE p.usuario.idUsuario = :idUsuario")
    boolean existsByUsuarioId(@Param("idUsuario") Long idUsuario);

    // ===================================================================
    // CONSULTAS PERSONALIZADAS CON @Query
    // ===================================================================

    /**
     * Busca propietarios por nombre o apellido (búsqueda parcial).
     *
     * @param busqueda Término de búsqueda
     * @return Lista de propietarios
     */
    @Query("SELECT p FROM Propietario p WHERE " +
           "LOWER(p.nombres) LIKE LOWER(CONCAT('%', :busqueda, '%')) OR " +
           "LOWER(p.apellidos) LIKE LOWER(CONCAT('%', :busqueda, '%'))")
    List<Propietario> buscarPropietarios(@Param("busqueda") String busqueda);

    /**
     * Busca propietarios por nombre completo (alias).
     *
     * @param nombre Término de búsqueda
     * @return Lista de propietarios
     */
    @Query("SELECT p FROM Propietario p WHERE " +
           "LOWER(p.nombres) LIKE LOWER(CONCAT('%', :nombre, '%')) OR " +
           "LOWER(p.apellidos) LIKE LOWER(CONCAT('%', :nombre, '%'))")
    List<Propietario> buscarPorNombreCompleto(@Param("nombre") String nombre);

    /**
     * Busca propietarios activos con mascotas.
     *
     * @return Lista de propietarios
     */
    @Query("SELECT DISTINCT p FROM Propietario p LEFT JOIN FETCH p.mascotas WHERE p.activo = true")
    List<Propietario> findPropietariosActivosConMascotas();

    /**
     * Busca propietarios que tienen más de X mascotas.
     *
     * @param cantidad Cantidad mínima de mascotas
     * @return Lista de propietarios
     */
    @Query("SELECT p FROM Propietario p WHERE SIZE(p.mascotas) > :cantidad")
    List<Propietario> findPropietariosConMasDeMascotas(@Param("cantidad") int cantidad);

    /**
     * Busca propietarios sin mascotas activas.
     *
     * @return Lista de propietarios
     */
    @Query("SELECT p FROM Propietario p WHERE p.activo = true AND " +
           "(SIZE(p.mascotas) = 0 OR NOT EXISTS (SELECT m FROM Mascota m WHERE m.propietario = p AND m.activo = true))")
    List<Propietario> findPropietariosSinMascotasActivas();

    /**
     * Cuenta propietarios activos.
     *
     * @return Número de propietarios activos
     */
    @Query("SELECT COUNT(p) FROM Propietario p WHERE p.activo = true")
    long countPropietariosActivos();

    /**
     * Busca propietarios por ciudad.
     * NOTA: La ciudad ahora está incluida en el campo 'direccion'.
     * Esta query busca propietarios cuya dirección contenga el texto de la ciudad.
     *
     * @param ciudad Ciudad a buscar en la dirección
     * @return Lista de propietarios
     */
    @Query("SELECT p FROM Propietario p WHERE LOWER(p.direccion) LIKE LOWER(CONCAT('%', :ciudad, '%')) AND p.activo = true")
    List<Propietario> findPropietariosPorCiudad(@Param("ciudad") String ciudad);

    /**
     * Busca propietarios ordenados por número de mascotas (descendente).
     *
     * @return Lista de propietarios ordenada
     */
    @Query("SELECT p FROM Propietario p WHERE p.activo = true ORDER BY SIZE(p.mascotas) DESC")
    List<Propietario> findPropietariosOrdenadosPorNumeroMascotas();

    /**
     * Busca propietarios activos.
     *
     * @return Lista de propietarios activos
     */
    @Query("SELECT p FROM Propietario p WHERE p.activo = true")
    List<Propietario> findPropietariosActivos();
}
