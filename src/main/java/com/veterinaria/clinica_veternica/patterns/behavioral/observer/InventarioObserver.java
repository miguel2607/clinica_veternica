package com.veterinaria.clinica_veternica.patterns.behavioral.observer;

import com.veterinaria.clinica_veternica.domain.agenda.Cita;
import com.veterinaria.clinica_veternica.domain.inventario.Inventario;
import com.veterinaria.clinica_veternica.patterns.creational.abstractfactory.EmailNotificacionFactory;
import com.veterinaria.clinica_veternica.patterns.creational.abstractfactory.NotificacionFactory;
import com.veterinaria.clinica_veternica.repository.InventarioRepository;
import com.veterinaria.clinica_veternica.service.interfaces.IInventarioService;
import com.veterinaria.clinica_veternica.util.Constants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

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
                ALERTA: Stock bajo detectado
                
                Insumo: %s
                Stock disponible: %d
                Stock mínimo: %d
                Stock máximo: %s
                
                Por favor, considere realizar una compra para reponer el inventario.""",
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
                ALERTA CRÍTICA: Stock muy bajo
                
                Insumo: %s
                Stock disponible: %d
                Stock mínimo: %d
                
                URGENTE: Se requiere reposición inmediata.""",
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
                ALERTA URGENTE: Stock agotado
                
                Insumo: %s
                Stock disponible: 0
                Stock mínimo: %d
                
                URGENTE: El insumo se ha agotado. Se requiere reposición inmediata.""",
                inventario.getInsumo().getNombre(),
                inventario.getInsumo().getStockMinimo()
        );

        enviarNotificacion("Alerta Urgente: Stock Agotado", mensaje);
    }

    /**
     * Envía una notificación usando el Abstract Factory.
     *
     * PROPÓSITO: Centraliza el envío de notificaciones de inventario.
     *
     * @param asunto Asunto de la notificación
     * @param mensaje Mensaje de la notificación
     */
    private void enviarNotificacion(String asunto, String mensaje) {
        try {
            // En producción, se obtendría el email del administrador o responsable de compras
            String emailDestino = "admin@clinicaveterinaria.com"; // Configurable
            
            var mensajeNotificacion = emailFactory.crearMensaje(emailDestino, asunto, mensaje);
            var enviador = emailFactory.crearEnviador();
            enviador.enviar(mensajeNotificacion);
            
            log.info("Notificación de inventario enviada: {}", asunto);
        } catch (IllegalArgumentException | IllegalStateException e) {
            log.error("Error de validación al enviar notificación de inventario: {}", e.getMessage(), e);
        } catch (RuntimeException e) {
            log.error("Error al enviar notificación de inventario: {}", e.getMessage(), e);
        }
    }

}

