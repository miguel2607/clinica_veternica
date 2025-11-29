package com.veterinaria.clinica_veternica.repository;

import com.veterinaria.clinica_veternica.domain.agenda.Cita;
import com.veterinaria.clinica_veternica.domain.paciente.Mascota;
import com.veterinaria.clinica_veternica.domain.usuario.Veterinario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repositorio para la entidad Cita.
 *
 * Proporciona operaciones CRUD y consultas personalizadas para
 * la gestión de citas médicas.
 *
 * @author Clínica Veterinaria Team
 * @version 1.0
 * @since 2025-11-02
 */
@Repository
public interface CitaRepository extends JpaRepository<Cita, Long> {

    // ===================================================================
    // CONSULTAS DERIVADAS
    // ===================================================================

    /**
     * Busca citas por veterinario.
     *
     * @param veterinario Veterinario
     * @return Lista de citas
     */
    List<Cita> findByVeterinario(Veterinario veterinario);

    /**
     * Busca citas por veterinario cargando las relaciones necesarias (mascota, propietario, servicio).
     *
     * @param veterinario Veterinario
     * @return Lista de citas con relaciones cargadas
     */
    @Query("SELECT DISTINCT c FROM Cita c " +
           "LEFT JOIN FETCH c.mascota m " +
           "LEFT JOIN FETCH m.propietario p " +
           "LEFT JOIN FETCH c.servicio s " +
           "WHERE c.veterinario = :veterinario " +
           "ORDER BY c.fechaCita, c.horaCita")
    List<Cita> findByVeterinarioWithRelations(@Param("veterinario") Veterinario veterinario);

    /**
     * Busca citas por mascota.
     *
     * @param mascota Mascota
     * @return Lista de citas
     */
    List<Cita> findByMascota(Mascota mascota);

    /**
     * Busca citas por estado.
     *
     * @param estado Estado de la cita
     * @return Lista de citas
     */
    List<Cita> findByEstado(String estado);

    // ===================================================================
    // CONSULTAS PERSONALIZADAS CON @Query
    // ===================================================================

    /**
     * Busca citas por veterinario y fecha.
     *
     * @param veterinario Veterinario
     * @param inicio Fecha de inicio
     * @param fin Fecha de fin
     * @return Lista de citas
     */
    @Query("SELECT c FROM Cita c WHERE c.veterinario = :veterinario " +
           "AND FUNCTION('TIMESTAMP', c.fechaCita, c.horaCita) BETWEEN :inicio AND :fin " +
           "ORDER BY c.fechaCita, c.horaCita")
    List<Cita> findCitasPorVeterinarioYFecha(@Param("veterinario") Veterinario veterinario,
                                              @Param("inicio") LocalDateTime inicio,
                                              @Param("fin") LocalDateTime fin);

    /**
     * Busca citas por mascota y ordenadas por fecha.
     *
     * @param mascota Mascota
     * @return Lista de citas ordenada
     */
    @Query("SELECT c FROM Cita c WHERE c.mascota = :mascota ORDER BY c.fechaCita DESC, c.horaCita DESC")
    List<Cita> findCitasPorMascotaOrdenadas(@Param("mascota") Mascota mascota);

    /**
     * Busca citas programadas (pendientes y confirmadas).
     *
     * @return Lista de citas programadas
     */
    @Query("SELECT c FROM Cita c WHERE c.estado IN ('PROGRAMADA', 'CONFIRMADA') " +
           "AND FUNCTION('TIMESTAMP', c.fechaCita, c.horaCita) > CURRENT_TIMESTAMP " +
           "ORDER BY c.fechaCita, c.horaCita")
    List<Cita> findCitasProgramadas();

    /**
     * Busca citas del día para un veterinario.
     *
     * @param veterinario Veterinario
     * @param inicio Inicio del día
     * @param fin Fin del día
     * @return Lista de citas
     */
    @Query("SELECT c FROM Cita c WHERE c.veterinario = :veterinario " +
           "AND FUNCTION('TIMESTAMP', c.fechaCita, c.horaCita) BETWEEN :inicio AND :fin " +
           "ORDER BY c.fechaCita, c.horaCita")
    List<Cita> findCitasDelDia(@Param("veterinario") Veterinario veterinario,
                                @Param("inicio") LocalDateTime inicio,
                                @Param("fin") LocalDateTime fin);

    /**
     * Busca citas en un rango de fechas.
     *
     * @param inicio Fecha de inicio
     * @param fin Fecha de fin
     * @return Lista de citas
     */
    @Query("SELECT c FROM Cita c WHERE FUNCTION('TIMESTAMP', c.fechaCita, c.horaCita) BETWEEN :inicio AND :fin " +
           "ORDER BY c.fechaCita, c.horaCita")
    List<Cita> findCitasEnRango(@Param("inicio") LocalDateTime inicio,
                                 @Param("fin") LocalDateTime fin);

    /**
     * Busca citas confirmadas pendientes de atención.
     *
     * @return Lista de citas
     */
    @Query("SELECT c FROM Cita c WHERE c.estado = 'CONFIRMADA' " +
           "AND FUNCTION('TIMESTAMP', c.fechaCita, c.horaCita) <= CURRENT_TIMESTAMP " +
           "AND FUNCTION('TIMESTAMP', c.fechaCita, c.horaCita) >= :hace2Horas " +
           "ORDER BY c.fechaCita, c.horaCita")
    List<Cita> findCitasPendientesAtencion(@Param("hace2Horas") LocalDateTime hace2Horas);

    /**
     * Cuenta citas por estado.
     *
     * @param estado Estado
     * @return Número de citas
     */
    @Query("SELECT COUNT(c) FROM Cita c WHERE c.estado = :estado")
    long countByEstado(@Param("estado") String estado);

    /**
     * Busca citas canceladas en un rango de fechas.
     *
     * @param inicio Fecha inicio
     * @param fin Fecha fin
     * @return Lista de citas canceladas
     */
    @Query("SELECT c FROM Cita c WHERE c.estado = 'CANCELADA' " +
           "AND FUNCTION('TIMESTAMP', c.fechaCita, c.horaCita) BETWEEN :inicio AND :fin")
    List<Cita> findCitasCanceladasEnRango(@Param("inicio") LocalDateTime inicio,
                                           @Param("fin") LocalDateTime fin);

    /**
     * Busca próximas citas para recordatorios (próximas 24 horas).
     * 
     * NOTA: El control de recordatorios enviados ahora se maneja en la tabla 'comunicaciones'.
     * Esta query devuelve todas las citas elegibles; el servicio debe verificar si ya tienen
     * recordatorios programados en la tabla de comunicaciones.
     *
     * @param ahora Fecha y hora actual
     * @param limite Fecha y hora límite (24 horas después)
     * @return Lista de citas
     */
    @Query("SELECT c FROM Cita c WHERE c.estado IN ('PROGRAMADA', 'CONFIRMADA') " +
           "AND FUNCTION('TIMESTAMP', c.fechaCita, c.horaCita) BETWEEN :ahora AND :limite")
    List<Cita> findCitasParaRecordatorio(@Param("ahora") LocalDateTime ahora,
                                          @Param("limite") LocalDateTime limite);

    /**
     * Busca citas atendidas por veterinario en un rango.
     *
     * @param veterinario Veterinario
     * @param inicio Fecha inicio
     * @param fin Fecha fin
     * @return Lista de citas
     */
    @Query("SELECT c FROM Cita c WHERE c.veterinario = :veterinario " +
           "AND c.estado = 'ATENDIDA' AND FUNCTION('TIMESTAMP', c.fechaCita, c.horaCita) BETWEEN :inicio AND :fin")
    List<Cita> findCitasAtendidasPorVeterinario(@Param("veterinario") Veterinario veterinario,
                                                  @Param("inicio") LocalDateTime inicio,
                                                  @Param("fin") LocalDateTime fin);

    /**
     * Cuenta citas activas (no canceladas ni no asistidas) para un veterinario en una hora específica.
     * Se considera el tiempo estimado de duración de las citas para detectar solapamientos.
     *
     * @param veterinario Veterinario
     * @param fechaCita Fecha de la cita
     * @param horaCita Hora de la cita
     * @param duracionMinutos Duración estimada en minutos
     * @param idCitaExcluir ID de cita a excluir (para actualizaciones, puede ser null)
     * @return Cantidad de citas que se solapan
     */
    @Query("SELECT COUNT(c) FROM Cita c WHERE c.veterinario = :veterinario " +
           "AND c.fechaCita = :fechaCita " +
           "AND c.estado NOT IN ('CANCELADA', 'NO_ASISTIO') " +
           "AND (:idCitaExcluir IS NULL OR c.idCita != :idCitaExcluir) " +
           "AND (" +
           "  (c.horaCita <= :horaCita AND FUNCTION('ADDTIME', c.horaCita, FUNCTION('SEC_TO_TIME', COALESCE(c.duracionEstimadaMinutos, 30) * 60)) > :horaCita) " +
           "  OR (c.horaCita < FUNCTION('ADDTIME', :horaCita, FUNCTION('SEC_TO_TIME', :duracionMinutos * 60)) AND c.horaCita >= :horaCita)" +
           ")")
    long countCitasConflictivas(@Param("veterinario") Veterinario veterinario,
                                 @Param("fechaCita") java.time.LocalDate fechaCita,
                                 @Param("horaCita") java.time.LocalTime horaCita,
                                 @Param("duracionMinutos") Integer duracionMinutos,
                                 @Param("idCitaExcluir") Long idCitaExcluir);

    /**
     * Busca citas activas (no canceladas ni no asistidas) que se solapan con el rango de tiempo especificado.
     *
     * @param veterinario Veterinario
     * @param fechaCita Fecha de la cita
     * @param horaInicio Hora de inicio del rango
     * @param horaFin Hora de fin del rango
     * @param idCitaExcluir ID de cita a excluir (puede ser null)
     * @return Lista de citas que se solapan
     */
    @Query("SELECT c FROM Cita c WHERE c.veterinario = :veterinario " +
           "AND c.fechaCita = :fechaCita " +
           "AND c.estado NOT IN ('CANCELADA', 'NO_ASISTIO') " +
           "AND (:idCitaExcluir IS NULL OR c.idCita != :idCitaExcluir) " +
           "AND c.horaCita < :horaFin " +
           "AND FUNCTION('ADDTIME', c.horaCita, FUNCTION('SEC_TO_TIME', COALESCE(c.duracionEstimadaMinutos, 30) * 60)) > :horaInicio")
    List<Cita> findCitasSolapadas(@Param("veterinario") Veterinario veterinario,
                                   @Param("fechaCita") java.time.LocalDate fechaCita,
                                   @Param("horaInicio") java.time.LocalTime horaInicio,
                                   @Param("horaFin") java.time.LocalTime horaFin,
                                   @Param("idCitaExcluir") Long idCitaExcluir);
}
