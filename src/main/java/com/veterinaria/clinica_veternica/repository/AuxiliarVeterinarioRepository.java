package com.veterinaria.clinica_veternica.repository;

import com.veterinaria.clinica_veternica.domain.usuario.AuxiliarVeterinario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio para la entidad AuxiliarVeterinario.
 *
 * @author Clínica Veterinaria Team
 * @version 1.0
 * @since 2025-11-02
 */
@Repository
public interface AuxiliarVeterinarioRepository extends JpaRepository<AuxiliarVeterinario, Long> {

    /**
     * Busca auxiliares veterinarios activos.
     *
     * @param activo Estado activo del auxiliar
     * @return Lista de auxiliares activos
     */
    List<AuxiliarVeterinario> findByActivo(Boolean activo);

    /**
     * Busca todos los auxiliares veterinarios activos.
     *
     * @return Lista de auxiliares activos
     */
    @Query("SELECT a FROM AuxiliarVeterinario a WHERE a.activo = true")
    List<AuxiliarVeterinario> findAuxiliaresActivos();

    /**
     * Cuenta auxiliares veterinarios activos.
     *
     * @return Número de auxiliares activos
     */
    @Query("SELECT COUNT(a) FROM AuxiliarVeterinario a WHERE a.activo = true")
    long countAuxiliaresActivos();
}
