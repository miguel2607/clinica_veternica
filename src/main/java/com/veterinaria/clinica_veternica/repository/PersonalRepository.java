package com.veterinaria.clinica_veternica.repository;

import com.veterinaria.clinica_veternica.domain.usuario.Personal;
import com.veterinaria.clinica_veternica.domain.usuario.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositorio para la entidad Personal.
 *
 * @author Clínica Veterinaria Team
 * @version 1.0
 * @since 2025-01-XX
 */
@Repository
public interface PersonalRepository extends JpaRepository<Personal, Long> {

    /**
     * Busca personal por usuario.
     *
     * @param usuario Usuario asociado
     * @return Optional con el personal si existe
     */
    @Query("SELECT p FROM Personal p WHERE p.usuario = :usuario")
    Optional<Personal> findByUsuario(@Param("usuario") Usuario usuario);

    /**
     * Verifica si existe personal con el documento especificado.
     *
     * @param documento Documento a verificar
     * @return true si existe
     */
    boolean existsByDocumento(String documento);

    /**
     * Verifica si existe personal con el correo especificado.
     *
     * @param correo Correo a verificar
     * @return true si existe
     */
    boolean existsByCorreo(String correo);
}

