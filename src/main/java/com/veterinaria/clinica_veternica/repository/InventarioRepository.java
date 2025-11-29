package com.veterinaria.clinica_veternica.repository;

import com.veterinaria.clinica_veternica.domain.inventario.Inventario;
import com.veterinaria.clinica_veternica.domain.inventario.Insumo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio para la entidad Inventario.
 *
 * @author Clínica Veterinaria Team
 * @version 1.0
 * @since 2025-11-02
 */
@Repository
public interface InventarioRepository extends JpaRepository<Inventario, Long> {

    Optional<Inventario> findByInsumo(Insumo insumo);
    
    @Query("SELECT i FROM Inventario i JOIN FETCH i.insumo WHERE i.insumo = :insumo")
    Optional<Inventario> findByInsumoWithFetch(@Param("insumo") Insumo insumo);

    @Query("SELECT i FROM Inventario i JOIN FETCH i.insumo WHERE i.cantidadActual <= i.insumo.stockMinimo")
    List<Inventario> findInventariosConStockBajo();

    @Query("SELECT i FROM Inventario i JOIN FETCH i.insumo WHERE i.cantidadActual = 0")
    List<Inventario> findInventariosAgotados();

    @Query("SELECT i FROM Inventario i WHERE i.fechaActualizacion BETWEEN :inicio AND :fin")
    List<Inventario> findInventariosConMovimientosEnRango(@Param("inicio") LocalDateTime inicio, @Param("fin") LocalDateTime fin);

    @Query("SELECT i FROM Inventario i JOIN FETCH i.insumo ORDER BY i.valorTotal DESC")
    List<Inventario> findInventariosOrdenadosPorValor();
    
    @Query("SELECT i FROM Inventario i JOIN FETCH i.insumo")
    List<Inventario> findAllWithInsumo();
}
