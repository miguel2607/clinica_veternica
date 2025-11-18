package com.veterinaria.clinica_veternica.service.impl;

import com.veterinaria.clinica_veternica.domain.comunicacion.Comunicacion;
import com.veterinaria.clinica_veternica.domain.usuario.Personal;
import com.veterinaria.clinica_veternica.domain.usuario.Usuario;
import com.veterinaria.clinica_veternica.dto.request.comunicacion.NotificacionRequestDTO;
import com.veterinaria.clinica_veternica.dto.response.comunicacion.NotificacionResponseDTO;
import com.veterinaria.clinica_veternica.exception.ResourceNotFoundException;
import com.veterinaria.clinica_veternica.exception.ValidationException;
import com.veterinaria.clinica_veternica.patterns.creational.abstractfactory.*;
import com.veterinaria.clinica_veternica.repository.ComunicacionRepository;
import com.veterinaria.clinica_veternica.repository.PersonalRepository;
import com.veterinaria.clinica_veternica.repository.PropietarioRepository;
import com.veterinaria.clinica_veternica.repository.UsuarioRepository;
import com.veterinaria.clinica_veternica.service.interfaces.INotificacionService;
import com.veterinaria.clinica_veternica.util.Constants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Implementación del servicio para gestión de notificaciones.
 * Usa Abstract Factory Pattern para diferentes canales (EMAIL, SMS, WHATSAPP, PUSH).
 * NOTA: En producción, esto se integraría con servicios reales de notificación.
 *
 * @author Clínica Veterinaria Team
 * @version 1.0
 * @since 2025-01-XX
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class NotificacionServiceImpl implements INotificacionService {

    private final ComunicacionRepository comunicacionRepository;
    private final UsuarioRepository usuarioRepository;
    private final PersonalRepository personalRepository;
    private final PropietarioRepository propietarioRepository;

    // Factories para diferentes canales
    private final EmailNotificacionFactory emailFactory;
    private final SMSNotificacionFactory smsFactory;
    private final WhatsAppNotificacionFactory whatsAppFactory;
    private final PushNotificacionFactory pushFactory;

    // Mapa de factories por canal
    private final Map<String, NotificacionFactory> factories = new HashMap<>();

    /**
     * Inicializa el mapa de factories.
     */
    private void initFactories() {
        if (factories.isEmpty()) {
            factories.put("EMAIL", emailFactory);
            factories.put("SMS", smsFactory);
            factories.put("WHATSAPP", whatsAppFactory);
            factories.put("PUSH", pushFactory);
        }
    }

    @Override
    public NotificacionResponseDTO enviarNotificacion(NotificacionRequestDTO requestDTO) {
        initFactories();

        // Buscar usuario
        Usuario usuario = usuarioRepository.findById(requestDTO.getIdUsuario())
            .orElseThrow(() -> new ResourceNotFoundException(Constants.ENTIDAD_USUARIO, "id", requestDTO.getIdUsuario()));

        // Obtener factory según canal
        NotificacionFactory factory = factories.get(requestDTO.getCanal().toUpperCase());
        if (factory == null) {
            throw new ValidationException(
                "Canal no válido: " + requestDTO.getCanal(),
                "canal",
                "El canal debe ser: EMAIL, SMS, WHATSAPP o PUSH"
            );
        }

        // Obtener datos de contacto según el canal
        String destinatario = obtenerDestinatario(usuario, requestDTO.getCanal());
        String nombreDestinatario = obtenerNombreDestinatario(usuario);
        String emailDestinatario = usuario.getEmail();
        String telefonoDestinatario = obtenerTelefonoDestinatario(usuario);

        // Validar destinatario
        ValidadorDestinatario validador = factory.crearValidador();
        if (!validador.esValido(destinatario)) {
            throw new ValidationException(
                "Destinatario inválido para el canal " + requestDTO.getCanal() + ": " + validador.getMensajeError(),
                "destinatario",
                validador.getMensajeError()
            );
        }

        // Normalizar destinatario
        destinatario = validador.normalizar(destinatario);

        // Construir mensaje
        String asunto = Constants.PREFIJO_NOTIFICACION + requestDTO.getMotivo();
        String mensaje = requestDTO.getMensaje() != null && !requestDTO.getMensaje().isBlank() 
            ? requestDTO.getMensaje() 
            : "Usted tiene una notificación: " + requestDTO.getMotivo();

        // Crear mensaje usando el factory
        MensajeNotificacion mensajeNotificacion = factory.crearMensaje(destinatario, asunto, mensaje);

        // Crear entidad Comunicacion
        Comunicacion comunicacion = Comunicacion.builder()
            .tipo(Constants.ENTIDAD_NOTIFICACION)
            .canal(requestDTO.getCanal().toUpperCase())
            .destinatarioNombre(nombreDestinatario)
            .destinatarioEmail(emailDestinatario)
            .destinatarioTelefono(telefonoDestinatario)
            .asunto(asunto)
            .mensaje(mensaje)
            .enviada(false)
            .build();

        // Guardar antes de enviar
        comunicacion = comunicacionRepository.save(comunicacion);
        log.info("Notificación creada: ID={}, Usuario={}, Canal={}, Motivo={}", 
            comunicacion.getIdComunicacion(), usuario.getUsername(), requestDTO.getCanal(), requestDTO.getMotivo());

        // Enviar notificación (SIMULADO)
        EnviadorNotificacion enviador = factory.crearEnviador();
        boolean enviado = enviador.enviar(mensajeNotificacion);

        // Actualizar estado
        if (enviado) {
            comunicacion.marcarComoEnviada(enviador.getIdExterno());
            log.info("Notificación enviada exitosamente: ID={}, ID Externo={}", 
                comunicacion.getIdComunicacion(), enviador.getIdExterno());
        } else {
            comunicacion.registrarFalloEnvio(enviador.getMensajeError());
            log.warn("Error al enviar notificación: ID={}, Error={}", 
                comunicacion.getIdComunicacion(), enviador.getMensajeError());
        }

        comunicacion = comunicacionRepository.save(comunicacion);

        // Convertir a DTO
        return toResponseDTO(comunicacion, usuario, enviador);
    }

    /**
     * Obtiene el destinatario según el canal.
     */
    private String obtenerDestinatario(Usuario usuario, String canal) {
        return switch (canal.toUpperCase()) {
            case "EMAIL" -> usuario.getEmail();
            case "SMS", "WHATSAPP" -> obtenerTelefonoDestinatario(usuario);
            case "PUSH" -> usuario.getUsername(); // Para push se usa username o token
            default -> throw new ValidationException("Canal no válido: " + canal, "canal", "Canal no soportado");
        };
    }

    /**
     * Obtiene el nombre del destinatario.
     */
    private String obtenerNombreDestinatario(Usuario usuario) {
        // Intentar obtener de Personal
        Personal personal = personalRepository.findByUsuario(usuario).orElse(null);
        if (personal != null) {
            return personal.getNombreCompleto();
        }

        // Intentar obtener de Propietario
        var propietario = propietarioRepository.findByEmail(usuario.getEmail()).orElse(null);
        if (propietario != null) {
            return propietario.getNombreCompleto();
        }

        // Si no se encuentra, usar username
        return usuario.getUsername();
    }

    /**
     * Obtiene el teléfono del destinatario.
     */
    private String obtenerTelefonoDestinatario(Usuario usuario) {
        // Intentar obtener de Personal
        Personal personal = personalRepository.findByUsuario(usuario).orElse(null);
        if (personal != null && personal.getTelefono() != null) {
            return personal.getTelefono();
        }

        // Intentar obtener de Propietario
        var propietario = propietarioRepository.findByEmail(usuario.getEmail()).orElse(null);
        if (propietario != null && propietario.getTelefono() != null) {
            return propietario.getTelefono();
        }

        // Si no se encuentra, retornar null (se validará después)
        return null;
    }

    @Override
    @Transactional(readOnly = true)
    public NotificacionResponseDTO buscarPorId(Long id) {
        Comunicacion comunicacion = comunicacionRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(Constants.ENTIDAD_NOTIFICACION, "id", id));
        
        Usuario usuario = obtenerUsuarioDeComunicacion(comunicacion);
        return toResponseDTO(comunicacion, usuario, null);
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificacionResponseDTO> listarTodas() {
        return comunicacionRepository.findByTipo(Constants.ENTIDAD_NOTIFICACION).stream()
            .map(com -> {
                Usuario usuario = obtenerUsuarioDeComunicacion(com);
                return toResponseDTO(com, usuario, null);
            })
            .toList();
    }


    @Override
    @Transactional(readOnly = true)
    public List<NotificacionResponseDTO> listarPorUsuario(Long idUsuario) {
        Usuario usuario = usuarioRepository.findById(idUsuario)
            .orElseThrow(() -> new ResourceNotFoundException(Constants.ENTIDAD_USUARIO, "id", idUsuario));

        return comunicacionRepository.findByDestinatarioEmail(usuario.getEmail()).stream()
            .filter(com -> Constants.ENTIDAD_NOTIFICACION.equals(com.getTipo()))
            .map(com -> toResponseDTO(com, usuario, null))
            .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificacionResponseDTO> listarPorCanal(String canal) {
        return comunicacionRepository.findByCanal(canal.toUpperCase()).stream()
            .filter(com -> Constants.ENTIDAD_NOTIFICACION.equals(com.getTipo()))
            .map(com -> {
                Usuario usuario = obtenerUsuarioDeComunicacion(com);
                return toResponseDTO(com, usuario, null);
            })
            .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificacionResponseDTO> listarEnviadas() {
        return comunicacionRepository.findByTipo(Constants.ENTIDAD_NOTIFICACION).stream()
            .filter(com -> Constants.isTrue(com.getEnviada()))
            .map(com -> {
                Usuario usuario = obtenerUsuarioDeComunicacion(com);
                return toResponseDTO(com, usuario, null);
            })
            .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificacionResponseDTO> listarPendientes() {
        return comunicacionRepository.findPendientesEnvio().stream()
            .filter(com -> Constants.ENTIDAD_NOTIFICACION.equals(com.getTipo()))
            .map(com -> {
                Usuario usuario = obtenerUsuarioDeComunicacion(com);
                return toResponseDTO(com, usuario, null);
            })
            .toList();
    }

    /**
     * Obtiene el usuario de una comunicación.
     */
    private Usuario obtenerUsuarioDeComunicacion(Comunicacion comunicacion) {
        if (comunicacion.getDestinatarioEmail() != null) {
            return usuarioRepository.findByEmail(comunicacion.getDestinatarioEmail())
                .orElse(null);
        }
        return null;
    }

    /**
     * Convierte Comunicacion a NotificacionResponseDTO.
     */
    private NotificacionResponseDTO toResponseDTO(Comunicacion comunicacion, Usuario usuario, EnviadorNotificacion enviador) {
        NotificacionResponseDTO.NotificacionResponseDTOBuilder builder = NotificacionResponseDTO.builder()
            .idComunicacion(comunicacion.getIdComunicacion())
            .tipo(comunicacion.getTipo())
            .canal(comunicacion.getCanal())
            .motivo(comunicacion.getAsunto() != null && comunicacion.getAsunto().startsWith(Constants.PREFIJO_NOTIFICACION) 
                ? comunicacion.getAsunto().substring(Constants.PREFIJO_NOTIFICACION.length()) 
                : comunicacion.getAsunto())
            .asunto(comunicacion.getAsunto())
            .mensaje(comunicacion.getMensaje())
            .enviada(comunicacion.getEnviada())
            .fechaEnvio(comunicacion.getFechaEnvio())
            .idExterno(comunicacion.getIdExterno())
            .mensajeError(comunicacion.getMensajeError())
            .fechaCreacion(comunicacion.getFechaCreacion());

        if (usuario != null) {
            builder.idUsuario(usuario.getIdUsuario())
                .nombreUsuario(obtenerNombreDestinatario(usuario))
                .emailUsuario(usuario.getEmail())
                .telefonoUsuario(obtenerTelefonoDestinatario(usuario));
        } else {
            builder.nombreUsuario(comunicacion.getDestinatarioNombre())
                .emailUsuario(comunicacion.getDestinatarioEmail())
                .telefonoUsuario(comunicacion.getDestinatarioTelefono());
        }

        if (enviador != null) {
            builder.estadoEnvio(enviador.getEstadoEnvio());
        } else {
            builder.estadoEnvio(com.veterinaria.clinica_veternica.util.Constants.isTrue(comunicacion.getEnviada()) ? "ENVIADO" : "PENDIENTE");
        }

        return builder.build();
    }
}

