package com.veterinaria.clinica_veternica.repository;

import com.veterinaria.clinica_veternica.domain.comunicacion.Correo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repositorio para la entidad Correo.
 *
 * @author Clínica Veterinaria Team
 * @version 1.0
 * @since 2025-11-02
 */
@Repository
public interface CorreoRepository extends JpaRepository<Correo, Long> {

    List<Correo> findByDestinatario(String destinatario);

    List<Correo> findByTipoCorreo(String tipoCorreo);

    List<Correo> findByEnviado(Boolean enviado);

    List<Correo> findByAbierto(Boolean abierto);

    List<Correo> findByProveedor(String proveedor);

    @Query("SELECT c FROM Correo c WHERE c.destinatario = :email ORDER BY c.fechaCreacion DESC")
    List<Correo> findCorreosPorDestinatarioOrdenados(@Param("email") String email);

    @Query("SELECT c FROM Correo c WHERE c.enviado = false AND c.intentosEnvio < c.maxIntentos")
    List<Correo> findCorreosPendientes();

    @Query("SELECT c FROM Correo c WHERE c.enviado = false AND c.intentosEnvio >= c.maxIntentos")
    List<Correo> findCorreosFallidos();

    @Query("SELECT c FROM Correo c WHERE c.enviado = true AND c.abierto = false")
    List<Correo> findCorreosNoAbiertos();

    @Query("SELECT c FROM Correo c WHERE c.fechaEnvio BETWEEN :inicio AND :fin ORDER BY c.fechaEnvio DESC")
    List<Correo> findCorreosEnRango(@Param("inicio") LocalDateTime inicio, @Param("fin") LocalDateTime fin);

    @Query("SELECT c FROM Correo c WHERE c.prioridad = 'ALTA' AND c.enviado = false")
    List<Correo> findCorreosAltaPrioridad();

    @Query("SELECT c FROM Correo c WHERE c.tipoEntidadRelacionada = :tipo " +
           "AND c.idEntidadRelacionada = :id ORDER BY c.fechaCreacion DESC")
    List<Correo> findCorreosPorEntidadRelacionada(@Param("tipo") String tipo, @Param("id") Long id);

    @Query("SELECT c FROM Correo c WHERE c.tipoCorreo = :tipo " +
           "AND c.fechaEnvio BETWEEN :inicio AND :fin ORDER BY c.fechaEnvio DESC")
    List<Correo> findCorreosPorTipoEnRango(@Param("tipo") String tipo,
                                           @Param("inicio") LocalDateTime inicio,
                                           @Param("fin") LocalDateTime fin);

    @Query("SELECT COUNT(c) FROM Correo c WHERE c.enviado = true AND c.abierto = true")
    long countCorreosAbiertos();

    @Query("SELECT COUNT(c) FROM Correo c WHERE c.enviado = true")
    long countCorreosEnviados();

    @Query("SELECT c FROM Correo c WHERE c.huboClic = true ORDER BY c.fechaPrimerClic DESC")
    List<Correo> findCorreosConClics();

    @Query("SELECT c FROM Correo c WHERE c.tipoCorreo = :tipo AND c.enviado = false")
    List<Correo> findCorreosPendientesPorTipo(@Param("tipo") String tipo);
}
