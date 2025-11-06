package com.veterinaria.clinica_veternica.repository;

import com.veterinaria.clinica_veternica.domain.inventario.Insumo;
import com.veterinaria.clinica_veternica.domain.inventario.TipoInsumo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para la entidad Insumo.
 *
 * @author Clínica Veterinaria Team
 * @version 1.0
 * @since 2025-11-02
 */
@Repository
public interface InsumoRepository extends JpaRepository<Insumo, Long> {

    Optional<Insumo> findByCodigo(String codigo);

    List<Insumo> findByTipoInsumo(TipoInsumo tipoInsumo);

    List<Insumo> findByActivo(Boolean activo);

    @Query("SELECT i FROM Insumo i WHERE i.activo = true ORDER BY i.nombre")
    List<Insumo> findInsumosActivos();

    @Query("SELECT i FROM Insumo i WHERE i.cantidadStock <= i.stockMinimo AND i.activo = true")
    List<Insumo> findInsumosConStockBajo();

    @Query("SELECT i FROM Insumo i WHERE i.cantidadStock = 0 AND i.activo = true")
    List<Insumo> findInsumosAgotados();

    @Query("SELECT i FROM Insumo i WHERE i.tipoInsumo = :tipo AND i.activo = true")
    List<Insumo> findInsumosActivosPorTipo(@Param("tipo") TipoInsumo tipo);

    @Query("SELECT i FROM Insumo i WHERE LOWER(i.nombre) LIKE LOWER(CONCAT('%', :busqueda, '%')) AND i.activo = true")
    List<Insumo> buscarInsumos(@Param("busqueda") String busqueda);

    boolean existsByCodigo(String codigo);
}
