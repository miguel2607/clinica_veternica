package com.veterinaria.clinica_veternica.service.impl;

import com.veterinaria.clinica_veternica.domain.paciente.Especie;
import com.veterinaria.clinica_veternica.domain.paciente.Mascota;
import com.veterinaria.clinica_veternica.domain.paciente.Propietario;
import com.veterinaria.clinica_veternica.domain.paciente.Raza;
import com.veterinaria.clinica_veternica.dto.request.paciente.MascotaRequestDTO;
import com.veterinaria.clinica_veternica.dto.response.paciente.MascotaResponseDTO;
import com.veterinaria.clinica_veternica.exception.BusinessException;
import com.veterinaria.clinica_veternica.exception.ResourceNotFoundException;

import com.veterinaria.clinica_veternica.mapper.paciente.MascotaMapper;
import com.veterinaria.clinica_veternica.repository.EspecieRepository;
import com.veterinaria.clinica_veternica.repository.MascotaRepository;
import com.veterinaria.clinica_veternica.repository.PropietarioRepository;
import com.veterinaria.clinica_veternica.repository.RazaRepository;
import com.veterinaria.clinica_veternica.service.interfaces.IMascotaService;
import com.veterinaria.clinica_veternica.util.Constants;
import com.veterinaria.clinica_veternica.util.ValidationHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class MascotaServiceImpl implements IMascotaService {

    private final MascotaRepository mascotaRepository;
    private final PropietarioRepository propietarioRepository;
    private final EspecieRepository especieRepository;
    private final RazaRepository razaRepository;
    private final MascotaMapper mascotaMapper;
    private final ValidationHelper validationHelper;

    @Override
    public MascotaResponseDTO crear(MascotaRequestDTO requestDTO) {
        // Validar que el propietario existe
        Propietario propietario = propietarioRepository.findById(requestDTO.getIdPropietario())
            .orElseThrow(() -> new ResourceNotFoundException(Constants.ENTIDAD_PROPIETARIO, "id", requestDTO.getIdPropietario()));

        // Validar que la especie existe
        Especie especie = especieRepository.findById(requestDTO.getIdEspecie())
            .orElseThrow(() -> new ResourceNotFoundException(Constants.ENTIDAD_ESPECIE, "id", requestDTO.getIdEspecie()));

        // Validar raza si se proporciona
        Raza raza = null;
        if (requestDTO.getIdRaza() != null) {
            raza = razaRepository.findById(requestDTO.getIdRaza())
                .orElseThrow(() -> new ResourceNotFoundException("Raza", "id", requestDTO.getIdRaza()));

            // Validar que la raza pertenezca a la especie
            validationHelper.validateRazaBelongsToSpecies(
                raza.getEspecie().getIdEspecie(),
                especie.getIdEspecie(),
                raza.getNombre(),
                especie.getNombre()
            );
        }

        Mascota mascota = mascotaMapper.toEntity(requestDTO);
        mascota.setPropietario(propietario);
        mascota.setEspecie(especie);
        mascota.setRaza(raza);

        if (mascota.getActivo() == null) {
            mascota.setActivo(true);
        }

        Mascota mascotaGuardada = mascotaRepository.save(mascota);
        return mascotaMapper.toResponseDTO(mascotaGuardada);
    }

    @Override
    public MascotaResponseDTO actualizar(Long id, MascotaRequestDTO requestDTO) {
        Mascota mascota = mascotaRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(Constants.ENTIDAD_MASCOTA, "id", id));

        // Validar propietario
        Propietario propietario = propietarioRepository.findById(requestDTO.getIdPropietario())
            .orElseThrow(() -> new ResourceNotFoundException(Constants.ENTIDAD_PROPIETARIO, "id", requestDTO.getIdPropietario()));

        // Validar especie
        Especie especie = especieRepository.findById(requestDTO.getIdEspecie())
            .orElseThrow(() -> new ResourceNotFoundException(Constants.ENTIDAD_ESPECIE, "id", requestDTO.getIdEspecie()));

        // Validar raza
        Raza raza = null;
        if (requestDTO.getIdRaza() != null) {
            raza = razaRepository.findById(requestDTO.getIdRaza())
                .orElseThrow(() -> new ResourceNotFoundException("Raza", "id", requestDTO.getIdRaza()));

            validationHelper.validateRazaBelongsToSpecies(
                raza.getEspecie().getIdEspecie(),
                especie.getIdEspecie(),
                raza.getNombre(),
                especie.getNombre()
            );
        }

        mascotaMapper.updateEntityFromDTO(requestDTO, mascota);
        mascota.setPropietario(propietario);
        mascota.setEspecie(especie);
        mascota.setRaza(raza);

        Mascota mascotaActualizada = mascotaRepository.save(mascota);
        return mascotaMapper.toResponseDTO(mascotaActualizada);
    }

    @Override
    @Transactional(readOnly = true)
    public MascotaResponseDTO buscarPorId(Long id) {
        Mascota mascota = mascotaRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(Constants.ENTIDAD_MASCOTA, "id", id));
        return mascotaMapper.toResponseDTO(mascota);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MascotaResponseDTO> listarTodas() {
        List<Mascota> mascotas = mascotaRepository.findAll();
        return mascotaMapper.toResponseDTOList(mascotas);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MascotaResponseDTO> listarActivas() {
        List<Mascota> mascotas = mascotaRepository.findByActivoTrue();
        return mascotaMapper.toResponseDTOList(mascotas);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MascotaResponseDTO> listarPorPropietario(Long idPropietario) {
        if (!propietarioRepository.existsById(idPropietario)) {
            throw new ResourceNotFoundException(Constants.ENTIDAD_PROPIETARIO, "id", idPropietario);
        }
        List<Mascota> mascotas = mascotaRepository.findByPropietarioId(idPropietario);
        return mascotaMapper.toResponseDTOList(mascotas);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MascotaResponseDTO> listarPorEspecie(Long idEspecie) {
        if (!especieRepository.existsById(idEspecie)) {
            throw new ResourceNotFoundException(Constants.ENTIDAD_ESPECIE, "id", idEspecie);
        }
        List<Mascota> mascotas = mascotaRepository.findByEspecieId(idEspecie);
        return mascotaMapper.toResponseDTOList(mascotas);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MascotaResponseDTO> listarPorRaza(Long idRaza) {
        if (!razaRepository.existsById(idRaza)) {
            throw new ResourceNotFoundException("Raza", "id", idRaza);
        }
        List<Mascota> mascotas = mascotaRepository.findByRazaId(idRaza);
        return mascotaMapper.toResponseDTOList(mascotas);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MascotaResponseDTO> buscarPorNombre(String nombre) {
        String nombreSanitizado = validationHelper.validateAndSanitizeSearchTerm(nombre, 100);
        List<Mascota> mascotas = mascotaRepository.findByNombreContainingIgnoreCase(nombreSanitizado);
        return mascotaMapper.toResponseDTOList(mascotas);
    }

    @Override
    public void eliminar(Long id) {
        Mascota mascota = mascotaRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(Constants.ENTIDAD_MASCOTA, "id", id));

        // Verificar si tiene historia clínica
        if (mascota.getHistoriaClinica() != null) {
            throw new BusinessException(
                "No se puede eliminar la mascota porque tiene una historia clínica asociada",
                "MASCOTA_CON_HISTORIA_CLINICA"
            );
        }

        // Verificar si tiene citas
        if (mascota.getCitas() != null && !mascota.getCitas().isEmpty()) {
            throw new BusinessException(
                "No se puede eliminar la mascota porque tiene " + mascota.getCitas().size() + " cita(s) asociada(s)",
                "MASCOTA_CON_CITAS"
            );
        }

        mascota.setActivo(false);
        mascotaRepository.save(mascota);
    }

    @Override
    public MascotaResponseDTO activar(Long id) {
        Mascota mascota = mascotaRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(Constants.ENTIDAD_MASCOTA, "id", id));

        if (Constants.isTrue(mascota.getActivo())) {
            throw new BusinessException("La mascota ya está activa", "MASCOTA_YA_ACTIVA");
        }

        mascota.setActivo(true);
        Mascota mascotaActivada = mascotaRepository.save(mascota);
        return mascotaMapper.toResponseDTO(mascotaActivada);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existePorNombreYPropietario(String nombre, Long idPropietario) {
        if (nombre == null || nombre.trim().isEmpty() || idPropietario == null) {
            return false;
        }
        return mascotaRepository.existsByNombreAndPropietarioId(nombre.trim(), idPropietario);
    }
}
