package com.veterinaria.clinica_veternica.patterns.behavioral.observer;

import com.veterinaria.clinica_veternica.domain.agenda.Cita;
import com.veterinaria.clinica_veternica.domain.comunicacion.Comunicacion;
import com.veterinaria.clinica_veternica.domain.inventario.Inventario;
import com.veterinaria.clinica_veternica.domain.usuario.Personal;
import com.veterinaria.clinica_veternica.domain.usuario.RolUsuario;
import com.veterinaria.clinica_veternica.domain.usuario.Usuario;
import com.veterinaria.clinica_veternica.patterns.creational.abstractfactory.EmailNotificacionFactory;
import com.veterinaria.clinica_veternica.patterns.creational.abstractfactory.NotificacionFactory;
import com.veterinaria.clinica_veternica.repository.ComunicacionRepository;
import com.veterinaria.clinica_veternica.repository.InventarioRepository;
import com.veterinaria.clinica_veternica.repository.PersonalRepository;
import com.veterinaria.clinica_veternica.repository.UsuarioRepository;
import com.veterinaria.clinica_veternica.service.interfaces.IInventarioService;
import com.veterinaria.clinica_veternica.util.Constants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Patrón Observer: InventarioObserver
 *
 * Observador que monitorea el inventario y genera alertas automáticas
 * cuando el stock de insumos está bajo o se agota.
 *
 * PROPÓSITO:
 * - Detecta automáticamente cuando el stock está bajo
 * - Genera alertas para prevenir desabastecimiento
 * - Notifica a los responsables de compras
 * - Mantiene un inventario óptimo
 *
 * TIPOS DE ALERTAS:
 * - STOCK_BAJO: Cuando el stock está por debajo del mínimo
 * - STOCK_CRITICO: Cuando el stock está muy bajo (menos del 50% del mínimo)
 * - STOCK_AGOTADO: Cuando el stock llega a cero
 * - MOVIMIENTO_ANORMAL: Cuando hay movimientos inusuales
 *
 * Justificación:
 * - Previene desabastecimiento crítico
 * - Automatiza la detección de problemas de inventario
 * - Mejora la gestión de compras
 * - Reduce pérdidas por falta de insumos
 *
 * @author Clínica Veterinaria Team
 * @version 1.0
 * @since 2025-01-27
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class InventarioObserver {

    private final InventarioRepository inventarioRepository;
    private final EmailNotificacionFactory emailFactory;
    private final IInventarioService inventarioService;
    private final UsuarioRepository usuarioRepository;
    private final ComunicacionRepository comunicacionRepository;
    private final PersonalRepository personalRepository;

    /**
     * Observa cambios en el inventario cuando se crea una cita.
     * Verifica si se requieren insumos y valida disponibilidad.
     *
     * PROPÓSITO: Valida disponibilidad de insumos antes de crear una cita
     * que requiere materiales específicos.
     */
    public void onCitaCreated(Cita cita) {
        log.debug("InventarioObserver: Verificando insumos para cita: {}", cita.getIdCita());

        // Verificar disponibilidad de insumos para el servicio
        // Nota: En producción, se podría tener una relación entre Servicio e Insumos
        // Por ahora, solo registramos el evento
        log.debug("Cita creada - verificación de insumos pendiente de implementación");
    }

    /**
     * Observa cambios en el inventario cuando cambia el estado de una cita.
     * Registra consumo de insumos cuando la cita es atendida.
     *
     * PROPÓSITO: Actualiza el inventario cuando se consumen insumos en una atención.
     */
    public void onCitaStateChanged(Cita cita, String estadoAnterior, String estadoNuevo) {
        if ("ATENDIDA".equals(estadoNuevo) && !"ATENDIDA".equals(estadoAnterior)) {
            log.debug("InventarioObserver: Cita atendida, registrando consumo de insumos: {}", 
                    cita.getIdCita());
            registrarConsumoInsumos(cita);
        }
    }


    /**
     * Registra el consumo de insumos cuando una cita es atendida.
     *
     * PROPÓSITO: Actualiza el inventario con el consumo real de insumos.
     *
     * @param cita Cita atendida
     */
    private void registrarConsumoInsumos(Cita cita) {
        // Implementación simplificada
        // En producción, se registrarían los insumos consumidos
        log.debug("Registrando consumo de insumos para cita: {}", cita.getIdCita());
    }

    /**
     * Monitorea el inventario periódicamente y genera alertas de stock bajo.
     * Se ejecuta cada hora para verificar el estado del inventario.
     *
     * PROPÓSITO: Detecta automáticamente problemas de inventario y genera alertas.
     */
    @Scheduled(fixedRate = Constants.UNA_HORA_MS) // Cada hora
    public void monitorearStock() {
        log.debug("InventarioObserver: Iniciando monitoreo de stock");

        List<Inventario> inventarios = inventarioRepository.findAll();

        for (Inventario inventario : inventarios) {
            verificarStockBajo(inventario);
            verificarStockCritico(inventario);
            verificarStockAgotado(inventario);
        }

        log.debug("InventarioObserver: Monitoreo de stock completado");
    }

    /**
     * Verifica si el stock está bajo y genera alerta si es necesario.
     *
     * PROPÓSITO: Detecta cuando el stock está por debajo del mínimo establecido.
     *
     * @param inventario Inventario a verificar
     */
    private void verificarStockBajo(Inventario inventario) {
        Integer cantidadActual = inventario.getCantidadActual();
        Integer stockMinimo = inventario.getInsumo().getStockMinimo();
        
        if (cantidadActual <= stockMinimo && cantidadActual > stockMinimo / 2) {
            log.warn("Stock bajo detectado: {} - Disponible: {}, Mínimo: {}", 
                    inventario.getInsumo().getNombre(),
                    cantidadActual,
                    stockMinimo);

            enviarAlertaStockBajo(inventario);
        }
    }

    /**
     * Verifica si el stock está crítico y genera alerta urgente.
     *
     * PROPÓSITO: Detecta cuando el stock está muy bajo y requiere acción inmediata.
     *
     * @param inventario Inventario a verificar
     */
    private void verificarStockCritico(Inventario inventario) {
        Integer cantidadActual = inventario.getCantidadActual();
        Integer stockMinimo = inventario.getInsumo().getStockMinimo();
        
        if (cantidadActual <= stockMinimo / 2 && cantidadActual > 0) {
            log.error("Stock crítico detectado: {} - Disponible: {}, Mínimo: {}", 
                    inventario.getInsumo().getNombre(),
                    cantidadActual,
                    stockMinimo);

            enviarAlertaStockCritico(inventario);
        }
    }

    /**
     * Verifica si el stock está agotado y genera alerta urgente.
     *
     * PROPÓSITO: Detecta cuando el stock llega a cero y requiere reposición inmediata.
     *
     * @param inventario Inventario a verificar
     */
    private void verificarStockAgotado(Inventario inventario) {
        Integer cantidadActual = inventario.getCantidadActual();
        if (cantidadActual != null && cantidadActual == 0) {
            log.error("Stock agotado detectado: {}", inventario.getInsumo().getNombre());
            enviarAlertaStockAgotado(inventario);
        }
    }

    /**
     * Envía alerta de stock bajo.
     *
     * PROPÓSITO: Notifica a los responsables cuando el stock está bajo.
     *
     * @param inventario Inventario con stock bajo
     */
    private void enviarAlertaStockBajo(Inventario inventario) {
        String mensaje = String.format("""
                <p>Alerta: <strong>Stock bajo detectado</strong></p>
                
                <div style="background-color: #fef3c7; padding: 20px; border-radius: 8px; border-left: 4px solid #f59e0b; margin: 20px 0;">
                    <h3 style="margin-top: 0; color: #92400e;">Detalles del Insumo:</h3>
                    <table style="width: 100%%; border-collapse: collapse;">
                        <tr>
                            <td style="padding: 8px 0; color: #4b5563; font-weight: 600;">Insumo:</td>
                            <td style="padding: 8px 0; color: #1f2937;">%s</td>
                        </tr>
                        <tr>
                            <td style="padding: 8px 0; color: #4b5563; font-weight: 600;">Stock disponible:</td>
                            <td style="padding: 8px 0; color: #dc2626; font-weight: 600;">%d</td>
                        </tr>
                        <tr>
                            <td style="padding: 8px 0; color: #4b5563; font-weight: 600;">Stock mínimo:</td>
                            <td style="padding: 8px 0; color: #1f2937;">%d</td>
                        </tr>
                        <tr>
                            <td style="padding: 8px 0; color: #4b5563; font-weight: 600;">Stock máximo:</td>
                            <td style="padding: 8px 0; color: #1f2937;">%s</td>
                        </tr>
                    </table>
                </div>
                
                <p><strong>Por favor, considere realizar una compra para reponer el inventario.</strong></p>
                """,
                inventario.getInsumo().getNombre(),
                inventario.getCantidadActual(),
                inventario.getInsumo().getStockMinimo(),
                inventario.getInsumo().getStockMaximo() != null ? inventario.getInsumo().getStockMaximo().toString() : "N/A"
        );

        enviarNotificacion("Alerta de Stock Bajo", mensaje);
    }

    /**
     * Envía alerta de stock crítico.
     *
     * PROPÓSITO: Notifica urgentemente cuando el stock está muy bajo.
     *
     * @param inventario Inventario con stock crítico
     */
    private void enviarAlertaStockCritico(Inventario inventario) {
        String mensaje = String.format("""
                <p><strong style="color: #dc2626; font-size: 18px;">⚠️ ALERTA CRÍTICA: Stock muy bajo</strong></p>
                
                <div style="background-color: #fee2e2; padding: 20px; border-radius: 8px; border-left: 4px solid #dc2626; margin: 20px 0;">
                    <h3 style="margin-top: 0; color: #991b1b;">Detalles del Insumo:</h3>
                    <table style="width: 100%%; border-collapse: collapse;">
                        <tr>
                            <td style="padding: 8px 0; color: #4b5563; font-weight: 600;">Insumo:</td>
                            <td style="padding: 8px 0; color: #1f2937;">%s</td>
                        </tr>
                        <tr>
                            <td style="padding: 8px 0; color: #4b5563; font-weight: 600;">Stock disponible:</td>
                            <td style="padding: 8px 0; color: #dc2626; font-weight: 700; font-size: 18px;">%d</td>
                        </tr>
                        <tr>
                            <td style="padding: 8px 0; color: #4b5563; font-weight: 600;">Stock mínimo:</td>
                            <td style="padding: 8px 0; color: #1f2937;">%d</td>
                        </tr>
                    </table>
                </div>
                
                <p style="color: #dc2626; font-weight: 600; font-size: 16px;">🚨 URGENTE: Se requiere reposición inmediata.</p>
                """,
                inventario.getInsumo().getNombre(),
                inventario.getCantidadActual(),
                inventario.getInsumo().getStockMinimo()
        );

        enviarNotificacion("Alerta Crítica de Stock", mensaje);
    }

    /**
     * Envía alerta de stock agotado.
     *
     * PROPÓSITO: Notifica urgentemente cuando el stock se agota.
     *
     * @param inventario Inventario agotado
     */
    private void enviarAlertaStockAgotado(Inventario inventario) {
        String mensaje = String.format("""
                <p><strong style="color: #dc2626; font-size: 20px;">🚨 ALERTA URGENTE: Stock Agotado</strong></p>
                
                <div style="background-color: #fee2e2; padding: 20px; border-radius: 8px; border-left: 4px solid #dc2626; margin: 20px 0;">
                    <h3 style="margin-top: 0; color: #991b1b;">Detalles del Insumo:</h3>
                    <table style="width: 100%%; border-collapse: collapse;">
                        <tr>
                            <td style="padding: 8px 0; color: #4b5563; font-weight: 600;">Insumo:</td>
                            <td style="padding: 8px 0; color: #1f2937; font-weight: 600;">%s</td>
                        </tr>
                        <tr>
                            <td style="padding: 8px 0; color: #4b5563; font-weight: 600;">Stock disponible:</td>
                            <td style="padding: 8px 0; color: #dc2626; font-weight: 700; font-size: 20px;">0</td>
                        </tr>
                        <tr>
                            <td style="padding: 8px 0; color: #4b5563; font-weight: 600;">Stock mínimo:</td>
                            <td style="padding: 8px 0; color: #1f2937;">%d</td>
                        </tr>
                    </table>
                </div>
                
                <p style="color: #dc2626; font-weight: 700; font-size: 18px;">⚠️ URGENTE: El insumo se ha agotado. Se requiere reposición inmediata.</p>
                """,
                inventario.getInsumo().getNombre(),
                inventario.getInsumo().getStockMinimo()
        );

        enviarNotificacion("Alerta Urgente: Stock Agotado", mensaje);
    }

    /**
     * Envía una notificación usando el Abstract Factory.
     * Envía correos a todos los administradores y auxiliares activos.
     *
     * PROPÓSITO: Centraliza el envío de notificaciones de inventario.
     *
     * @param asunto Asunto de la notificación
     * @param mensaje Mensaje de la notificación
     */
    private void enviarNotificacion(String asunto, String mensaje) {
        try {
            // Obtener emails de administradores y auxiliares activos
            List<Usuario> usuariosNotificar = usuarioRepository.findByRolAndEstado(RolUsuario.ADMIN, true);
            usuariosNotificar.addAll(usuarioRepository.findByRolAndEstado(RolUsuario.AUXILIAR, true));
            
            if (usuariosNotificar.isEmpty()) {
                log.warn("No hay usuarios activos para notificar sobre stock bajo");
                return;
            }
            
            // Enviar correo a cada usuario y guardar en BD
            int enviados = 0;
            int fallidos = 0;
            
            for (Usuario usuario : usuariosNotificar) {
                if (usuario.getEmail() != null && !usuario.getEmail().isBlank()) {
                    try {
                        var mensajeNotificacion = emailFactory.crearMensaje(
                                usuario.getEmail(), 
                                asunto, 
                                mensaje
                        );
                        var enviador = emailFactory.crearEnviador();
                        boolean enviado = enviador.enviar(mensajeNotificacion);
                        
                        // Guardar notificación en la base de datos
                        String nombreDestinatario = obtenerNombreDestinatario(usuario);
                        guardarNotificacionStockEnBD(
                            nombreDestinatario,
                            usuario.getEmail(),
                            obtenerTelefonoDestinatario(usuario),
                            asunto,
                            mensaje,
                            enviado,
                            enviado ? enviador.getIdExterno() : null
                        );
                        
                        if (enviado) {
                            enviados++;
                            log.info("Notificación de inventario enviada a: {}", usuario.getEmail());
                        } else {
                            fallidos++;
                            log.warn("Error al enviar notificación a: {}", usuario.getEmail());
                        }
                    } catch (Exception e) {
                        fallidos++;
                        log.error("Error al enviar notificación a {}: {}", usuario.getEmail(), e.getMessage());
                    }
                }
            }
            
            log.info("Notificaciones de inventario enviadas: {} exitosas, {} fallidas - Asunto: {}", 
                    enviados, fallidos, asunto);
        } catch (IllegalArgumentException | IllegalStateException e) {
            log.error("Error de validación al enviar notificación de inventario: {}", e.getMessage(), e);
        } catch (RuntimeException e) {
            log.error("Error al enviar notificación de inventario: {}", e.getMessage(), e);
        }
    }
    
    /**
     * Obtiene el nombre del destinatario desde Personal o Usuario.
     */
    private String obtenerNombreDestinatario(Usuario usuario) {
        Optional<Personal> personal = personalRepository.findByUsuario(usuario);
        if (personal.isPresent()) {
            return personal.get().getNombreCompleto();
        }
        return usuario.getUsername();
    }
    
    /**
     * Obtiene el teléfono del destinatario desde Personal.
     */
    private String obtenerTelefonoDestinatario(Usuario usuario) {
        Optional<Personal> personal = personalRepository.findByUsuario(usuario);
        return personal.map(Personal::getTelefono).orElse(null);
    }
    
    /**
     * Guarda una notificación de stock en la base de datos.
     * 
     * @param nombreDestinatario Nombre del destinatario
     * @param emailDestinatario Email del destinatario
     * @param telefonoDestinatario Teléfono del destinatario (opcional)
     * @param asunto Asunto de la notificación
     * @param mensaje Mensaje de la notificación
     * @param enviado Si fue enviada exitosamente
     * @param idExterno ID externo del proveedor (opcional)
     */
    @Transactional
    private void guardarNotificacionStockEnBD(String nombreDestinatario, String emailDestinatario, 
                                              String telefonoDestinatario, String asunto, String mensaje,
                                              boolean enviado, String idExterno) {
        try {
            // Limitar el mensaje a 2000 caracteres si es muy largo
            String mensajeLimitado = mensaje != null && mensaje.length() > 2000 
                ? mensaje.substring(0, 1997) + "..." 
                : mensaje;
            
            Comunicacion comunicacion = Comunicacion.builder()
                .tipo(Constants.ENTIDAD_NOTIFICACION)
                .canal("EMAIL")
                .destinatarioNombre(nombreDestinatario != null ? nombreDestinatario : "Usuario")
                .destinatarioEmail(emailDestinatario)
                .destinatarioTelefono(telefonoDestinatario)
                .asunto(asunto)
                .mensaje(mensajeLimitado)
                .enviada(enviado)
                .build();
            
            if (enviado && idExterno != null) {
                comunicacion.marcarComoEnviada(idExterno);
            } else if (!enviado) {
                comunicacion.registrarFalloEnvio("Error al enviar notificación");
            }
            
            comunicacionRepository.save(comunicacion);
            log.debug("Notificación de stock guardada en BD: ID={}, Asunto={}", 
                    comunicacion.getIdComunicacion(), asunto);
        } catch (Exception e) {
            log.error("Error al guardar notificación de stock en BD: {}", e.getMessage(), e);
            // No propagamos la excepción para no interrumpir el flujo principal
        }
    }

}

