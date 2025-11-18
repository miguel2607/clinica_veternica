package com.veterinaria.clinica_veternica.controller;

import com.veterinaria.clinica_veternica.dto.request.comunicacion.NotificacionRequestDTO;
import com.veterinaria.clinica_veternica.dto.response.comunicacion.NotificacionResponseDTO;
import com.veterinaria.clinica_veternica.service.interfaces.INotificacionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller REST para gestión de notificaciones.
 * Usa Abstract Factory Pattern para diferentes canales (EMAIL, SMS, WHATSAPP, PUSH).
 *
 * @author Clínica Veterinaria Team
 * @version 1.0
 * @since 2025-01-XX
 */
@RestController
@RequestMapping("/api/notificaciones")
@RequiredArgsConstructor
@Tag(name = "Notificaciones", description = "API para gestión de notificaciones (usa Abstract Factory pattern)")
public class NotificacionController {

    private final INotificacionService notificacionService;

    @Operation(summary = "Enviar notificación a un usuario", 
               description = "Envía una notificación a un usuario por el canal especificado (EMAIL, SMS, WHATSAPP, PUSH). Todo es simulado.")
    @PreAuthorize("hasAnyRole('ADMIN', 'VETERINARIO', 'RECEPCIONISTA')")
    @PostMapping
    public ResponseEntity<NotificacionResponseDTO> enviarNotificacion(@Valid @RequestBody NotificacionRequestDTO requestDTO) {
        return new ResponseEntity<>(notificacionService.enviarNotificacion(requestDTO), HttpStatus.CREATED);
    }

    @Operation(summary = "Buscar notificación por ID")
    @GetMapping("/{id}")
    public ResponseEntity<NotificacionResponseDTO> buscarPorId(
            @Parameter(description = "ID de la notificación") @PathVariable Long id) {
        return ResponseEntity.ok(notificacionService.buscarPorId(id));
    }

    @Operation(summary = "Listar todas las notificaciones")
    @GetMapping
    public ResponseEntity<List<NotificacionResponseDTO>> listarTodas() {
        return ResponseEntity.ok(notificacionService.listarTodas());
    }

    @Operation(summary = "Listar notificaciones por usuario")
    @GetMapping("/usuario/{idUsuario}")
    public ResponseEntity<List<NotificacionResponseDTO>> listarPorUsuario(
            @Parameter(description = "ID del usuario") @PathVariable Long idUsuario) {
        return ResponseEntity.ok(notificacionService.listarPorUsuario(idUsuario));
    }

    @Operation(summary = "Listar notificaciones por canal")
    @GetMapping("/canal/{canal}")
    public ResponseEntity<List<NotificacionResponseDTO>> listarPorCanal(
            @Parameter(description = "Canal (EMAIL, SMS, WHATSAPP, PUSH)") @PathVariable String canal) {
        return ResponseEntity.ok(notificacionService.listarPorCanal(canal));
    }

    @Operation(summary = "Listar notificaciones enviadas")
    @GetMapping("/enviadas")
    public ResponseEntity<List<NotificacionResponseDTO>> listarEnviadas() {
        return ResponseEntity.ok(notificacionService.listarEnviadas());
    }

    @Operation(summary = "Listar notificaciones pendientes")
    @GetMapping("/pendientes")
    public ResponseEntity<List<NotificacionResponseDTO>> listarPendientes() {
        return ResponseEntity.ok(notificacionService.listarPendientes());
    }
}

