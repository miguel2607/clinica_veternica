package com.veterinaria.clinica_veternica.patterns.behavioral.observer;

import com.veterinaria.clinica_veternica.domain.agenda.Cita;
import com.veterinaria.clinica_veternica.domain.inventario.Insumo;
import com.veterinaria.clinica_veternica.domain.inventario.Inventario;
import com.veterinaria.clinica_veternica.patterns.creational.abstractfactory.EmailNotificacionFactory;
import com.veterinaria.clinica_veternica.patterns.creational.abstractfactory.EnviadorNotificacion;
import com.veterinaria.clinica_veternica.patterns.creational.abstractfactory.MensajeNotificacion;
import com.veterinaria.clinica_veternica.repository.InventarioRepository;
import com.veterinaria.clinica_veternica.service.interfaces.IInventarioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.*;

/**
 * Tests para el patrón Observer - InventarioObserver
 */
@ExtendWith(MockitoExtension.class)
class InventarioObserverTest {

    @Mock
    private InventarioRepository inventarioRepository;

    @Mock
    private EmailNotificacionFactory emailFactory;

    @Mock
    private IInventarioService inventarioService;

    @InjectMocks
    private InventarioObserver inventarioObserver;

    private Inventario inventario;
    private Insumo insumo;
    private Cita cita;

    @BeforeEach
    void setUp() {
        insumo = Insumo.builder()
                .idInsumo(1L)
                .nombre("Jeringa 5ml")
                .stockMinimo(50)
                .stockMaximo(500)
                .build();

        inventario = Inventario.builder()
                .idInventario(1L)
                .insumo(insumo)
                .cantidadActual(100)
                .build();

        cita = Cita.builder()
                .idCita(1L)
                .build();
    }

    @ParameterizedTest(name = "Cambio {0} -> {1} no registra consumo adicional")
    @MethodSource("estadoCitaScenarios")
    void debeManejarCambiosDeEstadoSinRegistrarConsumo(String estadoAnterior, String estadoNuevo) {
        // Act
        inventarioObserver.onCitaStateChanged(cita, estadoAnterior, estadoNuevo);

        // Assert (implementación simplificada para efectos de la prueba)
        verify(inventarioRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debe verificar insumos cuando se crea una cita")
    void debeVerificarInsumosCuandoSeCreaCita() {
        // Act
        inventarioObserver.onCitaCreated(cita);

        // Assert - Solo verificamos que el método se ejecuta sin errores
        verify(inventarioRepository, never()).save(any());
    }

    @ParameterizedTest(name = "Debe enviar alerta con texto \"{1}\" cuando stock es {0}")
    @MethodSource("stockAlertScenarios")
    void debeDetectarAlertasDeStock(int cantidadActual, String textoEsperado) {
        // Arrange
        inventario.setCantidadActual(cantidadActual);
        when(inventarioRepository.findAll()).thenReturn(Collections.singletonList(inventario));

        MensajeNotificacion mensaje = mock(MensajeNotificacion.class);
        EnviadorNotificacion enviador = mock(EnviadorNotificacion.class);
        when(emailFactory.crearMensaje(anyString(), anyString(), anyString()))
                .thenReturn(mensaje);
        when(emailFactory.crearEnviador()).thenReturn(enviador);

        // Act
        inventarioObserver.monitorearStock();

        // Assert
        verify(emailFactory, times(1)).crearMensaje(
                anyString(),
                contains(textoEsperado),
                anyString()
        );
        verify(enviador, times(1)).enviar(mensaje);
    }

    @Test
    @DisplayName("No debe enviar alertas cuando stock está por encima del mínimo")
    void noDebeEnviarAlertasCuandoStockEstaPorEncimaDelMinimo() {
        // Arrange
        inventario.setCantidadActual(100);
        when(inventarioRepository.findAll()).thenReturn(Collections.singletonList(inventario));

        // Act
        inventarioObserver.monitorearStock();

        // Assert
        verify(emailFactory, never()).crearMensaje(anyString(), anyString(), anyString());
        verify(emailFactory, never()).crearEnviador();
    }

    @Test
    @DisplayName("Debe monitorear múltiples inventarios correctamente")
    void debeMonitorearMultiplesInventariosCorrectamente() {
        // Arrange
        Inventario inventario1 = Inventario.builder()
                .idInventario(1L)
                .insumo(Insumo.builder().idInsumo(1L).nombre("Insumo 1").stockMinimo(50).build())
                .cantidadActual(100)
                .build();

        Inventario inventario2 = Inventario.builder()
                .idInventario(2L)
                .insumo(Insumo.builder().idInsumo(2L).nombre("Insumo 2").stockMinimo(30).build())
                .cantidadActual(20)
                .build();

        Inventario inventario3 = Inventario.builder()
                .idInventario(3L)
                .insumo(Insumo.builder().idInsumo(3L).nombre("Insumo 3").stockMinimo(20).build())
                .cantidadActual(0)
                .build();

        when(inventarioRepository.findAll()).thenReturn(Arrays.asList(inventario1, inventario2, inventario3));

        MensajeNotificacion mensaje = mock(MensajeNotificacion.class);
        EnviadorNotificacion enviador = mock(EnviadorNotificacion.class);
        when(emailFactory.crearMensaje(anyString(), anyString(), anyString()))
                .thenReturn(mensaje);
        when(emailFactory.crearEnviador()).thenReturn(enviador);

        // Act
        inventarioObserver.monitorearStock();

        // Assert
        // Debe enviar 2 alertas (inventario2 crítico e inventario3 agotado)
        verify(emailFactory, times(2)).crearMensaje(anyString(), anyString(), anyString());
        verify(enviador, times(2)).enviar(mensaje);
    }

    @Test
    @DisplayName("Debe manejar error al enviar notificación de stock bajo")
    void debeManejarErrorAlEnviarNotificacionDeStockBajo() {
        // Arrange
        inventario.setCantidadActual(50);
        when(inventarioRepository.findAll()).thenReturn(Collections.singletonList(inventario));
        when(emailFactory.crearMensaje(anyString(), anyString(), anyString()))
                .thenThrow(new IllegalArgumentException("Email inválido"));

        // Act & Assert - No debe lanzar excepción
        inventarioObserver.monitorearStock();

        verify(emailFactory, times(1)).crearMensaje(anyString(), anyString(), anyString());
    }

    @Test
    @DisplayName("Debe manejar error al enviar notificación de stock agotado")
    void debeManejarErrorAlEnviarNotificacionDeStockAgotado() {
        // Arrange
        inventario.setCantidadActual(0);
        when(inventarioRepository.findAll()).thenReturn(Collections.singletonList(inventario));
        when(emailFactory.crearMensaje(anyString(), anyString(), anyString()))
                .thenThrow(new RuntimeException("Error de conexión"));

        // Act & Assert - No debe lanzar excepción
        inventarioObserver.monitorearStock();

        verify(emailFactory, times(1)).crearMensaje(anyString(), anyString(), anyString());
    }

    @Test
    @DisplayName("Debe monitorear inventario cuando no hay inventarios")
    void debeMonitorearInventarioCuandoNoHayInventarios() {
        // Arrange
        when(inventarioRepository.findAll()).thenReturn(Collections.emptyList());

        // Act
        inventarioObserver.monitorearStock();

        // Assert
        verify(inventarioRepository, times(1)).findAll();
        verify(emailFactory, never()).crearMensaje(anyString(), anyString(), anyString());
    }

    @Test
    @DisplayName("Debe detectar stock crítico exactamente en el límite (50% del mínimo)")
    void debeDetectarStockCriticoEnElLimite() {
        // Arrange
        inventario.setCantidadActual(25); // Exactamente 50% del mínimo (50)
        when(inventarioRepository.findAll()).thenReturn(Collections.singletonList(inventario));

        MensajeNotificacion mensaje = mock(MensajeNotificacion.class);
        EnviadorNotificacion enviador = mock(EnviadorNotificacion.class);
        when(emailFactory.crearMensaje(anyString(), anyString(), anyString()))
                .thenReturn(mensaje);
        when(emailFactory.crearEnviador()).thenReturn(enviador);

        // Act
        inventarioObserver.monitorearStock();

        // Assert
        verify(emailFactory, times(1)).crearMensaje(
                anyString(),
                contains("Crítica"),
                anyString()
        );
    }

    @Test
    @DisplayName("Debe incluir información del insumo en mensaje de alerta")
    void debeIncluirInformacionDelInsumoEnMensajeDeAlerta() {
        // Arrange
        inventario.setCantidadActual(0);
        when(inventarioRepository.findAll()).thenReturn(Collections.singletonList(inventario));

        MensajeNotificacion mensaje = mock(MensajeNotificacion.class);
        EnviadorNotificacion enviador = mock(EnviadorNotificacion.class);
        when(emailFactory.crearMensaje(anyString(), anyString(), anyString()))
                .thenReturn(mensaje);
        when(emailFactory.crearEnviador()).thenReturn(enviador);

        // Act
        inventarioObserver.monitorearStock();

        // Assert
        verify(emailFactory, times(1)).crearMensaje(
                anyString(),
                anyString(),
                contains("Jeringa 5ml")
        );
    }

    private static Stream<Arguments> stockAlertScenarios() {
        return Stream.of(
                Arguments.of(50, "Stock Bajo"),
                Arguments.of(20, "Crítica"),
                Arguments.of(0, "Agotado")
        );
    }

    private static Stream<Arguments> estadoCitaScenarios() {
        return Stream.of(
                Arguments.of("CONFIRMADA", "ATENDIDA"),
                Arguments.of("PROGRAMADA", "CONFIRMADA"),
                Arguments.of("ATENDIDA", "ATENDIDA")
        );
    }
}
