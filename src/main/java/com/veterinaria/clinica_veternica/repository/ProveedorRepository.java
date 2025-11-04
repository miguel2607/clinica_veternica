package com.veterinaria.clinica_veternica.repository;

import com.veterinaria.clinica_veternica.domain.inventario.Proveedor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para la entidad Proveedor.
 *
 * @author Clínica Veterinaria Team
 * @version 1.0
 * @since 2025-11-02
 */
@Repository
public interface ProveedorRepository extends JpaRepository<Proveedor, Long> {

    Optional<Proveedor> findByNit(String nit);

    List<Proveedor> findByActivo(Boolean activo);

    @Query("SELECT p FROM Proveedor p WHERE p.activo = true ORDER BY p.nombre")
    List<Proveedor> findProveedoresActivos();

    @Query("SELECT p FROM Proveedor p WHERE LOWER(p.nombre) LIKE LOWER(CONCAT('%', :busqueda, '%')) AND p.activo = true")
    List<Proveedor> buscarProveedores(@Param("busqueda") String busqueda);

    boolean existsByNit(String nit);
}
