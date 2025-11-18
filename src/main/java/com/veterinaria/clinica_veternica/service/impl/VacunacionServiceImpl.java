package com.veterinaria.clinica_veternica.service.impl;

import com.veterinaria.clinica_veternica.domain.clinico.HistoriaClinica;
import com.veterinaria.clinica_veternica.domain.clinico.Vacunacion;
import com.veterinaria.clinica_veternica.domain.usuario.Veterinario;
import com.veterinaria.clinica_veternica.dto.request.clinico.VacunacionRequestDTO;
import com.veterinaria.clinica_veternica.dto.response.clinico.VacunacionResponseDTO;
import com.veterinaria.clinica_veternica.exception.ResourceNotFoundException;
import com.veterinaria.clinica_veternica.mapper.clinico.VacunacionMapper;
import com.veterinaria.clinica_veternica.repository.HistoriaClinicaRepository;
import com.veterinaria.clinica_veternica.repository.VacunacionRepository;
import com.veterinaria.clinica_veternica.repository.VeterinarioRepository;
import com.veterinaria.clinica_veternica.service.interfaces.IVacunacionService;
import com.veterinaria.clinica_veternica.util.Constants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Implementación del servicio para gestión de Vacunaciones.
 * Sigue los principios SOLID:
 * - SRP: Responsabilidad única de gestionar vacunaciones
 * - DIP: Depende de abstracciones (repositorios, mappers)
 *
 * @author Clínica Veterinaria Team
 * @version 1.0
 * @since 2025-11-15
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class VacunacionServiceImpl implements IVacunacionService {

    private final VacunacionRepository vacunacionRepository;
    private final HistoriaClinicaRepository historiaClinicaRepository;
    private final VeterinarioRepository veterinarioRepository;
    private final VacunacionMapper vacunacionMapper;

    @Override
    public VacunacionResponseDTO crear(Long idHistoriaClinica, VacunacionRequestDTO requestDTO) {
        log.info("Creando nueva vacunación para historia clínica ID: {}", idHistoriaClinica);

        // Validar que la historia clínica existe
        HistoriaClinica historiaClinica = historiaClinicaRepository.findById(idHistoriaClinica)
                .orElseThrow(() -> new ResourceNotFoundException(Constants.ENTIDAD_HISTORIA_CLINICA, "id", idHistoriaClinica));

        // Validar que el veterinario existe
        Veterinario veterinario = veterinarioRepository.findById(requestDTO.getIdVeterinario())
                .orElseThrow(() -> new ResourceNotFoundException(Constants.ENTIDAD_VETERINARIO, "id", requestDTO.getIdVeterinario()));

        // Mapear DTO a entidad
        Vacunacion vacunacion = vacunacionMapper.toEntity(requestDTO);
        
        // Establecer relaciones
        vacunacion.setHistoriaClinica(historiaClinica);
        vacunacion.setMascota(historiaClinica.getMascota());
        vacunacion.setVeterinario(veterinario);
        
        // Establecer enfermedades prevenidas por defecto si no se proporciona
        if (vacunacion.getEnfermedadesPrevenidas() == null || vacunacion.getEnfermedadesPrevenidas().isBlank()) {
            String enfermedades = obtenerEnfermedadesPorTipoVacuna(requestDTO.getNombreVacuna());
            vacunacion.setEnfermedadesPrevenidas(enfermedades);
        }
        
        // Establecer sitio de aplicación si no se proporciona
        if (vacunacion.getSitioAplicacion() == null || vacunacion.getSitioAplicacion().isBlank()) {
            vacunacion.setSitioAplicacion("Cuello");
        }
        
        // Establecer esquema completo si no se proporciona
        if (vacunacion.getEsquemaCompleto() == null) {
            vacunacion.setEsquemaCompleto(!Boolean.FALSE.equals(requestDTO.getEsquemaCompleto()));
        }
        
        // Establecer número de dosis si no se proporciona
        if (vacunacion.getNumeroDosis() == null) {
            vacunacion.setNumeroDosis(1);
        }
        
        // Establecer total de dosis en esquema si no se proporciona
        if (vacunacion.getTotalDosisEsquema() == null) {
            vacunacion.setTotalDosisEsquema(1);
        }

        Vacunacion vacunacionGuardada = vacunacionRepository.save(vacunacion);
        log.info("Vacunación creada exitosamente con ID: {}", vacunacionGuardada.getIdVacunacion());
        
        return vacunacionMapper.toResponseDTO(vacunacionGuardada);
    }

    @Override
    @Transactional(readOnly = true)
    public List<VacunacionResponseDTO> listarPorHistoriaClinica(Long idHistoriaClinica) {
        log.info("Listando vacunaciones para historia clínica ID: {}", idHistoriaClinica);

        HistoriaClinica historiaClinica = historiaClinicaRepository.findById(idHistoriaClinica)
                .orElseThrow(() -> new ResourceNotFoundException(Constants.ENTIDAD_HISTORIA_CLINICA, "id", idHistoriaClinica));

        List<Vacunacion> vacunaciones = vacunacionRepository.findByHistoriaClinica(historiaClinica);
        return vacunacionMapper.toResponseDTOList(vacunaciones);
    }

    /**
     * Obtiene las enfermedades prevenidas según el tipo de vacuna.
     */
    private String obtenerEnfermedadesPorTipoVacuna(String nombreVacuna) {
        if (nombreVacuna == null || nombreVacuna.isBlank()) {
            return "Enfermedades prevenidas según tipo de vacuna";
        }
        
        String nombreLower = nombreVacuna.toLowerCase();
        if (nombreLower.contains("antirrábica") || nombreLower.contains("antirrabica")) {
            return "Rabia";
        } else if (nombreLower.contains("polivalente") || nombreLower.contains("múltiple")) {
            return "Moquillo, Parvovirus, Hepatitis, Parainfluenza";
        } else if (nombreLower.contains("leptospirosis")) {
            return "Leptospirosis";
        } else if (nombreLower.contains("hepatitis")) {
            return "Hepatitis canina";
        } else {
            return "Enfermedades prevenidas según tipo de vacuna";
        }
    }
}

