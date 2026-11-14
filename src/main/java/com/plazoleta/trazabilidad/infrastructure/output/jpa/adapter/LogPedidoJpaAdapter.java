package com.plazoleta.trazabilidad.infrastructure.output.jpa.adapter;

import com.plazoleta.trazabilidad.domain.model.LogPedido;
import com.plazoleta.trazabilidad.domain.spi.LogPedidoPersistencePort;
import com.plazoleta.trazabilidad.infrastructure.output.jpa.mapper.LogPedidoEntityMapper;
import com.plazoleta.trazabilidad.infrastructure.output.jpa.repository.LogPedidoRepository;

import java.util.List;

public class LogPedidoJpaAdapter implements LogPedidoPersistencePort {

    private final LogPedidoRepository logPedidoRepository;
    private final LogPedidoEntityMapper logPedidoEntityMapper;

    public LogPedidoJpaAdapter(LogPedidoRepository logPedidoRepository,
                               LogPedidoEntityMapper logPedidoEntityMapper) {
        this.logPedidoRepository = logPedidoRepository;
        this.logPedidoEntityMapper = logPedidoEntityMapper;
    }

    @Override
    public LogPedido guardarLog(LogPedido log) {
        return logPedidoEntityMapper.toModel(
                logPedidoRepository.save(logPedidoEntityMapper.toEntity(log)));
    }

    @Override
    public List<LogPedido> buscarPorIdPedido(Long idPedido) {
        return logPedidoRepository.findByIdPedidoOrderByFechaAscIdAsc(idPedido).stream()
                .map(logPedidoEntityMapper::toModel)
                .toList();
    }
}