package com.veterinaria.clinica_veternica.repository;

import com.veterinaria.clinica_veternica.domain.comunicacion.RecordatorioCita;
import com.veterinaria.clinica_veternica.domain.agenda.Cita;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repositorio para la entidad RecordatorioCita.
 *
 * @author Clínica Veterinaria Team
 * @version 1.0
 * @since 2025-11-02
 */
@Repository
public interface RecordatorioCitaRepository extends JpaRepository<RecordatorioCita, Long> {

    List<RecordatorioCita> findByCita(Cita cita);

    List<RecordatorioCita> findByEnviado(Boolean enviado);

    @Query("SELECT r FROM RecordatorioCita r WHERE r.enviado = false " +
           "AND r.fechaProgramadaEnvio <= :ahora AND r.intentosEnvio < r.maxIntentos")
    List<RecordatorioCita> findRecordatoriosPendientesEnvio(@Param("ahora") LocalDateTime ahora);

    @Query("SELECT r FROM RecordatorioCita r WHERE r.cita = :cita ORDER BY r.fechaProgramadaEnvio")
    List<RecordatorioCita> findRecordatoriosPorCitaOrdenados(@Param("cita") Cita cita);

    @Query("SELECT r FROM RecordatorioCita r WHERE r.enviado = true AND r.confirmado = false")
    List<RecordatorioCita> findRecordatoriosSinConfirmar();

    @Query("SELECT r FROM RecordatorioCita r WHERE r.canal = :canal AND r.enviado = false")
    List<RecordatorioCita> findRecordatoriosPendientesPorCanal(@Param("canal") String canal);
}
