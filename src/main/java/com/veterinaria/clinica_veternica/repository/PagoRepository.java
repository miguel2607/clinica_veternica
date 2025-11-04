package com.veterinaria.clinica_veternica.repository;

import com.veterinaria.clinica_veternica.domain.facturacion.Pago;
import com.veterinaria.clinica_veternica.domain.facturacion.Factura;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Repositorio para la entidad Pago.
 *
 * @author Clínica Veterinaria Team
 * @version 1.0
 * @since 2025-11-02
 */
@Repository
public interface PagoRepository extends JpaRepository<Pago, Long> {

    List<Pago> findByFactura(Factura factura);

    List<Pago> findByMetodoPago(String metodoPago);

    @Query("SELECT p FROM Pago p WHERE p.factura = :factura ORDER BY p.fechaPago DESC")
    List<Pago> findPagosPorFacturaOrdenados(@Param("factura") Factura factura);

    @Query("SELECT p FROM Pago p WHERE p.fechaPago BETWEEN :inicio AND :fin ORDER BY p.fechaPago DESC")
    List<Pago> findPagosEnRango(@Param("inicio") LocalDateTime inicio, @Param("fin") LocalDateTime fin);

    @Query("SELECT p FROM Pago p WHERE p.metodoPago = :metodo AND p.fechaPago BETWEEN :inicio AND :fin")
    List<Pago> findPagosPorMetodoEnRango(@Param("metodo") String metodo,
                                          @Param("inicio") LocalDateTime inicio,
                                          @Param("fin") LocalDateTime fin);

    @Query("SELECT SUM(p.monto) FROM Pago p WHERE p.fechaPago BETWEEN :inicio AND :fin")
    BigDecimal sumMontoEnRango(@Param("inicio") LocalDateTime inicio, @Param("fin") LocalDateTime fin);

    @Query("SELECT SUM(p.monto) FROM Pago p WHERE p.factura = :factura")
    BigDecimal sumMontoPorFactura(@Param("factura") Factura factura);
}
