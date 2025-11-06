package com.veterinaria.clinica_veternica.repository;

import com.veterinaria.clinica_veternica.domain.facturacion.Factura;
import com.veterinaria.clinica_veternica.domain.paciente.Propietario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio para la entidad Factura.
 *
 * @author Clínica Veterinaria Team
 * @version 1.0
 * @since 2025-11-02
 */
@Repository
public interface FacturaRepository extends JpaRepository<Factura, Long> {

    Optional<Factura> findByNumeroFactura(String numeroFactura);

    List<Factura> findByPropietario(Propietario propietario);

    List<Factura> findByPagada(Boolean pagada);

    @Query("SELECT f FROM Factura f WHERE f.propietario = :propietario ORDER BY f.fechaEmision DESC")
    List<Factura> findFacturasPorPropietarioOrdenadas(@Param("propietario") Propietario propietario);

    @Query("SELECT f FROM Factura f WHERE f.fechaEmision BETWEEN :inicio AND :fin ORDER BY f.fechaEmision DESC")
    List<Factura> findFacturasEnRango(@Param("inicio") LocalDate inicio, @Param("fin") LocalDate fin);

    @Query("SELECT f FROM Factura f WHERE f.pagada = false AND f.anulada = false AND f.fechaVencimiento < CURRENT_DATE")
    List<Factura> findFacturasVencidas();

    @Query("SELECT f FROM Factura f WHERE f.pagada = false AND f.anulada = false")
    List<Factura> findFacturasPendientes();

    @Query("SELECT f FROM Factura f WHERE f.pagada = true AND f.fechaEmision BETWEEN :inicio AND :fin")
    List<Factura> findFacturasPagadasEnRango(@Param("inicio") LocalDate inicio, @Param("fin") LocalDate fin);

    @Query("SELECT SUM(f.total) FROM Factura f WHERE f.pagada = true AND f.fechaEmision BETWEEN :inicio AND :fin")
    BigDecimal sumTotalFacturasPagadasEnRango(@Param("inicio") LocalDate inicio, @Param("fin") LocalDate fin);

    @Query("SELECT COUNT(f) FROM Factura f WHERE f.pagada = :pagada AND f.anulada = false")
    long countByPagada(@Param("pagada") Boolean pagada);

    boolean existsByNumeroFactura(String numeroFactura);
}
