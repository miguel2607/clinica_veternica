package com.veterinaria.clinica_veternica.patterns.structural.facade;

import com.veterinaria.clinica_veternica.dto.response.facade.AlertasMedicasDTO;
import com.veterinaria.clinica_veternica.dto.response.facade.BusquedaGlobalDTO;
import com.veterinaria.clinica_veternica.dto.response.paciente.MascotaResponseDTO;
import com.veterinaria.clinica_veternica.dto.response.paciente.PropietarioResponseDTO;
import com.veterinaria.clinica_veternica.dto.response.usuario.VeterinarioResponseDTO;
import com.veterinaria.clinica_veternica.service.interfaces.IMascotaService;
import com.veterinaria.clinica_veternica.service.interfaces.IPropietarioService;
import com.veterinaria.clinica_veternica.service.interfaces.IVeterinarioService;
import com.veterinaria.clinica_veternica.util.ValidationHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Locale;

/**
 * Servicio especializado para operaciones de Búsqueda.
 * Parte de la división del antipatrón God Object (ClinicaFacade).
 *
 * <p>Responsabilidades:</p>
 * <ul>
 *   <li>Búsqueda global en múltiples entidades</li>
 *   <li>Identificar mascotas con alertas médicas</li>
 *   <li>Búsquedas avanzadas con filtros</li>
 * </ul>
 *
 * @author Clínica Veterinaria Team
 * @version 2.0
 * @since 2025-11-17
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BusquedaFacadeService {

    private final IMascotaService mascotaService;
    private final IPropietarioService propietarioService;
    private final IVeterinarioService veterinarioService;
    private final ValidationHelper validationHelper;

    /**
     * Realiza una búsqueda global en mascotas, propietarios y veterinarios.
     *
     * @param termino Término de búsqueda
     * @return BusquedaGlobalDTO con resultados de todas las entidades
     */
    public BusquedaGlobalDTO busquedaGlobal(String termino) {
        log.info("BusquedaFacadeService: Búsqueda global con término: {}", termino);

        // Validar y sanitizar término de búsqueda
        String terminoSanitizado = validationHelper.validateAndSanitizeSearchTerm(termino, 100);

        // Buscar en todas las entidades en paralelo
        List<MascotaResponseDTO> mascotas = mascotaService.buscarPorNombre(terminoSanitizado);
        List<PropietarioResponseDTO> propietarios = propietarioService.buscarPorNombre(terminoSanitizado);
        List<VeterinarioResponseDTO> veterinarios = veterinarioService.buscarPorNombre(terminoSanitizado);

        int totalResultados = mascotas.size() + propietarios.size() + veterinarios.size();

        log.info("Búsqueda global completada: {} mascotas, {} propietarios, {} veterinarios (Total: {})",
                mascotas.size(), propietarios.size(), veterinarios.size(), totalResultados);

        return BusquedaGlobalDTO.builder()
                .terminoBusqueda(terminoSanitizado)
                .mascotas(mascotas)
                .totalMascotas(mascotas.size())
                .propietarios(propietarios)
                .totalPropietarios(propietarios.size())
                .veterinarios(veterinarios)
                .totalVeterinarios(veterinarios.size())
                .totalResultados(totalResultados)
                .build();
    }

    /**
     * Obtiene mascotas que requieren atención médica.
     * Identifica mascotas con:
     * - Edad avanzada (>= 10 años para perros, >= 12 para gatos)
     * - Necesitan control anual
     *
     * @return AlertasMedicasDTO con mascotas que requieren atención
     */
    public AlertasMedicasDTO obtenerMascotasConAlertasMedicas() {
        log.info("BusquedaFacadeService: Identificando mascotas con alertas médicas");

        List<MascotaResponseDTO> todasMascotas = mascotaService.listarActivas();

        // Filtrar mascotas que requieren atención
        List<MascotaResponseDTO> mascotasConAlertas = todasMascotas.stream()
                .filter(this::requiereAtencionMedica)
                .toList();

        log.info("Alertas médicas: {} mascotas de {} totales requieren atención",
                mascotasConAlertas.size(), todasMascotas.size());

        return AlertasMedicasDTO.builder()
                .mascotasConAlertas(mascotasConAlertas)
                .totalAlertas(mascotasConAlertas.size())
                .mensaje(generarMensajeAlertas(mascotasConAlertas.size()))
                .build();
    }

    /**
     * Determina si una mascota requiere atención médica.
     * Criterios:
     * - Edad avanzada según especie
     *
     * @param mascota Mascota a evaluar
     * @return true si requiere atención
     */
    private boolean requiereAtencionMedica(MascotaResponseDTO mascota) {
        if (mascota.getFechaNacimiento() == null) {
            return false;
        }

        LocalDate hoy = LocalDate.now();
        int edad = hoy.getYear() - mascota.getFechaNacimiento().getYear();

        String especie = mascota.getEspecie() != null ? mascota.getEspecie().getNombre() : "";

        return switch (clasificarEspecie(especie)) {
            case PERRO -> edad >= 10; // Perros mayores de 10 años
            case GATO -> edad >= 12; // Gatos mayores de 12 años
            case OTRA -> edad >= 8;   // Otras especies
        };
    }

    /**
     * Genera mensaje descriptivo sobre las alertas.
     *
     * @param cantidadAlertas Cantidad de mascotas con alertas
     * @return Mensaje descriptivo
     */
    private String generarMensajeAlertas(int cantidadAlertas) {
        return switch (cantidadAlertas) {
            case 0 -> "No hay mascotas con alertas médicas actualmente";
            case 1 -> "1 mascota requiere atención médica";
            default -> cantidadAlertas + " mascotas requieren atención médica (edad avanzada)";
        };
    }

    private EspecieCategoria clasificarEspecie(String especie) {
        if (especie == null) {
            return EspecieCategoria.OTRA;
        }
        String especieNormalizada = especie.toLowerCase(Locale.ROOT);
        if (especieNormalizada.contains("perro")) {
            return EspecieCategoria.PERRO;
        }
        if (especieNormalizada.contains("gato")) {
            return EspecieCategoria.GATO;
        }
        return EspecieCategoria.OTRA;
    }

    private enum EspecieCategoria {
        PERRO, GATO, OTRA
    }
}
