package com.veterinaria.clinica_veternica.mapper.paciente;

import com.veterinaria.clinica_veternica.domain.paciente.Especie;
import com.veterinaria.clinica_veternica.domain.paciente.Raza;
import com.veterinaria.clinica_veternica.dto.request.paciente.RazaRequestDTO;
import com.veterinaria.clinica_veternica.dto.response.paciente.RazaResponseDTO;
import org.mapstruct.*;

import java.util.List;

/**
 * Mapper para convertir entre Raza (Entity) y sus DTOs.
 * MapStruct genera automáticamente la implementación.
 *
 * @author Clínica Veterinaria Team
 * @version 1.0
 * @since 2025-11-03
 */
@Mapper(componentModel = "spring")
public interface RazaMapper {

    /**
     * Convierte un RazaRequestDTO a Raza (Entity).
     *
     * @param requestDTO DTO de request
     * @return Entidad Raza
     */
    @Mapping(target = "idRaza", ignore = true)
    @Mapping(target = "especie", ignore = true)
    @Mapping(target = "mascotas", ignore = true)
    @Mapping(target = "fechaCreacion", ignore = true)
    @Mapping(target = "fechaModificacion", ignore = true)
    Raza toEntity(RazaRequestDTO requestDTO);

    /**
     * Convierte una Raza (Entity) a RazaResponseDTO.
     *
     * @param raza Entidad
     * @return DTO de response
     */
    @Mapping(target = "especie.idEspecie", source = "especie.idEspecie")
    @Mapping(target = "especie.nombre", source = "especie.nombre")
    @Mapping(target = "cantidadMascotas", expression = "java(raza.getCantidadMascotas())")
    RazaResponseDTO toResponseDTO(Raza raza);

    /**
     * Convierte una lista de Raza a lista de RazaResponseDTO.
     *
     * @param razas Lista de entidades
     * @return Lista de DTOs
     */
    List<RazaResponseDTO> toResponseDTOList(List<Raza> razas);

    /**
     * Actualiza una entidad Raza existente con los datos del DTO.
     *
     * @param requestDTO DTO con los datos actualizados
     * @param raza Entidad a actualizar
     */
    @Mapping(target = "idRaza", ignore = true)
    @Mapping(target = "especie", ignore = true)
    @Mapping(target = "mascotas", ignore = true)
    @Mapping(target = "fechaCreacion", ignore = true)
    @Mapping(target = "fechaModificacion", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDTO(RazaRequestDTO requestDTO, @MappingTarget Raza raza);

    /**
     * Mapea el ID de especie a la entidad Especie.
     *
     * @param idEspecie ID de la especie
     * @return Entidad Especie con solo el ID establecido
     */
    default Especie mapIdToEspecie(Long idEspecie) {
        if (idEspecie == null) {
            return null;
        }
        Especie especie = new Especie();
        especie.setIdEspecie(idEspecie);
        return especie;
    }
}
