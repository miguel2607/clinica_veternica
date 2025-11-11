package com.veterinaria.clinica_veternica.mapper.inventario;

import com.veterinaria.clinica_veternica.domain.inventario.Inventario;
import com.veterinaria.clinica_veternica.dto.response.inventario.InventarioResponseDTO;
import org.mapstruct.*;

import java.util.List;

/**
 * Mapper para convertir entre Inventario (Entity) y sus DTOs.
 * MapStruct genera automáticamente la implementación.
 *
 * @author Clínica Veterinaria Team
 * @version 1.0
 * @since 2025-11-04
 */
@Mapper(componentModel = "spring")
public interface InventarioMapper {

    /**
     * Convierte un Inventario (Entity) a InventarioResponseDTO.
     *
     * @param inventario Entidad
     * @return DTO de response
     */
    @Mapping(target = "idInsumo", source = "insumo.idInsumo")
    @Mapping(target = "nombreInsumo", source = "insumo.nombre")
    @Mapping(target = "codigoInsumo", source = "insumo.codigo")
    @Mapping(target = "stockMinimo", source = "insumo.stockMinimo")
    @Mapping(target = "stockMaximo", source = "insumo.stockMaximo")
    @Mapping(target = "precioUnitario", source = "insumo.precioCompra")
    @Mapping(target = "indiceRotacion", expression = "java(inventario.getIndiceRotacion())")
    @Mapping(target = "esNivelCritico", expression = "java(inventario.esNivelCritico())")
    @Mapping(target = "margenBruto", expression = "java(inventario.getMargenBruto())")
    @Mapping(target = "tieneMovimientoReciente", expression = "java(inventario.tieneMovimientoReciente())")
    InventarioResponseDTO toResponseDTO(Inventario inventario);

    /**
     * Convierte una lista de Inventario a lista de InventarioResponseDTO.
     *
     * @param inventarios Lista de entidades
     * @return Lista de DTOs
     */
    List<InventarioResponseDTO> toResponseDTOList(List<Inventario> inventarios);
}
