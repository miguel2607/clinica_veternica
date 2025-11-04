package com.veterinaria.clinica_veternica.mapper.comunicacion;

import com.veterinaria.clinica_veternica.domain.comunicacion.Correo;
import com.veterinaria.clinica_veternica.dto.request.comunicacion.CorreoRequestDTO;
import com.veterinaria.clinica_veternica.dto.response.comunicacion.CorreoResponseDTO;
import org.mapstruct.*;

import java.util.List;

/**
 * Mapper para convertir entre Correo (Entity) y sus DTOs.
 * MapStruct genera automáticamente la implementación.
 *
 * @author Clínica Veterinaria Team
 * @version 1.0
 * @since 2025-11-04
 */
@Mapper(componentModel = "spring")
public interface CorreoMapper {

    /**
     * Convierte un CorreoRequestDTO a Correo (Entity).
     *
     * @param requestDTO DTO de request
     * @return Entidad Correo
     */
    @Mapping(target = "idCorreo", ignore = true)
    @Mapping(target = "enviado", ignore = true)
    @Mapping(target = "fechaEnvio", ignore = true)
    @Mapping(target = "abierto", ignore = true)
    @Mapping(target = "fechaApertura", ignore = true)
    @Mapping(target = "vecesAbierto", ignore = true)
    @Mapping(target = "huboClic", ignore = true)
    @Mapping(target = "fechaPrimerClic", ignore = true)
    @Mapping(target = "intentosEnvio", ignore = true)
    @Mapping(target = "mensajeError", ignore = true)
    @Mapping(target = "idMensaje", ignore = true)
    @Mapping(target = "fechaCreacion", ignore = true)
    Correo toEntity(CorreoRequestDTO requestDTO);

    /**
     * Convierte un Correo (Entity) a CorreoResponseDTO.
     *
     * @param correo Entidad
     * @return DTO de response
     */
    @Mapping(target = "seAgotaronIntentos", expression = "java(correo.seAgotaronIntentos())")
    @Mapping(target = "puedeReintentar", expression = "java(correo.puedeReintentar())")
    @Mapping(target = "tieneAdjuntos", expression = "java(correo.tieneAdjuntos())")
    @Mapping(target = "esHTML", expression = "java(correo.esHTML())")
    @Mapping(target = "fueExitoso", expression = "java(correo.fueExitoso())")
    @Mapping(target = "tasaApertura", expression = "java(correo.getTasaApertura())")
    @Mapping(target = "minutosHastaApertura", expression = "java(correo.getMinutosHastaApertura())")
    @Mapping(target = "esAltaPrioridad", expression = "java(correo.esAltaPrioridad())")
    @Mapping(target = "resumen", expression = "java(correo.getResumen())")
    CorreoResponseDTO toResponseDTO(Correo correo);

    /**
     * Convierte una lista de Correo a lista de CorreoResponseDTO.
     *
     * @param correos Lista de entidades
     * @return Lista de DTOs
     */
    List<CorreoResponseDTO> toResponseDTOList(List<Correo> correos);
}
