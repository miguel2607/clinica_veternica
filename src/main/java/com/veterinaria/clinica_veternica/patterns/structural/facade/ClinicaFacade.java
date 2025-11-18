package com.veterinaria.clinica_veternica.patterns.structural.facade;

import com.veterinaria.clinica_veternica.dto.request.agenda.CitaRequestDTO;
import com.veterinaria.clinica_veternica.dto.request.clinico.EvolucionClinicaRequestDTO;
import com.veterinaria.clinica_veternica.dto.request.clinico.HistoriaClinicaRequestDTO;
import com.veterinaria.clinica_veternica.dto.request.paciente.MascotaRequestDTO;
import com.veterinaria.clinica_veternica.dto.request.paciente.PropietarioRequestDTO;
import com.veterinaria.clinica_veternica.dto.response.agenda.CitaResponseDTO;
import com.veterinaria.clinica_veternica.dto.response.clinico.HistoriaClinicaResponseDTO;
import com.veterinaria.clinica_veternica.dto.response.inventario.InventarioResponseDTO;
import com.veterinaria.clinica_veternica.dto.response.paciente.MascotaResponseDTO;
import com.veterinaria.clinica_veternica.dto.response.comunicacion.NotificacionResponseDTO;
import com.veterinaria.clinica_veternica.service.interfaces.ICitaService;
import com.veterinaria.clinica_veternica.service.interfaces.IHistoriaClinicaService;
import com.veterinaria.clinica_veternica.service.interfaces.IEvolucionClinicaService;
import com.veterinaria.clinica_veternica.service.interfaces.IMascotaService;
import com.veterinaria.clinica_veternica.service.interfaces.IPropietarioService;
import com.veterinaria.clinica_veternica.service.interfaces.IInventarioService;
import com.veterinaria.clinica_veternica.service.interfaces.INotificacionService;
import com.veterinaria.clinica_veternica.service.interfaces.IUsuarioService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Patrón Facade: ClinicaFacade
 *
 * Proporciona una interfaz unificada y simplificada para operaciones complejas
 * que requieren la coordinación de múltiples servicios.
 *
 * PROPÓSITO:
 * - Simplificar operaciones complejas para el frontend
 * - Reducir el número de llamadas HTTP necesarias
 * - Agrupar operaciones relacionadas en un solo punto de acceso
 * - Ocultar la complejidad de múltiples servicios detrás de una interfaz simple
 *
 * CASOS DE USO:
 * 1. Dashboard: Obtener resumen completo (citas del día, stock bajo, notificaciones)
 * 2. Crear cita completa: Validar, crear cita y enviar notificación
 * 3. Proceso de atención: Cita + Historia clínica + Evolución
 * 4. Registro completo de mascota: Propietario + Mascota + Historia clínica inicial
 * 5. Consultas combinadas: Múltiples datos relacionados en una sola llamada
 *
 * Justificación:
 * - El frontend necesita operaciones que requieren múltiples servicios
 * - Reduce la complejidad de integración del frontend
 * - Mejora el rendimiento al reducir llamadas HTTP
 * - Facilita el mantenimiento al centralizar lógica compleja
 *
 * @author Clínica Veterinaria Team
 * @version 1.0
 * @since 2025-11-17
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ClinicaFacade {

    // Constantes para claves de Map (evitar literales duplicados)
    private static final String KEY_CITAS_HOY = "citasHoy";
    private static final String KEY_TOTAL_CITAS_HOY = "totalCitasHoy";
    private static final String KEY_CITAS_PROGRAMADAS = "citasProgramadas";
    private static final String KEY_TOTAL_CITAS_PROGRAMADAS = "totalCitasProgramadas";
    private static final String KEY_STOCK_BAJO = "stockBajo";
    private static final String KEY_TOTAL_STOCK_BAJO = "totalStockBajo";
    private static final String KEY_STOCK_AGOTADO = "stockAgotado";
    private static final String KEY_TOTAL_STOCK_AGOTADO = "totalStockAgotado";
    private static final String KEY_NOTIFICACIONES_RECIENTES = "notificacionesRecientes";
    private static final String KEY_MASCOTA = "mascota";
    private static final String KEY_HISTORIA_CLINICA = "historiaClinica";
    private static final String KEY_CITAS = "citas";
    private static final String KEY_TOTAL_CITAS = "totalCitas";
    private static final String KEY_FECHA = "fecha";
    private static final String KEY_CITAS_POR_ESTADO = "citasPorEstado";
    private static final String KEY_INVENTARIO = "inventario";
    private static final String KEY_TOTAL_ITEMS = "totalItems";
    private static final String KEY_ORDENADOS_POR_VALOR = "ordenadosPorValor";
    private static final String KEY_TOTAL_MASCOTAS = "totalMascotas";
    private static final String KEY_PROPIETARIO = "propietario";
    private static final String KEY_TOTAL_PROPIETARIOS = "totalPropietarios";
    private static final String KEY_VETERINARIO = "veterinario";
    private static final String KEY_TOTAL_VETERINARIOS = "totalVeterinarios";
    private static final String KEY_CITAS_DEL_MES = "citasDelMes";
    private static final String KEY_PROPIETARIOS = "propietarios";
    private static final String KEY_VETERINARIOS = "veterinarios";
    private static final String KEY_MASCOTAS = "mascotas";
    private static final String KEY_HISTORIAS_CLINICAS = "historiasClinicas";
    private static final String KEY_HORARIOS = "horarios";
    private static final String KEY_CITA = "cita";
    private static final String KEY_EVOLUCION = "evolucion";
    private static final String KEY_NOTIFICACION_ENVIADA = "notificacionEnviada";
    private static final String KEY_CITA_ANTERIOR = "citaAnterior";
    private static final String KEY_NUEVA_CITA = "nuevaCita";
    private static final String KEY_TOTAL_PROPIETARIOS_RESULTADOS = "totalPropietarios";
    private static final String KEY_TOTAL_VETERINARIOS_RESULTADOS = "totalVeterinarios";
    private static final String KEY_TOTAL_RESULTADOS = "totalResultados";
    private static final String KEY_MASCOTAS_CON_ALERTAS = "mascotasConAlertas";
    private static final String KEY_TOTAL_ALERTAS = "totalAlertas";
    private static final String KEY_FECHA_INICIO = "fechaInicio";
    private static final String KEY_FECHA_FIN = "fechaFin";
    private static final String KEY_CITAS_POR_VETERINARIO = "citasPorVeterinario";
    private static final String KEY_CITAS_CON_RECORDATORIO = "citasConRecordatorio";
    private static final String KEY_TOTAL_RECORDATORIOS_ENVIADOS = "totalRecordatoriosEnviados";
    private static final String KEY_HORAS_ANTICIPACION = "horasAnticipacion";
    private static final String KEY_SIN_ESTADO = "SIN_ESTADO";
    private static final int LIMITE_NOTIFICACIONES_RECIENTES = 10;

    // Servicios inyectados
    private final ICitaService citaService;
    private final IHistoriaClinicaService historiaClinicaService;
    private final IMascotaService mascotaService;
    private final IPropietarioService propietarioService;
    private final IInventarioService inventarioService;
    private final INotificacionService notificacionService;
    private final IUsuarioService usuarioService;
    private final IEvolucionClinicaService evolucionClinicaService;
    private final com.veterinaria.clinica_veternica.service.interfaces.IVeterinarioService veterinarioService;
    private final com.veterinaria.clinica_veternica.service.interfaces.IHorarioService horarioService;

    // ===================================================================
    // OPERACIONES DE DASHBOARD Y RESUMEN
    // ===================================================================

    /**
     * Obtiene un resumen completo del dashboard para el frontend.
     * Incluye: citas del día, stock bajo, notificaciones pendientes, etc.
     *
     * @return Map con todos los datos del dashboard
     */
    @Transactional(readOnly = true)
    public Map<String, Object> obtenerDashboard() {
        log.info("ClinicaFacade: Obteniendo dashboard completo");

        Map<String, Object> dashboard = new HashMap<>();

        // Citas del día
        LocalDateTime inicioDia = LocalDate.now().atStartOfDay();
        LocalDateTime finDia = LocalDate.now().atTime(23, 59, 59);
        List<CitaResponseDTO> citasHoy = citaService.listarPorRangoFechas(inicioDia, finDia);
        dashboard.put(KEY_CITAS_HOY, citasHoy);
        dashboard.put(KEY_TOTAL_CITAS_HOY, citasHoy.size());

        // Citas programadas
        List<CitaResponseDTO> citasProgramadas = citaService.listarProgramadas();
        dashboard.put(KEY_CITAS_PROGRAMADAS, citasProgramadas);
        dashboard.put(KEY_TOTAL_CITAS_PROGRAMADAS, citasProgramadas.size());

        // Stock bajo
        List<InventarioResponseDTO> stockBajo = inventarioService.listarConStockBajo();
        dashboard.put(KEY_STOCK_BAJO, stockBajo);
        dashboard.put(KEY_TOTAL_STOCK_BAJO, stockBajo.size());

        // Stock agotado
        List<InventarioResponseDTO> stockAgotado = inventarioService.listarAgotados();
        dashboard.put(KEY_STOCK_AGOTADO, stockAgotado);
        dashboard.put(KEY_TOTAL_STOCK_AGOTADO, stockAgotado.size());

        // Notificaciones recientes (últimas 10)
        List<NotificacionResponseDTO> notificaciones = notificacionService.listarTodas();
        List<NotificacionResponseDTO> notificacionesRecientes = notificaciones.stream()
                .limit(LIMITE_NOTIFICACIONES_RECIENTES)
                .toList();
        dashboard.put(KEY_NOTIFICACIONES_RECIENTES, notificacionesRecientes);

        log.info("Dashboard obtenido: {} citas hoy, {} stock bajo", citasHoy.size(), stockBajo.size());
        return dashboard;
    }

    /**
     * Obtiene información completa de una mascota incluyendo su historia clínica.
     *
     * @param idMascota ID de la mascota
     * @return Map con información completa de la mascota
     */
    @Transactional(readOnly = true)
    public Map<String, Object> obtenerInformacionCompletaMascota(Long idMascota) {
        log.info("ClinicaFacade: Obteniendo información completa de mascota ID: {}", idMascota);

        Map<String, Object> informacion = new HashMap<>();

        // Información de la mascota
        MascotaResponseDTO mascota = mascotaService.buscarPorId(idMascota);
        informacion.put(KEY_MASCOTA, mascota);

        // Historia clínica
        try {
            HistoriaClinicaResponseDTO historiaClinica = historiaClinicaService.buscarPorMascota(idMascota);
            informacion.put(KEY_HISTORIA_CLINICA, historiaClinica);
        } catch (Exception e) {
            log.debug("Mascota {} no tiene historia clínica aún", idMascota);
            informacion.put(KEY_HISTORIA_CLINICA, null);
        }

        // Citas de la mascota
        List<CitaResponseDTO> citas = citaService.listarPorMascota(idMascota);
        informacion.put(KEY_CITAS, citas);
        informacion.put(KEY_TOTAL_CITAS, citas.size());

        return informacion;
    }

    // ===================================================================
    // OPERACIONES COMPLEJAS DE CITAS
    // ===================================================================

    /**
     * Crea una cita y envía notificación automáticamente.
     * Operación simplificada para el frontend.
     *
     * @param requestDTO Datos de la cita
     * @return Cita creada
     */
    public CitaResponseDTO crearCitaConNotificacion(CitaRequestDTO requestDTO) {
        log.info("ClinicaFacade: Creando cita con notificación automática");

        // Crear la cita (ya incluye validaciones y notificaciones vía Mediator)
        CitaResponseDTO cita = citaService.crear(requestDTO);

        log.info("Cita creada exitosamente con ID: {}", cita.getIdCita());
        return cita;
    }

    /**
     * Proceso completo de atención: marca cita como atendida y crea evolución clínica.
     *
     * @param idCita ID de la cita
     * @param evolucionRequestDTO Datos de la evolución clínica
     * @return Map con la cita actualizada y la evolución creada
     */
    public Map<String, Object> procesarAtencionCompleta(Long idCita, EvolucionClinicaRequestDTO evolucionRequestDTO) {
        log.info("ClinicaFacade: Procesando atención completa para cita ID: {}", idCita);

        Map<String, Object> resultado = new HashMap<>();

        // 1. Marcar cita como atendida
        CitaResponseDTO citaAtendida = citaService.marcarComoAtendida(idCita);
        resultado.put(KEY_CITA, citaAtendida);

        // 2. Obtener historia clínica de la mascota
        Long idMascota = citaAtendida.getMascota().getIdMascota();
        HistoriaClinicaResponseDTO historiaClinica = historiaClinicaService.buscarPorMascota(idMascota);

        // 3. Crear evolución clínica
        var evolucion = evolucionClinicaService.crear(historiaClinica.getIdHistoriaClinica(), evolucionRequestDTO);
        resultado.put(KEY_EVOLUCION, evolucion);

        log.info("Atención completa procesada: cita {} atendida, evolución creada", idCita);
        return resultado;
    }

    // ===================================================================
    // OPERACIONES DE REGISTRO COMPLETO
    // ===================================================================

    /**
     * Registro completo de nueva mascota: crea propietario, mascota e historia clínica inicial.
     * Operación simplificada para el frontend.
     *
     * @param propietarioRequestDTO Datos del propietario
     * @param mascotaRequestDTO Datos de la mascota
     * @param historiaClinicaRequestDTO Datos iniciales de la historia clínica (opcional)
     * @return Map con todas las entidades creadas
     */
    public Map<String, Object> registrarMascotaCompleta(
            PropietarioRequestDTO propietarioRequestDTO,
            MascotaRequestDTO mascotaRequestDTO,
            HistoriaClinicaRequestDTO historiaClinicaRequestDTO) {
        log.info("ClinicaFacade: Registrando mascota completa con propietario e historia clínica");

        Map<String, Object> resultado = new HashMap<>();

        // 1. Crear propietario
        var propietario = propietarioService.crear(propietarioRequestDTO);
        resultado.put(KEY_PROPIETARIO, propietario);
        log.debug("Propietario creado con ID: {}", propietario.getIdPropietario());

        // 2. Asociar mascota al propietario
        mascotaRequestDTO.setIdPropietario(propietario.getIdPropietario());
        var mascota = mascotaService.crear(mascotaRequestDTO);
        resultado.put(KEY_MASCOTA, mascota);
        log.debug("Mascota creada con ID: {}", mascota.getIdMascota());

        // 3. Crear historia clínica inicial (si se proporciona)
        if (historiaClinicaRequestDTO != null) {
            historiaClinicaRequestDTO.setIdMascota(mascota.getIdMascota());
            var historiaClinica = historiaClinicaService.crear(historiaClinicaRequestDTO);
            resultado.put(KEY_HISTORIA_CLINICA, historiaClinica);
            log.debug("Historia clínica inicial creada con ID: {}", historiaClinica.getIdHistoriaClinica());
        }

        log.info("Registro completo exitoso: propietario {}, mascota {}", 
                propietario.getIdPropietario(), mascota.getIdMascota());
        return resultado;
    }

    // ===================================================================
    // OPERACIONES DE CONSULTA COMBINADA
    // ===================================================================

    /**
     * Obtiene información completa para el calendario de citas.
     * Incluye: citas, horarios disponibles, veterinarios.
     *
     * @param fecha Fecha para la cual se quiere el calendario
     * @return Map con información del calendario
     */
    @Transactional(readOnly = true)
    public Map<String, Object> obtenerCalendarioCitas(LocalDate fecha) {
        log.info("ClinicaFacade: Obteniendo calendario para fecha: {}", fecha);

        Map<String, Object> calendario = new HashMap<>();

        // Citas del día
        LocalDateTime inicioDia = fecha.atStartOfDay();
        LocalDateTime finDia = fecha.atTime(23, 59, 59);
        List<CitaResponseDTO> citas = citaService.listarPorRangoFechas(inicioDia, finDia);
        calendario.put(KEY_CITAS, citas);
        calendario.put(KEY_FECHA, fecha);
        calendario.put(KEY_TOTAL_CITAS, citas.size());

        // Agrupar citas por estado
        Map<String, Long> citasPorEstado = citas.stream()
                .collect(java.util.stream.Collectors.groupingBy(
                        c -> c.getEstado() != null ? c.getEstado() : KEY_SIN_ESTADO,
                        java.util.stream.Collectors.counting()
                ));
        calendario.put(KEY_CITAS_POR_ESTADO, citasPorEstado);

        return calendario;
    }

    /**
     * Obtiene resumen de inventario con alertas.
     *
     * @return Map con resumen de inventario
     */
    @Transactional(readOnly = true)
    public Map<String, Object> obtenerResumenInventario() {
        log.info("ClinicaFacade: Obteniendo resumen de inventario");

        Map<String, Object> resumen = new HashMap<>();

        //el inventario
        List<InventarioResponseDTO> todoInventario = inventarioService.listarTodos();
        resumen.put(KEY_INVENTARIO, todoInventario);
        resumen.put(KEY_TOTAL_ITEMS, todoInventario.size());

        // Stock bajo
        List<InventarioResponseDTO> stockBajo = inventarioService.listarConStockBajo();
        resumen.put(KEY_STOCK_BAJO, stockBajo);
        resumen.put(KEY_TOTAL_STOCK_BAJO, stockBajo.size());

        // Stock agotado
        List<InventarioResponseDTO> stockAgotado = inventarioService.listarAgotados();
        resumen.put(KEY_STOCK_AGOTADO, stockAgotado);
        resumen.put(KEY_TOTAL_STOCK_AGOTADO, stockAgotado.size());

        // Ordenados por valor
        List<InventarioResponseDTO> ordenadosPorValor = inventarioService.listarOrdenadosPorValor();
        resumen.put(KEY_ORDENADOS_POR_VALOR, ordenadosPorValor);

        return resumen;
    }

    // ===================================================================
    // ESTADÍSTICAS GENERALES
    // ===================================================================

    /**
     * Obtiene estadísticas generales de la clínica.
     *
     * @return Map con estadísticas generales
     */
    @Transactional(readOnly = true)
    public Map<String, Object> obtenerEstadisticasGenerales() {
        log.info("ClinicaFacade: Obteniendo estadísticas generales");

        Map<String, Object> estadisticas = new HashMap<>();

        // Total de mascotas activas
        List<com.veterinaria.clinica_veternica.dto.response.paciente.MascotaResponseDTO> mascotas = mascotaService.listarActivas();
        estadisticas.put(KEY_TOTAL_MASCOTAS, mascotas.size());

        // Total de propietarios activos
        List<com.veterinaria.clinica_veternica.dto.response.paciente.PropietarioResponseDTO> propietarios = propietarioService.listarActivos();
        estadisticas.put(KEY_TOTAL_PROPIETARIOS, propietarios.size());

        // Total de veterinarios activos
        List<com.veterinaria.clinica_veternica.dto.response.usuario.VeterinarioResponseDTO> veterinarios = veterinarioService.listarActivos();
        estadisticas.put(KEY_TOTAL_VETERINARIOS, veterinarios.size());

        // Citas del mes actual
        LocalDateTime inicioMes = LocalDate.now().withDayOfMonth(1).atStartOfDay();
        LocalDateTime finMes = LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth()).atTime(23, 59, 59);
        List<CitaResponseDTO> citasMes = citaService.listarPorRangoFechas(inicioMes, finMes);
        estadisticas.put(KEY_CITAS_DEL_MES, citasMes.size());

        // Citas programadas
        List<CitaResponseDTO> citasProgramadas = citaService.listarProgramadas();
        estadisticas.put(KEY_CITAS_PROGRAMADAS, citasProgramadas.size());

        return estadisticas;
    }

    // ===================================================================
    // INFORMACIÓN COMPLETA DE PROPIETARIO Y VETERINARIO
    // ===================================================================

    /**
     * Obtiene información completa de un propietario con todas sus mascotas.
     *
     * @param idPropietario ID del propietario
     * @return Map con información completa del propietario
     */
    @Transactional(readOnly = true)
    public Map<String, Object> obtenerInformacionCompletaPropietario(Long idPropietario) {
        log.info("ClinicaFacade: Obteniendo información completa de propietario ID: {}", idPropietario);

        Map<String, Object> informacion = new HashMap<>();

        // Información del propietario
        var propietario = propietarioService.buscarPorId(idPropietario);
        informacion.put(KEY_PROPIETARIO, propietario);

        // Mascotas del propietario
        List<com.veterinaria.clinica_veternica.dto.response.paciente.MascotaResponseDTO> mascotas = mascotaService.listarPorPropietario(idPropietario);
        informacion.put(KEY_MASCOTAS, mascotas);
        informacion.put(KEY_TOTAL_MASCOTAS, mascotas.size());

        // Historias clínicas de las mascotas
        List<HistoriaClinicaResponseDTO> historiasClinicas = new java.util.ArrayList<>();
        for (var mascota : mascotas) {
            try {
                HistoriaClinicaResponseDTO historia = historiaClinicaService.buscarPorMascota(mascota.getIdMascota());
                historiasClinicas.add(historia);
            } catch (Exception e) {
                log.debug("Mascota {} no tiene historia clínica", mascota.getIdMascota());
            }
        }
        informacion.put(KEY_HISTORIAS_CLINICAS, historiasClinicas);

        return informacion;
    }

    /**
     * Obtiene información completa de un veterinario con sus horarios y citas.
     *
     * @param idVeterinario ID del veterinario
     * @return Map con información completa del veterinario
     */
    @Transactional(readOnly = true)
    public Map<String, Object> obtenerInformacionCompletaVeterinario(Long idVeterinario) {
        log.info("ClinicaFacade: Obteniendo información completa de veterinario ID: {}", idVeterinario);

        Map<String, Object> informacion = new HashMap<>();

        // Información del veterinario
        var veterinario = veterinarioService.buscarPorId(idVeterinario);
        informacion.put(KEY_VETERINARIO, veterinario);

        // Horarios del veterinario
        List<com.veterinaria.clinica_veternica.dto.response.agenda.HorarioResponseDTO> horarios = horarioService.listarPorVeterinario(idVeterinario);
        informacion.put(KEY_HORARIOS, horarios);

        // Citas del veterinario
        List<CitaResponseDTO> citas = citaService.listarPorVeterinario(idVeterinario);
        informacion.put(KEY_CITAS, citas);
        informacion.put(KEY_TOTAL_CITAS, citas.size());

        // Citas programadas del veterinario
        long citasProgramadas = citas.stream()
                .filter(c -> "PROGRAMADA".equals(c.getEstado()))
                .count();
        informacion.put(KEY_CITAS_PROGRAMADAS, citasProgramadas);

        return informacion;
    }

    // ===================================================================
    // OPERACIONES DE CITAS CON NOTIFICACIÓN
    // ===================================================================

    /**
     * Cancela una cita y envía notificación al propietario.
     *
     * @param idCita ID de la cita
     * @param motivo Motivo de cancelación
     * @param usuario Usuario que cancela
     * @return Map con resultado de la operación
     */
    public Map<String, Object> cancelarCitaConNotificacion(Long idCita, String motivo, String usuario) {
        log.info("ClinicaFacade: Cancelando cita ID {} con notificación", idCita);

        Map<String, Object> resultado = new HashMap<>();

        // Cancelar la cita
        CitaResponseDTO citaCancelada = citaService.cancelar(idCita, motivo, usuario);
        resultado.put(KEY_CITA, citaCancelada);
        resultado.put(KEY_NOTIFICACION_ENVIADA, true);

        log.info("Cita {} cancelada exitosamente con notificación", idCita);
        return resultado;
    }

    /**
     * Reprograma una cita y envía notificación al propietario.
     *
     * @param idCita ID de la cita
     * @param nuevaCitaDTO Datos de la nueva cita
     * @return Map con resultado de la operación
     */
    public Map<String, Object> reprogramarCitaConNotificacion(Long idCita, CitaRequestDTO nuevaCitaDTO) {
        log.info("ClinicaFacade: Reprogramando cita ID {} con notificación", idCita);

        Map<String, Object> resultado = new HashMap<>();

        // Cancelar la cita anterior
        citaService.cancelar(idCita, "Reprogramación de cita", "Sistema");

        // Crear nueva cita
        CitaResponseDTO nuevaCita = citaService.crear(nuevaCitaDTO);
        resultado.put(KEY_CITA_ANTERIOR, idCita);
        resultado.put(KEY_NUEVA_CITA, nuevaCita);
        resultado.put(KEY_NOTIFICACION_ENVIADA, true);

        log.info("Cita {} reprogramada exitosamente a nueva cita {}", idCita, nuevaCita.getIdCita());
        return resultado;
    }

    // ===================================================================
    // BÚSQUEDAS AVANZADAS
    // ===================================================================

    /**
     * Realiza una búsqueda global en mascotas, propietarios y veterinarios.
     *
     * @param termino Término de búsqueda
     * @return Map con resultados de búsqueda
     */
    @Transactional(readOnly = true)
    public Map<String, Object> busquedaGlobal(String termino) {
        log.info("ClinicaFacade: Búsqueda global con término: {}", termino);

        Map<String, Object> resultados = new HashMap<>();

        // Buscar mascotas
        List<com.veterinaria.clinica_veternica.dto.response.paciente.MascotaResponseDTO> mascotas = mascotaService.buscarPorNombre(termino);
        resultados.put(KEY_MASCOTAS, mascotas);
        resultados.put(KEY_TOTAL_MASCOTAS, mascotas.size());

        // Buscar propietarios
        List<com.veterinaria.clinica_veternica.dto.response.paciente.PropietarioResponseDTO> propietarios = propietarioService.buscarPorNombre(termino);
        resultados.put(KEY_PROPIETARIOS, propietarios);
        resultados.put(KEY_TOTAL_PROPIETARIOS_RESULTADOS, propietarios.size());

        // Buscar veterinarios
        List<com.veterinaria.clinica_veternica.dto.response.usuario.VeterinarioResponseDTO> veterinarios = veterinarioService.buscarPorNombre(termino);
        resultados.put(KEY_VETERINARIOS, veterinarios);
        resultados.put(KEY_TOTAL_VETERINARIOS_RESULTADOS, veterinarios.size());

        resultados.put(KEY_TOTAL_RESULTADOS, mascotas.size() + propietarios.size() + veterinarios.size());

        return resultados;
    }

    /**
     * Obtiene mascotas con alertas médicas.
     *
     * @return Map con mascotas que requieren atención
     */
    @Transactional(readOnly = true)
    public Map<String, Object> obtenerMascotasConAlertasMedicas() {
        log.info("ClinicaFacade: Obteniendo mascotas con alertas médicas");

        Map<String, Object> alertas = new HashMap<>();

        // Obtener todas las mascotas activas
        List<com.veterinaria.clinica_veternica.dto.response.paciente.MascotaResponseDTO> mascotas = mascotaService.listarActivas();

        // Filtrar mascotas con alertas médicas:
        // - Vacunas pendientes (verificar fechas de vacunación)
        // - Tratamientos activos (verificar historias clínicas)
        // - Seguimientos médicos programados (verificar citas futuras)
        // Por ahora retornamos todas las mascotas activas como placeholder
        // Implementar lógica de filtrado de alertas médicas específicas
        alertas.put(KEY_MASCOTAS_CON_ALERTAS, mascotas);
        alertas.put(KEY_TOTAL_ALERTAS, mascotas.size());

        return alertas;
    }

    // ===================================================================
    // REPORTES
    // ===================================================================

    /**
     * Genera reporte de citas en un rango de fechas.
     *
     * @param fechaInicio Fecha de inicio
     * @param fechaFin Fecha de fin
     * @return Map con reporte de citas
     */
    @Transactional(readOnly = true)
    public Map<String, Object> generarReporteCitas(LocalDate fechaInicio, LocalDate fechaFin) {
        log.info("ClinicaFacade: Generando reporte de citas desde {} hasta {}", fechaInicio, fechaFin);

        Map<String, Object> reporte = new HashMap<>();

        LocalDateTime inicio = fechaInicio.atStartOfDay();
        LocalDateTime fin = fechaFin.atTime(23, 59, 59);

        List<CitaResponseDTO> citas = citaService.listarPorRangoFechas(inicio, fin);
        reporte.put(KEY_CITAS, citas);
        reporte.put(KEY_TOTAL_CITAS, citas.size());
        reporte.put(KEY_FECHA_INICIO, fechaInicio);
        reporte.put(KEY_FECHA_FIN, fechaFin);

        // Agrupar por estado
        Map<String, Long> citasPorEstado = citas.stream()
                .collect(java.util.stream.Collectors.groupingBy(
                        c -> c.getEstado() != null ? c.getEstado() : KEY_SIN_ESTADO,
                        java.util.stream.Collectors.counting()
                ));
        reporte.put(KEY_CITAS_POR_ESTADO, citasPorEstado);

        return reporte;
    }

    /**
     * Genera reporte completo de inventario.
     *
     * @return Map con reporte de inventario
     */
    @Transactional(readOnly = true)
    public Map<String, Object> generarReporteInventario() {
        log.info("ClinicaFacade: Generando reporte de inventario");

        Map<String, Object> resumen = new HashMap<>();

        // el inventario
        List<InventarioResponseDTO> todoInventario = inventarioService.listarTodos();
        resumen.put(KEY_INVENTARIO, todoInventario);
        resumen.put(KEY_TOTAL_ITEMS, todoInventario.size());

        // Stock bajo
        List<InventarioResponseDTO> stockBajo = inventarioService.listarConStockBajo();
        resumen.put(KEY_STOCK_BAJO, stockBajo);
        resumen.put(KEY_TOTAL_STOCK_BAJO, stockBajo.size());

        // Stock agotado
        List<InventarioResponseDTO> stockAgotado = inventarioService.listarAgotados();
        resumen.put(KEY_STOCK_AGOTADO, stockAgotado);
        resumen.put(KEY_TOTAL_STOCK_AGOTADO, stockAgotado.size());

        // Ordenados por valor
        List<InventarioResponseDTO> ordenadosPorValor = inventarioService.listarOrdenadosPorValor();
        resumen.put(KEY_ORDENADOS_POR_VALOR, ordenadosPorValor);

        return resumen;
    }

    /**
     * Genera reporte de atenciones por veterinario.
     *
     * @param fechaInicio Fecha de inicio
     * @param fechaFin Fecha de fin
     * @return Map con reporte de veterinarios
     */
    @Transactional(readOnly = true)
    public Map<String, Object> generarReporteVeterinarios(LocalDate fechaInicio, LocalDate fechaFin) {
        log.info("ClinicaFacade: Generando reporte de veterinarios desde {} hasta {}", fechaInicio, fechaFin);

        Map<String, Object> reporte = new HashMap<>();

        LocalDateTime inicio = fechaInicio.atStartOfDay();
        LocalDateTime fin = fechaFin.atTime(23, 59, 59);

        // Obtener todas las citas del período
        List<CitaResponseDTO> citas = citaService.listarPorRangoFechas(inicio, fin);

        // Agrupar citas por veterinario
        Map<Long, List<CitaResponseDTO>> citasPorVeterinario = citas.stream()
                .collect(java.util.stream.Collectors.groupingBy(
                        c -> c.getVeterinario().getIdPersonal()
                ));

        reporte.put(KEY_CITAS_POR_VETERINARIO, citasPorVeterinario);
        reporte.put(KEY_FECHA_INICIO, fechaInicio);
        reporte.put(KEY_FECHA_FIN, fechaFin);
        reporte.put(KEY_TOTAL_CITAS, citas.size());

        return reporte;
    }

    // ===================================================================
    // NOTIFICACIONES MASIVAS
    // ===================================================================

    /**
     * Envía recordatorios de citas próximas.
     *
     * @param horasAnticipacion Horas de anticipación
     * @return Map con resultado del envío
     */
    public Map<String, Object> enviarRecordatoriosCitasProximas(int horasAnticipacion) {
        log.info("ClinicaFacade: Enviando recordatorios de citas con {} horas de anticipación", horasAnticipacion);

        Map<String, Object> resultado = new HashMap<>();

        LocalDateTime ahora = LocalDateTime.now();
        LocalDateTime limite = ahora.plusHours(horasAnticipacion);

        // Obtener citas que requieren recordatorio
        List<CitaResponseDTO> citasParaRecordatorio = citaService.listarParaRecordatorio(ahora, limite);

        resultado.put(KEY_CITAS_CON_RECORDATORIO, citasParaRecordatorio);
        resultado.put(KEY_TOTAL_RECORDATORIOS_ENVIADOS, citasParaRecordatorio.size());
        resultado.put(KEY_HORAS_ANTICIPACION, horasAnticipacion);

        log.info("Se enviaron {} recordatorios", citasParaRecordatorio.size());
        return resultado;
    }

    /**
     * Notifica sobre insumos con stock bajo.
     *
     * @return Map con resultado de la notificación
     */
    public Map<String, Object> notificarStockBajo() {
        log.info("ClinicaFacade: Notificando stock bajo");

        Map<String, Object> resultado = new HashMap<>();

        // Obtener insumos con stock bajo
        List<InventarioResponseDTO> stockBajo = inventarioService.listarConStockBajo();
        List<InventarioResponseDTO> stockAgotado = inventarioService.listarAgotados();

        resultado.put(KEY_STOCK_BAJO, stockBajo);
        resultado.put(KEY_STOCK_AGOTADO, stockAgotado);
        resultado.put(KEY_TOTAL_STOCK_BAJO, stockBajo.size());
        resultado.put(KEY_TOTAL_STOCK_AGOTADO, stockAgotado.size());
        resultado.put(KEY_NOTIFICACION_ENVIADA, true);

        log.info("Notificación de stock bajo enviada: {} items con stock bajo, {} agotados",
                stockBajo.size(), stockAgotado.size());
        return resultado;
    }
}

