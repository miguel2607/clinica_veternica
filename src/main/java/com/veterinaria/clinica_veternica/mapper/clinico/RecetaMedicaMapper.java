package com.veterinaria.clinica_veternica.mapper.clinico;

import com.veterinaria.clinica_veternica.domain.clinico.RecetaMedica;
import com.veterinaria.clinica_veternica.domain.usuario.Veterinario;
import com.veterinaria.clinica_veternica.dto.request.clinico.RecetaMedicaRequestDTO;
import com.veterinaria.clinica_veternica.dto.response.clinico.RecetaMedicaResponseDTO;
import org.mapstruct.*;

import java.time.LocalDate;
import java.util.List;

@Mapper(componentModel = "spring")
public interface RecetaMedicaMapper {

    @Mapping(target = "idReceta", ignore = true)
    @Mapping(target = "historiaClinica", ignore = true)
    @Mapping(target = "veterinario", ignore = true)
    @Mapping(target = "numeroReceta", ignore = true)
    @Mapping(target = "fechaVigencia", ignore = true)
    @Mapping(target = "dispensada", ignore = true)
    @Mapping(target = "fechaCreacion", ignore = true)
    RecetaMedica toEntity(RecetaMedicaRequestDTO requestDTO);

    @Mapping(target = "veterinario", source = "veterinario", qualifiedByName = "mapVeterinarioSimple")
    @Mapping(target = "vigente", source = "receta", qualifiedByName = "calcularVigente")
    RecetaMedicaResponseDTO toResponseDTO(RecetaMedica receta);

    List<RecetaMedicaResponseDTO> toResponseDTOList(List<RecetaMedica> recetas);

    @Named("mapVeterinarioSimple")
    default RecetaMedicaResponseDTO.VeterinarioSimpleDTO mapVeterinarioSimple(Veterinario veterinario) {
        if (veterinario == null) return null;
        return RecetaMedicaResponseDTO.VeterinarioSimpleDTO.builder()
                .idPersonal(veterinario.getIdPersonal())
                .nombreCompleto(veterinario.getNombres() + " " + veterinario.getApellidos())
                .registroProfesional(veterinario.getRegistroProfesional())
                .build();
    }

    @Named("calcularVigente")
    default Boolean calcularVigente(RecetaMedica receta) {
        if (receta == null || receta.getFechaVigencia() == null) {
            return false;
        }
        return !receta.getFechaVigencia().isBefore(LocalDate.now());
    }
}
