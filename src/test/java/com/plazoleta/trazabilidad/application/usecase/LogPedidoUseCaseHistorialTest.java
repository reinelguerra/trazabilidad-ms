package com.plazoleta.trazabilidad.application.usecase;

import com.plazoleta.trazabilidad.domain.exception.AccesoDenegadoException;
import com.plazoleta.trazabilidad.domain.exception.PedidoSinRegistrosException;
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
import java.time.ZoneId;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LogPedidoUseCaseHistorialTest {

    private static final Long ID_PEDIDO = 15L;
    private static final Long ID_CLIENTE = 4L;

    @Mock
    private LogPedidoPersistencePort logPedidoPersistencePort;

    private LogPedidoUseCase useCase;

    @BeforeEach
    void preparar() {
        Clock reloj = Clock.fixed(Instant.parse("2026-11-13T20:00:00Z"), ZoneId.of("America/Bogota"));
        useCase = new LogPedidoUseCase(logPedidoPersistencePort, reloj);
    }

    @Test
    void devuelveElHistorialAlClienteDueñoDelPedido() {
        List<LogPedido> historial = List.of(
                log(null, EstadoPedido.PENDIENTE, LocalDateTime.of(2026, 11, 13, 10, 0)),
                log(EstadoPedido.PENDIENTE, EstadoPedido.EN_PREPARACION, LocalDateTime.of(2026, 11, 13, 10, 5)));
        when(logPedidoPersistencePort.buscarPorIdPedido(ID_PEDIDO)).thenReturn(historial);

        List<LogPedido> resultado = useCase.consultarHistorial(ID_PEDIDO, ID_CLIENTE);

        assertEquals(historial, resultado);
    }

    @Test
    void lanzaPedidoSinRegistrosCuandoNoHayLogs() {
        when(logPedidoPersistencePort.buscarPorIdPedido(ID_PEDIDO)).thenReturn(List.of());

        PedidoSinRegistrosException e = assertThrows(PedidoSinRegistrosException.class,
                () -> useCase.consultarHistorial(ID_PEDIDO, ID_CLIENTE));

        assertEquals(MensajeError.PEDIDO_SIN_REGISTROS, e.getMessage());
    }

    @Test
    void lanzaAccesoDenegadoCuandoElPedidoEsDeOtroCliente() {
        when(logPedidoPersistencePort.buscarPorIdPedido(ID_PEDIDO)).thenReturn(List.of(
                log(null, EstadoPedido.PENDIENTE, LocalDateTime.of(2026, 11, 13, 10, 0))));

        AccesoDenegadoException e = assertThrows(AccesoDenegadoException.class,
                () -> useCase.consultarHistorial(ID_PEDIDO, 99L));

        assertEquals(MensajeError.PEDIDO_AJENO, e.getMessage());
    }

    @Test
    void rechazaUnIdPedidoNulo() {
        ReglaDeNegocioException e = assertThrows(ReglaDeNegocioException.class,
                () -> useCase.consultarHistorial(null, ID_CLIENTE));

        assertEquals(MensajeError.ID_PEDIDO_INVALIDO, e.getMessage());
        verifyNoInteractions(logPedidoPersistencePort);
    }

    @Test
    void rechazaUnIdPedidoNoPositivo() {
        ReglaDeNegocioException e = assertThrows(ReglaDeNegocioException.class,
                () -> useCase.consultarHistorial(0L, ID_CLIENTE));

        assertEquals(MensajeError.ID_PEDIDO_INVALIDO, e.getMessage());
        verifyNoInteractions(logPedidoPersistencePort);
    }

    private LogPedido log(EstadoPedido anterior, EstadoPedido nuevo, LocalDateTime fecha) {
        return new LogPedido(ID_PEDIDO, ID_CLIENTE, 1L, 7L, anterior, nuevo, fecha);
    }
}