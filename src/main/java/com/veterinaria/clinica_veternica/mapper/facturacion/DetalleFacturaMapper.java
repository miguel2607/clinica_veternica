package com.veterinaria.clinica_veternica.mapper.facturacion;

import com.veterinaria.clinica_veternica.domain.facturacion.DetalleFactura;
import com.veterinaria.clinica_veternica.domain.facturacion.Factura;
import com.veterinaria.clinica_veternica.dto.request.facturacion.DetalleFacturaRequestDTO;
import com.veterinaria.clinica_veternica.dto.response.facturacion.DetalleFacturaResponseDTO;
import org.mapstruct.*;

import java.util.List;

/**
 * Mapper para convertir entre DetalleFactura (Entity) y sus DTOs.
 * MapStruct genera automáticamente la implementación.
 *
 * @author Clínica Veterinaria Team
 * @version 1.0
 * @since 2025-11-04
 */
@Mapper(componentModel = "spring")
public interface DetalleFacturaMapper {

    /**
     * Convierte un DetalleFacturaRequestDTO a DetalleFactura (Entity).
     *
     * @param requestDTO DTO de request
     * @return Entidad DetalleFactura
     */
    @Mapping(target = "idDetalle", ignore = true)
    @Mapping(target = "factura", ignore = true)
    @Mapping(target = "montoDescuento", ignore = true)
    @Mapping(target = "subtotal", ignore = true)
    @Mapping(target = "fechaCreacion", ignore = true)
    DetalleFactura toEntity(DetalleFacturaRequestDTO requestDTO);

    /**
     * Convierte un DetalleFactura (Entity) a DetalleFacturaResponseDTO.
     *
     * @param detalle Entidad
     * @return DTO de response
     */
    @Mapping(target = "idFactura", source = "factura.idFactura")
    @Mapping(target = "precioTotal", expression = "java(detalle.getPrecioTotal())")
    @Mapping(target = "tieneDescuento", expression = "java(detalle.tieneDescuento())")
    @Mapping(target = "esServicio", expression = "java(detalle.esServicio())")
    @Mapping(target = "esInsumo", expression = "java(detalle.esInsumo())")
    DetalleFacturaResponseDTO toResponseDTO(DetalleFactura detalle);

    /**
     * Convierte una lista de DetalleFactura a lista de DetalleFacturaResponseDTO.
     *
     * @param detalles Lista de entidades
     * @return Lista de DTOs
     */
    List<DetalleFacturaResponseDTO> toResponseDTOList(List<DetalleFactura> detalles);

    /**
     * Actualiza una entidad DetalleFactura existente con los datos del DTO.
     *
     * @param requestDTO DTO con los datos actualizados
     * @param detalle Entidad a actualizar
     */
    @Mapping(target = "idDetalle", ignore = true)
    @Mapping(target = "factura", ignore = true)
    @Mapping(target = "montoDescuento", ignore = true)
    @Mapping(target = "subtotal", ignore = true)
    @Mapping(target = "fechaCreacion", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDTO(DetalleFacturaRequestDTO requestDTO, @MappingTarget DetalleFactura detalle);

    /**
     * Mapea el ID de factura a la entidad Factura.
     *
     * @param idFactura ID de la factura
     * @return Entidad Factura con solo el ID establecido
     */
    default Factura mapIdToFactura(Long idFactura) {
        if (idFactura == null) {
            return null;
        }
        return Factura.builder()
                .idFactura(idFactura)
                .build();
    }
}
