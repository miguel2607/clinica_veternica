package com.veterinaria.clinica_veternica.repository;

import com.veterinaria.clinica_veternica.domain.usuario.Administrador;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio para la entidad Administrador.
 *
 * @author Clínica Veterinaria Team
 * @version 1.0
 * @since 2025-11-02
 */
@Repository
public interface AdministradorRepository extends JpaRepository<Administrador, Long> {

    /**
     * Busca administradores activos.
     *
     * @param activo Estado activo del administrador
     * @return Lista de administradores activos
     */
    List<Administrador> findByActivo(Boolean activo);

    /**
     * Busca todos los administradores activos.
     *
     * @return Lista de administradores activos
     */
    @Query("SELECT a FROM Administrador a WHERE a.activo = true")
    List<Administrador> findAdministradoresActivos();

    /**
     * Cuenta administradores activos.
     *
     * @return Número de administradores activos
     */
    @Query("SELECT COUNT(a) FROM Administrador a WHERE a.activo = true")
    long countAdministradoresActivos();
}
