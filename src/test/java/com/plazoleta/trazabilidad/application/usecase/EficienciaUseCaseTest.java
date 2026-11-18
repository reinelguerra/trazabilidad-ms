package com.plazoleta.trazabilidad.application.usecase;

import com.plazoleta.trazabilidad.domain.exception.AccesoDenegadoException;
import com.plazoleta.trazabilidad.domain.exception.ReglaDeNegocioException;
import com.plazoleta.trazabilidad.domain.exception.RestauranteNoEncontradoException;
import com.plazoleta.trazabilidad.domain.model.EficienciaEmpleado;
import com.plazoleta.trazabilidad.domain.model.EficienciaPedido;
import com.plazoleta.trazabilidad.domain.model.EstadoPedido;
import com.plazoleta.trazabilidad.domain.model.LogPedido;
import com.plazoleta.trazabilidad.domain.spi.LogPedidoPersistencePort;
import com.plazoleta.trazabilidad.domain.spi.RestauranteClientPort;
import com.plazoleta.trazabilidad.domain.util.MensajeError;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/** HU 18: tiempo por pedido y ranking por empleado, solo para el propietario del restaurante. */
class EficienciaUseCaseTest {

    private static final Long ID_RESTAURANTE = 3L;
    private static final Long ID_PROPIETARIO = 20L;
    private static final LocalDateTime BASE = LocalDateTime.of(2026, 11, 16, 10, 0, 0);

    private LogPedidoPersistencePort persistencia;
    private RestauranteClientPort restauranteClient;
    private EficienciaUseCase useCase;

    @BeforeEach
    void preparar() {
        persistencia = mock(LogPedidoPersistencePort.class);
        restauranteClient = mock(RestauranteClientPort.class);
        useCase = new EficienciaUseCase(persistencia, restauranteClient);
        when(restauranteClient.obtenerIdPropietario(ID_RESTAURANTE)).thenReturn(ID_PROPIETARIO);
    }

    private static LogPedido log(Long idPedido, Long idEmpleado, EstadoPedido anterior,
                                 EstadoPedido nuevo, int segundos) {
        return new LogPedido(idPedido, 7L, ID_RESTAURANTE, idEmpleado, anterior, nuevo,
                BASE.plusSeconds(segundos));
    }

    /** Historial completo de un pedido entregado: PENDIENTE en "inicio" y ENTREGADO "duracion" segundos después. */
    private static List<LogPedido> pedidoEntregado(Long idPedido, Long idEmpleado, int inicio, int duracion) {
        return List.of(
                log(idPedido, null, null, EstadoPedido.PENDIENTE, inicio),
                log(idPedido, idEmpleado, EstadoPedido.PENDIENTE, EstadoPedido.EN_PREPARACION, inicio + 1),
                log(idPedido, idEmpleado, EstadoPedido.EN_PREPARACION, EstadoPedido.LISTO, inicio + duracion - 1),
                log(idPedido, idEmpleado, EstadoPedido.LISTO, EstadoPedido.ENTREGADO, inicio + duracion));
    }

    @SafeVarargs
    private static List<LogPedido> juntar(List<LogPedido>... listas) {
        return Arrays.stream(listas).flatMap(List::stream).toList();
    }

    private void hayLogs(List<LogPedido> logs) {
        when(persistencia.buscarPorIdRestaurante(ID_RESTAURANTE)).thenReturn(logs);
    }

    @Test
    void tiempoPorPedido_calculaInicioFinYDuracion() {
        hayLogs(pedidoEntregado(1L, 7L, 0, 120));

        List<EficienciaPedido> resultado = useCase.consultarTiempoPorPedido(ID_RESTAURANTE, ID_PROPIETARIO);

        assertEquals(List.of(new EficienciaPedido(1L, 7L, BASE, BASE.plusSeconds(120), 120)), resultado);
    }

    @Test
    void tiempoPorPedido_variosPedidos_vienenOrdenadosPorId() {
        hayLogs(juntar(pedidoEntregado(2L, 7L, 0, 100), pedidoEntregado(1L, 7L, 10, 50)));

        List<EficienciaPedido> resultado = useCase.consultarTiempoPorPedido(ID_RESTAURANTE, ID_PROPIETARIO);

        assertEquals(List.of(1L, 2L), resultado.stream().map(EficienciaPedido::idPedido).toList());
    }

    @Test
    void tiempoPorPedido_ignoraLosPedidosQueNoTerminaron() {
        hayLogs(List.of(
                log(1L, null, null, EstadoPedido.PENDIENTE, 0),
                log(1L, 7L, EstadoPedido.PENDIENTE, EstadoPedido.EN_PREPARACION, 1)));

        assertTrue(useCase.consultarTiempoPorPedido(ID_RESTAURANTE, ID_PROPIETARIO).isEmpty());
    }

    @Test
    void tiempoPorPedido_ignoraLosCancelados() {
        hayLogs(List.of(
                log(1L, null, null, EstadoPedido.PENDIENTE, 0),
                log(1L, null, EstadoPedido.PENDIENTE, EstadoPedido.CANCELADO, 30)));

        assertTrue(useCase.consultarTiempoPorPedido(ID_RESTAURANTE, ID_PROPIETARIO).isEmpty());
    }

    @Test
    void tiempoPorPedido_sinLogs_devuelveListaVacia() {
        hayLogs(List.of());

        assertTrue(useCase.consultarTiempoPorPedido(ID_RESTAURANTE, ID_PROPIETARIO).isEmpty());
    }

    @Test
    void tiempoPorPedido_restauranteAjeno_lanzaAccesoDenegadoYNoLeeLogs() {
        AccesoDenegadoException e = assertThrows(AccesoDenegadoException.class,
                () -> useCase.consultarTiempoPorPedido(ID_RESTAURANTE, 99L));

        assertEquals(MensajeError.RESTAURANTE_AJENO, e.getMessage());
        verify(persistencia, never()).buscarPorIdRestaurante(any());
    }

    @Test
    void tiempoPorPedido_restauranteInexistente_propagaLaExcepcion() {
        when(restauranteClient.obtenerIdPropietario(5L))
                .thenThrow(new RestauranteNoEncontradoException(MensajeError.RESTAURANTE_NO_ENCONTRADO));

        assertThrows(RestauranteNoEncontradoException.class,
                () -> useCase.consultarTiempoPorPedido(5L, ID_PROPIETARIO));
    }

    @Test
    void tiempoPorPedido_idRestauranteInvalido_noConsultaNada() {
        assertThrows(ReglaDeNegocioException.class,
                () -> useCase.consultarTiempoPorPedido(null, ID_PROPIETARIO));
        assertThrows(ReglaDeNegocioException.class,
                () -> useCase.consultarTiempoPorPedido(0L, ID_PROPIETARIO));

        verify(restauranteClient, never()).obtenerIdPropietario(any());
    }

    @Test
    void tiempoPorPedido_propietarioNulo_lanzaAccesoDenegado() {
        assertThrows(AccesoDenegadoException.class,
                () -> useCase.consultarTiempoPorPedido(ID_RESTAURANTE, null));
    }

    @Test
    void ranking_agrupaPorEmpleadoYOrdenaDelMasRapidoAlMasLento() {
        hayLogs(juntar(
                pedidoEntregado(1L, 7L, 0, 100),
                pedidoEntregado(2L, 7L, 1000, 200),
                pedidoEntregado(3L, 8L, 2000, 60)));

        List<EficienciaEmpleado> ranking = useCase.consultarRankingEmpleados(ID_RESTAURANTE, ID_PROPIETARIO);

        assertEquals(List.of(
                new EficienciaEmpleado(1, 8L, 1, 60),
                new EficienciaEmpleado(2, 7L, 2, 150)), ranking);
    }

    @Test
    void ranking_conEmpate_desempataPorIdEmpleado() {
        hayLogs(juntar(pedidoEntregado(1L, 9L, 0, 100), pedidoEntregado(2L, 8L, 1000, 100)));

        List<EficienciaEmpleado> ranking = useCase.consultarRankingEmpleados(ID_RESTAURANTE, ID_PROPIETARIO);

        assertEquals(List.of(8L, 9L), ranking.stream().map(EficienciaEmpleado::idEmpleado).toList());
        assertEquals(List.of(1, 2), ranking.stream().map(EficienciaEmpleado::posicion).toList());
    }

    @Test
    void ranking_redondeaElPromedio() {
        hayLogs(juntar(pedidoEntregado(1L, 7L, 0, 10), pedidoEntregado(2L, 7L, 1000, 11)));

        List<EficienciaEmpleado> ranking = useCase.consultarRankingEmpleados(ID_RESTAURANTE, ID_PROPIETARIO);

        assertEquals(List.of(new EficienciaEmpleado(1, 7L, 2, 11)), ranking);
    }

    @Test
    void ranking_ignoraLasEntregasSinEmpleado() {
        hayLogs(pedidoEntregado(1L, null, 0, 60));

        assertTrue(useCase.consultarRankingEmpleados(ID_RESTAURANTE, ID_PROPIETARIO).isEmpty());
    }

    @Test
    void ranking_restauranteAjeno_lanzaAccesoDenegado() {
        assertThrows(AccesoDenegadoException.class,
                () -> useCase.consultarRankingEmpleados(ID_RESTAURANTE, 99L));

        verify(persistencia, never()).buscarPorIdRestaurante(any());
    }
}