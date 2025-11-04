package com.veterinaria.clinica_veternica.mapper.facturacion;

import com.veterinaria.clinica_veternica.domain.facturacion.Factura;
import com.veterinaria.clinica_veternica.domain.facturacion.Pago;
import com.veterinaria.clinica_veternica.domain.usuario.Usuario;
import com.veterinaria.clinica_veternica.dto.request.facturacion.PagoRequestDTO;
import com.veterinaria.clinica_veternica.dto.response.facturacion.PagoResponseDTO;
import org.mapstruct.*;

import java.util.List;

/**
 * Mapper para convertir entre Pago (Entity) y sus DTOs.
 * MapStruct genera automáticamente la implementación.
 *
 * @author Clínica Veterinaria Team
 * @version 1.0
 * @since 2025-11-04
 */
@Mapper(componentModel = "spring")
public interface PagoMapper {

    /**
     * Convierte un PagoRequestDTO a Pago (Entity).
     *
     * @param requestDTO DTO de request
     * @return Entidad Pago
     */
    @Mapping(target = "idPago", ignore = true)
    @Mapping(target = "factura", ignore = true)
    @Mapping(target = "comprobante", ignore = true)
    @Mapping(target = "usuario", ignore = true)
    @Mapping(target = "anulado", ignore = true)
    @Mapping(target = "fechaAnulacion", ignore = true)
    @Mapping(target = "motivoAnulacion", ignore = true)
    @Mapping(target = "fechaCreacion", ignore = true)
    Pago toEntity(PagoRequestDTO requestDTO);

    /**
     * Convierte un Pago (Entity) a PagoResponseDTO.
     *
     * @param pago Entidad
     * @return DTO de response
     */
    @Mapping(target = "idFactura", source = "factura.idFactura")
    @Mapping(target = "numeroFactura", source = "factura.numeroFactura")
    @Mapping(target = "idUsuario", source = "usuario.idUsuario")
    @Mapping(target = "nombreUsuario", expression = "java(pago.getNombreUsuario())")
    @Mapping(target = "esValido", expression = "java(pago.esValido())")
    @Mapping(target = "esConTarjeta", expression = "java(pago.esConTarjeta())")
    @Mapping(target = "esEfectivo", expression = "java(pago.esEfectivo())")
    @Mapping(target = "esTransferencia", expression = "java(pago.esTransferencia())")
    @Mapping(target = "descripcionPago", expression = "java(pago.getDescripcionPago())")
    @Mapping(target = "requiereValidacion", expression = "java(pago.requiereValidacion())")
    PagoResponseDTO toResponseDTO(Pago pago);

    /**
     * Convierte una lista de Pago a lista de PagoResponseDTO.
     *
     * @param pagos Lista de entidades
     * @return Lista de DTOs
     */
    List<PagoResponseDTO> toResponseDTOList(List<Pago> pagos);

    /**
     * Actualiza una entidad Pago existente con los datos del DTO.
     *
     * @param requestDTO DTO con los datos actualizados
     * @param pago Entidad a actualizar
     */
    @Mapping(target = "idPago", ignore = true)
    @Mapping(target = "factura", ignore = true)
    @Mapping(target = "comprobante", ignore = true)
    @Mapping(target = "usuario", ignore = true)
    @Mapping(target = "anulado", ignore = true)
    @Mapping(target = "fechaAnulacion", ignore = true)
    @Mapping(target = "motivoAnulacion", ignore = true)
    @Mapping(target = "fechaCreacion", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDTO(PagoRequestDTO requestDTO, @MappingTarget Pago pago);

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
