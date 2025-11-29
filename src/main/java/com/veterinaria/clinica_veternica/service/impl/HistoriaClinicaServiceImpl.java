package com.veterinaria.clinica_veternica.service.impl;

import com.veterinaria.clinica_veternica.domain.clinico.HistoriaClinica;
import com.veterinaria.clinica_veternica.domain.paciente.Mascota;
import com.veterinaria.clinica_veternica.dto.request.clinico.HistoriaClinicaRequestDTO;
import com.veterinaria.clinica_veternica.dto.response.clinico.HistoriaClinicaResponseDTO;
import com.veterinaria.clinica_veternica.exception.ResourceNotFoundException;
import com.veterinaria.clinica_veternica.exception.UnauthorizedException;
import com.veterinaria.clinica_veternica.exception.ValidationException;
import com.veterinaria.clinica_veternica.mapper.clinico.HistoriaClinicaMapper;
import com.veterinaria.clinica_veternica.patterns.behavioral.memento.HistoriaClinicaCaretaker;
import com.veterinaria.clinica_veternica.patterns.creational.builder.HistoriaClinicaBuilder;
import com.veterinaria.clinica_veternica.patterns.structural.proxy.HistoriaClinicaProxy;
import com.veterinaria.clinica_veternica.repository.HistoriaClinicaRepository;
import com.veterinaria.clinica_veternica.repository.MascotaRepository;
import com.veterinaria.clinica_veternica.service.interfaces.IHistoriaClinicaService;
import com.veterinaria.clinica_veternica.util.Constants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Implementación del servicio para gestión de Historias Clínicas.
 * Utiliza los patrones:
 * - Builder: Para construir historias clínicas complejas
 * - Memento: Para guardar y restaurar estados previos
 * - Proxy: Para control de acceso y auditoría
 *
 * @author Clínica Veterinaria Team
 * @version 1.0
 * @since 2025-11-06
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class HistoriaClinicaServiceImpl implements IHistoriaClinicaService {

    private final HistoriaClinicaRepository historiaClinicaRepository;
    private final MascotaRepository mascotaRepository;
    private final HistoriaClinicaMapper historiaClinicaMapper;
    private final HistoriaClinicaCaretaker historiaClinicaCaretaker;
    private final HistoriaClinicaProxy historiaClinicaProxy;

    @Override
    public HistoriaClinicaResponseDTO crear(HistoriaClinicaRequestDTO requestDTO) {
        log.info("Creando nueva historia clínica para mascota ID: {}", requestDTO.getIdMascota());

        // Validar que la mascota existe
        Mascota mascota = mascotaRepository.findById(requestDTO.getIdMascota())
                .orElseThrow(() -> new ResourceNotFoundException(Constants.ENTIDAD_MASCOTA, "id", requestDTO.getIdMascota()));

        // Validar que no exista ya una historia clínica para esta mascota
        if (historiaClinicaRepository.findByMascota(mascota).isPresent()) {
            throw new ValidationException(
                    "Ya existe una historia clínica para esta mascota",
                    "mascota",
                    "Cada mascota solo puede tener una historia clínica"
            );
        }

        HistoriaClinica historiaClinica = historiaClinicaMapper.toEntity(requestDTO);
        historiaClinica.setMascota(mascota);

        HistoriaClinica historiaGuardada = historiaClinicaRepository.save(historiaClinica);

        // Guardar memento inicial
        historiaClinicaCaretaker.guardarMemento(historiaGuardada);

        log.info("Historia clínica creada exitosamente con ID: {}", historiaGuardada.getIdHistoriaClinica());
        return historiaClinicaMapper.toResponseDTO(historiaGuardada);
    }

    @Override
    public HistoriaClinicaResponseDTO crearConBuilder(Long idMascota, HistoriaClinicaRequestDTO requestDTO) {
        log.info("Creando historia clínica con Builder para mascota ID: {}", idMascota);

        Mascota mascota = mascotaRepository.findById(idMascota)
                .orElseThrow(() -> new ResourceNotFoundException(Constants.ENTIDAD_MASCOTA, "id", idMascota));

        if (historiaClinicaRepository.findByMascota(mascota).isPresent()) {
            throw new ValidationException(
                    "Ya existe una historia clínica para esta mascota",
                    "mascota",
                    "Cada mascota solo puede tener una historia clínica"
            );
        }

        // Usar Builder para construir la historia clínica
        HistoriaClinica historiaClinica = new HistoriaClinicaBuilder()
                .conMascota(mascota)
                .conAlergias(requestDTO.getAlergias())
                .conEnfermedadesCronicas(requestDTO.getEnfermedadesCronicas())
                .conMedicamentosActuales(requestDTO.getMedicamentosActuales())
                .conObservacionesGenerales(requestDTO.getObservaciones())
                .build();

        HistoriaClinica historiaGuardada = historiaClinicaRepository.save(historiaClinica);

        // Guardar memento inicial
        historiaClinicaCaretaker.guardarMemento(historiaGuardada);

        log.info("Historia clínica creada con Builder exitosamente con ID: {}", historiaGuardada.getIdHistoriaClinica());
        return historiaClinicaMapper.toResponseDTO(historiaGuardada);
    }

    @Override
    public HistoriaClinicaResponseDTO actualizar(Long id, HistoriaClinicaRequestDTO requestDTO) {
        log.info("Actualizando historia clínica ID: {}", id);

        HistoriaClinica historiaClinica = historiaClinicaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(Constants.ENTIDAD_HISTORIA_CLINICA, "id", id));

        // Usar Proxy para verificar permisos de modificación
        if (!historiaClinicaProxy.tienePermisoEscritura(historiaClinica)) {
            throw new UnauthorizedException("No tiene permisos para modificar esta historia clínica");
        }

        // Guardar memento antes de actualizar (Memento pattern)
        historiaClinicaCaretaker.guardarMemento(historiaClinica);

        historiaClinicaMapper.updateEntityFromDTO(requestDTO, historiaClinica);
        HistoriaClinica historiaActualizada = historiaClinicaRepository.save(historiaClinica);

        log.info("Historia clínica actualizada exitosamente");
        return historiaClinicaMapper.toResponseDTO(historiaActualizada);
    }

    @Override
    @Transactional(readOnly = true)
    public HistoriaClinicaResponseDTO buscarPorId(Long id) {
        HistoriaClinica historiaClinica = historiaClinicaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(Constants.ENTIDAD_HISTORIA_CLINICA, "id", id));

        // Usar Proxy para control de acceso y auditoría
        if (!historiaClinicaProxy.tienePermisoLectura(historiaClinica)) {
            throw new UnauthorizedException("No tiene permisos para acceder a esta historia clínica");
        }

        return historiaClinicaMapper.toResponseDTO(historiaClinica);
    }

    @Override
    @Transactional(readOnly = true)
    public HistoriaClinicaResponseDTO buscarPorMascota(Long idMascota) {
        Mascota mascota = mascotaRepository.findById(idMascota)
                .orElseThrow(() -> new ResourceNotFoundException(Constants.ENTIDAD_MASCOTA, "id", idMascota));

        HistoriaClinica historiaClinica = historiaClinicaRepository.findByMascota(mascota)
                .orElseThrow(() -> new ResourceNotFoundException(Constants.ENTIDAD_HISTORIA_CLINICA, "mascota", idMascota));

        // Usar Proxy para control de acceso y auditoría
        if (!historiaClinicaProxy.tienePermisoLectura(historiaClinica)) {
            throw new UnauthorizedException("No tiene permisos para acceder a esta historia clínica");
        }

        return historiaClinicaMapper.toResponseDTO(historiaClinica);
    }

    @Override
    @Transactional(readOnly = true)
    public List<HistoriaClinicaResponseDTO> listarTodos() {
        List<HistoriaClinica> historias = historiaClinicaRepository.findAllWithMascotaAndPropietario();
        return historiaClinicaMapper.toResponseDTOList(historias);
    }

    @Override
    @Transactional(readOnly = true)
    public List<HistoriaClinicaResponseDTO> listarActivas() {
        List<HistoriaClinica> historias = historiaClinicaRepository.findHistoriasActivas();
        return historiaClinicaMapper.toResponseDTOList(historias);
    }

    @Override
    public void guardarMemento(Long id) {
        log.info("Guardando memento de historia clínica ID: {}", id);
        HistoriaClinica historiaClinica = historiaClinicaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(Constants.ENTIDAD_HISTORIA_CLINICA, "id", id));

        historiaClinicaCaretaker.guardarMemento(historiaClinica);
        log.info("Memento guardado exitosamente");
    }

    @Override
    public boolean restaurarUltimoMemento(Long id) {
        log.info("Restaurando último memento de historia clínica ID: {}", id);
        HistoriaClinica historiaClinica = historiaClinicaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(Constants.ENTIDAD_HISTORIA_CLINICA, "id", id));

        boolean restaurado = historiaClinicaCaretaker.restaurarUltimoMemento(historiaClinica);
        if (restaurado) {
            historiaClinicaRepository.save(historiaClinica);
            log.info("Historia clínica restaurada exitosamente");
        }
        return restaurado;
    }

    @Override
    public boolean restaurarMemento(Long id, int indice) {
        log.info("Restaurando memento índice {} de historia clínica ID: {}", indice, id);
        HistoriaClinica historiaClinica = historiaClinicaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(Constants.ENTIDAD_HISTORIA_CLINICA, "id", id));

        boolean restaurado = historiaClinicaCaretaker.restaurarMemento(historiaClinica, indice);
        if (restaurado) {
            historiaClinicaRepository.save(historiaClinica);
            log.info("Historia clínica restaurada exitosamente");
        }
        return restaurado;
    }

    @Override
    @Transactional(readOnly = true)
    public int obtenerCantidadMementos(Long id) {
        return historiaClinicaCaretaker.obtenerCantidadMementos(id);
    }

    @Override
    public void archivar(Long id, String motivo) {
        log.info("Archivando historia clínica ID: {}", id);
        HistoriaClinica historiaClinica = historiaClinicaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(Constants.ENTIDAD_HISTORIA_CLINICA, "id", id));

        historiaClinica.archivar(motivo);
        historiaClinicaRepository.save(historiaClinica);
        log.info("Historia clínica archivada exitosamente");
    }

    @Override
    public void reactivar(Long id) {
        log.info("Reactivando historia clínica ID: {}", id);
        HistoriaClinica historiaClinica = historiaClinicaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(Constants.ENTIDAD_HISTORIA_CLINICA, "id", id));

        historiaClinica.reactivar();
        historiaClinicaRepository.save(historiaClinica);
        log.info("Historia clínica reactivada exitosamente");
    }
}

