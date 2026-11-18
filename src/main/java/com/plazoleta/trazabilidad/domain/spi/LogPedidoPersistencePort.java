package com.plazoleta.trazabilidad.domain.spi;

import com.plazoleta.trazabilidad.domain.model.LogPedido;

import java.util.List;

public interface LogPedidoPersistencePort {

    LogPedido guardarLog(LogPedido log);

    List<LogPedido> buscarPorIdPedido(Long idPedido);
        List<LogPedido> buscarPorIdRestaurante(Long idRestaurante);
}