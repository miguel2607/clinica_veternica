package com.veterinaria.clinica_veternica.mapper.inventario;

import com.veterinaria.clinica_veternica.domain.inventario.Insumo;
import com.veterinaria.clinica_veternica.domain.inventario.MovimientoInventario;
import com.veterinaria.clinica_veternica.domain.usuario.Usuario;
import com.veterinaria.clinica_veternica.dto.request.inventario.MovimientoInventarioRequestDTO;
import com.veterinaria.clinica_veternica.dto.response.inventario.MovimientoInventarioResponseDTO;
import org.mapstruct.*;

import java.util.List;

/**
 * Mapper para convertir entre MovimientoInventario (Entity) y sus DTOs.
 * MapStruct genera automáticamente la implementación.
 *
 * @author Clínica Veterinaria Team
 * @version 1.0
 * @since 2025-11-04
 */
@Mapper(componentModel = "spring")
public interface MovimientoInventarioMapper {

    /**
     * Convierte un MovimientoInventarioRequestDTO a MovimientoInventario (Entity).
     *
     * @param requestDTO DTO de request
     * @return Entidad MovimientoInventario
     */
    @Mapping(target = "idMovimiento", ignore = true)
    @Mapping(target = "insumo", ignore = true)
    @Mapping(target = "stockAnterior", ignore = true)
    @Mapping(target = "stockNuevo", ignore = true)
    @Mapping(target = "costoTotal", ignore = true)
    @Mapping(target = "usuario", ignore = true)
    @Mapping(target = "fechaCreacion", ignore = true)
    MovimientoInventario toEntity(MovimientoInventarioRequestDTO requestDTO);

    /**
     * Convierte un MovimientoInventario (Entity) a MovimientoInventarioResponseDTO.
     *
     * @param movimiento Entidad
     * @return DTO de response
     */
    @Mapping(target = "idInsumo", source = "insumo.idInsumo")
    @Mapping(target = "nombreInsumo", source = "insumo.nombre")
    @Mapping(target = "codigoInsumo", source = "insumo.codigo")
    @Mapping(target = "idUsuario", source = "usuario.idUsuario")
    @Mapping(target = "nombreUsuario", expression = "java(movimiento.getNombreUsuario())")
    @Mapping(target = "esEntrada", expression = "java(movimiento.esEntrada())")
    @Mapping(target = "esSalida", expression = "java(movimiento.esSalida())")
    @Mapping(target = "esAjuste", expression = "java(movimiento.esAjuste())")
    @Mapping(target = "esPorVencimiento", expression = "java(movimiento.esPorVencimiento())")
    @Mapping(target = "esPorPerdida", expression = "java(movimiento.esPorPerdida())")
    @Mapping(target = "resumen", expression = "java(movimiento.getResumen())")
    @Mapping(target = "requiereAprobacion", expression = "java(movimiento.requiereAprobacion())")
    MovimientoInventarioResponseDTO toResponseDTO(MovimientoInventario movimiento);

    /**
     * Convierte una lista de MovimientoInventario a lista de MovimientoInventarioResponseDTO.
     *
     * @param movimientos Lista de entidades
     * @return Lista de DTOs
     */
    List<MovimientoInventarioResponseDTO> toResponseDTOList(List<MovimientoInventario> movimientos);

    /**
     * Mapea el ID de insumo a la entidad Insumo.
     *
     * @param idInsumo ID del insumo
     * @return Entidad Insumo con solo el ID establecido
     */
    default Insumo mapIdToInsumo(Long idInsumo) {
        if (idInsumo == null) {
            return null;
        }
        return Insumo.builder()
                .idInsumo(idInsumo)
                .build();
    }

    /**
     * Mapea el ID de usuario a la entidad Usuario.
     *
     * @param idUsuario ID del usuario
     * @return Entidad Usuario con solo el ID establecido
     */
    default Usuario mapIdToUsuario(Long idUsuario) {
        if (idUsuario == null) {
            return null;
        }
        Usuario usuario = new Usuario();
        usuario.setIdUsuario(idUsuario);
        return usuario;
    }
}
