package com.veterinaria.clinica_veternica.repository;

import com.veterinaria.clinica_veternica.domain.agenda.Horario;
import com.veterinaria.clinica_veternica.domain.usuario.Veterinario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio para la entidad Horario.
 *
 * @author Clínica Veterinaria Team
 * @version 1.0
 * @since 2025-11-02
 */
@Repository
public interface HorarioRepository extends JpaRepository<Horario, Long> {

    List<Horario> findByVeterinario(Veterinario veterinario);

    List<Horario> findByDiaSemana(String diaSemana);

    List<Horario> findByActivo(Boolean activo);

    @Query("SELECT h FROM Horario h WHERE h.veterinario = :veterinario AND h.activo = true")
    List<Horario> findHorariosActivosPorVeterinario(@Param("veterinario") Veterinario veterinario);

    @Query("SELECT h FROM Horario h WHERE h.diaSemana = :dia AND h.activo = true")
    List<Horario> findHorariosActivosPorDia(@Param("dia") String dia);
}
