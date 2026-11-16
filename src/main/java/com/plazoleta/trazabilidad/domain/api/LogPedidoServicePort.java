package com.plazoleta.trazabilidad.domain.api;

import com.plazoleta.trazabilidad.domain.model.LogPedido;

import java.util.List;

public interface LogPedidoServicePort {

    LogPedido registrarLog(LogPedido log);

    List<LogPedido> consultarHistorial(Long idPedido, Long idCliente);
}