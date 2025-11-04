package com.veterinaria.clinica_veternica.repository;

import com.veterinaria.clinica_veternica.domain.facturacion.DetalleFactura;
import com.veterinaria.clinica_veternica.domain.facturacion.Factura;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio para la entidad DetalleFactura.
 *
 * @author Clínica Veterinaria Team
 * @version 1.0
 * @since 2025-11-02
 */
@Repository
public interface DetalleFacturaRepository extends JpaRepository<DetalleFactura, Long> {

    List<DetalleFactura> findByFactura(Factura factura);

    @Query("SELECT d FROM DetalleFactura d WHERE d.factura = :factura ORDER BY d.idDetalle")
    List<DetalleFactura> findDetallesPorFacturaOrdenados(@Param("factura") Factura factura);

    @Query("SELECT COUNT(d) FROM DetalleFactura d WHERE d.factura = :factura")
    long countByFactura(@Param("factura") Factura factura);
}
