package com.veterinaria.clinica_veternica.mapper.paciente;

import com.veterinaria.clinica_veternica.domain.paciente.Especie;
import com.veterinaria.clinica_veternica.domain.paciente.Mascota;
import com.veterinaria.clinica_veternica.domain.paciente.Propietario;
import com.veterinaria.clinica_veternica.domain.paciente.Raza;
import com.veterinaria.clinica_veternica.dto.request.paciente.MascotaRequestDTO;
import com.veterinaria.clinica_veternica.dto.response.paciente.MascotaResponseDTO;
import org.mapstruct.*;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;

/**
 * Mapper para convertir entre Mascota (Entity) y sus DTOs.
 * MapStruct genera automáticamente la implementación.
 *
 * @author Clínica Veterinaria Team
 * @version 1.0
 * @since 2025-11-03
 */
@Mapper(componentModel = "spring")
public interface MascotaMapper {

    /**
     * Convierte un MascotaRequestDTO a Mascota (Entity).
     *
     * @param requestDTO DTO de request
     * @return Entidad Mascota
     */
    @Mapping(target = "idMascota", ignore = true)
    @Mapping(target = "propietario", ignore = true)
    @Mapping(target = "especie", ignore = true)
    @Mapping(target = "raza", ignore = true)
    @Mapping(target = "citas", ignore = true)
    @Mapping(target = "historiaClinica", ignore = true)
    @Mapping(target = "fechaCreacion", ignore = true)
    @Mapping(target = "fechaModificacion", ignore = true)
    Mascota toEntity(MascotaRequestDTO requestDTO);

    /**
     * Convierte una Mascota (Entity) a MascotaResponseDTO.
     *
     * @param mascota Entidad
     * @return DTO de response
     */
    @Mapping(target = "edad", source = "fechaNacimiento", qualifiedByName = "calcularEdadDesdeNacimiento")
    @Mapping(target = "edadFormateada", source = "fechaNacimiento", qualifiedByName = "formatearEdadDesdeNacimiento")
    @Mapping(target = "propietario", source = "propietario", qualifiedByName = "mapPropietarioSimple")
    @Mapping(target = "especie", source = "especie", qualifiedByName = "mapEspecieSimple")
    @Mapping(target = "raza", source = "raza", qualifiedByName = "mapRazaSimple")
    @Mapping(target = "tieneHistoriaClinica", source = "historiaClinica", qualifiedByName = "tieneHistoria")
    @Mapping(target = "cantidadCitas", source = "citas", qualifiedByName = "contarCitas")
    MascotaResponseDTO toResponseDTO(Mascota mascota);

    /**
     * Convierte una lista de Mascota a lista de MascotaResponseDTO.
     *
     * @param mascotas Lista de entidades
     * @return Lista de DTOs
     */
    List<MascotaResponseDTO> toResponseDTOList(List<Mascota> mascotas);

    /**
     * Actualiza una entidad Mascota existente con los datos del DTO.
     *
     * @param requestDTO DTO con los datos actualizados
     * @param mascota Entidad a actualizar
     */
    @Mapping(target = "idMascota", ignore = true)
    @Mapping(target = "propietario", ignore = true)
    @Mapping(target = "especie", ignore = true)
    @Mapping(target = "raza", ignore = true)
    @Mapping(target = "citas", ignore = true)
    @Mapping(target = "historiaClinica", ignore = true)
    @Mapping(target = "fechaCreacion", ignore = true)
    @Mapping(target = "fechaModificacion", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDTO(MascotaRequestDTO requestDTO, @MappingTarget Mascota mascota);

    /**
     * Calcula la edad en años de la mascota.
     *
     * @param fechaNacimiento Fecha de nacimiento
     * @return Edad en años
     */
    @Named("calcularEdadDesdeNacimiento")
    default Integer calcularEdad(LocalDate fechaNacimiento) {
        if (fechaNacimiento == null) {
            return null;
        }
        return Period.between(fechaNacimiento, LocalDate.now()).getYears();
    }

    /**
     * Formatea la edad de la mascota en formato legible.
     *
     * @param fechaNacimiento Fecha de nacimiento
     * @return Edad formateada (ej: "3 años y 2 meses")
     */
    @Named("formatearEdadDesdeNacimiento")
    default String formatearEdad(LocalDate fechaNacimiento) {
        if (fechaNacimiento == null) {
            return null;
        }

        Period periodo = Period.between(fechaNacimiento, LocalDate.now());
        int anios = periodo.getYears();
        int meses = periodo.getMonths();
        int dias = periodo.getDays();

        if (anios == 0 && meses == 0) {
            return formatearDias(dias);
        } else if (anios == 0) {
            return formatearMeses(meses);
        } else if (meses == 0) {
            return formatearAnios(anios);
        } else {
            return formatearAnios(anios) + " y " + formatearMeses(meses);
        }
    }

    private String formatearDias(int dias) {
        return dias + (dias == 1 ? " día" : " días");
    }

    private String formatearMeses(int meses) {
        return meses + (meses == 1 ? " mes" : " meses");
    }

    private String formatearAnios(int anios) {
        return anios + (anios == 1 ? " año" : " años");
    }

    /**
     * Mapea Propietario a PropietarioSimpleDTO.
     *
     * @param propietario Propietario
     * @return PropietarioSimpleDTO
     */
    @Named("mapPropietarioSimple")
    default MascotaResponseDTO.PropietarioSimpleDTO mapPropietarioSimple(Propietario propietario) {
        if (propietario == null) {
            return null;
        }
        return MascotaResponseDTO.PropietarioSimpleDTO.builder()
                .idPropietario(propietario.getIdPropietario())
                .nombreCompleto(propietario.getNombres() + " " + propietario.getApellidos())
                .telefono(propietario.getTelefono())
                .email(propietario.getEmail())
                .build();
    }

    /**
     * Mapea Especie a EspecieSimpleDTO.
     *
     * @param especie Especie
     * @return EspecieSimpleDTO
     */
    @Named("mapEspecieSimple")
    default MascotaResponseDTO.EspecieSimpleDTO mapEspecieSimple(Especie especie) {
        if (especie == null) {
            return null;
        }
        return MascotaResponseDTO.EspecieSimpleDTO.builder()
                .idEspecie(especie.getIdEspecie())
                .nombre(especie.getNombre())
                .build();
    }

    /**
     * Mapea Raza a RazaSimpleDTO.
     *
     * @param raza Raza
     * @return RazaSimpleDTO
     */
    @Named("mapRazaSimple")
    default MascotaResponseDTO.RazaSimpleDTO mapRazaSimple(Raza raza) {
        if (raza == null) {
            return null;
        }
        return MascotaResponseDTO.RazaSimpleDTO.builder()
                .idRaza(raza.getIdRaza())
                .nombre(raza.getNombre())
                .build();
    }

    /**
     * Verifica si la mascota tiene historia clínica.
     *
     * @param historiaClinica Historia clínica
     * @return true si tiene historia clínica
     */
    @Named("tieneHistoria")
    default Boolean tieneHistoriaClinica(Object historiaClinica) {
        return historiaClinica != null;
    }

    /**
     * Cuenta la cantidad de citas.
     *
     * @param citas Lista de citas
     * @return Cantidad de citas
     */
    @Named("contarCitas")
    default Integer contarCitas(List<?> citas) {
        return citas != null ? citas.size() : 0;
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

    /**
     * Mapea el ID de raza a la entidad Raza.
     *
     * @param idRaza ID de la raza
     * @return Entidad Raza con solo el ID establecido
     */
    default Raza mapIdToRaza(Long idRaza) {
        if (idRaza == null) {
            return null;
        }
        Raza raza = new Raza();
        raza.setIdRaza(idRaza);
        return raza;
    }
}
