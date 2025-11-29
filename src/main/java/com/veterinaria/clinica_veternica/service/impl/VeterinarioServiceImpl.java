package com.veterinaria.clinica_veternica.service.impl;


import com.veterinaria.clinica_veternica.domain.usuario.RolUsuario;
import com.veterinaria.clinica_veternica.domain.usuario.Usuario;
import com.veterinaria.clinica_veternica.domain.usuario.Veterinario;
import com.veterinaria.clinica_veternica.dto.request.usuario.VeterinarioRequestDTO;
import com.veterinaria.clinica_veternica.dto.response.usuario.VeterinarioResponseDTO;
import com.veterinaria.clinica_veternica.exception.BusinessException;
import com.veterinaria.clinica_veternica.exception.ResourceNotFoundException;
import com.veterinaria.clinica_veternica.exception.ValidationException;
import com.veterinaria.clinica_veternica.mapper.usuario.VeterinarioMapper;
import com.veterinaria.clinica_veternica.repository.UsuarioRepository;
import com.veterinaria.clinica_veternica.repository.VeterinarioRepository;
import com.veterinaria.clinica_veternica.service.interfaces.IVeterinarioService;
import com.veterinaria.clinica_veternica.util.Constants;
import com.veterinaria.clinica_veternica.util.NameParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class VeterinarioServiceImpl implements IVeterinarioService {

    private final VeterinarioRepository veterinarioRepository;
    private final UsuarioRepository usuarioRepository;
    private final VeterinarioMapper veterinarioMapper;

    @Override
    public VeterinarioResponseDTO crear(VeterinarioRequestDTO requestDTO) {
        // Validar registro profesional único
        if (veterinarioRepository.existsByRegistroProfesional(requestDTO.getRegistroProfesional())) {
            throw new ValidationException(
                "Ya existe un veterinario con el registro profesional: " + requestDTO.getRegistroProfesional(),
                "registroProfesional",
                "El registro profesional ya está registrado"
            );
        }

        Veterinario veterinario = veterinarioMapper.toEntity(requestDTO);

        if (veterinario.getActivo() == null) {
            veterinario.setActivo(true);
        }

        Veterinario veterinarioGuardado = veterinarioRepository.save(veterinario);
        return veterinarioMapper.toResponseDTO(veterinarioGuardado);
    }

    @Override
    public VeterinarioResponseDTO actualizar(Long id, VeterinarioRequestDTO requestDTO) {
        Veterinario veterinario = veterinarioRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(Constants.ENTIDAD_VETERINARIO, "id", id));

        // Validar registro profesional único (si cambió)
        if (!Objects.equals(veterinario.getRegistroProfesional(), requestDTO.getRegistroProfesional()) &&
            veterinarioRepository.existsByRegistroProfesional(requestDTO.getRegistroProfesional())) {
            throw new ValidationException(
                "Ya existe otro veterinario con el registro profesional: " + requestDTO.getRegistroProfesional(),
                "registroProfesional",
                "El registro profesional ya está registrado"
            );
        }

        veterinarioMapper.updateEntityFromDTO(requestDTO, veterinario);

        Veterinario veterinarioActualizado = veterinarioRepository.save(veterinario);
        return veterinarioMapper.toResponseDTO(veterinarioActualizado);
    }

    @Override
    @Transactional(readOnly = true)
    public VeterinarioResponseDTO buscarPorId(Long id) {
        Veterinario veterinario = veterinarioRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(Constants.ENTIDAD_VETERINARIO, "id", id));
        return veterinarioMapper.toResponseDTO(veterinario);
    }

    @Override
    @Transactional(readOnly = true)
    public VeterinarioResponseDTO buscarPorRegistroProfesional(String registroProfesional) {
        Veterinario veterinario = veterinarioRepository.findByRegistroProfesional(registroProfesional)
            .orElseThrow(() -> new ResourceNotFoundException(Constants.ENTIDAD_VETERINARIO, "registroProfesional", registroProfesional));
        return veterinarioMapper.toResponseDTO(veterinario);
    }

    @Override
    @Transactional(readOnly = true)
    public List<VeterinarioResponseDTO> listarTodos() {
        List<Veterinario> veterinarios = veterinarioRepository.findAllWithUsuario();
        return veterinarioMapper.toResponseDTOList(veterinarios);
    }

    @Override
    @Transactional(readOnly = true)
    public List<VeterinarioResponseDTO> listarActivos() {
        List<Veterinario> veterinarios = veterinarioRepository.findByActivoTrue();
        return veterinarioMapper.toResponseDTOList(veterinarios);
    }

    @Override
    @Transactional(readOnly = true)
    public List<VeterinarioResponseDTO> listarDisponibles() {
        List<Veterinario> veterinarios = veterinarioRepository.findVeterinariosDisponibles();
        return veterinarioMapper.toResponseDTOList(veterinarios);
    }

    @Override
    @Transactional(readOnly = true)
    public List<VeterinarioResponseDTO> listarPorEspecialidad(String especialidad) {
        if (especialidad == null || especialidad.trim().isEmpty()) {
            throw new ValidationException("La especialidad de búsqueda no puede estar vacía");
        }
        List<Veterinario> veterinarios = veterinarioRepository.findByEspecialidadContainingIgnoreCase(especialidad.trim());
        return veterinarioMapper.toResponseDTOList(veterinarios);
    }

    @Override
    @Transactional(readOnly = true)
    public List<VeterinarioResponseDTO> buscarPorNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new ValidationException("El nombre de búsqueda no puede estar vacío");
        }
        List<Veterinario> veterinarios = veterinarioRepository.buscarPorNombre(nombre.trim());
        return veterinarioMapper.toResponseDTOList(veterinarios);
    }

    @Override
    public void eliminar(Long id) {
        Veterinario veterinario = veterinarioRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(Constants.ENTIDAD_VETERINARIO, "id", id));

        // Verificar si tiene citas asociadas
        if (veterinario.getCitas() != null && !veterinario.getCitas().isEmpty()) {
            throw new BusinessException(
                "No se puede eliminar el veterinario porque tiene " +
                veterinario.getCitas().size() + " cita(s) asociada(s)",
                "VETERINARIO_CON_CITAS"
            );
        }

        // Verificar si tiene horarios asociados
        if (veterinario.getHorarios() != null && !veterinario.getHorarios().isEmpty()) {
            throw new BusinessException(
                "No se puede eliminar el veterinario porque tiene " +
                veterinario.getHorarios().size() + " horario(s) asociado(s)",
                "VETERINARIO_CON_HORARIOS"
            );
        }

        veterinario.setActivo(false);
        veterinarioRepository.save(veterinario);
    }

    @Override
    public VeterinarioResponseDTO activar(Long id) {
        Veterinario veterinario = veterinarioRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(Constants.ENTIDAD_VETERINARIO, "id", id));

        if (Constants.isTrue(veterinario.getActivo())) {
            throw new BusinessException("El veterinario ya está activo", "VETERINARIO_YA_ACTIVO");
        }

        veterinario.setActivo(true);
        Veterinario veterinarioActivado = veterinarioRepository.save(veterinario);
        return veterinarioMapper.toResponseDTO(veterinarioActivado);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existePorRegistroProfesional(String registroProfesional) {
        if (registroProfesional == null || registroProfesional.trim().isEmpty()) {
            return false;
        }
        return veterinarioRepository.existsByRegistroProfesional(registroProfesional.trim());
    }

    @Override
    @Transactional(readOnly = true)
    public VeterinarioResponseDTO obtenerPorUsuarioAutenticado() {
        // Obtener el username del usuario autenticado
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        
        // Buscar el usuario en la base de datos
        Usuario usuario = usuarioRepository.findByUsername(username)
            .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
        
        // Buscar el veterinario asociado al usuario (con usuario cargado)
        Veterinario veterinario = veterinarioRepository.findByUsuarioIdWithUsuario(usuario.getIdUsuario())
            .orElseThrow(() -> new ResourceNotFoundException(
                "No se encontró un veterinario asociado al usuario: " + username));
        
        return veterinarioMapper.toResponseDTO(veterinario);
    }

    @Override
    @Transactional
    public VeterinarioResponseDTO crearDesdeUsuario(Long idUsuario) {
        // Buscar el usuario
        Usuario usuario = usuarioRepository.findById(idUsuario)
            .orElseThrow(() -> new ResourceNotFoundException(Constants.ENTIDAD_USUARIO, "id", idUsuario));

        // Verificar que el usuario tenga rol VETERINARIO
        if (usuario.getRol() != com.veterinaria.clinica_veternica.domain.usuario.RolUsuario.VETERINARIO) {
            throw new ValidationException(
                "El usuario no tiene rol VETERINARIO",
                "rol",
                "Solo se pueden crear veterinarios para usuarios con rol VETERINARIO"
            );
        }

        // Verificar que no exista ya un veterinario asociado
        if (veterinarioRepository.existsByUsuarioId(idUsuario)) {
            throw new BusinessException(
                "El usuario ya tiene un veterinario asociado",
                "VETERINARIO_YA_EXISTE"
            );
        }

        // Extraer nombres y apellidos del username
        NameParser.NameParts nombreParts = NameParser.extractNamesAndLastNames(usuario.getUsername());

        // Crear el veterinario
        Veterinario veterinario = Veterinario.builder()
            .nombres(nombreParts.nombres())
            .apellidos(nombreParts.apellidos().isEmpty() ? "Pendiente" : nombreParts.apellidos())
            .documento("TEMP_" + usuario.getIdUsuario())
            .correo(usuario.getEmail())
            .telefono("0000000000")
            .especialidad("Medicina General")
            .registroProfesional("REG_" + usuario.getIdUsuario())
            .aniosExperiencia(0)
            .activo(usuario.getEstado())
            .usuario(usuario)
            .build();

        Veterinario veterinarioGuardado = veterinarioRepository.save(veterinario);
        log.info("✅ Veterinario creado desde usuario ID: {} (username: {}, nombres: {}, apellidos: {})",
                usuario.getIdUsuario(), usuario.getUsername(), nombreParts.nombres(), nombreParts.apellidos());

        return veterinarioMapper.toResponseDTO(veterinarioGuardado);
    }
    @Override
    @Transactional
    public int sincronizarUsuariosVeterinarios() {
        // Buscar todos los usuarios con rol VETERINARIO
        List<Usuario> usuariosVeterinarios = usuarioRepository.findByRol(RolUsuario.VETERINARIO);
        
        int creados = 0;
        int yaExistentes = 0;
        int errores = 0;

        for (Usuario usuario : usuariosVeterinarios) {
            try {
                // Verificar si ya tiene veterinario asociado
                if (veterinarioRepository.existsByUsuarioId(usuario.getIdUsuario())) {
                    yaExistentes++;
                    continue;
                }

                // Extraer nombres y apellidos
                NameParser.NameParts nombreParts = NameParser.extractNamesAndLastNames(usuario.getUsername());

                // Crear el veterinario
                Veterinario veterinario = Veterinario.builder()
                    .nombres(nombreParts.nombres())
                    .apellidos(nombreParts.apellidos().isEmpty() ? "Pendiente" : nombreParts.apellidos())
                    .documento("TEMP_" + usuario.getIdUsuario())
                    .correo(usuario.getEmail())
                    .telefono("0000000000")
                    .especialidad("Medicina General")
                    .registroProfesional("REG_" + usuario.getIdUsuario())
                    .aniosExperiencia(0)
                    .activo(usuario.getEstado())
                    .usuario(usuario)
                    .build();

                veterinarioRepository.save(veterinario);
                creados++;
                log.info("✅ Veterinario sincronizado para usuario ID: {} (username: {})",
                        usuario.getIdUsuario(), usuario.getUsername());
            } catch (Exception e) {
                errores++;
                log.error("❌ Error al sincronizar veterinario para usuario ID: {} - {}",
                        usuario.getIdUsuario(), e.getMessage(), e);
            }
        }

        log.info("📊 Sincronización completada: {} creados, {} ya existentes, {} errores",
                creados, yaExistentes, errores);

        return creados;
    }
}
