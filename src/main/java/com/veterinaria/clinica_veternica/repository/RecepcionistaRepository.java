package com.veterinaria.clinica_veternica.repository;

import com.veterinaria.clinica_veternica.domain.usuario.Recepcionista;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio para la entidad Recepcionista.
 *
 * @author Clínica Veterinaria Team
 * @version 1.0
 * @since 2025-11-02
 */
@Repository
public interface RecepcionistaRepository extends JpaRepository<Recepcionista, Long> {

    /**
     * Busca recepcionistas activos.
     *
     * @param estado Estado del recepcionista
     * @return Lista de recepcionistas activos
     */
    List<Recepcionista> findByEstado(Boolean estado);

    /**
     * Busca recepcionistas por turno.
     *
     * @param turno Turno del recepcionista
     * @return Lista de recepcionistas
     */
    List<Recepcionista> findByTurno(String turno);

    /**
     * Busca todos los recepcionistas activos.
     *
     * @return Lista de recepcionistas activos
     */
    @Query("SELECT r FROM Recepcionista r WHERE r.estado = true")
    List<Recepcionista> findRecepcionistasActivos();

    /**
     * Busca recepcionistas activos por turno.
     *
     * @param turno Turno
     * @return Lista de recepcionistas
     */
    @Query("SELECT r FROM Recepcionista r WHERE r.turno = :turno AND r.estado = true")
    List<Recepcionista> findRecepcionistasActivosPorTurno(String turno);

    /**
     * Cuenta recepcionistas activos.
     *
     * @return Número de recepcionistas activos
     */
    @Query("SELECT COUNT(r) FROM Recepcionista r WHERE r.estado = true")
    long countRecepcionistasActivos();
}
