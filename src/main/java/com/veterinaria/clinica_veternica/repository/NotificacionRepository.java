package com.veterinaria.clinica_veternica.repository;

import com.veterinaria.clinica_veternica.domain.comunicacion.Notificacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repositorio para la entidad Notificacion.
 *
 * @author Clínica Veterinaria Team
 * @version 1.0
 * @since 2025-11-02
 */
@Repository
public interface NotificacionRepository extends JpaRepository<Notificacion, Long> {

    List<Notificacion> findByTipo(String tipo);

    List<Notificacion> findByCanal(String canal);

    List<Notificacion> findByEnviada(Boolean enviada);

    @Query("SELECT n FROM Notificacion n WHERE n.destinatarioEmail = :email ORDER BY n.fechaCreacion DESC")
    List<Notificacion> findNotificacionesPorEmail(@Param("email") String email);

    @Query("SELECT n FROM Notificacion n WHERE n.enviada = false AND n.intentosEnvio < n.maxIntentos")
    List<Notificacion> findNotificacionesPendientes();

    @Query("SELECT n FROM Notificacion n WHERE n.enviada = true AND n.leida = false")
    List<Notificacion> findNotificacionesNoLeidas();

    @Query("SELECT n FROM Notificacion n WHERE n.fechaCreacion BETWEEN :inicio AND :fin ORDER BY n.fechaCreacion DESC")
    List<Notificacion> findNotificacionesEnRango(@Param("inicio") LocalDateTime inicio, @Param("fin") LocalDateTime fin);

    @Query("SELECT n FROM Notificacion n WHERE n.prioridad IN ('ALTA', 'URGENTE') AND n.enviada = false")
    List<Notificacion> findNotificacionesAltaPrioridad();
}
