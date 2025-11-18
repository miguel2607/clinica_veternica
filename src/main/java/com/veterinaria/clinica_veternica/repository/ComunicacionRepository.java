package com.veterinaria.clinica_veternica.repository;

import com.veterinaria.clinica_veternica.domain.comunicacion.Comunicacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repositorio para la entidad Comunicacion.
 *
 * @author Clínica Veterinaria Team
 * @version 2.0
 * @since 2025-11-04
 */
@Repository
public interface ComunicacionRepository extends JpaRepository<Comunicacion, Long> {

    /**
     * Busca comunicaciones por tipo.
     */
    List<Comunicacion> findByTipo(String tipo);

    /**
     * Busca comunicaciones por canal.
     */
    List<Comunicacion> findByCanal(String canal);

    /**
     * Busca comunicaciones pendientes de envío.
     */
    @Query("SELECT c FROM Comunicacion c WHERE c.enviada = false AND c.intentosEnvio < c.maxIntentos")
    List<Comunicacion> findPendientesEnvio();

    /**
     * Busca recordatorios programados para enviar.
     */
    @Query("SELECT c FROM Comunicacion c WHERE c.tipo = 'RECORDATORIO' AND c.enviada = false AND c.fechaProgramadaEnvio <= :ahora")
    List<Comunicacion> findRecordatoriosPendientes(LocalDateTime ahora);

    /**
     * Busca comunicaciones por destinatario.
     */
    List<Comunicacion> findByDestinatarioEmail(String email);

    /**
     * Cuenta comunicaciones no enviadas.
     */
    @Query("SELECT COUNT(c) FROM Comunicacion c WHERE c.enviada = false")
    long countPendientes();
}

