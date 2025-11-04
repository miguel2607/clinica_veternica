package com.veterinaria.clinica_veternica.repository;

import com.veterinaria.clinica_veternica.domain.clinico.EvolucionClinica;
import com.veterinaria.clinica_veternica.domain.clinico.HistoriaClinica;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repositorio para la entidad EvolucionClinica.
 *
 * @author Clínica Veterinaria Team
 * @version 1.0
 * @since 2025-11-02
 */
@Repository
public interface EvolucionClinicaRepository extends JpaRepository<EvolucionClinica, Long> {

    List<EvolucionClinica> findByHistoriaClinica(HistoriaClinica historiaClinica);

    List<EvolucionClinica> findByActivo(Boolean activo);

    @Query("SELECT e FROM EvolucionClinica e WHERE e.historiaClinica = :historia " +
           "AND e.activo = true ORDER BY e.fechaEvolucion DESC")
    List<EvolucionClinica> findByHistoriaOrdenadas(@Param("historia") HistoriaClinica historia);

    @Query("SELECT e FROM EvolucionClinica e WHERE e.fechaEvolucion BETWEEN :inicio AND :fin")
    List<EvolucionClinica> findEnRangoFecha(@Param("inicio") LocalDateTime inicio,
                                             @Param("fin") LocalDateTime fin);
}
