package com.plazoleta.trazabilidad.application.usecase;

import com.plazoleta.trazabilidad.domain.exception.ReglaDeNegocioException;
import com.plazoleta.trazabilidad.domain.model.EstadoPedido;
import com.plazoleta.trazabilidad.domain.model.LogPedido;
import com.plazoleta.trazabilidad.domain.spi.LogPedidoPersistencePort;
import com.plazoleta.trazabilidad.domain.util.MensajeError;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LogPedidoUseCaseTest {

    private static final LocalDateTime AHORA = LocalDateTime.of(2026, 11, 13, 20, 0);

    @Mock
    private LogPedidoPersistencePort logPedidoPersistencePort;

    private LogPedidoUseCase logPedidoUseCase;

    @BeforeEach
    void setUp() {
        Clock relojFijo = Clock.fixed(Instant.parse("2026-11-13T20:00:00Z"), ZoneOffset.UTC);
        logPedidoUseCase = new LogPedidoUseCase(logPedidoPersistencePort, relojFijo);
    }

    private LogPedido log(Long idEmpleado, EstadoPedido anterior, EstadoPedido nuevo, LocalDateTime fecha) {
        return new LogPedido(10L, 20L, 30L, idEmpleado, anterior, nuevo, fecha);
    }

    private void guardarDevuelveLoRecibido() {
        when(logPedidoPersistencePort.guardarLog(any(LogPedido.class)))
                .thenAnswer(invocacion -> invocacion.getArgument(0));
    }

    @Test
    void registrarLog_primerLogPendiente_guardaConFechaDelReloj() {
        guardarDevuelveLoRecibido();

        LogPedido resultado = logPedidoUseCase.registrarLog(log(null, null, EstadoPedido.PENDIENTE, null));

        assertEquals(EstadoPedido.PENDIENTE, resultado.estadoNuevo());
        assertEquals(AHORA, resultado.fecha());
        verify(logPedidoPersistencePort).guardarLog(resultado);
    }

    @Test
    void registrarLog_cambioValidoConEmpleado_guardaTodosLosCampos() {
        guardarDevuelveLoRecibido();

        LogPedido resultado = logPedidoUseCase.registrarLog(
                log(40L, EstadoPedido.PENDIENTE, EstadoPedido.EN_PREPARACION, null));

        assertEquals(10L, resultado.idPedido());
        assertEquals(20L, resultado.idCliente());
        assertEquals(30L, resultado.idRestaurante());
        assertEquals(40L, resultado.idEmpleado());
        assertEquals(EstadoPedido.PENDIENTE, resultado.estadoAnterior());
        assertEquals(EstadoPedido.EN_PREPARACION, resultado.estadoNuevo());
    }

    @Test
    void registrarLog_conFechaEnLaPeticion_laReemplazaPorLaDelServicio() {
        guardarDevuelveLoRecibido();
        LocalDateTime fechaVieja = LocalDateTime.of(2000, 1, 1, 0, 0);

        LogPedido resultado = logPedidoUseCase.registrarLog(
                log(null, null, EstadoPedido.PENDIENTE, fechaVieja));

        assertEquals(AHORA, resultado.fecha());
    }

    @Test
    void registrarLog_primerLogQueNoEsPendiente_lanzaReglaYNoGuarda() {
        LogPedido peticion = log(null, null, EstadoPedido.LISTO, null);

        ReglaDeNegocioException excepcion = assertThrows(ReglaDeNegocioException.class,
                () -> logPedidoUseCase.registrarLog(peticion));

        assertEquals(MensajeError.PRIMER_LOG_DEBE_SER_PENDIENTE, excepcion.getMessage());
        verify(logPedidoPersistencePort, never()).guardarLog(any(LogPedido.class));
    }

    @Test
    void registrarLog_sinEstadoAnteriorNiNuevo_lanzaReglaYNoGuarda() {
        LogPedido peticion = log(null, null, null, null);

        ReglaDeNegocioException excepcion = assertThrows(ReglaDeNegocioException.class,
                () -> logPedidoUseCase.registrarLog(peticion));

        assertEquals(MensajeError.PRIMER_LOG_DEBE_SER_PENDIENTE, excepcion.getMessage());
        verify(logPedidoPersistencePort, never()).guardarLog(any(LogPedido.class));
    }

    @Test
    void registrarLog_estadoNuevoIgualAlAnterior_lanzaReglaYNoGuarda() {
        LogPedido peticion = log(40L, EstadoPedido.LISTO, EstadoPedido.LISTO, null);

        ReglaDeNegocioException excepcion = assertThrows(ReglaDeNegocioException.class,
                () -> logPedidoUseCase.registrarLog(peticion));

        assertEquals(MensajeError.ESTADO_REPETIDO, excepcion.getMessage());
        verify(logPedidoPersistencePort, never()).guardarLog(any(LogPedido.class));
    }
}