package com.veterinaria.clinica_veternica.repository;

import com.veterinaria.clinica_veternica.domain.clinico.RecetaMedica;
import com.veterinaria.clinica_veternica.domain.clinico.HistoriaClinica;
import com.veterinaria.clinica_veternica.domain.paciente.Mascota;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para la entidad RecetaMedica.
 *
 * @author Clínica Veterinaria Team
 * @version 1.0
 * @since 2025-11-02
 */
@Repository
public interface RecetaMedicaRepository extends JpaRepository<RecetaMedica, Long> {

    Optional<RecetaMedica> findByNumeroReceta(String numeroReceta);

    List<RecetaMedica> findByHistoriaClinica(HistoriaClinica historiaClinica);

    List<RecetaMedica> findByMascota(Mascota mascota);

    List<RecetaMedica> findByVigente(Boolean vigente);

    @Query("SELECT r FROM RecetaMedica r WHERE r.mascota = :mascota " +
           "AND r.vigente = true ORDER BY r.fechaEmision DESC")
    List<RecetaMedica> findRecetasVigentesPorMascota(@Param("mascota") Mascota mascota);

    @Query("SELECT r FROM RecetaMedica r WHERE r.vigente = true AND r.fechaVigencia < CURRENT_DATE")
    List<RecetaMedica> findRecetasVencidas();

    boolean existsByNumeroReceta(String numeroReceta);
}
