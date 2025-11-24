package com.veterinaria.clinica_veternica.mapper.inventario;

import com.veterinaria.clinica_veternica.domain.inventario.Insumo;
import com.veterinaria.clinica_veternica.domain.inventario.TipoInsumo;
import com.veterinaria.clinica_veternica.dto.request.inventario.InsumoRequestDTO;
import com.veterinaria.clinica_veternica.dto.response.inventario.InsumoResponseDTO;
import org.mapstruct.*;

import java.util.List;

/**
 * Mapper para convertir entre Insumo (Entity) y sus DTOs.
 * MapStruct genera automáticamente la implementación.
 *
 * @author Clínica Veterinaria Team
 * @version 1.0
 * @since 2025-11-04
 */
@Mapper(componentModel = "spring")
public interface InsumoMapper {

    /**
     * Convierte un InsumoRequestDTO a Insumo (Entity).
     *
     * @param requestDTO DTO de request
     * @return Entidad Insumo
     */
    @Mapping(target = "idInsumo", ignore = true)
    @Mapping(target = "tipoInsumo", ignore = true)
    @Mapping(target = "fechaUltimaCompra", ignore = true)
    @Mapping(target = "fechaCreacion", ignore = true)
    @Mapping(target = "fechaModificacion", ignore = true)
    Insumo toEntity(InsumoRequestDTO requestDTO);

    /**
     * Convierte un Insumo (Entity) a InsumoResponseDTO.
     *
     * @param insumo Entidad
     * @return DTO de response
     */
    @Mapping(target = "idTipoInsumo", source = "tipoInsumo.idTipoInsumo")
    @Mapping(target = "nombreTipoInsumo", source = "tipoInsumo.nombre")
    @Mapping(target = "esStockBajo", expression = "java(insumo.esStockBajo())")
    @Mapping(target = "estaSinStock", expression = "java(insumo.estaSinStock())")
    @Mapping(target = "estaProximoAVencer", expression = "java(insumo.estaProximoAVencer())")
    @Mapping(target = "estaVencido", expression = "java(insumo.estaVencido())")
    @Mapping(target = "margenGanancia", expression = "java(insumo.getMargenGanancia())")
    @Mapping(target = "porcentajeMargen", expression = "java(insumo.getPorcentajeMargen())")
    @Mapping(target = "valorTotalInventario", expression = "java(insumo.getValorTotalInventario())")
    InsumoResponseDTO toResponseDTO(Insumo insumo);

    /**
     * Convierte una lista de Insumo a lista de InsumoResponseDTO.
     *
     * @param insumos Lista de entidades
     * @return Lista de DTOs
     */
    List<InsumoResponseDTO> toResponseDTOList(List<Insumo> insumos);

    /**
     * Actualiza una entidad Insumo existente con los datos del DTO.
     *
     * @param requestDTO DTO con los datos actualizados
     * @param insumo Entidad a actualizar
     */
    @Mapping(target = "idInsumo", ignore = true)
    @Mapping(target = "tipoInsumo", ignore = true)
    @Mapping(target = "fechaUltimaCompra", ignore = true)
    @Mapping(target = "fechaCreacion", ignore = true)
    @Mapping(target = "fechaModificacion", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDTO(InsumoRequestDTO requestDTO, @MappingTarget Insumo insumo);

    /**
     * Mapea el ID de tipo de insumo a la entidad TipoInsumo.
     *
     * @param idTipoInsumo ID del tipo de insumo
     * @return Entidad TipoInsumo con solo el ID establecido
     */
    default TipoInsumo mapIdToTipoInsumo(Long idTipoInsumo) {
        if (idTipoInsumo == null) {
            return null;
        }
        return TipoInsumo.builder()
                .idTipoInsumo(idTipoInsumo)
                .build();
    }
}
