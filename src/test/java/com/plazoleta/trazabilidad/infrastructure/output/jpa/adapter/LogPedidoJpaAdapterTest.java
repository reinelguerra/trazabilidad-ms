package com.plazoleta.trazabilidad.infrastructure.output.jpa.adapter;

import com.plazoleta.trazabilidad.domain.model.EstadoPedido;
import com.plazoleta.trazabilidad.domain.model.LogPedido;
import com.plazoleta.trazabilidad.infrastructure.output.jpa.entity.LogPedidoEntity;
import com.plazoleta.trazabilidad.infrastructure.output.jpa.mapper.LogPedidoEntityMapper;
import com.plazoleta.trazabilidad.infrastructure.output.jpa.repository.LogPedidoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LogPedidoJpaAdapterTest {

    private static final LocalDateTime FECHA = LocalDateTime.of(2026, 11, 13, 20, 0);

    @Mock
    private LogPedidoRepository logPedidoRepository;

    private LogPedidoJpaAdapter adapter;

    @BeforeEach
    void setUp() {
        LogPedidoEntityMapper mapper = Mappers.getMapper(LogPedidoEntityMapper.class);
        adapter = new LogPedidoJpaAdapter(logPedidoRepository, mapper);
    }

    private void guardarAsignaId() {
        when(logPedidoRepository.save(any(LogPedidoEntity.class))).thenAnswer(invocacion -> {
            LogPedidoEntity entidad = invocacion.getArgument(0);
            entidad.setId(99L);
            return entidad;
        });
    }

    @Test
    void guardarLog_conTodosLosCampos_guardaLaEntidadYDevuelveElModelo() {
        guardarAsignaId();
        LogPedido log = new LogPedido(10L, 20L, 30L, 40L,
                EstadoPedido.PENDIENTE, EstadoPedido.EN_PREPARACION, FECHA);

        LogPedido resultado = adapter.guardarLog(log);

        ArgumentCaptor<LogPedidoEntity> captor = ArgumentCaptor.forClass(LogPedidoEntity.class);
        verify(logPedidoRepository).save(captor.capture());
        LogPedidoEntity guardada = captor.getValue();
        assertEquals(10L, guardada.getIdPedido());
        assertEquals(20L, guardada.getIdCliente());
        assertEquals(30L, guardada.getIdRestaurante());
        assertEquals(40L, guardada.getIdEmpleado());
        assertEquals(EstadoPedido.PENDIENTE, guardada.getEstadoAnterior());
        assertEquals(EstadoPedido.EN_PREPARACION, guardada.getEstadoNuevo());
        assertEquals(FECHA, guardada.getFecha());
        assertEquals(log, resultado);
    }

    @Test
    void guardarLog_sinEmpleadoNiEstadoAnterior_conservaLosNulos() {
        guardarAsignaId();
        LogPedido log = new LogPedido(10L, 20L, 30L, null, null, EstadoPedido.PENDIENTE, FECHA);

        LogPedido resultado = adapter.guardarLog(log);

        assertNull(resultado.idEmpleado());
        assertNull(resultado.estadoAnterior());
        assertEquals(EstadoPedido.PENDIENTE, resultado.estadoNuevo());
    }
}