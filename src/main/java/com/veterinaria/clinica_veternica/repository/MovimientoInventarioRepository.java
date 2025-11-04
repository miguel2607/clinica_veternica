package com.veterinaria.clinica_veternica.repository;

import com.veterinaria.clinica_veternica.domain.inventario.MovimientoInventario;
import com.veterinaria.clinica_veternica.domain.inventario.Insumo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repositorio para la entidad MovimientoInventario.
 *
 * @author Clínica Veterinaria Team
 * @version 1.0
 * @since 2025-11-02
 */
@Repository
public interface MovimientoInventarioRepository extends JpaRepository<MovimientoInventario, Long> {

    List<MovimientoInventario> findByInsumo(Insumo insumo);

    List<MovimientoInventario> findByTipoMovimiento(String tipoMovimiento);

    @Query("SELECT m FROM MovimientoInventario m WHERE m.insumo = :insumo ORDER BY m.fechaMovimiento DESC")
    List<MovimientoInventario> findMovimientosPorInsumoOrdenados(@Param("insumo") Insumo insumo);

    @Query("SELECT m FROM MovimientoInventario m WHERE m.fechaMovimiento BETWEEN :inicio AND :fin ORDER BY m.fechaMovimiento DESC")
    List<MovimientoInventario> findMovimientosEnRango(@Param("inicio") LocalDateTime inicio, @Param("fin") LocalDateTime fin);

    @Query("SELECT m FROM MovimientoInventario m WHERE m.tipoMovimiento = :tipo AND m.fechaMovimiento BETWEEN :inicio AND :fin")
    List<MovimientoInventario> findMovimientosPorTipoEnRango(@Param("tipo") String tipo,
                                                               @Param("inicio") LocalDateTime inicio,
                                                               @Param("fin") LocalDateTime fin);

    @Query("SELECT SUM(m.cantidad) FROM MovimientoInventario m WHERE m.insumo = :insumo AND m.tipoMovimiento = :tipo")
    Integer sumCantidadPorTipo(@Param("insumo") Insumo insumo, @Param("tipo") String tipo);
}
