package com.veterinaria.clinica_veternica.mapper.clinico;

import com.veterinaria.clinica_veternica.domain.clinico.EvolucionClinica;
import com.veterinaria.clinica_veternica.domain.usuario.Veterinario;
import com.veterinaria.clinica_veternica.dto.request.clinico.EvolucionClinicaRequestDTO;
import com.veterinaria.clinica_veternica.dto.response.clinico.EvolucionClinicaResponseDTO;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface EvolucionClinicaMapper {

    @Mapping(target = "idEvolucion", ignore = true)
    @Mapping(target = "historiaClinica", ignore = true)
    @Mapping(target = "veterinario", ignore = true)
    @Mapping(target = "fechaEvolucion", ignore = true)
    @Mapping(target = "fechaCreacion", ignore = true)
    EvolucionClinica toEntity(EvolucionClinicaRequestDTO requestDTO);

    @Mapping(target = "veterinario", source = "veterinario", qualifiedByName = "mapVeterinarioSimple")
    @Mapping(target = "temperaturaEnRangoNormal", source = "evolucion", qualifiedByName = "checkTemperatura")
    @Mapping(target = "signosVitalesEstables", source = "evolucion", qualifiedByName = "checkSignosVitales")
    EvolucionClinicaResponseDTO toResponseDTO(EvolucionClinica evolucion);

    List<EvolucionClinicaResponseDTO> toResponseDTOList(List<EvolucionClinica> evoluciones);

    @Named("mapVeterinarioSimple")
    default EvolucionClinicaResponseDTO.VeterinarioSimpleDTO mapVeterinarioSimple(Veterinario veterinario) {
        if (veterinario == null) return null;
        return EvolucionClinicaResponseDTO.VeterinarioSimpleDTO.builder()
                .idPersonal(veterinario.getIdPersonal())
                .nombreCompleto(veterinario.getNombres() + " " + veterinario.getApellidos())
                .especialidad(veterinario.getEspecialidad())
                .build();
    }

    @Named("checkTemperatura")
    default Boolean checkTemperatura(EvolucionClinica evolucion) {
        if (evolucion == null || evolucion.getTemperatura() == null) return false;
        return evolucion.getTemperatura() >= 37.5 && evolucion.getTemperatura() <= 39.2;
    }

    @Named("checkSignosVitales")
    default Boolean checkSignosVitales(EvolucionClinica evolucion) {
        if (evolucion == null) return false;
        boolean tempOk = evolucion.getTemperatura() == null ||
            (evolucion.getTemperatura() >= 37.5 && evolucion.getTemperatura() <= 39.2);
        boolean fcOk = evolucion.getFrecuenciaCardiaca() == null ||
            (evolucion.getFrecuenciaCardiaca() >= 60 && evolucion.getFrecuenciaCardiaca() <= 140);
        boolean frOk = evolucion.getFrecuenciaRespiratoria() == null ||
            (evolucion.getFrecuenciaRespiratoria() >= 10 && evolucion.getFrecuenciaRespiratoria() <= 30);
        return tempOk && fcOk && frOk;
    }
}
