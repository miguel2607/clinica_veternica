package com.veterinaria.clinica_veternica.repository;

import com.veterinaria.clinica_veternica.domain.clinico.HistoriaClinica;
import com.veterinaria.clinica_veternica.domain.paciente.Mascota;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para la entidad HistoriaClinica.
 *
 * @author Clínica Veterinaria Team
 * @version 1.0
 * @since 2025-11-02
 */
@Repository
public interface HistoriaClinicaRepository extends JpaRepository<HistoriaClinica, Long> {

    Optional<HistoriaClinica> findByMascota(Mascota mascota);

    List<HistoriaClinica> findByActivo(Boolean activo);

    @Query("SELECT h FROM HistoriaClinica h WHERE h.mascota.idMascota = :idMascota")
    Optional<HistoriaClinica> findByMascotaId(@Param("idMascota") Long idMascota);

    @Query("SELECT h FROM HistoriaClinica h WHERE h.activo = true")
    List<HistoriaClinica> findHistoriasActivas();

    @Query("SELECT COUNT(h) FROM HistoriaClinica h WHERE h.activo = true")
    long countHistoriasActivas();
}
