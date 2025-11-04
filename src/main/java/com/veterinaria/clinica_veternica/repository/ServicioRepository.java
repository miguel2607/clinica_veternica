package com.veterinaria.clinica_veternica.repository;

import com.veterinaria.clinica_veternica.domain.agenda.Servicio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio para la entidad Servicio.
 *
 * @author Clínica Veterinaria Team
 * @version 1.0
 * @since 2025-11-02
 */
@Repository
public interface ServicioRepository extends JpaRepository<Servicio, Long> {

    Optional<Servicio> findByNombre(String nombre);

    List<Servicio> findByTipoServicio(String tipoServicio);

    List<Servicio> findByActivo(Boolean activo);

    @Query("SELECT s FROM Servicio s WHERE s.activo = true ORDER BY s.nombre")
    List<Servicio> findServiciosActivos();

    @Query("SELECT s FROM Servicio s WHERE s.tipoServicio = :tipo AND s.activo = true")
    List<Servicio> findServiciosActivosPorTipo(@Param("tipo") String tipo);

    @Query("SELECT s FROM Servicio s WHERE s.precio BETWEEN :min AND :max AND s.activo = true")
    List<Servicio> findServiciosPorRangoPrecio(@Param("min") BigDecimal min, @Param("max") BigDecimal max);

    boolean existsByNombre(String nombre);
}
