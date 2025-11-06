package com.veterinaria.clinica_veternica.repository;

import com.veterinaria.clinica_veternica.domain.practica.Estudiante;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para la entidad Estudiante.
 *
 * @author Clínica Veterinaria Team
 * @version 1.0
 * @since 2025-11-02
 */
@Repository
public interface EstudianteRepository extends JpaRepository<Estudiante, Long> {

    Optional<Estudiante> findByCodigoEstudiantil(String codigoEstudiantil);

    List<Estudiante> findByUniversidad(String universidad);

    List<Estudiante> findByActivo(Boolean activo);

    @Query("SELECT e FROM Estudiante e WHERE e.activo = true ORDER BY e.apellidos, e.nombres")
    List<Estudiante> findEstudiantesActivos();

    @Query("SELECT e FROM Estudiante e WHERE e.universidad = :universidad AND e.activo = true")
    List<Estudiante> findEstudiantesActivosPorUniversidad(@Param("universidad") String universidad);

    @Query("SELECT e FROM Estudiante e WHERE e.horasPracticaCompletadas >= :horas")
    List<Estudiante> findEstudiantesPorHorasMinimas(@Param("horas") Integer horas);

    boolean existsByCodigoEstudiantil(String codigoEstudiantil);
}
