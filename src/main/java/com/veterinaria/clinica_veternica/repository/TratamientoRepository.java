package com.veterinaria.clinica_veternica.repository;

import com.veterinaria.clinica_veternica.domain.clinico.Tratamiento;
import com.veterinaria.clinica_veternica.domain.clinico.HistoriaClinica;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio para la entidad Tratamiento.
 *
 * @author Clínica Veterinaria Team
 * @version 1.0
 * @since 2025-11-02
 */
@Repository
public interface TratamientoRepository extends JpaRepository<Tratamiento, Long> {

    List<Tratamiento> findByHistoriaClinica(HistoriaClinica historiaClinica);

    List<Tratamiento> findByActivo(Boolean activo);

    List<Tratamiento> findByEstado(String estado);

    @Query("SELECT t FROM Tratamiento t WHERE t.historiaClinica = :historia " +
           "AND t.activo = true ORDER BY t.fechaInicio DESC")
    List<Tratamiento> findTratamientosActivosPorHistoria(@Param("historia") HistoriaClinica historia);

    @Query("SELECT t FROM Tratamiento t WHERE t.estado = 'ACTIVO' AND t.activo = true")
    List<Tratamiento> findTratamientosActivos();

    @Query("SELECT t FROM Tratamiento t WHERE t.historiaClinica.mascota.idMascota = :idMascota")
    List<Tratamiento> findByMascotaId(@Param("idMascota") Long idMascota);
}
