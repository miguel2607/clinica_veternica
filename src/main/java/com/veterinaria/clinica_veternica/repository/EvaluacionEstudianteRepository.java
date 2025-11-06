package com.veterinaria.clinica_veternica.repository;

import com.veterinaria.clinica_veternica.domain.practica.EvaluacionEstudiante;
import com.veterinaria.clinica_veternica.domain.practica.Estudiante;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * Repositorio para la entidad EvaluacionEstudiante.
 *
 * @author Clínica Veterinaria Team
 * @version 1.0
 * @since 2025-11-02
 */
@Repository
public interface EvaluacionEstudianteRepository extends JpaRepository<EvaluacionEstudiante, Long> {

    List<EvaluacionEstudiante> findByEstudiante(Estudiante estudiante);

    @Query("SELECT e FROM EvaluacionEstudiante e WHERE e.estudiante = :estudiante ORDER BY e.fechaEvaluacion DESC")
    List<EvaluacionEstudiante> findEvaluacionesPorEstudianteOrdenadas(@Param("estudiante") Estudiante estudiante);

    @Query("SELECT e FROM EvaluacionEstudiante e WHERE e.fechaEvaluacion BETWEEN :inicio AND :fin")
    List<EvaluacionEstudiante> findEvaluacionesEnRango(@Param("inicio") LocalDate inicio, @Param("fin") LocalDate fin);

    @Query("SELECT AVG(e.calificacion) FROM EvaluacionEstudiante e WHERE e.estudiante = :estudiante")
    Double getPromedioCalificaciones(@Param("estudiante") Estudiante estudiante);
}
