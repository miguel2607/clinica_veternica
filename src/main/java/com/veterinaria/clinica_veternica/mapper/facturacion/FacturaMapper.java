package com.veterinaria.clinica_veternica.mapper.facturacion;

import com.veterinaria.clinica_veternica.domain.agenda.Cita;
import com.veterinaria.clinica_veternica.domain.facturacion.Factura;
import com.veterinaria.clinica_veternica.domain.paciente.Propietario;
import com.veterinaria.clinica_veternica.domain.usuario.Usuario;
import com.veterinaria.clinica_veternica.dto.request.facturacion.FacturaRequestDTO;
import com.veterinaria.clinica_veternica.dto.response.facturacion.FacturaResponseDTO;
import org.mapstruct.*;

import java.util.List;

/**
 * Mapper para convertir entre Factura (Entity) y sus DTOs.
 * MapStruct genera automáticamente la implementación.
 *
 * @author Clínica Veterinaria Team
 * @version 1.0
 * @since 2025-11-04
 */
@Mapper(componentModel = "spring", uses = {DetalleFacturaMapper.class, PagoMapper.class})
public interface FacturaMapper {

    /**
     * Convierte un FacturaRequestDTO a Factura (Entity).
     *
     * @param requestDTO DTO de request
     * @return Entidad Factura
     */
    @Mapping(target = "idFactura", ignore = true)
    @Mapping(target = "numeroFactura", ignore = true)
    @Mapping(target = "propietario", ignore = true)
    @Mapping(target = "cita", ignore = true)
    @Mapping(target = "subtotal", ignore = true)
    @Mapping(target = "total", ignore = true)
    @Mapping(target = "montoPagado", ignore = true)
    @Mapping(target = "fechaPago", ignore = true)
    @Mapping(target = "anulada", ignore = true)
    @Mapping(target = "fechaAnulacion", ignore = true)
    @Mapping(target = "motivoAnulacion", ignore = true)
    @Mapping(target = "usuario", ignore = true)
    @Mapping(target = "pagos", ignore = true)
    @Mapping(target = "fechaCreacion", ignore = true)
    @Mapping(target = "fechaModificacion", ignore = true)
    Factura toEntity(FacturaRequestDTO requestDTO);

    /**
     * Convierte una Factura (Entity) a FacturaResponseDTO.
     *
     * @param factura Entidad
     * @return DTO de response
     */
    @Mapping(target = "idPropietario", source = "propietario.idPropietario")
    @Mapping(target = "nombrePropietario", expression = "java(getNombrePropietario(factura))")
    @Mapping(target = "idCita", source = "cita.idCita")
    @Mapping(target = "idUsuario", source = "usuario.idUsuario")
    @Mapping(target = "nombreUsuario", expression = "java(getNombreUsuario(factura))")
    @Mapping(target = "montoDescuento", expression = "java(factura.getMontoDescuento())")
    @Mapping(target = "montoImpuesto", expression = "java(factura.getMontoImpuesto())")
    @Mapping(target = "saldoPendiente", expression = "java(factura.getSaldoPendiente())")
    @Mapping(target = "fechaVencimiento", ignore = true)
    @Mapping(target = "estaVencida", ignore = true)
    @Mapping(target = "diasVencimiento", ignore = true)
    @Mapping(target = "esValida", expression = "java(factura.esValida())")
    @Mapping(target = "porcentajePagado", expression = "java(factura.getPorcentajePagado())")
    @Mapping(target = "cantidadDetalles", expression = "java(factura.getCantidadDetalles())")
    @Mapping(target = "cantidadPagos", expression = "java(factura.getCantidadPagos())")
    FacturaResponseDTO toResponseDTO(Factura factura);

    /**
     * Convierte una lista de Factura a lista de FacturaResponseDTO.
     *
     * @param facturas Lista de entidades
     * @return Lista de DTOs
     */
    List<FacturaResponseDTO> toResponseDTOList(List<Factura> facturas);

    /**
     * Actualiza una entidad Factura existente con los datos del DTO.
     *
     * @param requestDTO DTO con los datos actualizados
     * @param factura Entidad a actualizar
     */
    @Mapping(target = "idFactura", ignore = true)
    @Mapping(target = "numeroFactura", ignore = true)
    @Mapping(target = "propietario", ignore = true)
    @Mapping(target = "cita", ignore = true)
    @Mapping(target = "subtotal", ignore = true)
    @Mapping(target = "total", ignore = true)
    @Mapping(target = "montoPagado", ignore = true)
    @Mapping(target = "fechaPago", ignore = true)
    @Mapping(target = "anulada", ignore = true)
    @Mapping(target = "fechaAnulacion", ignore = true)
    @Mapping(target = "motivoAnulacion", ignore = true)
    @Mapping(target = "usuario", ignore = true)
    @Mapping(target = "detalles", ignore = true)
    @Mapping(target = "pagos", ignore = true)
    @Mapping(target = "fechaCreacion", ignore = true)
    @Mapping(target = "fechaModificacion", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDTO(FacturaRequestDTO requestDTO, @MappingTarget Factura factura);

    /**
     * Obtiene el nombre completo del propietario.
     *
     * @param factura Factura
     * @return Nombre completo del propietario
     */
    default String getNombrePropietario(Factura factura) {
        if (factura.getPropietario() == null) {
            return null;
        }
        Propietario p = factura.getPropietario();
        return p.getNombres() + " " + p.getApellidos();
    }

    /**
     * Obtiene el nombre del usuario.
     *
     * @param factura Factura
     * @return Nombre del usuario
     */
    default String getNombreUsuario(Factura factura) {
        if (factura.getUsuario() == null) {
            return null;
        }
        return factura.getUsuario().getNombreCompleto();
    }

    /**
     * Mapea el ID de propietario a la entidad Propietario.
     *
     * @param idPropietario ID del propietario
     * @return Entidad Propietario con solo el ID establecido
     */
    default Propietario mapIdToPropietario(Long idPropietario) {
        if (idPropietario == null) {
            return null;
        }
        Propietario propietario = new Propietario();
        propietario.setIdPropietario(idPropietario);
        return propietario;
    }

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
