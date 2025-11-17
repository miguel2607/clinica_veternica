package com.veterinaria.clinica_veternica.mapper.clinico;

import com.veterinaria.clinica_veternica.domain.clinico.HistoriaClinica;
import com.veterinaria.clinica_veternica.domain.paciente.Mascota;
import com.veterinaria.clinica_veternica.dto.request.clinico.HistoriaClinicaRequestDTO;
import com.veterinaria.clinica_veternica.dto.response.clinico.HistoriaClinicaResponseDTO;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface HistoriaClinicaMapper {

    @Mapping(target = "idHistoriaClinica", ignore = true)
    @Mapping(target = "mascota", ignore = true)
    @Mapping(target = "evoluciones", ignore = true)
    @Mapping(target = "vacunaciones", ignore = true)
    @Mapping(target = "fechaCreacion", ignore = true)
    @Mapping(target = "fechaModificacion", ignore = true)
    HistoriaClinica toEntity(HistoriaClinicaRequestDTO requestDTO);

    @Mapping(target = "mascota", source = "mascota", qualifiedByName = "mapMascotaSimple")
    @Mapping(target = "cantidadEvoluciones", source = "evoluciones", qualifiedByName = "contarLista")
    @Mapping(target = "cantidadVacunas", source = "vacunaciones", qualifiedByName = "contarLista")
    HistoriaClinicaResponseDTO toResponseDTO(HistoriaClinica historia);

    List<HistoriaClinicaResponseDTO> toResponseDTOList(List<HistoriaClinica> historias);

    @Mapping(target = "idHistoriaClinica", ignore = true)
    @Mapping(target = "mascota", ignore = true)
    @Mapping(target = "evoluciones", ignore = true)
    @Mapping(target = "vacunaciones", ignore = true)
    @Mapping(target = "fechaCreacion", ignore = true)
    @Mapping(target = "fechaModificacion", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDTO(HistoriaClinicaRequestDTO requestDTO, @MappingTarget HistoriaClinica historia);

    @Named("mapMascotaSimple")
    default HistoriaClinicaResponseDTO.MascotaSimpleDTO mapMascotaSimple(Mascota mascota) {
        if (mascota == null) return null;
        return HistoriaClinicaResponseDTO.MascotaSimpleDTO.builder()
                .idMascota(mascota.getIdMascota())
                .nombre(mascota.getNombre())
                .especie(mascota.getEspecie() != null ? mascota.getEspecie().getNombre() : null)
                .propietarioNombre(mascota.getPropietario() != null ?
                    mascota.getPropietario().getNombres() + " " + mascota.getPropietario().getApellidos() : null)
                .build();
    }

    @Named("contarLista")
    default Integer contarLista(List<?> lista) {
        return lista != null ? lista.size() : 0;
    }
}
