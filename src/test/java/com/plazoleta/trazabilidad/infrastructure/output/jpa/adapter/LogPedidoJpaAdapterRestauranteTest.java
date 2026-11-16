package com.plazoleta.trazabilidad.infrastructure.output.jpa.adapter;

import com.plazoleta.trazabilidad.domain.model.EstadoPedido;
import com.plazoleta.trazabilidad.domain.model.LogPedido;
import com.plazoleta.trazabilidad.infrastructure.output.jpa.entity.LogPedidoEntity;
import com.plazoleta.trazabilidad.infrastructure.output.jpa.mapper.LogPedidoEntityMapper;
import com.plazoleta.trazabilidad.infrastructure.output.jpa.repository.LogPedidoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class LogPedidoJpaAdapterRestauranteTest {

    private LogPedidoRepository repositorio;
    private LogPedidoJpaAdapter adaptador;

    @BeforeEach
    void preparar() {
        repositorio = mock(LogPedidoRepository.class);
        adaptador = new LogPedidoJpaAdapter(repositorio, Mappers.getMapper(LogPedidoEntityMapper.class));
    }

    @Test
    void buscarPorIdRestaurante_devuelveLosLogsComoModelo() {
        LogPedidoEntity entidad = new LogPedidoEntity();
        entidad.setIdPedido(15L);
        entidad.setIdCliente(4L);
        entidad.setIdRestaurante(3L);
        entidad.setIdEmpleado(7L);
        entidad.setEstadoAnterior(EstadoPedido.LISTO);
        entidad.setEstadoNuevo(EstadoPedido.ENTREGADO);
        entidad.setFecha(LocalDateTime.of(2026, 11, 16, 10, 0));
        when(repositorio.findByIdRestauranteOrderByFechaAscIdAsc(3L)).thenReturn(List.of(entidad));

        List<LogPedido> logs = adaptador.buscarPorIdRestaurante(3L);

        assertEquals(1, logs.size());
        assertEquals(15L, logs.get(0).idPedido());
        assertEquals(3L, logs.get(0).idRestaurante());
        assertEquals(7L, logs.get(0).idEmpleado());
        assertEquals(EstadoPedido.ENTREGADO, logs.get(0).estadoNuevo());
    }

    @Test
    void buscarPorIdRestaurante_sinLogs_devuelveListaVacia() {
        when(repositorio.findByIdRestauranteOrderByFechaAscIdAsc(3L)).thenReturn(List.of());

        assertTrue(adaptador.buscarPorIdRestaurante(3L).isEmpty());
    }
}