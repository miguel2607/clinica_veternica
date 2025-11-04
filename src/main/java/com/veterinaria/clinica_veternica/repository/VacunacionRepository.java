package com.veterinaria.clinica_veternica.repository;

import com.veterinaria.clinica_veternica.domain.clinico.Vacunacion;
import com.veterinaria.clinica_veternica.domain.clinico.HistoriaClinica;
import com.veterinaria.clinica_veternica.domain.paciente.Mascota;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * Repositorio para la entidad Vacunacion.
 *
 * @author Clínica Veterinaria Team
 * @version 1.0
 * @since 2025-11-02
 */
@Repository
public interface VacunacionRepository extends JpaRepository<Vacunacion, Long> {

    List<Vacunacion> findByHistoriaClinica(HistoriaClinica historiaClinica);

    List<Vacunacion> findByMascota(Mascota mascota);

    List<Vacunacion> findByVigente(Boolean vigente);

    @Query("SELECT v FROM Vacunacion v WHERE v.mascota = :mascota " +
           "AND v.vigente = true ORDER BY v.fechaAplicacion DESC")
    List<Vacunacion> findVacunacionesVigentesPorMascota(@Param("mascota") Mascota mascota);

    @Query("SELECT v FROM Vacunacion v WHERE v.fechaProximaDosis BETWEEN :inicio AND :fin " +
           "AND v.esquemaCompleto = false")
    List<Vacunacion> findVacunacionesConProximaDosis(@Param("inicio") LocalDate inicio,
                                                       @Param("fin") LocalDate fin);

    @Query("SELECT v FROM Vacunacion v WHERE v.nombreVacuna LIKE %:nombre%")
    List<Vacunacion> buscarPorNombreVacuna(@Param("nombre") String nombre);
}
