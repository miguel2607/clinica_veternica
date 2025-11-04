package com.veterinaria.clinica_veternica.repository;

import com.veterinaria.clinica_veternica.domain.practica.Bitacora;
import com.veterinaria.clinica_veternica.domain.practica.Estudiante;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * Repositorio para la entidad Bitacora.
 *
 * @author Clínica Veterinaria Team
 * @version 1.0
 * @since 2025-11-02
 */
@Repository
public interface BitacoraRepository extends JpaRepository<Bitacora, Long> {

    List<Bitacora> findByEstudiante(Estudiante estudiante);

    @Query("SELECT b FROM Bitacora b WHERE b.estudiante = :estudiante ORDER BY b.fecha DESC")
    List<Bitacora> findBitacorasPorEstudianteOrdenadas(@Param("estudiante") Estudiante estudiante);

    @Query("SELECT b FROM Bitacora b WHERE b.fecha BETWEEN :inicio AND :fin ORDER BY b.fecha")
    List<Bitacora> findBitacorasEnRango(@Param("inicio") LocalDate inicio, @Param("fin") LocalDate fin);

    @Query("SELECT b FROM Bitacora b WHERE b.estudiante = :estudiante AND b.fecha = :fecha")
    List<Bitacora> findBitacorasPorEstudianteYFecha(@Param("estudiante") Estudiante estudiante, @Param("fecha") LocalDate fecha);
}
