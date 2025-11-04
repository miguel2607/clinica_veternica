package com.veterinaria.clinica_veternica.mapper.comunicacion;

import com.veterinaria.clinica_veternica.domain.agenda.Cita;
import com.veterinaria.clinica_veternica.domain.comunicacion.RecordatorioCita;
import com.veterinaria.clinica_veternica.dto.request.comunicacion.RecordatorioCitaRequestDTO;
import com.veterinaria.clinica_veternica.dto.response.comunicacion.RecordatorioCitaResponseDTO;
import org.mapstruct.*;

import java.util.List;

/**
 * Mapper para convertir entre RecordatorioCita (Entity) y sus DTOs.
 * MapStruct genera automáticamente la implementación.
 *
 * @author Clínica Veterinaria Team
 * @version 1.0
 * @since 2025-11-04
 */
@Mapper(componentModel = "spring")
public interface RecordatorioCitaMapper {

    /**
     * Convierte un RecordatorioCitaRequestDTO a RecordatorioCita (Entity).
     *
     * @param requestDTO DTO de request
     * @return Entidad RecordatorioCita
     */
    @Mapping(target = "idRecordatorio", ignore = true)
    @Mapping(target = "cita", ignore = true)
    @Mapping(target = "enviado", ignore = true)
    @Mapping(target = "fechaEnvio", ignore = true)
    @Mapping(target = "confirmado", ignore = true)
    @Mapping(target = "fechaConfirmacion", ignore = true)
    @Mapping(target = "tokenConfirmacion", ignore = true)
    @Mapping(target = "intentosEnvio", ignore = true)
    @Mapping(target = "mensajeError", ignore = true)
    @Mapping(target = "idExterno", ignore = true)
    @Mapping(target = "fechaCreacion", ignore = true)
    RecordatorioCita toEntity(RecordatorioCitaRequestDTO requestDTO);

    /**
     * Convierte un RecordatorioCita (Entity) a RecordatorioCitaResponseDTO.
     *
     * @param recordatorio Entidad
     * @return DTO de response
     */
    @Mapping(target = "idCita", source = "cita.idCita")
    @Mapping(target = "seAgotaronIntentos", expression = "java(recordatorio.seAgotaronIntentos())")
    @Mapping(target = "puedeReintentar", expression = "java(recordatorio.puedeReintentar())")
    @Mapping(target = "estaListoParaEnviar", expression = "java(recordatorio.estaListoParaEnviar())")
    @Mapping(target = "esRecordatorioInicial", expression = "java(recordatorio.esRecordatorioInicial())")
    @Mapping(target = "esRecordatorioFinal", expression = "java(recordatorio.esRecordatorioFinal())")
    @Mapping(target = "requiereConfirmacion", expression = "java(recordatorio.requiereConfirmacion())")
    @Mapping(target = "horasHastaEnvio", expression = "java(recordatorio.getHorasHastaEnvio())")
    @Mapping(target = "estaVencido", expression = "java(recordatorio.estaVencido())")
    RecordatorioCitaResponseDTO toResponseDTO(RecordatorioCita recordatorio);

    /**
     * Convierte una lista de RecordatorioCita a lista de RecordatorioCitaResponseDTO.
     *
     * @param recordatorios Lista de entidades
     * @return Lista de DTOs
     */
    List<RecordatorioCitaResponseDTO> toResponseDTOList(List<RecordatorioCita> recordatorios);

    /**
     * Mapea el ID de cita a la entidad Cita.
     *
     * @param idCita ID de la cita
     * @return Entidad Cita con solo el ID establecido
     */
    default Cita mapIdToCita(Long idCita) {
        if (idCita == null) {
            return null;
        }
        return Cita.builder()
                .idCita(idCita)
                .build();
    }
}
