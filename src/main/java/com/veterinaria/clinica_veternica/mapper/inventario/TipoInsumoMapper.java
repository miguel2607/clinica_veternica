package com.veterinaria.clinica_veternica.mapper.inventario;

import com.veterinaria.clinica_veternica.domain.inventario.TipoInsumo;
import com.veterinaria.clinica_veternica.dto.request.inventario.TipoInsumoRequestDTO;
import com.veterinaria.clinica_veternica.dto.response.inventario.TipoInsumoResponseDTO;
import org.mapstruct.*;

import java.util.List;

/**
 * Mapper para convertir entre TipoInsumo (Entity) y sus DTOs.
 * MapStruct genera automáticamente la implementación.
 *
 * @author Clínica Veterinaria Team
 * @version 1.0
 * @since 2025-11-04
 */
@Mapper(componentModel = "spring")
public interface TipoInsumoMapper {

    /**
     * Convierte un TipoInsumoRequestDTO a TipoInsumo (Entity).
     *
     * @param requestDTO DTO de request
     * @return Entidad TipoInsumo
     */
    @Mapping(target = "idTipoInsumo", ignore = true)
    @Mapping(target = "insumos", ignore = true)
    @Mapping(target = "requiereControlEspecial", ignore = true) // No se mapea desde DTO
    @Mapping(target = "fechaCreacion", ignore = true)
    @Mapping(target = "fechaModificacion", ignore = true)
    TipoInsumo toEntity(TipoInsumoRequestDTO requestDTO);

    /**
     * Convierte un TipoInsumo (Entity) a TipoInsumoResponseDTO.
     *
     * @param tipoInsumo Entidad
     * @return DTO de response
     */
    @Mapping(target = "cantidadInsumos", expression = "java(tipoInsumo.getCantidadInsumos())")
    @Mapping(target = "categoria", ignore = true) // La entidad no tiene categoria, solo requiereControlEspecial
    TipoInsumoResponseDTO toResponseDTO(TipoInsumo tipoInsumo);

    /**
     * Convierte una lista de TipoInsumo a lista de TipoInsumoResponseDTO.
     *
     * @param tiposInsumo Lista de entidades
     * @return Lista de DTOs
     */
    List<TipoInsumoResponseDTO> toResponseDTOList(List<TipoInsumo> tiposInsumo);

    /**
     * Actualiza una entidad TipoInsumo existente con los datos del DTO.
     *
     * @param requestDTO DTO con los datos actualizados
     * @param tipoInsumo Entidad a actualizar
     */
    @Mapping(target = "idTipoInsumo", ignore = true)
    @Mapping(target = "insumos", ignore = true)
    @Mapping(target = "requiereControlEspecial", ignore = true) // No se actualiza desde DTO
    @Mapping(target = "fechaCreacion", ignore = true)
    @Mapping(target = "fechaModificacion", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDTO(TipoInsumoRequestDTO requestDTO, @MappingTarget TipoInsumo tipoInsumo);
}
