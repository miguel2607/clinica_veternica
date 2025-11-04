package com.veterinaria.clinica_veternica.mapper.inventario;

import com.veterinaria.clinica_veternica.domain.inventario.Proveedor;
import com.veterinaria.clinica_veternica.dto.request.inventario.ProveedorRequestDTO;
import com.veterinaria.clinica_veternica.dto.response.inventario.ProveedorResponseDTO;
import org.mapstruct.*;

import java.util.List;

/**
 * Mapper para convertir entre Proveedor (Entity) y sus DTOs.
 * MapStruct genera automáticamente la implementación.
 *
 * @author Clínica Veterinaria Team
 * @version 1.0
 * @since 2025-11-04
 */
@Mapper(componentModel = "spring")
public interface ProveedorMapper {

    /**
     * Convierte un ProveedorRequestDTO a Proveedor (Entity).
     *
     * @param requestDTO DTO de request
     * @return Entidad Proveedor
     */
    @Mapping(target = "idProveedor", ignore = true)
    @Mapping(target = "insumos", ignore = true)
    @Mapping(target = "fechaCreacion", ignore = true)
    @Mapping(target = "fechaModificacion", ignore = true)
    Proveedor toEntity(ProveedorRequestDTO requestDTO);

    /**
     * Convierte un Proveedor (Entity) a ProveedorResponseDTO.
     *
     * @param proveedor Entidad
     * @return DTO de response
     */
    @Mapping(target = "cantidadInsumos", expression = "java(proveedor.getCantidadInsumos())")
    @Mapping(target = "esConfiable", expression = "java(proveedor.esConfiable())")
    @Mapping(target = "tieneEntregaRapida", expression = "java(proveedor.tieneEntregaRapida())")
    @Mapping(target = "infoContacto", expression = "java(proveedor.getInfoContacto())")
    ProveedorResponseDTO toResponseDTO(Proveedor proveedor);

    /**
     * Convierte una lista de Proveedor a lista de ProveedorResponseDTO.
     *
     * @param proveedores Lista de entidades
     * @return Lista de DTOs
     */
    List<ProveedorResponseDTO> toResponseDTOList(List<Proveedor> proveedores);

    /**
     * Actualiza una entidad Proveedor existente con los datos del DTO.
     *
     * @param requestDTO DTO con los datos actualizados
     * @param proveedor Entidad a actualizar
     */
    @Mapping(target = "idProveedor", ignore = true)
    @Mapping(target = "insumos", ignore = true)
    @Mapping(target = "fechaCreacion", ignore = true)
    @Mapping(target = "fechaModificacion", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDTO(ProveedorRequestDTO requestDTO, @MappingTarget Proveedor proveedor);
}
