package com.veterinaria.clinica_veternica.mapper.agenda;

import com.veterinaria.clinica_veternica.domain.agenda.CategoriaServicio;
import com.veterinaria.clinica_veternica.domain.agenda.Servicio;
import com.veterinaria.clinica_veternica.domain.agenda.TipoServicio;
import com.veterinaria.clinica_veternica.dto.request.agenda.ServicioRequestDTO;
import com.veterinaria.clinica_veternica.dto.response.agenda.ServicioResponseDTO;
import org.mapstruct.*;

import java.util.List;

/**
 * Mapper para convertir entre Servicio (Entity) y sus DTO_s.
 *
 * @author Clínica Veterinaria Team
 * @version 1.0
 * @since 2025-11-03
 */
@Mapper(componentModel = "spring")
public interface ServicioMapper {

    /**
     * Convierte un ServicioRequestDTO a Servicio (Entity).
     *
     * @param requestDTO DTO de request
     * @return Entidad Servicio
     */
    @Mapping(target = "idServicio", ignore = true)
    @Mapping(target = "tipoServicio", source = "tipoServicio", qualifiedByName = "stringToTipoServicio")
    @Mapping(target = "categoria", source = "categoria", qualifiedByName = "stringToCategoria")
    @Mapping(target = "duracionEstimadaMinutos", source = "duracionMinutos")
    @Mapping(target = "citas", ignore = true)
    @Mapping(target = "fechaCreacion", ignore = true)
    @Mapping(target = "fechaModificacion", ignore = true)
    Servicio toEntity(ServicioRequestDTO requestDTO);

    /**
     * Convierte un Servicio (Entity) a ServicioResponseDTO.
     *
     * @param servicio Entidad
     * @return DTO de response
     */
    @Mapping(target = "tipoServicio", source = "tipoServicio", qualifiedByName = "tipoServicioToString")
    @Mapping(target = "categoria", source = "categoria", qualifiedByName = "categoriaToString")
    @Mapping(target = "duracionMinutos", source = "duracionEstimadaMinutos")
    @Mapping(target = "duracionFormateada", source = "duracionEstimadaMinutos", qualifiedByName = "formatearDuracion")
    @Mapping(target = "cantidadCitas", source = "citas", qualifiedByName = "contarCitas")
    ServicioResponseDTO toResponseDTO(Servicio servicio);

    /**
     * Convierte una lista de Servicio a lista de ServicioResponseDTO.
     *
     * @param servicios Lista de entidades
     * @return Lista de DTOs
     */
    List<ServicioResponseDTO> toResponseDTOList(List<Servicio> servicios);

    /**
     * Actualiza una entidad Servicio existente con los datos del DTO.
     *
     * @param requestDTO DTO con los datos actualizados
     * @param servicio Entidad a actualizar
     */
    @Mapping(target = "idServicio", ignore = true)
    @Mapping(target = "tipoServicio", source = "tipoServicio", qualifiedByName = "stringToTipoServicio")
    @Mapping(target = "categoria", source = "categoria", qualifiedByName = "stringToCategoria")
    @Mapping(target = "duracionEstimadaMinutos", source = "duracionMinutos")
    @Mapping(target = "citas", ignore = true)
    @Mapping(target = "fechaCreacion", ignore = true)
    @Mapping(target = "fechaModificacion", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDTO(ServicioRequestDTO requestDTO, @MappingTarget Servicio servicio);

    @Named("stringToTipoServicio")
    default TipoServicio stringToTipoServicio(String tipoServicio) {
        if (tipoServicio == null) return null;
        return TipoServicio.valueOf(tipoServicio.toUpperCase());
    }

    @Named("stringToCategoria")
    default CategoriaServicio stringToCategoria(String categoria) {
        if (categoria == null) return null;
        return CategoriaServicio.valueOf(categoria.toUpperCase());
    }

    @Named("tipoServicioToString")
    default String tipoServicioToString(TipoServicio tipoServicio) {
        if (tipoServicio == null) return null;
        return tipoServicio.name();
    }

    @Named("categoriaToString")
    default String categoriaToString(CategoriaServicio categoria) {
        if (categoria == null) return null;
        return categoria.name();
    }

    @Named("formatearDuracion")
    default String formatearDuracion(Integer minutos) {
        if (minutos == null || minutos == 0) return null;
        int horas = minutos / 60;
        int mins = minutos % 60;

        if (horas == 0) {
            return mins + " minutos";
        } else if (mins == 0) {
            return horas + (horas == 1 ? " hora" : " horas");
        } else {
            return horas + (horas == 1 ? " hora" : " horas") + " " + mins + " minutos";
        }
    }

    @Named("contarCitas")
    default Integer contarCitas(List<?> citas) {
        return citas != null ? citas.size() : 0;
    }
}
