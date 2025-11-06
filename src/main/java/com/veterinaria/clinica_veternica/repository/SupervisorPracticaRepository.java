package com.veterinaria.clinica_veternica.repository;

import com.veterinaria.clinica_veternica.domain.practica.SupervisorPractica;
import com.veterinaria.clinica_veternica.domain.usuario.Veterinario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para la entidad SupervisorPractica.
 *
 * @author Clínica Veterinaria Team
 * @version 1.0
 * @since 2025-11-02
 */
@Repository
public interface SupervisorPracticaRepository extends JpaRepository<SupervisorPractica, Long> {

    Optional<SupervisorPractica> findByVeterinario(Veterinario veterinario);

    List<SupervisorPractica> findByActivo(Boolean activo);

    @Query("SELECT s FROM SupervisorPractica s WHERE s.activo = true ORDER BY s.veterinario.apellidos")
    List<SupervisorPractica> findSupervisoresActivos();

    @Query("SELECT COUNT(s) FROM SupervisorPractica s WHERE s.activo = true")
    long countSupervisoresActivos();
}
