package com.veterinaria.clinica_veternica.mapper.paciente;

import com.veterinaria.clinica_veternica.domain.paciente.Especie;
import com.veterinaria.clinica_veternica.dto.request.paciente.EspecieRequestDTO;
import com.veterinaria.clinica_veternica.dto.response.paciente.EspecieResponseDTO;
import org.mapstruct.*;

import java.util.List;

/**
 * Mapper para convertir entre Especie (Entity) y sus DTOs.
 * MapStruct genera automáticamente la implementación.
 *
 * @author Clínica Veterinaria Team
 * @version 1.0
 * @since 2025-11-03
 */
@Mapper(componentModel = "spring")
public interface EspecieMapper {

    /**
     * Convierte un EspecieRequestDTO a Especie (Entity).
     *
     * @param requestDTO DTO de request
     * @return Entidad Especie
     */
    @Mapping(target = "idEspecie", ignore = true)
    @Mapping(target = "razas", ignore = true)
    @Mapping(target = "fechaCreacion", ignore = true)
    @Mapping(target = "fechaModificacion", ignore = true)
    Especie toEntity(EspecieRequestDTO requestDTO);

    /**
     * Convierte una Especie (Entity) a EspecieResponseDTO.
     *
     * @param especie Entidad
     * @return DTO de response
     */
    @Mapping(target = "cantidadRazas", expression = "java(especie.getCantidadRazas())")
    EspecieResponseDTO toResponseDTO(Especie especie);

    /**
     * Convierte una lista de Especie a lista de EspecieResponseDTO.
     *
     * @param especies Lista de entidades
     * @return Lista de DTOs
     */
    List<EspecieResponseDTO> toResponseDTOList(List<Especie> especies);

    /**
     * Actualiza una entidad Especie existente con los datos del DTO.
     *
     * @param requestDTO DTO con los datos actualizados
     * @param especie Entidad a actualizar
     */
    @Mapping(target = "idEspecie", ignore = true)
    @Mapping(target = "razas", ignore = true)
    @Mapping(target = "fechaCreacion", ignore = true)
    @Mapping(target = "fechaModificacion", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDTO(EspecieRequestDTO requestDTO, @MappingTarget Especie especie);
}
