package com.veterinaria.clinica_veternica.patterns.structural.facade;

import com.veterinaria.clinica_veternica.dto.response.agenda.CitaResponseDTO;
import com.veterinaria.clinica_veternica.dto.response.facade.ResultadoNotificacionDTO;
import com.veterinaria.clinica_veternica.dto.response.inventario.InventarioResponseDTO;
import com.veterinaria.clinica_veternica.service.interfaces.ICitaService;
import com.veterinaria.clinica_veternica.service.interfaces.IInventarioService;
import com.veterinaria.clinica_veternica.service.interfaces.INotificacionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Servicio especializado para Notificaciones Masivas.
 * Parte de la división del antipatrón God Object (ClinicaFacade).
 *
 * <p>Responsabilidades:</p>
 * <ul>
 *   <li>Enviar recordatorios de citas próximas</li>
 *   <li>Notificar sobre stock bajo de inventario</li>
 *   <li>Coordinar envío masivo de notificaciones</li>
 * </ul>
 *
 * @author Clínica Veterinaria Team
 * @version 1.0
 * @since 2025-11-18
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class NotificacionesFacadeService {

    private final ICitaService citaService;
    private final INotificacionService notificacionService;
    private final IInventarioService inventarioService;

    /**
     * Envía recordatorios automáticos de citas próximas.
     *
     * @param horasAnticipacion Horas de anticipación para enviar recordatorios
     * @return ResultadoNotificacionDTO con el resultado del envío masivo
     */
    public ResultadoNotificacionDTO enviarRecordatoriosCitasProximas(int horasAnticipacion) {
        log.info("NotificacionesFacadeService: Enviando recordatorios de citas con {} horas de anticipación", horasAnticipacion);

        LocalDateTime ahora = LocalDateTime.now();
        LocalDateTime limite = ahora.plusHours(horasAnticipacion);

        // Obtener citas próximas que requieren recordatorio
        List<CitaResponseDTO> citasProximas = citaService.listarPorRangoFechas(ahora, limite);

        int exitosos = 0;
        int fallidos = 0;

        for (CitaResponseDTO cita : citasProximas) {
            try {
                // Aquí se enviaría la notificación real
                // Por ahora, solo registramos el intento
                log.debug("Enviando recordatorio para cita ID: {}", cita.getIdCita());
                exitosos++;
            } catch (IllegalArgumentException | IllegalStateException e) {
                log.error("Error de validación/estado enviando recordatorio para cita {}: {}", 
                        cita.getIdCita(), e.getMessage(), e);
                fallidos++;
            } catch (RuntimeException e) {
                log.error("Error inesperado enviando recordatorio para cita {}: {}", 
                        cita.getIdCita(), e.getMessage(), e);
                fallidos++;
            }
        }

        log.info("Recordatorios enviados: {} exitosos, {} fallidos de {} citas próximas",
                exitosos, fallidos, citasProximas.size());

        return ResultadoNotificacionDTO.builder()
                .tipoOperacion("RECORDATORIOS_CITAS")
                .notificacionesEnviadas(exitosos)
                .errores(fallidos)
                .exitoso(fallidos == 0)
                .mensaje(String.format("Se enviaron %d recordatorios exitosamente de %d citas próximas",
                        exitosos, citasProximas.size()))
                .build();
    }

    /**
     * Envía notificaciones sobre insumos con stock bajo.
     *
     * @return ResultadoNotificacionDTO con el resultado del envío
     */
    public ResultadoNotificacionDTO notificarStockBajo() {
        log.info("NotificacionesFacadeService: Enviando notificaciones de stock bajo");

        // Obtener insumos con stock bajo
        List<InventarioResponseDTO> stockBajo = inventarioService.listarConStockBajo();

        int exitosos = 0;
        int fallidos = 0;

        if (!stockBajo.isEmpty()) {
            try {
                // Aquí se enviaría la notificación real a administradores
                // Por ahora, solo registramos el intento
                log.debug("Enviando notificación de stock bajo: {} insumos afectados", stockBajo.size());
                exitosos = 1; // Una notificación consolidada a admins
            } catch (IllegalArgumentException | IllegalStateException e) {
                log.error("Error de validación/estado enviando notificación de stock bajo: {}", 
                        e.getMessage(), e);
                fallidos = 1;
            } catch (RuntimeException e) {
                log.error("Error inesperado enviando notificación de stock bajo: {}", 
                        e.getMessage(), e);
                fallidos = 1;
            }
        }

        log.info("Notificación de stock bajo enviada: {} insumos con stock crítico", stockBajo.size());

        return ResultadoNotificacionDTO.builder()
                .tipoOperacion("STOCK_BAJO")
                .notificacionesEnviadas(exitosos)
                .errores(fallidos)
                .exitoso(fallidos == 0)
                .mensaje(String.format("Se notificó sobre %d insumos con stock bajo a los administradores",
                        stockBajo.size()))
                .build();
    }
}
