package com.veterinaria.clinica_veternica.repository;

import com.veterinaria.clinica_veternica.domain.paciente.Mascota;
import com.veterinaria.clinica_veternica.domain.paciente.Propietario;
import com.veterinaria.clinica_veternica.domain.paciente.Especie;
import com.veterinaria.clinica_veternica.domain.paciente.Raza;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;


/**
 * Repositorio para la entidad Mascota.
 *
 * Proporciona operaciones CRUD y consultas personalizadas para
 * la gestión de mascotas (pacientes).
 *
 * @author Clínica Veterinaria Team
 * @version 1.0
 * @since 2025-11-02
 */
@Repository
public interface MascotaRepository extends JpaRepository<Mascota, Long> {

    // ===================================================================
    // CONSULTAS DERIVADAS
    // ===================================================================

    /**
     * Busca mascotas por propietario.
     *
     * @param propietario Propietario de las mascotas
     * @return Lista de mascotas
     */
    List<Mascota> findByPropietario(Propietario propietario);

    /**
     * Busca mascotas por especie.
     *
     * @param especie Especie de la mascota
     * @return Lista de mascotas
     */
    List<Mascota> findByEspecie(Especie especie);

    /**
     * Busca mascotas por raza.
     *
     * @param raza Raza de la mascota
     * @return Lista de mascotas
     */
    List<Mascota> findByRaza(Raza raza);

    /**
     * Busca mascotas activas.
     *
     * @param activo Estado activo
     * @return Lista de mascotas activas
     */
    List<Mascota> findByActivo(Boolean activo);

    /**
     * Busca mascotas por sexo.
     *
     * @param sexo Sexo de la mascota
     * @return Lista de mascotas
     */
    List<Mascota> findBySexo(String sexo);

    /**
     * Busca mascotas por nombre (coincidencia exacta).
     *
     * @param nombre Nombre de la mascota
     * @return Lista de mascotas
     */
    List<Mascota> findByNombre(String nombre);

    /**
     * Busca mascotas por nombre que contenga el texto (case insensitive).
     *
     * @param nombre Nombre a buscar
     * @return Lista de mascotas que coinciden
     */
    List<Mascota> findByNombreContainingIgnoreCase(String nombre);

    /**
     * Busca mascotas activas (query derivado).
     *
     * @return Lista de mascotas activas
     */
    List<Mascota> findByActivoTrue();

    /**
     * Busca mascotas por propietario ID.
     *
     * @param idPropietario ID del propietario
     * @return Lista de mascotas
     */
    @Query("SELECT m FROM Mascota m WHERE m.propietario.idPropietario = :idPropietario")
    List<Mascota> findByPropietarioId(@Param("idPropietario") Long idPropietario);

    /**
     * Busca mascotas por especie ID.
     *
     * @param idEspecie ID de la especie
     * @return Lista de mascotas
     */
    @Query("SELECT m FROM Mascota m WHERE m.especie.idEspecie = :idEspecie")
    List<Mascota> findByEspecieId(@Param("idEspecie") Long idEspecie);

    /**
     * Busca mascotas por raza ID.
     *
     * @param idRaza ID de la raza
     * @return Lista de mascotas
     */
    @Query("SELECT m FROM Mascota m WHERE m.raza.idRaza = :idRaza")
    List<Mascota> findByRazaId(@Param("idRaza") Long idRaza);

    /**
     * Verifica si existe una mascota con el nombre y propietario dados.
     *
     * @param nombre Nombre de la mascota
     * @param idPropietario ID del propietario
     * @return true si existe
     */
    @Query("SELECT COUNT(m) > 0 FROM Mascota m WHERE m.nombre = :nombre AND m.propietario.idPropietario = :idPropietario")
    boolean existsByNombreAndPropietarioId(@Param("nombre") String nombre, @Param("idPropietario") Long idPropietario);

    // ===================================================================
    // CONSULTAS PERSONALIZADAS CON @Query
    // ===================================================================

    /**
     * Busca mascotas activas por propietario.
     *
     * @param propietario Propietario
     * @return Lista de mascotas activas
     */
    @Query("SELECT m FROM Mascota m WHERE m.propietario = :propietario AND m.activo = true")
    List<Mascota> findMascotasActivasPorPropietario(@Param("propietario") Propietario propietario);

    /**
     * Busca mascotas por nombre (búsqueda parcial).
     *
     * @param nombre Término de búsqueda
     * @return Lista de mascotas
     */
    @Query("SELECT m FROM Mascota m WHERE LOWER(m.nombre) LIKE LOWER(CONCAT('%', :nombre, '%'))")
    List<Mascota> buscarPorNombre(@Param("nombre") String nombre);

    /**
     * Busca mascotas por especie y activas.
     *
     * @param especie Especie
     * @return Lista de mascotas
     */
    @Query("SELECT m FROM Mascota m WHERE m.especie = :especie AND m.activo = true")
    List<Mascota> findMascotasActivasPorEspecie(@Param("especie") Especie especie);

    /**
     * Busca mascotas por raza y activas.
     *
     * @param raza Raza
     * @return Lista de mascotas
     */
    @Query("SELECT m FROM Mascota m WHERE m.raza = :raza AND m.activo = true")
    List<Mascota> findMascotasActivasPorRaza(@Param("raza") Raza raza);

    /**
     * Busca mascotas esterilizadas.
     *
     * @return Lista de mascotas esterilizadas
     */
    @Query("SELECT m FROM Mascota m WHERE m.esterilizado = true AND m.activo = true")
    List<Mascota> findMascotasEsterilizadas();

    /**
     * Busca mascotas no esterilizadas.
     *
     * @return Lista de mascotas no esterilizadas
     */
    @Query("SELECT m FROM Mascota m WHERE m.esterilizado = false AND m.activo = true")
    List<Mascota> findMascotasNoEsterilizadas();

    /**
     * Busca mascotas que cumplen años en un mes específico.
     *
     * @param mes Mes del año (1-12)
     * @return Lista de mascotas
     */
    @Query("SELECT m FROM Mascota m WHERE MONTH(m.fechaNacimiento) = :mes AND m.activo = true")
    List<Mascota> findMascotasPorMesCumpleanios(@Param("mes") int mes);

    /**
     * Busca mascotas nacidas después de una fecha.
     *
     * @param fecha Fecha límite
     * @return Lista de mascotas
     */
    @Query("SELECT m FROM Mascota m WHERE m.fechaNacimiento > :fecha AND m.activo = true")
    List<Mascota> findMascotasNacidasDespuesDe(@Param("fecha") LocalDate fecha);

    /**
     * Cuenta mascotas activas.
     *
     * @return Número de mascotas activas
     */
    @Query("SELECT COUNT(m) FROM Mascota m WHERE m.activo = true")
    long countMascotasActivas();

    /**
     * Cuenta mascotas por especie.
     *
     * @param especie Especie
     * @return Número de mascotas
     */
    @Query("SELECT COUNT(m) FROM Mascota m WHERE m.especie = :especie AND m.activo = true")
    long countByEspecie(@Param("especie") Especie especie);

    /**
     * Busca mascotas con alergias registradas.
     * NOTA: Las alergias ahora se registran en HistoriaClinica, no en Mascota.
     * Esta query busca mascotas que tienen historia clínica con alergias registradas.
     *
     * @return Lista de mascotas con alergias
     */
    @Query("SELECT m FROM Mascota m JOIN m.historiaClinica h " +
           "WHERE h.alergias IS NOT NULL AND h.alergias <> '' AND m.activo = true")
    List<Mascota> findMascotasConAlergias();

    /**
     * Busca mascotas con enfermedades crónicas registradas.
     * NOTA: Las enfermedades crónicas ahora se registran en HistoriaClinica, no en Mascota.
     * Esta query busca mascotas que tienen historia clínica con enfermedades crónicas registradas.
     *
     * @return Lista de mascotas con enfermedades crónicas
     */
    @Query("SELECT m FROM Mascota m JOIN m.historiaClinica h " +
           "WHERE h.enfermedadesCronicas IS NOT NULL AND h.enfermedadesCronicas <> '' AND m.activo = true")
    List<Mascota> findMascotasConCondicionesMedicas();

    /**
     * Busca mascotas ordenadas por fecha de nacimiento (más jóvenes primero).
     *
     * @return Lista de mascotas ordenada
     */
    @Query("SELECT m FROM Mascota m WHERE m.activo = true ORDER BY m.fechaNacimiento DESC")
    List<Mascota> findMascotasOrdenadaPorEdad();
}
