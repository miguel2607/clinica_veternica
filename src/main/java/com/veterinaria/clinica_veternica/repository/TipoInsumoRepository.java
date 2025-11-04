package com.veterinaria.clinica_veternica.repository;

import com.veterinaria.clinica_veternica.domain.inventario.TipoInsumo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para la entidad TipoInsumo.
 *
 * @author Clínica Veterinaria Team
 * @version 1.0
 * @since 2025-11-02
 */
@Repository
public interface TipoInsumoRepository extends JpaRepository<TipoInsumo, Long> {

    Optional<TipoInsumo> findByNombre(String nombre);

    List<TipoInsumo> findByActivo(Boolean activo);

    @Query("SELECT t FROM TipoInsumo t WHERE t.activo = true ORDER BY t.nombre")
    List<TipoInsumo> findTiposActivos();

    boolean existsByNombre(String nombre);
}
